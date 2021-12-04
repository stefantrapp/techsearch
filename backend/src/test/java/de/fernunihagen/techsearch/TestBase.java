package de.fernunihagen.techsearch;

import org.junit.BeforeClass;

import de.fernunihagen.techsearch.jobs.QuartzScheduler;

public abstract class TestBase {

    @BeforeClass
    public static void setupBase() {
        QuartzScheduler.DisableTriggers = true;
    }
    
}
