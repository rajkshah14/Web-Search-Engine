package searchengine.features.metasearch;

import java.util.Comparator;

import searchengine.model.SearchResultItem;

public class MetaSearchResultItem  extends SearchResultItem{
    private String engineURL;
    private double colScore;

    public MetaSearchResultItem(int rank, String url, double tf_idf) {
        super(rank, url, tf_idf);
    }

    public String getEngineURL() {
        return engineURL;
    }

    public void setEngineURL(String engineURL) {
        this.engineURL = engineURL;
    }

    public void setColScore(double colScore) {
        this.colScore = colScore;
    }

    public double getScore() {
        return (getTf_idf()+0.4*getTf_idf()*colScore)/1.4;
    }

    public static Comparator<MetaSearchResultItem> normalizedComparator(){
        return new Comparator<MetaSearchResultItem>() {
            @Override
            public int compare(MetaSearchResultItem o1, MetaSearchResultItem o2) {
                return ((Double)o2.getScore()).compareTo(o1.getScore());
            }
        };
    }
}
