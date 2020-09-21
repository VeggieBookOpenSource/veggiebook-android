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

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/12/13
 * Time: 12:08 PM
 *
 */
public class MultipleChoiceQuestion extends ChoiceQuestion{
    transient Handler handler;
    Map<String,Boolean> defaultChoices;
    Map<String,Boolean> answer;
    Boolean requireSelection;

    public MultipleChoiceQuestion(Handler handler, String questionId, Boolean requireSelection) {
        super(questionId, "MCQ");
        this.handler = handler;
        answer=null;
        defaultChoices = new HashMap<String, Boolean>();
        this.requireSelection=requireSelection;

    }

    private MultipleChoiceQuestion(){
        super(null, "MCQ");
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void answer(Object data) {
        answer = (Map<String,Boolean>) data;
        handler.handleMCAnswer(this,(Map<String,Boolean>) data);
    }

    @Override
    public Object getAnswer() {
        return answer;
    }

    @Override
    public Object getDefault() {
        return defaultChoices;
    }

    public interface Handler{
        public void handleMCAnswer(MultipleChoiceQuestion question, Map<String, Boolean> answers);
    }


    public void addChoice(String displayText, String id, boolean defaultChoice) {
        super.addChoice(displayText, id);
        defaultChoices.put(id,defaultChoice);
    }

    public Boolean getRequireSelection() {
        return requireSelection;
    }
}
