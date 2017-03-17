package de.dkt.eservices.enlp;

public class TestConstants {
	
	public static final String pathToPackage = "rdftest/enlp-test-package.xml";
	
	static String relationExtractionInput2 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5,10>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Sofia\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"10\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,39> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,39>\n" +
					"        a               nif:Context , nif:String , nif:RFC5147String ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"39\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Like Sofia, Miguel is not a mere human.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=12,18>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Miguel\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"12\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,39> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Person> .\n" +
					"";
	
	static String relationExtractionExpectedOutput = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
					"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,13>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        dbo:birthDate         \"1954-07-17\"^^xsd:date ;\n" +
					"        nif:anchorOf          \"Angela Merkel\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        itsrdf:taClassRef     dbo:Person ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Angela_Merkel> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=21,29>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"an alien\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"29\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=56,59>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"She\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"56\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"59\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,86>\n" +
					"        a                        nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        dktnif:averageLatitude   \"52.96361111111111\"^^xsd:double ;\n" +
					"        dktnif:averageLongitude  \"11.504722222222222\"^^xsd:double ;\n" +
					"        dktnif:standardDeviationLatitude\n" +
					"                \"0.6016666666666666\"^^xsd:double ;\n" +
					"        dktnif:standardDeviationLongitude\n" +
					"                \"1.5033333333333339\"^^xsd:double ;\n" +
					"        nif:beginIndex           \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex             \"86\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString             \"Angela Merkel is not an alien. She was born in Hamburg. She then moved to Brandenburg.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=47,54>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Hamburg\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"47\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"54\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        geo:lat               \"53.56527777777778\"^^xsd:double ;\n" +
					"        geo:long              \"10.001388888888888\"^^xsd:double ;\n" +
					"        itsrdf:taClassRef     dbo:Location ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Hamburg> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=74,85>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Brandenburg\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"74\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"85\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        geo:lat               \"52.36194444444445\"^^xsd:double ;\n" +
					"        geo:long              \"13.008055555555556\"^^xsd:double ;\n" +
					"        itsrdf:taClassRef     dbo:Location ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Brandenburg> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=31,34>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"She\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"31\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"34\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
					"";
	
	static String englishPlaintextInput = 
			"Friedrich Wilhelm Nietzsche (/ˈniːtʃə/;[4] German: [ˈfʁiːdʁɪç ˈvɪlhɛlm ˈniːtʃə]; 15 October 1844 – 25 August 1900) was a German philosopher, cultural critic, poet, philologist, and Latin and Greek scholar whose work has exerted a profound influence on Western philosophy and modern intellectual history.[5][6][7][8] He began his career as a classical philologist before turning to philosophy. He became the youngest ever to hold the Chair of Classical Philology at the University of Basel in 1869, at the age of 24. Nietzsche resigned in 1879 due to health problems that plagued him most of his life, and he completed much of his core writing in the following decade.[9] In 1889, at age 44, he suffered a collapse and a complete loss of his mental faculties.[10] He lived his remaining years in the care of his mother (until her death in 1897), and then with his sister Elisabeth Förster-Nietzsche, and died in 1900.[11]";
					//"Nietzsche's body of work touched widely on art, philology, history, religion, tragedy, culture, and science, and drew early inspiration from figures such as Schopenhauer, Wagner, and Goethe. His writing spans philosophical polemics, poetry, cultural criticism, and fiction while displaying a fondness for aphorism and irony.[12] Some prominent elements of his philosophy include his radical critique of reason and truth in favor of perspectivism; his genealogical critique of religion and Christian morality, and his related theory of master–slave morality;[5][13] his aesthetic affirmation of existence in response to the 'death of God' and the profound crisis of nihilism;[5] his notion of the Apollonian and Dionysian; and his characterization of the human subject as the expression of competing wills, collectively understood as the will to power.[14] In his later work, he developed influential concepts such as the Übermensch and the doctrine of eternal return, and became increasingly preoccupied with the creative powers of the individual to overcome social, cultural, and moral contexts in pursuit of new values and aesthetic health.[8]\n" +
					//"After his death, Elisabeth Förster-Nietzsche became the curator and editor of her brother's manuscripts, reworking Nietzsche's unpublished writings to fit her own German nationalist ideology while often contradicting or obfuscating his stated opinions, which were explicitly opposed to antisemitism and nationalism. Through her published editions, Nietzsche's work became associated with fascism and Nazism;[15] 20th-century scholars contested this interpretation of his work and corrected editions of his writings were soon made available. His thought enjoyed renewed popularity in the 1960s, and his ideas have since had a profound impact on 20th and early-21st century thinkers across philosophy—especially in schools of continental philosophy such as existentialism, postmodernism, and post-structuralism—as well as art, literature, psychology, politics, and popular culture.[6][7][8][16][17]\n";
	
	static String expectedEntitySuggestResponseEnglish =
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=381,391>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philosophy\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"381\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"391\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=71,78>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"ˈniːtʃə\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"71\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"78\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=516,525>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"516\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"525\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nietzsche> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=260,270>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philosophy\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"260\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"270\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=683,686>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"age\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"683\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"686\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/ontology/age> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,27>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"27\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nietzsche> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=505,508>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"age\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"505\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"508\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/ontology/age> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=351,362>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philologist\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"351\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"362\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=888,897>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"888\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"897\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nietzsche> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=30,37>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"ˈniːtʃə\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"30\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"37\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=164,175>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philologist\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"164\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"175\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,920>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"920\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Friedrich Wilhelm Nietzsche (/ˈniːtʃə/;[4] German: [ˈfʁiːdʁɪç ˈvɪlhɛlm ˈniːtʃə]; 15 October 1844 – 25 August 1900) was a German philosopher, cultural critic, poet, philologist, and Latin and Greek scholar whose work has exerted a profound influence on Western philosophy and modern intellectual history.[5][6][7][8] He began his career as a classical philologist before turning to philosophy. He became the youngest ever to hold the Chair of Classical Philology at the University of Basel in 1869, at the age of 24. Nietzsche resigned in 1879 due to health problems that plagued him most of his life, and he completed much of his core writing in the following decade.[9] In 1889, at age 44, he suffered a collapse and a complete loss of his mental faculties.[10] He lived his remaining years in the care of his mother (until her death in 1897), and then with his sister Elisabeth Förster-Nietzsche, and died in 1900.[11]\"^^xsd:string .\n" +
					"";
			
	
	static String relationExtractionInput =
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
					"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix owl:   <http://www.w3.org/2002/07/owl#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,13>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        dbo:birthDate         \"1954-07-17\"^^xsd:date ;\n" +
					"        nif:anchorOf          \"Angela Merkel\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        itsrdf:taClassRef     dbo:Person ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Angela_Merkel> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=21,29>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"an alien\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"29\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=56,59>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"She\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"56\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"59\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,86>\n" +
					"        a                        nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        dktnif:averageLatitude   \"52.96361111111111\"^^xsd:double ;\n" +
					"        dktnif:averageLongitude  \"11.504722222222222\"^^xsd:double ;\n" +
					"        dktnif:standardDeviationLatitude\n" +
					"                \"0.6016666666666666\"^^xsd:double ;\n" +
					"        dktnif:standardDeviationLongitude\n" +
					"                \"1.5033333333333339\"^^xsd:double ;\n" +
					"        nif:beginIndex           \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex             \"86\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString             \"Angela Merkel is not an alien. She was born in Hamburg. She then moved to Brandenburg.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=47,54>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Hamburg\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"47\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"54\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        geo:lat               \"53.56527777777778\"^^xsd:double ;\n" +
					"        geo:long              \"10.001388888888888\"^^xsd:double ;\n" +
					"        itsrdf:taClassRef     dbo:Location ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Hamburg> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=31,34>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"She\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"31\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"34\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        owl:sameAs            \"http://dkt.dfki.de/documents/#char=0,13\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=74,85>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Brandenburg\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"74\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"85\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,86> ;\n" +
					"        geo:lat               \"52.36194444444445\"^^xsd:double ;\n" +
					"        geo:long              \"13.008055555555556\"^^xsd:double ;\n" +
					"        itsrdf:taClassRef     dbo:Location ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Brandenburg> .\n" +
					"";
			
	
	static String expectedResponse = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=78,81>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"all\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"78\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"81\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PDT\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=46,47>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \".\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"46\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"47\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \".\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=12,14>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"to\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"12\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"14\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"TO\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=72,76>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"some\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"72\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"76\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"DT\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=30,33>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"you\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"30\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"33\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=43,46>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"man\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"43\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"46\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"NN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=61,65>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"some\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"61\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"65\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"DT\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=91,93>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"to\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"91\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"93\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"TO\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=86,90>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"same\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"86\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"90\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"JJ\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=21,22>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \",\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"22\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \",\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=57,60>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"win\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"57\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"60\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VBP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,97>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"97\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"If you like to gamble, I tell you I'm your man.      You win some, lose some, all the same to me.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=23,24>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"I\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"23\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"24\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=38,42>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"your\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"38\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"42\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP$\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=94,96>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"me\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"94\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"96\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=65,66>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \",\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"65\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"66\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \",\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=3,6>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"you\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"3\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"6\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,2>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"If\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"2\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"IN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=67,71>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"lose\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"67\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"71\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VB\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=34,35>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"I\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"34\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"35\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=15,21>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"gamble\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"15\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VB\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=53,56>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"You\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"53\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"56\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=96,97>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \".\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"96\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"97\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \".\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=76,77>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \",\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"76\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"77\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \",\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=82,85>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"the\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"82\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"85\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"DT\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=25,29>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"tell\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"25\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"29\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VBP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=7,11>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"like\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"7\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"11\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VBP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=35,37>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"'m\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"35\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"37\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VBP\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,97> .\n" +
					"";
	
	static String expectedResponse2= 
			
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=48,53>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Typen\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"48\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"53\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"NN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=31,32>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \".\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"31\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"32\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"$.\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=39,42>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"auf\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"39\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"42\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"APPR\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=86,87>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \".\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"86\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"87\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"$.\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,4>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Halb\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"4\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"ADJD\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=10,11>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \",\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"10\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"11\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"$,\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,23>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Augen\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"23\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"NN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=24,31>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"brennen\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"24\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"31\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VVFIN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,87>\n" +
					"        a               nif:Context , nif:String , nif:RFC5147String ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"87\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Halb Sechs, meine Augen brennen. Tret' auf 'nen Typen, der zwischen toten Tauben pennt.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=74,80>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Tauben\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"74\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"80\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"NN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=59,67>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"zwischen\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"59\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"67\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"APPR\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=43,44>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"'\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"43\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"44\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"$[\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=33,37>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Tret\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"33\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"37\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"NE\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5,10>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Sechs\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"10\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"CARD\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=12,17>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"meine\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"12\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"17\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PPOSAT\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=55,58>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"der\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"55\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"58\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"PRELS\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=44,47>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"nen\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"44\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"47\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"ADJA\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=37,38>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"'\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"37\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"38\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"$[\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=68,73>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"toten\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"68\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"73\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"ADJA\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=81,86>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"pennt\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"81\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"86\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"VVFIN\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=53,54>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \",\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"53\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"54\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:posTag            \"$,\"^^xsd:string ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,87> .\n" +
					"";
	
	
	static String turtleInput2= 
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,87>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"87\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Halb Sechs, meine Augen brennen. Tret' auf 'nen Typen, der zwischen toten Tauben pennt.\"^^xsd:string .\n" +
					"";
	
	static String expectedResponse372 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,20>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"20\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Wilkommen in Berlin.\"^^xsd:string .\n" +
					"";
					
	static String expectedResponse22 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=11,17>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Berlin\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"11\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"17\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,26> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,26>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Welcome to Berlin in 2016.\"^^xsd:string .\n" +
					"";
	
	static String turtleInput3 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"@prefix time:  <http://www.w3.org/2006/time#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,26>\n" +
					"        a                     nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        dktnif:meanDateEnd    \"2017-01-01T01:00:00\"^^xsd:dateTime ;\n" +
					"        dktnif:meanDateStart  \"2016-01-01T01:00:00\"^^xsd:dateTime ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString          \"Welcome to Berlin in 2016.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=21,25>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"2016\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"25\"^^xsd:nonNegativeInteger ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"2017-01-01T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"2016-01-01T00:00:00\"^^xsd:dateTime .\n" +
					"";
	
