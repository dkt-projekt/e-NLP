package de.dkt.eservices.erattlesnakenlp.linguistic;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetCleaner {

	// the regexes for cleaning below are ported from a ruby script: http://nlp.stanford.edu/projects/glove/preprocess-twitter.rb

	public static String spotAllCaps(String input){
		
		Pattern pat = Pattern.compile("(\\b[A-Z]{2,}\\b)");
		Matcher m = pat.matcher(input);
		while (m.find()){
			input = m.replaceAll("$1 <allCaps>");
		}
		
		return input;
	}
	
	public static String splitHashTags(String input){
		
		Pattern p = Pattern.compile("#(\\w+)\\b");
		Matcher m = p.matcher(input);
		int a = 0;
		while (m.find()){
			String s = m.group(1);
			String[] parts = s.split("(?=[A-Z])"); // ?= is there for zero-width match
			String ns = String.join(" ", parts);
			input = input.substring(0, m.start() + a) + ns + input.substring(m.end() + a, input.length());
			a += parts.length-2; // -2 instead of the expected -1, because the list is joined on whitespace, so there is one less whitespace than the length of the list.
		}
		return input;
	}
	
	public static String clean(String input){
		
		HashMap<String, String> hm = new HashMap<String, String>();
		//hm.put("(https?:\\/\\/\\S+\\b|www\\.(\\w+\\.)+\\S*)", "$1 <url>"); // recognizing URLs
		hm.put("(https?:\\/\\/\\S+\\b|www\\.(\\w+\\.)+\\S*)", "<url>"); // recognizing URLs. Will have to play with this for a bit to find out if better to leave actual url in there or not 
		hm.put("(\\s+\\w+)/(\\w+\\s+)", "$1 / $2"); // splitting up words separated by /, like pen/pencil. Whitespace in regex to prevent matching on URLs
		hm.put("(@\\w+)", "<user>");
		hm.put("(?i)\\s+[8:=;]['`\\-]?[)d]+|[)d]+['`\\-]?[8:=;]\\s+", " <smile> ");
		hm.put("\\s+[8:=;]['`\\-]?[pP]+\\s+", " <lolface> ");
		hm.put("\\s+[8:=;]['`\\-]?\\(+|\\)+['`\\-]?[8:=;]\\s+", " <sadface> ");
		hm.put("\\s+[8:=;]['`\\-]?[\\/|l*]\\s+", " <neutralface> ");
		hm.put("\\s+<3\\s+"," <heart> ");
		hm.put("\\s+[-+]?[.\\d]*[\\d]+[:,.\\d]*\\s+", " <number> ");
		hm.put("([!?.]){2,}\\s+", " $1 <punctuationRepeat> ");
		
		for (String p : hm.keySet()) {
			Pattern pat = Pattern.compile(p);
			Matcher m = pat.matcher(input);
			while (m.find()) {
				//System.out.println("debugging " + hm.get(p) + ": " + m.group());
				input = m.replaceAll(hm.get(p));
			}
		}
		
		// do the other cleaning (slightly more complicated than just replaces
		input = splitHashTags(input); // NOTE that the sequence of these two determines whether fullcaps hashtags are tagged or not. Currently they are.
		input = spotAllCaps(input); // if splitHashTags is put after spotCallCaps, hashtags like #HASHTAG will not be tagged with <allCaps>
		
	    return input;
		
//			.gsub(/\b(\S*?)(.)\2{2,}\b/){ # Mark elongated words (eg. "wayyyy" => "way <ELONG>")
//				# TODO: determine if the end letter should be repeated once or twice (use lexicon/dict)
//				$~[1] + $~[2] + " <ELONG>"
//			}
		
	}
	
	public static String lowercase(String input){
		return input.toLowerCase();
	}
	
	
	public static void main(String args[]){
		
		String tweet = "“@MichaelSkolnik: A  :| woman  #thisIsaHashTag <3 in #Ferguson #andThisIsAnotherOne :P was  :-) shot!!! :( @AAP in head last night 3 times, took/taken selfie. http://t.co/5DZv7HE8FM” EVERYTHING WRONG WITH THIS WORLD⬆️";
		System.out.println(clean(tweet));
		
	}

}
