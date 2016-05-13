# The Client

#### Introduction
The client was built using the Android Studio IDE v2.1 and the Minimum SDK Version was set to be 23 (the lastest API at the moment of the document).

The app consists of a main activity running an Alarm Manager that triggers the methods to request messages from the server every 60 seconds. The Android Asynchronous Http Client, from James Smith, was using to handle the http requests to the server. The json parsing is manually handle inside of each model class.

In adition, a Settings activity was built for the user to change his name. Since the app auto-registers itself when it is launched for the first time, this allows the user to change update his name from "anonymous" (default name) to whatever he wants.

#### Software Structure
* The *activities* package contains the MainActivity and the SettingsActivity.
* The *adapter* package contains the implementation of the list adapter used in the MainActivity message list.
* The *entites* package contains the class implementations of the system model (Client and Message).
* The *HttpServiceWrapper* is a wrapper for the HttpClient used that builds and perform HTTP requests.
* The *App* class has all the static constants shared by the application.

## Running the Client
To run the client you either gonna need an android phone on emulator.

Before installing the app, change the value of the static constant *DEFAULT_HOST_URI* inside of the [App](https://github.com/rafaelbezerra-dev/cpe556-final-project/blob/master/MyApplication/app/src/main/java/com/example/yasmin/myapplication/utils/App.java) class. If you desire to run this locally, the loopback address for the android emulator is `10.0.2.2`. Otherwise, use the same IP that was used to launch the server.

To install the app on a connected device or running emulator, run the following command:
```bash
$ adb -s [device] install app-localchat-debug.apk
```

## References
* http://developer.android.com/
* http://loopj.com/android-async-http/