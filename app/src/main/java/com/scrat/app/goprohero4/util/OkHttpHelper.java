package com.scrat.app.goprohero4.util;

import android.text.TextUtils;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import okio.BufferedSink;
import okio.Okio;

/**
 * Created by yixuanxuan on 16/1/15.
 */
public class OkHttpHelper {
    private static class SingletonHolder {
        private static OkHttpHelper instance = new OkHttpHelper();
    }

    public static OkHttpHelper getInstance(){
        return SingletonHolder.instance;
    }

    private OkHttpClient client;
    private OkHttpHelper() {
        client = new OkHttpClient();
    }

    private Request buildPostFormRequest(String url, Map<String, String> params)
            throws UnsupportedEncodingException {

        FormEncodingBuilder builder = new FormEncodingBuilder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncoded(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }

        RequestBody requestBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(url).post(requestBody);

        return reqBuilder.build();
    }

    private Request buildGetFormRequest(String url, Map<String, String> params) throws UnsupportedEncodingException {
        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder(url);
            sb.append('?');
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
            sb.deleteCharAt(sb.lastIndexOf("&"));
            url = sb.toString();
        }
        Request.Builder builder = new Request.Builder().url(url);
        return builder.build();
    }

    private String getResponseBody(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response.code() + ", " + response.message());
        }

        String encoding = "UTF-8";
        String contextType = response.headers().get("Content-Type");
        if (!TextUtils.isEmpty(contextType)) {
            if (contextType.toUpperCase().contains("UTF")) {
                encoding = "UTF-8";
            } else if (contextType.toUpperCase().contains("GBK")) {
                encoding = "GBK";
            }
        }
        return new String(response.body().bytes(), encoding);
    }

    public String httpPost(String url, Map<String, String> params) throws IOException {
        L.d("[httpPost] %s", url);
        L.d("[params] %s", params);
        Request request = buildPostFormRequest(url, params);
        Response response = client.newCall(request).execute();
        L.d("%s", response.code());
        String body = getResponseBody(response);
        L.d("[response] %s", body);
        return body;
    }

    public String httpGet(String url, Map<String, String> params) throws IOException {
        L.d("[httpGet] %s", url);
        L.d("[params] %s", params);
        Request request = buildGetFormRequest(url, params);
        Response response = client.newCall(request).execute();
        L.d("%s", response.code());
        String body = getResponseBody(response);
        L.d("[response] %s", body);
        return body;
    }

    public String httpGet(String url) throws IOException {
        return httpGet(url, null);
    }

    public void download(String url, File targetFile) {
        Request request = new Request.Builder().url(url).build();
        BufferedSink sink = null;
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            sink = Okio.buffer(Okio.sink(targetFile));
            sink.writeAll(response.body().source());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(sink);
        }
    }

    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }
}
