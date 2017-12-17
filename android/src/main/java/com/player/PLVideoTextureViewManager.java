package com.player;

import android.view.LayoutInflater;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.player.utils.Config;

import java.util.Map;

/**
 * Created by lmy2534290808 on 2017/12/2.
 */

public class PLVideoTextureViewManager extends ViewGroupManager<PLVideoTextureView> implements LifecycleEventListener {
    private static final String NAME = "RCTPlayerAndroid";

    private static final String TAG = PLVideoTextureViewManager.class.getSimpleName();
    private int mediaCodec = AVOptions.MEDIA_CODEC_SW_DECODE;//解码风格默认软解码
    private boolean started=true;//进入视频时是否开始

    private PLVideoTextureView plVideoTextureView;
    private ThemedReactContext themedReactContext;
    private RCTEventEmitter mEventEmitter;
    private String uri;

    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        for (Events event : Events.values()) {
            builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
        }
        return builder.build();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected PLVideoTextureView createViewInstance(ThemedReactContext reactContext) {
        themedReactContext = reactContext;
        LayoutInflater inflater=LayoutInflater.from(reactContext);
        mEventEmitter=reactContext.getJSModule(RCTEventEmitter.class);
        //plVideoTextureView=(PLVideoTextureView) inflater.inflate(R.layout.video_view,null);
        plVideoTextureView=new PLVideoTextureView(reactContext);
        plVideoTextureView.setOnPreparedListener(mOnPreparedListener);
        plVideoTextureView.setOnInfoListener(mOnInfoListener);
        plVideoTextureView.setOnErrorListener(mOnErrorListener);
        plVideoTextureView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        plVideoTextureView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        plVideoTextureView.setOnCompletionListener(mOnCompletionListener);
        plVideoTextureView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        reactContext.addLifecycleEventListener(this);//监听LifecycleEventListener的生命周期需要添加这句
        return plVideoTextureView;
    }

    /**
     * 设置source属性，包括是否是直播流，解码类型，准备时长，是否有缓存等
     *
     * @param plVideoTextureView
     * @param source
     */
    @ReactProp(name = "source")
    public void setSource(PLVideoTextureView plVideoTextureView, ReadableMap source) {
        AVOptions avOptions = new AVOptions();
         uri = source.getString("uri");//视频连接地址
        //得到解码类型
        int mediaCodec = source.hasKey("mediaCodec") ? source.getInt("mediaCodec") : AVOptions.MEDIA_CODEC_SW_DECODE;
        //得到视频准备时间
        int timeout = source.hasKey("timeout") ? source.getInt("timeout") : 10 * 1000;
        //得到是否是直播流
        boolean liveStreaming = source.hasKey("liveStreaming") ? source.getBoolean("liveStreaming") : false;
        //得到是否应用缓存
        boolean cache = source.hasKey("cache") ? source.getBoolean("cache") : false;
        boolean started=source.hasKey("started")?source.getBoolean("started"):true;
        avOptions.setInteger(AVOptions.KEY_MEDIACODEC, mediaCodec);
        avOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, timeout);
        avOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, liveStreaming ? 1 : 0);
        if (!liveStreaming && cache) {
            avOptions.setString(AVOptions.KEY_CACHE_DIR, Config.DEFAULT_CACHE_DIR);
        }
        plVideoTextureView.setAVOptions(avOptions);
        //MediaController mediaController = new MediaController(themedReactContext, !liveStreaming, liveStreaming);
       // plVideoTextureView.setMediaController(mediaController);
        plVideoTextureView.setVideoPath(uri);
    }
    /**
     * 设置角度
     *
     * @param plVideoTextureView
     * @param rotation
     */
    @ReactProp(name = "rotation")
    public void setRotation(PLVideoTextureView plVideoTextureView, int rotation) {
        plVideoTextureView.setDisplayOrientation(rotation);
    }

    /**
     * 设置视频模式
     *
     * @param plVideoTextureView
     * @param aspectRatio
     */
    @ReactProp(name = "aspectRatio", defaultInt = PLVideoTextureView.ASPECT_RATIO_FIT_PARENT)
    public void setAspectRatio(PLVideoTextureView plVideoTextureView, int aspectRatio) {
        /**
         *  ASPECT_RATIO_ORIGIN = 0;
         *  ASPECT_RATIO_FIT_PARENT = 1
         *  ASPECT_RATIO_PAVED_PARENT = 2
         *  ASPECT_RATIO_16_9 = 3
         *  ASPECT_RATIO_4_3 = 4
         */
        plVideoTextureView.setDisplayAspectRatio(aspectRatio);
    }

    /**
     * 设置暂停或播放
     * @param plVideoTextureView
     * @param paused
     */
    @ReactProp(name = "paused")
    public void setPaused(PLVideoTextureView plVideoTextureView, boolean paused) {
        if (paused) {
            plVideoTextureView.pause();
            mEventEmitter.receiveEvent(getTargetId(), Events.PAUSE.toString(), Arguments.createMap());
        } else {
            plVideoTextureView.start();
        }
    }

    /**
     * 视频准备更新
     */
    private PLMediaPlayer.OnPreparedListener mOnPreparedListener=new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer, int i) {
            //Log.d(TAG, "onPrepared ! ");
            plVideoTextureView.start();
            mEventEmitter.receiveEvent(getTargetId(), Events.LOADING.toString(), Arguments.createMap());
        }
    };
    /**
     * 视频信息监听
     */
    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            //Log.d(TAG, "onInfo: " + what + ", " + extra);
            switch (what) {
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    //一下两行解决flv开始黑屏的问题
                    if(uri.indexOf(".flv")!=-1){
                        plVideoTextureView.pause();
                        plVideoTextureView.start();
                    }
                    mEventEmitter.receiveEvent(getTargetId(), Events.PLAYING.toString(), Arguments.createMap());
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    mEventEmitter.receiveEvent(getTargetId(),Events.BUFFERING_START.toString(),Arguments.createMap());
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    mEventEmitter.receiveEvent(getTargetId(), Events.BUFFERING_END.toString(), Arguments.createMap());
                    break;
            }
            return true;
        }
    };
    /**
     * 视频错误监听
     */
    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
            //Log.e(TAG, "Error happened, errorCode = " + errorCode);
            WritableMap event = Arguments.createMap();
            event.putInt("errorCode",errorCode);
            mEventEmitter.receiveEvent(getTargetId(), Events.ERROR.toString(), Arguments.createMap());
            return true;
        }
    };
    /**
     * 视频完成监听
     */
    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            //Log.d(TAG, "Play Completed !");
            mEventEmitter.receiveEvent(getTargetId(), Events.SHUTDOWN.toString(), Arguments.createMap());
        }
    };
    /**
     * 视频缓冲更新监听
     */
    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new PLMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
            //Log.d(TAG, "onBufferingUpdate: " + precent);
        }
    };
    /**
     * 视频seek完成监听
     */
    private PLMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new PLMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(PLMediaPlayer plMediaPlayer) {
            //Log.d(TAG, "onSeekComplete !");
        }
    };
    /**
     * 视频尺寸改变监听
     */
    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new PLMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
            //Log.d(TAG, "onVideoSizeChanged: " + width + "," + height);
        }
    };

    @Override
    public void addView(PLVideoTextureView parent, View child, int index) {
        return;
    }


    public int getTargetId() {
        return plVideoTextureView.getId();
    }
    @Override
    public void onHostResume() {
        plVideoTextureView.start();
    }

    @Override
    public void onHostPause() {
        plVideoTextureView.pause();
    }

    @Override
    public void onHostDestroy() {
        plVideoTextureView.stopPlayback();
    }
}
