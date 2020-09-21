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
public class Translation {

	public Translation() {
		//for ormlite
	}

	@DatabaseField(id = true)
	private String id;
	
	@DatabaseField(index=true, foreign=true, canBeNull=false, unique=false)
	private TextItem textItem;
	
	@DatabaseField(canBeNull=false, foreign=true)
	private Language language;
	
	@DatabaseField()
	private String textString;

    public Translation(TextItem textItem, Language language, String textString) {
        this.id = language.getLanguageCode() + textItem.getId();
        this.textItem = textItem;
        this.language = language;
        this.textString = textString;
    }

    public String getId() {
        return id;
    }

    public TextItem getTextItem() {
        return textItem;
    }

    public Language getLanguage() {
        return language;
    }

    public String getTextString() {
        return textString;
    }
}
