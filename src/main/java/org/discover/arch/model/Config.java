package org.discover.arch.model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@Setter
@Getter
public class Config {
    private final static String configPath = "/config.json";
    private String rootPath;
    private String outputFolderName;
    private String ecoreRequiredFilesFolder;
    private List<String> archivesForSearching;
    private List<String> extensionsForSearching;
    private List<String> externalResources;
    private List<String> avoidFileNames;

    private List<Map<String, Object>> conversionLogs;
    private List<String> filesFound;
    private Map<String, Object> reports;
    private Map<String, Date> cache;
    public int timeCacheForDiscoveringSearchOverFilesInSeconds;
    public int timeCacheForPollingFromExternalResources;

    private SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private final static Logger logger = LogManager.getLogger(Config.class);

    public Config() throws Exception {

        logger.debug("Config@config -> Start configuration");

        JSONObject config = readConfigFile();

        // set root path
        this.rootPath = config.getString("rootPath");
        // check fot root path
        this.validateRootPath();
        // set output folder
        this.outputFolderName = config.getString("outputFolderName");
        // set ecore files folder
        this.ecoreRequiredFilesFolder = config.getString("ecoreRequiredFilesFolder");
        // set cache for discovering
        this.timeCacheForDiscoveringSearchOverFilesInSeconds = config
                .getInt("timeCacheForDiscoveringSearchOverFilesInSeconds");
        // set cache for polling
        this.timeCacheForPollingFromExternalResources = config
                .getInt("timeCacheForPollingFromExternalResources");

        // set archives for searching
        this.archivesForSearching = this.fromJSONArrayToArrayList(config.getJSONArray("archivesForSearching"));
        // set extension for searching
        this.extensionsForSearching = this.fromJSONArrayToArrayList(config.getJSONArray("extensionsForSearching"));
        // set external resources
        this.externalResources = this.fromJSONArrayToArrayList(config.getJSONArray("externalResources"));
        // set file names to avoid
        this.avoidFileNames = this.fromJSONArrayToArrayList(config.getJSONArray("avoidFileNames"));

        logger.debug("Config@Config() -> End configuartion");

        this.conversionLogs = new ArrayList<>();
        this.filesFound = new ArrayList<>();
        this.reports = new HashMap<>();

        this.loadCache();
    }

