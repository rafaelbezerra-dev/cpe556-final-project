from flask import Flask, request, Response, json, jsonify
from flask_restful import reqparse, abort, Api, Resource
from contracts import *
from client_api import *
from threading import *

def abort_if_message_doesnt_exist(message_id):
    if message_id not in MESSAGES:
        abort(404, message="Message {} doesn't exist".format(message_id))


class MessageAPI(Resource):
    @staticmethod
    def register_routes(api):
        api.add_resource(MessageAPI, '/message/<message_id>')

    def get(self, message_id):
        abort_if_message_doesnt_exist(message_id)
        return MESSAGES[message_id].serialize()


class MessageListAPI(Resource):
    @staticmethod
    def register_routes(api):
        api.add_resource(MessageListAPI, '/messages')

    def get(self):
        last_message = 0
        if "last_message" in request.args:
            last_message = int(request.args["last_message"])
        message_list = MESSAGES.values()[last_message:]
        serialized_list = [o.serialize() for o in message_list]
        return serialized_list

    def post(self):
        lock.acquire()
        try:
            last_message = 0
            if "last_message" in request.args:
                last_message = int(request.args["last_message"])

            client_id = int(request.json['clientId'])
            abort_if_client_doesnt_exist(client_id)

            message_content = request.json['messageContent']
            client_name = CLIENTS[client_id].client_name

            message_id = len(MESSAGES)
            MESSAGES[message_id] = Message(message_id, message_content, client_id, client_name)

            message_list = MESSAGES.values()[last_message:]
            serialized_list = [o.serialize() for o in message_list]
            return serialized_list, 201

        finally:
            lock.release()
