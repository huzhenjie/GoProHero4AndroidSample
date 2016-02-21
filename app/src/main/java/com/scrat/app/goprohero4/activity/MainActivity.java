package com.scrat.app.goprohero4.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.scrat.app.goprohero4.GoProApp;
import com.scrat.app.goprohero4.R;
import com.scrat.app.goprohero4.task.GoProControlAsyncTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGalleryBtn;
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mPowerOff;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadioGroup modes = (RadioGroup) findViewById(R.id.primary_modes);
        final RadioButton videoBtn = (RadioButton) findViewById(R.id.rb_mode_video);
        final RadioButton photoBtn = (RadioButton) findViewById(R.id.rb_mode_photo);
        modes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == videoBtn.getId()) {
                    showToast("video mode");
//                    http://10.5.5.9/gp/gpControl/command/mode?p=0
                    new GoProControlAsyncTask("/command/mode", "p=0").execute();
                } else if (checkedId == photoBtn.getId()) {
                    showToast("photo mode");
//                     http://10.5.5.9/gp/gpControl/command/mode?p=1
                    new GoProControlAsyncTask("/command/mode", "p=1").execute();
                }
            }
        });

        mGalleryBtn = (Button) findViewById(R.id.btn_gallery);
        mGalleryBtn.setOnClickListener(this);
        mStartBtn = (Button) findViewById(R.id.btn_start_shutter);
        mStartBtn.setOnClickListener(this);
        mStopBtn = (Button) findViewById(R.id.btn_stop_shutter);
        mStopBtn.setOnClickListener(this);
        mPowerOff = (Button) findViewById(R.id.btn_power_off);
        mPowerOff.setOnClickListener(this);

        mVideoView = (VideoView) findViewById(R.id.vv_preview);
        MediaController mc = new MediaController(this);
        mVideoView.setMediaController(mc);
        mVideoView.setVisibility(View.GONE);
    }

    private void showToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GoProApp.getContext(), content, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoGalleryActivity() {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == mGalleryBtn) {
            gotoGalleryActivity();
        } else if (v == mStartBtn) {
//            http://10.5.5.9/gp/gpControl/command/shutter?p=1
            new GoProControlAsyncTask("/command/shutter", "p=1").execute();
        } else if (v == mStopBtn) {
//            http://10.5.5.9/gp/gpControl/command/shutter?p=0
            new GoProControlAsyncTask("/command/shutter", "p=0").execute();
        } else if (v == mPowerOff) {
//            http://10.5.5.9/gp/gpControl/command/system/sleep
            new GoProControlAsyncTask("/command/system/sleep").execute();
        }
    }

}
