package org.process.models.xmi;

import java.nio.file.Paths;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.config.Config;
import org.config.EclConfig;

import org.utils.Utils;

public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Main@main -> Running ECORE processing");
        try {
            Config config = new Config();

            EclConfig eclConfig = new EclConfig();

            //EcoreModelHandler ecoreModelHandler = new EcoreModelHandler(config);

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

            eclRunner.run(eclConfig, Utils.discoverModelFromPath(Paths.get(config.getRootPath(), config.getOutputFolderName(), "xmi")
                .toString(),config.getModelExtension()));

            long endTimeEclRunner = System.nanoTime();

            logger.info("Ecl runner execution time in seconds: " + ((endTimeEclRunner - startTimeEclRunner)/ 1000000000)+ " s");


        } catch (Exception e) {
            logger.info("Main@main -> ERROR: " + e.getMessage());
        }
    }
}
