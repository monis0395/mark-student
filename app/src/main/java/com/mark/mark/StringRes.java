package com.mark.mark;

import android.app.Application;

/**
 * Created by Winner 10 on 4/12/2017.
 */

public class StringRes extends Application {

    private String HOSTNAME = "http://172.16.164.175/mark_php/";

    public void setHOSTNAME(String HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    public String getHOSTNAME() {
        return HOSTNAME;
    }
}
