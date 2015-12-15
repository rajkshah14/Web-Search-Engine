package project08.indexer;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.postgresql.util.PSQLException;

import project08.misc.CommonFunction;
import project08.misc.Dictionary;
import project08.misc.db.DBConfig;
import project08.misc.db.DML;
import project08.misc.log.Log;

/**
 * Indexer will calculate word frequency & add into database
 * @author Raj
 */
public class Indexer {

	private String url;
	private int docId;
	private InputStream inputStream;
	private String language = DML.Language_other;
	private Connection conn = null;
	private int lenght = 0;
	private String content_snap ="'";
	
	public Indexer(String url, int docId) {
		this.url = url;
		this.docId = docId;
	}

	public Indexer(InputStream inputStream, int docId) {
		this.inputStream = inputStream;
		this.docId = docId;
	}

	public Indexer(String url) {
		this.url = url;
		this.docId = url.hashCode();
	}

	/**
	 * Call this function for indexing html content Parse html, find freuency of
	 * words, add into database
	 * @return true if processing is successful & false if anything wents wrong
	 */
	public boolean processURL() {
		return process(readURL());
	}

	public boolean process(String html) {
		conn = DBConfig.getConnection(true);
		html = StringEscapeUtils.unescapeHtml4(html);
		Charset.forName("UTF-8").encode(html);
		extractMetaData(html);
		html = Html2Text.fromat(html).toLowerCase();
		if (html == null)
			return false;
		language = LanguageClassifier.getLanguage(html);
		generateContentSnap(html);
		html = removeStopWords(html, language);
		html = new Stemmer().stemString(html);
		HashMap<String, Integer> frequencyMap = CommonFunction.calculateFrequency(html);
		insertIntoDatabase(frequencyMap);
		updateDocumentsTable();
		try {
			conn.close();
		} catch (SQLException e) {
			Log.logException(e);
		}
		return true;
	}
	
