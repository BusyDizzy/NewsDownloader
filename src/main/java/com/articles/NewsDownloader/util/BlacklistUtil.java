package com.articles.NewsDownloader.util;

import lombok.experimental.UtilityClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class BlacklistUtil {

    public List<String> loadBlacklistedWords(String filePath) throws IOException {
        List<String> blacklistedWords = new ArrayList<>();
        try (InputStream inputStream = BlacklistUtil.class.getClassLoader().getResourceAsStream(filePath);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                blacklistedWords.addAll(Arrays.asList(line.split(" ")));
            }
        }
        return blacklistedWords;
    }
}