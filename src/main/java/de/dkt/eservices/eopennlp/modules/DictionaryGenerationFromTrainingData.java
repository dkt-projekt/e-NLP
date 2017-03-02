package de.dkt.eservices.eopennlp.modules;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.util.StringList;

public class DictionaryGenerationFromTrainingData {

	public boolean generateDictionaryFromTrainingData(String dictionaryName, String dictionaryDirectory, String trainingDataFile){
		try{

			List<String> entities = new LinkedList<String>();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(trainingDataFile), "utf-8"));
			String line = br.readLine();
			while(line!=null){
				if(!line.trim().equalsIgnoreCase("")){
//					System.out.println(line);
					int pos = line.indexOf("<START");
					while(pos!=-1){
						
						String entity ;
						try{
							entity = line.substring(line.indexOf(">", pos)+1, line.indexOf("<END", pos)).trim();
						}
						catch(IndexOutOfBoundsException e){
							entity = line.substring(line.indexOf(">", pos)+1).trim();
						}
						//System.out.println(entity);
						if(!entities.contains(entity)){
							entities.add(entity);
						}
						pos = line.indexOf("<START",pos+1);
					}
				}
				line = br.readLine();
			}
			br.close();
			
			System.out.println(entities.size());
			
			Dictionary dic = new Dictionary();
			for (String string : entities) {
				StringList sl = new StringList(string);
				dic.put(sl);
			}

			File f = new File(dictionaryName);
//			if(!f.exists()){
////				System.out.println(f.getPath());
////				System.out.println(f.getName());
////				File f2 = new File(f.getPath(), f.getName());
//				f.createNewFile();
//			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"utf-8"));
			for (String s : entities) {
				bw.write(s + "\n");
			}
			bw.close();
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void main(String[] args) {
		DictionaryGenerationFromTrainingData d = new DictionaryGenerationFromTrainingData();
//		d.generateDictionaryFromTrainingData("", "", "C:\\Users\\jmschnei\\Desktop\\dkt-test\\NER-de-train.LOC.opennlp_small.out");

		d.generateDictionaryFromTrainingData("C:\\Users\\pebo01\\Desktop\\dictionaries\\wikine-de-LOC.dict", 
				"", 
				"C:\\Users\\pebo01\\Desktop\\dictionaries\\aij-wikiner-de.LOC.opennlp.out");
/*		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-de-ORG.dict", 

		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-de-LOC.dict", 
				"", 
				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\aij-wikiner-de.LOC.opennlp.out");
		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-de-ORG.dict", 
				"", 
				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\aij-wikiner-de.ORG.opennlp.out");
		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-de-PER.dict", 
				"", 
				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\aij-wikiner-de.PER.opennlp.out");
		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-en-LOC.dict", 
				"", 
				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\aij-wikiner-en.LOC.opennlp.out");
		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-en-ORG.dict", 
				"", 
				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\aij-wikiner-en.ORG.opennlp.out");
		d.generateDictionaryFromTrainingData("C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\dictionaries\\wikiner-en-PER.dict", 
				"", 
				"C:\\Users\\jmschnei\\Desktop\\dkt-test\\wikiner\\aij-wikiner-en.PER.opennlp.out");
	*/
		//some changes here for testing e-Gitggggeeeeeeeeeeeeeeeeeegreaaaaaaaaaaaat
	}
	
}
