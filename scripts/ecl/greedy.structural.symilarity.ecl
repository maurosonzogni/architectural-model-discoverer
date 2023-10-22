pre { 
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");
    var firstModelName = clearName(FirstModel!SystemInstance.all().first().name);
    var secondModelName = clearName(SecondModel!SystemInstance.all().first().name);
}

/* 
* Two passes:
* In the first pass, each rule is evaluated for all the pairs of instances in the two models that have a type-of relationship with the types specified by the leftParameter and rightParameter of the rule. 
* In the second pass, each rule that is marked as greedy is executed for all pairs that have not been compared in the first pass, and which have a kind-of relationship with the types specified by the rule. 
* In both passes, to evaluate the compare part of the rule, the guard must be satisfied.
*/
@greedy
rule CompareByName
    match firstModel: FirstModel!ComponentInstance
    with secondModel: SecondModel!ComponentInstance {
        // check if category are equals, that's avoid to compare SW  with HW 
        guard : firstModel.category.toString().equalsIgnoreCase(secondModel.category.toString())  
        // levenshtein.distance return 0.0 if the passed string are equals
        compare: true//levenshtein.distance(clearName(firstModel.name),clearName(secondModel.name)) < threshold
        do {
            "DO...".println();
        }

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









