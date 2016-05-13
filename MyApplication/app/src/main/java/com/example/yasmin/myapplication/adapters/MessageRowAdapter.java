package com.example.yasmin.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.yasmin.myapplication.entities.Message;
import com.example.yasmin.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Rafael on 5/9/2016.
 */
public class MessageRowAdapter extends ArrayAdapter<Message> {
    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;
    private String userName;
    private ArrayList<Message> list;


    public MessageRowAdapter(Context context, ArrayList<Message> list) {
        super(context, ROW_LAYOUT, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.adp__message_row, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        Message message = list.get(position);

        if (message != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView content = (TextView) v.findViewById(R.id.adp__message_row_content);
            TextView client = (TextView) v.findViewById(R.id.adp__message_row_client);
            TextView status = (TextView) v.findViewById(R.id.adp__message_row_status);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (content != null){
                content.setText(message.getMessageContent());
            }
            if (client != null){
                client.setText(message.getClientName());
            }
            if (status != null){
                String statusMsg = message.isSent() ? "sent" : "pending";
                status.setText(statusMsg);
            }
        }

        // the view must be returned to our activity
        return v;
    }
}
