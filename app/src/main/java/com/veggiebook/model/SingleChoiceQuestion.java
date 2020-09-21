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
 * Date: 2/12/13
 * Time: 12:17 PM
 *
 */
public class SingleChoiceQuestion extends ChoiceQuestion {
    private transient Handler handler;
    private Integer defaultSelectionId;
    private Integer answerId=null;




    public SingleChoiceQuestion(Handler handler, String questionId) {
        super(questionId, "SCQ");
        this.handler = handler;
        defaultSelectionId = null;
    }

    private SingleChoiceQuestion(){
        super(null, "SCQ");
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void answer(Object data) {
        answerId = (Integer) data;
        handler.handleSCAnswer(this, (Integer) data);

    }

    @Override
    public Object getAnswer() {
        return answerId;
    }

    @Override
    public Object getDefault() {
        return defaultSelectionId;
    }

    public void setDefaultSelectionId(Integer defaultSelectionId){
        this.defaultSelectionId = defaultSelectionId;
    }

    public interface Handler{
        public void handleSCAnswer(SingleChoiceQuestion question, Integer selectionPosition);
    }


}
