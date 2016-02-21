package com.scrat.app.goprohero4.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.scrat.app.goprohero4.Constants;
import com.scrat.app.goprohero4.R;


/**
 * Created by yixuanxuan on 16/2/18.
 */
public class VideoActivity extends Activity {
    private VideoView mVideoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
    }

    private void initView() {
        mVideoView = (VideoView) findViewById(R.id.video);

        Uri uri = getVideoUri();
        mVideoView.setVideoURI(uri);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }

    private Uri getVideoUri() {
        Intent i = getIntent();
        String url = i.getStringExtra(Constants.EXTRA_KEY_URL);
        return Uri.parse(url);
    }

    public static void startVideoActivity(Activity activity, String url) {
        Intent i = new Intent(activity, VideoActivity.class);
        i.putExtra(Constants.EXTRA_KEY_URL, url);
        activity.startActivity(i);
    }
}
