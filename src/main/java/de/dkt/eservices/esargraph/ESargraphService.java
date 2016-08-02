package de.dkt.eservices.esargraph;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import de.dfki.lt.dare.element.DefaultRelationEdge;
import de.dfki.lt.dare.element.io.XmlIOUtils;
import de.dfki.lt.dare.pattern.RePattern;
import de.dfki.lt.dare.pattern.RePatternVertex;
import de.dfki.lt.dare.sargraph2.element.xml.XmlSerializableSarGraph;
import eu.freme.common.exception.BadRequestException;
import eu.freme.common.exception.ExternalServiceFailedException;

@Component
public class ESargraphService {
    
	public static void main(String[] args) {
		ESargraphService ess = new ESargraphService();
		ess.processData("", "", "");
	}
	
    public String processData(String data, String languageParam, String index) throws ExternalServiceFailedException, BadRequestException {
        try {
            String nif = "We will return the document: " + data + " in the language: " + languageParam;
            
            //the xml file path in file system or in class path
            String patternFile = "patterns/marriage.xml";
            List<RePattern> patterns = XmlIOUtils.readList(RePattern.class, patternFile);
            
            for (RePattern rePattern : patterns) {
//            	System.out.println("---------------------------");
//				System.out.println("\t"+rePattern.graphToString());
				for (RePatternVertex v : rePattern.vertexSet()) {
				    //vertex unique id
				    short vid = v.getVertexId();
				    //the lemma
				    String lemma = v.getLemma();
				    //the POS
				    String posTag = v.getPosTag();

				    System.out.println(vid + " --> " + lemma + " --> " + posTag);

				    if (rePattern.getRole(v) != null) {
				      //the argument in the pattern 
				      String patternArgument = rePattern.getRole(v);
					    System.out.println(" \t\t\t" + patternArgument);
				    }
				    
				    Set<DefaultRelationEdge> outgoingEdges = rePattern.outgoingEdgesOf(v);
				    Set<DefaultRelationEdge> incomingEdges = rePattern.incomingEdgesOf(v);
				}
			}

//            String sargraphFile = "sargraph/acquisition.xml";
//            XmlSerializableSarGraph g = XmlIOUtils.read(XmlSerializableSarGraph.class, sargraphFile);
//
//            System.out.println(g);
            
            

            return nif;
        } catch (Exception e) {
            throw new ExternalServiceFailedException(e.getMessage());
        }
    }

    public String storeData(String data, String languageParam, String index) throws ExternalServiceFailedException, BadRequestException {
        try {
            String nif = "We will return the document: " + data + " in the language: " + languageParam;
            
            //the xml file path in file system or in class path
            String patternFile = "patterns/acquisition.xml";
            List<RePattern> patterns = XmlIOUtils.readList(RePattern.class, patternFile);
            
            XmlIOUtils.write(RePattern.class, patterns,new File("output-patterns.xml"));
            
            String sargraphFile = "sargraph/acquisition.xml";
            XmlSerializableSarGraph g = XmlIOUtils.read(XmlSerializableSarGraph.class, sargraphFile);
            
            XmlIOUtils.write(g, new File("output-sargraph.xml"));

            
            //TODO How to store new relation
            
            return nif;
        } catch (Exception e) {
            throw new ExternalServiceFailedException(e.getMessage());
        }
    }
}
