package de.dkt.eservices.ecorenlp.modules;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer {

    protected StanfordCoreNLP pipeline;
    
    public Lemmatizer() {
        // Create StanfordCoreNLP object properties, with POS tagging
        // (required for lemmatization), and lemmatization
        Properties props;
        props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        
        /*
         * TODO: find out how to load models other than english; provide the thing with a type/lang/model (which should be loaded/included through maven/pom.xml then)
         * TODO: create lemmatization-only function that does not take a whole document (as a string) as input, but just a single word
         * 
         * See: http://nlp.stanford.edu/software/corenlp.shtml
         */
        this.pipeline = new StanfordCoreNLP(props);
    }
    
    public List<String> lemmatize(String documentText, String languageParam)
    {
    	List<String> lemmas = new LinkedList<String>();
        Annotation document = new Annotation(documentText);

        this.pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
                lemmas.add(token.get(LemmaAnnotation.class));
            }
        }
        // convert to string
        return lemmas;//.toString();
    }
    
    /*
    public static String lemmatizeOpenNLPAnnotatedFile(String inputfile, String languageParam){
    	
    	String outputfile = null;
    	
    	BufferedReader br;
    	String outFile = "C:\\Users\\pebo01\\Desktop\\ubuntuShare\\debug.txt";
    	BufferedWriter bw;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputfile), "utf-8"));
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"utf-8"));			
			String line = br.readLine();
			Lemmatizer slem = new Lemmatizer();

			while(line!=null){
				if(!line.trim().equalsIgnoreCase("")){
					List<String> lemmatized = slem.lemmatize(line, "en");
					int index = 0;
					for (String item : lemmatized){
						if(item.startsWith("<start")){
							String type = item.substring(7, 10);
							item = "<START:" + type.toUpperCase() + item.substring(10, item.length());
							lemmatized.set(index, item);
						}
						else if (item.equals("<end>")){
							item = "<END>";
							lemmatized.set(index, item);
						}
						index += 1;
						
					}
					String outputString = "";
					String joined = String.join(" ", lemmatized);
					bw.write(joined + "\n");
				}
				line = br.readLine();
			}
			br.close();
	    	bw.close();
	    	
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
    	return outputfile;
    	
    }
     */

    

    public static void main(String[] args) {
    	
    	
    }

}