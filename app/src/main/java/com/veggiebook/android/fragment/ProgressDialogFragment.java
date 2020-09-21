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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.veggiebook.R;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/15/13
 * Time: 12:49 AM
 *
 */
public class ProgressDialogFragment extends DialogFragment {
    private final static String MESSAGE_ARG = "message";
    private final static String CANCELABLE_ARG = "cancelable";

    public static ProgressDialogFragment newInstance(String message, boolean can) {
        ProgressDialogFragment frag = new ProgressDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(CANCELABLE_ARG, can);
        args.putString(MESSAGE_ARG, message);
        frag.setArguments(args);
        return frag;
    }

    public static ProgressDialogFragment newInstance(boolean can) {
        Bundle args = new Bundle();
        args.putBoolean(CANCELABLE_ARG, can);
        ProgressDialogFragment frag = new ProgressDialogFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String message = getArguments().getString(MESSAGE_ARG);
        final boolean cancelable = getArguments().getBoolean(CANCELABLE_ARG);
        ProgressDialog pbarDialog = new ProgressDialog(getActivity());
        if ( message ==  null )
            pbarDialog.setMessage(getString(R.string.loading));
        else
            pbarDialog.setMessage(message);
        pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pbarDialog.setIndeterminate(true);
        pbarDialog.setCancelable(cancelable);
        setCancelable(cancelable);
        setRetainInstance(true);
        return pbarDialog;

    }

    public void onDismiss (DialogInterface dialog){
        super.onDismiss(dialog);
        if ( isCancelable() )
        {
            if ( getActivity() instanceof DialogInterface.OnDismissListener)
            {
                ((DialogInterface.OnDismissListener) getActivity()).onDismiss(dialog);
            }
        }
    }

}