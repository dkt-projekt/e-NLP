package de.dkt.eservices.enlp;

public class TestConstants {
	
	public static final String pathToPackage = "rdftest/enlp-test-package.xml";
	
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
	
	static String expectedResponse22 = 
			"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
					"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix dfkinif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=11,17>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Berlin\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"11\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"17\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,26> ;\n" +
					"        geo:lat               \"52.516666666666666\"^^xsd:double ;\n" +
					"        geo:long              \"13.383333333333333\"^^xsd:double ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Berlin> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,26>\n" +
					"        a                         nif:Context , nif:String , nif:RFC5147String ;\n" +
					"        dfkinif:averageLatitude   \"52.516666666666666\"^^xsd:double ;\n" +
					"        dfkinif:averageLongitude  \"13.383333333333333\"^^xsd:double ;\n" +
					"        dfkinif:standardDeviationLatitude\n" +
					"                \"0.0\"^^xsd:double ;\n" +
					"        dfkinif:standardDeviationLongitude\n" +
					"                \"0.0\"^^xsd:double ;\n" +
					"        nif:beginIndex            \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex              \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString              \"Welcome to Berlin in 2016.\"^^xsd:string .\n" +
					"";
	
	static String turtleInput3 = 
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
			"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
			"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
			"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
			"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=0,26>\n" +
			"        a                    nif:RFC5147String , nif:String , nif:Context ;\n" +
			"        nif:beginIndex       \"0\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex         \"26\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:isString         \"Welcome to Berlin in 2016.\"^^xsd:string ;\n" +
			"        nif:meanDateRange    \"20160101010000_20170101010000\"^^xsd:string .\n" +
			"\n" +
			"<http://dkt.dfki.de/documents/#char=21,25>\n" +
			"        a                  nif:RFC5147String , nif:String ;\n" +
			"        nif:anchorOf       \"2016\"^^xsd:string ;\n" +
			"        nif:beginIndex     \"21\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:endIndex       \"25\"^^xsd:nonNegativeInteger ;\n" +
			"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" +
			"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=20160101000000_20170101000000> .\n" +
			"";
	
	static String expectedResponse3 =
			
			"<rdf:RDFxmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"xmlns:dbo=\"http://dbpedia.org/ontology/\"xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos/\"xmlns:itsrdf=\"http://www.w3.org/2005/11/its/rdf#\"xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"xmlns:dfkinif=\"http://dkt.dfki.de/ontologies/nif#\"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"xmlns:nif=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#\"><rdf:Descriptionrdf:about=\"http://dkt.dfki.de/documents/#char=0,26\"><dfkinif:averageLongituderdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">13.383333333333333</dfkinif:averageLongitude><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#Context\"/><nif:meanDateRangerdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">20160101010000_20170101010000</nif:meanDateRange><dfkinif:standardDeviationLongituderdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">0.0</dfkinif:standardDeviationLongitude><nif:endIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">26</nif:endIndex><nif:isStringrdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">WelcometoBerlinin2016.</nif:isString><dfkinif:standardDeviationLatituderdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">0.0</dfkinif:standardDeviationLatitude><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/><dfkinif:averageLatituderdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">52.516666666666666</dfkinif:averageLatitude><nif:beginIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">0</nif:beginIndex></rdf:Description><rdf:Descriptionrdf:about=\"http://dkt.dfki.de/documents/#char=21,25\"><itsrdf:taIdentRefrdf:resource=\"http://dkt.dfki.de/ontologies/nif#date=20160101000000_20170101000000\"/><nif:entityrdf:resource=\"http://dkt.dfki.de/ontologies/nif#date\"/><nif:endIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">25</nif:endIndex><nif:beginIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">21</nif:beginIndex><nif:anchorOfrdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">2016</nif:anchorOf><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/></rdf:Description><rdf:Descriptionrdf:about=\"http://dkt.dfki.de/documents/#char=11,17\"><nif:entityrdf:resource=\"http://dkt.dfki.de/ontologies/nif#location\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#RFC5147String\"/><geo:latrdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">52.516666666666666</geo:lat><nif:referenceContextrdf:resource=\"http://dkt.dfki.de/documents/#char=0,26\"/><rdf:typerdf:resource=\"http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#String\"/><itsrdf:taIdentRefrdf:resource=\"http://dbpedia.org/resource/Berlin\"/><nif:anchorOfrdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">Berlin</nif:anchorOf><nif:endIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">17</nif:endIndex><nif:beginIndexrdf:datatype=\"http://www.w3.org/2001/XMLSchema#nonNegativeInteger\">11</nif:beginIndex><geo:longrdf:datatype=\"http://www.w3.org/2001/XMLSchema#double\">13.383333333333333</geo:long></rdf:Description></rdf:RDF>";
			
			
	static String expectedResponse37 = 
			"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
					"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix dfkinif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=13,19>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Berlin\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"13\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"19\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,20> ;\n" +
					"        geo:lat               \"52.51861111111111\"^^xsd:double ;\n" +
					"        geo:long              \"13.408333333333333\"^^xsd:double ;\n" +
					"        itsrdf:taIdentRef     <http://de.dbpedia.org/resource/Berlin> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,20>\n" +
					"        a                         nif:String , nif:RFC5147String , nif:Context ;\n" +
					"        dfkinif:averageLatitude   \"52.51861111111111\"^^xsd:double ;\n" +
					"        dfkinif:averageLongitude  \"13.408333333333333\"^^xsd:double ;\n" +
					"        dfkinif:standardDeviationLatitude\n" +
					"                \"0.0\"^^xsd:double ;\n" +
					"        dfkinif:standardDeviationLongitude\n" +
					"                \"0.0\"^^xsd:double ;\n" +
					"        nif:beginIndex            \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex              \"20\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString              \"Wilkommen in Berlin.\"^^xsd:string .\n" +
					"";
	
