package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 8/1/13
 * Time: 9:07 AM
 *
 */
public class PrintRequest {
    private String language;
    private String id;
    private String pantry;
    private String type;

    public PrintRequest(String language, String id, String pantry, String type) {
        this.language = language;
        this.id = id;
        this.pantry = pantry;
        this.type = type;
    }
}
