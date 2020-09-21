package com.veggiebook.service.dto;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/19/13
 * Time: 2:35 PM
 *
 */
public class SelectablesRequest {
    private String bookType;
    private String bookId;
    private String[] attributes;

    public SelectablesRequest(String bookType, String bookId, List<String> attributes) {
        this.bookType = bookType;
        this.bookId = bookId;

        this.attributes = attributes.toArray(new String[attributes.size()]);
    }

    public String getBookType() {
        return bookType;
    }

    public String getBookId() {
        return bookId;
    }


    public String[] getAttributes() {
        return attributes;
    }

}
