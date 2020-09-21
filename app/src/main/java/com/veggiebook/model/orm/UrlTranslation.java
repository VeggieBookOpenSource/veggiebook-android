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

@DatabaseTable()
public class UrlTranslation {

	public UrlTranslation() {
		//for ormlite
	}

	@DatabaseField(generatedId =  true)
	private int id;

	@DatabaseField(index=true, foreign=true, canBeNull=false, unique=false)
	private ContentUrl contentUrl;

	@DatabaseField(canBeNull=false, foreign=true)
	private Language language;

    public void setTextString(String textString) {
        this.textString = textString;
    }

    @DatabaseField()
	private String textString;

    public UrlTranslation(ContentUrl contentUrl, Language language, String textString) {
        this.contentUrl = contentUrl;
        this.language = language;
        this.textString = textString;
    }

    public int getId() {
        return id;
    }

    public ContentUrl getContentUrl() {
        return contentUrl;
    }

    public Language getLanguage() {
        return language;
    }

    public String getTextString() {
        return textString;
    }
}
