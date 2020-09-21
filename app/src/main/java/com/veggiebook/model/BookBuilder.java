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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.Dao;
import com.veggiebook.BuildConfig;
import com.veggiebook.android.AppSettings;
import com.veggiebook.android.fragment.CoverSelectionFragment;
import com.veggiebook.model.orm.BookInfo;
import com.veggiebook.model.orm.ContentUrl;
import com.veggiebook.model.orm.DatabaseHelper;
import com.veggiebook.model.orm.Language;
import com.veggiebook.model.orm.Selectable;
import com.veggiebook.model.orm.UrlTranslation;
import com.veggiebook.model.orm.VeggieBook;
import com.veggiebook.service.dto.AvailablePhotosResponse;
import com.veggiebook.service.dto.CreateBookRequest;
import com.veggiebook.service.dto.InterviewResponse;
import com.veggiebook.service.dto.SelectableResponse;
import com.veggiebook.service.rest.http.VbHttpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.veggiebook.model.SelectionQuestion.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/12/13
 * Time: 10:49 AM
 */
public class BookBuilder implements  Handler, MultipleChoiceQuestion.Handler, CoverSelectQuestion.Handler, ShareQuestion.Handler {

    protected Map<String, Boolean> attributeMap;
    protected AvailableBook availableBook;

    public static final String PANTRY_QUESTION_ID = "PANTY_SELECTION";
    public static final String SELECTION_QUESTION_FORMAT = "SELECTION_QUESTION_";
    public static final String LOADING_QUESTION_ID = "LOADING_SELECTABLES";
    static final String HIDDEN = "H";
    static final String MULTICHOICE = "M";
    static final String MUTLTICHOICE_NO_SELECTION_REQUIRED = "Z";
    public static final String GET_RECIPES_LQ_ID = "GET_RECIPES";
    public static final String COVER_UPLOAD_ID = "QUESTION_UPLOAD";
    public static final String SHARE_QUESTION_ID = "SHARE_QUESTION";

    static transient Logger log = LoggerFactory.getLogger(BookBuilder.class);

    List<Pantry> pantries;
    String selectedPantryId;

    List<Question> questionList;
    transient Map<String, Question> questionMap;
    protected InterviewResponse response;
    Map<Integer, SelectionQuestion.Answer> selectableMap;
    private CoverSelectQuestion.Answer coverPhoto;
    List<CoverSelectionFragment.PhotoHolder> availablePhotos;
    Date startedAt = new Date();



