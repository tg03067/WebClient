package com.green.webclienttest.picsum.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class PicSumGetRes {
    private String id;
    private String author;
    private String downloadUrl;

    @JsonProperty(value = "downloadUrl")
    public String getDownloadUrl() {
        return downloadUrl;
    }

    @JsonProperty(value = "download_url")
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}
