from root.techsearch.data_model import Language, SearchDto
from test_base import TestBase
from root.techsearch.doc2vec_model import Doc2VecService
from root.techsearch.word2vec_model import Word2VecService
from root.techsearch import main
import matplotlib

class TestDoc2Vec(TestBase):
        
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