package com.articles.NewsDownloader.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.Resource;

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

    public List<String> loadBlacklistedWords(Resource blacklistResource) throws IOException {
        List<String> blacklistedWords = new ArrayList<>();
        try (InputStream inputStream = blacklistResource.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                blacklistedWords.addAll(Arrays.asList(line.split(" ", -1)));
            }
        }
        return blacklistedWords;
    }
}