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

import java.util.UUID;

@DatabaseTable()
public class ContentUrl {

	public ContentUrl() {
		// for ormlite
        test = UUID.randomUUID().toString();
	}

	@DatabaseField(generatedId = true)
	private int id;


    public int getId() {
        return id;
    }

    @DatabaseField()
    private String test;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<UrlTranslation> translations;

    public ForeignCollection<UrlTranslation> getTranslations() {
        return translations;
    }
}
