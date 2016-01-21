package project08.query;

import java.util.*;


public class ResultSnippet {
    private static final int maxWordCount = 32;
    private static final int minWordCount = 8;

    private static final double sizeMod = 1;
    private static final double distTermMod = 1;
    private static final double termMod = 1;


    private int start = 0;
    private int end = 0;
    private int wordCount = 0; //All words in the ResultSnippet
    private int relevantWordCount = 0; //All query terms in the snippet
    private int queryTermCount = 0; //Number of given terms
    private int distinctTermCount = 0; //Number of distinct query terms in snippet
    private List<String> terms;
    private String[] text;
    private boolean[] termContained;

    private static Comparator<ResultSnippet> BY_SCORE =  new Comparator<ResultSnippet>() {
        public int compare(ResultSnippet s1, ResultSnippet e2) {
            double score = s1.getScore() - e2.getScore();
            if(score < 0)
                return -1;
            if(score == 0)
                return 0;
            if(score > 0)
                return 1;
            return 0;
        }
    };

    private static Comparator<ResultSnippet> BY_START =  new Comparator<ResultSnippet>() {
        public int compare(ResultSnippet s1, ResultSnippet e2) {
            return s1.start - e2.start;
        }
    };

    private double getScore(){

        return (sizeMod * (1-((double)(wordCount-minWordCount)/(maxWordCount-minWordCount)))) //The less words the snippet has the better
               * (distTermMod*((double)distinctTermCount/queryTermCount)) //The more distinct terms are in the snippet, the better
               * (termMod*((double)relevantWordCount/wordCount)) //The more terms are in the snippet, the better
                ;
    }

    private ResultSnippet(String[] text, int start, int end, List<String> terms){
        termContained = new boolean[terms.size()];
        for (int i = 0; i < termContained.length; i++) {
            termContained[i] = false;
        }

        this.terms = terms;
        this.text = text;
        this.start = start;
        this.end = end;
        wordCount = end-start+1;
        queryTermCount = terms.size();
        for (int i = start; i <= end; i++) { //Count terms in snippet
            for (int j = 0; j < terms.size(); j++) {
                if(text[i].equalsIgnoreCase(terms.get(j))) {
                    relevantWordCount++;
                    termContained[j] = true;
                }
            }
        }
        for (int i = 0; i < termContained.length; i++) { //Count distinct terms
            if(termContained[i])
                distinctTermCount++;
        }
    }

    /**
     * Combines two Snippets into one
     * @param other
     * @return
     */
    private ResultSnippet combine(ResultSnippet other){
        int s = Math.min(start,other.start);
        int e = Math.max(end,other.end);
        if(e-s+1 <= maxWordCount)
            return new ResultSnippet(text,s,e,terms);
        else return null;
    }

    private String getSnippetText(){
        String ret = "";
        for (int i = start; i <= end; i++) {
            ret += text[i] + " ";
        }
        for (String term : terms) {
            ret = ret.replaceAll(term.toLowerCase(),"<b>"+term+"</b>");
        }
        return ret;
    }

    public static String createSnippetText(String text, List<String> terms){
        String[] words = text.split(" ");
        //Get positions of terms in text
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            for (String term : terms) {
                if(words[i].equalsIgnoreCase(term))
                    positions.add(i);
            }
        }

        //Create Snippets for every term
        Set<ResultSnippet> snippets = new HashSet<>();
        for (Integer position : positions) {
            int s = Math.max(0,position-4);
            int e = Math.min(words.length,position+3);
            if(e-s+1 <minWordCount){
                if(s == 0)
                    e+= minWordCount-e-s+1;
                else if(e == maxWordCount)
                    s-= minWordCount-e-s+1;
            }
            snippets.add(new ResultSnippet(words,s,e,terms));
        }

        //Combine snippets until there are no more
        boolean cont = true;

        while(cont){
            cont = false;
            List<ResultSnippet> tmpList = new ArrayList<>(snippets);
            Collections.sort(tmpList, ResultSnippet.BY_START);
            for (int i = 1; i < tmpList.size(); i++) {
               ResultSnippet combined =  tmpList.get(i-1).combine(tmpList.get(i));
                if(combined != null){
                    if(snippets.add(combined)) cont = true;
                }
            }
        }

        //Get best snippets
        List<ResultSnippet> snippetList = new ArrayList<>(snippets);
        Collections.sort(snippetList, ResultSnippet.BY_SCORE);
        List<ResultSnippet> chosenSnippets = new ArrayList<>();
        int count = 0;
        int i = 0;
        while(count < maxWordCount && i < snippetList.size()){
            ResultSnippet tmp = snippetList.get(i);
            i++;
            if(count +tmp.wordCount <= maxWordCount){
                chosenSnippets.add(tmp);
                count += tmp.wordCount;
            }
        }
        //return text
        Collections.sort(chosenSnippets, ResultSnippet.BY_START);
        String ret = "... ";
        for (ResultSnippet chosenSnippet : chosenSnippets) {
            ret += chosenSnippet.getSnippetText() + "... ";
        }
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        if(!(obj instanceof ResultSnippet)) return false;

        ResultSnippet other = (ResultSnippet) obj;

        if(this.start == other.start && this.end == other.end) return true;

        return false;
    }

    @Override
    public int hashCode() {
        return (int)Math.round(0.5*(start + end)*(start + end + 1) + end);
    }

    public static void main (String[] args)  {
        String text = "A snippet is a small section of text or source code that can be inserted into the code of a program or Web page. Snippets provide an easy way to implement commonly used code or functions into a larger section of code. Instead of rewriting the same code over and over again, a programmer can save the code as a snippet and simply drag and drop the snippet wherever it is needed. By using snippets, programmers and Web developers can also organize common code sections into categories, creating a cleaner development environment." +
                "Snippets used in software programming often contain one or more functions written in C, Java, or another programming language. For example, a programmer may create a basic \"mouse-down event\" snippet to play an action each time the user clicks a mouse button. Other snippets might be used to perform \"Open file\" and \"Save file\" operations. Some programmers also use plain text snippets to comment code, such as adding developer information at the beginning of each source file." +
                "In Web development, snippets often contain HTML code. An HTML snippet might be used to insert a formatted table, a Web form, or a block of text. CSS snippets may be used to apply formatting to a Web page. Web scripting snippets written in JavaScript, PHP, or another scripting language may be used to streamline dynamic Web page development. For example, a PHP snippet that contains database connection information can be inserted into each page that accesses information from a database. Whether programming software or developing websites, using snippets can save the developer a lot of time.";

        ArrayList<String> terms  = new ArrayList<>();
        terms.add("software");
        terms.add("HTML");

        String result = createSnippetText(text,terms);

        System.out.println(result);
    }

}
