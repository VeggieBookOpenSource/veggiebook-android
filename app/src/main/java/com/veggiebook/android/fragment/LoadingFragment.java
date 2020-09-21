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

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.veggiebook.R;
import com.veggiebook.android.activity.InterviewActivity;
import com.veggiebook.android.view.InterviewViewPager;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.LoadingQuestion;
import com.veggiebook.model.Question;
import com.veggiebook.service.rest.http.VbHttpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/13/13
 * Time: 2:31 PM
 */
public class LoadingFragment extends RoboFragment implements QuestionUi, ConnectionErrorDialog.ConnectionErrorDialogListener{
    public static final String QUESTION_ID_KEY = "questionId";
    public static final String POSITION_KEY = "position";
    public static Logger log = LoggerFactory.getLogger(LoadingFragment.class);
    private ProgressDialogFragment progressDialog;

    @InjectView(R.id.webView)
    WebView webView;

    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @InjectView(R.id.textView)
    TextView textView;

    @InjectView(R.id.imageView)
    ImageView arrow;

    boolean isLoaded;

    private Question question;


    public static LoadingFragment newInstance(LoadingQuestion question) {
        LoadingFragment questionFragment = new LoadingFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID_KEY, question.getQuestionIdentifier());
        questionFragment.setArguments(args);

        return questionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.loadting_fragement, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(getScale());

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }

        });
        webView.getSettings().setJavaScriptEnabled(true);

        //get the question from the book manager
        Bundle args = getArguments();

        Question q = getBookBuilder().getQuestion(args.getString(QUESTION_ID_KEY));
        setQuestion(q);

        webView.loadUrl((String) question.getDefault());
        textView.setVisibility(View.GONE);
        arrow.setVisibility(View.GONE);

        InterviewActivity interviewActivity = (InterviewActivity) getActivity();
        if(interviewActivity.getViewPager().getCurrentItem()==0 && interviewActivity.getPreviousPosition()==-1){
            interviewActivity.setProgressSpinnerVisible(true);

            new LoadSelectables().execute();
        }

    }


    @Override
    public boolean isValidAnswer() {
        return isLoaded;
    }

    @Override
    public void answerQuestion() {
        question.answer(null);
    }

    @Override
    public boolean displayKeepDrop() {
        return false;
    }


    @Override
    public Question getQuestion() {
        return question;
    }

    private void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public boolean displayImageTitle() {
        return false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        InterviewActivity interviewActivity = (InterviewActivity) getActivity();
        if (isVisibleToUser) {
            if (interviewActivity != null && getQuestion() != null) {
                if(interviewActivity.getPreviousPosition() > interviewActivity.getViewPager().getCurrentItem()){
                    return;
                }

                isLoaded = false;
                ((InterviewActivity) getActivity()).setProgressSpinnerVisible(true);
                new LoadSelectables().execute();
                    return;
                }

        }
        else {
            if (interviewActivity != null) {
                ((InterviewActivity) getActivity()).setProgressSpinnerVisible(false);
            }


        }

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        new LoadSelectables().execute();
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        InterviewViewPager viewPager = ((InterviewActivity) getActivity()).getViewPager();
        viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }

    private LoadingFragment getSelf(){
        return this;
    }

    public class LoadSelectables extends AsyncTask<Void, Void, List<Question>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialogFragment.newInstance(getString(R.string.loading_now), false);
            progressDialog.show(getFragmentManager(), "LOADING_DIALOG");

        }



        @Override
        protected List<Question> doInBackground(Void... params) {
           return retryDownload(3);
        }

        //retrys up to retries times,
        //returns true if successful
        //false otherwise.
        protected List<Question> retryDownload(int retries){
            if(retries==0)
                return null;


            try {
                return getBookBuilder().downloadSelectables();

            } catch (VbHttpException e) {
                log.error(e.getLocalizedMessage(), e);

                return retryDownload(retries-1);
            } catch (SQLException e) {
                //This is not really a connection error, and should never happen.
                //Need to do better error handling here.
                log.error(e.getLocalizedMessage(), e);
                return retryDownload(retries-1);
            }

        }


        @Override
        protected void onPostExecute(List<Question> newQuestions) {
            boolean successful = newQuestions != null;

            progressDialog.dismiss();
            arrow.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            if(getActivity()==null)
                return;
            if(successful){
                isLoaded = true;
                InterviewActivity interviewActivity = (InterviewActivity) getActivity();
                getBookBuilder().replaceOrCreateSelectables(newQuestions);
                interviewActivity.getViewPager().getAdapter().notifyDataSetChanged();
                interviewActivity.setProgressSpinnerVisible(false);
            }
            else{
                ConnectionErrorDialog alert = new ConnectionErrorDialog();
                alert.setListener(getSelf());
                alert.show(getFragmentManager(), "CONNECTION_ERROR");

            }
        }
    }

    protected BookBuilder getBookBuilder() {
        if(getActivity()==null)
            return null;

        return ((InterviewActivity) getActivity()).getBookBuilder();
    }


    private int getScale() {
        Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        Double val = new Double(width) / new Double(320);
        val = val * 100d;
        return val.intValue();
    }



}