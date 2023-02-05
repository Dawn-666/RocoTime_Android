package com.rocoplus.rocotime;

import androidx.annotation.NonNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {
    @NonNull
    public static byte[] toMd5(byte[] bytes) {
        try {
            return MessageDigest.getInstance("md5").digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            return new byte[]{};
        }
    }
    @NonNull
    public static String toHex(@NonNull byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
