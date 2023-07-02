-- Sparql variables bound to rdf terms  ?title, ?author. ?address
-- Select statement retirms qieru resi;ts as a table 
-- Sparql is based on rdf turtle serialization and basic graphj pattern mathcing.
-- Sparql works as a pattern matching algoritm
-- NAME OF THE GRAPH WE WILL BE QUERYING FROM THE SPARQL ENDPOINT => FROM CLAUSE
-- WHERE SPECIFY THE GRAPH PATTERN TO BE MATCHED
-- FILTER 
-- UNARY OPERATORS ON VARIABLES
-- STRING PROCESSING AND MATCHING WITH REGEX FLAG I i no casesentsitve
-- ORDER BY
-- LIMIT 
-- OFFSET FORM WHERE TO START
-- optional graph patterns that might fulfilled but not always
-- distinguish patterns combine with or curly brakets aroundd graph patterns
-- How to query different rdf graphs
-- SPARQL
-- PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
-- PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
-- PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
-- PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
-- PREFIX owl:  <http://www.w3.org/2002/07/owl#>


-- all the graphs available:
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT DISTINCT ?g 
WHERE{
  GRAPH ?g { ?s ?p ?o.}
}

--- Dimmi tutte le attivit'a che sai fare 
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>



SELECT  ?activity_label
WHERE{
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.

}
--- Deammi una desctizione di queste attivita
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT  ?activity_label ?activity_description
WHERE{
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  ?activity rdfs:comment ?activity_description.

}
---------------conosci qusta attivita'  
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


ask {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  filter regex(?activity_label,".*cook.*pasta.*","i")
  ?activity rdfs:comment ?activity_description.

}
--descrivimi questa attivita

SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?activity_description 
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  filter regex(?activity_label,".*cook.*pasta.*","i")
  ?activity rdfs:comment ?activity_description.

}

-- dammi i passi per completare/fare questa attivita, dimmi come fare quseta attiaivta

SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?sub_activity_label ?x
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  filter regex(?activity_label,".*cook.*pasta.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  ?sub_activity strc:hasPriority ?x.
} 
order by ?x

SELECT ?sub_activity_label ?x
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  filter regex(?activity_label,".*season.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  ?sub_activity strc:hasPriority ?x.
} 
order by ?x

-- Dimmi qual'e l'attivit'a al passo 2


SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?sub_activity_label ?x
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*season.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  ?sub_activity strc:hasPriority ?x.
  FILTER(?x=2)
} 
order by ?x
-- sono al passo 2 quali sono le precondizioni per svolgere la sub act nr 2
SELECT ?prec_label 
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*season.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  ?sub_activity strc:hasPriority ?x.
  FILTER(?x=2).
  ?sub_activity strc:after ?prec.
?prec rdfs:label ?prec_label.
} 
order by ?x


-- sono alla subact X quali sono le sue precondizioni
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?prec_label
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*cook.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  FILTER regex(?sub_activity_label,".*add.*Salt.*","i").
  ?sub_activity strc:after ?prec.
 ?prec rdfs:label ?prec_label.
} 
order by ?x
--- ho fatto l'attivit'a x cosa devo fare poi 
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?suc_label
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*cook.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  FILTER regex(?sub_activity_label,".*.*Salt.*","i").
  ?sub_activity strc:hasSuccessor ?suc.
 ?suc rdfs:label ?suc_label.
} 
order by ?x
-- vorrei fare l attivita x quali sono i passi succ e le condizioni per completarla 
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?suc_label ?prec_label
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*cook.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  FILTER regex(?sub_activity_label,".*.*Salt.*","i").
  ?sub_activity strc:hasSuccessor ?suc.
 ?suc rdfs:label ?suc_label.
 ?sub_activity strc:after ?prec.
 ?prec rdfs:label ?prec_label.

} 
order by ?x

----------------------------------------------------------------
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?opt_activity_label
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*cook.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  FILTER regex(?sub_activity_label,".*.*wait.*","i").
  ?sub_activity strc:hasSuccessor ?suc.
 ?suc rdfs:label ?suc_label.
 ?sub_activity strc:after ?prec.
 ?prec rdfs:label ?prec_label.
Optional{?opt_activity strc:isOptional ?sub_activity. 
?opt_activity rdfs:label ?opt_activity_label}.

} 
order by ?x
----------------------------------------------------------------  
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


SELECT ?opt_activity_label
WHERE {
  ?activity a strc:Activity.
  ?activity rdfs:label ?activity_label.
  FILTER regex(?activity_label,".*X.*","i")
  ?sub_activity strc:isSubActivityOf ?activity.
  ?sub_activity rdfs:label ?sub_activity_label.
  FILTER regex(?sub_activity_label,".*X.*","i").
  ?sub_activity strc:hasSuccessor ?suc.
  ?suc rdfs:label ?suc_label.
  ?sub_activity strc:after ?prec.
  ?prec rdfs:label ?prec_label.
  OPTIONAL{
    ?opt_activity strc:isOptional ?sub_activity. 
    ?opt_activity rdfs:label ?opt_activity_label
  }.
  FILTER(bound(?opt_activity_label))
} 
----------------------------------------------------------------  
SPARQL
PREFIX strc: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/procedure_structure/procedure_structure.ttl#>
PREFIX cook: <https://raw.githubusercontent.com/xRyuo3/Ontology/main/activity/cookingPasta.ttl#>
PREFIX rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
PREFIX owl:  <http://www.w3.org/2002/07/owl#>


ORDER BY ?x
       SPARQL
        ask
        where
            {  ?s ?p ?o .
                BIND(STRBEFORE(str(?s), \"#\") as ?prefix).
                Filter(?prefix=\"%s\").
                Filter regex(?s,\".*%s.*\",\"i\").
            }""",ontologyIRI,resource);



-- .        =>match any single char letter/digit/whitespace 
-- \.       => match dot 
-- [abc]    => match one letter between a b c 
-- \d       => match one digit
-- [^drp]an =>match any three letter word ending with 'an' that does not start with 'd', 'r' or 'p'.
-- [A-Z]    =>match one charter in range
-- waz{3,5}up  => any string that start with waz and has min 3 max 5 z
-- 'aa+b*c+'   => + 1 or more * 0 or more ? zero or one =>optionality
-- ? => This metacharacter allows you to match either zero or one of the preceding character or group
