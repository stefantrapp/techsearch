import jsonpickle
import psycopg2
import pprint
from psycopg2.extras import NamedTupleCursor
import gensim
from flask_classful import FlaskView, route
import logging
from gensim.models import word2vec
from gensim.models.word2vec import Word2Vec
from root.techsearch.data_model import Language, TsneDto, TsneRequestDto,\
    SearchDto, Word2VecSearchResponseDto
import string
import multiprocessing
import os
from root.techsearch import config
import io
import codecs
import datetime
from datetime import timezone
from root.techsearch.model_utils import GeneratorIterator
from sklearn.manifold._t_sne import TSNE
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.cm as cm
import base64
import warnings
from root.techsearch.utils import jsonDecode, getLanguageName
from flask.globals import request
from flask.wrappers import Response

REBUILD_WORD2VEC_MODEL = "REBUILD_WORD2VEC_MODEL"

class Word2VecView(FlaskView):
    '''
    API für das Word2Vec-Modell
    '''
   
    logger = logging.getLogger(__name__)
   
    word2VecService = None
   
    def __init__(self):
        self.word2VecService = Word2VecService();
    
    @route("/")
    def index(self):
        return "Word2Vec API"
    
    @route("/search/", methods=['POST'])
    def search(self):
        json = request.json
        searchDto = jsonDecode(json, SearchDto)        
        
        words = self.word2VecService.query_word2vec_model(searchDto)
        
        responseDto = Word2VecSearchResponseDto()
        responseDto.words = words
        
        jsonResponse = jsonpickle.encode(responseDto)
        
        return Response(jsonResponse, content_type="application/json", ) 
    
    @route("/tsne/", methods=['POST'])
    def tsne(self):
        json = request.json
        dto = jsonDecode(json, TsneRequestDto)
        
        plot = self.word2VecService.makePlot(dto)
        
        tsne = TsneDto();
        tsne.imageBase64 = plot 
        
        jsonResponse = jsonpickle.encode(tsne)
        
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
        print(now)
        with self.conn.cursor() as cur:
            cur.execute("update application_job_state set last_sign_of_life = %s where job_type = %s", (now,REBUILD_WORD2VEC_MODEL))

class Word2VecJob(object):
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
                cur.execute(query, (REBUILD_WORD2VEC_MODEL,))
                row = cur.fetchone()
                #self.logger.info("Job-Status Word2Vec: " + str(row))
                
                should_run = row.should_run
                running = row.running
                if should_run == True and running == False:
                    with conn.cursor() as cur:
                            cur.execute("update application_job_state set running = true, start_time = %s where job_type = %s", (datetime.datetime.now(), REBUILD_WORD2VEC_MODEL))            
            
        if should_run == True and running == False:
            word2VecService = Word2VecService()
            word2VecService.create_word2vec_model(Language.ENGLISH);
            word2VecService.create_word2vec_model(Language.GERMAN);
                
            with psycopg2.connect(config.db_connection_string) as conn:
                with conn.cursor() as cur:
                    cur.execute("update application_job_state set running = false, should_run = false where job_type = %s", (REBUILD_WORD2VEC_MODEL,))
    
