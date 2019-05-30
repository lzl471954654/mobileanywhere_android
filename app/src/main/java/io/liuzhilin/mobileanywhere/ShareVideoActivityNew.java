package io.liuzhilin.mobileanywhere;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.liuzhilin.mobileanywhere.bean.Blog;
import io.liuzhilin.mobileanywhere.bean.BlogComment;
import io.liuzhilin.mobileanywhere.callback.RequestCallback;
import io.liuzhilin.mobileanywhere.manager.ThreadManager;
import io.liuzhilin.mobileanywhere.manager.UserCacheManager;
import io.liuzhilin.mobileanywhere.requests.RequestCenter;
import io.liuzhilin.mobileanywhere.util.CallBackParser;
import io.liuzhilin.mobileanywhere.util.FileUtils;
import io.liuzhilin.mobileanywhere.util.GsonUtils;

public class ShareVideoActivityNew extends AppCompatActivity {

    private Button mBtnStart;
    private Button mBtnPlay;
    private Button mSendButton;
    private TextView videoSize;

    private File mVideoFile;


    private String url = null;
    private String pointId = null;
    private String blogId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_video_view);
        mBtnStart = findViewById(R.id.btn_start);
        mBtnPlay = findViewById(R.id.btn_play);
        mSendButton = findViewById(R.id.tv_show);
        videoSize = findViewById(R.id.video_size_text);

        url = getIntent().getStringExtra("url");
        pointId = getIntent().getStringExtra("pointId");
        blogId = getIntent().getStringExtra("blogId");

        initListener();
    }

    private void initListener(){
        mBtnStart.setOnClickListener((view)->{
            mVideoFile = new File(this.getCacheDir(),"/video/"+System.currentTimeMillis()+".mp4");
            if (!mVideoFile.getParentFile().exists()){
                mVideoFile.getParentFile().mkdir();
            }
            Uri fileUri = MyFileProvider.getUriForFile(this,BuildConfig.APPLICATION_ID+".fileprovider",mVideoFile);
            Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0);
            //好使
            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,10485760L);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
            startActivityForResult(intent,5);
        });
        mSendButton.setOnClickListener((view)->{
            doUploadVideo();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Share","destroy");
    }

    private void doUploadVideo(){
        if (mVideoFile == null || !mVideoFile.exists()){
            Toast.makeText(this,"没有文件，无法上传。",Toast.LENGTH_SHORT).show();
            return;
        }
        long size = mVideoFile.length();
        videoSize.setText("视频大小："+size+"字节");
        ThreadManager.getThreadManager().addNetworkTask(()->{
            FileUtils.INSTANCE.sendFileToGitHub(mVideoFile, ".mp4", new RequestCallback() {
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
                            Toast.makeText(ShareVideoActivityNew.this,"上传失败",Toast.LENGTH_SHORT).show();
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
            blog.setBlogType((short)3);
            blog.setBlogOwner(UserCacheManager.getCurrentUser().getUserAccount());
            blog.setBlogCreateTime(new Date(System.currentTimeMillis()));
            blog.setBlogMediaUrl(mediaUrl);
            blog.setBlogPointId(pointId);
            String json = GsonUtils.gson.toJson(blog);
            map.put("blogData",json);
        }else {
            BlogComment blogComment = new BlogComment();
            blogComment.setBlogCommentsOwner(UserCacheManager.getCurrentUser().getUserAccount());
            blogComment.setBlogCommentsType((short)3);
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
                    Toast.makeText(ShareVideoActivityNew.this,"发送成功",Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void failed(Exception e) {
                e.printStackTrace();
                runOnUiThread(()->{
                    Toast.makeText(ShareVideoActivityNew.this,"发送失败:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                });
            }
        }));
    }
}
