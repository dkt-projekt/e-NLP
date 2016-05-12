package de.dkt.eservices.ecorenlp.modules;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
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

import de.dkt.common.niftools.DKTNIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.erattlesnakenlp.linguistic.EntityRelationTriple;


public class RelationExtractor {
	
	static StanfordCoreNLP pipeline;
	
	public static void initPipeline(){
		//note that this does the full analysis, from (again) tokenizing, sentencesplitting, tagging, lemmatizing, parsing, etc. Can possibly be sped up by feeding it a(n already tokenize) list of tokens rather than a raw string. Also, doesn't ensure identical tokenization (e.g. the SimpleTokenizer from openNLP that is used everywhere else may tokenize things in a different way)
		Properties props = PropertiesUtils.asProperties("annotators", "tokenize,ssplit,pos,lemma,depparse,natlog,openie"); // possibly I can also use just parse rather than depparse. Seems to go faster, but check if it means lower quality output (which is then probably the case...)
		pipeline = new StanfordCoreNLP(props);
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
	
	public static ArrayList<EntityRelationTriple> extractRelationTriples(Model nifModel){
		
		String isstr = NIFReader.extractIsString(nifModel);
		ArrayList<EntityRelationTriple> tripleList = new ArrayList<EntityRelationTriple>();
		Annotation ann = new Annotation(isstr);
		pipeline.annotate(ann);
		for (CoreMap sentence : ann.get(CoreAnnotations.SentencesAnnotation.class)) {
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			int sentenceStart = isstr.indexOf(sentence.toString());
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			for (RelationTriple triple : triples) {
				boolean subjectReplaced = false;
				boolean objectReplaced = false; // may want to use this as threshold too at some point
				EntityRelationTriple ert = new EntityRelationTriple();
				ert.setSubject(triple.subjectGloss());
				ert.setRelation(triple.relationLemmaGloss());
				ert.setObject(triple.objectGloss());
				int subjectStart = tokens.get(triple.subjectTokenSpan().first()).beginPosition(); // TODO: debug why this doesn't always get the correct index
				int subjectEnd = subjectStart + triple.subjectGloss().length();
				String subjectEntityURI = NIFReader.extractDocumentURI(nifModel) + "#char=" + subjectStart + "," + subjectEnd;
				String subjectTaIdentRef = NIFReader.extractTaIdentRefWithEntityURI(nifModel, subjectEntityURI);
				if (!(subjectTaIdentRef == null)){
					ert.setSubject(subjectTaIdentRef);
					subjectReplaced = true;
				}
				int objectStart = tokens.get(triple.objectTokenSpan().first()).beginPosition(); // TODO: debug why this doesn't always get the correct index
				int objectEnd = objectStart + triple.objectGloss().length();
				String objectEntityURI = NIFReader.extractDocumentURI(nifModel) + "#char=" + objectStart + "," + objectEnd;
				String objectTaIdentRef = NIFReader.extractTaIdentRefWithEntityURI(nifModel, objectEntityURI);
				if (!(objectTaIdentRef == null)){
					ert.setObject(objectTaIdentRef);
					objectReplaced = true;
				}
				if (subjectReplaced || objectReplaced){
					tripleList.add(ert);
					
				}

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

		
		
		Date d1 = new Date();
		
		String docFolder = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\out\\out\\english";
		
		//String outputFolder = "C:\\Users\\pebo01\\Desktop\\mendelsohnDocs\\out";
		File df = new File(docFolder);
		ArrayList<EntityRelationTriple> masterList = new ArrayList<EntityRelationTriple>();
		initPipeline();
		Date d3 = new Date();
		int c = 0;
		for (File f : df.listFiles()){
			c += 1;
			String fileContent = readFile(f.getAbsolutePath(), StandardCharsets.UTF_8);
			Model nifModel = NIFWriter.initializeOutputModel();
			NIFWriter.addInitialString(nifModel, fileContent, DKTNIF.getDefaultPrefix());
			ArrayList<EntityRelationTriple> singleList = extractRelationTriples(nifModel);
			for (EntityRelationTriple rt : singleList){
				masterList.add(rt);
			}
			// loop through nifModel to extract entities and make frequency hash dict of entities
		}
		
		// then extract most frequent ones, and get relation triples for these
		// make frequency dict out of date to get to some sort of summarization based on the entities. That's the idea :)
		
		for (EntityRelationTriple ert : masterList){
			System.out.println("Relation:" + ert.getSubject() + "," + ert.getRelation() + "," + ert.getObject());
		}

		Date d2 = new Date();
		long i = (d2.getTime()-d1.getTime())/1000;
		System.out.println("Initialization took " + (d3.getTime()-d1.getTime())/1000 + " seconds.\n");
		System.out.println("Processed " + c + " documents in " + i + " seconds.\n");
		
		
		
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
		
		/*
		Date d1 = new Date();
		RelationExtractor.initPipeline();
		Date d2 = new Date();
		long initTime = (d2.getTime()-d1.getTime())/1000;
		System.out.println("Done initializing. Took " + initTime + " seconds.\n");
		*/
		
		
	}

}
