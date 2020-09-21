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

package com.veggiebook.model.orm;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/15/13
 * Time: 1:56 AM
 *
 */
@DatabaseTable
public class SecretSelectable {
    public SecretSelectable() {
    }

    @DatabaseField(id = true)
    private String id;

    @DatabaseField(canBeNull = false, uniqueCombo = true)
    Integer secretId;

    @DatabaseField(canBeNull = false, uniqueCombo = true)
    String profileId;

    @DatabaseField(canBeNull = false, foreign = true)
    ContentUrl url;

    @DatabaseField(canBeNull = true, foreign = true)
    SecretBook secretBook;

    @DatabaseField(canBeNull = false, foreign = true)
    ContentUrl title;

    @DatabaseField(canBeNull = false)
    String imgUrl;

    @DatabaseField(canBeNull = false)
    Integer extras;

    @DatabaseField(canBeNull = false)
    Boolean selected;

    @DatabaseField(canBeNull = false, foreign = true)
    ContentUrl shareUrl;


    public SecretSelectable(Integer recipeId, String profileId, ContentUrl url, VeggieBook veggieBook, ContentUrl title, String imgUrl, ContentUrl shareUrl) {
        this.id = String.format("%d/%s", recipeId, profileId);
        this.secretId = secretId;
        this.profileId = profileId;
        this.url = url;
        this.secretBook = secretBook;
        this.title = title;
        this.imgUrl = imgUrl;
        this.selected = true;
        this.extras = 0;
        this.shareUrl=shareUrl;
    }

    public ContentUrl getUrl() {
        return url;
    }

    public void setUrl(ContentUrl url) {
        this.url = url;
    }

    public SecretBook getsecretBook() {
        return secretBook;
    }

    public void setsecretBook(SecretBook secretBook) {
        this.secretBook = secretBook;
    }


    public ContentUrl getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public Integer getExtras() {
        return extras;
    }

    public void setShareUrl(ContentUrl shareUrl) {
        this.shareUrl = shareUrl;
    }

    public ContentUrl getShareUrl() {
        return shareUrl;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setExtras(Integer extras) {
        this.extras = extras;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Integer getsecretId() {
        return secretId;
    }

    public String getId() {
        return id;
    }
}
