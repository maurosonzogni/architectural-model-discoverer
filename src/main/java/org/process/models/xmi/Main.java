package org.process.models.xmi;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.discover.arch.model.Config;

public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Main@main -> Running ECORE processing");
        try {
            Config config = new Config();

            EcoreModelHandler ecoreModelHandler = new EcoreModelHandler(config);

            EolRunner eolRunner = EolRunner.getInstance();
            
            JavaQueryAADLModelInst javaQueryAADLModelInst = JavaQueryAADLModelInst.getInstance();

            ecoreModelHandler.discoverModelFromPath();
            config.loadJSONFilesGeneratedByDiscoveringPhase();
            ecoreModelHandler.processModels(eolRunner, javaQueryAADLModelInst);

            ecoreModelHandler.generateCSVFileFromProcessedModels("results");
            
        } catch (Exception e) {
            logger.info("Main@main -> ERROR: " + e.getMessage());
        }
    }
}
