package com.mark.mark;

import android.app.Application;

/**
 * Created by Winner 10 on 4/12/2017.
 */

public class StringRes extends Application {

    private String HOSTNAME = "http://10.120.112.23/mark_php/";

    public void setHOSTNAME(String HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    public String getHOSTNAME() {
        return HOSTNAME;
    }
}