	static String expectedResponse3 =
			
			"<rdf:RDFxmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"xmlns:time=\"http://www.w3.org/2006/time#\"xmlns:nif-ann=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#\"xmlns:itsrdf=\"http://www.w3.org/2005/11/its/rdf#\"xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"xmlns:dktnif=\"http://dkt.dfki.de/ontologies/nif#\"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"xmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"><rdf:Descriptionrdf:about=\"http://dkt.dfki.de/documents/#char=0,26\"><nif:isStringrdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">WelcometoBerlinin2016.</nif:isString><nif:endIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">26</nif:endIndex><nif:beginIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</nif:beginIndex><dktnif:meanDateStartrdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2016-01-01T01:00:00</dktnif:meanDateStart><dktnif:meanDateEndrdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2017-01-01T01:00:00</dktnif:meanDateEnd><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#Context\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/></rdf:Description><rdf:Descriptionrdf:about=\"http://dkt.dfki.de/documents/#char=21,25\"><time:intervalStartsrdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2016-01-01T00:00:00</time:intervalStarts><time:intervalFinishesrdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">2017-01-01T00:00:00</time:intervalFinishes><itsrdf:taClassRefrdf:resource=\"http://www.w3.org/2006/time#TemporalEntity\"/><nif:endIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">25</nif:endIndex><nif:beginIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">21</nif:beginIndex><nif:anchorOfrdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">2016</nif:anchorOf><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/></rdf:Description><rdf:Descriptionrdf:about=\"http://dkt.dfki.de/documents/#char=11,17\"><itsrdf:taClassRefrdf:resource=\"http://dbpedia.org/ontology/Location\"/><nif:referenceContextrdf:resource=\"http://dkt.dfki.de/documents/#char=0,26\"/><nif:endIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">17</nif:endIndex><nif:beginIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">11</nif:beginIndex><nif:anchorOfrdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Berlin</nif:anchorOf><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/></rdf:Description></rdf:RDF>";
	
	static String expectedResponse234 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"@prefix time:  <http://www.w3.org/2006/time#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=543,547>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Irun\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"543\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"547\"^^xsd:nonNegativeInteger ;\n" +
			        "        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=156,163>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"21 July\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"156\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"163\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-07-22T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-07-21T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=146,151>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"South\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"146\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"151\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=201,213>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"201\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"213\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Organisation> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=58,65>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"20 July\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"58\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"65\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-07-21T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-07-20T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,4>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"1936\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"4\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1937-01-01T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-01-01T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=399,403>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"July\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"399\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"403\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-08-01T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-07-01T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=650,662>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"650\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"662\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Organisation> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=494,505>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"5 September\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"494\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"505\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-09-06T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-09-05T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=372,393>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Campaign of Guipuzcoa\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"372\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"393\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Organisation> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=407,416>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"September\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"407\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"416\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-10-01T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-09-01T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=254,260>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Ferrol\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"254\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"260\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=277,282>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Spain\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"277\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"282\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=345,356>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Emilio Mola\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"345\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"356\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Person> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,805>\n" +
					"        a                     nif:Context , nif:String , nif:RFC5147String ;\n" +
					"        dktnif:meanDateEnd    \"1936-10-26T01:30:00\"^^xsd:dateTime ;\n" +
					"        dktnif:meanDateStart  \"1936-06-04T01:30:00\"^^xsd:dateTime ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"805\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString          \"1936\\n\\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipuzcoa from July to September. The capture of Guipuzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irun, closing the French border to the Republicans. On 13 September, the Basques surrendered Madrid to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,26>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Sanjurjo\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Person> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=636,642>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Madrid\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"636\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"642\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=704,710>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Bilbao\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"704\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"710\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=788,804>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"end of September\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"788\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"804\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-09-30T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-09-20T00:00:00\"^^xsd:dateTime .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=598,610>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"13 September\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"598\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"610\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"1936-09-14T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"1936-09-13T00:00:00\"^^xsd:dateTime .\n" +
					"";
			
	static String expectedResponse371 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=13,19>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Berlin\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"19\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,20> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,20>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"20\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Wilkommen in Berlin.\"^^xsd:string .\n" +
					"";
	
	static String expectedResponse37 =
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=13,19>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Berlin\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"19\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,20> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,20>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"20\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Wilkommen in Berlin.\"^^xsd:string .\n" +
					"";
	
	static String bodyInput5 = "1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipuzcoa from July to September. The capture of Guipuzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irun, closing the French border to the Republicans. On 13 September, the Basques surrendered Madrid to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.";
	
	static String expectedResponse5 = 
			
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
				"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +

					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=650,662>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"650\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"662\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Organisation> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,26>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Sanjurjo\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Person> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=277,282>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Spain\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"277\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"282\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=254,260>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Ferrol\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"254\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"260\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=704,710>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Bilbao\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"704\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"710\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=201,213>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"201\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"213\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Organisation> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,805>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"805\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"1936\\n\\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipuzcoa from July to September. The capture of Guipuzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irun, closing the French border to the Republicans. On 13 September, the Basques surrendered Madrid to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=543,547>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Irun\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"543\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"547\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=372,393>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Campaign of Guipuzcoa\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"372\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"393\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Organisation> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=146,151>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"South\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"146\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"151\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=636,642>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Madrid\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"636\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"642\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Location> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=345,356>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Emilio Mola\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"345\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"356\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Person> .\n" +
					"";
	
	static String expectedResponse83 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,7>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"7\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Welcome\"^^xsd:string .\n" +
					"";
	static String expectedResponse888 =
			
			"{\n" +
					"  \"exception\": \"eu.freme.common.exception.BadRequestException\",\n" +
					"  \"path\": \"/e-nlp/namedEntityRecognition\",\n" +
					"  \"message\": \"Unsupported mode combination: Either provide NIF input or use link in combination with spot.\",\n" +
					"  \"error\": \"Bad Request\",\n" +
					"  \"status\": 400,\n" +
					"  \"timestamp\": 1463128970107\n" +
					"}";
	
	static String expectedResponse154553 =
			
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,7>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"7\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Welcome\"^^xsd:string .\n" +
					"";
	
	
	static String expectedResponse84 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,7>\n" +
					"        a               nif:Context , nif:String , nif:RFC5147String ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"7\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Welcome\"^^xsd:string .\n" +
					"";
	
	static String expectedResponse6 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=13,30>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Herbert Eulenberg\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"30\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,36> ;\n" +
					"        itsrdf:taClassRef     <http://dbpedia.org/ontology/Person> ;\n" +
					"        itsrdf:taIdentRef     <http://d-nb.info/gnd/118682636> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,36>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"36\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"wer weiß, wo Herbert Eulenberg ging?\"^^xsd:string .\n" +
					"";
	
