from flask_classful import FlaskView, route
from gensim.models import doc2vec
import logging
from root.techsearch.data_model import Language, SearchDto, Doc2VecSearchResponseDto
from gensim.models.doc2vec import Doc2Vec, TaggedDocument
import multiprocessing
import psycopg2
from root.techsearch import config
import datetime
from root.techsearch.model_utils import GeneratorIterator
from psycopg2.extras import NamedTupleCursor
import os
from time import sleep
from root.techsearch.utils import getLanguageName, jsonDecode    
from flask.globals import request

import jsonpickle
from flask.wrappers import Response

REBUILD_DOC2VEC_MODEL = "REBUILD_DOC2VEC_MODEL"


class Doc2VecView(FlaskView):
    '''
    API für das Doc2Vec-Modell
    '''
    logger = logging.getLogger(__name__)
    doc2VecService = None
    
    def __init__(self):
        '''
        '''
        self.doc2VecService = Doc2VecService()
    
    @route("/search/", methods=['POST'])
    def search(self):
        json = request.json
        searchDto = jsonDecode(json, SearchDto)        
        
        doc_is = self.doc2VecService.query_doc2vec_model(searchDto)
        
        responseDto = Doc2VecSearchResponseDto()
        responseDto.documents = doc_is
        
        jsonResponse = jsonpickle.encode(responseDto)
        
        return Response(jsonResponse, content_type="application/json", )    


class ProgressCallback(object):
    '''
    Callback für die Fortschritsanzeige
    '''
        
    def __init__(self, logger, max_iteration: int, conn):
        self.logger = logger
        self.max_iteration = max_iteration
        self.conn = conn
    
    def next_iteration(self, iteration: int):
        self.iteration = iteration
        
    def next_sentence(self, cur_sentences: int, max_sentence: int):
        self.logger.info('Iteration {iter}/{max_iteration}: {cur} von {max_sentences} Dokumente: {perc}%'.format(max_iteration = self.max_iteration, 
                                                                                                                       iter = self.iteration, cur = cur_sentences, max_sentences = max_sentence, perc = round(cur_sentences/max_sentence * 100, 1)))
        
        now = datetime.datetime.now()
        
        with self.conn.cursor() as cur:
            cur.execute("update application_job_state set last_sign_of_life = %s where job_type = %s", (now,REBUILD_DOC2VEC_MODEL))    

class Doc2VecJob(object):
    '''
    Job für die Erstellung des Modells
    '''
    
    logger = logging.getLogger(__name__)
    
    def __init__(self):
        '''
        '''
    
    def run(self):
        with psycopg2.connect(config.db_connection_string) as conn:
            with conn.cursor(cursor_factory=NamedTupleCursor) as cur:
                query = "select * from application_job_state where job_type = %s" 
                cur.execute(query, (REBUILD_DOC2VEC_MODEL,))
                row = cur.fetchone()
                #self.logger.info("Job-Status Doc2Vec: " + str(row))
                
                should_run = row.should_run
                running = row.running
                if should_run == True and running == False:
                    with conn.cursor() as cur:
                            cur.execute("update application_job_state set running = true, start_time = %s where job_type = %s", (datetime.datetime.now(), REBUILD_DOC2VEC_MODEL))            
            
        if should_run == True and running == False:
            doc2vecService = Doc2VecService()
            doc2vecService.create_doc2vec_model(Language.ENGLISH);
            doc2vecService.create_doc2vec_model(Language.GERMAN);
                
            with psycopg2.connect(config.db_connection_string) as conn:
                with conn.cursor() as cur:
                    cur.execute("update application_job_state set running = false, should_run = false where job_type = %s", (REBUILD_DOC2VEC_MODEL,))
                    
