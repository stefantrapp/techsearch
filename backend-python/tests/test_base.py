import yaml
import logging.config
import unittest
import cProfile
import io
from pstats import SortKey
import pstats

class TestBase(unittest.TestCase):
    
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        
        with open('./logging.yaml', 'r') as stream:
            configs = yaml.load(stream, Loader=yaml.FullLoader)

        logging.config.dictConfig(configs)
        
    def start_profiling(self):
        self.profile = cProfile.Profile()
        
        self.profile.enable()
    
    def run_profiling(self, func):
        self.start_profiling()
        self.profile.runcall(func)
        self.end_profiling()    
        
    def end_profiling(self):
        self.profile.disable()
        
        string_stream = io.StringIO()
        sort_key = SortKey.CUMULATIVE
        ps = pstats.Stats(self.profile, stream=string_stream).sort_stats(sort_key)
        ps.print_stats()
        print(string_stream.getvalue())