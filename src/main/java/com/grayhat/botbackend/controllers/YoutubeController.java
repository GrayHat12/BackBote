package com.grayhat.botbackend.controllers;

import com.grayhat.botbackend.models.YoutubeRequest;
import com.grayhat.botbackend.models.YoutubeResponse;
import com.grayhat.botbackend.scrapers.Youtube.Youtube;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YoutubeController {
    @RequestMapping(
        value = "/youtube",
        method = RequestMethod.POST
    )
    public YoutubeResponse youtube(@RequestBody YoutubeRequest payload) throws Exception{
        Youtube youtube;
        if (payload.searchTerm != null && payload.searchTerm.length() > 0) {
            youtube = new Youtube(payload.searchTerm);
        }else {
            youtube = new Youtube();
        }
        return youtube.getResponse(payload.maxItems);
    }
}