	static String bodyInput5 = "1936\n\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipuzcoa from July to September. The capture of Guipuzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irun, closing the French border to the Republicans. On 13 September, the Basques surrendered Madrid to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.";
	
	static String expectedResponse5 = 
			"@prefix geo:   <http://www.w3.org/2003/01/geo/wgs84_pos/> .\n" +
					"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
					"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix dfkinif: <http://dkt.dfki.de/ontologies/nif#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=650,662>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"650\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"662\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:organization ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nationalists> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=18,26>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Sanjurjo\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"18\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:person ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Sanjurjo> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=277,282>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Spain\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"277\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"282\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        geo:lat               \"40.43333333333333\"^^xsd:double ;\n" +
					"        geo:long              \"-3.7\"^^xsd:double ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Spain> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=254,260>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Ferrol\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"254\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"260\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Ferrol> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=704,710>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Bilbao\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"704\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"710\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        geo:lat               \"43.25694444444444\"^^xsd:double ;\n" +
					"        geo:long              \"-2.923611111111111\"^^xsd:double ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Bilbao> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=201,213>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Nationalists\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"201\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"213\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:organization ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Nationalists> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,805>\n" +
					"        a                         nif:String , nif:Context , nif:RFC5147String ;\n" +
					"        dfkinif:averageLatitude   \"41.852856249999995\"^^xsd:double ;\n" +
					"        dfkinif:averageLongitude  \"-3.0322722222222223\"^^xsd:double ;\n" +
					"        dfkinif:standardDeviationLatitude\n" +
					"                \"1.4449139905737536\"^^xsd:double ;\n" +
					"        dfkinif:standardDeviationLongitude\n" +
					"                \"0.7861709280932566\"^^xsd:double ;\n" +
					"        nif:beginIndex            \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex              \"805\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString              \"1936\\n\\nCoup leader Sanjurjo was killed in a plane crash on 20 July, leaving an effective command split between Mola in the North and Franco in the South. On 21 July, the fifth day of the rebellion, the Nationalists captured the main Spanish naval base at Ferrol in northwestern Spain. A rebel force under Colonel Beorlegui Canet, sent by General Emilio Mola, undertook the Campaign of Guipuzcoa from July to September. The capture of Guipuzcoa isolated the Republican provinces in the north. On 5 September, after heavy fighting the force took Irun, closing the French border to the Republicans. On 13 September, the Basques surrendered Madrid to the Nationalists, who then advanced toward their capital, Bilbao. The Republican militias on the border of Viscaya halted these forces at the end of September.\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=543,547>\n" +
					"        a                     nif:String , nif:RFC5147String ;\n" +
					"        nif:anchorOf          \"Irun\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"543\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"547\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        geo:lat               \"43.33781388888889\"^^xsd:double ;\n" +
					"        geo:long              \"-1.788811111111111\"^^xsd:double ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Irun> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=372,393>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Campaign of Guipuzcoa\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"372\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"393\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:organization ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Campaign_of_Guipuzcoa> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=146,151>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"South\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"146\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"151\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/property/south> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=636,642>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf          \"Madrid\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"636\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"642\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:location ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        geo:lat               \"40.38333333333333\"^^xsd:double ;\n" +
					"        geo:long              \"-3.716666666666667\"^^xsd:double ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Madrid> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=345,356>\n" +
					"        a                     nif:RFC5147String , nif:String ;\n" +
					"        dbo:birthDate         \"1887-06-09\"^^xsd:date ;\n" +
					"        dbo:deathDate         \"1937-06-03\"^^xsd:date ;\n" +
					"        nif:anchorOf          \"Emilio Mola\"^^xsd:string ;\n" +
					"        nif:beginIndex        \"345\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex          \"356\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity            dfkinif:person ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,805> ;\n" +
					"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Emilio_Mola> .\n" +
					"";
	
	static String expectedResponse6 = 
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
					"        nif:entity            <http://dkt.dfki.de/ontologies/nif#PER> ;\n" +
					"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,36> ;\n" +
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
			"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
					"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
					"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
					"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
					"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=0,26>\n" +
					"        a                  nif:RFC5147String , nif:String , nif:Context ;\n" +
					"        nif:beginIndex     \"0\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex       \"26\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:isString       \"Welcome to Berlin in 2016.\"^^xsd:string ;\n" +
					"        nif:meanDateRange  \"20160101010000_20170101010000\"^^xsd:string .\n" +
					"\n" +
					"<http://dkt.dfki.de/documents/#char=21,25>\n" +
					"        a                  nif:RFC5147String , nif:String ;\n" +
					"        nif:anchorOf       \"2016\"^^xsd:string ;\n" +
					"        nif:beginIndex     \"21\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:endIndex       \"25\"^^xsd:nonNegativeInteger ;\n" +
					"        nif:entity         <http://dkt.dfki.de/ontologies/nif#date> ;\n" +
					"        itsrdf:taIdentRef  <http://dkt.dfki.de/ontologies/nif#date=20160101000000_20170101000000> .\n" +
					"";
	
	static String dictUploadData = 
			"Mahler	http://d-nb.info/gnd/11857633X\n" +
				"Herbert Eulenberg	http://d-nb.info/gnd/118682636\n" +
				"Leonardo da Vinci	http://d-nb.info/gnd/118640445\n" +
				"Treitschke	http://d-nb.info/gnd/118623761\n" +
				"Schmädel 	http://d-nb.info/gnd/117521221";

}
