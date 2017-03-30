package com.phy.demofrovoice.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 储存卡工具类
 */

public class SDCardUtil {
    private static String TAG = "SDCardUtil";
    public static final String APK_FILE_NAME = "shanghuizhijia.apk";
    /**
     * 判断SD卡是否存在
     * @return
     */
    public static boolean isSDCardMounted() {

        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /*
    * 获取sdcard绝对物理路径
    */

    public static String getSDCardPath() {

        if (isSDCardMounted()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();

        } else {
            return null;
        }
    }
    /*
    * 获取sdcard的剩余的可用空间的大小。返回MB字节
    */
    public static long getSDCardFreeSize() {

        if (isSDCardMounted()) {

            StatFs fs = new StatFs(getSDCardPath());
            long size = fs.getBlockSize();
            long count = fs.getAvailableBlocks();
            return size * count / 1024 / 1024;
        }
        return 0;
    }

    /*
    * 将文件（byte[]）保存进sdcard指定的路径下
    */

    public static boolean saveFileToSDCard(byte[] data, String dir, String filename) {

        BufferedOutputStream bos = null;

        if (isSDCardMounted()) {

            Log.i(TAG, "==isSDCardMounted==TRUE");

            File file = new File(getSDCardPath() + File.separator + dir);

            Log.i(TAG, "==file:" + file.toString() + file.exists());

            if (!file.exists()) {
                boolean flags = file.mkdirs();
                Log.i(TAG, "==创建文件夹:" + flags);
            }
            try {
                bos = new BufferedOutputStream(new FileOutputStream(new File(
                        file, filename)));
                bos.write(data, 0, data.length);

                bos.flush();

                return true;

            } catch (Exception e) {

                e.printStackTrace();

            } finally {

                try {
                    bos.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }
        }
        return false;

    }

}
