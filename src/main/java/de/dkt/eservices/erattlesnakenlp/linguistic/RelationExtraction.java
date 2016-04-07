package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dkt.common.niftools.NIF;
import de.dkt.common.niftools.NIFReader;
import de.dkt.common.niftools.NIFWriter;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import eu.freme.common.conversion.rdf.RDFConstants.RDFSerialization;
import eu.freme.common.exception.ExternalServiceFailedException;

/**
 * @author Julian Moreno Schneider julian.moreno_schneider@dfki.de, Peter Bourgonje peter.bourgonje@dfki.de
 *
 */
public class RelationExtraction {

	public static Model extractRelationsNIFString(String nif) throws Exception {
		Model nifModel = NIFReader.extractModelFromFormatString(nif, RDFSerialization.TURTLE);
		return extractRelationsNIF(nifModel);
	}

	public static Model extractRelationsNIF(Model nifModel) throws Exception {

		// Extract all the entities in the NIF and put them into a structure.
		List<Entity> entities = extractEntitiesList(nifModel);
		
		// Detect sentences or paragraphs.
		//Span[] sentenceSpans = SentenceDetector.detectSentenceSpans(content, sentModel);
//		List<SpanText> texts = new LinkedList<SpanText>();
//		texts.add(new SpanText(text));
		String text = NIFReader.extractIsString(nifModel);
		List<LinguisticUnit> units = TextSplitter.splitText(text,0);
		List<SpanText> texts = new LinkedList<SpanText>();
		for (LinguisticUnit lu : units) {
			texts.add((SpanText) lu);
		}

		String nifString = NIFReader.model2String(nifModel, "TTL");
		Model m2 = NIFReader.extractModelFromFormatString(nifString, RDFSerialization.TURTLE);
		
		MaxentTagger tagger = Tagger.initTagger("en");
		Tagger.tagNIF(tagger, m2, "turtle", "de-sent.bin");

//		System.out.println(NIFReader.model2String(m2, "TTL"));
		List<SpanWord> verbs = extractSpanVerbsFromNIF(m2);
		//Detect relations between entities inside the same: sentence, paragraph, window.
		List<SpanRelation> relations = new LinkedList<SpanRelation>();
		/**
		 * For every text, look for entities that are inside the text, and then establish a relation between them.
		 */
		for (SpanText st : texts) {
			for (int i = 0; i < entities.size(); i++) {
				Entity e1 = entities.get(i);
				if(e1.startSpan>=st.startSpan && e1.endSpan<=st.endSpan){
					for (int j = 0; j < entities.size(); j++) {
						if(i!=j){
							Entity e2 = entities.get(j);
							if(e2.startSpan>=st.startSpan && e2.endSpan<=st.endSpan){
								
								for (SpanWord sp : verbs) {
									if(sp.startSpan>=st.startSpan && sp.endSpan<=st.endSpan){
										System.out.println(e1.text+" --> "+sp.text+" --> "+e2.text);
									}
								}
								relations.add(new SpanRelation(e1,e2,null));
							}
						}
					}					
				}
			}
		}
		/**
		 * Cleaning for not having duplicates
		 */
		for (int i = 0; i < relations.size(); i++) {
			SpanRelation sr = relations.get(i);
			for (int j = i+1; j < relations.size(); j++) {
				SpanRelation sr2 = relations.get(j);
				if(sr.startSpan==sr2.startSpan && sr.endSpan==sr2.endSpan){
					
					if( (sr.subject.equals(sr2.subject) && sr.object.equals(sr2.object)) ||
							(sr.subject.equals(sr2.object) && sr.object.equals(sr2.subject))
							){
						relations.remove(j);
						j--;
					}
					
				}
			}
		}
		for (SpanRelation r : relations) {
			NIFWriter.addAnnotationRelation(nifModel, r.startSpan, r.endSpan, "", r.getSubject(), r.getAction(), r.getObject());			
		}
		return nifModel;
	}

	private static List<SpanWord> extractSpanVerbsFromNIF(Model nifModel) {
		List<SpanWord> verbs = new LinkedList<SpanWord>();
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.posTag);
//		System.out.println("DEBUG: SIZE OF VERBS: "+iterEntities.hasNext());
        while (iterEntities.hasNext()) {
            Resource r = iterEntities.nextResource();
            String entityURI = r.getURI();
            String posTag = "";
            int startSpan = 0;
            int endSpan = 0;
            StmtIterator iter2 = r.listProperties();
//    		System.out.println("DEBUG: SIZE OF PROPERTIES OF "+entityURI+": "+iterEntities.hasNext());
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI();
				if(predicate.equalsIgnoreCase(NIF.beginIndex.getURI())){
					String object = st2.getObject().asLiteral().getString();
					startSpan = Integer.parseInt(object);
				}
				else if(predicate.equalsIgnoreCase(NIF.endIndex.getURI())){
					String object = st2.getObject().asLiteral().getString();
					endSpan = Integer.parseInt(object);
				}
				else if(predicate.equalsIgnoreCase(NIF.posTag.getURI())){
					posTag = st2.getObject().asLiteral().getString();
				}
				else{
				}
			}
            if(posTag.startsWith("V")){
            	SpanWord sw = new SpanWord(entityURI, startSpan, endSpan);
                verbs.add(sw);
				System.out.println(posTag);
            }
        }
        if(verbs.isEmpty()){
        	return null;
        }
        return verbs;
	}

	public static List<Entity> extractEntitiesList(Model nifModel){
		List<Entity> list = new LinkedList<Entity>();
				
		ResIterator iterEntities = nifModel.listSubjectsWithProperty(NIF.entity);
        while (iterEntities.hasNext()) {
    		Map<String,String> map = new HashMap<String,String>();
            Resource r = iterEntities.nextResource();

            String entityURI = r.getURI();
            int startSpan = 0;
            int endSpan = 0;
            StmtIterator iter2 = r.listProperties();
            while (iter2.hasNext()) {
				Statement st2 = iter2.next();
				String predicate =st2.getPredicate().getURI();
				if(predicate.equalsIgnoreCase(NIF.beginIndex.getURI())){
					String object = st2.getObject().asLiteral().getString();
					startSpan = Integer.parseInt(object);
				}
				else if(predicate.equalsIgnoreCase(NIF.endIndex.getURI())){
					String object = st2.getObject().asLiteral().getString();
					endSpan = Integer.parseInt(object);
				}
				else{
					String object = null;
					if(st2.getObject().isResource()){
						object = st2.getObject().asResource().getURI();
					}
					else{
						object = st2.getObject().asLiteral().getString();
					}
					map.put(predicate,object);
				}
			}
           	Entity e = new Entity(entityURI, startSpan, endSpan, map);
            list.add(e);
        }
        if(list.isEmpty()){
        	return null;
        }
		return list;
	}

	public static void main(String[] args) throws Exception {
		String s = 
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
						"        dfkinif:averageLatitude   \"41.85285625\"^^xsd:double ;\n" +
						"        dfkinif:averageLongitude  \"-3.0322722222222223\"^^xsd:double ;\n" +
						"        dfkinif:standardDeviationLatitude\n" +
						"                \"1.4449139905737536\"^^xsd:double ;\n" +
						"        dfkinif:standardDeviationLongitude\n" +
						"                \"0.7861709280932567\"^^xsd:double ;\n" +
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
		Model m = RelationExtraction.extractRelationsNIFString(s);
//		System.out.println(NIFReader.model2String(m, "TTL"));
//		System.out.println(s);
	}
}
