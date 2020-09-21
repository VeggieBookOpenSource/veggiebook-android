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
 * @author Daniel DiPasquo
 *
 * BookInfo represents the available books that a user can create.  These are pulled from the Server.
 * There should be no locally generated data in this table.
 * 
 */
@DatabaseTable()
public class BookInfo {

	public BookInfo() {
		// for ormlite 
	}
	
	@DatabaseField(id = true)
	private String id;
	
	@DatabaseField(canBeNull=false, foreign=true)
	private BookType type;
	
	@DatabaseField(canBeNull=false, foreign=true)
	private TextItem title;
	
	@DatabaseField(canBeNull=true, foreign=true)
	private Image image;
	
	@DatabaseField(canBeNull=false)
	private boolean deprecated;

    @DatabaseField(canBeNull=false)
    private boolean hasSelectables;

    @DatabaseField(canBeNull=false, foreign=true)
    private TextItem loadingUrl;

    @DatabaseField(canBeNull=false)
    private int pIndex;

    public BookInfo(String id, BookType type, TextItem title, Image image, boolean hasSelectables, TextItem loadingUrl) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.image = image;
        this.deprecated=false;
        this.hasSelectables=hasSelectables;
        this.loadingUrl=loadingUrl;
        this.pIndex = 100;

    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getId() {
        return id;
    }

    public BookType getType() {
        return type;
    }

    public TextItem getTitle() {
        return title;
    }

    public Image getImage() {
        return image;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public boolean hasSelectables() {
        return hasSelectables;
    }

    public TextItem getLoadingUrl() {
        return loadingUrl;
    }

    public int getpIndex() {
        return pIndex;
    }

    public void setpIndex(int pIndex) {
        this.pIndex = pIndex;
    }
}
