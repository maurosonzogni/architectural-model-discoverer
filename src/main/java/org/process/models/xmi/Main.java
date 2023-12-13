package org.process.models.xmi;

import java.nio.file.Paths;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.config.Config;
import org.config.EclConfig;
import org.config.EgxConfig;
import org.utils.Utils;

public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Main@main -> Running ECORE processing");
        try {
            Config config = new Config();

        
            /*EcoreModelHandler ecoreModelHandler = new EcoreModelHandler(config);
            
            EolRunner eolRunner = EolRunner.getInstance();
            
            JavaQueryAADLModelInst javaQueryAADLModelInst =
            JavaQueryAADLModelInst.getInstance();
            
            config.loadJSONFilesGeneratedByDiscoveringPhase();
            ecoreModelHandler.processModels(eolRunner, javaQueryAADLModelInst);
            
            ecoreModelHandler.generateCSVFileFromProcessedModels("results");*/
           

            EclConfig eclConfig = new EclConfig();

            // Perform only if ECL is enabled, disabled by default

            if (eclConfig.getEnabled()) {
                logger.info("INIZIO FASE ECL");

                EclRunner eclRunner = EclRunner.getInstance();

                long startTime = System.nanoTime();

                eclRunner.run(eclConfig,
                        Utils.discoverModelFromPath(Paths.get(config.getRootPath(), config.getOutputFolder(), "xmi")
                                .toString(), config.getModelExtension()));

                long endTime = System.nanoTime();

                logger.info("Ecl runner execution time in seconds: "
                        + ((endTime - startTime) / 1000000000) + " s");
            }
            
            EgxConfig egxConfig = new EgxConfig();
            // Perform only if EGX is enabled, disabled by default
            if (egxConfig.getEnabled()) {
                logger.info("INIZIO FASE EGX");

                EgxRunner egxRunner = EgxRunner.getInstance();

                long startTime = System.nanoTime();

                egxRunner.run(egxConfig,
                        Utils.discoverModelFromPath(Paths.get(config.getRootPath(),
                                config.getOutputFolder(), "xmi")
                                .toString(), config.getModelExtension()));
                long endTime = System.nanoTime();

                logger.info("Egx runner execution time in seconds: "
                        + ((endTime - startTime) / 1000000000) + " s");
            }

        } catch (Exception e) {
            logger.info("Main@main -> ERROR: " + e.getMessage());
        }
    }
}
