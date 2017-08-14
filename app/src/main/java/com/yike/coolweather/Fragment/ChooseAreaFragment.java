package com.yike.coolweather.Fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yike.coolweather.Activity.WeatherActivity;
import com.yike.coolweather.JavaBean.City;
import com.yike.coolweather.JavaBean.County;
import com.yike.coolweather.JavaBean.Province;
import com.yike.coolweather.R;
import com.yike.coolweather.Util.HttpUtil;
import com.yike.coolweather.Util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Fly on 2017/8/1.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList;//省列表

    private List<City> cityList;//市列表

    private List<County> countyList;//县列表

    private Province selectedProvince;//选中的省份

    private City selectedCity;//选中的市

    private County selectedCounty;//选中的县


    private int currentLevel;//当前选择的级别

    private Context currentContext;

    //@RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button)view.findViewById(R.id.back_button);
        listView = (ListView)view.findViewById(R.id.list_view);
        //adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);

        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,dataList);

        listView.setAdapter(adapter);
        return view;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){
                    String weatherId = countyList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有省份，优先从数据库查询，如果没有就去服务器上查询
     */
    private void queryProvinces(){
        //设置布局文件
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        //查询数据库，获取所有的省份
        provinceList = DataSupport.findAll(Province.class);
        //Toast.makeText(getActivity(), "多少个省份："+provinceList.size(),Toast.LENGTH_LONG).show();
        if(provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");//从服务器获取数据
        }
    }

    /**
     * 查询选中的省份的全部市
     *
     */
    private void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId()))
                .find(City.class);
        if(cityList.size() > 0){
            dataList.clear();
            for( City city : cityList ){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFromServer(address, "city");
        }
    }

    /**
     * 查询选中市的全部县
     */
    public void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId()))
                .find(County.class);
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address, "county");

        }

    }

    /**
     * 根据传入的地址和类型从服务器查询省市县数据
     *
     * @param address
     * @param type
     */
    private void queryFromServer(String address, final String type){
        //System.out.println("address:"+address);
        //Toast.makeText(getActivity(), type+":"+address, Toast.LENGTH_SHORT).show();
        showProcessDialog();

        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread返回主线程
                getActivity().runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        closeProcessDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                //System.out.println("============="+responseText);
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceRespnse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                //Toast.makeText(getActivity(), "=============result:"+result, Toast.LENGTH_SHORT).show();

                //System.out.println("=============result:"+result);

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProcessDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProcessDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     *
     */
    private void closeProcessDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
