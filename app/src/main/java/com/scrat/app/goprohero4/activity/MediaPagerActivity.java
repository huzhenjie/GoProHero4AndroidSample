package com.scrat.app.goprohero4.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scrat.app.goprohero4.Constants;
import com.scrat.app.goprohero4.GoProApp;
import com.scrat.app.goprohero4.R;
import com.scrat.app.goprohero4.model.GoProMedia;
import com.scrat.app.goprohero4.util.OkHttpHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by yixuanxuan on 16/2/17.
 */
public class MediaPagerActivity extends Activity implements View.OnClickListener {
    private TextView mPaginationTv;
    private TextView mTitleTv;
    private Button mDownloadBtn;
    private Button mDeleteBtn;
    private Button mPlayBtn;
    private ViewPager mPager;
    private ArrayList<GoProMedia> mMedias;
    private int mPosition;
    private MediaAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_media_pager);

        initView();

        initData();
    }

    private void initView() {
        mPaginationTv = (TextView) findViewById(R.id.pagination);
        mTitleTv = (TextView) findViewById(R.id.title);
        mDeleteBtn = (Button) findViewById(R.id.delete);
        mDeleteBtn.setOnClickListener(this);
        mDownloadBtn = (Button) findViewById(R.id.download);
        mDownloadBtn.setOnClickListener(this);
        mPlayBtn = (Button) findViewById(R.id.play);
        mPlayBtn.setOnClickListener(this);
        mPlayBtn.setVisibility(View.INVISIBLE);
        mPager = (ViewPager) findViewById(R.id.pager);

    }

    private void initData() {
        Intent i = getIntent();
        mMedias = (ArrayList<GoProMedia>) i.getSerializableExtra(Constants.EXTRA_KEY_MEDIAS);
        mPosition = i.getIntExtra(Constants.EXTRA_KEY_POSITION, 0);
        updateView(mPosition);
        mAdapter = new MediaAdapter(this);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(mPosition);
        mPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    public void onClick(View v) {
        GoProMedia media = mMedias.get(mPosition);
        if (v == mDeleteBtn) {
            new DeleteAsyncTask(media).execute();
        } else if (v == mDownloadBtn) {
            new DownloadAsyncTask(media).execute();
        } else if (v == mPlayBtn) {
            VideoActivity.startVideoActivity(this, media.getDownloadUrl());
        }
    }

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            updateView(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void updateView(int position) {
        mPosition = position;
        mPaginationTv.setText(String.format("%s/%s", position + 1, mMedias.size()));
        GoProMedia media = mMedias.get(position);
        if (media.isVideo()) {
            mPlayBtn.setVisibility(View.VISIBLE);
        } else {
            mPlayBtn.setVisibility(View.INVISIBLE);
        }
        mTitleTv.setText(media.getFileName());
    }

    private void updateViewToEmpty() {
        setContentView(R.layout.activity_empty_media_pager);
    }

    private class MediaAdapter extends PagerAdapter {
        private LayoutInflater inflater;
        public MediaAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mMedias.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, container, false);
            SimpleDraweeView imageView = (SimpleDraweeView) imageLayout.findViewById(R.id.image);
            GoProMedia media = mMedias.get(position);
            Uri uri = media.getThumbnailUri();
            imageView.setImageURI(uri);
            container.addView(imageLayout);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public static void startImagePagerActivityForResult(Activity activity, int requestCode, ArrayList<GoProMedia> medias, int position) {
        Intent i = new Intent(activity, MediaPagerActivity.class);
        i.putExtra(Constants.EXTRA_KEY_POSITION, position);
        i.putExtra(Constants.EXTRA_KEY_MEDIAS, medias);
        activity.startActivityForResult(i, requestCode);
    }

    public class DeleteAsyncTask extends AsyncTask<Void, Void, Void> {
        private GoProMedia mMedia;
        public DeleteAsyncTask(GoProMedia media) {
            mMedia = media;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String url = mMedia.getDeleteUrl();
            try {
                OkHttpHelper.getInstance().httpGet(url);
                mMedias.remove(mPosition);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int totalMedia = mMedias.size();
            if (mPosition == 0) {
                if (totalMedia == 0) {
                    updateViewToEmpty();
                    return;
                }
            } else if (totalMedia < mPosition + 1) {
                mPosition --;
            }
            mAdapter.notifyDataSetChanged();
            updateView(mPosition);
        }
    }

    public class DownloadAsyncTask extends AsyncTask<Void, Void, Void> {

        private GoProMedia mMedia;
        public DownloadAsyncTask(GoProMedia media) {
            mMedia = media;
        }

        private File getTargetFile() {
            return new File("/sdcard/DCIM/Camera/" + mMedia.getFileName());
        }

        @Override
        protected Void doInBackground(Void... params) {
            File file = getTargetFile();
            if (file.exists())
                return null;

            String url = mMedia.getDownloadUrl();
            OkHttpHelper.getInstance().download(url, file);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showToast("Download success");
        }
    }

    private void showToast(final String content) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(GoProApp.getContext(), content, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
