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
 * Date: 3/15/13
 * Time: 3:13 AM
 */
public class VbNotReadyException extends Exception {
    public VbNotReadyException() {
    }

    public VbNotReadyException(String detailMessage) {
        super(detailMessage);
    }

    public VbNotReadyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public VbNotReadyException(Throwable throwable) {
        super(throwable);
    }
}
