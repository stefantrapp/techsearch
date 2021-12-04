

from root.techsearch.data_model import Language, SearchDto
from test_base import TestBase
from root.techsearch.doc2vec_model import Doc2VecService
from root.techsearch.word2vec_model import Word2VecService
from root.techsearch import main
import matplotlib

class TestWord2Vec(TestBase):

    def test_word2vec(self):
        matplotlib.use('PS')
        
        main.configDatabase()
        word2vecService = Word2VecService();
        word2vecService.limit = " limit 1000"
        #super().run_profiling(lambda : word2vecService.create_word2vec_model(language=Language.ENGLISH))
        
        keys = ['technology', 'invention', 'industry', 'patent']
        word2vecService.makePlot(keys);
        #print(similarWords)
        
    def test_findSimilarPosWords(self):
        main.configDatabase()
        word2vecService = Word2VecService();
        
        #word2vecService.process_only_claims = True
        #word2vecService.limit = " limit 100000"
        #word2vecService.create_word2vec_model(language=Language.GERMAN)
        
        
        #super().run_profiling(lambda : word2vecService.create_word2vec_model(language=Language.ENGLISH))
        
        words = ["increase", "decrease", "release", "damage", "raise", "change", "maximize", "augment", "minimize", "diminish", "change", "differentiate", "blemish", "break", "bug", "cause", "crack", "damage", "defect", "deform", "deform", "degrade", "deprive", "destroy", "deteriorate", "disadvantage", "disparate", "hamper", "harm", "hinder", "impair", "smash", "spoil", "stain", "trouble", "weaken", "fail", "degrade", "worsen"]

        all_words = set(words)
        
        for word in words:
            searchDto = SearchDto()
            searchDto.searchTerm = word
            searchDto.language = "ENGLISH"
            #print ("quering for " + word)
            similar_words = word2vecService.query_word2vec_model(searchDto)
            
            new_similar_words = []
            for similar_word in similar_words:
                if similar_word not in all_words:
                    all_words.add(similar_word)
                    new_similar_words.append(similar_word)
            
            print("Similar word for: " + word + "\n" + str(new_similar_words))
            
        
        #keys = ['technology', 'invention', 'industry', 'patent']
        #word2vecService.makePlot(keys);
        #print(similarWords)
        
        
    def test_dump_sentences(self):
        word2vecService = Word2VecService();
        
        super().run_profiling(lambda : word2vecService.dump_sentences(language=Language.ENGLISH))
        
    def test_doc2vec(self):
        main.configDatabase()
        doc2vecService = Doc2VecService();
        #doc2vecService.limit = " limit 100"
        #super().run_profiling(lambda : doc2vecService.create_doc2vec_model(language=Language.ENGLISH))
        #doc2vecService.create_doc2vec_model(language=Language.ENGLISH)
        searchDto = SearchDto()
        searchDto.searchTerm = "Asynchronmotoren"
        searchDto.language = "GERMAN"
        
        simiarDocs = doc2vecService.query_doc2vec_model(searchDto)
        print(simiarDocs)