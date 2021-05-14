package com.grayhat.botbackend.scrapers.Youtube;

import java.util.List;

public class VideoData {
    public List<String> keywords;
    public String shortDescription;
    public long viewCount;
    public int averageRating;
    public boolean isLiveContent;
    public String liveUrl;
    public String videoId;
    public int lengthSeconds;
    public List<String> thumbnails;
    public String title;
    public String author;
    public boolean isLive;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof VideoData) {
            VideoData ob = (VideoData)obj;
            return ob.videoId == this.videoId;
        }
        return super.equals(obj);
    }
}
