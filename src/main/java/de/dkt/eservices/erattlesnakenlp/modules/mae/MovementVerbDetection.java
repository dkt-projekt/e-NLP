package de.dkt.eservices.erattlesnakenlp.modules.mae;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;

import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.eopennlp.EOpenNLPService;
import de.dkt.eservices.eopennlp.modules.SentenceDetector;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import eu.freme.common.conversion.rdf.RDFConstants;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import opennlp.tools.util.Span;

@Component
public class MovementVerbDetection {

	final String inputDir = "/media/jan/d018c9bd-9b5f-4e1a-90ab-9ec557ed8c21/research/datasets/mendelsohn/mendelsohn-languages-cleaned/en";
	final String rawTriggers = "abandon,accompany,advance,amble,arrive,ascend,backpack,balloon,bicycle,bike,boat,bobsled,bus,cab,canoe,caravan,chariot,chase,climb,coach,come,conduct,crawl,cross,cruise,cycle,depart,descend,desert,dogsled,drift,drive,enter,escape,escort,exit,ferry,flee,float,fly,follow,gallop,glide,gondola,goosestep,guide,hasten,helicopter,hike,hurry,jeep,jet,journey,jump,kayak,lead,leap,leave,march,meander,moped,motor,motorbike,motorcycle,move,oar,paddle,parachute,parade,pedal,perambulate,prance,promenade,prowl,punt,pursue,race,raft,ramble,recede,return,rickshaw,ride,roam,rocket,row,run,rush,sail,shadow,shepherd,skate,skateboard,ski,skip,sled,sledge,sleigh,slide,speed,stagger,stray,stride,stroll,stumble,swagger,swim,tack,tail,taxi,tear,toboggan,track,trail,tram,travel,trek,trolley,walk,wander,wind,yacht";
	Set<String> triggers;

	protected StanfordCoreNLP pipeline;

	@PostConstruct
	public void initializeModels(){
		Properties props;
		props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);

		triggers = new HashSet<String>();
		for (String t : rawTriggers.split(",")) {
			triggers.add(t);
		}
	}

	public Model detectMovementVerbs(Model nifModel, String languageParam, RDFConstants.RDFSerialization inFormat) throws IOException{
		try{
			String documentURI = NIFReader.extractDocumentWholeURI(nifModel);
			String inputText = NIFReader.extractIsString(nifModel).toLowerCase();
			List<Trigger> list = new ArrayList<Trigger>();
			Annotation document = new Annotation(inputText);
			pipeline.annotate(document);
			List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
			for (CoreMap sentence : sentences) {
				Trigger trigger = new Trigger();
				for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
					// Retrieve and add the lemma for each word into the list of lemmas
					String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
					if (triggers.contains(lemma)) {
						trigger.getTriggerWords().add(lemma);
						NIFWriter.addMAEMovementVerb(nifModel, documentURI, lemma, token.beginPosition(), token.endPosition());
					}
				}
			}
			return nifModel;
		}
		catch(Exception e){
			e.printStackTrace();
			return nifModel;
		}
	}

	public static void main(String[] args) throws Exception {
		MovementVerbDetection mvd = new MovementVerbDetection();
		mvd.initializeModels();
		Model model = NIFWriter.initializeOutputModel();
		NIFWriter.addInitialString(model, "I want to travel to Chicago using backpack,balloon or bicycle and then take a bike and a boat there.", "http://algo.algo.algo");
		model = mvd.detectMovementVerbs(model, "en", RDFSerialization.TURTLE);
		System.out.println(NIFReader.model2String(model, RDFSerialization.TURTLE));
	}
}