	static String nerTrainingData = 
			"The Oxford Companion to Philosophy says , \" there is no single defining position that all anarchists hold , and those considered anarchists at best share a certain family resemblance . \"\n" +
					"In the end , for anarchist historian <START:PER> Daniel Guerin <END> \" Some anarchists are more individualistic than social , some more social than individualistic .\n" +
					"From this climate <START:PER> William Godwin <END> developed what many consider the first expression of modern anarchist thought .\n" +
					"<START:PER> Godwin <END> was , according to <START:PER> Peter Kropotkin <END> , \" the first to formulate the political and economical conceptions of anarchism , even though he did not give that name to the ideas developed in his work \" , while <START:PER> Godwin <END> attached his anarchist ideas to an early <START:PER> Edmund Burke <END> .\n" +
					"Its classical period , which scholars demarcate as from 1860 to 1939 , is associated with the working-class movements of the nineteenth century and the Spanish Civil War-era struggles against fascism .\n" +
					"Due to its links to active workers ' movements , the International became a significant organization .\n" +
					"<START:PER> Karl Marx <END> became a leading figure in the International and a member of its General Council .\n" +
					"<START:PER> Proudhon <END> 's followers , the mutualists , opposed <START:PER> Marx <END> 's state socialism , advocating political abstentionism and small property holdings .\n" +
					"They allied themselves with the federalist socialist sections of the International , who advocated the revolutionary overthrow of the state and the collectivization of property .\n" +
					"Subsequently , the International became polarised into two camps , with <START:PER> Marx <END> and <START:PER> Bakunin <END> as their respective figureheads .\n" +
					"In response , unions across America prepared a general strike in support of the event .\n" +
					"The incident became known as the <START:PER> Haymarket affair <END> , and was a setback for the labour movement and the struggle for the eight hour day .\n" +
					"The event also had the secondary purpose of memorializing workers killed as a result of the <START:PER> Haymarket affair <END> .\n" +
					"<START:PER> Malatesta <END> warned that the syndicalists aims were in perpetuating syndicalism itself , whereas anarchists must always have anarchy as their end and consequently refrain from committing to any particular method of achieving it .\n" +
					"The most successful was the Confederación Nacional del Trabajo ( National Confederation of Labour : CNT ) , founded in 1910 .\n" +
					"CGT membership was estimated to be around 100,000 for the year 2003 .\n" +
					"Other active syndicalist movements include the US Workers Solidarity Alliance and the UK Solidarity Federation .\n" +
					"Some anarchists , such as <START:PER> Johann Most <END> , advocated publicizing violent acts of retaliation against counter-revolutionaries because \" we preach not only action in and for itself , but also action as propaganda . \"\n" +
					"The anarcho-syndicalist , <START:PER> Fernand Pelloutier <END> , argued in 1895 for renewed anarchist involvement in the labor movement on the basis that anarchism could do very well without \" the individual dynamiter . \"\n" +
					"For example , U.S. President <START:PER> McKinley <END> 's assassin <START:PER> Leon Czolgosz <END> claimed to have been influenced by anarchist and feminist <START:PER> Emma Goldman <END> .\n" +
					"This was in spite of <START:PER> Goldman <END> 's disavowal of any association with him , his registered membership in the Republican Party , and never having belonged to an anarchist organization .\n" +
					"Propaganda of the deed was abandoned by the vast majority of the anarchist movement after World War I ( 1914 -- 18 ) and the 1917 October Revolution .\n" +
					"Important factors towards this include state repression , the level of organization of the labour movement , and the influence of the October Revolution [ citation needed ] .\n" +
					"Anarchists participated alongside the Bolsheviks in both February and October revolutions , and were initially enthusiastic about the Bolshevik coup .\n" +
					"However , the Bolsheviks soon turned against the anarchists and other left-wing opposition , a conflict that culminated in the 1921 Kronstadt rebellion which the new government repressed .\n" +
					"Both wrote accounts of their experiences in Russia , criticizing the amount of control the Bolsheviks exercised .\n" +
					"The victory of the Bolsheviks in the October Revolution and the resulting Russian Civil War did serious damage to anarchist movements internationally .\n" +
					"Italian anarchists played a key role in the anti-fascist organisation Arditi del Popolo , which was strongest in areas with anarchist traditions , and achieved some success in their activism , such as repelling Blackshirts in the anarchist stronghold of Parma in August 1922 .\n" +
					"But in 1936 , the CNT changed its policy and anarchist votes helped bring the popular front back to power .\n" +
					"Months later , the former ruling class responded with an attempted coup causing the Spanish Civil War ( 1936 -- 1939 ) .\n" +
					"Stalinist-led troops suppressed the collectives and persecuted both dissident Marxists and anarchists .\n" +
					"In the U.K. this was associated with the punk rock movement , as exemplified by bands such as Crass and the Sex Pistols .\n" +
					"Anarchist anthropologist <START:PER> David Graeber <END> and anarchist historian <START:PER> Andrej Grubacic <END> have posited a rupture between generations of anarchism , with those \" who often still have not shaken the sectarian habits \" of the nineteenth century contrasted with the younger activists who are \" much more informed , among other elements , by indigenous , feminist , ecological and cultural-critical ideas \" , and who by the turn of the 21st century formed \" by far the majority \" of anarchists .\n" +
					"Anarchists became known for their involvement in protests against the meetings of the World Trade Organization ( WTO ) , Group of Eight , and the World Economic Forum .\n" +
					"A landmark struggle of this period was the confrontations at WTO conference in Seattle in 1999 .\n" +
					"One reaction against sectarianism within the anarchist milieu was \" anarchism without adjectives \" , a call for toleration first adopted by <START:PER> Fernando Tarrida del Mármol <END> in 1889 in response to the \" bitter debates \" of anarchist theory at the time .\n" +
					"<START:PER> Proudhon <END> proposed spontaneous order , whereby organization emerges without central authority , a \" positive anarchy \" where order arises when everybody does \" what he wishes and only what he wishes \" and where \" business transactions alone produce the social order . \"\n" +
					"According to <START:PER> William Batchelder Greene <END> , each worker in the mutualist system would receive \" just and exact pay for his work ; services equivalent in cost being exchangeable for services equivalent in cost , without profit or discount . \"\n" +
					"<START:PER> Proudhon <END> first characterised his goal as a \" third form of society , the synthesis of communism and property . \"\n" +
					"<START:PER> Godwin <END> , a philosophical anarchist , from a rationalist and utilitarian basis opposed revolutionary action and saw a minimal state as a present \" necessary evil \" that would become increasingly irrelevant and powerless by the gradual spread of knowledge .\n" +
					"<START:PER> Godwin <END> advocated extreme individualism , proposing that all cooperation in labour be eliminated on the premise that this would be most conducive with the general good .\n" +
					"<START:PER> Godwin <END> was a utilitarian who believed that all individuals are not of equal value , with some of us \" of more worth and importance \" than others depending on our utility in bringing about social good .\n" +
					"<START:PER> Godwin <END> opposed government because he saw it as infringing on the individual 's right to \" private judgement \" to determine which actions most maximise utility , but also makes a critique of all authority over the individual 's judgement .\n" +
					"This aspect of <START:PER> Godwin <END> 's philosophy , stripped of utilitarian motivations , was developed into a more extreme form later by <START:PER> Stirner <END> .\n" +
					"The most extreme form of individualist anarchism , called \" egoism, \" or egoist anarchism , was expounded by one of the earliest and best-known proponents of individualist anarchism , <START:PER> Max Stirner <END> .\n" +
					"<START:PER> Stirner <END> 's The Ego and Its Own , published in 1844 , is a founding text of the philosophy .\n" +
					"To <START:PER> Stirner <END> , rights were spooks in the mind , and he held that society does not exist but \" the individuals are its reality \" .\n" +
					"<START:PER> Stirner <END> 's philosophy has been seen as a precedent of existentialism with other thinkers like <START:PER> Friedrich Nietzsche <END> and <START:PER> Søren Kierkegaard <END> .\n" +
					"Collectivist anarchism , also referred to as \" revolutionary socialism \" or a form of such , is a revolutionary form of anarchism , commonly associated with <START:PER> Mikhail Bakunin <END> and <START:PER> Johann Most <END> .\n" +
					"It was inspired by the late 19th century writings of early feminist anarchists such as <START:PER> Lucy Parsons <END> , <START:PER> Emma Goldman <END> , <START:PER> Voltairine de Cleyre <END> , and <START:PER> Dora Marsden <END> .\n" +
					"It developed \" mostly in Holland , Britain , and the U.S. , before and during the Second World War \" .\n" +
					"Another recent form of anarchism critical of formal anarchist movements is insurrectionary anarchism , which advocates informal organization and active resistance to the state ; its proponents include <START:PER> Wolfi Landstreicher <END> and <START:PER> Alfredo M. Bonanno <END> .\n" +
					"Free love advocates sometimes traced their roots back to <START:PER> Josiah Warren <END> and to experimental communities , viewed sexual freedom as a clear , direct expression of an individual 's self-ownership .\n" +
					"In New York 's Greenwich Village , bohemian feminists and socialists advocated self-realisation and pleasure for women ( and also men ) in the here and now .\n" +
					"They encouraged playing with sexual roles and sexuality , and the openly bisexual radical <START:PER> Edna St. Vincent Millay <END> and the lesbian anarchist <START:PER> Margaret Anderson <END> were prominent among them .\n" +
					"Thus it came about that she was the first and only woman , indeed the first and only American , to take up the defense of homosexual love before the general public . \"\n" +
					"<START:PER> Max Stirner <END> wrote in 1842 a long essay on education called The False Principle of our Education .\n" +
					"In it <START:PER> Stirner <END> names his educational principle \" personalist, \" explaining that self-understanding consists in hourly self-creation .\n" +
					"Perhaps the best-known effort in this field was <START:PER> Francisco Ferrer <END> 's Modern School ( Escuela Moderna ) , a project which exercised a considerable influence on Catalan education and on experimental techniques of teaching generally . \"\n" +
					"The first of these was started in New York City in 1911 .\n" +
					"Summerhill is often cited as an example of anarchism in practice .\n" +
					"The term deschooling was popularized by <START:PER> Ivan Illich <END> , who argued that the school as an institution is dysfunctional for self-determined learning and serves the creation of a consumer society instead .\n" +
					"According to <START:PER> Luther <END> 's notetaker <START:PER> Mathesius <END> , <START:PER> Luther <END> thought the boy was a soulless mass of flesh possessed by the devil , and suggested that he be suffocated .\n" +
					"Almost all the characteristics described in <START:PER> Kanner <END> 's first paper on the subject , notably \" autistic aloneness \" and \" insistence on sameness \" , are still regarded as typical of the autistic spectrum of disorders .\n" +
					"<START:PER> Kanner <END> 's reuse of autism led to decades of confused terminology like infantile schizophrenia , and child psychiatry 's focus on maternal deprivation led to misconceptions of autism as an infant 's response to \" refrigerator mothers \" .\n" +
					"The Internet has helped autistic individuals bypass nonverbal cues and emotional sharing that they find so hard to deal with , and has given them a way to form online communities and work remotely .\n" +
					"Another notable high albedo body is Eris , with an albedo of 0.86 .\n" +
					"Studies by the Hadley Centre have investigated the relative ( generally warming ) effect of albedo change and ( cooling ) effect of carbon sequestration on planting forests .\n" +
					"Over Antarctica they average a little more than 0.8 .\n" +
					"The numeric character references in HTML and XML are \" & # 65; \" and \" & # 97; \" for upper and lower case , respectively .\n" +
					"Alabama ( i i i / l ə ˈ b æ m ə / ) is a state located in the southeastern region of the United States of America .\n" +
					"It is bordered by Tenn. to the north , Georgia to the east , Florida and the Gulf of Mexico to the south , and Mississippi to the west .\n" +
					"Alabama ranks 30th in total land area and ranks second in the size of its inland waterways .\n" +
					"Following World War II , Alabama experienced growth as the economy of the state transitioned from agriculture to diversified interests in heavy manufacturing , mineral extraction , education , and technology .\n" +
					"In addition , the establishment or expansion of multiple military installations , primarily those of the U.S. Army and U.S. Air Force , added to state jobs .\n" +
					"The capital of Ala. is Montgomery .\n" +
					"The largest city by population is Birmingham .\n" +
					"The largest city by total land area is Huntsville .\n" +
					"Although the origin of Ala. could be discerned , sources disagree on its meaning .\n" +
					"This notion was popularized in the 1850s through the writings of <START:PER> Alexander Beaufort Meek <END> .\n" +
					"Scholars believe the word comes from the Choctaw alba ( meaning \" plants \" or \" weeds \" ) and amo ( meaning \" to cut \" , \" to trim \" , or \" to gather \" ) .\n" +
					"Ala. was part of the new frontier in the 1820s and 1830s .\n" +
					"The economy of the central \" Black Belt \" was built around large cotton plantations whose owners built their wealth on slave labor .\n" +
					"While few battles were fought in the state , Alabama contributed about 120,000 soldiers to the American Civil War .\n" +
					"After the Civil War , the state was still chiefly agricultural , with an economy tied to cotton .\n" +
					"In 1900 , fourteen Black Belt counties had more than 79,000 voters on the rolls .\n" +
					"The Voting Rights Act of 1965 also protected the suffrage of poor whites .\n" +
					"The population growth rate in Ala. dropped by nearly half from 1910 -- 1920 , reflecting the effect of emigration .\n" +
					"At the same time , many rural whites and blacks migrated to the city of Birmingham for work in new industrial jobs .\n" +
					"It experienced such rapid growth that it was nicknamed \" The Magic City \" .\n" +
					"By the 1920s , Birmingham was the 19th largest city in the U.S. and held more than 30 % of the population of the state .\n" +
					"In addition , the state legislature gerrymandered the few Birmingham legislative seats to ensure election by persons living outside Birmingham .\n" +
					"One result was that Jefferson County , containing Birmingham 's industrial and economic powerhouse , contributed more than one-third of all tax revenue to the state , but did not received a proportional amount in services .\n" +
					"A 1960 study noted that because of rural domination , \" A minority of about 25 per cent of the total state population is in majority control of the Alabama legislature . \"\n" +
					"These factors created a longstanding tradition that any candidate who wanted to be viable with white voters had to run as a Democrat regardless of political beliefs .\n" +
					"Industrial development related to the demands of World War II brought prosperity .\n" +
					"In the 1960s under Governor <START:PER> George Wallace <END> , many whites in the state opposed integration efforts .\n" +
					"After 1972 , the state 's white voters shifted much of their support to Republican candidates in presidential elections ( as also occurred in neighboring southern states ) .\n" +
					"Since 1990 the majority of whites in the state have voted increasingly Republican in state elections .\n" +
					"Democrats are still the majority party in both houses of the legislature .\n";

	static String expectedResponse7 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,32>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"William Godwin\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:birthDate         \"1756-03-03\"^^xsd:date ;\n" +
					"        nif:deathDate         \"1836-04-07\"^^xsd:date ;\n" +
					"        nif:endIndex          \"32\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,112> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/William_Godwin> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,112>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"112\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"From this climate William Godwin developed what many consider the first expression of modern anarchist thought .\"^^xsd:string .\n" +
					"";
	
	static String expectedResponse8 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"@prefix time:  <http://www.w3.org/2006/time#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,26>\n" +
					"        a                     nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        dktnif:meanDateEnd    \"2017-01-01T01:00:00\"^^xsd:dateTime ;\n" +
					"        dktnif:meanDateStart  \"2016-01-01T01:00:00\"^^xsd:dateTime ;\n" +
					"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString          \"Welcome to Berlin in 2016.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=21,25>\n" +
					"        a                      nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf           \"2016\"^^xsd:string ;\n" +
					"        nif:beginIndex         \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex           \"25\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,26> ;\n" + 
					"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
					"        time:intervalFinishes  \"2017-01-01T00:00:00\"^^xsd:dateTime ;\n" +
					"        time:intervalStarts    \"2016-01-01T00:00:00\"^^xsd:dateTime .\n" +
					"";
	
	static String expectedResponse9 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
					+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
					+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
					+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
					+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
					+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
					+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
					+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
					+"\n"
					+"<http://dkt.dfki.de/documents/#char=0,18>\n"
					+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
					+"        dktnif:meanDateEnd    \"2016-11-24T12:30:00\"^^xsd:dateTime ;\n"
					+"        dktnif:meanDateStart  \"2016-03-07T12:30:00\"^^xsd:dateTime ;\n"
					+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
					+"        nif:endIndex          \"18\"^^xsd:nonNegativeInteger ;\n"
					+"        nif:isString          \"2016 blabla Sommer\"^^xsd:string .\n"
					+"\n"
					+"<http://dkt.dfki.de/documents/#char=12,18>\n"
					+"        a                      nif:RFC5147String , nif:String ;\n"
					+"        nif:anchorOf           \"Sommer\"^^xsd:string ;\n"
					+"        nif:beginIndex         \"12\"^^xsd:nonNegativeInteger ;\n"
					+"        nif:endIndex           \"18\"^^xsd:nonNegativeInteger ;\n"
					+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,18> ;\n"
					+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
					+"        time:intervalFinishes  \"2016-08-30T00:00:00\"^^xsd:dateTime ;\n"
					+"        time:intervalStarts    \"2016-07-01T00:00:00\"^^xsd:dateTime .\n"
					+"\n"
					+"<http://dkt.dfki.de/documents/#char=0,4>\n"
					+"        a                      nif:RFC5147String , nif:String ;\n"
					+"        nif:anchorOf           \"2016\"^^xsd:string ;\n"
					+"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n"
					+"        nif:endIndex           \"4\"^^xsd:nonNegativeInteger ;\n"
					+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,18> ;\n"
					+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
					+"        time:intervalFinishes  \"2017-01-01T00:00:00\"^^xsd:dateTime ;\n"
					+"        time:intervalStarts    \"2016-01-01T00:00:00\"^^xsd:dateTime .\n"
					+"";
	
