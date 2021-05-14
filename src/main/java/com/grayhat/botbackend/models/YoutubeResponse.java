package com.grayhat.botbackend.models;

import java.util.List;

import com.grayhat.botbackend.scrapers.Youtube.VideoData;

public class YoutubeResponse {
    public List<VideoData> videos;
    public int results;
    public String message;
    public long timeTaken;
}