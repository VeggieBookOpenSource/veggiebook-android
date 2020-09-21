package com.veggiebook.service.rest;

import android.location.Location;

import com.google.gson.Gson;
import com.veggiebook.service.dto.AvailablePhotosRequest;
import com.veggiebook.service.dto.AvailablePhotosResponse;
import com.veggiebook.service.dto.CreateBookRequest;
import com.veggiebook.service.dto.CreateBookResponse;
import com.veggiebook.service.dto.InterviewRequest;
import com.veggiebook.service.dto.InterviewResponse;
import com.veggiebook.service.dto.LibraryInfoResponse;
import com.veggiebook.service.dto.PantriesResponse;
import com.veggiebook.service.dto.PrintRequest;
import com.veggiebook.service.dto.PrintResponse;
import com.veggiebook.service.dto.RecordEventRequest;
import com.veggiebook.service.dto.RegisterRequest;
import com.veggiebook.service.dto.RegisterResponse;
import com.veggiebook.service.dto.SelectableResponse;
import com.veggiebook.service.dto.SelectablesRequest;
import com.veggiebook.service.rest.http.VBHttpUtil;
import com.veggiebook.service.rest.http.VbHttpException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/1/13
 * Time: 2:16 PM
 * Default implementation of veggiebook rest endpoint service
 */

public class VBRestServiceImpl implements VBRestService{
    private String host;
    private String protocol;
    private VBHttpUtil httpUtil;
    private static Logger log = LoggerFactory.getLogger(VBRestServiceImpl.class);

    private final static String LIBRARY_INFO_ENDPOINT="/qhmobile/libraryInfo";
    private final static String GET_INTERVIEW_ENDPOINT="/qhmobile/getInterview";
    private final static String GET_SELECTABLES_ENDPOINT="/qhmobile/getSelectables";
    private final static String AVAILABLE_PHOTOS_ENDPOINT="/qhmobile/availableCoverPhotos/";
    private final static String UPLOAD_IMAGE_ENDPOINT="/qhmobile/uploadCoverPhoto/";
    private final static String REGISTER_ENDPOINT="/qhmobile/register/";
    private final static String CREATE_VEGGIEBOOK_ENDPOINT="/qhmobile/createVeggieBook/";
    private final static String PRINT_VEGGIEBOOK_ENDPOINT="/qhmobile/printVeggieBook/";
    private final static String PANTRIES_ENDPOINT="/qhmobile/pantries/";
    private final static String RECORD_EVENT_ENDPOINT="/qhmobile/recordEvent/";


    private final static String CRLF = "\r\n";
    private final static String TWO_HYPHENS = "--";
    private final static String BOUNDRY =  "*****";

    public VBRestServiceImpl(String host, String protocol) {
        this.host = host;
        this.protocol = protocol;

        httpUtil = new VBHttpUtil();
    }

    private URL getUrl(String endpoint) throws MalformedURLException {
        return new URL(protocol, host, endpoint);

    }

    @Override
    public LibraryInfoResponse getLibraryInfo() throws VbHttpException {
        log.info("Getting Library Info");
        try{
            return httpUtil.postJson(getUrl(LIBRARY_INFO_ENDPOINT),null,null,LibraryInfoResponse.class);
        }
        catch(MalformedURLException e){
            throw new VbHttpException(e.getMessage(),e);
        }
    }

