package com.dollarandtrump.angelcar.utils;

/**
 * Created by humnoyDeveloper on 3/18/2016 AD. 14:50
 */
public class RegistrationResult {
    private int result; // สถานะการทำงาน EventBus
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RegistrationResult() {
    }

    public RegistrationResult(int result,String email) {
        this.result = result;
        this.email = email;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
