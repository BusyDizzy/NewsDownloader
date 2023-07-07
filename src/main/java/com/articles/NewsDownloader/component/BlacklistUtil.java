package com.articles.NewsDownloader.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@CacheConfig(cacheNames = "blacklistedWords")
public class BlacklistUtil {
    @Cacheable
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