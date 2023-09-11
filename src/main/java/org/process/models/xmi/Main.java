package org.process.models.xmi;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.discover.arch.model.Config;
import org.utils.Utils;

public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Main@main -> Running ECORE processing");
        try {
            Config config = new Config();

            EcoreModelHandler ecoreModelHandler = new EcoreModelHandler(config);

            /*
             * scommentare
             * 
             * EolRunner eolRunner = EolRunner.getInstance();
             * 
             * JavaQueryAADLModelInst javaQueryAADLModelInst =
             * JavaQueryAADLModelInst.getInstance();
             * 
             * ecoreModelHandler.discoverModelFromPath();
             * config.loadJSONFilesGeneratedByDiscoveringPhase();
             * ecoreModelHandler.processModels(eolRunner, javaQueryAADLModelInst);
             * 
             * ecoreModelHandler.generateCSVFileFromProcessedModels("results");
             */

            // ECL
            logger.info("INIZIO FASE ECL");

            EclRunner eclRunner = EclRunner.getInstance();
            long startTimeEclRunner = System.nanoTime();

            eclRunner.run("structural.similarity.ecl", ecoreModelHandler.discoverModelPath());
            long endTimeEclRunner = System.nanoTime();

            logger.info("Ecl runner execution time in seconds: " + ((endTimeEclRunner - startTimeEclRunner)/ 1000000000)+ " s");


        } catch (Exception e) {
            logger.info("Main@main -> ERROR: " + e.getMessage());
        }
    }
}
