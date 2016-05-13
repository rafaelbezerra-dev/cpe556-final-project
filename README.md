# CPE 556 - Final Project

The repo contains all the code for the Final Project of CPE 556 - Computing Principles of Embedded Systems.

The project consists of a simple server with RESTful API that supports users to register as clients and exchange message with other user already registered. The project also has a simple Android App to act as the client-side and allow users to properly register (and change their usernames) and send/view messages.

The goal was to build a TCP client and server and have them communicating over the WLAN.

## Documentation
A more detailed documentation on the client an the server can be found at the following links:
* [Client](https://github.com/rafaelbezerra-dev/cpe556-final-project/tree/master/MyApplication)
* [Server](https://github.com/rafaelbezerra-dev/cpe556-final-project/tree/master/flask-server)

## Tests

The client-side of the project was tested on the following devices:
* Google Nexus 7 – 2013 version (Android 6.0.1)
* Google Nexus 6P (Android 6.0.1)
* Galaxy Nexus (Cyanogenmod 11)

And the server side was tested on a Raspberry PI 3.
## Future Work
Future work may include:
* Persist the messages and clients using SQLite3 Database
* Add a few more functions to the chat room
* Analyze how this application could differ from their competitors* 

