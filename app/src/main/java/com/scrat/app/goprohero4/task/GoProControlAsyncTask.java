package com.scrat.app.goprohero4.task;

import android.os.AsyncTask;

import com.scrat.app.goprohero4.util.L;
import com.scrat.app.goprohero4.util.OkHttpHelper;

import java.io.IOException;

/**
 * Created by yixuanxuan on 16/2/20.
 */
public class GoProControlAsyncTask extends AsyncTask<Void, Void, Void> {
    private String mAction;
    private String mParams;

    public GoProControlAsyncTask(String action, String params) {
        mAction = action;
        mParams = params;
    }

    public GoProControlAsyncTask(String action) {
        mAction = action;
        mParams = null;
    }

    private String getUrl() {
        String url = "http://10.5.5.9/gp/gpControl" + mAction;
        if (mParams != null) {
            url += "?" + mParams;
        }
        return url;
    }

    @Override
    protected Void doInBackground(Void... params) {
        String url = getUrl();
        try {
            OkHttpHelper.getInstance().httpGet(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        L.i("%s", getUrl());
    }
}
