pre {
    // Import Math
    var Math = Native("java.lang.Math");
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");

    var firstModelName = clearName(FirstModel!SystemInstance.all().first().name);
    var secondModelName = clearName(SecondModel!SystemInstance.all().first().name);

    var componentDistance: Real= 0.asDouble();
    var connectionDistance: Real= 0.asDouble();
    var featureDistance: Real= 0.asDouble();
    var flowSpecificationDistance: Real= 0.asDouble();

    var componentMatch: Integer = 0;
    var featureMatch: Integer = 0;
    var connectionMatch: Integer = 0;
    var flowSpecificationMatch: Integer = 0;
    
}


/* 
* Check Component and Systems
*/
@greedy
rule CompareComponentsByName
    match firstModelComponents: FirstModel!ComponentInstance
    with secondModelComponents: SecondModel!ComponentInstance {
        // check if category are equals, that's avoid to compare SW  with HW 
        guard : (componentWeigth>0) and firstModelComponents.category.toString().equalsIgnoreCase(secondModelComponents.category.toString())
        // OR compare
        compare: levenshtein.distance(clearName(firstModelComponents.name),clearName(secondModelComponents.name)) < threshold
        do {
            //"DO COMPONENT...".println();
            componentMatch = componentMatch + 1;       
        }

}



rule CompareFeaturesByName
    match firstModelFeatures: FirstModel!FeatureInstance
    with secondModelFeatures: SecondModel!FeatureInstance {
        // check if category are equals, that's avoid to compare SW  with HW 
        guard : (featureWeigth>0) and firstModelFeatures.category.toString().equalsIgnoreCase(secondModelFeatures.category.toString())
        // OR compare
        compare: levenshtein.distance(clearName(firstModelFeatures.name),clearName(secondModelFeatures.name)) < threshold
        do {
            //"DO FEATURES...".println();
            featureMatch = featureMatch + 1;       
        }

}

rule CompareConnectionsByName
    match firstModelConnections: FirstModel!ConnectionInstance
    with secondModelConnections: SecondModel!ConnectionInstance {
        // Mettere check su kind se proprio
        guard : (connectionWeigth > 0)
        // OR compare
        compare: levenshtein.distance(clearName(firstModelConnections.name),clearName(secondModelConnections.name)) < threshold
        do {
            //"DO CONNECTIONS...".println();
            connectionMatch = connectionMatch + 1;       
        }

}

rule CompareFlowSpecificationByName
    match firstModelFlowSpecifications: FirstModel!FlowSpecification
    with secondModelFlowSpecifications: SecondModel!FlowSpecification {
        // check if category are equals, that's avoid to compare SW  with HW 
        guard : (flowSpecificationWeigth > 0)
        // OR compare
        compare: levenshtein.distance(clearName(firstModelFlowSpecifications.name),clearName(secondModelFlowSpecifications.name)) < threshold
        do {
            //"DO FlowSpecification...".println();
            flowSpecificationMatch = flowSpecificationMatch + 1;       
        }

}

post {
    
    if(componentWeigth > 0){
        componentDistance = numberOfElementDistance(FirstModel!ComponentInstance.all().size(), SecondModel!ComponentInstance.all().size(), componentMatch)*componentWeigth.asDouble();
        ("COMPONENT DISTANCE: " + componentDistance).println();
    }
    if(connectionWeigth > 0){
        connectionDistance = numberOfElementDistance(FirstModel!ConnectionInstance.all().size(), SecondModel!ConnectionInstance.all().size(), connectionMatch)*connectionWeigth.asDouble();
        ("CONNECTION DISTANCE: " + connectionDistance).println();
    }
    if(featureWeigth > 0){
        featureDistance = numberOfElementDistance(FirstModel!FeatureInstance.all().size(), SecondModel!FeatureInstance.all().size(), featureMatch)*featureWeigth.asDouble();
        ("FEATURE DISTANCE: " + featureDistance).println();
        
    }
    if(flowSpecificationWeigth > 0){
        flowSpecificationDistance = numberOfElementDistance(FirstModel!FlowSpecification.all().size(), SecondModel!FlowSpecification.all().size(), flowSpecificationMatch)*flowSpecificationWeigth.asDouble();
        ("FLOW SPEC DISTANCE: " + flowSpecificationDistance).println();
        
    }

    // 0 = model are equals
    // 1 = model are complitely different.
    var structuralDistance = 1- (componentDistance + connectionDistance + featureDistance + flowSpecificationDistance).asDouble();
    ("STRUCTURAL DISTANCE: " + structuralDistance).println();
            
}



// ELEMENT
operation numberOfElementDistance(elementsFirstModel: Integer, elementsSecondModel: Integer, matchingElements: Integer): Real{
    ("# di elementi nei modelli: "+ elementsFirstModel + " | " + elementsFirstModel).println();
    // Avoid 0/0 division
    // se abbiamo trovato più match vuol degli elementi di entrambi i modelli significa che potremmo avere più cose simili
    if ((elementsFirstModel == 0 and elementsSecondModel == 0) or (matchingElements>elementsFirstModel and matchingElements>elementsSecondModel)){
        return 1.asDouble();
    }
    //
    return (matchingElements/Math.max(elementsFirstModel,elementsSecondModel)).asDouble();  
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









