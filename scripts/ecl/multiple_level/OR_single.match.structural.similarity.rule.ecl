/**
* A simple ecl file that provide rules to compute model similarity.
* To exclude specific rules from analisys is necessary edit ecl.config.json file and set to 0 the weigth of
* element yo want to exclude.
* The commented println() below can be usefull for debugging purpouse, in the other hand they help to increase execution time (tip: enable iff needed)
*
* NOTE: 
* if both models don't have a specific element (ComponentInstace,ConnectionIntsance,FeatureInstance,FlowSpecification) that will be considered
* as a "things" that they have in common
*
* @Author Sonzogni Mauro
*/


/**
* Executed before all
*/
pre {
    // Import Math
    var Math = Native("java.lang.Math");
    // Import NormalizedLevenshtein similarity
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
* Compare Components and/or Systems
*/
@greedy
rule CompareComponentsByName
    match firstModelComponents: FirstModel!ComponentInstance
    with secondModelComponents: SecondModel!ComponentInstance {
        // guard is computed only on ComponentInstance and SystemInstance, the second because rule is annotated as @greedy
        // check if componentWeigth is greater than 0 (weigth range must be (0,1])
        // check if category of components are equals, it will be a waste of time compare for example software component with hardware component
        guard : (componentWeigth>0) and firstModelComponents.category.toString().equalsIgnoreCase(secondModelComponents.category.toString())
        // compare is compuded only if guard expression is true
        // check if name similarity is less than a given threshold, similarity value will be between [0,1], 0 = words are equals
        compare: levenshtein.distance(clearName(firstModelComponents.name),clearName(secondModelComponents.name)) < threshold
        // do statement is executed only if compare expression is true
        do {
            //"DO COMPONENT...".println();
            componentMatch = componentMatch + 1;       
        }
}


/* 
* Compare Features
*/
rule CompareFeaturesByName
    match firstModelFeatures: FirstModel!FeatureInstance
    with secondModelFeatures: SecondModel!FeatureInstance {
        guard : (featureWeigth>0) and firstModelFeatures.category.toString().equalsIgnoreCase(secondModelFeatures.category.toString())
        compare: levenshtein.distance(clearName(firstModelFeatures.name),clearName(secondModelFeatures.name)) < threshold
        do {
            //"DO FEATURES...".println();
            featureMatch = featureMatch + 1;       
        }
}

/* 
* Compare Connections
*/
rule CompareConnectionsByName
    match firstModelConnections: FirstModel!ConnectionInstance
    with secondModelConnections: SecondModel!ConnectionInstance {
        guard : (connectionWeigth > 0)
        compare: levenshtein.distance(clearName(firstModelConnections.name),clearName(secondModelConnections.name)) < threshold
        do {
            //"DO CONNECTIONS...".println();
            connectionMatch = connectionMatch + 1;       
        }
}

/* 
* Compare FlowSpecifications
*/
rule CompareFlowSpecificationsByName
    match firstModelFlowSpecifications: FirstModel!FlowSpecification
    with secondModelFlowSpecifications: SecondModel!FlowSpecification {
        guard : (flowSpecificationWeigth > 0)
        compare: levenshtein.distance(clearName(firstModelFlowSpecifications.name),clearName(secondModelFlowSpecifications.name)) < threshold
        do {
            //"DO FLOW SPECIFICATION...".println();
            flowSpecificationMatch = flowSpecificationMatch + 1;       
        }
}

/**
* executed after all
*/
post {
    
    if(componentWeigth > 0){
        componentDistance = numberOfElementDistance(FirstModel!ComponentInstance.all().size(), SecondModel!ComponentInstance.all().size(), componentMatch)*componentWeigth.asDouble();
        //("COMPONENT DISTANCE: " + componentDistance).println();
    }
    if(connectionWeigth > 0){
        connectionDistance = numberOfElementDistance(FirstModel!ConnectionInstance.all().size(), SecondModel!ConnectionInstance.all().size(), connectionMatch)*connectionWeigth.asDouble();
        //("CONNECTION DISTANCE: " + connectionDistance).println();
    }
    if(featureWeigth > 0){
        featureDistance = numberOfElementDistance(FirstModel!FeatureInstance.all().size(), SecondModel!FeatureInstance.all().size(), featureMatch)*featureWeigth.asDouble();
        //("FEATURE DISTANCE: " + featureDistance).println();
        
    }
    if(flowSpecificationWeigth > 0){
        flowSpecificationDistance = numberOfElementDistance(FirstModel!FlowSpecification.all().size(), SecondModel!FlowSpecification.all().size(), flowSpecificationMatch)*flowSpecificationWeigth.asDouble();
        //("FLOW SPEC DISTANCE: " + flowSpecificationDistance).println(); 
    }

    // 0 = model are equals
    // 1 = model are complitely different.
    var structuralDistance = 1 - (componentDistance + connectionDistance + featureDistance + flowSpecificationDistance).asDouble();
    //("STRUCTURAL DISTANCE: " + structuralDistance).println();
            
}


/**
* @param elementsFirstModel
* @param elementsSecondModel
* @param matchingElements
* @return Real
*/
operation numberOfElementDistance(elementsFirstModel: Integer, elementsSecondModel: Integer, matchingElements: Integer): Real{
    //("# elements: "+ elementsFirstModel + " | " + elementsFirstModel).println();
    // If both models don't have a certain type of element, it is as if they have all those elements in common
    if ((elementsFirstModel == 0 and elementsSecondModel == 0) or (matchingElements > elementsFirstModel and matchingElements > elementsSecondModel)){
        return 1.asDouble();
    }
    if (matchingElements== 0){
        return 0.asDouble();
    }
    //
    return (matchingElements/Math.max(elementsFirstModel,elementsSecondModel)).asDouble();  
}

/**
* Return the original name or the name cleared
* @param name
* @return String
*/
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

    // Check if name length is at least 9 (_Instance)
    if (name.length() > 9){
        // Check if last 9 char are equal to _Instance
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









