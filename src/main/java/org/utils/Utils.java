package org.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

import com.opencsv.CSVWriter;

public class Utils {

    private final static Logger logger = LogManager.getLogger(Utils.class);

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
