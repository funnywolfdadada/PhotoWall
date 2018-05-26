package com.funnywolf.photowallfallsdemo;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by funnywolf on 18-5-25.
 */

public class OkHttpUtils {
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    public static Response getResponse(String url) {
        Request request = new Request.Builder().url(url).build();
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
        }finally {
            return response;
        }
    }

    public static Response getResponse(Request request) {
        Response response = null;
        try {
            response = mOkHttpClient.newCall(request).execute();
        }finally {
            return response;
        }
    }

    public static ResponseBody getResponseBody(String url) {
        ResponseBody responseBody = null;
        Response response = getResponse(url);
        if(response != null && response.isSuccessful()) {
            responseBody = response.body();
        }
        return responseBody;
    }

    public static ResponseBody getResponseBody(Request request) {
        ResponseBody responseBody = null;
        Response response = getResponse(request);
        if(response != null && response.isSuccessful()) {
            responseBody = response.body();
        }
        return responseBody;
    }
}
