package de.fernunihagen.techsearch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.fernunihagen.techsearch.data.Setting;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Integer> {
    Setting findByKey(String key);
    
}
