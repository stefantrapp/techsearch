from root.techsearch.server import ServerApp
import logging.config
import yaml
from root.techsearch.scheduler import Scheduler
from root.techsearch import config

def configLogging():
    with open('./logging.yaml', 'r') as stream:
        configs = yaml.load(stream, Loader=yaml.FullLoader)
        
    logging.config.dictConfig(configs)

def configDatabase():
    with open('..//backend//config//application.yaml', 'r') as stream:
        appConfig = yaml.load(stream, Loader=yaml.FullLoader)
    
    dataSource = appConfig.get("spring").get("datasource")
    username = dataSource.get("username")
    password = dataSource.get("password")
    # Das Format für die Spring-Boot-Anwendung ist wie folgt: jdbc:postgresql://localhost:5432/techsearch
    # Damit die Datenbank-Verbindung nur einmal angegeben werden muss, die relevanten Teile extrahieren 
    # und in das Format für einen psycopg2-Connectionstring konvertieren 
    url = dataSource.get("url")
    index = url.find("://")
    remaining = url[index + 3:]
    index = remaining.find("/") 
    serverWithPort = remaining[:index]
    dbname = remaining[index+1:]
    index = serverWithPort.find(":")
    server = serverWithPort[:index]
    port = serverWithPort[index+1:]
    
    db_connection_string =  f'host={server} port={port} dbname={dbname} user={username} password={password}'
    config.db_connection_string = db_connection_string 

if __name__ == '__main__':
    configLogging()
    
    configDatabase()
    
    scheduler = Scheduler()
    scheduler.start()
    
    server = ServerApp("");
    server.run()
    
    
    
    