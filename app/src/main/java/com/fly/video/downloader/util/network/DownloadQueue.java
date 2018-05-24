package com.fly.video.downloader.util.network;

import com.fly.video.downloader.util.io.FileStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DownloadQueue {

    protected HashMap<String, Downloader> downloaders = new HashMap<>();
    protected QueueListener listener = null;

    public DownloadQueue setQueueListener(QueueListener listener)
    {
        this.listener = listener;
        return this;
    }

    public ArrayList<Downloader> getDownloaders()
    {
        ArrayList<Downloader> result = new ArrayList<>();

        for (Map.Entry<String, Downloader> entry : downloaders.entrySet()
             ) {
            result.add(entry.getValue());
        }
        return result;
    }

    public String getHash(Downloader downloader)
    {
        for (Map.Entry<String, Downloader> entry : downloaders.entrySet())
        {
            if (entry.getValue() == downloader)
                return entry.getKey();
        }
        return null;
    }

    public Downloader get(String hash)
    {
        return downloaders.get(hash);
    }


    public DownloadQueue cancel(String ...hashes)
    {
        for (int i = 0; i < hashes.length; i++) {
            if (downloaders.containsKey(hashes[i]))
                downloaders.get(hashes[i]).cancel();
        }
        return this;
    }

    public DownloadQueue cancelAll()
    {
        for (Map.Entry<String, Downloader> entry : downloaders.entrySet()
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

    public DownloadQueue add(String hash, Downloader downloader)
    {
        downloaders.put(hash, downloader);
        return this;
    }

    public DownloadQueue remove(String ...hashes)
    {
        for (int i = 0; i < hashes.length; i++) {
            cancel(hashes[i]);
            downloaders.remove(hashes[i]);
        }

        return this;
    }

    public DownloadQueue remove(Downloader ...downloaders1)
    {
        for (int i = 0; i < downloaders1.length; i++)
            remove(getHash(downloaders1[i]));

        return this;
    }

    public DownloadQueue start() throws Exception
    {
        for (Map.Entry<String, Downloader> entry : downloaders.entrySet()
             ) {
            Downloader downloader = entry.getValue();
            downloader.setDownloadListener(mDownloaderListener);

            FileStorage file = downloader.getFile();
            if (file != null && file.exists())
                mDownloaderListener.onDownloaded(downloader);

            downloader.start();
        }
        return this;
    }

    private Downloader.DownloaderListener mDownloaderListener = new Downloader.DownloaderListener() {
        @Override
        public void onDownloaded(Downloader downloader) {

            if (listener != null)
            {
                listener.onDownloaded(getHash(downloader), downloader);

                boolean downloaded = true;
                ArrayList<String> accidentHashes = new ArrayList<>();

                for (Map.Entry<String, Downloader> entry: downloaders.entrySet()
                        ) {
                    downloader = entry.getValue();

                    if (downloader.isCanceled() || downloader.isError()) {
                        accidentHashes.add(entry.getKey());
                        continue;
                    } else if (!downloader.isDownloaded()) {
                        downloaded = false;
                        break;
                    }
                }

                if (downloaded) listener.onQueueDownloaded(DownloadQueue.this, accidentHashes);
            }
        }

        @Override
        public void onDownloadProgress(Downloader downloader, long loaded, long total) {

            if (listener != null)
            {
                listener.onDownloadProgress(getHash(downloader), downloader, loaded, total);
                // 计算总比例
                long _loaded = 0;
                long _total = 0;

                for (Map.Entry<String, Downloader> entry: downloaders.entrySet()
                        ) {
                    downloader = entry.getValue();
                    _loaded += downloader.getLoaded();
                    _total += downloader.getTotal();
                }

                listener.onQueueProgress(DownloadQueue.this, _loaded, _total);
            }
        }

        @Override
        public void onDownloadCanceled(Downloader downloader) {

            if (listener != null) listener.onDownloadCanceled(getHash(downloader), downloader);
        }

        @Override
        public void onDownloadError(Downloader downloader, Exception e) {

            if (listener != null) listener.onDownloadError(getHash(downloader), downloader, e);
        }
    };

    public interface QueueListener {
        void onQueueDownloaded(DownloadQueue downloadQueue, ArrayList<String> accidentHashes);
        void onQueueProgress(DownloadQueue downloadQueue, long loaded, long total);
        void onDownloaded(String hash, Downloader downloader);
        void onDownloadProgress(String hash, Downloader downloader, long loaded, long total);
        void onDownloadCanceled(String hash, Downloader downloader);
        void onDownloadError(String hash, Downloader downloader, Exception e);
    }

}
