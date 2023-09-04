package org.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;

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
     *                                  TODO: valutare se mettere tutto in utils
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
     * This method take in input a 2dArray and print it in console
     * 
     * @param matrix
     */
    public static void print2dArray(int[][] matrix) {
        System.out.println("MATRIX");
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                System.out.print(matrix[r][c] + "\t");
            }
            System.out.println();
        }
    }

}
