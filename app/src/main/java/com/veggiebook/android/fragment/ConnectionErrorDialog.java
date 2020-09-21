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

package com.veggiebook.android.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.veggiebook.R;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/19/13
 * Time: 12:07 PM
 *
 */
public class ConnectionErrorDialog extends DialogFragment {

    public interface ConnectionErrorDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ConnectionErrorDialogListener mListener;


    public void setListener(ConnectionErrorDialogListener mListener) {
        this.mListener = mListener;
    }

    private DialogFragment getDialogFragment(){
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.connection_error_title);
        alertDialogBuilder.setMessage(R.string.connection_error_message);
        //null should be your on click listener
        alertDialogBuilder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mListener!=null)
                    mListener.onDialogPositiveClick(getDialogFragment());
            }

        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mListener!=null)
                    mListener.onDialogNegativeClick(getDialogFragment());
            }
        });



        return alertDialogBuilder.create();
    }

}
