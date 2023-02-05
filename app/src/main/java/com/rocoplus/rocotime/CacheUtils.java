package com.rocoplus.rocotime;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CacheUtils {
    //构造函数
    private CacheUtils() {}
    //获取外部缓存
    @NonNull
    private static String getExternalCacheDirPath(@NonNull Context context) {
        return Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() + "/cache";
    }
    //清除所有缓存
    public static void clearAllCache(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            CacheUtils.deleteDir(new File(CacheUtils.getExternalCacheDirPath(context)));
        CacheUtils.deleteDir(context.getCacheDir().getAbsoluteFile());
    }
    //统计缓存大小
    private static long totalCacheSize(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return CacheUtils.totalDirSize(context.getCacheDir()) + CacheUtils.totalDirSize(new File(CacheUtils.getExternalCacheDirPath(context)));
        else return CacheUtils.totalDirSize(context.getCacheDir());
    }
    //获取清理大小
    @NonNull
    public static String getClearedByte(Context context) {
        long before = CacheUtils.totalCacheSize(context);
        CacheUtils.clearAllCache(context);
        return CacheUtils.formatFileSize(before - CacheUtils.totalCacheSize(context));
    }
    //递归统计大小
    private static long totalDirSize(@NonNull File dir) {
        File[] files;
        long size = 0;
        if (dir.isDirectory() && (files = dir.listFiles()) != null)
            for (File file : files)
                size += CacheUtils.totalDirSize(file);
        else return dir.length();
        return size;
    }
    //转换文件大小
    @NonNull
    public static String formatFileSize(long size) {
        if (size < 1024) return size + " Byte";
        double bytes = size;
        if ((bytes /= 1024) < 1024)
            return new BigDecimal(Double.toString(bytes)).setScale(2, RoundingMode.HALF_UP).toPlainString() + " KB";
        if ((bytes /= 1024) < 1024)
            return new BigDecimal(Double.toString(bytes)).setScale(2, RoundingMode.HALF_UP).toPlainString() + " MB";
        if ((bytes /= 1024) < 1024)
            return new BigDecimal(Double.toString(bytes)).setScale(2, RoundingMode.HALF_UP).toPlainString() + " GB";
        return new BigDecimal(Double.toString(bytes / 1024)).setScale(2, RoundingMode.HALF_UP).toPlainString() + " TB";
    }
    //递归删除目录
    public static boolean deleteDir(@NonNull File dir) {
        if (!dir.isDirectory())
            return !dir.exists() || dir.delete();
        File[] files = dir.listFiles();
        if (files != null)
            for (File file : files)
                if (!CacheUtils.deleteDir(file))
                    return false;
        return true;
    }
}
