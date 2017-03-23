package com.yougy.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2016/9/26.
 */
public class IOUtils {

    public static void close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
