package com.example.yasmin.myapplication.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.yasmin.myapplication.entities.Client;
import com.example.yasmin.myapplication.entities.Message;
import com.example.yasmin.myapplication.utils.App;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by Rafael on 5/9/2016.
 */
public class HttpServiceWrapper {

    public static void getClientList(Context context, JsonHttpResponseHandler handler) {
//        String uri = App.DEFAULT_HOST_URI + "/clients";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");

        Uri uri = Uri.parse(App.DEFAULT_HOST_URI + "/clients?" + params);
        client.get(uri.toString(), params, handler);
    }

    public static void getMessageList(Context context, int lastMessageId, JsonHttpResponseHandler handler) {
//        String url = App.DEFAULT_HOST_URI + "/messages";
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("last_message", lastMessageId);

        Uri uri = Uri.parse(App.DEFAULT_HOST_URI + "/messages?" + params);
        client.get(uri.toString(), handler);
    }

    public static void sendClient(Context context, JsonHttpResponseHandler handler) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int userId = sharedPreferences.getInt(App.PREF_KEY_USERID, -1);
        String userName = sharedPreferences.getString(App.PREF_KEY_USERNAME, "anonymous");

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");

        StringEntity entity = null;
        try {
            JSONObject jsonParams = new Client(userName).serialize();
            entity = new StringEntity(jsonParams.toString(), App.DEFAULT_ENCODING);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (userId == -1) { // REGISTER A NEW CLIENT IN THE SERVER
            Uri uri = Uri.parse(App.DEFAULT_HOST_URI + "/clients?" + params);
            client.post(context, uri.toString(), entity, App.DEFAULT_CONTENT_TYPE, handler);
        } else { // UPDATE AN EXISTING CLIENT IN THE SERVER
            Uri uri = Uri.parse(App.DEFAULT_HOST_URI + "/client/" + userId + "?" + params);
            client.put(context, uri.toString(), entity, App.DEFAULT_CONTENT_TYPE, handler);
        }
    }

    public static void sendMessage(Context context, Message message, JsonHttpResponseHandler handler) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int lastMessageId = sharedPreferences.getInt(App.PREF_KEY_LAST_MSG_ID, 0);
//        String uri = App.DEFAULT_HOST_URI + "/messages";
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("q", "android");
        params.put("last_message", lastMessageId);

        StringEntity entity = null;
        try {
            JSONObject jsonParams = message.serialize();
            entity = new StringEntity(jsonParams.toString(), App.DEFAULT_ENCODING);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        entity.setContentType(App.DEFAULT_CONTENT_TYPE);
        Uri uri = Uri.parse(App.DEFAULT_HOST_URI + "/messages?" + params);
        client.post(context, uri.toString(), entity, App.DEFAULT_CONTENT_TYPE, handler);
    }
}
