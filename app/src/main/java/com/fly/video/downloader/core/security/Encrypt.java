package com.fly.video.downloader.core.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encrypt {

    public static String MD5(String plaintext)
    {
        return MD5(plaintext.getBytes());
    }

    public static String MD5(byte[] bytes)
    {
        byte[] resBytes = MD5Byte(bytes);
        if (resBytes == null) return null;

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < resBytes.length; ++i)
            sb.append(Integer.toHexString((resBytes[i] & 0xFF) | 0x100).substring(1,3));

        return sb.toString();
    }

    public static byte[] MD5Byte(String plaintext)
    {
        return MD5Byte(plaintext.getBytes());
    }

    public static byte[] MD5Byte(byte[] bytes)
    {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(bytes);
            return m.digest();
        } catch (NoSuchAlgorithmException e) {}

        return null;
    }
}
