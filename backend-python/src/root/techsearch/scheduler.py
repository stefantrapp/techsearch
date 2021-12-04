from apscheduler.schedulers.background import BackgroundScheduler
from root.techsearch.doc2vec_model import Doc2VecJob
from root.techsearch.word2vec_model import Word2VecJob

class Scheduler(object):
    '''
    Scheduler für die Jobs zur Erstellung der Modelle
    '''

    def __init__(self):
        '''
        Constructor
        '''
        
    def start(self):
        scheduler = BackgroundScheduler(timezone="Europe/Berlin")
        
        scheduler.add_job(Doc2VecJob().run, 'interval', seconds=10, id='Doc2VecJob', coalesce=True, max_instances=2)
        scheduler.add_job(Word2VecJob().run, 'interval', seconds=10, id='Word2VecJob', coalesce=True, max_instances=2)
      
        scheduler.start()
      
        

