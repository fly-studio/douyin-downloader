package com.example.administrator.myapplication.share;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.administrator.myapplication.network.Http;
import com.example.administrator.myapplication.share.app.Douyin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/5/4.
 */

public class Downloader extends AsyncTask<String[], Integer, String> {
    private Context context;

    public Downloader(Context context)
    {
        this.context = context;
    }

    @Override
    protected String doInBackground(String[] ...params) {
        String[] urls = params[0];
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String path = "/mnt/shared/App/" + df.format(new Date()) + ".mp4";

        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdir();
            try {
                //创建文件
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        Http.sendGetAndSaveFile(urls[0], null, path);

        return path;
    }

    @Override
    protected void onPostExecute(String path) {

        Toast.makeText(this.context, "文件已经保存到:" + path, Toast.LENGTH_LONG).show();

        super.onPostExecute(path);
    }
}
