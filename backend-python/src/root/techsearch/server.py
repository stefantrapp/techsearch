from flask import Flask

from waitress import serve
import logging
from root.techsearch import config

from root.techsearch.word2vec_model import Word2VecView
from root.techsearch.api_view import ApiView
from root.techsearch.doc2vec_model import Doc2VecView

class ServerApp(object):
    '''
    Einstiegsklasse für die Webanwendung.
    '''

    logger = logging.getLogger(__name__)
    app = Flask(__name__)
    
    def __init__(self, params):
        '''
        Constructor
        '''
        ApiView.register(self.app)
        Word2VecView.register(self.app, route_prefix="api", route_base="word2vec")
        Doc2VecView.register(self.app, route_prefix="api", route_base="doc2vec")
    
    @app.route("/")
    def index():  # @NoSelf
        return "Die API ist unter dem Pfad <a href='/api'>/api</a> zu finden"
   
    def run(self):
        self.logger.info("Starte Server...")
        #self.app.run(debug = True); # Hier würde der Developmentserver von Flask verwendet werden
        
        config.flaskApp = self.app 
        serve(self.app, listen='127.0.0.1:5000')
    