class Word2VecService(object):
    '''
    Service für das Word2Vec-Modell
    '''

    
    limit = ""
    
    minPreprocessorsCount = 2;
    
    minFreeCpusCount = 4;
    
    logger = logging.getLogger(__name__)
    
    loaded_Models = None
    
    process_only_claims = False
        
    def __init__(self):
        self.loaded_Models = { "ENGLISH": None, "GERMAN": None }
    
    def get_model(self, language: str):
        model = self.loaded_Models[language]
        
        if model == None:
            modelFilePath = self.getModelFile(language)
            model = Word2Vec.load(modelFilePath)
            self.loaded_Models[language] = model
            
        return model
            
    def query_word2vec_model(self, searchDto: SearchDto):
        language = searchDto.language
        #lngName = getLanguageName(language)
        model = self.get_model(language)

        searchWord = searchDto.searchTerm
        
        if searchWord not in model.wv.key_to_index:
            return list()
        
        simWords = model.wv.most_similar(searchWord, topn=40)
        
        words = list(map(lambda x: x[0], simWords))
        
        return words

    def create_word2vec_model(self, language: Language):
        fastWord2Vec = word2vec.FAST_VERSION
        self.logger.info("word2vec.FAST_VERSION: " + str(fastWord2Vec))
        
        cpuCount = multiprocessing.cpu_count()
        workerCount = max(self.minPreprocessorsCount, cpuCount - self.minFreeCpusCount);
        
        num_of_epochs = 5
        max_iteration = num_of_epochs + 1
        
        conn = psycopg2.connect(config.db_connection_string)
        try:
            conn.autocommit = True # Damit die Fortschritte laufend in der Daten gespeichert werden können
            progress_callback = ProgressCallback(self.logger, max_iteration, conn)
            
            languageName = getLanguageName(language)
            
            modelFilePath = self.getModelFile(languageName)
            self.logger.info("Word2Vec-Model wird erzeugt. Nach der Fertiggstellung wird es hier gespeichert: " + modelFilePath)
            
            test_iterator = self.sentence_generator(language, progress_callback)
            has_next = next(test_iterator, None)
            
            if has_next == None:
                self.logger.info("Keine Dokumente für " + languageName + " gefunden.")
            else:
                iterator = GeneratorIterator(lambda : self.sentence_generator(language, progress_callback), progress_callback)
                # Parameter sg ({0, 1}, optional) – Training algorithm: 1 for skip-gram; otherwise CBOW.
                # the skip-gram is more accurate and reliable for infrequent words versus the CBOW (Goldbergand Levy, 2014). 
                model = Word2Vec(sentences=iterator, vector_size=100, window=5, min_count=1, workers=workerCount, epochs=num_of_epochs, sg=1)
                
                self.logger.info("Speichere Word2Vec-Model in: " + modelFilePath)
                model.save(modelFilePath)
        finally:
            conn.close()
        
    
    def getModelFile(self, language: str):
        modelFilePath = os.path.abspath("word2vec-" + language + ".model")
        return modelFilePath
    
    def makePlot(self, dto: TsneRequestDto):
        language = dto.language
        model = self.get_model(language)

        keys = []
        for key in dto.keys:
            if key in model.wv.key_to_index:
                keys.append(key) 
        
        if len(keys) == 0:
            return ""
        
        warnings.simplefilter(action='ignore', category=FutureWarning)
        model_gn = model.wv
        
        embedding_clusters = []
        word_clusters = []
        for word in keys:
            embeddings = []
            words = []
            for similar_word, _ in model_gn.most_similar(word, topn=30):
                words.append(similar_word)
                embeddings.append(model_gn[similar_word])
            embedding_clusters.append(embeddings)
            word_clusters.append(words)

        embedding_clusters = np.array(embedding_clusters)
        n, m, k = embedding_clusters.shape
        tsne_model_en_2d = TSNE(perplexity=15, n_components=2, init='pca', n_iter=3500, random_state=32)
        embeddings_en_2d = np.array(tsne_model_en_2d.fit_transform(embedding_clusters.reshape(n * m, k))).reshape(n, m, 2)

        return self.tsne_plot_similar_words('', keys, embeddings_en_2d, word_clusters, 0.7)

    def tsne_plot_similar_words(self, title, labels, embedding_clusters, word_clusters, a, filename=None):
        plt.figure(figsize=(9, 9)) # Hier kann die Größe (Breite und Höhe) der Bildes festgelegt werden 
        numbers = np.linspace(0, 1, len(labels))
        
        colors = plt.get_cmap('rainbow')(numbers) 
        for label, embeddings, words, color in zip(labels, embedding_clusters, word_clusters, colors):
            x = embeddings[:, 0]
            y = embeddings[:, 1]
            plt.scatter(x, y, c=color, alpha=a, label=label)
            for i, word in enumerate(words):
                plt.annotate(word, alpha=0.5, xy=(x[i], y[i]), xytext=(5, 2),
                             textcoords='offset points', ha='right', va='bottom', size=8)
        plt.legend(loc=4)
        plt.title(title)
        plt.grid(True)
        
        if filename:
            plt.savefig(filename, format='png', dpi=150, bbox_inches='tight')
        
        my_stringIObytes = io.BytesIO()
        plt.savefig(my_stringIObytes, format='png')
        my_stringIObytes.seek(0)
        my_base64_jpgData = base64.b64encode(my_stringIObytes.read())
        base64_message = my_base64_jpgData.decode('ascii')
        
        # Hiermit würde eine HTML-Datei erzeugt werden, in der das Diagramm als Bilde direkt eingebettet ist.
        #html = '<html><body><div><img src="data:image/png;base64, ' + base64_message + '" /></div></body></html>' 
        #f = open("image.html", "w")
        #f.write(html)
        #f.close()
        
        # Hiermit kann das Diagramm direkt angezeigt werden.
        #plt.show()
        return base64_message
    
    def sentence_generator(self, language: Language, progress_callback):
        with psycopg2.connect(config.db_connection_string) as conn:
            with conn.cursor(name="name_of_cursor", cursor_factory=NamedTupleCursor) as cur_words:
                query = "select value from Setting s where key = 'STOP_WORDS_" + getLanguageName(language) + "'"
                cur_words.execute(query)
                stop_words = cur_words.fetchone()[0]
                stopwords = stop_words.split(",")
                all_stopwords = set(map(lambda word: word.strip(), stopwords))
                
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
                    allSentences = row.sentences

                    if self.process_only_claims:
                        indexOfClaims = allSentences.find("CLAIMS.")
                        if indexOfClaims != -1:
                            allSentences = allSentences[indexOfClaims:]
                    
                    # Interpunktonen entfernen
                    allSentences = allSentences.translate(str.maketrans('', '', string.punctuation))
                   
                    sentences = allSentences.split("\n");
                    
                    for sentence in sentences:
                        if len(sentence) == 0:
                            continue

                        sentence = sentence.lower()
                        words = sentence.split() 
                        
                        new_words = [word for word in words if not word in all_stopwords]

                        yield new_words
    