    /**
     * Method that take in input a json array and return an array list
     * TODO: move in utilities class?
     * 
     * @param jsonArray
     * @return List<String>
     */
    private List<String> fromJSONArrayToArrayList(JSONArray jsonArray) {
        // initialize new array list
        List<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getString(i));
        }
        return arrayList;
    }

    /**
     * Read config file frome the specified location
     * 
     * @return
     * @throws Exception
     */
    private static JSONObject readConfigFile() throws Exception {
        logger.debug("Config@readConfigFile()-> Read configuration file");
        // read config file from config path
        InputStream is = Config.class.getResourceAsStream(configPath);
        if (is == null) {
            throw new NullPointerException("Cannot find resource file " + configPath);
        }
        JSONTokener tokener = new JSONTokener(is);
        JSONObject data = new JSONObject(tokener);
        return data;
    }

    /**
     * Try to create a file in root path defined in the config.json file
     * 
     * @throws Exception
     */
    private void validateRootPath() throws Exception {
        File file = new File(this.rootPath);
        if (!file.exists())
            throw new Exception("The root path: " + this.rootPath + " does not exists");
    }

    /**
     * Add path for searching if not already exists
     * 
     * @param path
     */
    public void addMoreArchivesForSearching(String path) {
        if (!this.archivesForSearching.contains(path)) {
            this.archivesForSearching.add(path);
            this.saveConfig();
        }
    }

    public boolean saveConfig() {
        try {
            String jsonStr = new JSONObject(this.archivesForSearching).toString(2);
            FileWriter fw = new FileWriter(Paths.get(configPath).toString());
            fw.write(jsonStr);
            fw.close();
            return true;
        } catch (Exception e) {
            System.out.println("ERROR SAVING THE CONFIG JSON");
            e.printStackTrace();
            return false;
        }
    }

    public boolean createFolderOutput() throws Exception {

        if (this.isInCache("createFolderOutput", this.timeCacheForDiscoveringSearchOverFilesInSeconds)) {

            logger.info("Config@createFolderOutput() -> OUTPUT FOLDER WAS CREATED BEFORE, THE CURRENT TIME INVALIDATION CACHE IS: "
                            + this.timeCacheForDiscoveringSearchOverFilesInSeconds + "s");
            return false;
        }

        
        File file = Paths.get(this.rootPath, this.outputFolderName).toFile();
        file.mkdir();
        for (File childFile : Objects.requireNonNull(file.listFiles())) {
            deleteDirectory(childFile.toPath());
        }
        for (String ext : this.extensionsForSearching) {
            new File(Paths.get(file.getPath(), ext).toString()).mkdir();
        }
        new File(Paths.get(file.getPath(), "xmi").toString()).mkdir();
        this.putInCache("createFolderOutput");


        return true;
    }

    public static void deleteDirectory(Path path) throws IOException {
        File file = new File(path.toAbsolutePath().toString());
        if (file.isDirectory()) {
            FileUtils.deleteDirectory(file);
        } else {
            FileUtils.forceDelete(file);
        }
    }

    public void loadJSONFilesGeneratedByDiscoveringPhase() {
        Path outputPathFolder = Paths.get(this.rootPath, this.outputFolderName).toAbsolutePath();
        File conversionLogsFile = outputPathFolder.resolve("conversion-logs.json").toFile();
        File filesFoundFile = outputPathFolder.resolve("files-found.txt").toFile();
        File reportsLogsFile = outputPathFolder.resolve("reports-logs.json").toFile();
        this.conversionLogs = new ArrayList<>();
        this.filesFound = new ArrayList<>();
        this.reports = new HashMap<>();
        try {
            if (conversionLogsFile.exists()) {
                String read = String.join("\n", Files.readAllLines(conversionLogsFile.toPath()));
                for (Object object : new JSONArray(read)) {
                    JSONObject jsonObject = (JSONObject) object;
                    this.conversionLogs.add(jsonObject.toMap());
                }
            }
        } catch (Exception e) {
            System.out.println(
                    "Error in loading the .json files generated by the discovering phase: -> loading \"conversion-logs.json\"");
        }
        try {
            if (reportsLogsFile.exists()) {
                String read = String.join("\n", Files.readAllLines(reportsLogsFile.toPath()));
                this.reports = new JSONObject(read).toMap();
            }
        } catch (Exception e) {
            System.out.println(
                    "Error in loading the .json files generated by the discovering phase: -> loading \"reports-logs.json\"");
        }
        try {
            if (filesFoundFile.exists()) {
                this.filesFound = Files.readAllLines(filesFoundFile.toPath());
            }
        } catch (Exception e) {
            System.out.println(
                    "Error in loading the .json files generated by the discovering phase: -> loading \"files-found.txt\"");
        }

    }

    /*
     * 
     * Cache Area
     * Todo: valutare se serve a qualcosa effettivamente
     * 
     * 
     */
    public void loadCache() {
        Path cachePath = Paths.get("storage/cache", "cache.txt").toAbsolutePath();
        File cacheFile = cachePath.toFile();

        logger.debug("Config@loadCache() -> LOADING CACHE FROM DISK");
        this.cache = new HashMap<>();
        if (cacheFile.exists()) {
            try {
                for (String line : Files.readAllLines(cachePath)) {
                    String[] data = line.split(",");
                    this.cache.put(data[0].trim(), this.formatterDate.parse(data[1].trim()));
                }
            } catch (Exception e) {
                logger.error("Config@loadCache() -> " + e.getMessage());
            }
        } else {
            try {
                Files.createFile(cachePath);
            } catch (Exception e) {
                logger.error("Config@loadCache() -> " + e.getMessage());
            }

        }
    }

    /**
     * put in cache the key passed in input with date as value in the following
     * format 24-08-2023 13:33:59
     * 
     * @param key
     */
    public void putInCache(String key) {
        this.cache.put(key, new Date());
    }

    /**
     * Check if key is in cache, if it's in cache return false, if difference
     * between cache time and now is greater than
     * delay param remove key from cache and return flase, true otherwise.
     * 
     * @param key
     * @param delay
     * @return true/false
     * 
     */
    public boolean isInCache(String key, int delay) {
        // check if key is contained in cache
        if (!this.cache.containsKey(key))
            return false;

        Date now = new Date();
        // Calculate time difference in seconds
        long diffSeconds = (now.getTime() - this.cache.get(key).getTime()) / 1000;

        if (diffSeconds > delay) {
            this.cache.remove(key);
            return false;
        }

        return true;
    }

    public void persistCacheInDisk() {
        Path cachePath = Paths.get("storage/cache", "cache.txt").toAbsolutePath();
        File cacheFile = cachePath.toFile();

        if (!cacheFile.exists()) {
            try {
                Files.createFile(cachePath);
            } catch (Exception error) {
                logger.error("Config@persistCacheInDisk() -> " + error.getMessage());
                error.printStackTrace();
            }
        }

        List<String> data = new ArrayList<>();
        for (Map.Entry<String, Date> entry : this.cache.entrySet()) {
            data.add(entry.getKey() + "," + this.formatterDate.format(entry.getValue()));
        }
        try {
            Files.write(cachePath, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
