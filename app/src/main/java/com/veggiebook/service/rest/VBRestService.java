package com.veggiebook.service.rest;

import android.location.Location;

import com.veggiebook.service.dto.AvailablePhotosResponse;
import com.veggiebook.service.dto.CreateBookRequest;
import com.veggiebook.service.dto.CreateBookResponse;
import com.veggiebook.service.dto.InterviewResponse;
import com.veggiebook.service.dto.LibraryInfoResponse;
import com.veggiebook.service.dto.PantriesResponse;
import com.veggiebook.service.dto.PrintResponse;
import com.veggiebook.service.dto.RecordEventRequest;
import com.veggiebook.service.dto.RegisterResponse;
import com.veggiebook.service.dto.SelectableResponse;
import com.veggiebook.service.rest.http.VbHttpException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/1/13
 * Time: 2:10 PM
 * Interface for the implementation of a service for veggiebook rest endpoints
 */
public interface VBRestService {

    public LibraryInfoResponse getLibraryInfo() throws VbHttpException;
    public InterviewResponse getInterview(String bookType, String bookId, String languageId, double lat, double lon, String profileId) throws  VbHttpException;
    public SelectableResponse[] getSelectables(String bookType, String bookId, List<String> attributes) throws  VbHttpException;
    public AvailablePhotosResponse uploadCoverImage(String profileId, String filePath) throws VbHttpException;
    public RegisterResponse register(String token, String firstName, String lastName)throws VbHttpException;
    public AvailablePhotosResponse[] getAvailablePhotos(String profileId, String bookId) throws  VbHttpException;
    public CreateBookResponse createVeggieBook(String profileId, String bookId, String bookType, List<String> attributes, List<CreateBookRequest.Selectable> selectables, String photoId, String language, String pantryId, Location location) throws  VbHttpException;
    public PantriesResponse[] pantries() throws VbHttpException;
    public PrintResponse printVeggieBook(String veggieBookId, String language, String pantryId, String type) throws VbHttpException;
    public void recordEvent(RecordEventRequest request) throws VbHttpException;
}
