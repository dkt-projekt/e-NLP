package de.dkt.eservices.ecorenlp.modules;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.SpanAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import edu.stanford.nlp.util.PropertiesUtils;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.BadRequestException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.DFKINIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.eopennlp.modules.NameFinder;


public class RelationExtractor {
	
	public static StanfordCoreNLP initPipeline(){
		//note that this does the full analysis, from (again) tokenizing, sentencesplitting, tagging, lemmatizing, parsing, etc. Can possibly be sped up by feeding it a(n already tokenize) list of tokens rather than a raw string. Also, doesn't ensure identical tokenization (e.g. the SimpleTokenizer from openNLP that is used everywhere else may tokenize things in a different way)
		Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie"); // possibly I can also use just parse rather than depparse. Seems to go faster, but check if it means lower quality output (which is then probably the case...)
		return new StanfordCoreNLP(props);
	}
	
	
	public static ArrayList<String> getTriplesForEntity(ArrayList<RelationTriple> relationTriples, String entity, String function){
		
		ArrayList<String> entityTriples = new ArrayList<String>();
		for (RelationTriple triple : relationTriples){
			if (function.equalsIgnoreCase("subject")){
				if (entity.equalsIgnoreCase(triple.subjectGloss())){ // TODO: debug exact difference between subjectGloss and subjectLemmaGloss
					entityTriples.add(triple.relationLemmaGloss() + " " + triple.objectLemmaGloss());
				}
			}
			else if (function.equalsIgnoreCase("object")){
				if (entity.equalsIgnoreCase(triple.objectGloss())){
					entityTriples.add(triple.subjectLemmaGloss() + " " + triple.relationLemmaGloss());
				}
			}
			else{
				throw new BadRequestException("Element '" + function + "' not defined in relation triples.\n");
			}
		}
		return entityTriples;
	}
	
	//public static ArrayList<RelationTriple> extractRelationTriples(StanfordCoreNLP pipeline, Model nifModel){
	public static ArrayList<ArrayList<String>> extractRelationTriples(StanfordCoreNLP pipeline, Model nifModel){
		
		String isstr = NIFReader.extractIsString(nifModel);
		//ArrayList<RelationTriple> tripleList = new ArrayList<RelationTriple>();
		ArrayList<ArrayList<String>> tripleList = new ArrayList<ArrayList<String>>();
		Annotation ann = new Annotation(isstr);
		pipeline.annotate(ann);
		for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			int sentenceStart = isstr.indexOf(sentence.toString());
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			for (RelationTriple triple : triples) {
				boolean subjectReplaced = false;
				boolean objectReplaced = false; // may want to use this as threshold too at some point
				ArrayList<String> candidateList = new ArrayList<String>();
				candidateList.add(triple.subjectGloss());
				candidateList.add(triple.relationGloss());
				candidateList.add(triple.objectGloss());
				int subjectStart = tokens.get(triple.subjectTokenSpan().first()).beginPosition(); // TODO: debug why this doesn't always get the correct index
				int subjectEnd = subjectStart + triple.subjectGloss().length();
				String subjectEntityURI = NIFReader.extractDocumentURI(nifModel) + "#char=" + subjectStart + "," + subjectEnd;
				String subjectTaIdentRef = NIFReader.extractTaIdentRefWithEntityURI(nifModel, subjectEntityURI);
				if (!(subjectTaIdentRef == null)){
					candidateList.set(0,  subjectTaIdentRef);
					subjectReplaced = true;
				}
				int objectStart = tokens.get(triple.objectTokenSpan().first()).beginPosition(); // TODO: debug why this doesn't always get the correct index
				int objectEnd = objectStart + triple.objectGloss().length();
				String objectEntityURI = NIFReader.extractDocumentURI(nifModel) + "#char=" + objectStart + "," + objectEnd;
				String objectTaIdentRef = NIFReader.extractTaIdentRefWithEntityURI(nifModel, objectEntityURI);
				if (!(objectTaIdentRef == null)){
					candidateList.set(2,  objectTaIdentRef);
					objectReplaced = true;
				}
				if (subjectReplaced){
					tripleList.add(candidateList);
				}
				//tripleList.add(triple);
			}
		}
		
