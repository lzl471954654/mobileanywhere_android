package io.liuzhilin.mobileanywhere;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().hide();
        videoView = findViewById(R.id.videoView);
        String url = getIntent().getStringExtra("url");
        if (url == null || url.isEmpty()){
            Toast.makeText(this,"无效视频地址",Toast.LENGTH_SHORT).show();
        }else {
            videoView.setMediaController(new MediaController(this));
            videoView.setVideoURI(Uri.parse(url));
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(VideoPlayerActivity.this,"播放完成",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
