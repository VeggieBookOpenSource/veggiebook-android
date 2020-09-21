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

import android.content.Context;
import android.location.Location;

import com.j256.ormlite.dao.Dao;
import com.veggiebook.BuildConfig;
import com.veggiebook.android.AppSettings;
import com.veggiebook.model.orm.Attribute;
import com.veggiebook.model.orm.BookInfo;
import com.veggiebook.model.orm.BookType;
import com.veggiebook.model.orm.ContentUrl;
import com.veggiebook.model.orm.DatabaseHelper;
import com.veggiebook.model.orm.Image;
import com.veggiebook.model.orm.ImageSize;
import com.veggiebook.model.orm.ImageSizeCategory;
import com.veggiebook.model.orm.Language;
import com.veggiebook.model.orm.Selectable;
import com.veggiebook.model.orm.TextItem;
import com.veggiebook.model.orm.Translation;
import com.veggiebook.model.orm.UrlTranslation;
import com.veggiebook.model.orm.VeggieBook;
import com.veggiebook.service.dto.CreateBookRequest;
import com.veggiebook.service.dto.CreateBookResponse;
import com.veggiebook.service.dto.LibraryInfoResponse;
import com.veggiebook.service.rest.RestServiceFactory;
import com.veggiebook.service.rest.VBRestService;
import com.veggiebook.service.rest.http.VbHttpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.veggiebook.BuildConfig.WEB_SERVICES_HOST;

public class BookManager {
    protected VBRestService restService = null;
    private static Logger log = LoggerFactory.getLogger(BookManager.class);
    protected static BookManager bookManager = null;
    protected Map<String, List<AvailableBook>> availableBooks = new HashMap<String, List<AvailableBook>>();
    public static BookBuilder bookBuilder = null;


    public static BookManager getBookManager() {
        if (bookManager == null) {
            bookManager = new BookManager(RestServiceFactory.getRestService(WEB_SERVICES_HOST, "https"));
        }

        return bookManager;

    }

    protected BookManager(VBRestService restService) {
        this.restService = restService;
    }


