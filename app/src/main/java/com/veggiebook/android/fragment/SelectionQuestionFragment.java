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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.veggiebook.R;
import com.veggiebook.android.AppSettings;
import com.veggiebook.android.activity.InterviewActivity;
import com.veggiebook.android.view.MySwitch;
import com.veggiebook.android.view.ScrollDetectWebview;
import com.veggiebook.model.BookBuilder;
import com.veggiebook.model.Question;
import com.veggiebook.model.SelectionQuestion;

import org.json.JSONException;
import org.json.JSONObject;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;


/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/13/13
 * Time: 2:31 PM
 */
public class SelectionQuestionFragment extends RoboFragment implements QuestionUi, CompoundButton.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, View.OnClickListener {
    public static final String QUESTION_ID_KEY = "questionId";

    @InjectView(R.id.webView)
    ScrollDetectWebview webView;


    @InjectView(R.id.progressBar)
    ProgressBar progressBar;

    @InjectView(R.id.switchView)
    Switch includeSwitch;

    @InjectView(R.id.spinner)
    Spinner extrasSpinner;

    @InjectView(R.id.textView1)
    TextView printTextView;


    @InjectView(R.id.textView)
    TextView progressText;



    boolean isLoaded;

    private String questionId;




    public static SelectionQuestionFragment newInstance(SelectionQuestion question) {
        SelectionQuestionFragment questionFragment = new SelectionQuestionFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION_ID_KEY, question.getQuestionIdentifier());
        questionFragment.setArguments(args);

        return questionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.select_question_fragement, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        webView.setPadding(0, 0, 0, 0);
        webView.setInitialScale(getScale());
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
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // do your handling codes here, which url is the requested url
                // probably you need to open that url rather than redirect:
                view.loadUrl(url);
                return false; // then it is not handled by default action
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.extra_copies, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        extrasSpinner.setAdapter(adapter);
        extrasSpinner.setOnItemSelectedListener(this);

        //get the question from the book manager
        Bundle args = getArguments();

        questionId =  args.getString(QUESTION_ID_KEY);


        includeSwitch.setOnCheckedChangeListener(this);
        if(getSelectionQuestion() == null)
            return;

        if (getSelectionQuestion().getAnswer() == null) {
            includeSwitch.setChecked(!((SelectionQuestion.Answer) getSelectionQuestion().getDefault()).isIncluded());
            extrasSpinner.setSelection(((SelectionQuestion.Answer) getSelectionQuestion().getDefault()).getExtraCopies());
        } else {
            includeSwitch.setChecked(!((SelectionQuestion.Answer) getSelectionQuestion().getAnswer()).isIncluded());

            extrasSpinner.setSelection(((SelectionQuestion.Answer) getSelectionQuestion().getAnswer()).getExtraCopies());
        }


        setPrintExtrasVisibility(!includeSwitch.isChecked()?View.VISIBLE:View.INVISIBLE);


        progressText.setText(String.format("(%d/%d)", getSelectionQuestion().getPosition(), getSelectionQuestion().getSelectionQuestionCount()));

        setWebViewMode();

        isLoaded = false;
        if (getSelectionQuestion().getUrl() == null) {
            setVisibility(View.INVISIBLE);


        } else {
            String url =  getSelectionQuestion().getUrl();
            if (url.contains("?")){
                 url = url.concat("&selection");
            }
            else{
                url = url.concat("?selection");
            }
            webView.loadUrl(url);
            setVisibility(View.VISIBLE);
            isLoaded = true;
        }

    }

    private void setPrintExtrasVisibility(int visibility){
        printTextView.setVisibility(visibility);
        extrasSpinner.setVisibility(visibility);

    }

    private void setVisibility(int visibility) {
        includeSwitch.setVisibility(visibility);
        progressBar.setVisibility(visibility);
        webView.setVisibility(visibility);
        if(includeSwitch.isChecked()){
            setPrintExtrasVisibility(View.GONE);
        }
        else {
            setPrintExtrasVisibility(visibility);
        }
    }

    @Override
    public boolean isValidAnswer() {
        //show the next button if the
        return true;

    }

    @Override
    public void answerQuestion() {
        Question q =  getSelectionQuestion();
        if(q==null) return;
        q.answer(getSelectionQuestion().new Answer(!includeSwitch.isChecked(), extrasSpinner.getSelectedItemPosition(), webView.isScrolled()));
        //track the answering of this question
        JSONObject props = new JSONObject();
        try {
            props.put("Book Type", "Veggie");
            props.put("Book Id", getBookBuilder().getAvailableBook().getBookId());
            props.put("Recipe Id", questionId);
            props.put("Keep", !includeSwitch.isChecked());
            props.put("Extra Copies", extrasSpinner.getSelectedItemPosition());
            props.put("Was Scrolled", webView.isScrolled());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean displayKeepDrop() {
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(!(id==R.id.menu_drop || id==R.id.menu_keep))
            return super.onOptionsItemSelected(item);

        //include checked means dropped
        includeSwitch.setChecked(id==R.id.menu_drop);

        if(pageForwardTask!=null){
            pageForwardTask.cancel(true);
            pageForwardTask = null;
        }

        pageForwardTask = new PageForwardTask();
        pageForwardTask.execute((long) 280);

        return  true;
    }

    @Override
    public Question getQuestion() {
        return getBookBuilder().getQuestion(questionId);
    }

    @Override
    public boolean displayImageTitle() {
        return false;
    }



    private SelectionQuestion getSelectionQuestion() {
        return (SelectionQuestion) getQuestion();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        setWebViewMode();
        setPrintExtrasVisibility(!isChecked?View.VISIBLE:View.INVISIBLE);
        //track the change
        JSONObject props = new JSONObject();
        try {
            props.put("Book Type", "Veggie");
            props.put("Book Id", getBookBuilder().getAvailableBook().getBookId());
            props.put("Recipe Id", questionId);
            props.put("Changed To", isChecked);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected BookBuilder getBookBuilder() {
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

    private void setWebViewMode(){
        if(!includeSwitch.isChecked()){
            webView.setAlpha((float) 1.0);
        }
        else{
            webView.setAlpha((float)0.3);
        }

    }


    @Override
    public void onClick(View v) {
        setWebViewMode();
        getActivity().invalidateOptionsMenu();
    }

    private PageForwardTask pageForwardTask = null;

    public class PageForwardTask extends AsyncTask<Long,Void,Void>{

        @Override
        protected Void doInBackground(Long... params) {
            if(params.length !=1){
                return null;
            }
            else{
                try {
                    Thread.sleep(params[0]);
                    return null;
                } catch (InterruptedException e) {
                    return null;
                }
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pageForwardTask = null;
            if(getActivity()==null)
                return;

            ((InterviewActivity) getActivity()).pageForward();

        }

    }

}