package cn.xu.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IOUtils {
    private static final String filePath = "data.txt";
    private static final String cutKeyAndValue = ":";
    private static final Map<String, Integer> data = new HashMap<>();

    public static String readData() {
        try {
            FileReader file = new FileReader(filePath);
            BufferedReader buffer = new BufferedReader(file);
            String line;
            while ((line = buffer.readLine()) != null) {
                String[] strings = line.split(cutKeyAndValue);
                if (strings.length < 2) {
                    continue;
                }
                String key = stringLostFrontAndEndEmpty(strings[0]);
                String valueStr = stringLostFrontAndEndEmpty(strings[1]);
                data.put(key, Integer.parseInt(valueStr));
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String stringLostFrontAndEndEmpty(String str) {
        char[] chars = str.toCharArray();
        int offset = 0;
        while (offset < str.length() && charIsEmpty(chars[offset])) {
            offset++;
        }
        int lastNotEmpty = str.length() - 1;
        while (lastNotEmpty >= 0 && charIsEmpty(chars[lastNotEmpty])) {
            lastNotEmpty--;
        }
        int length = lastNotEmpty - offset + 1;
        if (length <= 0) {
            return null;
        }
        return String.valueOf(chars, offset, length);
    }

    private static boolean charIsEmpty(char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }

    public static Integer get(String key) {
        return data.get(key);
    }
}
