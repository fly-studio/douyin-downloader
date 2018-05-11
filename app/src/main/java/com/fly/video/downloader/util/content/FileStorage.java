package com.fly.video.downloader.util.content;

import android.support.annotation.NonNull;

import com.fly.video.downloader.App;
import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.core.security.Encrypt;

import java.io.File;

public class FileStorage {

    public enum TYPE {
        IMAGE,
        AVATAR,
        VIDEO
    }

    protected TYPE type;
    protected String filename;

    public FileStorage(@NonNull TYPE type, @NonNull String filename)
    {
        this.type = type;
        this.filename = filename;
    }

    protected String getRelativePath()
    {
        String md5 = Encrypt.MD5(filename);
        String path = null;
        switch (type)
        {
            case IMAGE:
                path = "images";
                break;
            case AVATAR:
                path = "avatars";
                break;
            case VIDEO:
                path = "video";
                break;
        }

        return path + "/" + md5.substring(0, 2) + "/" + md5.substring(2, 4) + "/";
    }

    protected File createFile(File file) throws Exception
    {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                //创建文件
                file.createNewFile();
            } catch (Exception e) {
                throw e;
            }
        }
        return file;
    }

    public File getCacheDir() throws Exception
    {
        File path = Storage.getCacheDir(App.getAppContext(), getRelativePath());
        path.mkdirs();
        return File.createTempFile(filename, null, path);
    }

    public File getDCIMDir() throws Exception
    {
        File path;
        switch (type)
        {
            case VIDEO:
                path = Storage.getVideoDir();
                break;
            case AVATAR:
            case IMAGE:
            default:
                path = Storage.getPictureDir();
                break;
        }
        path.mkdirs();
        return createFile(new File(path, filename));
    }
}
