pre {
    // https://github.com/tdebatty/java-string-similarity
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");
    
    // Import Math
    var Math = Native("java.lang.Math");
    

    var firstModelName = clearName(FirstModel!ComponentInstance.all().first().name);
    var secondModelName = clearName(SecondModel!ComponentInstance.all().first().name);
    // List of hardware component
    var hardwareCategorySequence:Sequence = Sequence{"device","memory","bus","processor"};
    // List of software component
    var softwareCategorySequence:Sequence = Sequence{"process","thread","thread group","threadGroup", "subprogram","subprogram group","subprogramGroup","virtual bus","virtualBus","virtual processor","virtualProcessor"};
    // List of system component
    var systemCategorySequence:Sequence = Sequence{"system"};
    
    var editDistance = 1.asDouble();
    var connectorsFirstModel = FirstModel!ConnectionInstance.all().size();
    var connectorsSecondModel = SecondModel!ConnectionInstance.all().size();

    var componentsFirstModel = FirstModel!ComponentInstance.all().size();
    var componentsSecondModel = SecondModel!ComponentInstance.all().size();
}

    



rule CompareByComponentName
    match componentsFirstModel: FirstModel!ComponentInstance
    with componentsSecondModel: SecondModel!ComponentInstance {
        // check if category are equals, that's avoid to compare SW components with HW components
        guard : componentsFirstModel.category.toString().equalsIgnoreCase(componentsSecondModel.category.toString())  
        // 0.0 if are equals
        compare: levenshtein.distance(clearName(componentsFirstModel.name),clearName(componentsSecondModel.name)).asDouble() < threshold.asDouble()
        do {
            "INZIO".println();
            levenshtein.distance(clearName(componentsFirstModel.name),clearName(componentsSecondModel.name)).asDouble().println();

            clearName(componentsFirstModel.name).println();
            clearName(componentsSecondModel.name).println();
            editDistance = (numberOfComponentDistance()*componentDistanceWeigth.asDouble()+ numberOfConnectorDistance()*connectorDistanceWeigth.asDouble());
            editDistance.println();
            "FINE".println();
        }

}



operation numberOfComponentDistance(): Real{
    ("# di componenti dei modelli: "+ componentsFirstModel + " | " + componentsSecondModel).println();
    // Differenza della numerosità di componenti in valore assoluto / massimo numero di componenti;
    return Math.abs(componentsFirstModel - componentsSecondModel).asDouble()/Math.max(componentsFirstModel,componentsSecondModel).asDouble();
     
}

operation numberOfConnectorDistance(): Real{
    ("# di connettori dei modelli: "+ connectorsFirstModel + " | " + connectorsSecondModel).println();
    // Differenza della numerosità di connettori in valore assoluto / massimo numero di connettori;
    return Math.abs(connectorsFirstModel - connectorsSecondModel).asDouble()/Math.max(connectorsFirstModel,connectorsSecondModel).asDouble();  
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



