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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.veggiebook.R;
import com.veggiebook.android.AppSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file
	private static final String DATABASE_NAME = "veggieBook.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 25;

    //Logger
    private static Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

	// the DAO object we use to access the SimpleData table
    private Dao<BookInfo, String> bookInfoDao = null;
    private RuntimeExceptionDao<BookInfo,String> bookRuntimeDao = null;
    private Dao<BookType,String> bookTypeDao = null;
    private RuntimeExceptionDao<BookType,String> bookTypeRuntimeDao = null;
    private Dao<Image, Integer> imageDao = null;
    private RuntimeExceptionDao<Image,Integer> imageRuntimeDao = null;
    private Dao<ImageSizeCategory,String> imageSizeCategoryDao = null;
    private RuntimeExceptionDao<ImageSizeCategory,String> imageSizeCategoryRuntimeDao = null;
    private Dao<ImageSize, String> imageSizeDao = null;
    private RuntimeExceptionDao<ImageSize,String> imageSizeRuntimeDao = null;
    private Dao<Language,String> languageDao = null;
    private RuntimeExceptionDao<Language,String> languageRuntimeDao = null;
    private Dao<TextItem, Integer> textItemDao = null;
    private RuntimeExceptionDao<TextItem,Integer> textItemRuntimeDao = null;
    private Dao<Translation,String> translationDao = null;
    private RuntimeExceptionDao<Translation,String> translationRuntimeDao = null;
    private Dao<Attribute,Integer> attributeDao = null;
    private Dao<ContentUrl,Integer> contentUrlDao = null;
    private Dao<Selectable, String> selectablesDao = null;
    private Dao<UrlTranslation, Integer> urlTranslationDao = null;
    private Dao<VeggieBook, String> veggieBookDao = null;

    private static DatabaseHelper singleton = null;

    public static DatabaseHelper getDatabaseHelper(){
        if(singleton==null){
             singleton = new DatabaseHelper(AppSettings.getContext());
        }
        return singleton;
    }

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			log.info( "onCreate");
			TableUtils.createTable(connectionSource, BookInfo.class);
            TableUtils.createTable(connectionSource, BookType.class);
            TableUtils.createTable(connectionSource, Image.class);
            TableUtils.createTable(connectionSource, ImageSize.class);
            TableUtils.createTable(connectionSource, ImageSizeCategory.class);
            TableUtils.createTable(connectionSource, Language.class);
            TableUtils.createTable(connectionSource, TextItem.class);
            TableUtils.createTable(connectionSource, Translation.class);
            TableUtils.createTable(connectionSource, Attribute.class);
            TableUtils.createTable(connectionSource,ContentUrl.class);
            TableUtils.createTable(connectionSource, Selectable.class);
            TableUtils.createTable(connectionSource, UrlTranslation.class);
            TableUtils.createTable(connectionSource, VeggieBook.class);
        } catch (SQLException e) {
			log.error( "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			log.info("onUpgrade");
            TableUtils.dropTable(connectionSource, BookInfo.class,true);
            TableUtils.dropTable(connectionSource, BookType.class,true);
            TableUtils.dropTable(connectionSource, Image.class,true);
            TableUtils.dropTable(connectionSource, ImageSize.class,true);
            TableUtils.dropTable(connectionSource, ImageSizeCategory.class,true);
            TableUtils.dropTable(connectionSource, Language.class,true);
            TableUtils.dropTable(connectionSource, TextItem.class,true);
            TableUtils.dropTable(connectionSource, Translation.class,true);
            TableUtils.dropTable(connectionSource, Attribute.class, true);
            TableUtils.dropTable(connectionSource, ContentUrl.class, true);
            TableUtils.dropTable(connectionSource, Selectable.class, true);
            TableUtils.dropTable(connectionSource, UrlTranslation.class, true);
            TableUtils.dropTable(connectionSource, VeggieBook.class, true);
            AppSettings.setLibraryVersion("UNSET");
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			log.error( "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
	 * value.
	 */
    public Dao<BookInfo, String> getBookInfoDao() throws SQLException {
        if(bookInfoDao==null){
            bookInfoDao = getDao(BookInfo.class);
        }
        return bookInfoDao;
    }

    public RuntimeExceptionDao<BookInfo, String> getBookRuntimeDao() {
        if(bookRuntimeDao==null){
            bookRuntimeDao = getRuntimeExceptionDao(BookInfo.class);
        }
        return bookRuntimeDao;
    }

    public Dao<BookType, String> getBookTypeDao() throws SQLException {
        if(bookTypeDao==null){
            bookTypeDao = getDao(BookType.class);
        }
        return bookTypeDao;
    }

    public RuntimeExceptionDao<BookType, String> getBookTypeRuntimeDao() {
        if(bookTypeRuntimeDao==null){
            bookTypeRuntimeDao = getRuntimeExceptionDao(BookType.class);
        }
        return bookTypeRuntimeDao;
    }

    public Dao<Image, Integer> getImageDao() throws SQLException {
        if(imageDao==null){
            imageDao =getDao(Image.class);
        }
        return imageDao;
    }

    public RuntimeExceptionDao<Image, Integer> getImageRuntimeDao() {
        if(imageRuntimeDao==null){
            imageRuntimeDao = getRuntimeExceptionDao(Image.class);
        }
        return imageRuntimeDao;
    }

    public Dao<ImageSizeCategory, String> getImageSizeCategoryDao() throws SQLException {
        if(imageSizeCategoryDao==null){
            imageSizeCategoryDao=getDao(ImageSizeCategory.class);
        }
        return imageSizeCategoryDao;
    }

    public RuntimeExceptionDao<ImageSizeCategory, String> getImageSizeCategoryRuntimeDao() {
        if(imageSizeCategoryRuntimeDao==null){
            imageSizeCategoryRuntimeDao = getRuntimeExceptionDao(ImageSizeCategory.class);
        }
        return imageSizeCategoryRuntimeDao;
    }

    public Dao<ImageSize, String> getImageSizeDao() throws SQLException {
        if(imageSizeDao==null){
            imageSizeDao=getDao(ImageSize.class);
        }
        return imageSizeDao;
    }

    public RuntimeExceptionDao<ImageSize, String> getImageSizeRuntimeDao() {
        if(imageSizeRuntimeDao==null){
            imageSizeRuntimeDao=getRuntimeExceptionDao(ImageSize.class);
        }
        return imageSizeRuntimeDao;
    }

    public Dao<Language, String> getLanguageDao() throws SQLException {
        if(languageDao==null){
            languageDao=getDao(Language.class);
        }
        return languageDao;
    }

    public RuntimeExceptionDao<Language, String> getLanguageRuntimeDao() {
        if(languageRuntimeDao==null){
            languageRuntimeDao = getRuntimeExceptionDao(Language.class);
        }
        return languageRuntimeDao;
    }

    public Dao<TextItem, Integer> getTextItemDao() throws SQLException {
        if(textItemDao==null){
            textItemDao = getDao(TextItem.class);
        }
        return textItemDao;
    }

    public RuntimeExceptionDao<TextItem, Integer> getTextItemRuntimeDao() {
        if(textItemRuntimeDao==null){
            textItemRuntimeDao=getRuntimeExceptionDao(TextItem.class);
        }

        return textItemRuntimeDao;
    }

    public Dao<Translation, String> getTranslationDao() throws SQLException {
        if(translationDao==null){
            translationDao=getDao(Translation.class);
        }
        return translationDao;
    }

    public RuntimeExceptionDao<Translation, String> getTranslationRuntimeDao() {
        if(translationRuntimeDao==null){
            translationRuntimeDao=getRuntimeExceptionDao(Translation.class);
        }
        return translationRuntimeDao;
    }

    public Dao<Attribute, Integer> getAttributeDao() throws SQLException {
        if(attributeDao==null){
            attributeDao=getDao(Attribute.class);
        }
        return attributeDao;
    }

    public Dao<ContentUrl, Integer> getContentUrlDao() throws SQLException {
        if(contentUrlDao==null){
            contentUrlDao=getDao(ContentUrl.class);
        }
        return contentUrlDao;
    }

    public Dao<Selectable, String> getSelectablesDao() throws SQLException {
        if(selectablesDao==null){
            selectablesDao= getDao(Selectable.class);
        }
        return selectablesDao;
    }

    public Dao<UrlTranslation, Integer> getUrlTranslationDao() throws SQLException {
        if(urlTranslationDao==null){
            urlTranslationDao=getDao(UrlTranslation.class);
        }
        return urlTranslationDao;
    }

    public Dao<VeggieBook, String> getVeggieBookDao() throws SQLException {
        if(veggieBookDao==null){
            veggieBookDao=getDao(VeggieBook.class);
        }
        return veggieBookDao;
    }

    /**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
        bookInfoDao = null;
        bookRuntimeDao = null;
        bookTypeDao = null;
        bookTypeRuntimeDao = null;
        imageDao = null;
        imageRuntimeDao = null;
        imageSizeCategoryDao = null;
        imageSizeCategoryRuntimeDao = null;
        imageSizeDao = null;
        imageSizeRuntimeDao = null;
        languageDao = null;
        languageRuntimeDao = null;
        textItemDao = null;
        textItemRuntimeDao = null;
        translationDao = null;
        translationRuntimeDao = null;
        attributeDao = null;
        contentUrlDao = null;
        selectablesDao = null;
        urlTranslationDao = null;
        veggieBookDao = null;

	}
}