    @Override
    public InterviewResponse getInterview(String bookType, String bookId, String languageId, double lat, double lon, String profileId) throws VbHttpException {
        InterviewRequest requestDTO = new InterviewRequest(bookType,bookId,languageId,lat,lon,profileId);
        Gson gson = new Gson();
        String request = gson.toJson(requestDTO);
        try{
            return httpUtil.postJson(getUrl(GET_INTERVIEW_ENDPOINT),null,request,InterviewResponse.class);
        } catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }
    }

    @Override
    public SelectableResponse[] getSelectables(String bookType, String bookId, List<String> attributes) throws  VbHttpException{
        SelectablesRequest requestDTO = new SelectablesRequest(bookType,bookId,attributes);
        Gson gson = new Gson();
        String request = gson.toJson(requestDTO);
        try{
            return httpUtil.postJson(getUrl(GET_SELECTABLES_ENDPOINT),null,request,SelectableResponse[].class);
        } catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }
    }

    @Override
    public AvailablePhotosResponse[] getAvailablePhotos(String profileId, String bookId) throws  VbHttpException {
        AvailablePhotosRequest requestDTO = new AvailablePhotosRequest(profileId, bookId);
        Gson gson = new Gson();
        String request = gson.toJson(requestDTO);
        try{
            return httpUtil.postJson(getUrl(AVAILABLE_PHOTOS_ENDPOINT),null,request, AvailablePhotosResponse[].class);
        }   catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }

    }

    @Override
    public CreateBookResponse createVeggieBook(String profileId, String bookId, String bookType, List<String> attributes, List<CreateBookRequest.Selectable> selectables, String photoId, String language, String pantryId, Location location) throws VbHttpException {
        CreateBookRequest requestDTO = new CreateBookRequest(profileId, bookType, bookId, language, attributes.toArray(new String[attributes.size()]),selectables.toArray(new CreateBookRequest.Selectable[selectables.size()]), photoId, pantryId, location);
        Gson gson = new Gson();
        String request = gson.toJson(requestDTO);
        try{
            return httpUtil.postJson(getUrl(CREATE_VEGGIEBOOK_ENDPOINT),null,request, CreateBookResponse.class);
        }   catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }
    }

    @Override
    public PrintResponse printVeggieBook(String veggieBookId, String language, String pantryId, String type) throws VbHttpException {
        PrintRequest requestDTO = new PrintRequest(language, veggieBookId, pantryId, type);
        Gson gson = new Gson();
        String request = gson.toJson(requestDTO);
        try{
            return httpUtil.postJson(getUrl(PRINT_VEGGIEBOOK_ENDPOINT),null,request, PrintResponse.class);
        }   catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }
    }

    @Override
    public void recordEvent(RecordEventRequest requestDto) throws VbHttpException {
        Gson gson = new Gson();
        String request = gson.toJson(requestDto);
        try{
            httpUtil.postJson(getUrl(RECORD_EVENT_ENDPOINT),null,request, Void.class);
        }
        catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }
    }

    @Override
    public PantriesResponse[] pantries() throws VbHttpException {
        try{
            return httpUtil.postJson(getUrl(PANTRIES_ENDPOINT),null,null,PantriesResponse[].class);
        }
        catch(MalformedURLException e){
            throw new VbHttpException(e.getMessage(),e);
        }    }

    private void copy(InputStream input,
                      OutputStream output)
            throws IOException {
        byte[] buf = new byte[4*1024];
        int bytesRead = input.read(buf);
        while (bytesRead != -1) {
            output.write(buf, 0, bytesRead);
            bytesRead = input.read(buf);
        }
        output.flush();
    }

    @Override
    public AvailablePhotosResponse uploadCoverImage(String profileId, String filePath) throws VbHttpException {
        HttpURLConnection httpUrlConnection = null;
        try {
            URL url = getUrl(UPLOAD_IMAGE_ENDPOINT);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + this.BOUNDRY);

            DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());

            String attachmentName = new File(filePath).getName();
            request.writeBytes(this.TWO_HYPHENS + this.BOUNDRY + this.CRLF);
            request.writeBytes("Content-Disposition: form-data; name=\"img\";filename=\"" + attachmentName + "\"" + this.CRLF);
            request.writeBytes(this.CRLF);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            copy(new FileInputStream(filePath), baos);
            request.write(baos.toByteArray());
            baos.close();
            request.writeBytes(this.CRLF);
            request.writeBytes(this.TWO_HYPHENS + this.BOUNDRY + this.CRLF);
            request.writeBytes("Content-Disposition: form-data; name=\"owner\"" + this.CRLF);
            request.writeBytes(this.CRLF);
            request.writeBytes(profileId);
            request.writeBytes(this.TWO_HYPHENS + this.BOUNDRY + this.CRLF);
            request.flush();
            request.close();

            //check the response code
            int response = httpUrlConnection.getResponseCode();
            if(response != HttpURLConnection.HTTP_OK){
                String msg = httpUrlConnection.getResponseMessage();
                log.error("HTTP "+response + ": {}", msg!=null?msg:"");
                throw new VbHttpException("HTTP "+response + ": " + msg!=null?msg:"");
            }

            log.debug("HTTP 200 ok.");

            //get the data
            String encoding = httpUrlConnection.getContentEncoding();
            encoding = encoding == null ? "UTF-8" : encoding;
            String responseString = IOUtils.toString(httpUrlConnection.getInputStream(), encoding);

            if(responseString==null){
                log.debug("Response: null");
                return null;
            }

            log.debug("Response: {}", responseString );

            Gson gson = new Gson();

            return gson.fromJson(responseString, AvailablePhotosResponse.class);
        } catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        } catch (IOException e) {
            throw new VbHttpException(e.getMessage(),e);
        }

    }

    @Override
    public RegisterResponse register(String token, String firstName, String lastName) throws VbHttpException {
        RegisterRequest dto = new RegisterRequest(token,firstName,lastName);
        Gson gson = new Gson();
        String request = gson.toJson(dto);
        try{
            return httpUtil.postJson(getUrl(REGISTER_ENDPOINT),null,request,RegisterResponse.class);
        } catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        }
    }


}
