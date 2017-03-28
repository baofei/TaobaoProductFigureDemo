package com.bf.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bf.demo.view.LeftSlideLayout;
import com.bf.demo.view.LeftSlideModel;

public class MainActivity extends AppCompatActivity {

    private LeftSlideLayout leftSlideLayout;
    private LeftSlideLayout leftSlideLayout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftSlideLayout = (LeftSlideLayout) findViewById(R.id.leftSlideLayout);
        leftSlideLayout2 = (LeftSlideLayout) findViewById(R.id.leftSlideLayout2);
        leftSlideLayout.attachModelView(new DemoModel());
        leftSlideLayout2.attachModelView(new DemoModel2());
        leftSlideLayout.setOnReleaseLintener(new LeftSlideLayout.OnReleaseLintener() {
            @Override
            public void onRelease() {
                Log.e("MainActivity", "释放查看图文详情1");
            }
        });
        leftSlideLayout2.setOnReleaseLintener(new LeftSlideLayout.OnReleaseLintener() {
            @Override
            public void onRelease() {
                Log.e("MainActivity", "释放查看图文详情2");
            }
        });
    }
}
