package org.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.utils.Utils;

import lombok.Data;


@Data
public class EclConfig {

    private final static String eclConfigFilePath = "/ecl.config.json";

    private final static Logger logger = LogManager.getLogger(EclConfig.class);

    private String eclScriptsFolderPath;
    private String eclScriptName;
    private String csvFileFolderPath;
    private String csvFileName;
    private EclParams eclParams;
    

    public EclConfig() throws Exception {
        JSONObject eclConfiguration = Utils.readJSONFile(eclConfigFilePath);
        //
        this.eclScriptsFolderPath = eclConfiguration.getString("eclScriptsFolderPath");
        this.eclScriptName = eclConfiguration.getString("eclScriptName");
        this.csvFileFolderPath = eclConfiguration.getString("csvFileFolderPath");
        this.csvFileName = eclConfiguration.getString("csvFileName");

        // Configure ecl params
        JSONObject eclParamsObject = eclConfiguration.getJSONObject("eclParams");

        this.eclParams= new EclParams(eclParamsObject.getDouble("threshold"),eclParamsObject.getDouble("componentWeigth"),eclParamsObject.getDouble("connectionWeigth"), eclParamsObject.getDouble("featureWeigth"));

    }

    

}
