package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/19/13
 * Time: 2:35 PM
 *
 */
public class SelectableResponse {
    private String title_en;
    private Integer id;
    private String title_es;
    private String url_es;
    private String url_en;
    private String photoUrl;
    private String photoUrl_es;
    private String shareUrl_en;
    private String shareUrl_es;
    private String coverPhotoId;
    private String coverPhotoId_es;


    public String getTitle_en() {
        return title_en;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle_es() {
        return title_es;
    }

    public String getUrl_es() {
        return url_es;
    }

    public String getUrl_en() {
        return url_en;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getShareUrl_en() {
        return shareUrl_en;
    }

    public String getShareUrl_es() {
        return shareUrl_es;
    }

    public String getCoverPhotoId() {
        return coverPhotoId;
    }

    public String getPhotoUrl_es() {
        return photoUrl_es;
    }

    public String getCoverPhotoId_es() {
        return coverPhotoId_es;
    }
}