	static String expectedResponse10 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
			+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
			+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
			+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
			+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
			+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=5,14>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"silvester\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"5\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"14\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,75> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2015-01-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2014-12-31T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=23,37>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"tag der arbeit\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"23\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"37\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,75> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2014-05-02T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2014-05-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=15,22>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"neujahr\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"15\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"22\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,75> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2014-01-02T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2014-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=50,75>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"tag der deutschen einheit\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"50\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"75\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,75> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2014-10-04T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2014-10-03T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,75>\n"
			+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
			+"        dktnif:meanDateEnd    \"2014-11-12T08:30:00\"^^xsd:dateTime ;\n"
			+"        dktnif:meanDateStart  \"2014-02-09T08:30:00\"^^xsd:dateTime ;\n"
			+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex          \"75\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:isString          \"2014 silvester neujahr tag der arbeit maifeiertag tag der deutschen einheit\"^^xsd:string .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,4>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"2014\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"4\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,75> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2015-01-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2014-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=38,49>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"maifeiertag\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"38\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"49\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,75> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2014-05-02T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2014-05-01T00:00:00\"^^xsd:dateTime .\n"
			+"";
	
	static String expectedResponse11 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
			+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
			+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
			+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
			+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
			+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=43,49>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"15 Uhr\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"43\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"49\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,61> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2017-12-17T15:01:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2017-12-17T15:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=16,21>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"17.12\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"16\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"21\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,61> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2017-12-18T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2017-12-17T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,61>\n"
			+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
			+"        dktnif:meanDateEnd    \"2017-12-17T15:45:20\"^^xsd:dateTime ;\n"
			+"        dktnif:meanDateStart  \"2017-12-17T15:45:20\"^^xsd:dateTime ;\n"
			+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex          \"61\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:isString          \"Tele 5 zeigt am 17.12. um 20.15 Uhr und um 15 Uhr die Komödie\"^^xsd:string .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=26,35>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"20.15 Uhr\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"26\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"35\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,61> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2017-12-17T20:16:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2017-12-17T20:15:00\"^^xsd:dateTime .\n"
			+"";
	
	static String expectedResponse666 =
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=13,30>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Herbert Eulenberg\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"30\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,36> ;\n" +
					"        itsrdf:taClassRef     dktnif:AAPJE ;\n" +
					"        itsrdf:taIdentRef     <http://d-nb.info/gnd/118682636> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,36>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"36\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"wer weiß, wo Herbert Eulenberg ging?\"^^xsd:string .\n" +
					"";
	
	static String expectedResponse12 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
			+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
			+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
			+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
			+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
			+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=11,21>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"dieser Tag\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"11\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"21\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,58> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-09T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=22,33>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"diese Woche\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"22\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"33\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,58> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-13T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-06T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,10>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"08.10.1990\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"10\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,58> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-09T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=47,58>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"dieses Jahr\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"47\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"58\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,58> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1991-01-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=34,46>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"dieser Monat\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"34\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"46\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,58> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-11-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,58>\n"
			+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
			+"        dktnif:meanDateEnd    \"1990-12-19T17:48:00\"^^xsd:dateTime ;\n"
			+"        dktnif:meanDateStart  \"1990-06-22T17:48:00\"^^xsd:dateTime ;\n"
			+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex          \"58\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:isString          \"08.10.1990 dieser Tag diese Woche dieser Monat dieses Jahr\"^^xsd:string .\n"
			+"";
	
	static String expectedResponse13 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
			+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
			+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
			+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
			+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
			+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,39>\n"
			+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
			+"        dktnif:meanDateEnd    \"2006-07-11T01:00:00\"^^xsd:dateTime ;\n"
			+"        dktnif:meanDateStart  \"1986-06-24T01:00:00\"^^xsd:dateTime ;\n"
			+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex          \"39\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:isString          \"1990 seit 10 Jahren, 2010 vor 10 Jahren\"^^xsd:string .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=5,19>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"seit 10 Jahren\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"5\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"19\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,39> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-01-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1980-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=26,39>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"vor 10 Jahren\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"26\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"39\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,39> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2000-01-02T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2000-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,4>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"1990\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"4\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,39> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1991-01-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=21,25>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"2010\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"21\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"25\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,39> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"2011-01-01T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"2010-01-01T00:00:00\"^^xsd:dateTime .\n"
			+"";
	
	static String expectedResponse14 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
			+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
			+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
			+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
			+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
			+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=10,16>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"monday\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"10\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"16\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,31> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-09T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=25,31>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"friday\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"25\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"31\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,31> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-13T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-12T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,31>\n"
			+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
			+"        dktnif:meanDateEnd    \"1990-10-10T18:00:00\"^^xsd:dateTime ;\n"
	        +"        dktnif:meanDateStart  \"1990-10-08T18:00:00\"^^xsd:dateTime ;\n"
			+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex          \"31\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:isString          \"8.10.1990 monday tuesday friday\"^^xsd:string .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,9>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"8.10.1990\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"9\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,31> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-09T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=17,24>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"tuesday\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"17\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"24\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,31> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-10T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-09T00:00:00\"^^xsd:dateTime .\n"
			+"";
	
	static String expectedResponse15 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n"
			+"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n"
			+"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n"
			+"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n"
			+"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n"
			+"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n"
			+"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n"
			+"@prefix time:  <http://www.w3.org/2006/time#> .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,9>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"8.10.1990\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"9\"^^xsd:nonNegativeInteger ;\n"
	        +"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,30> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-09T00:00:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T00:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=21,29>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"9.00 a.m\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"21\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"29\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,30> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-08T09:01:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T09:00:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=10,19>\n"
			+"        a                      nif:RFC5147String , nif:String ;\n"
			+"        nif:anchorOf           \"10.34 p.m\"^^xsd:string ;\n"
			+"        nif:beginIndex         \"10\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex           \"19\"^^xsd:nonNegativeInteger ;\n"
	        +"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,30> ;\n"
			+"        itsrdf:taClassRef      time:TemporalEntity ;\n"
			+"        time:intervalFinishes  \"1990-10-08T22:35:00\"^^xsd:dateTime ;\n"
			+"        time:intervalStarts    \"1990-10-08T22:34:00\"^^xsd:dateTime .\n"
			+"\n"
			+"<http://dkt.dfki.de/documents/#char=0,30>\n"
			+"        a                     nif:RFC5147String , nif:String , nif:Context ;\n"
			+"        dktnif:meanDateEnd    \"1990-10-08T14:31:40\"^^xsd:dateTime ;\n"
	        +"        dktnif:meanDateStart  \"1990-10-08T14:31:40\"^^xsd:dateTime ;\n"
			+"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:endIndex          \"30\"^^xsd:nonNegativeInteger ;\n"
			+"        nif:isString          \"8.10.1990 10.34 p.m. 9.00 a.m.\"^^xsd:string .\n"
			+"";
	
	static String expectedResponse16 = "@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
			"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
			"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
			"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
			"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
			"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
			"@prefix time:  <http://www.w3.org/2006/time#> .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,65>\n" +
			"        a                     nif:RFC5147String , nif:String , nif:Context ;\n" +
			"        dktnif:meanDateEnd    \"2016-04-18T21:00:00\"^^xsd:dateTime ;\n" +
			"        dktnif:meanDateStart  \"2015-07-31T21:00:00\"^^xsd:dateTime ;\n" +
			"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex          \"65\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:isString          \"8.10.2015 nächster Tag nächste Woche nächster Monat nächstes Jahr\"^^xsd:string .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=23,36>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        nif:anchorOf           \"nächste Woche\"^^xsd:string ;\n" +
			"        nif:beginIndex         \"23\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"36\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,65> ;\n" +
			"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
			"        time:intervalFinishes  \"2015-10-17T00:00:00\"^^xsd:dateTime ;\n" +
			"        time:intervalStarts    \"2015-10-10T00:00:00\"^^xsd:dateTime .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=37,51>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        nif:anchorOf           \"nächster Monat\"^^xsd:string ;\n" +
			"        nif:beginIndex         \"37\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"51\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,65> ;\n" +
			"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
			"        time:intervalFinishes  \"2015-12-01T00:00:00\"^^xsd:dateTime ;\n" +
			"        time:intervalStarts    \"2015-11-01T00:00:00\"^^xsd:dateTime .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,9>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        nif:anchorOf           \"8.10.2015\"^^xsd:string ;\n" +
			"        nif:beginIndex         \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"9\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,65> ;\n" +
			"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
			"        time:intervalFinishes  \"2015-10-09T00:00:00\"^^xsd:dateTime ;\n" +
			"        time:intervalStarts    \"2015-10-08T00:00:00\"^^xsd:dateTime .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=52,65>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        nif:anchorOf           \"nächstes Jahr\"^^xsd:string ;\n" +
			"        nif:beginIndex         \"52\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"65\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,65> ;\n" +
			"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
			"        time:intervalFinishes  \"2017-01-01T00:00:00\"^^xsd:dateTime ;\n" +
			"        time:intervalStarts    \"2016-01-01T00:00:00\"^^xsd:dateTime .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=10,22>\n" +
			"        a                      nif:RFC5147String , nif:String ;\n" +
			"        nif:anchorOf           \"nächster Tag\"^^xsd:string ;\n" +
			"        nif:beginIndex         \"10\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex           \"22\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:referenceContext   <http://dkt.dfki.de/documents/#char=0,65> ;\n" +
			"        itsrdf:taClassRef      time:TemporalEntity ;\n" +
			"        time:intervalFinishes  \"2015-10-10T00:00:00\"^^xsd:dateTime ;\n" +
			"        time:intervalStarts    \"2015-10-09T00:00:00\"^^xsd:dateTime .\n" +
			"";
			
	static String dictUploadData = 
			"Mahler	http://d-nb.info/gnd/11857633X\n" +
				"Herbert Eulenberg	http://d-nb.info/gnd/118682636\n" +
				"Leonardo da Vinci	http://d-nb.info/gnd/118640445\n" +
				"Treitschke	http://d-nb.info/gnd/118623761\n" +
				"Schmädel 	http://d-nb.info/gnd/117521221";

//Mahler	http://d-nb.info/gnd/11857633X
//Herbert Eulenberg	http://d-nb.info/gnd/118682636
//Leonardo da Vinci	http://d-nb.info/gnd/118640445
//Treitschke	http://d-nb.info/gnd/118623761
//Schmädel 	http://d-nb.info/gnd/117521221
	
