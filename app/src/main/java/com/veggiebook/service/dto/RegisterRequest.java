package com.veggiebook.service.dto;

/**
 * Created with IntelliJ IDEA.
 * User: danieldipasquo
 * Date: 3/13/13
 * Time: 9:44 AM
 *
 */
public class RegisterRequest {
    private String at;
    private String firstName;
    private String lastName;

    public RegisterRequest(String at, String firstName, String lastName) {
        this.at = at;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
