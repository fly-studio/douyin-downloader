package com.fly.video.downloader.util.io;

import android.support.annotation.NonNull;

import com.fly.video.downloader.App;
import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.core.security.Encrypt;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class FileStorage extends File {

    public enum TYPE {
        IMAGE,
        AVATAR,
        VIDEO
    }

    public FileStorage(@NonNull File file)
    {
        super(file.getAbsolutePath());
    }

    public FileStorage(@NonNull String pathname) {
        super(pathname);
    }

    public FileStorage(String parent, @NonNull String child) {
        super(parent, child);
    }

    public FileStorage(File parent, @NonNull String child) {
        super(parent, child);
    }

    public FileStorage(@NonNull URI uri) {
        super(uri);
    }

    public boolean createNewFile() throws IOException
    {
        getParentFile().mkdirs();

        return super.createNewFile();
    }

    public FileStorage createTempFile() throws IOException
    {
        getParentFile().mkdirs();
        return new FileStorage(File.createTempFile(this.getName(), null, this.getParentFile()));
    }

    public static final class Builder
    {
        private TYPE type;
        private String filename;
        private File path;

        public Builder(@NonNull TYPE type, @NonNull String filename)
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

        public FileStorage build()
        {
            return new FileStorage(path, filename);
        }

        public Builder setToCacheDir()
        {
            path = Storage.getCacheDir(App.getAppContext(), getRelativePath());
            return this;
        }

        public Builder setToDCIMDir()
        {
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

            return this;
        }
    }

}
