package com.scrat.app.goprohero4.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;
import com.scrat.app.goprohero4.Constants;
import com.scrat.app.goprohero4.R;
import com.scrat.app.goprohero4.model.GoProMedia;
import com.scrat.app.goprohero4.util.GoProUtil;
import com.scrat.app.goprohero4.util.L;
import com.scrat.app.goprohero4.util.OkHttpHelper;
import com.scrat.app.goprohero4.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by yixuanxuan on 16/2/7.
 */
public class GalleryActivity extends Activity {
    private List<GoProMedia> mediaList;
    private GalleryAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        initView();
        reloadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ACTIVITY_KEY_MEDIA_PAGER) {
            reloadData();
        }
    }

    private void initView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_row);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        mediaList = new ArrayList<>();
        mAdapter = new GalleryAdapter(this, mediaList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 5, false));
    }

    private void reloadData() {
        new LoadGalleryAsyncTask(mAdapter, mediaList).execute();
    }

    class LoadGalleryAsyncTask extends AsyncTask<Void, Void, Void> {
        private GalleryAdapter mAdapter;
        private List<GoProMedia> mMediaList;

        public LoadGalleryAsyncTask(GalleryAdapter adapter, List<GoProMedia> mediaList) {
            mAdapter = adapter;
            mMediaList = mediaList;
        }

        @Override
        protected Void doInBackground(Void... params) {
            int i = 0;
            while (i ++ < 3) {
                try {
                    String content = OkHttpHelper.getInstance().httpGet(GoProUtil.getMediaListUrl());
                    //{"id":"209712930612731535","media":[{"d":"100GOPRO","fs":[{"n":"GOPR9997.MP4","mod":"1454799418","ls":"-1","s":"28568661"},{"n":"GOPR9998.MP4","mod":"1454845214","ls":"-1","s":"14155001"}]},{"d":"101GOPRO","fs":[{"n":"GOPR0003.JPG","mod":"1454847052","s":"3584926"},{"n":"GOPR0004.MP4","mod":"1454867262","ls":"168582","s":"5949727"}]}]}
                    List<GoProMedia> mediaList = GoProMedia.parse(content);
                    mMediaList.clear();
                    mMediaList.addAll(mediaList);
                    L.e("%s", mediaList);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    waitFor(3);
                    L.e("retry %s", i);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void waitFor(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

        private LayoutInflater mInflater;
        private List<GoProMedia> mMediaList;

        public GalleryAdapter(Context context, List<GoProMedia> mediaList) {
            mInflater = LayoutInflater.from(context);
            mMediaList = mediaList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_gallery, null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Uri uri = mMediaList.get(position).getThumbnailUri();
            holder.imageView.setImageURI(uri);
            holder.index = position;
        }

        @Override
        public int getItemCount() {
            return mMediaList.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private int index;
        private SimpleDraweeView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (SimpleDraweeView) itemView.findViewById(R.id.image);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            L.d("index=%s", index);
            MediaPagerActivity.startImagePagerActivityForResult(
                    GalleryActivity.this,
                    Constants.ACTIVITY_KEY_MEDIA_PAGER,
                    (ArrayList<GoProMedia>) mediaList,
                    index);
        }
    }

}