	static String expectedResponse658 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=2324,2328>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"2324\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"2328\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=7943,7947>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"7943\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"7947\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=7366,7370>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"7366\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"7370\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=341,345>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"341\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"345\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=6328,6330>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"6328\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"6330\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=6951,6953>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"6951\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"6953\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=1397,1399>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"1397\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"1399\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=4019,4023>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"4019\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"4023\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5733,5735>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5733\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"5735\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=817,821>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"817\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"821\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=354,356>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"354\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"356\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=7940,7942>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"7940\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"7942\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=906,910>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"906\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"910\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=3643,3647>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"3643\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"3647\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=338,340>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"338\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"340\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=1400,1404>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"1400\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"1404\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=4016,4018>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"4016\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"4018\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=3640,3642>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"3640\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"3642\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=814,816>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"814\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"816\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=230,232>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"230\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"232\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=903,905>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"903\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"905\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5427,5431>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5427\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"5431\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=4460,4462>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"4460\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"4462\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5570,5572>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5570\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"5572\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=7363,7365>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"7363\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"7365\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=233,237>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"233\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"237\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=4463,4467>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"4463\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"4467\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=6331,6335>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"6331\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"6335\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=750,752>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"750\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"752\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5736,5740>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5736\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"5740\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=2321,2323>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"2321\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"2323\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,8071>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"8071\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Sexualität und Islam: Mohammed war in gewisser Weise Feminist\\tSexualität und Islam\\tMohammed war in gewisser Weise Feminist\\tEva Thöne\\tInterview\\tIst der Islam aggressiv und antifeministisch? Die ägyptische Wissenschaftlerin Shereen El Feki plädiert dafür genauer hinzusehen - und erklärt was Sexualität mit Abschiebung zu tun hat.\\t'Shereen El Feki Shereen El Feki wuchs als Tochter eines Ägypters und einer Britin in Kanada auf und studierte Immunologie. Später arbeitete sie als Journalistin u.a. für den Sender Al Jazeera außerdem für die Uno. 2013 erschien ihr Buch ''Sex und die Zitadelle. Liebesleben in der sich wandelnden arabischen Welt''. Derzeit arbeitet sie mit der NGO ''Promundo'' an einer breiten Studie zu Sexualität im arabischen Raum. El Feki pendelt zwischen London und Kairo. SPIEGEL ONLINE: Frau El Feki Sie leben in Kairo und London. Wo fühlen Sie sich sicherer vor sexueller Gewalt? El Feki: Ich bin kein gutes Beispiel. Ich bin Ausländerin in Ägypten und werde auch immer so wahrgenommen - wie es den Frauen im Alltagsleben geht vermag ich nicht zu bewerten. Ich weiß natürlich worauf Sie mit Ihrer Frage hinauswollen. Ja sexuelle Gewalt spielt eine große Rolle in Ägypten. Umfragen der Uno haben ergeben dass 99 Prozent aller ägyptischen Frauen schon mal sexuell belästigt wurden. Trotzdem: Es gibt auch Fortschritte. SPIEGEL ONLINE: Wo sehen Sie die bei dieser hohen Quote? El Feki: Vor zehn Jahren war das Thema sexuelle Gewalt noch absolut tabu heute ist es Gespräch. Die sexuellen Attacken auf dem Tahrirplatz ab 2011 waren der Auslöser weil sie viele Menschen dazu brachten sich offen zu äußern. Viele der Konservativen die damals argumentierten die Frauen seien nicht angemessen angezogen gewesen trauen sich heute nicht mehr das so auszusprechen. Sie mögen es noch denken. Aber sie schweigen weil sich der öffentliche Diskurs geändert hat. Das hat natürlich auch mit dem Opportunismus der aktuellen Regierung zu tun: Weil sich Al-Sisi zumindest nach außen hin von den Muslimbrüdern distanzieren will kriminalisiert er sexuelle Belästigung. Für andere Bereiche gilt das nicht. Im Namen der Religion werden in Ägypten Homosexuelle nach wie vor ins Gefängnis geworfen. SPIEGEL ONLINE: Über Ägypten hinaus - beobachten Sie im arabischen Raum länderübergreifend eine Ausweitung von Frauenrechten? El Feki: Ich kann in dieser Debatte nur vor jeder Verallgemeinerung warnen. Ich kann nicht über Millionen Menschen in ganz unterschiedlichen Lebenssituationen sprechen. Klar ist nur: Der Aufstieg des ''Islamischen Staates'' (IS) hat in vielen arabischen Ländern eine Debatte darüber ausgelöst was mit dem Islam passiert. Wer spricht in seinem Namen und bestimmt so auch Vorstellungen von Sexualität mit? Durch Abgrenzung vom IS finden viele junge Araber ihre Stimme. ''Das ist nicht mein Islam'' ist die Wendung die Sie dann in sozialen Netzwerken lesen können. Häufig hören wir in den westlichen Massenmedien aber nur von den Männern die Frauen belästigen. Die die sich in Projekten und Initiativen gegen Sexualgewalt stellen finden kaum statt. Der Posterboy des Jahres 2016 wird in den westlichen Medien der Flüchtling mit einem Abschiebebescheid in der Hand sein. Das ist verkürzt und manchmal auch rassistisch. Liegt aber auch daran dass der Diskurs über den Islam während der letzten Jahrzehnte vor allem von fundamentalistischen Stimmen geprägt war und so auch von konservative Interpretationen des Korans. Tatsächlich hat der Islam aber auch Potenzial für gleichberechtigtes Leben. Islamische Feministinnen weisen seit Jahren immer wieder darauf hin. SPIEGEL ONLINE: Haben Sie ein Beispiel für dieses Potenzial? El Feki: Das geht zurück bis zum Propheten Mohammed der von starken Frauen umgeben war. Er äußerte sich auch sehr klar zu Sex sagte etwa dass auch die Frau den Geschlechtsverkehr genießen solle. In gewisser Weise war er Feminist. SPIEGEL ONLINE: Es gibt aber eben auch diese reaktionären Interpretationen des Islam die sexuelle Gewalt von Männern mit dem Koran rechtfertigen. El Feki: Sexualität ist das was uns als Menschen zentral ausmacht. Sie durch Berufung auf eine höhere Autorität zu kontrollieren ist also eine verdammt gute Möglichkeit um Macht auszuüben - das hat der Islam erkannt wie auch schon andere Religionen. Konservative Auslegungen des Christentums oder des Hinduismus funktionieren da genauso. SPIEGEL ONLINE: Das bedeutet aber noch nicht dass man sich als Mann automatisch dieser Macht beugen muss. El Feki: In autoritären Systemen die stark auf Religion fußen ist es jedoch schwierig sich dieser Interpretation zu entziehen. Es gibt aber darüber hinaus bestimmte universelle Faktoren die ausschlaggebend dafür sind ob ein Mann gegenüber Frauen gewalttätig wird. Studien der Uno belegen dass sich die Umstände in denen Männer daheim oder öffentlich gegen Frauen gewalttätig werden weltweit ähneln: Männer werden zum Beispiel wahrscheinlicher gewalttätig wenn sie als Kinder selbst Opfer von Gewalt wurden. Wenn sie mitbekamen dass ihrer Mutter vom Vater oder einem anderen Mann Gewalt angetan wurde. Wenn sie denken dass das System sie damit davonkommen lässt. Dazu kommt vermutlich noch ein starker Einfluss von Arbeitslosigkeit die liegt in vielen arabischen Ländern im zweistelligen Prozentbereich. Wenn du als junger Mann in arabischen Ländern keinen Job bekommst hat das tiefgreifende Konsequenzen. SPIEGEL ONLINE: Weil du nicht als Versorger funktionierst? El Feki: Es wird schwieriger auszudrücken dass du nach konventionellen Vorstellungen ein richtiger Mann bist. Du kannst nicht aus dem Haus deiner Eltern ausziehen nicht heiraten. Und hast dann auch keinen regelmäßigen Sex. SPIEGEL ONLINE: Aber das ist doch noch keine Entschuldigung für sexualisierte Gewalt. El Feki: Ich habe selbst sexualisierte Gewalt erlebt und möchte ganz klar sagen dass solche Übergriffe immer unentschuldbar sind. Etwas zu verstehen bedeutet aber auch nicht es zu entschuldigen. Und wir können Einstellungen nun mal nur ändern wenn wir verstehen wie sie zustande kommen. In der arabischen Region wo die Männer in patriarchalisch organisierten und autoritär geführten Regimen auch noch unter Druck stehen wegen der ökonomischen Situation ist es nicht verwunderlich dass sie die Frustration ausagieren. SPIEGEL ONLINE: Das Sein bestimmt nach Ihrer Auffassung also das Bewusstsein. El Feki: Wie stark das Persönliche und das Politische verschlungen sind zeigen ja selbst die Diskussionen von Flüchtlingsgegnern über die Übergriffe an Silvester in Köln. Ich meine damit nicht die Gewalt per se. Sondern dass die Gewalttaten verallgemeinert werden wenn sie als Beispiel für sexuelle Praxis und Auffassungen über Sexualität in den arabischen Ländern insgesamt dargestellt werden. Was im Schlafzimmer passiert wird dann plötzlich relevant für eine Diskussion über Einwanderungspolitik. SPIEGEL ONLINE: Wie sollte Deutschland Flüchtlingen aus dem arabischen Raum begegnen um sexuellen Übergriffen vorzubeugen? El Feki: Je ausgeschlossener sich die Männer fühlen desto wahrscheinlicher werden Übergriffe. Das Ausgeschlossensein führt ja nur die Frustration weiter die schon in den Heimatländern erlebt wurde. Mit Bildung und Therapie kann man Einstellungen von Männern ändern - wenn sie noch jung sind und selbst dann wenn sie mit Gewalt aufgewachsen sind. SPIEGEL ONLINE: Wie schlägt sich Deutschland hier in Ihren Augen? El Feki: Hier erkenne ich bisher keine klare Idee sondern viel Übereiltes. Wenn Flüchtlinge plötzlich nicht mehr ins Schwimmbad gelassen werden und über härtere Abschiebungsregeln diskutiert wird ist das natürlich auf gewisse Weise die unausweichliche Konsequenz von Köln. Aber solche Aktionen helfen keinem. Auch weil klar ist: Wenn Deutschland junge Männer in ihre Länder zurückschickt produziert es erstklassiges Rekrutierungsmaterial für extremistische Bewegungen. Die die die sich hier abgelehnt fühlen werden nicht einfach friedlich heimkehren. Das Interview mit Shereen El Feki ist der erste Teil einer Gesprächsreihe von SPIEGEL ONLINE in der Perspektiven auf Islam und Sexualität beleuchtet werden.'\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=357,361>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"357\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"361\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=5424,5426>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"El\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"5424\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"5426\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/El> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=6954,6958>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"6954\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"6958\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=753,757>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Feki\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"753\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"757\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,8071> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Feki> .\n" +
					"";
	
	static String expectedResponse57 = 
			"@prefix dktnif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif-ann: <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-annotation#> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=381,391>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philosophy\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"381\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"391\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=71,78>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"ˈniːtʃə\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"71\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"78\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=516,525>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"516\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"525\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nietzsche> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=260,270>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philosophy\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"260\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"270\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=683,686>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"age\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"683\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"686\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/ontology/age> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,27>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"27\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nietzsche> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=505,508>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"age\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"505\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"508\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:Other ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/ontology/age> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=351,362>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philologist\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"351\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"362\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=888,897>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"888\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"897\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nietzsche> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=30,37>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"ˈniːtʃə\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"30\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"37\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=164,175>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"philologist\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"164\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"175\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,920> ;\n" +
					"        itsrdf:taClassRef     dktnif:dummy2LM .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,920>\n" +
					"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex    \"920\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString    \"Friedrich Wilhelm Nietzsche (/ˈniːtʃə/;[4] German: [ˈfʁiːdʁɪç ˈvɪlhɛlm ˈniːtʃə]; 15 October 1844 – 25 August 1900) was a German philosopher, cultural critic, poet, philologist, and Latin and Greek scholar whose work has exerted a profound influence on Western philosophy and modern intellectual history.[5][6][7][8] He began his career as a classical philologist before turning to philosophy. He became the youngest ever to hold the Chair of Classical Philology at the University of Basel in 1869, at the age of 24. Nietzsche resigned in 1879 due to health problems that plagued him most of his life, and he completed much of his core writing in the following decade.[9] In 1889, at age 44, he suffered a collapse and a complete loss of his mental faculties.[10] He lived his remaining years in the care of his mother (until her death in 1897), and then with his sister Elisabeth Förster-Nietzsche, and died in 1900.[11]\"^^xsd:string .\n" +
					"";
	
	static String germanPlainTextInput =
			
