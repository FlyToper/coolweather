package com.yike.coolweather.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;

import com.yike.coolweather.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getString("weather", null) != null){
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == event.KEYCODE_BACK){
            exitBy2BackClick();
        }
        return false;
    }

    private void exitBy2BackClick(){
        Timer tExit = null;

        if(!isExit) {
            isExit = true;//准备退出
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();

            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false;//取消退出
                }
            }, 2000);//如果2秒内没有按下返回键,则启动定时器取消刚才执行的任务
        }else{
            finish();
            System.exit(0);
        }
    }
}