    public synchronized List<AvailableBook> getAvailableBooks(Context context, String bookType) {
        if(availableBooks.containsKey(bookType)){
            return availableBooks.get(bookType);
        }
        try {
            loadAvailableBooks(bookType);
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return availableBooks.get(bookType);
    }

    private synchronized void loadAvailableBooks(String bookType) throws SQLException {
        Dao<BookInfo, String> bookInfos;
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        bookInfos = databaseHelper.getBookInfoDao();
        BookType bt = databaseHelper.getBookTypeDao().queryForId(bookType);


        availableBooks.put(bookType, new ArrayList<AvailableBook>((int) bookInfos.countOf()));
        Language language = getLanguage(databaseHelper, getLanguage());
        ImageSizeCategory imageSizeCategory = getImageSizeCategory(databaseHelper);


        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put("type_id", bt.getType());
        fields.put("deprecated", false);
        for (BookInfo book : bookInfos.queryForFieldValues(fields)) {
            fields.clear();
            fields.put("textItem_id", book.getTitle().getId());
            List<Translation> translations = databaseHelper.getTranslationDao().queryForFieldValues(fields);
            HashMap<String,String> titles = new HashMap<String, String>();
            for(Translation t : translations){
                titles.put(t.getLanguage().getLanguageCode(), t.getTextString());
            }
            fields.clear();
            fields.put("textItem_id", (-1 * book.getTitle().getId()));
            translations = databaseHelper.getTranslationDao().queryForFieldValues(fields);
            HashMap<String,String> loadUrls = new HashMap<String, String>();
            for(Translation t : translations){
                loadUrls.put(t.getLanguage().getLanguageCode(), t.getTextString());
            }


            ImageSize imageSize = null;
            ImageSize largestImageSize = null;

            fields.clear();
            fields.put("image_id", book.getImage().getId());
            fields.put("resolution_id", imageSizeCategory.getResolution());
            imageSize = databaseHelper.getImageSizeDao().queryForFieldValues(fields).get(0);
            fields.clear();
            fields.put("image_id", book.getImage().getId());
            fields.put("resolution_id", databaseHelper.getImageSizeCategoryDao().queryForId("img500").getResolution());

            largestImageSize = databaseHelper.getImageSizeDao().queryForFieldValues(fields).get(0);


            availableBooks.get(bookType).add(
                    new AvailableBook(
                            titles,
                            imageSize!=null ? imageSize.getUrl() : null,
                            largestImageSize !=null ? largestImageSize.getUrl() : null,
                            book.getType().getType(),
                            book.getId(),
                            book.hasSelectables(),
                            loadUrls,
                            book.getpIndex()));

        }

        Collections.sort(availableBooks.get(bookType), new AvailableBookComparator());

    }

    public AvailableBook getAvailableBook(String bookId) {

        if (availableBooks.isEmpty()) {
            return null;
        }
        for (List<AvailableBook> bookList : availableBooks.values())
            for (AvailableBook av : bookList) {
                if (av.getBookId().equals(bookId)) {
                    return av;
                }
            }

        return null;
    }


    public synchronized List<RecipeBook> getMyBooks() throws SQLException {
        if (AppSettings.getProfileId() == null) {
            return new ArrayList<RecipeBook>();
        }
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        List<VeggieBook> veggieBooks =
                databaseHelper.getVeggieBookDao().
                        queryBuilder().orderBy("modificationDate", false).
                        where().eq("profileId", AppSettings.getProfileId()).query();
        List<RecipeBook> result = new ArrayList <RecipeBook>(veggieBooks.size());

        for(VeggieBook vb : veggieBooks){
            if(vb.isCreated())
                result.add(new RecipeBook(vb.getBookInfo().getId()));
        }

        return result;
    }

    public synchronized void synchronizeWithServer() {
        try
            {
            //we must create the Database helper before checking the library version
            //because if onUpgrade was called the library version may have been reset
            DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();

            LibraryInfoResponse libraryInfoResponse = restService.getLibraryInfo();
            String previousVersion = AppSettings.getLibraryVersion();

            AppSettings.setLibraryVersion(libraryInfoResponse.getVersion());



            availableBooks.clear();
            synchronize(databaseHelper, libraryInfoResponse);

        } catch (VbHttpException e) {
            log.warn(e.getMessage(), e);
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
        }
    }


    protected Language getLanguage(DatabaseHelper databaseHelper, String langString) throws SQLException {
        Language language = databaseHelper.getLanguageDao().queryForId(langString);
        //Default to english if we can't find the language
        if (language == null)
            language = databaseHelper.getLanguageDao().queryForId("en");
        return language;
    }

    protected ImageSizeCategory getImageSizeCategory(DatabaseHelper databaseHelper) throws SQLException {
        return databaseHelper.getImageSizeCategoryDao().queryForId("img200");
    }

    protected void synchronize(DatabaseHelper databaseHelper, LibraryInfoResponse libraryInfoResponse) throws SQLException {

        //Image Size Categories
        Dao<ImageSizeCategory, String> imageSizeCategories = databaseHelper.getImageSizeCategoryDao();
        for (String imageResolution : libraryInfoResponse.getImageSizes()) {
            imageSizeCategories.createIfNotExists(new ImageSizeCategory(imageResolution));
        }

        //Languages
        Dao<Language, String> languages = databaseHelper.getLanguageDao();
        for (String languageCode : libraryInfoResponse.getLanguages()) {
            languages.createIfNotExists(new Language(languageCode));
        }

        //Book types
        Dao<BookType, String> bookTypes = databaseHelper.getBookTypeDao();
        for (String type : libraryInfoResponse.getBookTypes()) {
            bookTypes.createIfNotExists(new BookType(type));
        }

        //synchronize the book list
        synchronize(databaseHelper, libraryInfoResponse.getBooksAvailable());


    }

    protected void synchronize(DatabaseHelper databaseHelper, LibraryInfoResponse.AvailableBook book) throws SQLException {
        //Image Size Categories

        Dao<ImageSizeCategory, String> imageSizeCategories = databaseHelper.getImageSizeCategoryDao();
        ImageSizeCategory thumbnail = imageSizeCategories.queryForId("thumbnail");
        ImageSizeCategory img100 = imageSizeCategories.queryForId("img100");
        ImageSizeCategory img200 = imageSizeCategories.queryForId("img200");
        ImageSizeCategory img300 = imageSizeCategories.queryForId("img300");
        ImageSizeCategory img500 = imageSizeCategories.queryForId("img500");

        //Languages
        Dao<Language, String> languages = databaseHelper.getLanguageDao();
        Language en = languages.queryForId("en");
        Language es = languages.queryForId("es");

        //Book types
        Dao<BookType, String> bookTypes = databaseHelper.getBookTypeDao();

        //DAOs
        Dao<TextItem, Integer> textItems = databaseHelper.getTextItemDao();
        Dao<Translation, String> translations = databaseHelper.getTranslationDao();
        Dao<Image, Integer> images = databaseHelper.getImageDao();
        Dao<ImageSize, String> imageSizes = databaseHelper.getImageSizeDao();

        //title
        log.info(book.getTitle().getEn());
        TextItem titleTextItem = textItems.createIfNotExists(new TextItem(book.getTitle().getId()));
        //add the translations
        translations.createOrUpdate(new Translation(titleTextItem, en, book.getTitle().getEn()));
        translations.createOrUpdate(new Translation(titleTextItem, es, book.getTitle().getEs()));

        //translation for loading url
        TextItem loadingUrl = textItems.createIfNotExists(new TextItem((-1 * book.getTitle().getId())));
        translations.createOrUpdate(new Translation(loadingUrl, en, book.getLoadingUrl_en()));
        translations.createOrUpdate(new Translation(loadingUrl, es, book.getLoadingUrl_es()));


        //book type
        BookType bookType = bookTypes.queryForId(book.getType());


        Image image = null;
        if(book.getImage() != null){
            //Image
            image = images.createIfNotExists(new Image(book.getImage().getId()));
            //Sizes
            imageSizes.createOrUpdate(new ImageSize(image, book.getImage().getThumbnail(), thumbnail));
            imageSizes.createOrUpdate(new ImageSize(image, book.getImage().getImg100(), img100));
            imageSizes.createOrUpdate(new ImageSize(image, book.getImage().getImg200(), img200));
            imageSizes.createOrUpdate(new ImageSize(image, book.getImage().getImg300(), img300));
            imageSizes.createOrUpdate(new ImageSize(image, book.getImage().getImg500(), img500));
        }
        //Add the BookInfos
        Dao<BookInfo, String> bookInfos = databaseHelper.getBookInfoDao();
        BookInfo bookInfo = new BookInfo(book.getId(), bookType, titleTextItem, image, book.getHasSelectables(), loadingUrl);
        if(bookType.equals("SECRETS_BOOK")){
            bookInfo.setpIndex(book.getpIndex());
        }
        bookInfos.createOrUpdate(bookInfo);


    }

    protected void synchronize(DatabaseHelper databaseHelper, LibraryInfoResponse.AvailableBook[] books) throws SQLException {
        for (LibraryInfoResponse.AvailableBook book : books) {
            synchronize(databaseHelper, book);
        }
    }


    public class AvailableBookComparator implements Comparator<AvailableBook> {

        @Override
        public int compare(AvailableBook book1, AvailableBook book2) {
            if (book1.getBookType().equals(book2.getBookType()))
                if(book1.getBookType().equals("RECIPE_BOOK"))
                    return book1.getTitle().compareTo(book2.getTitle());
                else
                    return book1.getpIndex() - book2.getpIndex();

            return book1.getBookType().equals("RECIPE_BOOK") ? -1 : 1;

        }
    }

    public VBRestService getRestService() {
        return restService;
    }

    public String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if(!language.equals("es"))
            language = "en";
        return language;
    }


