package com.example.yasmin.myapplication.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

//import org.json.*;
import com.example.yasmin.myapplication.adapters.MessageRowAdapter;
import com.example.yasmin.myapplication.entities.Client;
import com.example.yasmin.myapplication.entities.Message;
import com.example.yasmin.myapplication.R;
import com.example.yasmin.myapplication.receivers.BCastReceiver;
import com.example.yasmin.myapplication.services.HttpServiceWrapper;
import com.example.yasmin.myapplication.utils.App;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();

    private static final int PREFERENCES_REQUEST = 1;
    private static final int BROADCAST_REQUEST = 2;

    private static final long INTERVAL_FIVE_SECONDS = 5 * 1000;
    private static final long INTERVAL_TEN_SECONDS = 2 * INTERVAL_FIVE_SECONDS;

    private static ArrayList<Client> clientArray;
    private static ArrayList<Message> messageArray;
    private int userId;
    private String userName;
    private int lastMessageId;

    private static MessageRowAdapter messageRowAdapter;
    private ListView messageListView;
    private EditText newMessageEditText;
    private ImageButton sendButton;
    private SharedPreferences sharedPreferences;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layt__main);
        messageListView = (ListView) findViewById(R.id.layt_main_lst_messages);
        newMessageEditText = (EditText) findViewById(R.id.layt_main_message);
        sendButton = (ImageButton) findViewById(R.id.layt_main_btn_send);

        clientArray = new ArrayList<>();
        messageArray = new ArrayList<>();
        alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        messageRowAdapter = new MessageRowAdapter(this, messageArray);
        messageListView.setAdapter(messageRowAdapter);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        lastMessageId = sharedPreferences.getInt(App.PREF_KEY_LAST_MSG_ID, 0);
        userId = sharedPreferences.getInt(App.PREF_KEY_USERID, -1);
        if (sharedPreferences.contains(App.PREF_KEY_USERNAME))
            userName = sharedPreferences.getString(App.PREF_KEY_USERNAME, "anonymous");
        else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            userName = "anonymous";
            editor.putString(App.PREF_KEY_USERNAME, userName);
            editor.apply();
        }

        if (userId == -1) {
            sendClient();
        }

        getClientList();
        getMessageList(true);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = newMessageEditText.getText().toString();
                userId = sharedPreferences.getInt(App.PREF_KEY_USERID, 0);
                userName = sharedPreferences.getString(App.PREF_KEY_USERNAME, "anonymous");
                sendMessage(new Message(msg, userId, userName, false));
                newMessageEditText.setText("");

            }
        });

    }


    @Override
    protected void onStart() {
        if (alarmIntent == null) {
            Log.d("ALARM", "starting alarm manager");
            Intent intent = new Intent(this, SyncAlarmReceiver.class);
//            Intent intent = new Intent(this, BCastReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(this, this.BROADCAST_REQUEST, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    INTERVAL_FIVE_SECONDS,
//                    INTERVAL_FIVE_SECONDS, alarmIntent);
//            long thirtySecondsFromNow = System.currentTimeMillis() + INTERVAL_FIVE_SECONDS;
//            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, thirtySecondsFromNow,  INTERVAL_FIVE_SECONDS, alarmIntent);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis() + INTERVAL_FIVE_SECONDS,
                    INTERVAL_FIVE_SECONDS,
                    alarmIntent);

//            alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime()+INTERVAL_FIVE_SECONDS,
//                    INTERVAL_FIVE_SECONDS,
//                    alarmIntent);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.HOUR_OF_DAY, 14);
//            long triggerAtMillis = calendar.getTimeInMillis();
//
//            long intervalMillis = 10 * 1000;
//            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, alarmIntent);
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        if (alarmIntent != null) {
            alarmMgr.cancel(alarmIntent);
            alarmIntent = null;
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_preferences:
                startActivityForResult(new Intent(this, SettingsActivity.class), PREFERENCES_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case PREFERENCES_REQUEST:
                sendClient();
                break;
        }
    }

    private void getClientList() {
        HttpServiceWrapper.getClientList(this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject object = null;

                for (int i = 0; i < response.length(); i++) {
                    try {
                        object = (JSONObject) response.get(i);
                        clientArray.add(new Client(object));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "I got " + clientArray.size() + " clients.");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't load the client list.", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void sendClient() {
        HttpServiceWrapper.sendClient(this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "post success");
                try {
                    Client c = new Client(response);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(App.PREF_KEY_USERID, c.getClientId());
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't send the client info.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMessageList() {
        getMessageList(false);
    }

    private void getMessageList(final boolean allMessages) {
        lastMessageId = 0;
        if (!allMessages)
            lastMessageId = sharedPreferences.getInt(App.PREF_KEY_LAST_MSG_ID, 0);
        HttpServiceWrapper.getMessageList(this, lastMessageId, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                JSONObject object = null;
                int start = 1;
                if (allMessages) {
                    messageArray.clear();
                    start = 0;
                }
                for (int i = start; i < response.length(); i++) {
                    try {
                        object = (JSONObject) response.get(i);
                        messageArray.add(new Message(object));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                messageRowAdapter.notifyDataSetChanged();
                if (messageArray.size() > 0) {
                    lastMessageId = messageArray.get(messageArray.size() - 1).getMessageId();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(App.PREF_KEY_LAST_MSG_ID, lastMessageId);
                    editor.apply();
                }
                Log.d(TAG, "I got " + messageArray.size() + " message.");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't load the message list.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendMessage(final Message message) {
//        message.setClientName(userName);
//        message.setSent(false);
        messageArray.add(message);
        messageRowAdapter.notifyDataSetChanged();

        HttpServiceWrapper.sendMessage(this, message, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "post success");
                messageArray.remove(message);
                JSONObject object = null;
                int start = 1;
                lastMessageId = sharedPreferences.getInt(App.PREF_KEY_LAST_MSG_ID, 0);
                if (lastMessageId == 0)
                    start = 0;
                for (int i = start; i < response.length(); i++) {
                    try {
                        object = (JSONObject) response.get(i);
                        messageArray.add(new Message(object));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (messageArray.size() > 0) {
                    lastMessageId = messageArray.get(messageArray.size() - 1).getMessageId();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(App.PREF_KEY_LAST_MSG_ID, lastMessageId);
                    editor.apply();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                messageRowAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry, I couldn't send the new message.", Toast.LENGTH_LONG).show();
            }
        });
    }


    static public class SyncAlarmReceiver extends BroadcastReceiver {
        private final String TAG = SyncAlarmReceiver.class.getCanonicalName();

        public SyncAlarmReceiver() {
        }

        private void getMessageList(final Context context, final boolean allMessages) {
            final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            final ArrayList<Message> messageArray = MainActivity.messageArray;
            final MessageRowAdapter messageRowAdapter = MainActivity.messageRowAdapter;
            int lastMessageId = 0;
            if (!allMessages)
                lastMessageId = sharedPreferences.getInt(App.PREF_KEY_LAST_MSG_ID, 0);
            HttpServiceWrapper.getMessageList(context, lastMessageId, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    JSONObject object = null;
                    int start = 1;
                    if (allMessages) {
                        messageArray.clear();
                        start = 0;
                    }
                    for (int i = start; i < response.length(); i++) {
                        try {
                            object = (JSONObject) response.get(i);
                            messageArray.add(new Message(object));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    messageRowAdapter.notifyDataSetChanged();
                    if (messageArray.size() > 0) {
                        int lastMessageId = messageArray.get(messageArray.size() - 1).getMessageId();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(App.PREF_KEY_LAST_MSG_ID, lastMessageId);
                        editor.apply();
                    }
                    Log.d(TAG, "I got " + messageArray.size() + " message.");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {
                    Toast.makeText(context, "Sorry, I couldn't load the message list.", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Alarm executed at: " + new java.util.Date());
            getMessageList(context, false);
//            if (isOnline(context)) {
//                ServiceHelper serviceHelper = new ServiceHelper();
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                long userId = prefs.getLong(App.PREF_KEY_USERID, App.PREF_DEFAULT_USER_ID);
//                long lastMessageSeqNum = prefs.getLong(App.PREF_KEY_LAST_SEQNUM, App.PREF_DEFAULT_LAST_SEQNUM);
//                String userName = prefs.getString(App.PREF_KEY_USERNAME, App.PREF_DEFAULT_USER_NAME);
//                double userLatitude = (double) prefs.getFloat(App.PREF_KEY_LATITUDE, 0);
//                double userLongitude = (double) prefs.getFloat(App.PREF_KEY_LONGITUDE, 0);
//
//                String uuidString = prefs.getString(App.PREF_KEY_REGISTRATION_ID, "");
//                if (!uuidString.isEmpty()) {
//                    UUID registrationID = UUID.fromString(uuidString);
//                    Peer peer = new Peer(userId, userName, userLatitude, userLongitude);
//                    serviceHelper.syncAsync(context, registrationID, peer, lastMessageSeqNum, new ArrayList<Message>());
//                }
//            }
        }

    }

}



/*
* JSONObject jsonParams = new JSONObject();
        jsonParams.put("notes", "Test api support");
        StringEntity entity = new StringEntity(jsonParams.toString());
        client.post(context, restApiUrl, entity, "application/json",
                responseHandler);
*
* */

