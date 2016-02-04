package searchengine.features.dictionary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GermanDict implements IDict{
    private static final String fileName = "germanSynonyms.txt";
    private File file;

    GermanDict(){
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource(fileName).getFile());

    }
    @Override
    public List<String> getSynonyms(String word) {
        Set<String> synonyms = new HashSet<>();
        Pattern p = Pattern.compile("(^|;)"+word.toLowerCase()+"($|;)");

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = line.toLowerCase();
                line = line.replaceAll("(\\s\\([^)]*\\))", "");
                Matcher m = p.matcher(line);
                if(m.find())
                {
                    String[] split = line.split(";");
                    for (String s : split) {
                        synonyms.add(s);
                    }
                }

            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(synonyms);
    }

    public static void main (String[] args) throws IOException {
        GermanDict dict = new GermanDict();
        List<String> syns = dict.getSynonyms("Schreibblock");
        for (String syn : syns) {
            System.out.println(syn);
        }
    }
}
