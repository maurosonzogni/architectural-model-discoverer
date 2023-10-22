package org.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.config.EclConfig;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osate.contribution.sei.arinc653.StateInformationType;

import com.opencsv.CSVWriter;

public class Utils {

    private final static Logger logger = LogManager.getLogger(Utils.class);

    /**
     * 
     * @return
     * @throws Exception
     */
    public static List<String> discoverModelFromPath(String stringPath, List<String> modelExtension) throws Exception {

        // Check if path is null
        if (stringPath == null) {
            throw new Exception("There is not root path for reading the XMI models");
        }
        File filePath = Paths.get(stringPath).toFile();

        if (!filePath.exists()) {
            throw new Exception("The path to get the xmi converted files does not exist: " + filePath);
        }

        if (!filePath.isDirectory()) {
            throw new Exception("The file to process the xmi converted models must be a directory");
        }

        List<String> uris = new ArrayList<>();

        Files.walk(Path.of(stringPath)).sorted().map(Path::toFile).forEach(
                (File file) -> {
                    
                    if (file.isFile()) {
                        String path = file.getPath();
                        String fileExtension = FilenameUtils.getExtension(path);
                        if (!fileExtension.isEmpty() && modelExtension.contains(fileExtension) && ! path.contains(" ")) {

                                uris.add(path);
                        }

                    }
                });

        return uris;
    }

    /**
     * Method that allow to create a EmfModel
     * 
     * @param name            the name of the model that will be create
     * @param modelURI
     * @param metaModelURI
     * @param readOnLoad
     * @param storeOnDisposal
     * @return EmfModel
     * @throws EolModelLoadingException
     * 
     */
    public static EmfModel createEmfModel(String name, String modelURI, String metaModelURI, boolean readOnLoad,
            boolean storeOnDisposal)
            throws EolModelLoadingException {

        // Instantiate new EmfModel()
        EmfModel emfModel = new EmfModel();
        // set name
        emfModel.setName(name);
        // set model file
        emfModel.setModelFile(modelURI);
        // set meta-model file
        emfModel.setMetamodelFile(metaModelURI);

        emfModel.setReadOnLoad(readOnLoad);
        emfModel.setStoredOnDisposal(storeOnDisposal);
        // load emf model
        emfModel.load();

        return emfModel;
    }



    /**
     * Read configuration file from the specified location
     * 
     * @return
     * @throws Exception
     */
    public static JSONObject readJSONFile(String path) throws Exception {

        logger.debug("Utils@readJSONFile(String path)-> Reading file: "+ path);
        // read config file from config path
        InputStream inputStream = EclConfig.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new NullPointerException("Cannot find resource file " + path);
        }
        JSONTokener tokener = new JSONTokener(inputStream);
        JSONObject configuration = new JSONObject(tokener);
        return configuration;
    }


     /**
     * Method that take in input a json array and return an array list
     * 
     * @param jsonArray
     * @return List<String>
     */
    public static List<String> fromJSONArrayToArrayList(JSONArray jsonArray) {
        // initialize new array list
        List<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < jsonArray.length(); i++) {
            arrayList.add(jsonArray.getString(i));
        }
        return arrayList;
    }

    /**
     * Get as input a list of string[] and write them on a specified csv row by row
     * 
     * @param lines
     * @param folderPath
     * @param fileName
     * @throws IOException
     */
    public static void writeToCSV(List<String[]> lines, String folderPath, String fileName) throws IOException {

        Path path = Paths.get(folderPath);
        // create folder if not already exists
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        Path filePath = path.resolve(fileName);

        // If file already exist delete it
        Files.deleteIfExists(filePath.toAbsolutePath());

        File file = new File(filePath.toString());
        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(file);

        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile);

        try {
            for (String[] line : lines) {
                writer.writeNext(line);
            }
            // closing writer connection
            writer.close();

        } catch (Exception error) {
            logger.error(error.getMessage());
        }

    }

    /**
     * This method take in input a 2dArray and print it in console
     * 
     * @param matrix
     */
    public static void print2dArray(Double[][] matrix) {
        System.out.println("MATRIX");
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                System.out.print(matrix[r][c] + "\t");
            }
            System.out.println();
        }
    }

}
