package project08.search;



import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import project08.model.SearchResultItem;

public class CommandLineSearch {
    public static void main (String[] args) throws ClassNotFoundException, SQLException {
        Search search = new Search();
        Scanner sc = new Scanner(System.in);
        do
        {
            System.out.println("Please enter your Query: ");
            String query = sc.nextLine();
            List<SearchResultItem> searchResult = search.search(query, 5, false);
            if(searchResult.size()>0)
                for (SearchResultItem searchResultItem : searchResult) {
                    System.out.println(searchResultItem);
                }
            else System.out.println("No Results");
        }while(true && sc.hasNextLine());
        sc.close();
    }

}
