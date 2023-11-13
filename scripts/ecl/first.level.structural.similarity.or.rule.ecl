pre {
    // Import Math
    var Math = Native("java.lang.Math");
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");

    var firstModelName = clearName(FirstModel!SystemInstance.all().first().name);
    var secondModelName = clearName(SecondModel!SystemInstance.all().first().name);
    // 0 = model are equals
    // 1 = model are complitely different.
    var structuralDistance: Real= 1.asDouble();

    var componentDistance: Real= 0.asDouble();
    var connectionDistance: Real= 0.asDouble();
    var featureDistance: Real= 0.asDouble();
    var flowSpecificationDistance: Real= 0.asDouble();

}


/* 
* 
*/
@greedy
rule CompareByName
    match firstModel: FirstModel!SystemInstance
    with secondModel: SecondModel!SystemInstance {
        // check if category are equals, that's avoid to compare SW  with HW 
        guard : true
        // AND compare
        // valutare se mettere una variabile per ogni elemento per capire se si vuole controllare o no
        compare: (checkElementSimilarity(firstModel.componentInstance, secondModel.componentInstance, componentWeigth) or checkElementSimilarity(firstModel.connectionInstance,secondModel.connectionInstance, connectionWeigth) or checkElementSimilarity(firstModel.featureInstance,secondModel.featureInstance,featureWeigth) or checkElementSimilarity(firstModel.flowSpecification,secondModel.flowSpecification, flowSpecificationWeigth))
        do {
            "DO...".println();
            if(componentWeigth != 0){
                componentDistance = numberOfElementDistance(firstModel.componentInstance.size(),secondModel.componentInstance.size())*componentWeigth.asDouble();
                "COMPONENT DISTANCE: ".println();
                componentDistance.println();
                numberOfElementDistance(firstModel.componentInstance.size(),secondModel.componentInstance.size()).asDouble().println();
            }
            if(connectionWeigth != 0){
                connectionDistance = numberOfElementDistance(firstModel.connectionInstance.size(),secondModel.connectionInstance.size())*connectionWeigth.asDouble();
                "CONNECTION DISTANCE: ".println();
                connectionDistance.println();
            }
            if(featureWeigth != 0){
                featureDistance = numberOfElementDistance(firstModel.featureInstance.size(),secondModel.featureInstance.size())*featureWeigth.asDouble();
                "FEATURE DISTANCE: ".println();
                featureDistance.println();
            }
            if(flowSpecificationWeigth != 0){
                flowSpecificationDistance = numberOfElementDistance(firstModel.flowSpecification.size(),secondModel.flowSpecification.size())*flowSpecificationWeigth.asDouble();
                "FLOW SPEC DISTANCE: ".println();
                flowSpecificationDistance.println();
            }

            structuralDistance = componentDistance + connectionDistance + featureDistance + flowSpecificationDistance;
            "STRUCTURAL DISTANCE: ".println();
            structuralDistance.println();
            
        }

}


// CHECK ELEMENTS (ELEMENTS CAN BE: componentInstances, connectionInstances, featureInstances, flowSpecification)
operation checkElementSimilarity(elementsFirstModel: Any, elementsSecondModel: Any, elementsWeigth: Real): Boolean{
    "CheckElementSimilarity:".println();
    // Spilt conditions for more readable code
    // if elementsWeigth is 0, return true to skip useless computations
    if ((elementsFirstModel.size()== 0 and elementsSecondModel.size()==0) or (elementsWeigth == 0)){
        return true;
    }
    // return true if there is at least one match, false otherwise
    return (elementsFirstModel.select(e|elementsSecondModel.select(el|(levenshtein.distance(clearName(el.name),clearName(e.name)) < threshold)).size()> 0).size()>0);
     
}



// ELEMENT
operation numberOfElementDistance(elementsFirstModel: Integer, elementsSecondModel: Integer): Real{
    ("# di elementi nei modelli: "+ elementsFirstModel + " | " + elementsFirstModel).println();
    // Avoid 0/0 division
    if (elementsFirstModel == 0 and elementsSecondModel == 0){
        return 0;
    }
    // Differenza della numerosità di componenti in valore assoluto / massimo numero di componenti;
    return (Math.abs(elementsFirstModel - elementsSecondModel).asDouble()/Math.max(elementsFirstModel,elementsSecondModel).asDouble()).asDouble();  
}




// Return the original name or the name cleared
operation clearName(name : String): String{
   
    // Check if name length is at least 14 (_impl_instance)
    if (name.length() > 14){
        // Check if last 14 char are equal to _impl_instance
        if(name.substring((name.length()- 14),name.length()).equalsIgnoreCase("_impl_instance")){
            // Trim _impl_Instance
            name = name.substring(0, (name.length()- 14));
        }    
    }

    // Check if name length is at least 13 (_imp_instance)
    if (name.length() > 13){
        // Check if last 13 char are equal to _imp_instance
        if(name.substring((name.length()- 13),name.length()).equalsIgnoreCase("_imp_instance")){
            // Trim _impl_Instance
            name = name.substring(0, (name.length()- 13));
        } 
    }

    if (name.length() > 9){
        // Check if last 9 char are equal to _impl_Instance
        if(name.substring((name.length()- 9),name.length()).equalsIgnoreCase("_instance")){
            // Trim _instance
            name = name.substring(0, (name.length()- 9));
        }
    }

     // Check if name length is at least 5
     if (name.length() > 4){
        // Check if firsts 5 char are equal to this_
        if(name.substring(0,5).equalsIgnoreCase("this_")){
            // Trim this_
            name = name.substring(5);
        }
    }
    return name;
}









