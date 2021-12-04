from flask import url_for

from root.techsearch import config
from flask_classful import FlaskView, route
import os
from root.techsearch.config import db_connection_string

class ApiView(FlaskView):
    '''
    Einsiegsklasse für die oberste Ebene der API
    '''
    
    default_methods = ['GET', 'POST'] # Überflüssig, wenn man bei @route auch "methods" angibt.
    
    def __init__(self):
        '''
        '''
    
    @route("/")
    def index(self):
        # Eine Liste mit Links für die API erzeugen
        links = []
        for rule in config.flaskApp.url_map.iter_rules():
            if "GET" in rule.methods and self.has_no_empty_params(rule):
                url = url_for(rule.endpoint, **(rule.defaults or {}))
                links.append((url, rule.endpoint))

        links.sort(key=takeFirst)

        linkHtml = ""
        for link in links:
            linkHtml = linkHtml + "<a href=""" + link[0] +  """>""" + link[0] + "</a><br>"
                       
        return "API<br>" + db_connection_string + "<br>" + linkHtml
    
    def has_no_empty_params(self, rule):
        defaults = rule.defaults if rule.defaults is not None else ()
        arguments = rule.arguments if rule.arguments is not None else ()
        return len(defaults) >= len(arguments)
    
    
    @route("/shutdown/")
    def shutdown(self):
        os._exit(0)
        return "Shutting down..."
    
    
    
def takeFirst(elem):
        return elem[0]
