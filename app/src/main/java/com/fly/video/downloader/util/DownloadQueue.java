package com.fly.video.downloader.util;

import com.fly.video.downloader.util.content.Downloader;
import com.fly.video.downloader.util.exception.DownloadFileException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadQueue {

    public enum STATUS {
        UNKNOWN,
        DOWNLOADING,
        DONE,
        CANCELED
    }

    protected HashMap<String, DownloaderStatus> downloaders = new HashMap<>();
    protected QueueListener listener = null;

    public DownloadQueue setQueueListener(QueueListener listener)
    {
        this.listener = listener;
        return this;
    }

    public ArrayList<Downloader> getAllDownloaders()
    {
        ArrayList<Downloader> result = new ArrayList<>();

        for (Map.Entry<String, DownloaderStatus> entry : downloaders.entrySet()
             ) {
            result.add(entry.getValue().downloader);
        }
        return result;
    }

    public String getHash(Downloader downloader)
    {
        for (Map.Entry<String, DownloaderStatus> entry : downloaders.entrySet())
        {
            if (entry.getValue().downloader == downloader)
                return entry.getKey();
        }
        return null;
    }

    public DownloaderStatus get(Downloader downloader)
    {
        for (Map.Entry<String, DownloaderStatus> entry : downloaders.entrySet())
        {
            if (entry.getValue().downloader == downloader)
                return entry.getValue();
        }
        return null;
    }

    public DownloaderStatus get(String hash)
    {
        return downloaders.get(hash);
    }


    public DownloadQueue cancel(String[] ...hashes)
    {
        for (int i = 0; i < hashes.length; i++) {
            if (downloaders.containsKey(hashes[i]))
                downloaders.get(hashes[i]).cancel();
        }
        return this;
    }

    public DownloadQueue cancelAll()
    {
        for (Map.Entry<String, DownloaderStatus> entry : downloaders.entrySet()
                ) {
            entry.getValue().cancel();
        }
        return this;
    }

    public DownloadQueue clear()
    {
        cancelAll();
        downloaders.clear();
        return this;
    }

    public boolean add(String hash, Downloader downloader)
    {
        // 没加入，或者已经取消或出错了
        if (!downloaders.containsKey(hash) || (downloaders.containsKey(hash) && downloaders.get(hash).status == STATUS.CANCELED)) {
            downloaders.put(hash, new DownloaderStatus(hash, downloader.setDownloadListener(mDownloaderListener)));
            return true;
        }
        return false;
    }

    public DownloadQueue remove(String[] ...hashes)
    {
        for (int i = 0; i < hashes.length; i++) {
            cancel(hashes[i]);
            downloaders.remove(hashes[i]);
        }

        return this;
    }

    public DownloadQueue download() throws Exception
    {
        for (Map.Entry<String, DownloaderStatus> entry : downloaders.entrySet()
             ) {
            DownloaderStatus status = entry.getValue();
            Downloader downloader = status.downloader;
            downloader.setDownloadListener(mDownloaderListener);
            File file = downloader.getFile();
            if (file == null)
                throw new DownloadFileException("Please saveToX before download, eg: new Download(..).saveToCache()");

            if (file.exists() && file.length() > 0)
                mDownloaderListener.onDownloaded(downloader);

            status.download();

        }
        return this;
    }

    Downloader.DownloaderListener mDownloaderListener = new Downloader.DownloaderListener() {
        @Override
        public void onDownloaded(Downloader downloader) {
            DownloaderStatus status = get(downloader);
            if (status != null) {
                status.done();
                if (listener != null) listener.onDownloaded(status.hash, downloader);
            }

            if (listener != null)
            {
                boolean downloaded = true;
                ArrayList<String> canceledHashes = new ArrayList<>();

                for (Map.Entry<String, DownloaderStatus> entry: downloaders.entrySet()
                        ) {
                    status = entry.getValue();

                    if (status.status == STATUS.CANCELED) {
                        canceledHashes.add(entry.getKey());
                        continue;
                    } else if (status.status != STATUS.DONE) {
                        downloaded = false;
                        break;
                    }
                }

                if (downloaded) listener.onQueueDownloaded(DownloadQueue.this, canceledHashes);
            }
        }

        @Override
        public void onDownloadProgress(Downloader downloader, long loaded, long total) {
            DownloaderStatus status = get(downloader);
            if (status != null) {
                status.downloading(loaded, total);
                if (listener != null) listener.onDownloadProgress(status.hash, downloader, loaded, total);
            }

            if (listener != null)
            {
                // 计算总比例
                long _loaded = 0;
                long _total = 0;

                for (Map.Entry<String, DownloaderStatus> entry: downloaders.entrySet()
                        ) {
                    status = entry.getValue();
                    _loaded += status.loaded;
                    _total += status.total;
                }

                listener.onQueueProgress(DownloadQueue.this, _loaded, _total);
            }
        }

        @Override
        public void onDownloadCanceled(Downloader downloader) {
            DownloaderStatus status = get(downloader);
            if (status != null) {
                status.cancel();
                if (listener != null) listener.onDownloadCanceled(status.hash, downloader);
            }

        }

        @Override
        public void onDownloadError(Downloader downloader, Exception e) {
            DownloaderStatus status = get(downloader);
            if (status != null) {
                status.cancel();
                if (listener != null)
                    listener.onDownloadError(status.hash, downloader, e);
            }

        }
    };

    public interface QueueListener {
        void onQueueDownloaded(DownloadQueue downloadQueue, ArrayList<String> canceledHashes);
        void onQueueProgress(DownloadQueue downloadQueue, long loaded, long total);
        void onDownloaded(String hash, Downloader downloader);
        void onDownloadProgress(String hash, Downloader downloader, long loaded, long total);
        void onDownloadCanceled(String hash, Downloader downloader);
        void onDownloadError(String hash, Downloader downloader, Exception e);
    }

    private class DownloaderStatus {

        public String hash;
        public Downloader downloader;
        public STATUS status = STATUS.UNKNOWN;
        public long total = 0;
        public long loaded = 0;

        public DownloaderStatus(String hash, Downloader downloader)
        {
            this.hash = hash;
            this.downloader = downloader;
        }

        public DownloaderStatus download() throws Exception
        {
            if (status == STATUS.UNKNOWN)
            {
                status = STATUS.DOWNLOADING;
                downloader.download();
            }
            return this;
        }

        public DownloaderStatus cancel()
        {
            if (status == STATUS.DOWNLOADING)
                downloader.cancel();

            status = STATUS.CANCELED;
            loaded = total = 0;
            return this;
        }

        public DownloaderStatus done()
        {
            status = STATUS.DONE;
            loaded = total;
            return this;
        }

        public DownloaderStatus downloading(long loaded, long total)
        {
            status = STATUS.DOWNLOADING;
            this.loaded = loaded;
            this.total = total;
            return this;
        }
    }

}
