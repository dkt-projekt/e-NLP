package de.dkt.eservices.erattlesnakenlp.modules.mae;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Created by jan on 03.05.17.
 */
public class TriggerDetector {

    final String inputDir = "/media/jan/d018c9bd-9b5f-4e1a-90ab-9ec557ed8c21/research/datasets/mendelsohn/mendelsohn-languages-cleaned/en";
    final String rawTriggers = "abandon,accompany,advance,amble,arrive,ascend,backpack,balloon,bicycle,bike,boat,bobsled,bus,cab,canoe,caravan,chariot,chase,climb,coach,come,conduct,crawl,cross,cruise,cycle,depart,descend,desert,dogsled,drift,drive,enter,escape,escort,exit,ferry,flee,float,fly,follow,gallop,glide,gondola,goosestep,guide,hasten,helicopter,hike,hurry,jeep,jet,journey,jump,kayak,lead,leap,leave,march,meander,moped,motor,motorbike,motorcycle,move,oar,paddle,parachute,parade,pedal,perambulate,prance,promenade,prowl,punt,pursue,race,raft,ramble,recede,return,rickshaw,ride,roam,rocket,row,run,rush,sail,shadow,shepherd,skate,skateboard,ski,skip,sled,sledge,sleigh,slide,speed,stagger,stray,stride,stroll,stumble,swagger,swim,tack,tail,taxi,tear,toboggan,track,trail,tram,travel,trek,trolley,walk,wander,wind,yacht";
    Set<String> triggers;

    protected StanfordCoreNLP pipeline;


    public TriggerDetector() {
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        this.pipeline = new StanfordCoreNLP(props);

        triggers = new HashSet<String>();
        for (String t : rawTriggers.split(",")) {
            triggers.add(t);
        }
    }

    public List<Trigger> detect() throws IOException {

        List<Trigger> list = new ArrayList<Trigger>();
        for (File file : new File(inputDir).listFiles()) {
            String text = FileUtils.readFileToString(file, "iso-8859-1");
            Annotation document = new Annotation(text);
            pipeline.annotate(document);
            List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);

            for (CoreMap sentence : sentences) {
                Trigger trigger = new Trigger();
                for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    // Retrieve and add the lemma for each word into the list of lemmas
                    String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                    if (triggers.contains(lemma)) {
                        trigger.getTriggerWords().add(lemma);
                    }
                }

                if (trigger.getTriggerWords().size()>0) {
                    trigger.setSentence(sentence.toString().trim());
                    trigger.setDoc(file.getName());
                    list.add(trigger);
                }
            }
        }

        return list;
    }

    public void write(List<Trigger> triggers) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(triggers);
        System.out.println(str);
    }


    public static void main(String[] args) throws IOException {
        TriggerDetector td = new TriggerDetector();
        List<Trigger> triggers = td.detect();
        td.write(triggers);
    }
}
