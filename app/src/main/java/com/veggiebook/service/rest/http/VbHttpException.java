package com.veggiebook.service.rest.http;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 2/1/13
 * Time: 2:28 PM
 * Exception thrown if http call is unsuccessful
 */
public class VbHttpException extends Exception{

    public VbHttpException(String detailMessage) {
        super(detailMessage);
    }

    public VbHttpException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
