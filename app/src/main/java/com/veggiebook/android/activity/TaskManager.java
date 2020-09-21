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

package com.veggiebook.android.activity;

import android.os.AsyncTask;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/21/13
 * Time: 1:21 PM
 *
 * This is used to ensure only one task of a type is running at a time.  If another task is running, it is cancelled
 *
 */
public class TaskManager <T extends AsyncTask>{
    private T task;

    public T newTask(T task){
        if(this.task != null){
            this.task.cancel(true);
        }
        this.task = task;
        return task;
    }

    public void cancel(boolean mayInterupt){
        if(task!=null){
            task.cancel(mayInterupt);
        }
    }

    public T getTask(){
        return task;
    }

}
