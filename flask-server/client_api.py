from flask import Flask, request, Response, json, jsonify
from flask_restful import reqparse, abort, Api, Resource
from contracts import *


def abort_if_client_doesnt_exist(client_id):
    if client_id not in CLIENTS:
        abort(404, message="Client {} doesn't exist".format(client_id))


class ClientAPI(Resource):
    @staticmethod
    def register_routes(api):
        # api.add_resource(self, '/clients')
        api.add_resource(ClientAPI, '/client/<int:client_id>')

    def get(self, client_id):
        abort_if_client_doesnt_exist(client_id)
        return CLIENTS[client_id].serialize()

    def put(self, client_id):
        lock.acquire()
        try:
            abort_if_client_doesnt_exist(client_id)
            CLIENTS[client_id].client_name = request.json['clientName']
            return CLIENTS[client_id].serialize()
        finally:
            lock.release()


class ClientListAPI(Resource):
    @staticmethod
    def register_routes(api):
        api.add_resource(ClientListAPI, '/clients')
        # api.add_resource(self, '/client/<client_id>')

    def get(self):
        l = [o.serialize() for o in CLIENTS.values()]
        return l


    def post(self):
        lock.acquire()
        try:
            req = request
            client_id = len(CLIENTS)
            CLIENTS[client_id] = Client(client_id, request.json['clientName'])
            return CLIENTS[client_id].serialize(), 201
        finally:
            lock.release()