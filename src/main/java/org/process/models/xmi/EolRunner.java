package org.process.models.xmi;

import org.eclipse.epsilon.eol.EolModule;
import org.utils.Utils;

import lombok.NoArgsConstructor;

import org.eclipse.epsilon.emc.emf.EmfModel;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@NoArgsConstructor
public class EolRunner implements QueryModel {
   
    private static EolRunner INSTANCE = null;

    static public EolRunner getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EolRunner();
        }
        return INSTANCE;
    }

    /**
     * @param eolScript: Name of the .eol script
     * @param modelPath: Path of the aaxl2 instance model
     * @return Map that contains the result of several metrics computed over the
     *         model
     */
    @Override
    public Map<String, Object> run(String eolScript, String modelPath) throws Exception {
        if (eolScript == null)
            eolScript = "main";
        EolModule module = new EolModule();
        Path rootPath = Paths.get("scripts", "eol").toAbsolutePath();
        String eolPath = rootPath.resolve(eolScript + ".eol").toString();
        String metaModelPath = Paths.get("ecore", "aadl2_inst.ecore").toAbsolutePath().toString();
        EmfModel model = Utils.createEmfModel("ModelImpl", modelPath, metaModelPath, true, false);
        module.parse(new File(eolPath));
        module.getContext().getModelRepository().addModel(model);
        return (Map<String, Object>) module.execute();
    }


    @Override
    public Map<String, Object> run(String modelPath) throws Exception {
        return this.run("main", modelPath);
    }

    @Override
    public Map<String, Object> run(String modelPath, Map<String, Object> data) throws Exception {
        return this.run("main", modelPath);
    }

}
