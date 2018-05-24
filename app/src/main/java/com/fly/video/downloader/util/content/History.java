package com.fly.video.downloader.util.content;

import com.fly.video.downloader.App;
import com.fly.video.downloader.core.io.Storage;
import com.fly.video.downloader.util.model.Video;

import java.io.File;
import java.util.ArrayList;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class History {
    private static final String filename = "history.txt";

    private static File settingsFile;

    static {
        settingsFile = Storage.getFileDir(App.getAppContext(), "settings/" + filename);
        try{
            if (!settingsFile.exists())
            {
                settingsFile.getParentFile().mkdirs();
                settingsFile.createNewFile();
            }
        } catch (Exception e) {}
    }


    public static ArrayList<Video> get(int page, int size)
    {
        try (
                Source fileSource = Okio.source(settingsFile);
                BufferedSource bufferedSource = Okio.buffer(fileSource);
        ) {
            ArrayList<Video> videos = new ArrayList<>();

            int offset = (page - 1) * size;
            int i = 0;
            while(i < offset && !bufferedSource.exhausted())
            {
                bufferedSource.readUtf8Line();
                i++;
            }
            i = 0;
            while (i < size && !bufferedSource.exhausted())
            {
                String line = bufferedSource.readUtf8Line();
                Video video = parseToVideo(line);
                if (video != null)
                    videos.add(video);
            }
            return videos;
        } catch (Exception e) {}

        return null;
    }

    public static <T extends Video> void put(T video)
    {
        try (
                Sink sink = Okio.sink(settingsFile);
                BufferedSink bufferedSink = Okio.buffer(sink);
        ) {
            bufferedSink.writeUtf8(video.getClass().getName());
            bufferedSink.writeUtf8(";");
            bufferedSink.writeUtf8(video.toJson());
            bufferedSink.writeUtf8("\n");
        } catch (Exception e) {

        }
    }

    private static <T extends Video> T parseToVideo(String json)
    {
        try
        {
            String clazzName = json.substring(0, json.indexOf(";"));
            Class<? extends Video> clazz = Class.forName(clazzName).asSubclass(Video.class);

            return (T)Video.fromJSON(clazz, json);
        } catch (Exception e){

        }
        return null;
    }

}
