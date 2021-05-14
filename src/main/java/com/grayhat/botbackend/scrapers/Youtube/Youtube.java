package com.grayhat.botbackend.scrapers.Youtube;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.VideoDetails;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.grayhat.botbackend.models.YoutubeResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Youtube {

    private final String HOME = "https://www.youtube.com/";
    private final String VIDEO_ID_REGEX = "\\\"videoId\\\":\\\"(.{11})\\\"";

    // init downloader
    private YoutubeDownloader downloader;


    private String currentUrl = null;
    private Document page = null;
    private long startTime;

    public Youtube() {
        this.startTime = System.nanoTime();
        this.currentUrl = HOME;
        // init downloader
        this.downloader = new YoutubeDownloader();
        // downloader configurations
        this.downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
        this.downloader.setParserRetryOnFailure(0);
    }

    public Youtube(String searchTerm) {
        this();
        this.currentUrl = "https://www.youtube.com/results?search_query="+URLEncoder.encode(searchTerm, StandardCharsets.UTF_8);
    }

    private void fetch(String url) throws IOException {
        this.page = Jsoup.connect(url).get();   
    }

    private Set<String> scrape() {
        if(this.page == null) return null;
        Set<String> videoIds = new HashSet<>();
        String pageBody = this.page.outerHtml();
        Pattern pattern = Pattern.compile(VIDEO_ID_REGEX, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pageBody);
        while(matcher.find()) {
            String id = matcher.group(1);
            if (id != null) {
                videoIds.add(id);
            }
        }
        return videoIds;
    }

    private VideoData getMetadata(String videoId) throws YoutubeException {
        YoutubeVideo video = downloader.getVideo(videoId);
        VideoDetails details = video.details();
        if (details.isLive()) return null;
        VideoData data = new VideoData();
        data.author = details.author();
        data.averageRating = details.averageRating();
        data.isLive = details.isLive();
        data.isLiveContent = details.isLiveContent();
        data.keywords = details.keywords();
        data.lengthSeconds = details.lengthSeconds();
        data.liveUrl = details.liveUrl();
        data.shortDescription = details.description();
        data.thumbnails = details.thumbnails();
        data.title = details.title();
        data.videoId = details.videoId();
        data.viewCount = details.viewCount();
        return data;
    }

    public YoutubeResponse getResponse(int maxItems) {
        YoutubeResponse response = new YoutubeResponse();
        List<VideoData> datas = new LinkedList<>();
        try {
            this.fetch(this.currentUrl);
        } catch (IOException e) {
            response.videos = new LinkedList<>();
            response.message = e.getMessage();
            response.results = 0;
            response.timeTaken = System.nanoTime() - startTime;
            e.printStackTrace();
            return response;
        }
        Set<String> videoIds = this.scrape();
        for(String id: videoIds) {
            if (maxItems > 0) {
                if (datas.size() >= maxItems) break;
            }
            try {
                VideoData data = this.getMetadata(id);
                int ind = datas.indexOf(data);
                if (ind == -1) {
                    datas.add(data);
                }
            } catch (YoutubeException e) {
                e.printStackTrace();
            }
        }
        response.videos = datas;
        response.message = "success";
        response.results = datas.size();
        response.timeTaken = System.nanoTime() - startTime;
        return response;
    }

}