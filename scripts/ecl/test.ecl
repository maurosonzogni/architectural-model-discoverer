pre {
    // https://github.com/tdebatty/java-string-similarity
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");
    
    //Sommo le due distanze per ottenere un'unica distanza che attualmente chiamiamo di edit
    var editDistance = numberOfComponentDistance() + numberOfConnectorDistance();

}



rule FuzzyTree2Tree 
    match l : FirstModel!SystemInstance
    with r : SecondModel!SystemInstance {

    compare : l.name.fuzzyMatch(r.name)
}

operation String fuzzyMatch(other : String) : Boolean {

    self.println();
    other.println();
    levenshtein.distance(self,other).println();
    
    return true;
}



// Per ora stampa semplicemente il numero delle componenti dei due modelli
operation numberOfComponentDistance(): Integer{
    var componentsFirstModel = FirstModel!ComponentInstance.all().size();
    var componentsSecondModel = SecondModel!ComponentInstance.all().size();
    "# di componenti".println();
    componentsFirstModel.println();
    componentsSecondModel.println();
    // manteniamo sempre un  numero positivo
    if(componentsFirstModel>=componentsSecondModel){
        return componentsFirstModel - componentsSecondModel;
    }else{
        return componentsSecondModel - componentsFirstModel;
    }
     
}

operation numberOfConnectorDistance(): Integer{
    var connectorsFirstModel = FirstModel!ConnectionInstance.all().size();
    var connectorsSecondModel = SecondModel!ConnectionInstance.all().size();
    "# di connettori".println();
    connectorsFirstModel.println();
    connectorsSecondModel.println();
    // manteniamo sempre un  numero positivo
    if(connectorsFirstModel>=connectorsSecondModel){
        return connectorsFirstModel -connectorsSecondModel;
    }else{
        return connectorsSecondModel - connectorsFirstModel;
    }

}



/*
IDEA per MATRICE
Similarità strutturale calcolata come distanza di edit
Edit distance: given a cost function on edit operations (e.g. addition/deletion of nodes and edges), determine the minimum cost
transformation from one graph to another. In our case we consider the cost of addition/deletion equals to 1, nodes= components and edges= connectors
SD = (distanzaConnettori+distanzaComponenti)--> prova banale


*/