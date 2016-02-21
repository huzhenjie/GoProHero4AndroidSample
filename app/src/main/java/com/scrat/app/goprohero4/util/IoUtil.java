package com.scrat.app.goprohero4.util;

import android.database.Cursor;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yixuanxuan on 16/2/18.
 */
public class IoUtil {
    public static void close(Cursor cursor) {
        if (cursor == null)
            return;
        try {
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(Closeable closeable) {
        if (closeable == null)
            return;

        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
