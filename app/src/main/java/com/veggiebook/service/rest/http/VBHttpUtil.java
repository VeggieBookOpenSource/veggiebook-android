package com.veggiebook.service.rest.http;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/1/13
 * Time: 2:23 PM
 * A utility for very simple HTTP handling
 */
public class VBHttpUtil {

    private static Logger log = LoggerFactory.getLogger(VBHttpUtil.class);

    private void setDefaultHeaders(HttpURLConnection urlConnection){
        urlConnection.setRequestProperty("Content-type", "application/json; charset=utf-8");
    }

    public <T> T postJson(URL url, Map<String, String> headers, String content, Class<T> dtoClazz) throws  VbHttpException{
        log.info("postJson", "Sending HTTP POST");
        log.info("postJson", "URL: {}", url.toString());
        log.info("postJson", "Content: {}",  content!=null?content:"");
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try{
                //Set POST as method
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                //send the content
                if(content==null) content="";
                byte[] bytes = content.getBytes("UTF-8");
                urlConnection.setFixedLengthStreamingMode(bytes.length);



                //Set default headers
                setDefaultHeaders(urlConnection);

                //Add the headers to the request
                if(headers != null){
                    for(String key : headers.keySet()){
                        urlConnection.setRequestProperty(key, headers.get(key));
                    }
                }

                //not needed to be called explicitly, but it makes it more clear whats going on.
                urlConnection.connect();


                OutputStream out = null;
                try{
                    out = urlConnection.getOutputStream();
                    out.write(bytes);
                }finally {
                    if(out!=null) out.close();
                }

                //check the response code
                int response = urlConnection.getResponseCode();
                if(response != HttpURLConnection.HTTP_OK){
                    String msg = urlConnection.getResponseMessage();
                    log.error("postJson", "HTTP "+response + ": {}", msg!=null?msg:"");
                    throw new VbHttpException("HTTP "+response + ": " + msg!=null?msg:"");
                }

                log.info("HTTP 200 ok.");

                //get the data
                String encoding = urlConnection.getContentEncoding();
                encoding = encoding == null ? "UTF-8" : encoding;
                String responseString = IOUtils.toString(urlConnection.getInputStream(),encoding);
                log.info(responseString);
                if(responseString==null){
                    log.debug("Response: null");
                    return null;
                }

                log.info("postJson", "Response: {}", responseString );

                if(dtoClazz.isInstance(Void.class)){
                    return null;
                }

                Gson gson = new Gson();
                return gson.fromJson(responseString,dtoClazz);

            }
            finally {
                urlConnection.disconnect();
            }



        } catch (MalformedURLException e) {
            throw new VbHttpException(e.getMessage(),e);
        } catch (IOException e) {
            throw new VbHttpException(e.getMessage(),e);
        }

    }



}
