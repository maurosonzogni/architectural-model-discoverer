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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.execute.context.Variable;
import org.eclipse.epsilon.eol.types.EolAny;
import org.eclipse.epsilon.eol.types.EolBag;
import org.eclipse.epsilon.eol.types.EolCollectionType;
import org.eclipse.epsilon.eol.types.EolNativeType;
import org.eclipse.epsilon.eol.types.EolPrimitive;
import org.eclipse.epsilon.eol.types.EolPrimitiveType;
import org.eclipse.epsilon.eol.types.EolType;
import org.osate.aadl2.instance.InstancePackage;
import org.osate.aadl2.util.Aadl2ResourceFactoryImpl;
import org.utils.Utils;

import lombok.NoArgsConstructor;

/**
 * @author Mauro Sonzogni
 * 
 */
@NoArgsConstructor
public class EclRunner {

    private static EclRunner INSTANCE = null;

    private final static Logger logger = LogManager.getLogger(EclRunner.class);

    /**
     * 
     * @return EclRunner
     */
    static public EclRunner getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EclRunner();
        }
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("aaxl2", new Aadl2ResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        InstancePackage.eINSTANCE.eClass();
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

        Variable thresholdVariable = new Variable("threshold", eclConfig.getEclParams().getThreshold(),
                 EolPrimitiveType.Real);

        Variable componentWeigth = new Variable("componentWeigth", eclConfig.getEclParams().getComponentWeigth(),
                 EolPrimitiveType.Real);
        Variable connectionWeigth = new Variable("connectionWeigth", eclConfig.getEclParams().getConnectionWeigth(),
                 EolPrimitiveType.Real);
        Variable featureWeigth = new Variable("featureWeigth", eclConfig.getEclParams().getFeatureWeigth(),
                 EolPrimitiveType.Real);
        Variable flowSpecificationWeigth = new Variable("flowSpecificationWeigth",
                eclConfig.getEclParams().getFlowSpecificationWeigth(), EolPrimitiveType.Real);

        Double[][] matrix = new Double[uriList.size()][uriList.size()];

        // Same as eol runner
        Path eclFileFolderPath = Paths.get(eclConfig.getEclScriptsFolderPath()).toAbsolutePath();

        String eclFilePath = eclFileFolderPath.resolve(eclConfig.getEclScriptName()).toString();

        String metaModelPath = Paths.get("ecore", "aadl2_inst.ecore").toAbsolutePath().toString();

        List<String[]> csv = new ArrayList<>();

        // create a new ArrayList for csv header
        List<String> headers = new ArrayList<String>(Arrays.asList("model_name"));

        for (int i = 0; i < uriList.size(); i++) {
            logger.info("STEP: " + i + " OF " + uriList.size());
            // We need to create a model that satisfy addModel method, for this reason we
            // use te EmfModel that implements even IModel interface
            EmfModel firstModel = Utils.createEmfModel("FirstModel", uriList.get(i), metaModelPath, true, false);

            List<String> csvRow = new ArrayList<String>();

            for (int j = 0; j < uriList.size(); j++) {
                logger.info("INTERNAL STEP: " + j + " OF " + uriList.size());
                // Initialize ecl module
                EclModule eclModule = new EclModule();
                // parse ecl file
                eclModule.parse(new File(eclFilePath));
                try {

                    EmfModel secondModel = Utils.createEmfModel("SecondModel", uriList.get(j), metaModelPath, true,
                            false);

                    // pass data that can be used in ecl script
                    // Maybe thresholds or others
                    eclModule.getContext().getFrameStack().putGlobal(thresholdVariable, componentWeigth,
                            connectionWeigth, featureWeigth, flowSpecificationWeigth);

                    // Add models to ecl module
                    eclModule.getContext().getModelRepository().addModel(firstModel);
                    eclModule.getContext().getModelRepository().addModel(secondModel);

                    // execute ecl module
                    eclModule.execute();

                    Double structuralDistance = (Double) eclModule.getContext().getFrameStack()
                            .get("structuralDistance")
                            .getValue();

                    matrix[i][j] = structuralDistance;

                    // initialize csv headers with moldels name
                    if (i == 0) {
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
                } finally {
                    // In any case clear ecl module context to avoid unexpected behavior
                    eclModule.getContext().dispose();
                }

            }

            if (i == 0) {
                String[] headersStringsArray = Arrays.copyOf(headers.toArray(), headers.size(), String[].class);
                csv.add(headersStringsArray);
            }

            String[] csvRowStringsArray = Arrays.copyOf(csvRow.toArray(), csvRow.size(), String[].class);
            csv.add(csvRowStringsArray);

        }

        Utils.writeToCSV(csv, eclConfig.getCsvFileFolderPath(), eclConfig.getCsvFileName());

        // Utils.print2dArray(matrix);

    }


}