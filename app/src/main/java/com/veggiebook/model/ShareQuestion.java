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


public class ShareQuestion extends Question {
    private transient Handler handler=null;

    public ShareQuestion(Handler handler, String identifier) {
        super(identifier, "SQ");
        this.handler = handler;
    }

    @Override
    public void answer(Object data) {

    }

    @Override
    public Object getAnswer() {
        return null;
    }

    @Override
    public Object getDefault() {
        return null;
    }

    interface Handler {
        public void HandleShareAnswer(Answer answer);

    }
    public class Answer{

    }
}
