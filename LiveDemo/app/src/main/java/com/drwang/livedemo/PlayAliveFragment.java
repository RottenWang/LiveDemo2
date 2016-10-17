package com.drwang.livedemo;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import static com.drwang.livedemo.R.id.radio_btn_auto;
import static com.drwang.livedemo.R.id.radio_btn_fast;
import static com.drwang.livedemo.R.id.radio_btn_smooth;

/**
 * Created by dr.wang on 2016/10/15.
 */
public class PlayAliveFragment extends Fragment implements ITXLivePlayListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "PlayAliveFragment";
    private TXCloudVideoView mPlayerView;
    private TXLivePlayer mLivePlayer;
    private ImageView mIvLoadingView;
    private Button mBtnPlay;
    private String flvUrl;
    private Button mBtnHWDecode;
    private Button mBtnCacheStrategy;
    private LinearLayout mLlCacheStrategy;
    private RadioGroup mRgCacheStrategy;
    private TXLivePlayConfig mPlayConfig;
    private ImageView mIvloading;
    private AnimationDrawable mAnimation;
    private Button mBtnOrientation;
    private MainActivity mainActiviy;
    private Button mBtnRenderMode;
    private Button mBtnChannel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_play, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initListener();
        Log.i(TAG, "onViewCreated: 执行完毕");
    }

    //显示模式 水平 竖直
    private int mRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;
    private int mPlayTypeLive = TXLivePlayer.PLAY_TYPE_LIVE_RTMP;

    private void initView(View view) {
        //配置信息
        mPlayConfig = new TXLivePlayConfig();
        //显示的界面
        mPlayerView = (TXCloudVideoView) view.findViewById(R.id.video_view_play);
        mLivePlayer = new TXLivePlayer(getActivity());
        flvUrl = "rtmp://live.hkstv.hk.lxdns.com/live/hks";
        mLivePlayer.setPlayerView(mPlayerView);//设置播放的view
        //设置屏幕方向
        mLivePlayer.setRenderRotation(mRenderRotation);
        //设置播放模式  自适应模式将图像等比例缩放，适配最长边，缩放后的宽和高都不会超过显示区域，居中显示，画面可能会留有黑边。
        mLivePlayer.setRenderMode(mRenderMode);
        //播放按钮
        mBtnPlay = (Button) view.findViewById(R.id.btnPlay);
        //硬件加速
        mBtnHWDecode = (Button) view.findViewById(R.id.btnHWDecode);
        //缓存策略
        mBtnCacheStrategy = (Button) view.findViewById(R.id.btnCacheStrategy);
        mLlCacheStrategy = (LinearLayout) view.findViewById(R.id.layoutCacheStrategy);
        mRgCacheStrategy = (RadioGroup) view.findViewById(R.id.cacheStrategyRadioGroup);
        //动画图
        mIvloading = (ImageView) view.findViewById(R.id.loadingImageView);
        //横屏竖屏
        mBtnOrientation = (Button) view.findViewById(R.id.btnOrientation);
        //填充模式
        mBtnRenderMode = (Button) view.findViewById(R.id.btnRenderMode);
        //选择频道
        mBtnChannel = (Button) view.findViewById(R.id.btnChannel);
    }

    private void initData() {
        mainActiviy = (MainActivity) getActivity();
        mAnimation = (AnimationDrawable) mIvloading.getDrawable();
    }

    private boolean isPlaying;//是否正在播放
    private boolean isHWDecode;//是否开启硬件加速
    private boolean isPorttait = true;// 是否是竖屏
    private int mRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
    private void initListener() {
//        mBtnChannel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mainActiviy, "敬请期待~", Toast.LENGTH_SHORT).show();
//            }
//        });
        //填充模式
        mBtnRenderMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRenderMode == TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION) {
                    mRenderMode = TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN;
                    mBtnRenderMode.setBackgroundResource(R.drawable.fill_mode);
                }else {
                    mRenderMode = TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION;
                    mBtnRenderMode.setBackgroundResource(R.drawable.adjust_mode);
                }
                mLivePlayer.setRenderMode(mRenderMode);
            }
        });
        mBtnOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPorttait){
                    //是竖屏 切换为横屏
                    mRenderRotation = TXLiveConstants.RENDER_ROTATION_LANDSCAPE;
                    mBtnOrientation.setBackgroundResource(R.drawable.landscape);
                    mainActiviy.mLlMainTab.setVisibility(View.GONE);
                }else {
                    //不是竖屏 切换为竖屏
                    mRenderRotation = TXLiveConstants.RENDER_ROTATION_PORTRAIT;
                    mBtnOrientation.setBackgroundResource(R.drawable.portrait);
                    mainActiviy.mLlMainTab.setVisibility(View.VISIBLE);
                }
                mLivePlayer.setRenderRotation(mRenderRotation);
                isPorttait = !isPorttait;
            }
        });
        mRgCacheStrategy.setOnCheckedChangeListener(this);
        mBtnCacheStrategy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlCacheStrategy.setVisibility(View.VISIBLE);
            }
        });
        //是否开启硬件加速
        mBtnHWDecode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHWDecode) {
                    //开启 去关闭
                    mLivePlayer.stopPlay(true);
                    mBtnHWDecode.setBackgroundResource(R.drawable.quick2);
                    mLivePlayer.enableHardwareDecode(false);
                    mLivePlayer.startPlay(flvUrl, mPlayTypeLive);
                    Toast.makeText(getActivity(), "关闭硬件加速", Toast.LENGTH_SHORT).show();
                } else {
                    //关闭 去开启
                    mLivePlayer.stopPlay(true);
                    mBtnHWDecode.setBackgroundResource(R.drawable.quick);
                    mLivePlayer.enableHardwareDecode(true);
                    mLivePlayer.startPlay(flvUrl, mPlayTypeLive);
                    Toast.makeText(getActivity(), "开启硬件加速", Toast.LENGTH_SHORT).show();

                }
                isHWDecode = !isHWDecode;
            }
        });
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLivePlayer != null) {
                    if (isPlaying) {
                        //正在播放
                        mIvloading.setVisibility(View.GONE);
                        mAnimation.stop();
                        mPlayerView.setVisibility(View.GONE);
                        mBtnPlay.setBackgroundResource(R.drawable.play_start);
                        mLivePlayer.setPlayListener(null);
                        mLivePlayer.stopPlay(true);
                    } else {
                        //未播放
                        mIvloading.setVisibility(View.VISIBLE);
                        mAnimation.start();
                        mPlayerView.setVisibility(View.VISIBLE);
                        mBtnPlay.setBackgroundResource(R.drawable.play_pause);
                        mLivePlayer.setPlayListener(PlayAliveFragment.this);
                        mLivePlayer.startPlay(flvUrl, mPlayTypeLive);
                    }
                    isPlaying = !isPlaying;
                }


            }
        });
    }
    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            isPlaying = false;
            mLivePlayer.stopPlay(true);
            mLivePlayer.setPlayListener(null);
            mPlayerView.setVisibility(View.GONE);
            mBtnPlay.setBackgroundResource(R.drawable.play_start);
        }
        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            mIvloading.setVisibility(View.GONE);
            mAnimation.stop();
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mLivePlayer.resume();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLivePlayer.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLivePlayer.stopPlay(true);
        mPlayerView.onDestroy();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case radio_btn_fast:
                //极速模式
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMinAutoAdjustCacheTime(1);
                mPlayConfig.setMaxAutoAdjustCacheTime(1);

                break;
            case radio_btn_smooth:
                //流畅模式
                mPlayConfig.setAutoAdjustCacheTime(false);
                mPlayConfig.setCacheTime(5);

                break;
            case radio_btn_auto:
                //自动模式
                mPlayConfig.setAutoAdjustCacheTime(true);
                mPlayConfig.setMinAutoAdjustCacheTime(1);
                mPlayConfig.setMaxAutoAdjustCacheTime(5);
                break;
        }
        mLlCacheStrategy.setVisibility(View.GONE);
        mLivePlayer.setConfig(mPlayConfig);
    }
    public void fullScrren(){
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().recreate();
    }
}
