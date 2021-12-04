package de.fernunihagen.techsearch.utils;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * Klasse eines Wrappers um eine Map, die Werte bei Bedarf (lazy) erzeugt.
 *
 * @param <K>
 * @param <V>
 */
public class LazyMapWrapper<K, V> {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock writeLock = lock.writeLock();
    private Lock readLock = lock.readLock();
    
    private Map<K, V> wrappingMap;
    
    public LazyMapWrapper(Map<K, V> wrappingMap) {
        this.wrappingMap = wrappingMap;
    }
    
    public V getOrPut(K key, Function<K, V> creatorFunc) {
        readLock.lock();
        var value = wrappingMap.get(key);
        try {
            if (value == null) {
                readLock.unlock();
                
                writeLock.lock();
                try {
                    value = wrappingMap.get(key);
                    if (value == null) {
                        value = creatorFunc.apply(key);
                        wrappingMap.put(key, value);
                    }
                    readLock.lock();
                } finally {
                    writeLock.unlock();
                }
            }
        } finally {
            readLock.unlock();
        }
        return value;
    }
}