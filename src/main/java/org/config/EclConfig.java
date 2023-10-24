package org.config;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.utils.Utils;

import lombok.Data;

/**
 * @author Mauro Sonzogni
 */
@Data
public class EclConfig {

    private final static String eclConfigFilePath = "/ecl.config.json";

    private final static Logger logger = LogManager.getLogger(EclConfig.class);

    //if you want to skip all ecl operation set it to true in ecl.config.json
    private Boolean enabled = false;

    private String eclScriptsFolderPath;
    private String eclScriptName;
    private String csvFileFolderPath;
    private String csvFileName;
    private EclParams eclParams;

    public EclConfig() throws Exception {
        try {
            logger.info("Confiuring ECL...");
            JSONObject eclConfiguration = Utils.readJSONFile(eclConfigFilePath);

            this.enabled = eclConfiguration.getBoolean("enabled");
            // if user don't want perform ecl is useless do thinghs
            if (enabled) {
                this.eclScriptsFolderPath = eclConfiguration.getString("eclScriptsFolderPath");
                this.eclScriptName = eclConfiguration.getString("eclScriptName");
                this.csvFileFolderPath = eclConfiguration.getString("csvFileFolderPath");
                this.csvFileName = eclConfiguration.getString("csvFileName");

                // Configure ecl params
                JSONObject eclParamsObject = eclConfiguration.getJSONObject("eclParams");

                this.eclParams = new EclParams(eclParamsObject.getDouble("threshold"),
                        eclParamsObject.getDouble("componentWeigth"), eclParamsObject.getDouble("connectionWeigth"),
                        eclParamsObject.getDouble("featureWeigth"));
            } else {
                logger.info(
                        "ECL operations are diasbled, if you want to enable please provide to set to true field 'enabled' in ecl.config.json");

            }
        } catch (Exception e) {
            logger.error(e);
        }

    }

}