	public boolean processStream() {
		StringBuffer buffer = new StringBuffer();
		int ptr = 0;
		try {
			while ((ptr = this.inputStream.read()) != -1) {
				buffer.append((char) ptr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return process(buffer.toString());

	}
	
	private void generateContentSnap(String html) {
		html = html.replaceAll("[^A-Za-z]+", " "); //"[^A-Za-z]+"^[a-zA-Z0-9]*$
		html = html.trim().replaceAll("[ ]+", " ");
		content_snap += html + "'";
	}

	/**
	 * Remove stop words from string of html content depending on language
	 * @param input
	 * @return
	 */
	private String removeStopWords(String input, String language) {
		
		List<String> words = new ArrayList<>(Arrays.asList(input.split(" ")));
		words.removeAll(Collections.singleton(""));
		List<String> stopwords;
		switch (language) {
		case DML.Language_deutsch:
			stopwords = Arrays.asList(Dictionary.getDeutchStopWords());
			break;
		case DML.Language_english:
			stopwords = Arrays.asList(Dictionary.stopEnglishStopWords());
			break;
		default:
				return input;
		}
		
		for (String stopWord : stopwords)
			words.removeAll(Collections.singleton(stopWord));
		StringBuilder builder = new StringBuilder();
		for (String word : words)
			if (word.length() > 1)
				builder.append(word).append(" ");
		return builder.toString();
	}

	/**
	 * Inserts frequency into database
	 * @param map containing key (word) & value (frequency)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean insertIntoDatabase(HashMap<String, Integer> hm) {
		// To reduce load batch wise data is pushed into database
		final int batchSize = 50;
		int count = 0;
		String sql = "insert into " + DML.Table_Features + " (" 
				+ DML.Col_docId + ", " 
				+ DML.Col_term + ", " 
				+ DML.Col_term_frequency + ", "
				+ DML.Col_tf + ","
				+ DML.Col_wordId + "," 
				+ DML.Col_language + ")"
				+ " values (" + docId + ", ?, ?,?,?,?) "
				+ " ON CONFLICT DO NOTHING";

		if (conn == null)
			conn = DBConfig.getConnection();
		try {
			if (conn.isClosed())
				conn = DBConfig.getConnection();
			removeItemsFromDatabase(conn);
			PreparedStatement ps = conn.prepareStatement(sql);

			Iterator it = hm.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();
				int freq = Integer.parseInt(pair.getValue().toString());
				lenght += freq;
				String word = pair.getKey().toString();
				if (word.length()<2 || word.length()> DML.maxTermLength) //word > maxTermLength dirty fix. Sometimes words are too long and indexer does not terminate correctly
					continue;
				ps.setString(1, word);
				ps.setInt(2, freq);
				ps.setDouble(3, 1 + Math.log10(freq));
				ps.setInt(4, word.hashCode());
				ps.setString(5, language);
				ps.addBatch();
				if (++count % batchSize == 0)
					ps.executeBatch();
			}
			ps.executeBatch();
			ps.close();
			return true;
		} catch (PSQLException e) {
			e.printStackTrace();
		} catch (BatchUpdateException e) {
			e.getNextException().printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Old data should be removed for docId
	 * @param connection
	 * @throws SQLException
	 */
	private void removeItemsFromDatabase(Connection connection) throws SQLException {
		String sql = "DELETE FROM " + DML.Table_Features + " where "
				+ DML.Col_docId + " = '" + docId + "'";
		PreparedStatement ps = connection.prepareStatement(sql);
		ps.execute();
	}
	
	private void extractMetaData(String html) {
		String metaTagDescription = "<\\s*meta\\s*name\\s*=\\s*\"description\"\\s*content\\s*=\\s*\"(.*)\"\\s*>";
		String metaTagKeywords = "<\\s*meta\\s*name\\s*=\\s*\"keywords\"\\s*content\\s*=\\s*\"(.*)\"\\s*>";
		String metaTagAuthor = "<\\s*meta\\s*name\\s*=\\s*\"author\"\\s*content\\s*=\\s*\"(.*)\"\\s*>";
		String metaTagTitle = "<\\s*title\\s*>(.*)<\\/title>";

		Pattern p = Pattern.compile(metaTagDescription);
		Matcher m = p.matcher(html);
		String description = "";
		if(m.find()) {
			description = m.group(1);
		}
		p = Pattern.compile(metaTagKeywords);
		m = p.matcher(html);
		String keywords = "";
		if(m.find()) {
			keywords = m.group(1);
		}
		p = Pattern.compile(metaTagAuthor);
		m = p.matcher(html);
		String author = "";

		if(m.find()){
			author = m.group(1);
		}

		p = Pattern.compile(metaTagTitle);
		m = p.matcher(html);
		String title = "";
		if(m.find()) {
			title = m.group(1);
		}

		if (conn==null)
			conn = DBConfig.getConnection();

		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement("UPDATE "+DML.Table_Documents+" SET "
                    + DML.Col_desc + " = ?, "
                    + DML.Col_keywords + " = ?, "
                    + DML.Col_author + " = ?, "
                    + DML.Col_title + " = ? "
                    + "WHERE " + DML.Col_docId + " = ?");

			stmt.setString(1, Html2Text.fromat(description));
			stmt.setString(2, Html2Text.fromat(keywords));
			stmt.setString(3, Html2Text.fromat(author.substring(0,Math.min(DML.Col_author_length,author.length()))));
			stmt.setString(4, Html2Text.fromat(title.substring(0,Math.min(DML.Col_title_length,title.length()))));
			stmt.setInt(5, docId);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}

	private void updateDocumentsTable() {
		String sql = "Update " + DML.Table_Documents + " SET " 
				+ DML.Col_language + " = '" + language + "', "
				+ DML.Col_length + " = " + lenght +", "
				+ DML.Col_content_snap + " = " + content_snap 
				+ " WHERE " + DML.Col_docId + " = " + docId;
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String readURL() {
		try {
			URL url = new URL(this.url);
			InputStream is = url.openStream();
			int ptr = 0;
			StringBuffer buffer = new StringBuffer();
			while ((ptr = is.read()) != -1) {
				buffer.append((char) ptr);
			}
			is.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
