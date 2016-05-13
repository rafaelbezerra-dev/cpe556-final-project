package com.example.yasmin.myapplication.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yasmin on 5/8/2016.
 */
public class Client {
    int clientId;
    String clientName;

    public Client(String clientName) {
        this.clientName = clientName;
    }

    public Client(int id, String name) {
        clientId = id;
        clientName = name;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject json = new JSONObject();
//        json.put("clientId", clientId);
        json.put("clientName", clientName);

        return json;
    }

    public Client(JSONObject object) throws JSONException {
        clientId = object.getInt("clientId");
        clientName = object.getString("clientName");
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName(){
        return clientName;
    }

}
