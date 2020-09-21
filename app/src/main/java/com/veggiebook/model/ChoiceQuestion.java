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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/12/13
 * Time: 12:16 PM
 *
 */
public abstract class ChoiceQuestion extends Question {
    List<Choice> choices;
    Map<String,Choice> choiceMap;

    public ChoiceQuestion(String identifier, String type) {
        super(identifier, type);
    }

    public void addChoice(String displayText, String id){
        if(choices==null){
            choices = new ArrayList<Choice>();
        }
        Choice choice = new Choice(displayText,id);
        choices.add(choice);
        if(choiceMap==null){
            choiceMap = new HashMap<String, Choice>();
        }
        choiceMap.put(choice.getId(),choice);
    }

    public List<String> getChoiceTexts(){
        List<String> list = new ArrayList<String>(choices.size());
        for(Choice choice : choices){
            list.add(choice.getDisplayText()) ;
        }
        return list;
    }

    public Choice getChoice(int position){
        if(choices==null || choices.size()<position || position==0)
            return null;

        return choices.get(position-1);
    }

    public Choice getChoice(String id){
        if(choiceMap==null)
            return null;

        return choiceMap.get(id);
    }

    public class Choice{
        private String displayText;
        private String id;

        public Choice(String displayText, String id) {
            this.displayText = displayText;
            this.id = id;
        }

        public String getDisplayText() {
            return displayText;
        }

        public String getId() {
            return id;
        }
    }

    public List<Choice> getChoices() {
        return choices;
    }
}
