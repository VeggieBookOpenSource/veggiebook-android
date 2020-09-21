//  Copyright © 2020 Quick Help For Meals, LLC. All rights reserved.
//
//  This file is part of VeggieBook.
//
//  VeggieBook is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, version 3 of the license only.
//
//  VeggieBook is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or fitness for a particular purpose. See the
//  GNU General Public License for more details.

package com.veggiebook.model;

import java.util.Locale;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/6/13
 * Time: 12:02 PM
 *  An available book is a representation of book information for display.
 */
public class AvailableBook {
    //The title of the book
    //each entry in the map represents a language
    private Map<String,String> title;
    //small image of the book
    private String url;
    //large image of the book
    private String largeUrl;
    private String bookType;
    private String bookId;
    private boolean hasSelectables;
    private int pIndex;
    //url to display if the book is loading
    //each key represents a language, each entry, the url string
    private Map<String, String> loadingUrl;

    public AvailableBook(Map<String,String> title,
                         String url,
                         String largeUrl,
                         String bookType,
                         String bookId,
                         boolean hasSelectables,
                         Map<String,String> loadingUrl,
                         int pIndex) {
        this.title = title;
        this.url = url;
        this.largeUrl = largeUrl;
        this.bookType = bookType;
        this.bookId = bookId;
        this.hasSelectables=hasSelectables;
        this.loadingUrl=loadingUrl;
        this.pIndex = pIndex;
    }



    public String getTitle() {
        String language = Locale.getDefault().getLanguage();
        if(!title.containsKey(language))
            language = "en";
        return title.get(language);
    }

    public String getUrl() {
        return url;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public String getBookType() {
        return bookType;
    }

    public String getBookId() {
        return bookId;
    }

    public boolean hasSelectables() {
        return hasSelectables;
    }

    public String getLoadingUrl() {
        String language = Locale.getDefault().getLanguage();
        if(!loadingUrl.containsKey(language))
            language = "en";
        return loadingUrl.get(language);
    }

    public int getpIndex() {
        return pIndex;
    }

    public void setpIndex(int pIndex) {
        this.pIndex = pIndex;
    }
}
