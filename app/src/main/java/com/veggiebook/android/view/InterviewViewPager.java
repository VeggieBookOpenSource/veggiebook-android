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

package com.veggiebook.android.view;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/14/13
 * Time: 10:03 AM
 *
 */
public class InterviewViewPager extends ViewPager  {
    private static final int SWIPE_MIN_DISTANCE = 6;//120;
    private static final int SWIPE_MAX_OFF_PATH = 125;//250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;//200;
    private boolean fromBack = false;


    public InterviewViewPager(Context context) {
        super(context);

    }

    public InterviewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
       return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public void setCurrentItem(int item) {
        fromBack = getCurrentItem() > item;
        super.setCurrentItem(item);
    }



    public boolean isFromBack() {
        return fromBack;
    }

}
