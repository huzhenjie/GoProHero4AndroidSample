package com.scrat.app.goprohero4.util;

import android.util.Log;

/**
 * Log工具
 * Created by yixuanxuan on 15-6-2.
 */
public class L {

    private static final String LOG_TAG = "err";
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    private static final String STACK_FORMAT = "%s.%s(%d): %s";

    private static final boolean canWriteLogs = true; // 总开关
    private static final boolean canWriteVerboseLogs = true;
    private static final boolean canWriteDebugLogs = true;
    private static final boolean canWriteInfoLogs = true;
    private static final boolean canWriteWarnLogs = true;
    private static final boolean canWriteErrorLogs = true;

    private L() {
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static void v(String message, Object... args) {
        if (!canWriteVerboseLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.VERBOSE, null, message, caller, args);
    }

    public static void d(String message, Object... args) {
        if (!canWriteDebugLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.DEBUG, null, message, caller, args);
    }

    public static void i(String message, Object... args) {
        if (!canWriteInfoLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.INFO, null, message, caller, args);
    }

    public static void w(String message, Object... args) {
        if (!canWriteWarnLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.WARN, null, message, caller, args);
    }

    public static void e(Throwable ex) {
        if (!canWriteErrorLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.ERROR, ex, null, caller);
    }

    public static void e(String message, Object... args) {
        if (!canWriteErrorLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.ERROR, null, message, caller, args);
    }

    public static void e(Throwable ex, String message, Object... args) {
        if (!canWriteErrorLogs) {
            return;
        }

        StackTraceElement caller = getCallerStackTraceElement();
        log(Log.ERROR, ex, message, caller, args);
    }

    private static String getFormatMessage(StackTraceElement caller, String message, Object... args) {
        String callerClazzName = caller.getClassName();
        if (args.length > 0) {
            message = String.format(message, args);
        }
        return String.format(STACK_FORMAT, callerClazzName, caller.getMethodName(), caller.getLineNumber(), message);
    }

    private static void log(int priority, Throwable ex, String message, StackTraceElement caller, Object... args) {
        if (!canWriteLogs) {
            return;
        }

        String log;
        if (ex == null) {
            log = getFormatMessage(caller, message, args);
        } else {
            String logMessage = message == null ? ex.getMessage() : getFormatMessage(caller, message, args);
            String logBody = Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, logMessage, logBody);
        }

        Log.println(priority, LOG_TAG, log);
    }

}
