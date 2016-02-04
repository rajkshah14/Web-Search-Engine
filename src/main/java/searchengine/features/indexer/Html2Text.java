package searchengine.features.indexer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Html2Text {

	public static String format(String content) {
		String scriptregex = "<(script|style)[^>]*>[^<]*</(script|style)>";
		Pattern p1 = Pattern.compile(scriptregex, Pattern.CASE_INSENSITIVE);
		Matcher m1 = p1.matcher(content);
		content = m1.replaceAll(" ");
		
		String tagregex = "(?!<\\s*img[^>]*>)<[^>]*>"; //Match everything except for images
		Pattern p2 = Pattern.compile(tagregex);
		Matcher m2 = p2.matcher(content);
		content = m2.replaceAll(" ");
		
		String multiplenewlines = "(\\n{1,2})(\\s*\\n)+";
		content = content.replaceAll(multiplenewlines, "$1").replaceAll("\\.", " ").replaceAll("&nbsp", "");
//		System.out.println(content);
//		content= content.replaceAll("<script.*?</script>", "").replaceAll("<style.*?</style>", " ").replaceAll("<.*?>", " ").replaceAll("[^a-zA-Z]"," ").replaceAll("\\s+"," ").toLowerCase().trim();
//		System.out.println("The parsed content of URL:" + contents);
		content = content.replaceAll("[-+.^:,]", " ").replaceAll("\\s+", " ");
		return content;
	}
}
