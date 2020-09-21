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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.veggiebook.R;

//@SuppressLint("NewApi")
public class CascadeItemView extends FrameLayout {
	private ImageView imgView;
	private TextView titleView;
    private ImageView smallImageview;
    private ImageView checkBackground;
    private ImageView checkBackground2;
    private ImageView check;
    private View viewStub;
    private boolean checked=false;




	public CascadeItemView(Context context) {
		super(context);
        init();
    }

    public CascadeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CascadeItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item, this);
        imgView = (ImageView) this.findViewById(R.id.bookImg);
        titleView = (TextView) this.findViewById(R.id.veggie_name);
        smallImageview = (ImageView) this.findViewById(R.id.smallImage);
        viewStub = this.findViewById(R.id.viewStub);
        checkBackground = (ImageView) this.findViewById(R.id.checkBackground);
        checkBackground.setVisibility(View.GONE);
        checkBackground2 = (ImageView) this.findViewById(R.id.checkBackground2);
        checkBackground2.setVisibility(View.GONE);
        check = (ImageView) this.findViewById(R.id.check);
        check.setVisibility(View.GONE);


        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);


    }
	

	public void setImageAndTitle(int imageId, String title){
		imgView.setImageResource(imageId);
		imgView.setContentDescription(title);
		titleView.setText(title);
    }

    public void setImageAndTitle(int imageId, int string_id){
        imgView.setImageResource(imageId);
        imgView.setContentDescription(getContext().getString(string_id));
        titleView.setText(getContext().getString(string_id));
    }

    public void setTitle(String title){
        imgView.setContentDescription(title);
        titleView.setText(title);
    }

    public void setGradientEnabled(boolean enabled){
        viewStub.setVisibility(enabled?View.VISIBLE:View.INVISIBLE);
    }

    public ImageView getImgView() {
        return imgView;
    }

    public TextView getTitleView() {
        return titleView;
    }

    public ImageView getSmallImageview() {
        return smallImageview;
    }

    public void enableCheckBox(){
        checkBackground.setVisibility(VISIBLE);
        checkBackground2.setVisibility(VISIBLE);
        if(checked)
            check.setVisibility(VISIBLE);
        else
            check.setVisibility(GONE);
    }

    public void setChecked(boolean checked){
        this.checked = checked;
        check.setVisibility(checked?VISIBLE:GONE);
    }

    public boolean isChecked(){
        return checked;
    }

    public boolean toggleCheckBox(){
        checked = !checked;
        check.setVisibility(checked?VISIBLE:GONE);
        return checked;
    }


}
