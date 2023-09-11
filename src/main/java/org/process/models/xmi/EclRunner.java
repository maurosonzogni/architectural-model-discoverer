package org.process.models.xmi;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.config.EclConfig;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.utils.Utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class EclRunner {

    private static EclRunner INSTANCE = null;

    private final static Logger logger = LogManager.getLogger(EclRunner.class);

    static public EclRunner getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EclRunner();
        }
        return INSTANCE;
    }

    /**
     * 
     * @param eclFileName
     * @param uriList
     * @throws Exception
     */
    protected void run(EclConfig eclConfig, List<String> uriList)
            throws Exception {

        Variable eclParams = new Variable("threshold", eclConfig.getEclParams().getThreshold(),
                EolCollectionType.Collection);

        Double[][] matrix = new Double[uriList.size()][uriList.size()];
        // Same as eol runner
        Path eclFileFolderPath = Paths.get(eclConfig.getEclScriptsFolderPath()).toAbsolutePath();
        String eclFilePath = eclFileFolderPath.resolve(eclConfig.getEclScriptName()).toString();
        String metaModelPath = Paths.get("ecore", "aadl2_inst.ecore").toAbsolutePath().toString();

        List<String[]> csv = new ArrayList<>();

        // create a new ArrayList for csv header
        List<String> headers = new ArrayList<String>(Arrays.asList("model_name"));

        boolean firstTime = true;

        for (int i = 0; i < uriList.size(); i++) {
            // We need to create a model that satisfy addModel method, for this reason we
            // use te EmfModel that implements even IModel interface
            EmfModel firstModel = Utils.createEmfModel("FirstModel", uriList.get(i), metaModelPath, true, false);

            List<String> csvRow = new ArrayList<String>();

            for (int j = 0; j < uriList.size(); j++) {
                try {
                    // TODO: capire la differenza con Ecl parallel
                    EclModule eclModule = new EclModule();
                    eclModule.parse(new File(eclFilePath));

                    EmfModel secondModel = Utils.createEmfModel("SecondModel", uriList.get(j), metaModelPath, true,
                            false);

                    eclModule.parse(new File(eclFilePath));

                    // pass data that can be used in ecl script
                    // Maybe thresholds or others
                    eclModule.getContext().getFrameStack().putGlobal(eclParams);

                    // Add models to ecl module
                    eclModule.getContext().getModelRepository().addModel(firstModel);
                    eclModule.getContext().getModelRepository().addModel(secondModel);
                    // execute ecl module
                    eclModule.execute();

                    // Only 3 digists after 0
                    matrix[i][j] = Math.round(
                            (Double) eclModule.getContext().getFrameStack().get("editDistance").getValue() * 1000.0)
                            / 1000.0;

                    // initialize csv headers with moldels name
                    if (firstTime) {
                        // on first loop add all model names
                        String modelName = (String) eclModule.getContext().getFrameStack().get("secondModelName")
                                .getValue();
                        headers.add(modelName);
                    }

                    // in this way we initialize all the csv row with model name
                    if (j == 0) {
                        csvRow.add((String) eclModule.getContext().getFrameStack().get("firstModelName").getValue());
                    }
                    csvRow.add((matrix[i][j]).toString());

                } catch (Exception e) {
                    logger.error("Error performing ECL script: " + e.getMessage());
                }
            }

            if (firstTime) {
                String[] headersStringsArray = Arrays.copyOf(headers.toArray(), headers.size(), String[].class);
                csv.add(headersStringsArray);
            }
            // TEST CSV
            firstTime = false;
            String[] csvRowStringsArray = Arrays.copyOf(csvRow.toArray(), csvRow.size(), String[].class);
            csv.add(csvRowStringsArray);

        }

            Utils.writeToCSV(csv, eclConfig.getCsvFileFolderPath(), eclConfig.getCsvFileName());

            Utils.print2dArray(matrix);
        

    }

}