package org.osate.standalone.model;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osate.aadl2.instance.InstancePackage;
import org.osate.aadl2.util.Aadl2ResourceFactoryImpl;

public class LoadXMIModel {
    private static LoadXMIModel INSTANCE = null;

    private LoadXMIModel() {
    }

    public static LoadXMIModel getInstance() {
        if (INSTANCE != null)
            return INSTANCE;
        INSTANCE = new LoadXMIModel();
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("aaxl2", new Aadl2ResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
        InstancePackage.eINSTANCE.eClass();
        return INSTANCE;
    }

    /**
     * @param uriModel
     * @return Resource model from the XMI
     */
    public Resource getResourceObjectFromXMIModel(String uriModel) throws Exception {

        final ResourceSet rs = new ResourceSetImpl();
        final Resource resource = rs.getResource(URI.createFileURI(uriModel), true);
        resource.load(null);
        return resource;
    }

}
