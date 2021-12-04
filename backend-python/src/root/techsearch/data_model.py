from enum import Enum

'''
Klassen für die DTOs des Python-Backends
'''

class Language(Enum):
    ENGLISH = 0
    GERMAN = 1
    
class DbConfigDto(object):
    dbConnectionString = None
    
    
class SearchDto(object):
    def __init__(self):
        self.searchTerm = None
        self.language = None

class Doc2VecSearchResponseDto(object):
    def __init__(self):
        self.documents = None

class Word2VecSearchResponseDto(object):
    def __init__(self):
        self.words = None


class SearchResponse(object):
    def __init__(self, name):
        self.name = name

class TsneRequestDto(object):
    def __init__(self):
        self.keys = None
        self.language = None
        
class TsneDto (object):
    def __init__(self):
        self.imageBase64 = None
