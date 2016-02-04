package searchengine.features.dictionary;

import java.io.IOException;

public class DictionaryManager {
	public enum Language {
		ENG, GER
	}

	public static IDict getDictionary(Language lang) {
		try {
			switch (lang) {
			case ENG:
				return new EnglishDict();
			case GER:
				return new GermanDict();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
