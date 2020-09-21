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
 * Date: 2/19/13
 * Time: 5:17 PM
 *
 */
public class CoverSelectQuestion extends Question{
    Answer photoAnswer;
    transient Handler handler;


    public CoverSelectQuestion(Handler handler, String identifier) {
        super(identifier, "CS");
        this.handler = handler;
    }

    private CoverSelectQuestion() {
        super(null, "CS");
    }

    @Override
    public void answer(Object data) {
        photoAnswer = (Answer) data;
        handler.handleCoverSelectAnswer(photoAnswer);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public Object getAnswer() {
        return photoAnswer;
    }

    @Override
    public Object getDefault() {
        return null;
    }

    interface Handler {
        public void handleCoverSelectAnswer(Answer photoAnswer);
    }

    public static class Answer {
        private String id;
        private String url;

        public Answer(String id, String url) {
            this.id = id;
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }
    }
}
