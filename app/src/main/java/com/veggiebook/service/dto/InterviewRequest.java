package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/11/13
 * Time: 3:54 PM
 *
 */
public class InterviewRequest {
    private String bookType;
    private String bookId;
    private String language;
    private Double lat;
    private Double lon;
    private String profileId;

    public InterviewRequest(String bookType, String bookId, String language, Double lat, Double lon, String profileId) {
        this.bookType = bookType;
        this.bookId = bookId;
        this.language = language;
        this.lat = lat;
        this.lon = lon;
        this.profileId=profileId;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

}
