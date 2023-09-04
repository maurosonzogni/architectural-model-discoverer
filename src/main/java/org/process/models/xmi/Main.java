package org.process.models.xmi;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.discover.arch.model.Config;
import org.eclipse.epsilon.ecl.trace.MatchTrace;

public class Main {

    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        logger.info("Main@main -> Running ECORE processing");
        try {
            Config config = new Config();

            EcoreModelHandler ecoreModelHandler = new EcoreModelHandler(config);

            /*
             * TODO: scommentare
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

            // TEST ECL
            logger.info("INIZIO TEST ECL");
            EclRunner eclRunner = EclRunner.getInstance();

            List<String> uriModels = ecoreModelHandler.discoverModelPath();

            // Attualmente ci appoggiamo a ecoreModelHandler, si potrebbe rendere più
            // snesato
            for (int i = 0; i < ecoreModelHandler.discoverModelPath().size(); i++) {
                for (int j = 0; j < ecoreModelHandler.discoverModelPath().size(); j++) {
                    try {
                        MatchTrace c = eclRunner.test(uriModels.get(i), uriModels.get(j));

                    } catch (Exception e) {
                        logger.error("Error performing ECL script: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            logger.info("Main@main -> ERROR: " + e.getMessage());
        }
    }
}
