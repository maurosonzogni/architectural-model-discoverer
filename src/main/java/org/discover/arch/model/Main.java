package org.discover.arch.model;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Main {
    private static Config config = null;
    private final static Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            // By now the config location file is hardcoded in Config class
            config = new Config();


        } catch (Exception e) {
            logger.error("Main@main -> ERROR LOADING THE CONFIG FILE: " + e.getMessage());
            return;
        }

        try {
            boolean isCreated = config.createFolderOutput();

            if (!isCreated) {
                logger.warn("Main@main -> STRUCTURE FOLDER NOT CREATED, PLEASE CHECK IF ALREADY EXISTS");
                return;
            }

            logger.info("STAGE 1");
            logger.info("ANALYZING THE RESOURCES PATHS");

            ResourcesProviderAnalyzer resourcesProviderAnalyzer = new ResourcesProviderAnalyzer(config);
            List<String> rootPathToAnalyze = resourcesProviderAnalyzer.getFileResourcePaths();
            SearchFileTraversal fileDiscover = new SearchFileTraversal(config).setSearchPaths(rootPathToAnalyze);
            ArchModelConverter archModelConverter = new ArchModelConverter(config);
            
            fileDiscover.analyseModels(archModelConverter);
        } catch (Exception e) {
            logger.error("Main@main -> ERROR: " + e.getMessage());
        }
    }

}