    public static BookBuilder fromJson(String json) {
        if (json != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Question.class, new QuestionDeserializer())
                    .create();
            BookBuilder bookBuilder = gson.fromJson(json, BookBuilder.class);

            bookBuilder.questionMap = new HashMap<String, Question>();


            //Set the handlers correctly
            for (Question q : bookBuilder.getQuestionList()) {
                bookBuilder.questionMap.put(q.getQuestionIdentifier(),q);
                if (q instanceof SelectionQuestion) {
                    ((SelectionQuestion) q).setHandler(bookBuilder);
                }

                if (q instanceof MultipleChoiceQuestion) {
                    ((MultipleChoiceQuestion) q).setHandler(bookBuilder);
                }


                if (q instanceof CoverSelectQuestion) {
                    ((CoverSelectQuestion) q).setHandler(bookBuilder);
                }

            }
            return bookBuilder;

        }
        return null;
    }

    private List<Question> getQuestionList() {
        return questionList;
    }

    /**
     * A BookBuilder should always be instantiated in a background process, as it makes a
     * web request.
     */
    public static BookBuilder instantiate(AvailableBook availableBook) throws VbHttpException {
        BookBuilder builder = new BookBuilder(availableBook);

        //if this is a secret book, there is no interview to load, just create a loading question
        if(availableBook.getBookType().equals("SECRETS_BOOK") && availableBook.hasSelectables()) {
            builder.addQuestion(
                    new LoadingQuestion(LOADING_QUESTION_ID, availableBook.getLoadingUrl()));

        }
        else{
            builder.loadInterview();
            builder.init();
        }

        return builder;
    }

    private void initFromJson(String json) {
        Gson gson = new Gson();
        Dto dto = gson.fromJson(json, Dto.class);
        response = dto.response;
        init();

    }

    protected BookBuilder(AvailableBook availableBook) {
        this.availableBook = availableBook;
        attributeMap = new HashMap<String, Boolean>();
        selectableMap = new HashMap<Integer, SelectionQuestion.Answer>();
    }

    protected void loadInterview() throws VbHttpException {
        // Acquire a reference to the system Location Manager
        log.debug("loadInterview, book: {}", availableBook.getBookId());

        response = AppSettings.getBookManager().getRestService().getInterview(availableBook.getBookType(), availableBook.getBookId(), AppSettings.getBookManager().getLanguage(), AppSettings.getLastLatitude(), AppSettings.getLastLongitude(), AppSettings.getProfileId());


    }

    protected void setPantryList(InterviewResponse.Pantry[] closestPantries, InterviewResponse.Pantry[] recentPantries) {
         pantries = new ArrayList<Pantry>((closestPantries != null ? closestPantries.length : 0) + (recentPantries != null ? recentPantries.length : 0));
        if (recentPantries != null) {
            for (InterviewResponse.Pantry pantry : recentPantries) {
                pantries.add(new Pantry(pantry.getName(), pantry.getId()));
            }
        }
        if (closestPantries != null) {
            for (InterviewResponse.Pantry pantry : closestPantries) {
                Pantry myPantry = new Pantry(pantry.getName(), pantry.getId());
                if (!pantries.contains(myPantry)) {
                    pantries.add(myPantry);
                }
            }
        }

    }


    public int getNumQuestions() {
        return questionList != null ? questionList.size() : 0;
    }

    public Question getQuestion(int position) {
        return questionList != null ? questionList.get(position) : null;
    }

    public Question getQuestion(String id) {
        return questionMap != null ? questionMap.get(id) : null;
    }


    //implements MultipleChoiceQuestion.Handler
    @Override
    public void handleMCAnswer(MultipleChoiceQuestion question, Map<String, Boolean> answers) {
        attributeMap.putAll(answers);
    }


    public synchronized List<CoverSelectionFragment.PhotoHolder> getAvailablePhotos() {
        return availablePhotos;
    }

    public synchronized void loadAvailablePhotos() throws VbHttpException {
        availablePhotos = null;

        if(availableBook.getBookType().equals("SECRETS_BOOK")){
            availablePhotos = new LinkedList<CoverSelectionFragment.PhotoHolder>();
            for (Question q : questionList) {
                if(q instanceof SelectionQuestion && selectableMap.get(((SelectionQuestion) q).getsId()) != null && selectableMap.get(((SelectionQuestion) q).getsId()).isIncluded()){
                    SelectionQuestion sq = (SelectionQuestion) q;
                    availablePhotos.add(new CoverSelectionFragment.PhotoHolder(sq.getCoverPhotoId(), sq.getPhotoUrl(), false));

                }
            }

        }
        else{

            AvailablePhotosResponse[] responses = AppSettings.getBookManager().getRestService().getAvailablePhotos(AppSettings.getProfileId(), getAvailableBook().getBookId());
            availablePhotos = new ArrayList<CoverSelectionFragment.PhotoHolder>(responses.length);
            for (AvailablePhotosResponse r : responses) {
                availablePhotos.add(new CoverSelectionFragment.PhotoHolder(r.getId(), r.getUrl(), r.getMine()));
            }
        }

    }

    public boolean isComplete() {
        return attributeMap != null && selectableMap != null && coverPhoto != null;
    }

    @Override
    public void handleCoverSelectAnswer(CoverSelectQuestion.Answer photo) {
        this.coverPhoto = photo;
    }




    public void addQuestion(Question question) {
        if (questionList == null) {
            questionList = new ArrayList<Question>();
        }
        if (questionMap == null) {
            questionMap = new HashMap<String, Question>();
        }

        questionList.add(question);

        questionMap.put(question.getQuestionIdentifier(), question);

    }

    public void init() {
        // pantry selection is now done only during printing.
        // /addQuestion(createPantryQuestion());
        //add all the questions we have
        setPantryList(response.getClosestPantries(), response.getRecentPantries());
        AppSettings.setPantryList(pantries);

        for (InterviewResponse.Question responseQuestion : response.getQuestions()) {
            //if the question is hidden, then don't create a question, just put its answers into the
            if (responseQuestion.getQtype().equals(HIDDEN)) {
                for (InterviewResponse.Choice choice : responseQuestion.getChoices()) {
                    attributeMap.put(choice.getAttribute(), choice.getDefaultChoice());
                }
            } else if (responseQuestion.getQtype().equals(MULTICHOICE) || responseQuestion.getQtype().equals(MUTLTICHOICE_NO_SELECTION_REQUIRED)) {
                MultipleChoiceQuestion q = new MultipleChoiceQuestion(this, responseQuestion.getMnemonic(), responseQuestion.getQtype().equals(MULTICHOICE));
                q.setQuestionText(responseQuestion.getIntro());
                for (InterviewResponse.Choice choice : responseQuestion.getChoices()) {
                    q.addChoice(choice.getContent(), choice.getAttribute(), choice.getDefaultChoice());
                }
                addQuestion(q);
            } else {
                log.warn("Recipe Book had unhandled question type: {}", responseQuestion.getQtype());
            }

        }
        //if this book has selectables available, create the LoadSelectablesQuestion
        if (availableBook.hasSelectables()) {
            addQuestion(new LoadingQuestion(LOADING_QUESTION_ID, availableBook.getLoadingUrl()));
        }


    }

    public AvailableBook getAvailableBook() {
        return availableBook;
    }

    public List<String> getAttributeList() {
        List<String> list = new ArrayList<String>(attributeMap.size());
        for (String key : attributeMap.keySet()) {
            if (attributeMap.get(key)) {
                list.add(key);
            }
        }
        return list;
    }

    public Set<Integer> getSelectableIds(){
        return selectableMap.keySet();
    }

    public List<CreateBookRequest.Selectable> getSelectableList() {
        List<CreateBookRequest.Selectable> list = new ArrayList<CreateBookRequest.Selectable>(selectableMap.size());
        for (Integer key : selectableMap.keySet()) {
            SelectionQuestion.Answer answer = selectableMap.get(key);
            list.add(new CreateBookRequest.Selectable(key, answer.isIncluded(), answer.getExtraCopies(), answer.isScrolled()));
        }
        return list;
    }

    public CoverSelectQuestion.Answer getCoverPhoto() {
        return coverPhoto;
    }


    public void shuffleSelectables(SelectableResponse[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private void swap(SelectableResponse[] a, int i, int change) {
        SelectableResponse helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }


    public void clearSelectables(){
        if (selectableMap.size() > 0) {
            questionList.remove(questionMap.remove(COVER_UPLOAD_ID));
            for (Integer questionId : selectableMap.keySet()) {
                questionList.remove(questionMap.remove(SELECTION_QUESTION_FORMAT + questionId));
            }
            selectableMap.clear();
        }
    }

    public void replaceOrCreateSelectables(List<Question> newQuestions){
        //if we already go the selectables, we need to blow them away and get new ones
        if (selectableMap.size() > 0) {
            questionList.remove(questionMap.remove(COVER_UPLOAD_ID));
            questionList.remove(questionMap.remove(SHARE_QUESTION_ID));
            for (Integer questionId : selectableMap.keySet()) {
                questionList.remove(questionMap.remove(SELECTION_QUESTION_FORMAT + questionId));
            }
            selectableMap.clear();
        }

        for(Question question : newQuestions){
            addQuestion(question);
        }
    }

    public List<Question> downloadSelectables() throws VbHttpException, SQLException {
        //Because the way our PagerAdapter works
        List<Question> newQuestions = new LinkedList<Question>();

        //Download selectables from the backend
        SelectableResponse[] responses = AppSettings.getBookManager().getRestService().getSelectables(getAvailableBook().getBookType(), availableBook.getBookId(), getAttributeList());
        shuffleSelectables(responses);

        //Database helper to use for checking and saving off persistent local data
        DatabaseHelper databaseHelper = DatabaseHelper.getDatabaseHelper();
        BookInfo bookInfo = databaseHelper.getBookInfoDao().queryForId(getAvailableBook().getBookId());
        VeggieBook veggieBook = new VeggieBook(bookInfo);
        databaseHelper.getVeggieBookDao().createIfNotExists(veggieBook);

        //remove all recipes from the veggiebook
        for(Selectable s : databaseHelper.getSelectablesDao().queryForEq("veggieBook_id", veggieBook.getId())){
            s.setVeggieBook(null);
            databaseHelper.getSelectablesDao().update(s);
        }

        //now add all the responses into the veggiebook.
        for (int i=0; i<responses.length; i++) {
            SelectableResponse r = responses[i];
            String url;
            String title;

            if (AppSettings.getBookManager().getLanguage().equals("es")) {
                url = r.getUrl_es();
                title = r.getTitle_es();
            } else {
                url = r.getUrl_en();
                title = r.getTitle_en();
            }

            SelectionQuestion question = new SelectionQuestion(this, SELECTION_QUESTION_FORMAT + r.getId(), i+1, responses.length);
            question.setUrl(url);
            question.setQuestionText(title);
            if(BookManager.getBookManager().getLanguage().equals("es") && r.getPhotoUrl_es() != null){
                question.setCoverPhotoId(r.getCoverPhotoId_es());
                question.setPhotoUrl(r.getPhotoUrl_es());
            }
            else{
                question.setCoverPhotoId(r.getCoverPhotoId());
                question.setPhotoUrl(r.getPhotoUrl());
            }
            question.setsId(r.getId());

            newQuestions.add(question);

            //put in a default answer
            question.answer(question.new Answer(true,0,false));


            //Check to see if we already have this selectable
            Dao<Selectable, String> selectables = databaseHelper.getSelectablesDao();
            //This look up is a hack to work around the fact that ormlite does not allow a primary key of multiple columns
            Selectable selectable = selectables.queryForId(String.format("%d/%s", r.getId(), AppSettings.getProfileId()));
            // if so, answer the question
            if(selectable != null){
                //question.answer(question.new Answer(selectable.getSelected(), 0));
                //replace data in case it is different
                Dao<Language, String> languages = databaseHelper.getLanguageDao();
                Language en = languages.queryForId("en");
                Language es = languages.queryForId("es");

                ContentUrl contentUrl = databaseHelper.getContentUrlDao().queryForId(selectable.getUrl().getId());
                ContentUrl titleText = databaseHelper.getContentUrlDao().queryForId(selectable.getTitle().getId());
                ContentUrl shareUrl = databaseHelper.getContentUrlDao().queryForId(selectable.getShareUrl().getId());

                Dao<UrlTranslation, Integer> urlTranslations = databaseHelper.getUrlTranslationDao();
                UrlTranslation contentEn = urlTranslations.queryBuilder().where().eq("contentUrl_id", contentUrl.getId()).and().eq("language_id", en).query().get(0);
                contentEn.setTextString(r.getUrl_en());
                urlTranslations.update(contentEn);
                UrlTranslation contentEs =  urlTranslations.queryBuilder().where().eq("contentUrl_id", contentUrl.getId()).and().eq("language_id", es).query().get(0);
                contentEs.setTextString(r.getUrl_es());
                urlTranslations.update(contentEs);

                UrlTranslation titleEn = urlTranslations.queryBuilder().where().eq("contentUrl_id", titleText.getId()).and().eq("language_id", en).query().get(0);
                titleEn.setTextString(r.getTitle_en());
                urlTranslations.update(contentEn);
                UrlTranslation titleEs =  urlTranslations.queryBuilder().where().eq("contentUrl_id", titleText.getId()).and().eq("language_id", es).query().get(0);
                titleEs.setTextString(r.getTitle_es());
                urlTranslations.update(contentEs);


                UrlTranslation shareUrlEn = urlTranslations.queryBuilder().where().eq("contentUrl_id", shareUrl.getId()).and().eq("language_id", en).query().get(0);
                shareUrlEn.setTextString(r.getShareUrl_en());
                urlTranslations.update(shareUrlEn);
                UrlTranslation shareUrlEs =  urlTranslations.queryBuilder().where().eq("contentUrl_id", shareUrl.getId()).and().eq("language_id", es).query().get(0);
                shareUrlEs.setTextString(r.getShareUrl_es());
                urlTranslations.update(shareUrlEs);

            }
            else {
                //otherwise, we need to put it in our database
                Dao<Language, String> languages = databaseHelper.getLanguageDao();
                Language en = languages.queryForId("en");
                Language es = languages.queryForId("es");

                ContentUrl contentUrl = new ContentUrl();
                ContentUrl titleText = new ContentUrl();
                ContentUrl shareUrl = new ContentUrl();
                databaseHelper.getContentUrlDao().create(contentUrl);
                databaseHelper.getContentUrlDao().create(titleText);
                databaseHelper.getContentUrlDao().create(shareUrl);


                Dao<UrlTranslation, Integer> urlTranslations = databaseHelper.getUrlTranslationDao();
                UrlTranslation contentEn = new UrlTranslation(contentUrl, en, r.getUrl_en());
                urlTranslations.create(contentEn);
                UrlTranslation contentEs = new UrlTranslation(contentUrl, es, r.getUrl_es());
                urlTranslations.create(contentEs);

                UrlTranslation titleEn = new UrlTranslation(titleText, en, r.getTitle_en());
                urlTranslations.create(titleEn);
                UrlTranslation titleEs = new UrlTranslation(titleText, es, r.getTitle_es());
                urlTranslations.create(titleEs);

                UrlTranslation shareUrlEn = new UrlTranslation(shareUrl, en, r.getShareUrl_en());
                urlTranslations.create(shareUrlEn);
                UrlTranslation shareUrlEs = new UrlTranslation(shareUrl, es, r.getShareUrl_es());
                urlTranslations.create(shareUrlEs);


                selectable = new Selectable(r.getId(), AppSettings.getProfileId(), contentUrl, null, titleText, r.getPhotoUrl(), shareUrl);
            }
            selectable.setVeggieBook(veggieBook);

            selectables.createOrUpdate(selectable);

        }

        if(BuildConfig.CHOOSE_EXTRAS) {
            ShareQuestion shareQuestion = new ShareQuestion(this, SHARE_QUESTION_ID);
            newQuestions.add(shareQuestion);
        }

        CoverSelectQuestion coverSelectQuestion = new CoverSelectQuestion(this, COVER_UPLOAD_ID);
        newQuestions.add(coverSelectQuestion);

        //check to see if this veggieBook is an edit, in which case answer the cover select fragment
        if(veggieBook.getCoverImageUrl() != null){
            coverSelectQuestion.answer(new CoverSelectQuestion.Answer(veggieBook.getCoverImageId(), veggieBook.getCoverImageUrl()));
        }
        return newQuestions;
    }

    @Override
    public void handleSelectableAnswer(SelectionQuestion q, SelectionQuestion.Answer answer) {
        selectableMap.put(q.getsId(), answer);

    }

    public String getSelectedPantryId() {
        return selectedPantryId;
    }

    public String toJson() {

        Gson gson = new Gson();
        String json = gson.toJson(this);
        log.debug("{}", json);

        return json;

    }

    @Override
    public void HandleShareAnswer(ShareQuestion.Answer answer) {
    }


    private class Dto {
        public InterviewResponse response;
        public Map<String, Boolean> attributeMap;

    }


}
