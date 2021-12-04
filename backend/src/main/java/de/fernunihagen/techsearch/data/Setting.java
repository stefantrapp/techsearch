package de.fernunihagen.techsearch.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;

/**
 * Klasse für eine Einstellung der Anwendung.
 */

@Entity
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="id")
    private int id;
    
    private String key;
    
    /**
     * Ein für Menschen verständlicher Name. Nur für die GUI.
     */
    private String name;
    
    /**
     * Eine Name für eine Gruppe um zusammengehörige Einstellungen zu gruppieren. Nur für die GUI.
     */
    private String gruppe;
    
    /**
     * Die Position der Einstellung. Nur für die GUI.
     */
    @ColumnDefault("0")
    private int position;
    
    /**
     * Der Wert einer Einstellung.
     */
    @Column(columnDefinition = "TEXT NULL")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getGruppe() {
        return gruppe;
    }

    public void setGruppe(String gruppe) {
        this.gruppe = gruppe;
    }
    
}
