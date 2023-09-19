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
}



rule CompareByCategory
    
    match componentsFirstModel: FirstModel!ComponentInstance
    with componentsSecondModel: SecondModel!ComponentInstance {
        // check if category are equals, that's avoid to compare SW components with HW components
        guard : componentsFirstModel.category.toString().equalsIgnoreCase(componentsSecondModel.category.toString())  
    
        compare: true
        do {
            clearName(componentsFirstModel.name).println();
            clearName(componentsSecondModel.name).println();
            editDistance = levenshtein.distance(clearName(componentsFirstModel.name),clearName(componentsSecondModel.name)).asDouble();
        }

}



// Return the original name or the name without this_ and _impl_Instance
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



