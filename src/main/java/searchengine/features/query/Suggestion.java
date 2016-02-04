package searchengine.features.query;

class Suggestion implements Comparable<Suggestion>{

    public String getOriginalTerm() {
        return originalTerm;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public double getDistance() {
        return distance;
    }

    private String originalTerm;
    private String suggestion;
    private double distance;

    public Suggestion(String originalTerm, String suggestion, double distance){
        this.originalTerm = originalTerm;
        this.suggestion = suggestion;
        this.distance = distance;
    }


    @Override
    public int compareTo(Suggestion o) {
        if(distance < o.distance) return -1;
        else if(distance > o.distance) return 1;
        else return 0;
    }

    @Override
    public String toString() {
        return suggestion;
    }
}
