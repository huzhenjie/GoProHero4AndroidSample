package com.scrat.app.goprohero4.util;

/**
 * Created by yixuanxuan on 16/2/21.
 */
public class GoProUtil {
    private static final String sHost = "http://10.5.5.9";

    public static String getDownloadUrl(String folder, String fileName) {
        return sHost + "/videos/DCIM/" + folder + "/" + fileName;
    }

    public static String getThumbnailUrl(String folder, String fileName) {
        return sHost + "/gp/gpMediaMetadata?p=/" + folder + "/" + fileName;
    }

    public static String getDeleteUrl(String folder, String fileName) {
        return sHost + "/gp/gpControl/command/storage/delete?p=/" + folder + "/" + fileName;
    }

    public static String getMediaListUrl() {
        return sHost + ":8080/gp/gpMediaList";
    }

}
