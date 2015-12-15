package project08.misc;

import java.util.HashMap;
import java.util.StringTokenizer;

public class CommonFunction {
	
	/**
	 * Calculates frequency of words
	 * @param input string of parsed html content
	 * @return hashmap of word & frequency
	 */
	public static HashMap<String, Integer> calculateFrequency(String input) {
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		StringTokenizer tokenizer = new StringTokenizer(input, " ");
		while (tokenizer.hasMoreElements()) {
			String element = (String) tokenizer.nextElement();
			element = element.toLowerCase().trim();
			if (hm.containsKey(element)) {
				int count = hm.get(element);
				hm.remove(element);
				hm.put(element, count + 1);
			} else {
				hm.put(element, 1);
			}
		}
		return hm;
	}
}
