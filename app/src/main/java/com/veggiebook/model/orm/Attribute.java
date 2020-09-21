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
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/15/13
 * Time: 2:28 AM
 *
 */
@DatabaseTable
public class Attribute {
    public Attribute() {
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String serverId;

    @DatabaseField(canBeNull = false, foreign = true)
    private VeggieBook veggieBook;

    public Attribute(String serverId, VeggieBook veggieBook) {
        this.serverId = serverId;
        this.veggieBook = veggieBook;
    }

    public int getId() {
        return id;
    }


    public VeggieBook getVeggieBook() {
        return veggieBook;
    }

    public String getServerId() {
        return serverId;
    }
}
