package com.scrat.app.goprohero4.model;

import android.net.Uri;

import com.scrat.app.goprohero4.util.GoProUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yixuanxuan on 16/2/7.
 */
public class GoProMedia implements Serializable {
    private String folder; // d
    private String fileName; // n
    private long modifyTime; // mod
    private long size; // s

    public static final GoProMedia NULL = new GoProMedia();

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFolder() {
        return folder;
    }

    public long getSize() {
        return size;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public Uri getThumbnailUri() {
        return Uri.parse(GoProUtil.getThumbnailUrl(folder, fileName));
    }

    public String getDownloadUrl() {
        return GoProUtil.getDownloadUrl(folder, fileName);
    }

    public String getDeleteUrl() {
        return GoProUtil.getDeleteUrl(folder, fileName);
    }

    public boolean isVideo() {
        return fileName != null && fileName.endsWith(".MP4");
    }

    @Override
    public String toString() {
        return "GoProMedia{" +
                "folder='" + folder + '\'' +
                ", fileName='" + fileName + '\'' +
                ", modifyTime=" + modifyTime +
                ", size=" + size +
                '}';
    }

    public static List<GoProMedia> parse(String jsonStr) throws JSONException {
        JSONObject obj = new JSONObject(jsonStr);
        JSONArray medias = obj.optJSONArray("media");
        if (medias == null)
            return Collections.emptyList();

        List<GoProMedia> mediaList = new ArrayList<>();
        int totalMedia = medias.length();
        for (int i = 0; i < totalMedia; i++) {
            JSONObject media = medias.optJSONObject(i);
            if (media == null) {
                continue;
            }

            String folder = media.optString("d");
            JSONArray fs = media.optJSONArray("fs");
            if (fs == null) {
                continue;
            }

            int totalFs = fs.length();
            for (int j = 0; j < totalFs; j++) {
                JSONObject f = fs.optJSONObject(j);
                GoProMedia goProMedia = toGoProMedia(f, folder);
                if (goProMedia != NULL) {
                    mediaList.add(goProMedia);
                }
            }
        }
        return mediaList;
    }

    private static GoProMedia toGoProMedia(JSONObject obj, String folder) {
        if (obj == null)
            return NULL;

        String fileName = obj.optString("n");
        long modifyTime = obj.optLong("mod");
        long size = obj.optLong("s");

        GoProMedia goProMedia = new GoProMedia();
        goProMedia.setFolder(folder);
        goProMedia.setFileName(fileName);
        goProMedia.setModifyTime(modifyTime);
        goProMedia.setSize(size);
        return goProMedia;
    }
}
