{ include("samPlans.asl") }
/*
    This rule infers that if there exists an ontology with a specific prefix PX, 
    and this ontology actually contains a resource that matches the one we are passing to it, 
    then we deduce that this resource has the same prefix.
*/
isPrefixOf(PX,Resource):- prefix(PX,ontology(IRI)) & jase.inspect(IRI,Resource). 
/*
    If this rule succeeds, then the resource is associated with a JasonLiteral. The resource can be both a sting or a Jason atom 
*/
hasJasonLiteral(Resource,JasonLiteral):- prefix(PX,ontology(IRI)) & jase.inspect(IRI,Resource,JasonLiteral) & .ground(JasonLiteral).
/*
    If succeeds the rule associate that the ontology IRI R was inferred using the resoner Reasoner
*/
inferredWith(R,Reasoner):-inferredFrom(K,R) &.substring(Reasoner,K).
/*
    The initial setup of the agent involves the stimulation to memorize new ontologies from a given URL. 
    Upon starting, the agent questions itself if it is familiar with these ontologies. 
    In the event of a negative response, the agent can execute a plan that incorporates the internal action "memorize." 
    This action retrieves the ontology from the specified URL and stores it in its memory.
*/
!learnSomething.
+!learnSomething:
    true<-
        ?prefix(pks,ontology("https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#"));
        ?prefix(cook,ontology("https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#"));
        ?prefix(uni,ontology("https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/enrollUniversity.ttl#"));
        ?prefix(rdf,ontology("http://www.w3.org/1999/02/22-rdf-syntax-ns#"));
        ?prefix(rdfs,ontology("http://www.w3.org/2000/01/rdf-schema#"));
        ?prefix(owl,ontology("http://www.w3.org/2002/07/owl#")).

/*
    Test goals are normally used to retrieve information from the belief base, or to
    check if something we expected is indeed believed by the agent, during the exe-
    cution of a plan body.
    The agente memorize the new ontology only the first time he hear about it.
*/ 
+?prefix(PX,ontology(R)): not knownResource(R) <- .print("Carramba");jase.memorize(R); +prefix(PX,ontology(R));.print("I memorize the ontology with prefix:",PX).
-?prefix(PX,R) : true <- .print("[ PREFIX ]: ",PX," already known").
+!prefix(PX,ontology(R)): not knownResource(R) <- jase.memorize(R); +prefix(PX,ontology(R));.print("I memorize the ontology with prefix:",PX).
-!prefix(PX,R) : true <- .print("[ PREFIX ]: ",PX," already known").

//Triple(K,E):-
/*
    Up to now every time we will restart the agent, he will always restart with the gols of learnSomething
    but form his persistent belief base he will know that something he alredy learn, so he doesnt memorize the 
    same information 
*/


/*
Triggering events for the below listed plans that shows the inspect functioning
!resource("prepareSeasoning").
!resource("sub_activity").
*/


+!resource(R):.ground(R)& isPrefixOf(PX,R) & hasJasonLiteral(R,J)&not prefix(PX,J) <-
    +prefix(PX,J);
    ?prefix(PX,ontology(A));
    .print("Hi!\nThe resource ",R," could belong to the ontology <",A,">.\n I will store this resource by associating it with the atom: ",J,"\nResult: prefix(",PX,",",J,")" ).
-!resource(R):.ground(R)& isPrefixOf(PX,R) & hasJasonLiteral(R,J)&prefix(PX,J)<-.print("I already know this resource! :P").
-!resource(R): not .ground(R)<-.print("Variable").
-!resource(R): R==like | R==order_by<-.print("").
-!resource(R): true <-.print("Non la so man\n",R,"\n").
/*
+!infUsing(R,Reasoner):not inferredWith(R,Reasoner)<- .print("Inferring additional information form ontology: ",R," using resoner: ",Reasoner); jase.infer(R,Reasoner).
-!infUsing(R,Reasoner): inferredWith(R,Reasoner)<-.print("The ontology: ",R," was alredy inferred.").
*/

/*
Uncomment only if you want to infer automatically every time you learn a new ontology
*/
/*
+knownResource(R):not inferredFrom(K,R) <-  .print("Inferring additional information form ontology: ",R); jase.infer(R).
-knownResource(R): true <-  .print("The ontology: ",R," was alredy inferred.").

//every time SamAgent infers an ontology he prints a message to let us know
+inferredFrom(I,R):true<-.print("This is the new inferred resource: ",I).
*/

//This piece of asl was used to showcase in the thesis the workflow of the reason
/*
prefix(rdfs,label).
prefix(rdf,type).
prefix(pks,a_ctivity).
!start.
+!start:true<-!request([type(X,a_ctivity),label(X,Y)],[X,Y]).
*/
//+!request(Triple,Filter):true<-.print("This is my answer:\n ");jase.reason(Triple,Filter,Result);.print(Result); .concat("+request(",Triple,",",Filter,"):true<-answer(",Filter,",",Result,").",R);.add_plan(R);pLib.save_plan(R,"d").//answer(Filter,Result).
/*
prefix(rdfs,label).
prefix(rdf,type).
prefix(pks,a_ctivity).
prefix(pks,hasPriority).
prefix(pks,s_ubActivity).
prefix(pks,isSubActivityOf).
prefix(rdfs,comment).
prefix(pks,after).
prefix(pks,before).
prefix(pks,hasSuccessor).
*/
//!humanQuestion([type(A,a_ctivity),label(A,Activity_Label),like(Activity_Label,"cook_pasta"),isSubActivityOf(S,A),label(S,SubActivity_Label),hasPriority(S,X),order_by([X])],[X,SubActivity_Label]).


//!humanQuestion([type(A,a_ctivity),label(A,Y),like(Y,"_enroll")],[A]).
//[type(A,a_ctivity),label(A,Activity_Label),like(Activity_Label,"cook_pasta"),isSubActivityOf(S,A),label(S,SubActivity_Label),hasPriority(S,X),order_by([X])]
+humanQuestion(Triple,Filter):true<-.print("TRIPLE\n",Triple,Filter,"\n");!checkTriples(Triple);jase.reason(Triple,Filter,Result);answer(Filter,Result);.concat("+humanQuestion(",Triple,",",Filter,"):true<-answer(",Filter,",",Result,").",R);pLib.save_plan(R,"samPlans");.add_plan(R).
//-humanQuestion(Triple,Filter):true<-answer([Sorry],["I have to think more, i cannot find any answer to your question"]).
+!checkTriples([H|T]):H\==[] <- H =.. [P,[S,O],Annot];!resource(P);!resource(S);!resource(O);!checkTriples(T).//;/ !checkTriples(H).
-!checkTriples(E):H\==[]<-.print("ciao").//answer([Sorry],["I have to think more, i cannot find any answer to your question"]).
-!checkTriples(E):true<-.print("Triples ok").

//+?P(X,Z):true<-.print(X).
//+!insTriple[H]:true<-
//jase.reason(Triple,Filter,Result);.print(Result).

//answer(Filter,Result).


//{ begin savePlans }

//+!request(Triple,Filter):true<-.print("I received a request, trying to answer...");jase.reason(Triple,Filter,Result);.print(Result);pLib.save_plan(Result,"d").//answer(Filter,Result).//.concat("+request(",Triple,",",Filter,"):true<-answer(",Filter,",",Result,").",R);.add_plan(R);answer(Filter,Result).

//pLib.savePlan()
//{end}