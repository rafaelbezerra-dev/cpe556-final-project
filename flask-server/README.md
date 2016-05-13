# Project Server

#### Introduction
The server for the project was build using Python v2.7, the Flask Framework v0.10.1 and Flask's library, Flask-RESTful v0.3.5.

It has a list of clients (registered users) and messages. Each user can have as many messages as him wants. These lists are kept in memory for simplicity purposes, but an integration with a SQLite database can be easily developed due to the use of the Flask Framework, that supports the integration with the database.

#### Software Structure
* **client_api.py**: Contains the routes and the services for the client API.
* **message_api.py**: Contains the routes and the services for the message API.
* **contracts.py**: Contains the list of clients and messages and also has the implementations of these objects' classes.
* **main.py**: Main program that registers the routes for the services and starts the server.

## Dependencies
Assuming the system has Python 2.7 already installed.

```bash
$ sudo apt-get install python-pip python-dev build-essential
$ sudo pip install Flask
$ sudo pip install flask-restful
```

## Running the Server
#### Usage
```bash
$ python main.py [IP ADDRESS] [PORT]
```
#### Example
```bash
$ cd flask-server
$ python main.py 155.246.76.25 8000
```

## References
* http://flask.pocoo.org/
* http://flask-restful-cn.readthedocs.io/en/0.3.4/installation.html
* http://www.bradcypert.com/writing-a-restful-api-in-flask-sqlalchemy/
