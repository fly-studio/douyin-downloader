package com.fly.video.downloader.core.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Storage {

    public static String getStoragePath(@NonNull Context context)
    {
        return getStoragePath(context, false);
    }

    public static String getStoragePath(@NonNull Context mContext, boolean is_removale) {

        StorageManager mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);
            final int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                Object storageVolumeElement = Array.get(result, i);
                String path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (is_removale == removable) {
                    return path;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static File getCacheDir(Context context)
    {
        return getCacheDir(context, null);
    }

    public static File getCacheDir(@NonNull Context context, String suffix)
    {
        File file = context.getCacheDir();
        return new File(file, suffix);
    }

    public static File getFileDir(@NonNull Context context, String suffix)
    {
        File file = context.getFilesDir();
        return new File(file, suffix);
    }

    public static File getFileDir(@NonNull Context context)
    {
        return getFileDir(context, null);
    }

    public static String getNowFilename()
    {
        return getNowFilename("yyyyMMddHHmmss");
    }

    public static String getNowFilename(@NonNull String format)
    {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    public static String getRandomFilename(int length)
    {
        return RandomStringUtils.randomNumeric(length);
    }

    public static File getDataDir(@NonNull Context context)
    {
        return getDataDir(context, null);
    }

    @SuppressLint("NewApi")
    public static File getDataDir(@NonNull Context context, String suffix)
    {
        File file = context.getDataDir();
        return new File(file, suffix);
    }

    public static File getVideoDir()
    {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    public static File getPictureDir()
    {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    public static File getDCIMDir()
    {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    public static void rescanGallery(@NonNull Context context, File ...files)
    {
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            paths[i] = files[i].getAbsolutePath();
        }
        MediaScannerConnection.scanFile(context, paths,null, null);
    }

}
