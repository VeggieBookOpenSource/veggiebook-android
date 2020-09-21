package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/12/13
 * Time: 11:22 AM
 *
 */
public class AvailablePhotosResponse {
    private String id;
    private String url;
    private Boolean mine;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getMine() {
        return mine;
    }
}
