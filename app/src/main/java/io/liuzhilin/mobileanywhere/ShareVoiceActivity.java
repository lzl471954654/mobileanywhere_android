package io.liuzhilin.mobileanywhere;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.liuzhilin.mobileanywhere.bean.Blog;
import io.liuzhilin.mobileanywhere.bean.BlogComment;
import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.manager.ThreadManager;
import io.liuzhilin.mobileanywhere.manager.UserCacheManager;
import io.liuzhilin.mobileanywhere.requests.RequestCenter;
import io.liuzhilin.mobileanywhere.util.CallBackParser;
import io.liuzhilin.mobileanywhere.util.DIgestUtils;
import io.liuzhilin.mobileanywhere.util.FileUtils;
import io.liuzhilin.mobileanywhere.util.GsonUtils;

public class ShareVoiceActivity extends AppCompatActivity {

    private Button mBtnStart;
    private Button mBtnPlay;
    private Button mSendButton;

    private File mAudioFile;
    private Long mStartRecordTime;
    private Long mEndRecordTime;

    private ExecutorService mExecutorService;
    private Handler mMainHandler;

    private MediaRecorder mMediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean mIsPlaying=false;

    private String url = null;
    private String pointId = null;
    private String blogId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_voice);

        mBtnStart = findViewById(R.id.btn_start);
        mBtnPlay = findViewById(R.id.btn_play);
        mSendButton = findViewById(R.id.tv_show);

        mMainHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newSingleThreadExecutor();
        url = getIntent().getStringExtra("url");
        pointId = getIntent().getStringExtra("pointId");
        blogId = getIntent().getStringExtra("blogId");

        //对按钮进行监听
        mBtnStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //开始录音
                        startRecord();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        //停止录音
                        stopRecord();
                        break;
                }
                return true;
            }
        });
        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playrecorder();
            }
        });
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUploadAudio();
            }
        });
    }

    private void doUploadAudio(){
        if (mAudioFile == null || !mAudioFile.exists()){
            Toast.makeText(this,"没有录音，无法上传。",Toast.LENGTH_SHORT).show();
            return;
        }
        ThreadManager.getThreadManager().addNetworkTask(()->{
            FileUtils.INSTANCE.sendFileToGitHub(mAudioFile, ".m4a", new RequestCallback() {
                @Override
                public void success(String json) {
                    System.out.println(json);
                    doUpload(json);
                }

                @Override
                public void failed(Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ShareVoiceActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
    }

    private void doUpload(String mediaUrl){

        Map<String,String> map = new HashMap<>();
        if (RequestCenter.SEND_BLOG.equals(url)){
            Blog blog = new Blog();
            blog.setBlogType((short)4);
            blog.setBlogOwner(UserCacheManager.getCurrentUser().getUserAccount());
            blog.setBlogCreateTime(new Date(System.currentTimeMillis()));
            blog.setBlogMediaUrl(mediaUrl);
            blog.setBlogPointId(pointId);
            String json = GsonUtils.gson.toJson(blog);
            map.put("blogData",json);
        }else {
            BlogComment blogComment = new BlogComment();
            blogComment.setBlogCommentsOwner(UserCacheManager.getCurrentUser().getUserAccount());
            blogComment.setBlogCommentsType((short)4);
            blogComment.setBlogCommentsCreateTime(new Date(System.currentTimeMillis()));
            blogComment.setBlogCommentsMediaUrl(url);
            blogComment.setBlogId(blogId);
            blogComment.setBlogCommentsToType((short)1);
            String json = GsonUtils.gson.toJson(blogComment);
            map.put("data",json);
        }
        RequestCenter.Companion.requestPost(url,map,new CallBackParser(new RequestCallback() {
            @Override
            public void success(String json) {
                runOnUiThread(()->{
                    Toast.makeText(ShareVoiceActivity.this,"发送成功",Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void failed(Exception e) {
                e.printStackTrace();
                runOnUiThread(()->{
                    Toast.makeText(ShareVoiceActivity.this,"发送失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
            }
        }));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopPlayer();
    }

    /**
     * 开始录音
     */
    private void startRecord() {
        //更改UI的状态
        mBtnStart.setText("松手就可以停止录音");

        //开始录音
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {
                //释放之前存在的资源,因为这里是按下的时候就开始录音，所以按下的时候一定要先释放相应的资源
                releaseResources();
                if (!doStart()) {//不成功弹Toast提示用户
                    ToastFail();//提示用户
                }
            }
        });
    }

    /**
     * 提示用户失败信息
     */
    private void ToastFail() {
        mAudioFile = null;
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShareVoiceActivity.this, "录制音频失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 开始录音是否成功,
     * 所以所有的逻辑就直接在这里去写了
     */
    private boolean doStart() {
        /*
         * 1.创建MediaRecorder对象
         * 2.创建相应的保存文件
         * 3.配置相应的MediaRecorder
         * 4.开始录音
         */
        try {
            //创建mediaRecorder对象
            mMediaRecorder = new MediaRecorder();
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //创建保存的文件
                String path = this.getCacheDir().getAbsolutePath() + "/" + System.currentTimeMillis() + ".m4a";
                mAudioFile = new File(path);
                mAudioFile.getParentFile().mkdirs();
                mAudioFile.createNewFile();

                /*
                 * 重点来了，配置MediaRecorder
                 */
                //配置采集方式，这里用的是麦克风的采集方式
                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //配置输出方式，这里用的是MP4，
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                //配置采样频率，频率越高月接近原始声音，Android所有设备都支持的采样频率为44100
                mMediaRecorder.setAudioSamplingRate(44100);
                //配置文件的编码格式,AAC是比较通用的编码格式
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                //配置码率，这里一般通用的是96000
                mMediaRecorder.setAudioEncodingBitRate(96000);
                //配置录音文件的位置
                mMediaRecorder.setOutputFile(mAudioFile.getAbsolutePath());

                //开始录制音频
                mMediaRecorder.prepare();//准备
                mMediaRecorder.start();//开始录音

                /*因为这里要做相应的时间判断，所以要做时间的记录*/
                mStartRecordTime = System.currentTimeMillis();

            } else {
                //因为这里SD卡没有挂在，所以就直接返回false，如果你真的想在项目中使用的话，需要判断内存大小什么的，这里为了简便就没有写，
                //但是现在基本上都没事，因为手机内容都很大，但是如果做项目的话这些都要做的！
                return false;
            }
        } catch (IOException |RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }


    /**
     * 停止录音
     */
    private void stopRecord() {
        mBtnStart.setText("按下开始录音");
        mExecutorService.submit(new Runnable() {
            @Override
            public void run() {

                if(!doStop()){
                    ToastFail();
                }
                //这里停止后应该，释放相应的资源
                releaseResources();
            }
        });
    }

    /**
     * 停止录音
     */
    private boolean doStop() {
        try {
            //这里说处理停止播放的逻辑
            mMediaRecorder.stop();

            //因为这里要判断相应的时间,如果大于三秒就直接保存，否则删除文件
            mEndRecordTime = System.currentTimeMillis();
            final int time = (int) ((mEndRecordTime - mStartRecordTime) / 1000);
            if (time > 1) {
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String des = "录音成功" + time + "秒";
                        Toast.makeText(ShareVoiceActivity.this,des,Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (mAudioFile.exists()) {
                    mAudioFile.delete();
                }
                mMainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShareVoiceActivity.this,"录音时间太短",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            Log.e("error", "doStop: " + e);
            return false;
        }
        return true;
    }

    /**
     * 播放录音
     *
     */
    public void playrecorder() {
        if (!mIsPlaying) {
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    doPlay(mAudioFile);
                }
            });

        } else {
            Toast.makeText(ShareVoiceActivity.this, "正在播放", Toast.LENGTH_SHORT).show();
        }
    }


    private void doPlay(File audioFile) {
        try {
            //配置播放器 MediaPlayer
            mediaPlayer = new MediaPlayer();
            //设置声音文件
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            //配置音量,中等音量
            mediaPlayer.setVolume(1,1);
            //播放是否循环
            mediaPlayer.setLooping(false);

            //设置监听回调 播放完毕
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopPlayer();
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    stopPlayer();
                    Toast.makeText(ShareVoiceActivity.this,"播放失败",Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

            //设置播放
            mediaPlayer.prepare();
            mediaPlayer.start();

            //异常处理，防止闪退

        } catch (Exception e) {
            e.printStackTrace();
            stopPlayer();
        }


    }

    private void stopPlayer(){
        mIsPlaying=false;
        mediaPlayer.release();
        mediaPlayer=null;
    }


    /**
     * 释放资源
     */
    private void releaseResources() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在主线程关闭的时候一定要停止线程，避免内存泄露
        mExecutorService.shutdownNow();
    }
}
