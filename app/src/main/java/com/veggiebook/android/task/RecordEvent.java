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

package com.veggiebook.android.task;

import android.os.AsyncTask;

import com.veggiebook.model.BookManager;
import com.veggiebook.service.dto.RecordEventRequest;
import com.veggiebook.service.rest.http.VbHttpException;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 1/26/15
 * Time: 8:01 AM
 * RecordEvent is used to record a viewing or sharing event.
 */
public class RecordEvent extends AsyncTask<RecordEventRequest, Void, Void>{
    @Override
    protected Void doInBackground(RecordEventRequest... params) {
        for(RecordEventRequest param : params){

            try {
                BookManager.getBookManager().getRestService().recordEvent(param);
            } catch (VbHttpException e) {
                e.printStackTrace();
                //ignore issues recording data for now.
            }
        }
        return null;
    }
}
