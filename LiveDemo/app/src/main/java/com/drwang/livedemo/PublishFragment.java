package com.drwang.livedemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.TXLivePusher;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by dr.wang on 2016/10/15.
 */
public class PublishFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = "PublishFragment";
    private TXLivePusher mLivePusher;
    private TXLivePushConfig mPushConfig;
    private TXCloudVideoView mCloudVideoView;
    private Button mBtnPlay;
    private String rtmpUrl;
    private Button mBtnFaceBeauty;
    private LinearLayout mLlFaceBeauty;
    private SeekBar mSeekBarBeauty;
    private SeekBar mSeekBarWhitening;
    private Button mBtnCameraChange;
    private Button mBtnFlash;
    private Button mBtnTouchFocus;
    private Button mBtnHWEncode;
    private Button mBtnBitrate;
    private LinearLayout mLlBitrate;
    private RadioGroup mRgBitrate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_publish, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        initListener();
    }



    //初始化控件
    private void initView(View view) {
        //1.创建LivePusher
        mLivePusher = new TXLivePusher(getActivity());
        //2.创建配置信息
        mPushConfig = new TXLivePushConfig();
        //3. TODO  设置配置信息
        mCloudVideoView = (TXCloudVideoView) view.findViewById(R.id.video_view);
        //4. 点击播放的按钮
        mBtnPlay = (Button) view.findViewById(R.id.btnPlay);
        //5.美颜按钮
        mBtnFaceBeauty = (Button) view.findViewById(R.id.btnFaceBeauty);
        //6.美颜布局
        mLlFaceBeauty = (LinearLayout) view.findViewById(R.id.layoutFaceBeauty);
        mSeekBarBeauty = (SeekBar) view.findViewById(R.id.beauty_seekbar);//美颜
        mSeekBarWhitening = (SeekBar) view.findViewById(R.id.whitening_seekbar);//美白
        //7. 切换摄像头
        mBtnCameraChange = (Button) view.findViewById(R.id.btnCameraChange);
        //8. 闪光灯
        mBtnFlash = (Button) view.findViewById(R.id.btnFlash);
        //9.对焦模式
        mBtnTouchFocus = (Button) view.findViewById(R.id.btnTouchFoucs);
        //10,是否开启硬解码
        mBtnHWEncode = (Button) view.findViewById(R.id.btnHWEncode);
        //11.清晰度
        mBtnBitrate = (Button) view.findViewById(R.id.btnBitrate);
        mLlBitrate = (LinearLayout) view.findViewById(R.id.layoutBitrate);
        mRgBitrate = (RadioGroup) view.findViewById(R.id.resolutionRadioGroup);
    }
    private void initData() {
        //设置水印
///        mPushConfig.setWatermark(BitmapFactory.decodeResource(getResources(),R.drawable.watermark), 10, 10);
//        mLivePusher.setConfig(mPushConfig);
        //推流地址
        rtmpUrl = "rtmp://2000.livepush.myqcloud.com/live/2000_44c6e64e79af11e69776e435c87f075e?bizid=2000";
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.pause_publish);
        mPushConfig.setPauseImg(bitmap);
    }
    private boolean isBackGroundCamera;//是否是后置摄像头
    private boolean mFlashTurnOn = true;
    private boolean isAutoFocus;
    private boolean isHardwareAcceleration;
    private boolean isBitrateVisiable;
    private void initListener() {
        mRgBitrate.setOnCheckedChangeListener(this);
        //清晰度监听
        mBtnBitrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLlBitrate.setVisibility(isBitrateVisiable ? View.GONE : View.VISIBLE);
                isBitrateVisiable = !isBitrateVisiable;
            }
        });
        //编码方式
        mBtnHWEncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!HWSupportList.isHWVideoEncodeSupport()){
                    Toast.makeText(getActivity().getApplicationContext(),
                            "当前手机型号不支持或API级别过低（最低16）,请慎重开启硬件编码！",
                            Toast.LENGTH_SHORT).show();
                }
                isHardwareAcceleration= !isHardwareAcceleration;
                mPushConfig.setHardwareAcceleration(isHardwareAcceleration);
                mLivePusher.setConfig(mPushConfig);
                Toast.makeText(getActivity(),isHardwareAcceleration?"当前模式为硬编码模式":"当前模式为软编码模式", Toast.LENGTH_SHORT).show();
            }
        });
        //对角模式
        mBtnTouchFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoFocus){
                    //是自动对焦
                    mPushConfig.setTouchFocus(true);
                    Toast.makeText(getActivity(), "当前是手动对焦模式", Toast.LENGTH_SHORT).show();
                   }else {
                    mPushConfig.setTouchFocus(false);
                    Toast.makeText(getActivity(), "当前是自动对焦模式", Toast.LENGTH_SHORT).show();


                }
                isAutoFocus = !isAutoFocus;
                mLivePusher.setConfig(mPushConfig);
            }
        });
        mBtnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLivePusher.turnOnFlashLight(mFlashTurnOn)) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "打开闪光灯失败:请开启预览并使用后置摄像头!", Toast.LENGTH_SHORT).show();
                }else {
                    if (mFlashTurnOn){
                        mBtnFlash.setBackgroundResource(R.drawable.flash_on);
                    }else {
                        mBtnFlash.setBackgroundResource(R.drawable.flash_off);
                    }
                    mFlashTurnOn = !mFlashTurnOn;
                }
            }
        });
        //切换摄像头
        mBtnCameraChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBackGroundCamera){//是后置摄像头
                    mBtnCameraChange.setBackgroundResource(R.drawable.camera_change);

                }else {
                    mBtnCameraChange.setBackgroundResource(R.drawable.camera_change2);

                }
                mLivePusher.switchCamera();
                isBackGroundCamera = !isBackGroundCamera;
            }
        });
        // 推流按钮
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLivePusher != null) {
                    if (isPushing){
                        mLivePusher.stopCameraPreview(true); //停止摄像头预览
                        mLivePusher.stopPusher();            //停止推流
                        mLivePusher.setPushListener(null);   //解绑 listener
                        mBtnPlay.setBackgroundResource(R.drawable.play_start);
                        mCloudVideoView.setVisibility(View.GONE);
                    }else {
                        Log.i(TAG,"开始推流");
                        mCloudVideoView.setVisibility(View.VISIBLE);
                        mLivePusher.startPusher(rtmpUrl);

                        mLivePusher.startCameraPreview(mCloudVideoView);
                        mBtnPlay.setBackgroundResource(R.drawable.play_pause);
                    }
                    isPushing = !isPushing;

                }
            }
        });
        //美颜按钮
        mBtnFaceBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mLlFaceBeauty.setVisibility(isFaceBeautyVisiable ? View.GONE : View.VISIBLE);
                isFaceBeautyVisiable = !isFaceBeautyVisiable;
            }
        });
        mSeekBarBeauty.setOnSeekBarChangeListener(this);
        mSeekBarWhitening.setOnSeekBarChangeListener(this);
    }
    private boolean isPushing;
    private boolean isFaceBeautyVisiable;//美颜菜单是否可见
    private int mBeauty;//美白
    private int mWhitening;//美颜
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.beauty_seekbar:
                mBeauty = progress;
            break;
            case R.id.whitening_seekbar:
                mWhitening = progress;
                break;
        }
        boolean b = mLivePusher.setBeautyFilter(mBeauty, mWhitening);
        if (!b){
            Toast.makeText(getActivity(), "当前手机不支持美颜美白效果", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    //处理生命周期的方法



    @Override
    public void onStop() {
        super.onStop();
        mCloudVideoView.onPause();//摄像头图像渲染
        mLivePusher.stopCameraPreview(false);//停止摄像头采集
        mLivePusher.pausePusher();//通知sdk进入后台推流模式
    }
    @Override
    public void onResume() {
        super.onResume();
        mCloudVideoView.onResume();     // mCaptureView 是摄像头的图像渲染view
        mLivePusher.resumePusher();  // 通知 SDK 重回前台推流
        mLivePusher.startCameraPreview(mCloudVideoView); // 恢复摄像头的图像采集
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCloudVideoView.onDestroy();
    }
    private int mBitRate;//码率
    private int mResolution;//分辨率
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.radio_btn_auto:
                mBitRate = 800;
                mResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;
                mBtnBitrate.setBackgroundResource(R.drawable.auto_bitrate);
            break;
            case R.id.radio_btn_fix_360p:
                mBitRate = 700;
                mResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_360_640;
                break;
            case R.id.radio_btn_fix_540p:
                mBitRate = 1000;
                mResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960;
                mBtnBitrate.setBackgroundResource(R.drawable.fix_bitrate);
                break;
            case R.id.radio_btn_fix_720p:
                mBitRate = 1500;
                mResolution = TXLiveConstants.VIDEO_RESOLUTION_TYPE_720_1280;
                mBtnBitrate.setBackgroundResource(R.drawable.fix_bitrate);

                break;
        }
        mPushConfig.setVideoBitrate(mBitRate);//设置码率
        mPushConfig.setVideoResolution(mResolution);//设置分辨率
//        mPushConfig.setHomeOrientation();
        mLivePusher.setConfig(mPushConfig);
    }
}
