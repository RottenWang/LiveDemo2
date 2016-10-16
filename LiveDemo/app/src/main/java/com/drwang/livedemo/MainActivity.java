package com.drwang.livedemo;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TXLivePusher mLivePusher;
    private TXLivePushConfig mConfig;
    private TXCloudVideoView mCaptureView;
    private TextView mTvPublish;
    private TextView mTvPlayVod;
    private TextView mTvPlayAlive;
    private FragmentManager mFragmentManager;
    public LinearLayout mLlMainTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        init();
        mFragmentManager.beginTransaction().replace(R.id.content_layout, new PublishFragment()).commit();


    }

    private void init() {
        initView();
        initData();
        initListener();
    }

    private void initView() {
        //推流
        mTvPublish = (TextView) findViewById(R.id.text_publish_rtmp);
        //点播
        mTvPlayVod = (TextView) findViewById(R.id.text_play_vod);
        //直播
        mTvPlayAlive = (TextView) findViewById(R.id.text_play_alive);
        //三个textview的底层布局
        mLlMainTab = (LinearLayout) findViewById(R.id.main_tab_layout);
    }

    private void initData() {

    }

    private void initListener() {
        mTvPublish.setOnClickListener(this);
        mTvPlayVod.setOnClickListener(this);
        mTvPlayAlive.setOnClickListener(this);
        //获取到fragment的管理者
        mFragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //推流
            case R.id.text_publish_rtmp:
                mFragmentManager.beginTransaction().replace(R.id.content_layout, new PublishFragment()).commit();
                mLlMainTab.setBackgroundResource(R.drawable.main_tab_1);
                mTvPlayAlive.setTextColor(Color.BLACK);
                mTvPlayVod.setTextColor(Color.BLACK);
                mTvPublish.setTextColor(Color.WHITE);
                break;
            //点播
            case R.id.text_play_vod:
                mFragmentManager.beginTransaction().replace(R.id.content_layout, new PlayVodFragment()).commit();
                mLlMainTab.setBackgroundResource(R.drawable.main_tab_2);
                mTvPlayAlive.setTextColor(Color.BLACK);
                mTvPlayVod.setTextColor(Color.WHITE);
                mTvPublish.setTextColor(Color.BLACK);

                break;
            //直播
            case R.id.text_play_alive:
                mFragmentManager.beginTransaction().replace(R.id.content_layout, new PlayAliveFragment()).commit();
                mLlMainTab.setBackgroundResource(R.drawable.main_tab_3);
                mTvPlayAlive.setTextColor(Color.WHITE);
                mTvPlayVod.setTextColor(Color.BLACK);
                mTvPublish.setTextColor(Color.BLACK);
                break;
        }
    }

}
