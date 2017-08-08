package com.yike.coolweather.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.yike.coolweather.R;

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //无title
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //全屏
        setContentView(R.layout.activity_splash);

        //为启动页添加动画效果
        rlRoot = (RelativeLayout)findViewById(R.id.splash_activity);


        AlphaAnimation anim = new AlphaAnimation(0.3f, 1f);//透明度从0.3到1
        anim.setDuration(2000);//动画时长2秒
        rlRoot.startAnimation(anim);

        //2秒之后跳转主页面
        (new Handler(){
            @Override
            public void handleMessage(Message msg) {
               // super.handleMessage(msg);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }).sendEmptyMessageAtTime(0,2000);



    }
}
