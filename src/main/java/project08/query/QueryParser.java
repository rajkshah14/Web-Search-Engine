package project08.query;

import java.util.ArrayList;
import java.util.List;

import project08.indexer.Stemmer;
import project08.misc.db.DML;

/**
 * Created by Nico on 20.November.15.
 */
public class QueryParser {
    private String query;



    private List<String> terms;
    private List<String> quoted;
    private List<String> sites;

    public QueryParser(String query)
    {
        this.query = query.toLowerCase();
        terms = new ArrayList<>();
        quoted = new ArrayList<>();
        sites = new ArrayList<>();
        String[] terms = query.split(" ");
        Stemmer stem = new Stemmer();
        for (int i = 0; i < terms.length; i++) {
            if(terms[i].startsWith("site:")){ //Site operator
                sites.add(terms[i].substring(5));
            }else{
                if(terms[i].startsWith("\"") && terms[i].endsWith("\""))
                {
                    quoted.add(terms[i].substring(1,terms[i].length()-1)); //TODO should they also be stemmed?
                }else {
                    terms[i] = stem.stemString(terms[i]).trim();
                    this.terms.add(terms[i]);
                }
            }
        }
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

    public List<String> getSites() {
        return sites;
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
        return getQuotedWherePredicate() + getSitesWherePredicate();
    }
}
