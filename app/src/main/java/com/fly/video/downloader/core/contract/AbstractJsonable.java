package com.fly.video.downloader.core.contract;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import java.io.IOException;
import java.util.Date;

import okio.BufferedSink;
import okio.BufferedSource;

public abstract class AbstractJsonable {

    public static <T> T fromJson(final Class<T> clazz, String json)
    {
        JsonAdapter<T> jsonAdapter = makeAdapter(clazz);
        try {
            return jsonAdapter.fromJson(json);
        } catch (Exception e) {

        }
        return null;
    }

    public static <T> T fromJson(final Class<T> clazz, BufferedSource source)
    {
        JsonAdapter<T> jsonAdapter = makeAdapter(clazz);
        try {
            return jsonAdapter.fromJson(source);
        } catch (Exception e) {

        }
        return null;
    }

    private static <T> JsonAdapter<T> makeAdapter(final Class<T> clazz)
    {
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
                .build();
        return moshi.adapter(clazz);
    }

    public String toJson()
    {
        JsonAdapter jsonAdapter = makeAdapter(this.getClass());
        return jsonAdapter.toJson(this);
    }

    public void toJson(BufferedSink sink) throws IOException
    {
        JsonAdapter jsonAdapter = makeAdapter(this.getClass());
        jsonAdapter.toJson(sink,this);
    }

}
