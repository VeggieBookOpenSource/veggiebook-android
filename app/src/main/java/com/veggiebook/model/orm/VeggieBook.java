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

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.veggiebook.android.AppSettings;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/15/13
 * Time: 1:45 AM
 * Local store for a created VeggieBook
 */
@DatabaseTable
public class VeggieBook {
    public VeggieBook() {
    }

    @DatabaseField(id = true)
    String id;

    @DatabaseField(canBeNull=false, foreign=true)
    private BookInfo bookInfo;

    @DatabaseField(canBeNull=true)
    private String pantryId;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Selectable> selectables;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Attribute> attributes;

    @DatabaseField(canBeNull = true, foreign = true)
    private ContentUrl tipPage;

    @DatabaseField(canBeNull = true, foreign = true)
    private ContentUrl coverUrl;


    @DatabaseField(canBeNull = true)
    private String serverUid;

    @DatabaseField(canBeNull = true)
    private String coverImageUrl;

    @DatabaseField(canBeNull = true)
    private String coverImageId;

    @DatabaseField(canBeNull = false)
    private  String profileId;

    @DatabaseField(version = true)
    Date modificationDate;

    public VeggieBook(BookInfo bookInfo) {
        String profileId = AppSettings.getProfileId();
        this.id = createId(bookInfo);
        this.bookInfo = bookInfo;
        this.profileId = profileId;

    }

    public BookInfo getBookInfo() {
        return bookInfo;
    }


    public String getPantryId() {
        return pantryId;
    }

    public void setPantryId(String pantryId) {
        this.pantryId = pantryId;
    }

    public ForeignCollection<Selectable> getSelectables() {
        return selectables;
    }

    public void setSelectables(ForeignCollection<Selectable> selectables) {
        this.selectables = selectables;
    }

    public ForeignCollection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ForeignCollection<Attribute> attributes) {
        this.attributes = attributes;
    }

    public ContentUrl getTipPage() {
        return tipPage;
    }

    public void setTipPage(ContentUrl tipPage) {
        this.tipPage = tipPage;
    }

    public String getServerUid() {
        return serverUid;
    }

    public void setServerUid(String serverUid) {
        this.serverUid = serverUid;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getId() {
        return id;
    }

    public boolean isCreated(){
        return serverUid != null;
    }

    public String getCoverImageId() {
        return coverImageId;
    }

    public void setCoverImageId(String coverImageId) {
        this.coverImageId = coverImageId;
    }

    public ContentUrl getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(ContentUrl coverUrl) {
        this.coverUrl = coverUrl;
    }

    public static String createId(BookInfo bookInfo){
        return createId(bookInfo.getId());
    }

    public static String createId(String bookInfoId){
        return String.format("%s/%s", bookInfoId, AppSettings.getProfileId());
    }
}
