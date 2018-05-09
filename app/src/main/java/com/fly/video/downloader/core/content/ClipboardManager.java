package com.fly.video.downloader.core.content;

import android.content.ClipData;
import android.content.Context;

public class ClipboardManager {
    protected Context context;
    protected android.content.ClipboardManager clipBoard;

    public ClipboardManager(Context context)
    {
        clipBoard = (android.content.ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public android.content.ClipboardManager getClipBoard() {
        return clipBoard;
    }

    public ClipData getClipData()
    {
        return clipBoard.getPrimaryClip();
    }

    public String getText(int index)
    {
        ClipData clipData = getClipData();
        if (clipData == null)
            return null;
        ClipData.Item clipItem = clipData.getItemAt(index);

        return clipItem.getText().toString();
    }
}
