package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/15/13
 * Time: 4:09 AM
 *
 */
public class CreateBookResponse {
    private String id;
    private String tipsUrl_en;
    private String tipsUrl_es;
    private String coverUrl_en;
    private String coverUrl_es;

    public String getId() {
        return id;
    }

    public String getTipUrl_en() {
        return tipsUrl_en;
    }

    public String getTipUrl_es() {
        return tipsUrl_es;
    }

    public String getCoverUrl_en() {
        return coverUrl_en;
    }

    public String getCoverUrl_es() {
        return coverUrl_es;
    }
}
