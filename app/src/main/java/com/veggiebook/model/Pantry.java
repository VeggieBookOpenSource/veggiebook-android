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

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 5/8/13
 * Time: 2:02 PM
 *
 */
public class Pantry {
    String displayName;
    String id;

    public Pantry(String displayName, String id) {
        this.displayName = displayName;
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public boolean equals(Object o) {
        return this.getId().equals(((Pantry) o).getId());
    }

    public String getId() {
        return id;
    }
}
