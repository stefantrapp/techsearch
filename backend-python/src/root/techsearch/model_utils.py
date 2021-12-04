import logging

class GeneratorIterator(object):
    '''
    Iterator der mehrfach über ein Datensätze (Dokumente) iterieren kann und sich
    die Anzhal der Iterationen für die Protokollierung merkt. 
    '''
    
    logger = logging.getLogger(__name__)
    num_pass = 0
    
    def __init__(self, generator_function, progress_callback):
        self.generator_function = generator_function
        self.progress_callback = progress_callback
    
    def __iter__(self):
        self.num_pass = self.num_pass + 1
        self.progress_callback.next_iteration(self.num_pass)
        self.generator = self.generator_function()
        return self
     
    def __next__(self):
        res = next(self.generator)
        if res is None:
            raise StopIteration
        else:
            return res