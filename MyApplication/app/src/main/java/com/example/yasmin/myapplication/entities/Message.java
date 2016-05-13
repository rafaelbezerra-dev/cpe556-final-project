package com.example.yasmin.myapplication.entities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Yasmin on 5/8/2016.
 */
public class Message {
    int messageId;
    int clientId;
    String messageContent;
    String clientName;
    boolean isSent;

    public Message(String text, int client) {
        clientId = client;
        messageContent = text;
    }

    public Message(String messageContent, int clientId, String clientName, boolean isSent) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.isSent = isSent;
        this.messageContent = messageContent;
    }

    public Message(JSONObject object) throws JSONException {
        messageId = object.getInt("messageId");
        clientId = object.getInt("clientId");
        messageContent = object.getString("messageContent");
        clientName = object.getString("clientName");
        isSent = true;
    }

    public JSONObject serialize() throws JSONException {
        JSONObject json = new JSONObject();
//        json.put("messageId", messageId);
        json.put("clientId", clientId);
        json.put("messageContent", messageContent);
//        json.put("clientName", clientName);

        return json;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
