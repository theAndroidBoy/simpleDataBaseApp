package com.easyapps.simpleDataBase;


public class ExperimentHelper {

    public static final String TAG = "flow";

    public static void smallBreak()
    {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
