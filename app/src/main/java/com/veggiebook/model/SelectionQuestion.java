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
 * Date: 2/13/13
 * Time: 2:27 PM
 *
 */
public class SelectionQuestion extends Question{
    private String url;
    private Integer sId;
    private transient Handler handler;
    private Answer answer;
    private int position;
    private int selectionQuestionCount;
    private String photoUrl;
    private String coverPhotoId;


    public SelectionQuestion(Handler handler, String identifier, int position, int selectionQuestionCount) {
        super(identifier, "SQ");
        this.handler = handler;
        this.position = position;
        this.selectionQuestionCount = selectionQuestionCount;
    }

    private SelectionQuestion(){
        super(null, "SQ");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void answer(Object data) {
        this.answer = (Answer)data;
        handler.handleSelectableAnswer(this,answer);

    }

    @Override
    public Object getAnswer() {
        return answer;
    }

    @Override
    public Object getDefault() {
        if(getAnswer()==null)
            return new Answer(true,0,false);
        else
            return getAnswer();
    }

    public Integer getsId() {
        return sId;
    }

    public void setsId(Integer sId) {
        this.sId = sId;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public int getPosition() {
        return position;
    }

    public int getSelectionQuestionCount() {
        return selectionQuestionCount;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCoverPhotoId() {
        return coverPhotoId;
    }

    public void setCoverPhotoId(String coverPhotoId) {
        this.coverPhotoId = coverPhotoId;
    }

    public interface Handler{
        public void handleSelectableAnswer(SelectionQuestion q, Answer answer);
    }

    public class Answer{
        private boolean included;
        private int extraCopies;
        private boolean scrolled;

        public Answer(boolean included, int extraCopies, boolean scrolled) {
            this.included = included;
            this.extraCopies = extraCopies;
            this.scrolled = scrolled;
        }

        public boolean isIncluded() {
            return included;
        }

        public void setIncluded(boolean included) {
            this.included = included;
        }

        public int getExtraCopies() {
            return extraCopies;
        }

        public void setExtraCopies(int extraCopies) {
            this.extraCopies = extraCopies;
        }

        public boolean isScrolled() {
            return scrolled;
        }

        public void setScrolled(boolean scrolled) {
            this.scrolled = scrolled;
        }
    }

}