			"spiegel online	http://www.spiegel.de/panorama/gesellschaft/ukrainer-in-deutschland-vom-krieg-ins-exil-verbannt-a-1065478.html	Ukrainer in Deutschland: Flüchtlinge die niemand so nennt	Ukrainer in Deutschland	Flüchtlinge die niemand so nennt	Peter Maxwill	Artikel	Sie kamen für eine Woche und konnten nicht mehr zurück: Eine Gruppe junger Ukrainer ist in Deutschland gestrandet - wegen des Kriegs im Donbass staatliche Unterstützung bekommen sie nicht. Über ein Leben ohne Perspektive.	\"Die größte Meinungsverschiedenheit des Trios? Vielleicht die Frage was von Bremen zu halten ist: \"\"Wunderbar gemütlich\"\" sagt Alexander in perfektem Deutsch; \"\"ziemlich stressig und eng\"\" ist die Stadt in Yanas Augen. \"\"Einfach schön\"\" sei Bremen aber in jedem Fall wirft Alevtina ein - da nicken alle drei. \"\"Bloß keinen Streit jetzt\"\" fügt Alexander hinzu und nippt an seinem Kaffee. Alle lachen. Harmonie ist den drei Ukrainern besonders wichtig wenn sie sich treffen - so wie an diesem Wintertag in einem Bremer Café am Weserufer. Denn viel mehr als einander haben sie derzeit nicht: Vor anderthalb Jahren waren die angehenden Dolmetscher aus Donezk für eine siebentägige Studienreise nach Bremen geflogen dann überrollte der Krieg zwischen Regierungstruppen und prorussischen Kämpfern ihre Heimat und versperrte ihnen den Weg zurück. Seitdem sitzen sie in Deutschland fest ohne staatliche Unterstützung ohne Familie Freunde Perspektive. Yana Lysenko Alevtina Artash und Alexander Starostin sind Opfer eines Krieges den niemand so nennt und einer Flüchtlingskrise die kaum jemand kennt. Rund 6700 Menschen aus der Ukraine haben laut dem Bundesamt für Migration und Flüchtlinge seit dem Ausbruch des Konflikts vor zwei Jahren in Deutschland Asyl beantragt. Die tatsächliche Zahl der Einwanderer aus der Ukraine ist vermutlich deutlich höher. Die Ukraine ist derzeit Schauplatz einer weitgehend unbeachteten humanitären Krise: Seit der Konflikt mit prorussischen Kämpfern das Land zerrüttet herrschen vor allem im umkämpften Gebiet Donbass schlimme Zustände: Fünf Millionen Menschen sind laut Europarat insgesamt in Not geraten mehr als 24 Millionen haben nach Angaben des ukrainischen Sozialministeriums ihre Heimat verlassen. Yana Alexander und Alevtina waren im Juni 2014 noch Studenten der Universität Donezk im Studiengang Dolmetschen nur einen Monat später waren sie plötzlich Flüchtlinge - auch wenn sie sich selbst nicht so bezeichnen möchten. Damals zerstörte eine Buk-Rakete das Flugzeug MH17 über der Ostukraine und rund um Donezk flammten heftige Kämpfe auf. Die Studenten diskutierten bei Bremen gerade mit Altersgenossen in einer NS-Gedenkstätte über den Zweiten Weltkrieg und stellten ein Theaterprojekt auf die Beine als die Eskalation des Konflikts ihnen den Heimweg versperrte. So wurde aus ihrer Heimatstadt Donezk das Kriegsgebiet Donezk ihr Studentenwohnheim sei nun von Separatisten besetzt und in eine Kaserne verwandelt worden. Die Austauschstudenten waren nun Heimatlose ihre Zukunft ungewiss. \"\"Wir sitzen in der Falle\"\" sagte Alexander damals zu SPIEGEL ONLINE - und viel verändert hat sich daran nicht. Suche nach neuen Perspektiven Mittlerweile hätten 800.000 Ukrainer im Ausland Asyl beantragt sagt Bettina Schulte von der Uno-Flüchtlingsorganisation UNHCR - darunter sind viele Kinder und Jugendliche. \"\"Allein im Donbass sind derzeit 27 Millionen Menschen auf Hilfe angewiesen\"\" so Schulte. Das ukrainische Sozialministerium registriert zudem 5000 weitere Migranten aus dem Osten des Landes - pro Monat. \"\"Die Flüchtlinge können natürlich nur dann heimkehren wenn sich die politische Lage im Donbass stabilisiert\"\" sagt Schulte. Das gilt auch für Menschen wie Yana Alexander und Alevtina. Sie sind auf der Suche nach neuen Perspektiven im Großraum Bremen geblieben. Viele ihrer Freunde versuchen es inzwischen woanders: Katja 19 leistet Bundesfreiwilligendienst in Hannover Jewgenija 19 ist Au-Pair-Mädchen in Wien Anja 23 macht ein Freiwilliges Soziales Jahr im norddeutschen Osterholz-Scharmbek. Die anderen treffen sich nach wie vor regelmäßig in Bremen Alevtina und Yana haben eine WG gegründet Alexander stößt aus dem nahen Worpswede oft dazu. In Deutschland ist ihr Status völlig unklar. \"\"Eine Behördenpraxis besteht bezogen auf Asylantragsteller aus der Ukraine aktuell nicht\"\" teilte die Bundesregierung im Juni 2015 mit. \"\"Einschlägige Rechtsprechung gibt es dazu bisher nicht.\"\" Die Behörden wissen also nicht was sie mit Menschen machen sollen die der ukrainische Konflikt nach Deutschland gebracht hat. So gelten sie derzeit in Bremen als Studenten aus einem Nicht-EU-Land. Das bedeutet: Sie müssen ein Konto mit knapp 8000 Euro Guthaben vorweisen und erhalten keine Unterstützung als Asylbewerber kein Hartz IV kein Bafög kein Kindergeld. Stattdessen leben Alevtina Alexander und Yana nun von Stipendien Nebenjobs Spenden. Mehr über ihr Leben erzählen sie hier: \"\"Jetzt fängt mein Leben nochmal von vorne an\"\" Alevtina Artash (21) \"\"Ich war Anfang Juni noch mal in der Ukraine aber natürlich nicht in Donezk: Die Professoren die nicht mit den Rebellen zusammenarbeiten wollten haben eine Exil-Universität in Winnyzja bei Kiew eröffnet. Dort habe ich die letzte Prüfung meines Bachelor-Studiums nachgeholt damit ich hier in Bremen direkt mit dem Master anfangen kann. Im August kam dann mein Diplom aus der Ukraine darüber war ich unglaublich glücklich - denn sonst hätte ich noch mal ganz von vorne anfangen müssen. Meine Mutter hat mir inzwischen auch einen Koffer mit persönlichen Dingen und Kleidung geschickt ich bin ja nur mit Gepäck für eine Woche nach Deutschland gekommen. Dank eines Stipendiums studiere ich jetzt Sprachwissenschaft. Aber ich habe keine Ahnung was ich später mal machen werde: In das Separatistengebiet kann ich nicht zurück außerdem ist auch meine Mutter vor den Bomben in die unbesetzte Ukraine geflohen. Dort werde ich aber wohl auch keinen Job finden: Das ist für Ukrainer aus dem Donbass seit Ausbruch des Kriegs sehr schwierig. Ich habe trotzdem noch nicht den Wunsch abgehakt zurückzugehen - auch wenn das erst in zehn Jahren ist.\"\" Alexander Starostin (21) \"\"Ich kam schon mit 15 an die Uni für mein Leben gab es also schon ziemlich früh einen Plan: Nach dem Studium wollte ich Dolmetscher werden am liebsten natürlich in der Ukraine. Jetzt fängt mein Leben nochmal von vorne an - und ich habe gelernt keine Pläne mehr zu machen. Der Waffenstillstand zu Hause existiert nur auf dem Papier sogar die Wohnung meiner Schwester wurde schon beschossen. Ich selbst war vor anderthalb Jahren für ein paar Wochen auf der Krim und während eines Waffenstillstands wenig später kurz in Donezk um mein Bachelor-Diplom abzuholen. Dann habe ich zwei Praktika in einem Theater und in einer Kunsthalle in Niedersachsen gemacht inzwischen lebe ich vor allem von Spenden. Im Studium beschäftige ich mich jetzt mit grenzüberschreitender Kultur und mit Minderheiten in der Kunst und das passt gut: Ich weiß ja selbst sehr gut wie es sich als Fremder in einem anderen Land anfühlt - und was es bedeutet das bisherige Leben verloren zu haben.\"\" Yana Lysenko (22) \"\"Einige meiner Verwandten leben noch in Donezk aber meine Eltern sind nach Russland übergesiedelt. Dabei will ich aber betonen dass sie nicht aus politischen Gründen dorthin gezogen sind. Meinen Vater habe ich seit mehr als anderthalb Jahren nicht gesehen wir skypen aber regelmäßig. Im vergangenen Jahr war ich zweimal in der Ukraine. Einmal im Sommer um in Winnyza meine Master-Prüfungen abzulegen und einmal im Herbst um meine Familie wiederzusehen. In Deutschland habe ich parallel zu meinem Fernstudium in einer Bildungsstätte nördlich von Bremen ein Praktikum gemacht. Mit meinem Praktikum war ich im August 2015 fertig und habe mich für einen Studienplatz an der Uni Bremen beworben. Mein Studiengang heißt Integrierte Europastudien. Dabei geht es vor allem um Osteuropa ich lerne im Rahmen des Studiums auch Polnisch und Tschechisch nebenbei gebe ich meinen Kommilitonen ein Russisch-Sprachtutorium. Später möchte ich auch noch Politik oder internationale Beziehungen studieren; natürlich auch weil mich diese Themen persönlich betreffen. In die Ukraine zurückzukehren kann ich mir im Moment nicht vorstellen - weil das gesamte Donbass unter der Sowjetunion-Krankheit leidet.\"\" Animation DER SPIEGEL Wie der Krieg in die Ukraine kam Zusammengefasst: Hunderttausende Ukrainer haben wegen des Konflikts im Donbass ihr Heimatland verlassen. Viele sind auch nach Deutschland gekommen manche eher unfreiwillig - so wie die Studenten Yana Alexander und Alevtina. Die drei können nicht zurück in ihre Heimatstadt Donezk und bauen sich nun in Deutschland ein neues Leben auf. Das ist ziemlich schwierig denn sie sind weder EU-Bürger noch anerkannte Flüchtlinge.\"		1453796700000	26.01.2016 – 08:25	Ukraine-Konflikt	Panorama	Gesellschaft	http://cdn2.spiegel.de/images/image-937691-breitwandaufmacher-bvqj-937691.jpg	Kriegsflüchtlinge aus der Ukraine: Alleine in Deutschland		SPIEGEL ONLINE	© SPIEGEL ONLINE 2016	\n" +
					"spiegel online	http://www.spiegel.de/politik/deutschland/fluechtlinge-darf-deutschland-fluechtlinge-abweisen-a-1073833.html	Debatte um Obergrenzen: Darf Deutschland Flüchtlinge abweisen?	Debatte um Obergrenzen	Darf Deutschland Flüchtlinge abweisen?	Dietmar Hipp	Artikel	Es gibt eine Menge Pläne und Forderungen wie man die Anzahl der Flüchtlinge reduzieren kann. Aber was geht überhaupt? Wie ist die Rechtslage tatsächlich? Die wichtigsten Fragen und Antworten.	\"Plan B Plan A2 Mini-Schengen oder die Einführung von Obergrenzen - das sind Politiker-Codes für mögliche Lösungen der Flüchtlingskrise. Allen gemein ist dass an europäischen Binnengrenzen stärker als bisher Flüchtlinge abgewiesen werden sollen. Also Grenzen wie jener zwischen Deutschland und Österreich oder Slowenien und Kroatien. Nur: So plakativ und simpel all diese Forderungen sind so kompliziert sind dann eben doch die geltenden Gesetze. Was also ist rechtlich tatsächlich möglich? Lesen Sie hier die wichtigsten Fragen und Antworten. Wann sind Grenzkontrollen zwischen europäischen Staaten erlaubt? Durch die sogenannten Schengener Abkommen wurden zwar in einem Teil der EU (und benachbarter Staaten) die stationären Grenzkontrollen an den Binnengrenzen abgeschafft. Im Falle einer schwerwiegenden Bedrohung der öffentlichen Ordnung oder der inneren Sicherheit ist aber die vorübergehende Wiedereinführung von Grenzkontrollen erlaubt. Genau das macht zum Beispiel Deutschland derzeit. An den sogenannten Schengen-Außengrenzen darf ohnehin kontrolliert werden - also etwa zwischen Slowenien und Kroatien das zwar EU-Mitglied ist aber nicht zum Schengenraum gehört. Darf man bei solchen Kontrollen Flüchtlinge zurückweisen? Wer unerlaubt einreisen will der darf nach deutschem und europäischem Recht an der Grenze zurückgewiesen werden - etwa Personen die keinen Pass mit sich führen. Allerdings: Wird ein Asylantrag gestellt gilt diese Regel nicht. Zwar darf nach deutschem Recht einem Asylsuchenden die Einreise verweigert werden wenn er aus einem sogenannten sicheren Drittstaat einreist - und Deutschland ist eigentlich de facto nur von sicheren Drittstaaten umgeben. Aber dieses deutsche Asylverfahrensrecht wird überlagert von europäischen Vorschriften: Die deutsche Drittstaatenregelung gilt deshalb nicht bei einer Einreise aus EU-Mitgliedstaaten - und aufgrund eines besonderen Abkommens auch nicht gegenüber der Schweiz. Gibt es da keinen Interpretationsspielraum? Doch. Das Innenministerium zum Beispiel hält es für \"\"vertretbar\"\" Österreich auch nach europäischem Recht als sicheren Drittstaat zu behandeln. Im Moment ist das aber politisch - noch - nicht gewollt. Aber werden nicht bereits Flüchtlinge an der deutschen Grenze zurückgewiesen? Ja wer nicht explizit um Schutz in Deutschland bittet dem wird derzeit die Einreise verweigert. Das trifft regelmäßig etwa jene Flüchtlinge die durch Deutschland reisen wollen um in Schweden Asyl zu suchen. Diese werden dann nach Österreich zurückgewiesen. Was geschieht wenn ein Flüchtling an der deutsch-österreichischen Grenze das Wort \"\"Asyl\"\" sagt? Dann muss Deutschland seinen Asylantrag prüfen sofern es nach europäischem Recht zuständig ist. Wenn nicht dann muss zumindest geprüft werden welcher Staat für das Asylverfahren zuständig ist. Das bestimmt sich nach den sogenannten Dublin-Regeln. Diese werden zwar im Moment vielfach ignoriert doch könnte sich Deutschland natürlich nach wie vor auf deren Einhaltung berufen. Demnach müssen Flüchtlinge eigentlich in dem Staat Asyl bekommen über den sie erstmals in die EU eingereist sind. Für Flüchtlinge aus dem Nahen Osten wären das typischerweise Kroatien oder Griechenland. Heißt das die Asylsuchenden würden nach Österreich zurückgeschickt? Nein. Zuständig ist im Prinzip der Mitgliedstaat über den sie in die EU eingereist sind. \"\"Deutschland müsste Asylbewerber dann auch genau dorthin zurückschicken - und sie nicht einfach an der deutsch-österreichischen Grenze abweisen\"\" so der Konstanzer Europarechtsexperte Daniel Thym. Es müsste also erstens geprüft werden ob ein anderer Staat zuständig ist und wenn ja welcher. Zweitens müsste dieser Staat dann auch bereit sein den Flüchtling zurückzunehmen. An der slowenisch-kroatischen Grenze sieht eine solche Zurückweisung natürlich einfacher aus: Weil die Flüchtlinge in den allermeisten Fällen über Kroatien eingereist sind ist dann auch Kroatien für sie zuständig. Deutschland wäre aber ausnahmsweise zuständig etwa für Minderjährige ohne Begleitung und für Angehörige von Asylsuchenden deren Antrag bereits in Deutschland bearbeitet wurde oder wird. Muss sich Deutschland noch an europäische Regeln halten wenn andere das schon längst nicht mehr tun? Faktisch ist das erwähnte Dublin-System zusammengebrochen und es gibt sogar eine Vorschrift in den Europäischen Verträgen nach der ein Mitgliedstaat ausnahmsweise von EU-Richtlinien und -Verordnungen abweichen kann wenn \"\"die Aufrechterhaltung der öffentlichen Ordnung\"\" und der \"\"Schutz der inneren Sicherheit\"\" berührt sind. Solche Überlegungen gibt es auch im Innenministerium. Demnach würde das \"\"derzeitige Systemversagen des europäischen (Außen-)Grenzschutz- und Asylsystems\"\" ein \"\"Vorgehen zum Schutz der öffentlichen Sicherheit\"\" eröffnen. Rechtlich wäre dieser Weg am ehesten gangbar politisch ist er aber bisher nicht gewollt. Diese Variante ginge wohl auch nur vorübergehend und nur in dem Maß in dem das unbedingt notwendig ist. Die bloße Einhaltung von politisch gewollten Obergrenzen etwa dürfte also eher nicht dazu gehören. AP Antworten auf die wichtigsten Fragen zur Flüchtlingskrise Forum Liebe Leserinnen und Leser im Unterschied zu vielen anderen Artikeln auf SPIEGEL ONLINE finden Sie unter diesem Text kein Forum. Leider erreichen uns zum Thema Flüchtlinge so viele unangemessene beleidigende oder justiziable Forumsbeiträge dass eine gewissenhafte Moderation nach den Regeln unserer Netiquette kaum mehr möglich ist. Deshalb gibt es nur unter ausgewählten Artikeln zu diesem Thema ein Forum. Wir bitten um Verständnis.\"		1453819860000	26.01.2016 – 14:51	Deutsche Einwanderungspolitik	Politik	Deutschland	http://cdn1.spiegel.de/images/image-948394-breitwandaufmacher-celi-948394.jpg	Flüchtlinge an der deutsch-österreichischen Grenze: Ein Grundsatz und viele Ausnahmen	Flüchtlinge an der deutsch-österreichischen Grenze: Ein Grundsatz und viele Ausnahmen	DPA	© SPIEGEL ONLINE 2016	\n" +
					"spiegel online	http://www.spiegel.de/kultur/gesellschaft/sexualitaet-im-islam-interview-mit-shereen-el-feki-a-1072533.html	Sexualität und Islam: Mohammed war in gewisser Weise Feminist	Sexualität und Islam	Mohammed war in gewisser Weise Feminist	Eva Thöne	Interview	Ist der Islam aggressiv und antifeministisch? Die ägyptische Wissenschaftlerin Shereen El Feki plädiert dafür genauer hinzusehen - und erklärt was Sexualität mit Abschiebung zu tun hat.	\"Shereen El Feki Shereen El Feki wuchs als Tochter eines Ägypters und einer Britin in Kanada auf und studierte Immunologie. Später arbeitete sie als Journalistin u.a. für den Sender Al Jazeera außerdem für die Uno. 2013 erschien ihr Buch \"\"Sex und die Zitadelle. Liebesleben in der sich wandelnden arabischen Welt\"\". Derzeit arbeitet sie mit der NGO \"\"Promundo\"\" an einer breiten Studie zu Sexualität im arabischen Raum. El Feki pendelt zwischen London und Kairo. SPIEGEL ONLINE: Frau El Feki Sie leben in Kairo und London. Wo fühlen Sie sich sicherer vor sexueller Gewalt? El Feki: Ich bin kein gutes Beispiel. Ich bin Ausländerin in Ägypten und werde auch immer so wahrgenommen - wie es den Frauen im Alltagsleben geht vermag ich nicht zu bewerten. Ich weiß natürlich worauf Sie mit Ihrer Frage hinauswollen. Ja sexuelle Gewalt spielt eine große Rolle in Ägypten. Umfragen der Uno haben ergeben dass 99 Prozent aller ägyptischen Frauen schon mal sexuell belästigt wurden. Trotzdem: Es gibt auch Fortschritte. SPIEGEL ONLINE: Wo sehen Sie die bei dieser hohen Quote? El Feki: Vor zehn Jahren war das Thema sexuelle Gewalt noch absolut tabu heute ist es Gespräch. Die sexuellen Attacken auf dem Tahrirplatz ab 2011 waren der Auslöser weil sie viele Menschen dazu brachten sich offen zu äußern. Viele der Konservativen die damals argumentierten die Frauen seien nicht angemessen angezogen gewesen trauen sich heute nicht mehr das so auszusprechen. Sie mögen es noch denken. Aber sie schweigen weil sich der öffentliche Diskurs geändert hat. Das hat natürlich auch mit dem Opportunismus der aktuellen Regierung zu tun: Weil sich Al-Sisi zumindest nach außen hin von den Muslimbrüdern distanzieren will kriminalisiert er sexuelle Belästigung. Für andere Bereiche gilt das nicht. Im Namen der Religion werden in Ägypten Homosexuelle nach wie vor ins Gefängnis geworfen. SPIEGEL ONLINE: Über Ägypten hinaus - beobachten Sie im arabischen Raum länderübergreifend eine Ausweitung von Frauenrechten? El Feki: Ich kann in dieser Debatte nur vor jeder Verallgemeinerung warnen. Ich kann nicht über Millionen Menschen in ganz unterschiedlichen Lebenssituationen sprechen. Klar ist nur: Der Aufstieg des \"\"Islamischen Staates\"\" (IS) hat in vielen arabischen Ländern eine Debatte darüber ausgelöst was mit dem Islam passiert. Wer spricht in seinem Namen und bestimmt so auch Vorstellungen von Sexualität mit? Durch Abgrenzung vom IS finden viele junge Araber ihre Stimme. \"\"Das ist nicht mein Islam\"\" ist die Wendung die Sie dann in sozialen Netzwerken lesen können. Häufig hören wir in den westlichen Massenmedien aber nur von den Männern die Frauen belästigen. Die die sich in Projekten und Initiativen gegen Sexualgewalt stellen finden kaum statt. Der Posterboy des Jahres 2016 wird in den westlichen Medien der Flüchtling mit einem Abschiebebescheid in der Hand sein. Das ist verkürzt und manchmal auch rassistisch. Liegt aber auch daran dass der Diskurs über den Islam während der letzten Jahrzehnte vor allem von fundamentalistischen Stimmen geprägt war und so auch von konservative Interpretationen des Korans. Tatsächlich hat der Islam aber auch Potenzial für gleichberechtigtes Leben. Islamische Feministinnen weisen seit Jahren immer wieder darauf hin. SPIEGEL ONLINE: Haben Sie ein Beispiel für dieses Potenzial? El Feki: Das geht zurück bis zum Propheten Mohammed der von starken Frauen umgeben war. Er äußerte sich auch sehr klar zu Sex sagte etwa dass auch die Frau den Geschlechtsverkehr genießen solle. In gewisser Weise war er Feminist. SPIEGEL ONLINE: Es gibt aber eben auch diese reaktionären Interpretationen des Islam die sexuelle Gewalt von Männern mit dem Koran rechtfertigen. El Feki: Sexualität ist das was uns als Menschen zentral ausmacht. Sie durch Berufung auf eine höhere Autorität zu kontrollieren ist also eine verdammt gute Möglichkeit um Macht auszuüben - das hat der Islam erkannt wie auch schon andere Religionen. Konservative Auslegungen des Christentums oder des Hinduismus funktionieren da genauso. SPIEGEL ONLINE: Das bedeutet aber noch nicht dass man sich als Mann automatisch dieser Macht beugen muss. El Feki: In autoritären Systemen die stark auf Religion fußen ist es jedoch schwierig sich dieser Interpretation zu entziehen. Es gibt aber darüber hinaus bestimmte universelle Faktoren die ausschlaggebend dafür sind ob ein Mann gegenüber Frauen gewalttätig wird. Studien der Uno belegen dass sich die Umstände in denen Männer daheim oder öffentlich gegen Frauen gewalttätig werden weltweit ähneln: Männer werden zum Beispiel wahrscheinlicher gewalttätig wenn sie als Kinder selbst Opfer von Gewalt wurden. Wenn sie mitbekamen dass ihrer Mutter vom Vater oder einem anderen Mann Gewalt angetan wurde. Wenn sie denken dass das System sie damit davonkommen lässt. Dazu kommt vermutlich noch ein starker Einfluss von Arbeitslosigkeit die liegt in vielen arabischen Ländern im zweistelligen Prozentbereich. Wenn du als junger Mann in arabischen Ländern keinen Job bekommst hat das tiefgreifende Konsequenzen. SPIEGEL ONLINE: Weil du nicht als Versorger funktionierst? El Feki: Es wird schwieriger auszudrücken dass du nach konventionellen Vorstellungen ein richtiger Mann bist. Du kannst nicht aus dem Haus deiner Eltern ausziehen nicht heiraten. Und hast dann auch keinen regelmäßigen Sex. SPIEGEL ONLINE: Aber das ist doch noch keine Entschuldigung für sexualisierte Gewalt. El Feki: Ich habe selbst sexualisierte Gewalt erlebt und möchte ganz klar sagen dass solche Übergriffe immer unentschuldbar sind. Etwas zu verstehen bedeutet aber auch nicht es zu entschuldigen. Und wir können Einstellungen nun mal nur ändern wenn wir verstehen wie sie zustande kommen. In der arabischen Region wo die Männer in patriarchalisch organisierten und autoritär geführten Regimen auch noch unter Druck stehen wegen der ökonomischen Situation ist es nicht verwunderlich dass sie die Frustration ausagieren. SPIEGEL ONLINE: Das Sein bestimmt nach Ihrer Auffassung also das Bewusstsein. El Feki: Wie stark das Persönliche und das Politische verschlungen sind zeigen ja selbst die Diskussionen von Flüchtlingsgegnern über die Übergriffe an Silvester in Köln. Ich meine damit nicht die Gewalt per se. Sondern dass die Gewalttaten verallgemeinert werden wenn sie als Beispiel für sexuelle Praxis und Auffassungen über Sexualität in den arabischen Ländern insgesamt dargestellt werden. Was im Schlafzimmer passiert wird dann plötzlich relevant für eine Diskussion über Einwanderungspolitik. SPIEGEL ONLINE: Wie sollte Deutschland Flüchtlingen aus dem arabischen Raum begegnen um sexuellen Übergriffen vorzubeugen? El Feki: Je ausgeschlossener sich die Männer fühlen desto wahrscheinlicher werden Übergriffe. Das Ausgeschlossensein führt ja nur die Frustration weiter die schon in den Heimatländern erlebt wurde. Mit Bildung und Therapie kann man Einstellungen von Männern ändern - wenn sie noch jung sind und selbst dann wenn sie mit Gewalt aufgewachsen sind. SPIEGEL ONLINE: Wie schlägt sich Deutschland hier in Ihren Augen? El Feki: Hier erkenne ich bisher keine klare Idee sondern viel Übereiltes. Wenn Flüchtlinge plötzlich nicht mehr ins Schwimmbad gelassen werden und über härtere Abschiebungsregeln diskutiert wird ist das natürlich auf gewisse Weise die unausweichliche Konsequenz von Köln. Aber solche Aktionen helfen keinem. Auch weil klar ist: Wenn Deutschland junge Männer in ihre Länder zurückschickt produziert es erstklassiges Rekrutierungsmaterial für extremistische Bewegungen. Die die die sich hier abgelehnt fühlen werden nicht einfach friedlich heimkehren. Das Interview mit Shereen El Feki ist der erste Teil einer Gesprächsreihe von SPIEGEL ONLINE in der Perspektiven auf Islam und Sexualität beleuchtet werden.\"		1453819080000	26.01.2016 – 14:38	Gewalt gegen Frauen	Kultur	Gesellschaft	http://cdn1.spiegel.de/images/image-948018-breitwandaufmacher-riqs-948018.jpg	\"Syrer beten in einem türkischen Flüchtlingscamp: \"\"Etwas zu verstehen bedeutet aber auch nicht es zu entschuldigen.\"	\"Syrer beten in einem türkischen Flüchtlingscamp: \"\"Etwas zu verstehen bedeutet aber auch nicht es zu entschuldigen.\"	DPA	© SPIEGEL ONLINE 2016	\n";
	static String germanPlainTextInputShortened = "Sexualität und Islam: Mohammed war in gewisser Weise Feminist	Sexualität und Islam	Mohammed war in gewisser Weise Feminist	Eva Thöne	Interview	Ist der Islam aggressiv und antifeministisch? Die ägyptische Wissenschaftlerin Shereen El Feki plädiert dafür genauer hinzusehen - und erklärt was Sexualität mit Abschiebung zu tun hat.	'Shereen El Feki Shereen El Feki wuchs als Tochter eines Ägypters und einer Britin in Kanada auf und studierte Immunologie. Später arbeitete sie als Journalistin u.a. für den Sender Al Jazeera außerdem für die Uno. 2013 erschien ihr Buch ''Sex und die Zitadelle. Liebesleben in der sich wandelnden arabischen Welt''. Derzeit arbeitet sie mit der NGO ''Promundo'' an einer breiten Studie zu Sexualität im arabischen Raum. El Feki pendelt zwischen London und Kairo. SPIEGEL ONLINE: Frau El Feki Sie leben in Kairo und London. Wo fühlen Sie sich sicherer vor sexueller Gewalt? El Feki: Ich bin kein gutes Beispiel. Ich bin Ausländerin in Ägypten und werde auch immer so wahrgenommen - wie es den Frauen im Alltagsleben geht vermag ich nicht zu bewerten. Ich weiß natürlich worauf Sie mit Ihrer Frage hinauswollen. Ja sexuelle Gewalt spielt eine große Rolle in Ägypten. Umfragen der Uno haben ergeben dass 99 Prozent aller ägyptischen Frauen schon mal sexuell belästigt wurden. Trotzdem: Es gibt auch Fortschritte. SPIEGEL ONLINE: Wo sehen Sie die bei dieser hohen Quote? El Feki: Vor zehn Jahren war das Thema sexuelle Gewalt noch absolut tabu heute ist es Gespräch. Die sexuellen Attacken auf dem Tahrirplatz ab 2011 waren der Auslöser weil sie viele Menschen dazu brachten sich offen zu äußern. Viele der Konservativen die damals argumentierten die Frauen seien nicht angemessen angezogen gewesen trauen sich heute nicht mehr das so auszusprechen. Sie mögen es noch denken. Aber sie schweigen weil sich der öffentliche Diskurs geändert hat. Das hat natürlich auch mit dem Opportunismus der aktuellen Regierung zu tun: Weil sich Al-Sisi zumindest nach außen hin von den Muslimbrüdern distanzieren will kriminalisiert er sexuelle Belästigung. Für andere Bereiche gilt das nicht. Im Namen der Religion werden in Ägypten Homosexuelle nach wie vor ins Gefängnis geworfen. SPIEGEL ONLINE: Über Ägypten hinaus - beobachten Sie im arabischen Raum länderübergreifend eine Ausweitung von Frauenrechten? El Feki: Ich kann in dieser Debatte nur vor jeder Verallgemeinerung warnen. Ich kann nicht über Millionen Menschen in ganz unterschiedlichen Lebenssituationen sprechen. Klar ist nur: Der Aufstieg des ''Islamischen Staates'' (IS) hat in vielen arabischen Ländern eine Debatte darüber ausgelöst was mit dem Islam passiert. Wer spricht in seinem Namen und bestimmt so auch Vorstellungen von Sexualität mit? Durch Abgrenzung vom IS finden viele junge Araber ihre Stimme. ''Das ist nicht mein Islam'' ist die Wendung die Sie dann in sozialen Netzwerken lesen können. Häufig hören wir in den westlichen Massenmedien aber nur von den Männern die Frauen belästigen. Die die sich in Projekten und Initiativen gegen Sexualgewalt stellen finden kaum statt. Der Posterboy des Jahres 2016 wird in den westlichen Medien der Flüchtling mit einem Abschiebebescheid in der Hand sein. Das ist verkürzt und manchmal auch rassistisch. Liegt aber auch daran dass der Diskurs über den Islam während der letzten Jahrzehnte vor allem von fundamentalistischen Stimmen geprägt war und so auch von konservative Interpretationen des Korans. Tatsächlich hat der Islam aber auch Potenzial für gleichberechtigtes Leben. Islamische Feministinnen weisen seit Jahren immer wieder darauf hin. SPIEGEL ONLINE: Haben Sie ein Beispiel für dieses Potenzial? El Feki: Das geht zurück bis zum Propheten Mohammed der von starken Frauen umgeben war. Er äußerte sich auch sehr klar zu Sex sagte etwa dass auch die Frau den Geschlechtsverkehr genießen solle. In gewisser Weise war er Feminist. SPIEGEL ONLINE: Es gibt aber eben auch diese reaktionären Interpretationen des Islam die sexuelle Gewalt von Männern mit dem Koran rechtfertigen. El Feki: Sexualität ist das was uns als Menschen zentral ausmacht. Sie durch Berufung auf eine höhere Autorität zu kontrollieren ist also eine verdammt gute Möglichkeit um Macht auszuüben - das hat der Islam erkannt wie auch schon andere Religionen. Konservative Auslegungen des Christentums oder des Hinduismus funktionieren da genauso. SPIEGEL ONLINE: Das bedeutet aber noch nicht dass man sich als Mann automatisch dieser Macht beugen muss. El Feki: In autoritären Systemen die stark auf Religion fußen ist es jedoch schwierig sich dieser Interpretation zu entziehen. Es gibt aber darüber hinaus bestimmte universelle Faktoren die ausschlaggebend dafür sind ob ein Mann gegenüber Frauen gewalttätig wird. Studien der Uno belegen dass sich die Umstände in denen Männer daheim oder öffentlich gegen Frauen gewalttätig werden weltweit ähneln: Männer werden zum Beispiel wahrscheinlicher gewalttätig wenn sie als Kinder selbst Opfer von Gewalt wurden. Wenn sie mitbekamen dass ihrer Mutter vom Vater oder einem anderen Mann Gewalt angetan wurde. Wenn sie denken dass das System sie damit davonkommen lässt. Dazu kommt vermutlich noch ein starker Einfluss von Arbeitslosigkeit die liegt in vielen arabischen Ländern im zweistelligen Prozentbereich. Wenn du als junger Mann in arabischen Ländern keinen Job bekommst hat das tiefgreifende Konsequenzen. SPIEGEL ONLINE: Weil du nicht als Versorger funktionierst? El Feki: Es wird schwieriger auszudrücken dass du nach konventionellen Vorstellungen ein richtiger Mann bist. Du kannst nicht aus dem Haus deiner Eltern ausziehen nicht heiraten. Und hast dann auch keinen regelmäßigen Sex. SPIEGEL ONLINE: Aber das ist doch noch keine Entschuldigung für sexualisierte Gewalt. El Feki: Ich habe selbst sexualisierte Gewalt erlebt und möchte ganz klar sagen dass solche Übergriffe immer unentschuldbar sind. Etwas zu verstehen bedeutet aber auch nicht es zu entschuldigen. Und wir können Einstellungen nun mal nur ändern wenn wir verstehen wie sie zustande kommen. In der arabischen Region wo die Männer in patriarchalisch organisierten und autoritär geführten Regimen auch noch unter Druck stehen wegen der ökonomischen Situation ist es nicht verwunderlich dass sie die Frustration ausagieren. SPIEGEL ONLINE: Das Sein bestimmt nach Ihrer Auffassung also das Bewusstsein. El Feki: Wie stark das Persönliche und das Politische verschlungen sind zeigen ja selbst die Diskussionen von Flüchtlingsgegnern über die Übergriffe an Silvester in Köln. Ich meine damit nicht die Gewalt per se. Sondern dass die Gewalttaten verallgemeinert werden wenn sie als Beispiel für sexuelle Praxis und Auffassungen über Sexualität in den arabischen Ländern insgesamt dargestellt werden. Was im Schlafzimmer passiert wird dann plötzlich relevant für eine Diskussion über Einwanderungspolitik. SPIEGEL ONLINE: Wie sollte Deutschland Flüchtlingen aus dem arabischen Raum begegnen um sexuellen Übergriffen vorzubeugen? El Feki: Je ausgeschlossener sich die Männer fühlen desto wahrscheinlicher werden Übergriffe. Das Ausgeschlossensein führt ja nur die Frustration weiter die schon in den Heimatländern erlebt wurde. Mit Bildung und Therapie kann man Einstellungen von Männern ändern - wenn sie noch jung sind und selbst dann wenn sie mit Gewalt aufgewachsen sind. SPIEGEL ONLINE: Wie schlägt sich Deutschland hier in Ihren Augen? El Feki: Hier erkenne ich bisher keine klare Idee sondern viel Übereiltes. Wenn Flüchtlinge plötzlich nicht mehr ins Schwimmbad gelassen werden und über härtere Abschiebungsregeln diskutiert wird ist das natürlich auf gewisse Weise die unausweichliche Konsequenz von Köln. Aber solche Aktionen helfen keinem. Auch weil klar ist: Wenn Deutschland junge Männer in ihre Länder zurückschickt produziert es erstklassiges Rekrutierungsmaterial für extremistische Bewegungen. Die die die sich hier abgelehnt fühlen werden nicht einfach friedlich heimkehren. Das Interview mit Shereen El Feki ist der erste Teil einer Gesprächsreihe von SPIEGEL ONLINE in der Perspektiven auf Islam und Sexualität beleuchtet werden.'";
}
