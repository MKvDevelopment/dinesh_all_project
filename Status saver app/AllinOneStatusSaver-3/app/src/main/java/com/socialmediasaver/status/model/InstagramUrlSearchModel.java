package com.socialmediasaver.status.model;

import java.util.List;

public class InstagramUrlSearchModel {
    private String video_url,display_url;
    private  boolean is_video;
    private EdgeSidecarToChildren edge_sidecar_to_children;

    public EdgeSidecarToChildren getEdge_sidecar_to_children() {
        return edge_sidecar_to_children;
    }

    private List<DisplayResource> display_resources;

    public List<DisplayResource> getDisplay_resources() {
        return display_resources;
    }

    public String getDisplay_url() {
        return display_url;
    }

    public boolean isIs_video() {
        return is_video;
    }

    public String getVideo_url() {
        return video_url;
    }



}
