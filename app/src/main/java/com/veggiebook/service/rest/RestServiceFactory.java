package com.veggiebook.service.rest;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/1/13
 * Time: 2:14 PM
 * Used to get an implementation of a rest service
 */
public class RestServiceFactory {

    public static VBRestService getRestService(String host, String protocol){
        return new VBRestServiceImpl(host,protocol);
    }
}
