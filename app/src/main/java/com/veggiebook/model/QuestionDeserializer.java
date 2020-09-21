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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/20/13
 * Time: 12:53 PM
 *
 */
public class QuestionDeserializer implements JsonDeserializer<Question>{
    @Override
    public Question deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Class questionClass = null;
        String type = jsonObject.get("type").getAsString();

        if(type.equals("SCQ")){
            questionClass = SingleChoiceQuestion.class;
        }

        if(type.equals("MCQ")){
            questionClass = MultipleChoiceQuestion.class;
        }

        if(type.equals("SQ")){
            questionClass = SelectionQuestion.class;
        }

        if(type.equals("CS")) {
            questionClass = CoverSelectQuestion.class;
        }

        if(type.equals("LQ")) {
            questionClass = LoadingQuestion.class;
        }



        return context.deserialize(json, questionClass);
    }
}
