package de.fernunihagen.techsearch.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

public class LazyMapWrapperTest {

    /**
     * Testet, ob auch bei mehreren Threads nur einmal ein Wert lazy erzeugt wird.
     * @throws InterruptedException
     */
    @Test
    @Ignore
    public void test_getOrPut_SameValue() throws InterruptedException {
      Random rnd = new Random();
      
      for (int j = 0; j < 10000; j++) {      
          LazyMapWrapper<String, String> map = new LazyMapWrapper<String, String>(new HashMap<String, String>());
          List<Worker> workers = new ArrayList<Worker>();
          
          for (int i = 0; i < 24; i++) {
              workers.add(new Worker(i + 1, map));
          }
          
          /* Einen zufäligen Thread als erstens starten */
          workers.get(rnd.nextInt(workers.size())).start();
          /* Dann dir restlichen Thread starten */
          workers.parallelStream().forEach(w -> {
              if (w.getState() == State.NEW) {
                  w.start();    
              }
          });
          
          /* Warten bis alle Threads fertig sind. */
          workers.forEach(w -> {
            try {
                w.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
          });
    
          /* Prüfen, ob alle Threads den gleichen (dem vom zuerst gestarteten Thread) Wert bekommen haben */
          var firstValue = workers.get(0).getValue();
          assertNotNull(firstValue);
          workers.forEach(w -> assertEquals(firstValue, w.getValue()));

          if (j % 100 == 0) {
              System.out.println(j + " ok");
          }
      }
    }
    
    static class Worker extends Thread {
        
        private int id;
        private LazyMapWrapper<String, String> map;

        String value;
        
        public Worker(int id, LazyMapWrapper<String, String> map) {
            this.id = id;
            this.map = map;
        }
        
        @Override
        public void run() {
            value = map.getOrPut("a", c -> "wert:" + id); 
        }
        
        public String getValue() {
            return value;
        }
    }
}
