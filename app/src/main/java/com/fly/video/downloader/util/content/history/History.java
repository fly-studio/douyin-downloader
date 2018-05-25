package com.fly.video.downloader.util.content.history;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fly.video.downloader.App;
import com.fly.video.downloader.util.database.HistoryDBHelper;
import com.fly.video.downloader.util.model.Video;

import java.util.ArrayList;
import java.util.Date;

public class History {

    private static HistoryDBHelper dbHelper = new HistoryDBHelper(App.getAppContext());

    public static ArrayList<Video> get(int page, int size)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM history ORDER BY created_at DESC LIMIT ?, ?", new String[]{((page - 1) * size) + "", size + ""});
        ArrayList<Video> videos = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Video video = parseToVideo(cursor.getString(cursor.getColumnIndex("className")), cursor.getString(cursor.getColumnIndex("json")));
            if (video != null)
                videos.add(video);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        dbHelper.close();
        return videos;
    }

    public static <T extends Video> void put(T video)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM history WHERE vid = ? ", new String[]{video.getId()});
        ContentValues values = new ContentValues();
        values.put("vid", video.getId());
        values.put("className", video.getClass().getName());
        values.put("json", video.toJson());
        values.put("created_at", new Date().getTime() / 1000);

        if (cursor.moveToFirst()) // 有， 则更新
        {
            int id = cursor.getInt(0);
            dbHelper.getWritableDatabase().update("history", values, "id = ?", new String[]{id + ""});
        } else {
            dbHelper.getWritableDatabase().insert("history", null, values);
        }
        cursor.close();
        db.close();
    }

    private static <T extends Video> T parseToVideo(String className, String json)
    {
        try
        {
            Class<? extends Video> clazz = Class.forName(className).asSubclass(Video.class);

            return (T)Video.fromJson(clazz, json);
        } catch (Exception e){

        }
        return null;
    }

}
