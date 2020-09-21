package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/12/13
 * Time: 11:21 AM
 *
 */
public class AvailablePhotosRequest {
    private String profileId;
    private String bookId;

    public AvailablePhotosRequest(String profileId, String bookId) {
        this.profileId = profileId;
        this.bookId = bookId;
    }


}
