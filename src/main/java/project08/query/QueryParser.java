package project08.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import project08.dictionary.DictionaryManager;
import project08.dictionary.IDict;
import project08.indexer.Stemmer;
import project08.misc.db.DML;

public class QueryParser {
    private String query;
    private String lang;


    private List<String> terms;
    private List<String> quoted;
    private List<String> sites;
    private List<String> synonyms;

    public QueryParser(String query, String lang)
    {
        this.lang = lang;
        this.query = query.toLowerCase();
        terms = new ArrayList<>();
        quoted = new ArrayList<>();
        sites = new ArrayList<>();
        synonyms = new ArrayList<>();
        String[] terms = query.split(" ");
        Stemmer stem = new Stemmer();
        for (int i = 0; i < terms.length; i++) {
            if(terms[i].startsWith("site:")){ //Site operator
                sites.add(terms[i].substring(5));
            }else if(terms[i].startsWith("\"") && terms[i].endsWith("\""))
            {
              quoted.add(terms[i].substring(1,terms[i].length()-1)); //TODO should they also be stemmed?
            }else if(terms[i].startsWith("~")){
                synonyms.add(terms[i].substring(1));
            }else {
                terms[i] = stem.stemString(terms[i]).trim();
                this.terms.add(terms[i]);
            }
        }
    }

    public int getTermCount(){
        return terms.size()+quoted.size()+synonyms.size();
    }

    public QueryParser(String query)
    {
       this(query, "en");
    }


    public String getQuery() {
        return query;
    }

    public List<String> getTerms() {
        return terms;
    }

    public List<String> getQuoted() {
        return quoted;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public List<String> getSites() {
        return sites;
    }

    public List<String> getAllTerms(){
        ArrayList<String> t = new ArrayList<>(terms);
        t.addAll(quoted);
        t.addAll(synonyms);
        return t;
    }

    public String[] getTermsArray() {
        String[] strArray = new String[terms.size()];
        strArray = terms.toArray(strArray);
        return strArray;
    }

    public String[] getQuotedArray() {
        String[] strArray = new String[quoted.size()];
        strArray = quoted.toArray(strArray);
        return strArray;
    }

    public String[] getSitesArray() {
        String[] strArray = new String[sites.size()];
        strArray = sites.toArray(strArray);
        return strArray;
    }

    public String getQuotedWherePredicate()
    {
        String where = " ";
        for (String s : getQuoted()) {
            where += "AND "+ DML.Features_doc_id +" IN (" +
                    "SELECT "+DML.Col_docId+" FROM "+DML.Table_Features+" " +
                    "WHERE "+DML.Col_wordId+" = "+s.hashCode() +") ";
        }
        return where;
    }

    public String getSynonymWherePredicate()
    {
        IDict dict;
        if(lang.equalsIgnoreCase("en"))
            dict = DictionaryManager.getDictionary(DictionaryManager.Language.ENG);
        else if(lang.equalsIgnoreCase("de"))
            dict = DictionaryManager.getDictionary(DictionaryManager.Language.GER);
        else
            dict = DictionaryManager.getDictionary(DictionaryManager.Language.ENG);

        String where = " ";
        for (String s : getSynonyms()) {
            List<String> words = dict.getSynonyms(s);
            List<Integer> hashCodes = new ArrayList<>();
            for (String word : words) {
                hashCodes.add(word.hashCode());
            }
            where += "AND "+ DML.Features_word_id +" IN (" + StringUtils.join(hashCodes, ',') + ") ";
        }
        return where;
    }

    public String getSitesWherePredicate()
    {
        String where = " ";
        for (String s : getSites()) {
            where += "AND "+DML.Table_Documents+"."+DML.Col_url+" ILIKE '%"+s+"%' ";
        }
        return where;
    }

    public String getWherePedicate()
    {
        return getQuotedWherePredicate() + getSynonymWherePredicate() + getSitesWherePredicate();
    }
}
