package com.yougy.common.media;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class ReaderFile {

    public static String readerJsonFile(String filePath) {
        String result = "";
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            result = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
