from jsonpickle import unpickler
import jsonpickle.tags as tags
import jsonpickle.util as util
from root.techsearch.data_model import Language

def jsonDecode(json, clazz):
    '''
    Decodiert ais einem JSON-String eine Instanz der angegebenden Klasse.
    '''
    
    json[tags.OBJECT] = util.importable_name(clazz)
    obj = unpickler.Unpickler().restore(json)
    return obj


def getLanguageName(language: Language):
    if language == language.ENGLISH:
        return "ENGLISH"
    elif language == language.GERMAN:
        return "GERMAN"
    else:
        return "UNKNOWN" 

