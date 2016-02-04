package searchengine.features.indexer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import searchengine.misc.CommonFunction;
import searchengine.misc.Dictionary;
import searchengine.misc.db.DML;

public class LanguageClassifier {

	private final static float PROABILITY = 0.3f;
	public final static int ENGLISH = 1;
	public final static int GERMAN = 2;
	
	@SuppressWarnings("rawtypes")
	public static String getLanguage(String input) {
		input = clean(input);
		//By calculating frequency less comparison of words are needed
		HashMap<String, Integer> inputFrequency = CommonFunction.calculateFrequency(input);
		List<String> englishList = Arrays.asList(new Dictionary().getEnglishWords());
		Iterator it = inputFrequency.entrySet().iterator();
		
		int total_english_words_occured = 1;
		int total_words = 1;
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry)it.next();
			String item = (String)pair.getKey();
			int count = (Integer) pair.getValue();
			total_words +=count;
			if (englishList.contains(item)){
				total_english_words_occured += count;
			} 
		}
		
		float ratio = (float)total_english_words_occured/(float)total_words;

		if (ratio > PROABILITY) {
			return DML.Language_english;
		} else {
			return DML.Language_deutsch;	
		}
	}

	private static String clean(String input) {
		Pattern pattern = Pattern.compile("[^a-z A-Z]");
	    Matcher matcher = pattern.matcher(input);
	    input = matcher.replaceAll("");
	    input = input.toLowerCase();
	    return input;
	}
}
