package com.android.stepcounter.utils;

import android.util.Log;

import com.android.stepcounter.BuildConfig;

public class Logger {
    private static final String TAG = "Logger";
    private static final boolean LOG = BuildConfig.DEBUG;
    private static final boolean MASSAGE = BuildConfig.DEBUG;

    private static String showMassage(String msg) {
        StringBuilder buffer = new StringBuilder();

        if (MASSAGE) {
            final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];

            buffer.append("[ ");
            buffer.append(Thread.currentThread().getName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getFileName());
            buffer.append(": ");
            buffer.append(stackTraceElement.getLineNumber());
            buffer.append(": ");
            buffer.append(stackTraceElement.getMethodName());
        }

        buffer.append("() ] >>>  ");
        buffer.append(msg);
        return buffer.toString();
    }

    public static void e(Object msg) {
        if (LOG && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, showMassage(msg.toString()));
        }
    }


    public static void e(String msg, Exception e) {
        if (LOG && Log.isLoggable(TAG, Log.ERROR)) {
            Log.e(TAG, showMassage(msg), e);
        }
    }
}
