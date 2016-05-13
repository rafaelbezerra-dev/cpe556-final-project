from flask import Flask
from flask_restful import Api
import sys
from client_api import *
from message_api import *

def add_routes(api):
    ClientAPI.register_routes(api)
    ClientListAPI.register_routes(api)
    MessageAPI.register_routes(api)
    MessageListAPI.register_routes(api)


def main():
    host = '127.0.0.1'
    port = 5000
    if len(sys.argv) > 1:
        host = sys.argv[1]
        port = int(sys.argv[2])

    app = Flask(__name__)
    api = Api(app)
    add_routes(api)

    app.run(
        host=host,
        port=port,
        debug=True
    )


if __name__ == '__main__':
    main()
