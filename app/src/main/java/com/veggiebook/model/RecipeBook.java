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

import com.veggiebook.model.orm.BookInfo;
import com.veggiebook.model.orm.ContentUrl;
import com.veggiebook.model.orm.DatabaseHelper;
import com.veggiebook.model.orm.ImageSize;
import com.veggiebook.model.orm.Selectable;
import com.veggiebook.model.orm.TextItem;
import com.veggiebook.model.orm.Translation;
import com.veggiebook.model.orm.UrlTranslation;
import com.veggiebook.model.orm.VeggieBook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecipeBook {
    public static Logger log = LoggerFactory.getLogger(RecipeBook.class);
    private String title;
    private String veggieImgUrl;
    private List<Recipe> recipes;
    private String coverImageUrl;
    private String tipsUrl;
    private String coverUrl;
    private String veggieBookId;
    private String availableBookId;
    private String serverUid;
    private String bookInfoId;


    public RecipeBook(String bookId) throws SQLException {
        DatabaseHelper db = DatabaseHelper.getDatabaseHelper();

        VeggieBook veggieBook = db.getVeggieBookDao().queryForId(VeggieBook.createId(bookId));
        veggieBookId = veggieBook.getId();
        BookInfo bookInfo = db.getBookInfoDao().queryForId(veggieBook.getBookInfo().getId());
        bookInfoId = bookInfo.getId();
        TextItem textItem = bookInfo.getTitle();
        availableBookId = veggieBook.getBookInfo().getId();
        serverUid = veggieBook.getServerUid();

        coverImageUrl = veggieBook.getCoverImageUrl();


        BookManager bookManager = BookManager.getBookManager();

        List<Translation> translations = db.getTranslationDao()
                .queryBuilder()
                .where()
                .eq("textItem_id", textItem.getId())
                .and()
                .eq("language_id", bookManager.getLanguage())
                .query();

        Translation translation = translations != null ? translations.get(0) : null;

        if (translation == null) {
            title = "";
        } else {
            title = translation.getTextString();
        }

        log.debug("title: {}", title);

        Integer imageId;
        if(bookInfo.getImage()!=null){
            imageId = bookInfo.getImage().getId();

            List<ImageSize> imageSizes = db.getImageSizeDao().queryBuilder().where().eq("image_id", imageId).and().eq("resolution_id", "img500").query();
            ImageSize imageSize = imageSizes==null?null:imageSizes.get(0);

            if (imageSize != null) {
                veggieImgUrl = imageSize.getUrl();
            }
        }
        else {
            veggieImgUrl = null;
        }
        log.debug("veggieImage: {}", veggieImgUrl);

        //Get the translated coverUrl

        List<UrlTranslation> urlTranslations = db.getUrlTranslationDao().queryBuilder().where()
                .eq("contentUrl_id",veggieBook.getCoverUrl().getId()).and().eq("language_id", bookManager.getLanguage())
                .query();
        UrlTranslation urlTranslation = urlTranslations==null||urlTranslations.size()==0?null:urlTranslations.get(0);
        coverUrl = urlTranslation==null?"":urlTranslation.getTextString();

        recipes = new ArrayList<Recipe>();

        for(Selectable s : db.getSelectablesDao().queryBuilder().orderBy("id", true).where().eq("veggieBook_id", veggieBook.getId()).query()){
            if(s.getSelected()){
                ContentUrl titleContent = s.getTitle();
                ContentUrl contentUrl = s.getUrl();
                ContentUrl shareContentUrl = s.getShareUrl();

                urlTranslations = db.getUrlTranslationDao().queryBuilder().where()
                        .eq("contentUrl_id", titleContent.getId()).and().eq("language_id", bookManager.getLanguage())
                        .query();
                urlTranslation = urlTranslations==null?null:urlTranslations.get(0);
                String recipeTitle = null;
                if(urlTranslation == null){
                    recipeTitle = "";

                }
                else{
                    recipeTitle = urlTranslation.getTextString();
                }

                urlTranslations = db.getUrlTranslationDao().queryBuilder().where()
                        .eq("contentUrl_id", contentUrl.getId()).and().eq("language_id", bookManager.getLanguage())
                        .query();

                urlTranslation = urlTranslations==null?null:urlTranslations.get(0);
                String recipeUrl = null;
                if(urlTranslation == null){
                    recipeUrl = "";

                }
                else{
                    recipeUrl = urlTranslation.getTextString();
                }

                urlTranslations = db.getUrlTranslationDao().queryBuilder().where()
                        .eq("contentUrl_id", shareContentUrl.getId()).and().eq("language_id", bookManager.getLanguage())
                        .query();

                urlTranslation = urlTranslations==null?null:urlTranslations.get(0);
                String shareUrl = null;
                if(urlTranslation == null){
                    shareUrl = "";

                }
                else{
                    shareUrl = urlTranslation.getTextString();
                }

                Recipe recipe = new Recipe(String.format("%d", Math.abs(s.getRecipeId())), s.getImgUrl(),recipeTitle,recipeUrl, shareUrl);
                recipes.add(recipe);
            }

        }

        //tips Url
        ContentUrl tipsContentUrl = veggieBook.getTipPage();
        urlTranslations = db.getUrlTranslationDao().queryBuilder().where()
                .eq("contentUrl_id", tipsContentUrl.getId()).and().eq("language_id", bookManager.getLanguage())
                .query();
        urlTranslation = urlTranslations==null?null:urlTranslations.get(0);
        if(urlTranslation == null){
            tipsUrl = "";

        }
        else{
            tipsUrl = urlTranslation.getTextString();
        }

    }


    public String getTitle() {
        return title;
    }

    public String getVeggieImgUrl() {
        if(veggieImgUrl != null)
            return veggieImgUrl;
        else
            return coverImageUrl;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getTipsUrl() {
        return tipsUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getVeggieBookId() {
        return veggieBookId;
    }

    public String getAvailableBookId() {
        return availableBookId;
    }

    public String getServerUid() {
        return serverUid;
    }


    public static class Recipe {
        String photoUrl;
        String name;
        String url;
        String shareUrl;
        boolean hasImageUrl = true;
        int imageId;
        String id;


        public Recipe(String id, String photoUrl, String name, String url, String shareUrl) {
            this.photoUrl = photoUrl;
            this.name = name;
            this.url = url;
            this.shareUrl=shareUrl;
            this.hasImageUrl = true;
            this.id = id;

        }

        public Recipe(String id, int imageId, String name, String url, String shareUrl){
            this.hasImageUrl = false;
            this.imageId = imageId;
            this.name = name;
            this.url = url;
            this.shareUrl=shareUrl;
            this.id = id;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public boolean hasImageUrl(){
            return hasImageUrl;
        }

        public int getImageId(){
            return imageId;
        }

        public String getId(){
            return id;
        }

    }

    public String getBookInfoId() {
        return bookInfoId;
    }
}
