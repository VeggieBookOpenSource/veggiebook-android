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
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 7/31/13
 * Time: 6:58 PM
 *
 * A webview that detects keeps track if it was ever scrolled
 *
 */
public class ScrollDetectWebview extends WebView{
    private boolean isScrolled=false;

    public ScrollDetectWebview(Context context) {
        super(context);
    }

    public ScrollDetectWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollDetectWebview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        isScrolled=true;
        super.onScrollChanged(l, t, oldl, oldt);
    }


    public boolean isScrolled() {
        return isScrolled;
    }
}
