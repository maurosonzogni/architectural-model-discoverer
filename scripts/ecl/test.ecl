pre {
    // https://github.com/tdebatty/java-string-similarity
    var levenshtein = new Native("info.debatty.java.stringsimilarity.NormalizedLevenshtein");
    info();
    //Sommo le due distanze per ottenere un'unica distanza che attualmente chiamiamo di edit
    var editDistance = numberOfComponentDistance() + numberOfConnectorDistance();
}



rule FuzzyTree2Tree 
    match l : FirstModel!ComponentInstance
    with r : SecondModel!ComponentInstance {

    compare : l.name.fuzzyMatch(r.name)
}

operation String fuzzyMatch(other : String) : Boolean {

   // levenshtein.distance(self,other).println();
    
    return true;
}



// Per ora stampa semplicemente il numero delle componenti dei due modelli
operation numberOfComponentDistance(): Integer{
    var componentsFirstModel = FirstModel!ComponentInstance.all().size();
    var componentsSecondModel = SecondModel!ComponentInstance.all().size();
    ("# di componenti dei modelli: "+ componentsFirstModel + " | " + componentsSecondModel).println();
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
    ("# di connettori dei modelli: "+ connectorsFirstModel + " | " + connectorsSecondModel).println();
    // manteniamo sempre un  numero positivo
    if(connectorsFirstModel>=connectorsSecondModel){
        return connectorsFirstModel -connectorsSecondModel;
    }else{
        return connectorsSecondModel - connectorsFirstModel;
    }

}

// Print name of the systems 
operation info() {
    ("Evaluating: "+ FirstModel!SystemInstance.all().first().name +" | "+SecondModel!SystemInstance.all().first().name).println();
}

/*
IDEA per MATRICE
Similarità strutturale calcolata come distanza di edit
Edit distance: given a cost function on edit operations (e.g. addition/deletion of nodes and edges), determine the minimum cost
transformation from one graph to another. In our case we consider the cost of addition/deletion equals to 1, nodes= components and edges= connectors
SD = (distanzaConnettori+distanzaComponenti)--> prova banale
result example below
MATRIX
0       20      86      114     91      8       3
20      0       66      94      71      12      17
86      66      0       28      5       78      83
114     94      28      0       23      106     111
91      71      5       23      0       83      88
8       12      78      106     83      0       5
3       17      83      111     88      5       0


*/