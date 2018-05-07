package com.fly.video.downloader.share;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.fly.video.downloader.share.app.Douyin;

/**
 * Created by Administrator on 2018/5/4.
 */

public class Analyzer extends AsyncTask<String, Integer, String[]> {

    private Context context;

    public Analyzer(Context context)
    {
        this.context = context;
    }

    @Override
    protected String[] doInBackground(String ...params) {
        Douyin dy = new Douyin();
        String[] urls = dy.get(params[0]);
        return urls;
    }

    @Override
    protected void onPostExecute(String[] urls) {
        if (urls != null && urls.length > 0)
        {
            Toast.makeText(this.context, urls[0], Toast.LENGTH_LONG).show();
            Downloader downloader = new Downloader(this.context);
            downloader.execute(urls);
        }
        super.onPostExecute(urls);
    }
}
