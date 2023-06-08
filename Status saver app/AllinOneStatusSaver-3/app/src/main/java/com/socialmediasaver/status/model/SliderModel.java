package com.socialmediasaver.status.model;

public class SliderModel {

    private String slider,uri_link,id;

    public SliderModel(String slider, String uri_link, String id) {
        this.slider = slider;
        this.uri_link = uri_link;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri_link() {
        return uri_link;
    }

    public void setUri_link(String uri_link) {
        this.uri_link = uri_link;
    }

    public SliderModel() {
    }

    public String getSlider() {
        return slider;
    }

    public void setSlider(String slider) {
        this.slider = slider;
    }
}
