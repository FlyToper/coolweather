package com.yike.coolweather.Util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.yike.coolweather.Gson.Weather;
import com.yike.coolweather.JavaBean.City;
import com.yike.coolweather.JavaBean.County;
import com.yike.coolweather.JavaBean.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Fly on 2017/8/1.
 */

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     *
     * @param response
     * @return
     */
    public static boolean handleProvinceRespnse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allProvince = new JSONArray(response);
                for(int i = 0; i < allProvince.length(); i ++){
                    JSONObject provinceObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();//保存
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     *
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCity = new JSONArray(response);
                for(int i = 0; i < allCity.length(); i ++){
                    JSONObject cityObject = allCity.getJSONObject(i);
                    City city  = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);

                    city.save();//保存
                }

                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     *
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounty = new JSONArray(response);
                for(int i =0; i< allCounty.length(); i ++ ){
                    JSONObject countyObject = allCounty.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);

                    county.save();//保存
                }

                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        return false;

    }

    /**
     * 将返回的JSON数据解析成Weather实体类
     *
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather5");
            String weatherContent = jsonArray.getJSONObject(0).toString();

            return new Gson().fromJson(weatherContent, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