    public VeggieBook createVeggieBook(BookBuilder bookBuilder) throws VbHttpException, VbNotReadyException, SQLException {
        if (bookBuilder == null || !bookBuilder.isComplete()) {
            throw new VbNotReadyException("BookBuilder is not ready to create VeggieBook");
        }

        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();

        List<CreateBookRequest.Selectable> selectables =  bookBuilder.getSelectableList();

        Location lastKnownLocation = new Location("dummyprovider");
        lastKnownLocation.setLatitude(AppSettings.getLastLatitude());
        lastKnownLocation.setLongitude(AppSettings.getLastLongitude());

        CreateBookResponse response = getRestService().createVeggieBook(
                AppSettings.getProfileId(),
                bookBuilder.getAvailableBook().getBookId(),
                bookBuilder.getAvailableBook().getBookType(),
                bookBuilder.getAttributeList(),
                selectables,
                bookBuilder.getCoverPhoto().getId(),
                getLanguage(), bookBuilder.selectedPantryId,
                lastKnownLocation);

        Dao<BookInfo, String> bookInfos = databaseHelper.getBookInfoDao();
        BookInfo bi = bookInfos.queryForId(bookBuilder.getAvailableBook().getBookId());


        //Languages
        Dao<Language, String> languages = databaseHelper.getLanguageDao();
        Language en = languages.queryForId("en");
        Language es = languages.queryForId("es");

        ContentUrl tipUrl = new ContentUrl();
        databaseHelper.getContentUrlDao().create(tipUrl);

        Dao<UrlTranslation, Integer> urlTranslations = databaseHelper.getUrlTranslationDao();
        UrlTranslation contentEn = new UrlTranslation(tipUrl, en, response.getTipUrl_en());
        urlTranslations.createOrUpdate(contentEn);
        UrlTranslation contentEs = new UrlTranslation(tipUrl, es, response.getTipUrl_es());
        urlTranslations.createOrUpdate(contentEs);

        ContentUrl coverUrl = new ContentUrl();
        databaseHelper.getContentUrlDao().create(coverUrl);

        UrlTranslation coverEn = new UrlTranslation(coverUrl, en, response.getCoverUrl_en());
        urlTranslations.createOrUpdate(coverEn);
        UrlTranslation coverEs = new UrlTranslation(coverUrl, es, response.getCoverUrl_es());
        urlTranslations.createOrUpdate(coverEs);

        VeggieBook veggieBook = databaseHelper.getVeggieBookDao().queryForId(VeggieBook.createId(bi));
        if(veggieBook==null) {
            //Create VeggieBook
            veggieBook = new VeggieBook(bi);
        }

        veggieBook.setPantryId(bookBuilder.getSelectedPantryId());
        veggieBook.setTipPage(tipUrl);
        veggieBook.setServerUid(response.getId());
        veggieBook.setCoverImageUrl(bookBuilder.getCoverPhoto().getUrl());
        veggieBook.setCoverImageId(bookBuilder.getCoverPhoto().getId());
        veggieBook.setCoverUrl(coverUrl);

        databaseHelper.getVeggieBookDao().createOrUpdate(veggieBook);


        //Add selectables
        for (CreateBookRequest.Selectable s : bookBuilder.getSelectableList()) {
            Selectable selectable = databaseHelper.getSelectablesDao().queryBuilder()
                    .where()
                    .eq("recipeId",s.getRecipeId())
                    .and()
                    .eq("profileId", AppSettings.getProfileId())
                    .query().get(0);
            selectable.setVeggieBook(veggieBook);
            selectable.setSelected(s.isSelected());
            selectable.setExtras(s.getExtras());

            databaseHelper.getSelectablesDao().createOrUpdate(selectable);
        }

        //Add attributes
        for (String attribId : bookBuilder.getAttributeList()) {
            Attribute attribute = new Attribute(attribId, veggieBook);
            databaseHelper.getAttributeDao().create(attribute);
        }


        return veggieBook;
    }



}
