package de.dkt.eservices.ecorenlp;


public class TestConstants {
	
	public static final String pathToPackage = "rdftest/ecorenlp-test-package.xml";
	
	static String expectedResponse = 
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
					"        nif:posTag            \"NN\"^^xsd:string ;\n" +
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
	
	
}
