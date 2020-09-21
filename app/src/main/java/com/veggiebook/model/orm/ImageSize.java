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
public class ImageSize {

	public ImageSize(){
		
	}
	
	@DatabaseField(id=true)
	private String id;
	
	@DatabaseField(canBeNull=false, foreign=true)
	private Image image;
	
	@DatabaseField(canBeNull=false)
	private String url;
	
	@DatabaseField(canBeNull=false,foreign=true)
	private ImageSizeCategory resolution;

    public ImageSize(Image image, String url, ImageSizeCategory resolution) {
        id = resolution.getResolution() + image.getId();
        this.image = image;
        this.url = url;
        this.resolution = resolution;
    }

    public String getId() {
        return id;
    }

    public Image getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }

    public ImageSizeCategory getResolution() {
        return resolution;
    }
}
