package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import de.dkt.common.filemanagement.FileFactory;
import de.dkt.eservices.ecorenlp.modules.Tagger;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.nndep.DependencyParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.TypedDependency;
import eu.freme.common.exception.BadRequestException;

public class DepParserTree {

	

	/*[advmod(let-3, Just-1), 
	mark(let-3, to-2), 
	advcl(shipped-11, let-3), 
	nsubj(know-5, you-4), 
	ccomp(let-3, know-5), 
	punct(shipped-11, ,-6), 
	det(books-8, the-7), nsubjpass(shipped-11, books-8), aux(shipped-11, will-9), auxpass(shipped-11, be-10), 
	root(ROOT-0, shipped-11), case(you-14, to-12), cc:preconj(you-14, both-13), nmod(shipped-11, you-14), 
	cc(you-14, and-15), conj(you-14, Rice-16), punct(shipped-11, .-17)]*/

	static DependencyParser parser;
	static String currentLanguage = "none";

	public static void initParser(String language){
		if(currentLanguage.equalsIgnoreCase(language) && parser!=null){
			return;
		}
		String parserModel = null;
		if (language.equalsIgnoreCase("en")){
			parserModel = "edu/stanford/nlp/models/parser/nndep/english_UD.gz";  
		}
		else if (language.equalsIgnoreCase("de")){
			parserModel = "edu/stanford/nlp/models/parser/nndep/UD_German.gz";
		}
		else {
		}
		parser = DependencyParser.loadFromModelFile(parserModel);
		currentLanguage = language;
	}



	public static MaxentTagger tagger;
	static Logger logger = Logger.getLogger(Tagger.class);

	public static void initTagger(String language){

		String taggersDirectory = "taggers" + File.separator;
		try {
			File taggerFolder = FileFactory.generateOrCreateDirectoryInstance(taggersDirectory);
			if (language.equalsIgnoreCase("en")) {
				logger.info("Loading model: " + taggerFolder + File.separator
						+ "english-left3words-distsim.tagger");
				tagger = new MaxentTagger(taggerFolder + File.separator + "english-left3words-distsim.tagger");

			} else if (language.equalsIgnoreCase("de")) {
				// tagger = new MaxentTagger(taggersDirectory +
				// "german-hgc.tagger");
				logger.info("Loading model: " + taggerFolder + File.separator
						+ "german-fast.tagger");
				tagger = new MaxentTagger(taggerFolder + File.separator + "german-fast.tagger");

			} else {
				throw new BadRequestException("Unsupported language: " + language);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static void main(String[] args) {

		Tagger.initTagger("en");
		DepParserTree.initParser("en");

		DocumentPreprocessor tokenizer = new DocumentPreprocessor(new StringReader("Just to let you know, the books will be shipped to both you and Rice."));

		for (List<HasWord> sentence : tokenizer) {
			List<TaggedWord> tagged = Tagger.tagger.tagSentence(sentence);
			GrammaticalStructure gs = parser.predict(tagged);

			Collection<TypedDependency> list = gs.allTypedDependencies();
			System.out.println(list.size());
			DPTreeNode tree =  DepParserTree.generateTreeFromList(list);
			tree.printByLevel("   ", "   ");
			List<DPTreeNode> list2 = new LinkedList<DPTreeNode>();
			DPTreeNode result = tree.getShortestPath("you", "Just", list2);
			System.out.println(result.value);
		}


	}

	public static DPTreeNode generateTreeFromList(Collection<TypedDependency> list2){
		DPTreeNode tree = new DPTreeNode(null, "root", "ROOT");
		List<DPTreeNode> nodes = new LinkedList<DPTreeNode>();
		nodes.add(tree);
		List<TypedDependency> list = (List<TypedDependency>) list2;
		for(int i=0;!list.isEmpty();i++){
			TypedDependency td = list.get(i);
			
//			System.out.println(td.reln().toString());
			for (DPTreeNode dptn: nodes) {
//				System.out.println("\t" + "Value: "+dptn.value);
//				System.out.println("\t" + "DEP: "+td.gov().word());
				if(dptn.value != null && dptn.value.equalsIgnoreCase("ROOT") && td.gov().word()==null){
					DPTreeNode node = new DPTreeNode(dptn,td.reln().toString(),td.dep().word(),td.dep());
					dptn.childs.add(node);
//					System.out.println("Removing: "+td);
					list.remove(td);
					nodes.add(node);
					i--;
					break;
				}
				else if(dptn.value != null && dptn.value.equalsIgnoreCase(td.gov().word())){
					DPTreeNode node = new DPTreeNode(dptn,td.reln().toString(),td.dep().word(),td.dep());
					dptn.childs.add(node);
//					System.out.println("Removing2: "+td);
					list.remove(td);
					nodes.add(node);
					i--;
					break;
				}
			}
			
			
			if(i==list.size()-1){
				i=-1;
			}
		}

		return tree;
	}
	
	
	public static DPTreeNode generateTreeFromList_old(Collection<TypedDependency> list2){
		DPTreeNode tree = new DPTreeNode(null, "root", "ROOT");
		List<DPTreeNode> nodes = new LinkedList<DPTreeNode>();
		nodes.add(tree);
		List<TypedDependency> list = (List<TypedDependency>) list2;
		for(int i=0;!list.isEmpty();i++){
			TypedDependency td = list.get(i);
			
//			System.out.println(td.reln().toString());
			for (DPTreeNode dptn: nodes) {
//				System.out.println("\t" + "Value: "+dptn.value);
//				System.out.println("\t" + "DEP: "+td.gov().word());
				if(dptn.value.equalsIgnoreCase("ROOT") && td.gov().word()==null){
					DPTreeNode node = new DPTreeNode(dptn,td.reln().toString(),td.dep().word());
					dptn.childs.add(node);
//					System.out.println("Removing: "+td);
					list.remove(td);
					nodes.add(node);
					i--;
					break;
				}
				else if(dptn.value.equalsIgnoreCase(td.gov().word())){
					DPTreeNode node = new DPTreeNode(dptn,td.reln().toString(),td.dep().word());
					dptn.childs.add(node);
//					System.out.println("Removing2: "+td);
					list.remove(td);
					nodes.add(node);
					i--;
					break;
				}
			}
			
			
			if(i==list.size()-1){
				i=-1;
			}
		}
//		while(!list.isEmpty()){
//			Iterator<TypedDependency> iter = list.iterator();
//			while(iter.hasNext()) {
//				TypedDependency td = iter.next();
//				System.out.println(td);
//				for (DPTreeNode dptn: nodes) {
//					System.out.println("\t" + "Value: "+dptn.value);
//					System.out.println("\t" + "DEP: "+td.gov().word());
//					if(dptn.value.equalsIgnoreCase("ROOT") && td.gov().word()==null){
//						DPTreeNode node = new DPTreeNode(dptn,td.reln().toPrettyString(),td.gov().word());
//						dptn.childs.add(node);
//						System.out.println("Removing: "+td);
//						list.remove(td);
//						nodes.add(node);
//						break;
//					}
//					else if(dptn.value.equalsIgnoreCase(td.gov().word())){
//						DPTreeNode node = new DPTreeNode(dptn,td.reln().toPrettyString(),td.gov().word());
//						dptn.childs.add(node);
//						System.out.println("Removing: "+td);
//						list.remove(td);
//						nodes.add(node);
//						break;
//					}
//				}
//			}
//		}
		//System.out.println(nodes.size());
		return tree;
	}
}