		return tripleList;
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
			}
	
	public static void main(String[] args) throws Exception {

		
		/*
		String docFolder = "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\out";
		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\out";
		File df = new File(docFolder);
		ArrayList<RelationTriple> masterList = new ArrayList<RelationTriple>();
		StanfordCoreNLP pipeline = initPipeline();
		for (File f : df.listFiles()){
			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
			Model nifModel = NIFWriter.initializeOutputModel();
			NIFWriter.addInitialString(nifModel, fileContent, DFKINIF.getDefaultPrefix());
			ArrayList<RelationTriple> singleList = extractRelationTriples(pipeline, nifModel);
			for (RelationTriple rt : singleList){
				masterList.add(rt);
			}
			// loop through nifModel to extract entities and make frequency hash dict of entities
		}
		*/
		// then extract most frequent ones, and get relation triples for these
		// make frequency dict out of date to get to some sort of summarization based on the entities. That's the idea :)
		
		
		
		/*
		// Print SemanticGraph
		System.out.println("semGraph: " + sentence.get(SemanticGraphCoreAnnotations.CollapsedDependenciesAnnotation.class)
					.toString(SemanticGraph.OutputFormat.LIST));

		// Alternately, to only run e.g., the clause splitter:
		List<SentenceFragment> clauses = new OpenIE(props).clausesInSentence(sentence);
		for (SentenceFragment clause : clauses) {
			System.out.println("clause: " + clause.parseTree.toString(SemanticGraph.OutputFormat.LIST));
		}
		*/
		
		Date d1 = new Date();
		StanfordCoreNLP pipeline = initPipeline();
		Date d2 = new Date();
		long initTime = (d2.getTime()-d1.getTime())/1000;
		System.out.println("Done initializing. Took " + initTime + " seconds.\n");
		
		String nietzscheNIF = 
				"@prefix dbo:   <http://dbpedia.org/ontology/> .\n" +
						"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n" +
						"@prefix xsd:   <http://www.w3.org/2001/XMLSchema#> .\n" +
						"@prefix itsrdf: <http://www.w3.org/2005/11/its/rdf#> .\n" +
						"@prefix nif:   <http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#> .\n" +
						"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=362,381>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"University of Basel\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"362\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"381\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#organization> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/University> , <http://dbpedia.org/resource/Basel> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=754,763>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1844-10-15\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1900-08-25\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"754\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"763\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Friedrich_Nietzsche> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=0,2815>\n" +
						"        a               nif:RFC5147String , nif:String , nif:Context ;\n" +
						"        nif:beginIndex  \"0\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex    \"2815\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:isString    \"Friedrich Wilhelm Nietzsche was a German philosopher, cultural critic, poet, and Latin and Greek scholar whose work has exerted a profound influence on Western philosophy and modern intellectual history. Beginning his career as a classical philologist before turning to philosophy, he became the youngest-ever occupant of the Chair of Classical Philology at the University of Basel in 1869, at age 24. He resigned in 1879 due to health problems that plagued him most of his life, and he completed much of his core writing in the following decade. In 1889, at age 44, he suffered a collapse and a complete loss of his mental faculties. He lived his remaining years in the care of his mother (until her death in 1897) and then his sister Elisabeth Förster-Nietzsche, and died in 1900.\\r\\n\\r\\nNietzsche's body of writing spanned philosophical polemics, poetry, cultural criticism, and fiction, and drew widely on art, philology, history, religion, and science while displaying a fondness for aphorism, metaphor, and irony. His work engaged with a wide range of subjects including morality, aesthetics, tragedy, epistemology, atheism, and consciousness. Some prominent elements of his philosophy include his radical critique of reason and truth in favor of perspectivism; his notion of the Apollonian and Dionysian; his genealogical critique of religion and morality, and his related theory of master-slave morality; his aesthetic affirmation of existence in response to the death of God and the profound crisis of nihilism; and his characterization of the human subject as the expression of competing wills, collectively understood as the will to power. In his later work, he developed influential concepts such as the Uebermensch and the doctrine of eternal recurrence, and became increasingly preoccupied with the creative powers of the individual to overcome social, cultural, and moral contexts in pursuit of aesthetic health.\\r\\n\\r\\nAfter his death, Elizabeth Förster-Nietzsche became the curator and editor of her brother's manuscripts, reworking Nietzsche's unpublished writings to fit her own German nationalist ideology while often contradicting or obfuscating his stated opinions, which were explicitly opposed to antisemitism and nationalism. Through her published editions, Nietzsche's work became associated with fascism and Nazism; 20th-century scholars contested this interpretation of his work and corrected editions of his writings were soon made available. His thought enjoyed renewed popularity in the 1960s, and his ideas have since had a profound impact on twentieth and early-twenty-first century thinkers across philosophy—especially in schools of continental philosophy such as existentialism, postmodernism, and post-structuralism—as well as art, literature, psychology, politics, and popular culture.\"^^xsd:string .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=1944,1961>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1533-09-07\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1603-03-24\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Elizabeth Förster\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"1944\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"1961\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Elizabeth_I_of_England> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=736,753>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        nif:anchorOf          \"Elisabeth Förster\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"736\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"753\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Elisabeth_(musical)> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=0,27>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        dbo:birthDate         \"1844-10-15\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1900-08-25\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Friedrich Wilhelm Nietzsche\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"0\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"27\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Friedrich_Nietzsche> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=786,795>\n" +
						"        a                     nif:RFC5147String , nif:String ;\n" +
						"        dbo:birthDate         \"1844-10-15\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1900-08-25\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"786\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"795\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Friedrich_Nietzsche> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=1962,1971>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1844-10-15\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1900-08-25\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"1962\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"1971\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Friedrich_Nietzsche> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=2275,2284>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1844-10-15\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1900-08-25\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"2275\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"2284\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Friedrich_Nietzsche> .\n" +
						"\n" +
						"<http://dkt.dfki.de/documents/#char=2042,2051>\n" +
						"        a                     nif:String , nif:RFC5147String ;\n" +
						"        dbo:birthDate         \"1844-10-15\"^^xsd:date ;\n" +
						"        dbo:deathDate         \"1900-08-25\"^^xsd:date ;\n" +
						"        nif:anchorOf          \"Nietzsche\"^^xsd:string ;\n" +
						"        nif:beginIndex        \"2042\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:endIndex          \"2051\"^^xsd:nonNegativeInteger ;\n" +
						"        nif:entity            <http://dkt.dfki.de/ontologies/nif#person> ;\n" +
						"        nif:referenceContext  <http://dkt.dfki.de/documents/#char=0,2815> ;\n" +
						"        itsrdf:taIdentRef     <http://dbpedia.org/resource/Friedrich_Nietzsche> .\n" +
						"";
		
		//ArrayList<RelationTriple> relationTriples = extractRelationTriples(pipeline, NIFReader.extractModelFromFormatString(nietzscheNIF, RDFSerialization.TURTLE));
		ArrayList<ArrayList<String>> relationTriples = extractRelationTriples(pipeline, NIFReader.extractModelFromFormatString(nietzscheNIF, RDFSerialization.TURTLE));
		System.out.println("relationTruples:" + relationTriples);
		Date d3 = new Date();
		long passOne = (d3.getTime()-d2.getTime())/1000;
		System.out.println("Done getting relations. Took " + passOne+ " seconds.\n");
		
		/*
		ArrayList<String> entitySubjectTriples = getTriplesForEntity(relationTriples, "Nietzsche" , "subject");
		Date d4 = new Date();
		long passTwo = (d4.getTime()-d3.getTime())/1000;
		System.out.println("Done getting entity triples. Took " + passTwo+ " seconds.\n");
		System.out.println("OUTPUT:" + entitySubjectTriples);
		*/
		
		
		//TODO: get URI in triples, and only return those having a URI as subject, so we can put it in an ontology
		
	}

}
