pre {
    // https://github.com/tdebatty/java-string-similarity
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");
    
    // Import Math
    var Math = Native("java.lang.Math");

    var connectorsFirstModel = FirstModel!ConnectionInstance.all().size();
    var connectorsSecondModel = SecondModel!ConnectionInstance.all().size();

    var componentsFirstModel = FirstModel!ComponentInstance.all().size();
    var componentsSecondModel = SecondModel!ComponentInstance.all().size();

    // Print System Instatce name
    info();
   
    
    var firstModelName = clearName(FirstModel!ComponentInstance.all().first().name);
    var secondModelName = clearName(SecondModel!ComponentInstance.all().first().name);

    compareByCategory();
    // List of hardware component
    var hardwareCategorySequence:Sequence = Sequence{"device","memory","bus","processor"};
    // List of software component
    var softwareCategorySequence:Sequence = Sequence{"process","thread","thread group","threadGroup", "subprogram","subprogram group","subprogramGroup","virtual bus","virtualBus","virtual processor","virtualProcessor"};
    // List of system component
    var systemCategorySequence:Sequence = Sequence{"system"};
    
    var editDistance = numberOfComponentDistance()*0.5+ numberOfConnectorDistance()*0.5;
}

rule StructuralSimilarity 
    match l : FirstModel!ComponentInstance
    with r : SecondModel!ComponentInstance {

    compare : l.name.fuzzyMatch(r.name)
}


operation String fuzzyMatch(other : String) : Boolean {

   // levenshtein.distance(clearName(self),clearName(other)).println();
    return true;
}

rule CompareByCategory{

}

operation compareByCategory(){
    for(componentsFirstModel in FirstModel!ComponentInstance.all){
	    var categoryComponentFirstModel = componentsFirstModel.category;
        for(componentsSecondModel in SecondModel!ComponentInstance.all){
            var categoryComponentSecondModel = componentsSecondModel.category;
            if(categoryComponentFirstModel.toString().equalsIgnoreCase(categoryComponentSecondModel.toString())){
               // levenshtein.distance(clearName(componentsFirstModel.name),clearName(componentsSecondModel.name)).println();
            }else{
                // "Nothing to do! Different category".println();
            }
        }
    }
}

// 
operation numberOfComponentDistance(): Real{
    ("# di componenti dei modelli: "+ componentsFirstModel + " | " + componentsSecondModel).println();

    var firstModelHWComponents = 0;
    var firstModelSWComponents = 0;
    var firstModelSysComponents = 0;
    
    var secondModelHWComponents = 0;
    var secondModelSWComponents = 0;
    var secondModelSysComponents = 0;

    for(componentsFirstModel in FirstModel!ComponentInstance.all){
	    var categoryComponentFirstModel = componentsFirstModel.category.toString();
        if(hardwareCategorySequence.indexOf(categoryComponentFirstModel) > -1){
            firstModelHWComponents++;
        }
        if(softwareCategorySequence.indexOf(categoryComponentFirstModel) > -1){
            firstModelSWComponents++;
        }
        if(systemCategorySequence.indexOf(categoryComponentFirstModel) > -1){
            firstModelSysComponents++;
        }
       
    }
    for(componentsSecondModel in SecondModel!ComponentInstance.all){
        var categoryComponentSecondModel = componentsSecondModel.category.toString();
        if(hardwareCategorySequence.indexOf(categoryComponentSecondModel) > -1){
            secondModelHWComponents++;
        }
        if(softwareCategorySequence.indexOf(categoryComponentSecondModel) > -1){
            secondModelSWComponents++;
        }
        if(systemCategorySequence.indexOf(categoryComponentSecondModel) > -1){
            secondModelSysComponents++;
        }
    }
    var distanceHW = Math.abs(firstModelHWComponents - secondModelHWComponents).asDouble()/Math.max(firstModelHWComponents,secondModelHWComponents).asDouble();
    var distanceSW= Math.abs(firstModelSWComponents - secondModelSWComponents).asDouble()/Math.max(firstModelSWComponents,secondModelSWComponents).asDouble();
    var distanceSys = Math.abs(firstModelSysComponents - secondModelSysComponents).asDouble()/Math.max(firstModelSysComponents,secondModelSysComponents).asDouble();
    
    var weightedDistance= distanceHW * 0.45+ distanceSW*0.45 +distanceSW *0.1;
    
    // Differenza della numerosità di componenti in valore assoluto / massimo numero di componenti;
    return weightedDistance;
     
}

operation numberOfConnectorDistance(): Real{
    ("# di connettori dei modelli: "+ connectorsFirstModel + " | " + connectorsSecondModel).println();
    // Differenza della numerosità di connettori in valore assoluto / massimo numero di connettori;
    return Math.abs(connectorsFirstModel - connectorsSecondModel).asDouble()/Math.max(connectorsFirstModel,connectorsSecondModel).asDouble();  
}

// Print name of the component
operation info() {
    "##################################################################################################################".println();
    ("Evaluating: "+ FirstModel!SystemInstance.all().first().name +" | "+SecondModel!SystemInstance.all().first().name).println();
}


// Return the original name or the name without this_

operation clearName(name : String): String{

    // Check if name length is at least 5
    if (name.length() > 4){
        // Check if firsts 5 char are equal to this_
        if(name.substring(0,5).equalsIgnoreCase("this_")){
            // Trim this_
            name = name.substring(5);
        }
    }
    // Check if name length is at least 14 (_impl_Instance)
    if (name.length() > 14){
        // Check if last 14 char are equal to _impl_Instance
        if(name.substring((name.length()- 14),name.length()).equalsIgnoreCase("_impl_Instance")){
            // Trim _impl_Instance
            name = name.substring(0, (name.length()- 14));
        }
    }
    return name;
}



/*
IDEA per MATRICE
Similarità strutturale calcolata come distanza di edit
Edit distance: given a cost function on edit operations (e.g. addition/deletion of nodes and edges), determine the minimum cost
transformation from one graph to another. In our case we consider the cost of addition/deletion equals to 1, nodes= components and edges= connectors

Evry element of matrix is computed as described below:

element[i][j]= (|(n. components first model - n. components second model) / max(n. components first model,n. components second model)|)*weigth +
                (|(n. connectors first model - n. connectors second model) / max(n. connectors first model,n. connectors second model)|)*weigth;

where actual weigth= 0.5
result example below
MATRIX(using only example models)
0.0     0.842   0.943   0.965   0.951   0.733   0.5
0.842   0.0     0.691   0.792   0.726   0.458   0.683
0.943   0.691   0.0     0.29    0.09    0.814   0.886
0.965   0.792   0.29    0.0     0.225   0.882   0.929
0.951   0.726   0.09    0.225   0.0     0.839   0.902
0.733   0.458   0.814   0.882   0.839   0.0     0.467
0.5     0.683   0.886   0.929   0.902   0.467   0.0

Execution time ~ 2 seconds

*/