class Doc2VecService(object):
    '''
    Service für das Doc2Vec-Modell
    '''
    
    limit = ""
    
    minPreprocessorsCount = 2;
    
    minFreeCpusCount = 4;
    
    logger = logging.getLogger(__name__)

    loaded_Models = None
        
    def __init__(self):
        '''
        '''
        self.loaded_Models = { "ENGLISH": None, "GERMAN": None }

    def query_doc2vec_model(self, searchDto: SearchDto):
        language = searchDto.language
        #lngName = getLanguageName(language)
        model = self.loaded_Models[language]
        
        if model == None:
            modelFilePath = self.getModelFile(language)
            model = Doc2Vec.load(modelFilePath)
            self.loaded_Models[language] = model
        
        searchTerm = searchDto.searchTerm;

        if searchTerm == None or len(searchTerm) == 0:
            return list() 
        
        test_data = [searchDto.searchTerm]
        
        
        v1 = model.infer_vector(test_data)
        #print("V1_infer", v1)
        
        similar_docs = model.dv.most_similar(positive=[v1], topn=10)
        #print(similar_docs)

        doc_ids = list(map(lambda x: x[0], similar_docs))
        #print(doc_ids)

        return doc_ids

    def create_doc2vec_model(self, language: Language):
        fastDoc2Vec = doc2vec.FAST_VERSION
        self.logger.info("doc2vec.FAST: " + str(fastDoc2Vec))
        
        cpuCount = multiprocessing.cpu_count()
        workerCount = max(self.minPreprocessorsCount, cpuCount - self.minFreeCpusCount);
        
        num_of_epochs = 1
        max_iteration = num_of_epochs + 1
        
        conn = psycopg2.connect(config.db_connection_string)
        try:
            conn.autocommit = True # Damit die Fortschritte laufend in der Datenbank gespeichert werden können
            progress_callback = ProgressCallback(self.logger, max_iteration, conn)

            languageName = getLanguageName(language)

            modelFilePath = self.getModelFile(languageName)
            self.logger.info("Doc2Vec-Model wird erzeugt. Nach der Fertiggstellung wird es hier gespeichert: " + modelFilePath)
           
            test_iterator = self.doc_generator(language, progress_callback)
            has_next = next(test_iterator, None)
           
            if has_next == None:
                self.logger.info("Keine Dokumente für " + languageName + " gefunden.")
            else:
                iterator = GeneratorIterator(lambda : self.doc_generator(language, progress_callback), progress_callback)
                model = Doc2Vec(documents=iterator, vector_size=100, window=5, min_count=5, workers=workerCount, epochs=num_of_epochs)
                
                self.logger.info("Speichere Doc2Vec-Model in: " + modelFilePath)
                model.save(modelFilePath)
        finally:
            conn.close()
        
    
    def getModelFile(self, language: str):
        modelFilePath = os.path.abspath("doc2vec-" + language + ".model")
        return modelFilePath
    
    def doc_generator(self, language: Language, progress_callback):
        '''
        Generatorfunktion für die Dokumente einer Sprach aus der Datenbank
        '''
        
        with psycopg2.connect(config.db_connection_string) as conn:
            with conn.cursor(name="name_of_cursor", cursor_factory=NamedTupleCursor) as cur_count:
                query = "select count(*) from Document d where d.sentences is not null and language = " + str(language.value)
                cur_count.execute(query)
                max_sentences = cur_count.fetchone()[0]
        
            with conn.cursor(name="name_of_cursor", cursor_factory=NamedTupleCursor) as cur:
                cur.itersize = 500

                query = "select * from Document d where d.sentences is not null and language = %s order by id" + self.limit
                cur.execute(query, (str(language.value)))
        
                cur_sentence_count = 0
                while True:
                    row = cur.fetchone()
                    if not row:
                        break

                    cur_sentence_count += 1
                    
                    if cur_sentence_count % 100 == 0 or cur_sentence_count == max_sentences:
                        progress_callback.next_sentence(cur_sentence_count, max_sentences)
                    
                    #for row in rows:
                    words = row.sentences.split()
                    
                    doc = TaggedDocument(words, (str(row.id),))
                    yield doc
