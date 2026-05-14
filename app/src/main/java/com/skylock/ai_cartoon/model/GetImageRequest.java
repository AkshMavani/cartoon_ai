package com.skylock.ai_cartoon.model;

import java.util.ArrayList;
import java.util.List;

public class GetImageRequest extends Representation {
    private List<ImageResponse> images = new ArrayList();

    public List<ImageResponse> getImages() {
        return this.images;
    }

    public void setImages(List<ImageResponse> list) {
        this.images = list;
    }
}

