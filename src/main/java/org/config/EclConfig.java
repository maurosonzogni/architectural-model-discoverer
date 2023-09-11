package org.config;

import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EclConfig {

    private final static String eclConfigFilePath = "/ecl.config.json";

    private final static Logger logger = LogManager.getLogger(EclConfig.class);

    private String eclScriptsFolderPath;
    private String eclScriptName;
    private String csvFileFolderPath;
    private String csvFileName;
    private EclParams eclParams;
    

    public EclConfig() throws Exception {
        JSONObject eclConfiguration = readEclConfigurationFile();
        //
        this.eclScriptsFolderPath = eclConfiguration.getString("eclScriptsFolderPath");
        this.eclScriptName = eclConfiguration.getString("eclScriptName");
        this.csvFileFolderPath = eclConfiguration.getString("csvFileFolderPath");
        this.csvFileName = eclConfiguration.getString("csvFileName");

        // Configure ecl params
        JSONObject eclParamsObject = eclConfiguration.getJSONObject("eclParams");

        this.eclParams= new EclParams(eclParamsObject.getDouble("threshold"));

    }

    /**
     * Read configuration file from the specified location
     * 
     * @return
     * @throws Exception
     */
    private static JSONObject readEclConfigurationFile() throws Exception {

        logger.debug("EclConfig@readEclConfigurationFile()-> Reading configuration file");
        // read config file from config path
        InputStream inputStream = EclConfig.class.getResourceAsStream(eclConfigFilePath);
        if (inputStream == null) {
            throw new NullPointerException("Cannot find resource file " + eclConfigFilePath);
        }
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONObject configuration = new JSONObject(tokener);
        return configuration;
    }

}
