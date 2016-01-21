package project08.dictionary;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class EnglishDict implements IDict{

    private static final String wnhomePath = "/usr/bin/wordnet";
    IDictionary dict;

    EnglishDict() throws IOException {
        String wnhome = System.getenv ("WNHOME") ;
        if(wnhome == null)
            wnhome = wnhomePath;
        String path = wnhome + File.separator + "dict";

        URL url = new URL("file",null, path);

        dict = new Dictionary(url);

        dict.open();

    }

    @Override
    public List<String> getSynonyms(String word) {
        Set<String> synonyms = new HashSet<>();
        Set<IWordID> wordIds = new HashSet<>();

        IIndexWord noun = dict.getIndexWord (word, POS.NOUN);
        IIndexWord adj = dict.getIndexWord (word, POS.ADJECTIVE);
        IIndexWord adv = dict.getIndexWord (word, POS.ADVERB);
        IIndexWord verb = dict.getIndexWord (word, POS.VERB);
        if(noun != null)
            wordIds.addAll(noun.getWordIDs());
        if(adj != null)
            wordIds.addAll(adj.getWordIDs());
        if(adv != null)
            wordIds.addAll(adv.getWordIDs());
        if(verb != null)
            wordIds.addAll(verb.getWordIDs());

        for (IWordID wordId : wordIds) {
            IWord w = dict.getWord(wordId);
            synonyms.add(w.getLemma());
            ISynset synset = w.getSynset();
            for (IWord iWord : synset.getWords()) {
                synonyms.add(iWord.getLemma());
            }
        }
        return new ArrayList<>(synonyms);
    }

    public static void main (String[] args) throws IOException {
        EnglishDict dict = new EnglishDict();
        List<String> syns = dict.getSynonyms("dog");
        for (String syn : syns) {
            System.out.println(syn);
        }
    }
}
