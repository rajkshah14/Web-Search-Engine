package project08.misc.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DML {
	
	static Connection connection = null;
	private static boolean isLogger = false;
	
	//Tables
	public static final String Table_Documents = "documents";
	public static final String Table_Features = "features";
	public static final String Table_Links = "links";
	public static final String Table_Crawler_queue = "crawler_queue";
	public static final String Table_Crawl_Request = "crawl_request";
	public static final String Table_Doc_Count = "doc_count";

	//Views
	public static final String View_Features_tfidf = "features_tfidf";
	public static final String View_Features_bm25 = "features_bm25";
	public static final String View_Features_bm25_pagerank = "features_bm25_pagerank";

	//Functions
	public static final String Function_Calc_IDF = "calculate_idf";
	public static final String Function_Calc_TFIDF = "calculate_scores";
	public static final String Function_DOC_Count = "document_count";
	public static final String Function_DOC_Term_Count = "document_term_count";
	public static final String Function_Average_Doc_Length = "avg_doc_length";
	public static final String Function_Doc_Length = "doc_length";
	public static final String Function_Pagerank_Weight = "pagerank_weight";
	public static final String Function_bm25_Weight = "bm25_weight";
	public static final String Function_calculate_bm25_pagerank = "calculate_bm25_pagerank";

	//Indexes
	public static final String Index_queue_docId = "queue_docID";

	public static final String Index_queue_URL = "queue_URL";
	public static final String Index_queue_depth = "queue_Depth";
	public static final String Index_documents_docID = "documents_docID";
	public static final String Index_documents_URL = "documents_URL";
	public static final String Index_features_docID = "features_docID";
	public static final String Index_features_wordID = "features_wordID";
	public static final String Index_features_language = "features_language";
	
	//Table Documents
	public static final String Col_docId = "doc_id";
	public static final String Col_url = "url";
	public static final String Col_crawl_on_timestamp = "crawl_on_timestamp";
	public static final String Col_desc = "description";
	public static final String Col_keywords = "keywords";
	public static final String Col_author = "author";
	public static final String Col_title = "title";
	public static final String Col_length = "length";
	public static final String Col_pageRank = "page_rank";
	public static final String Col_content_snap = "content_snap";
	public static final int Col_author_length = 255;
	public static final int Col_title_length = 255;
	
	//Table Features
	public static final String Col_wordId = "word_id";
	public static final String Col_term = "term";
	public static final String Col_term_frequency = "term_frequency";
	public static final String Col_tf = "tf";
	public static final String Col_idf = "idf";
	public static final String Col_tf_idf = "tf_idf";
	public static final String Col_bm25 = "bm25";
	public static final String Col_bm25_pagerank = "bm25_pagerank";
	public static final String Col_score = "score";
	public static final String Col_language = "language";
	public static final int maxTermLength = 255;
	//Table Links
	public static final String Col_from_doc_id = "from_docid";

	public static final String Col_to_doc_id = "to_docid";
	//Table Crawler_queue
	public static final String Col_depth = "depth";

	public static final String Col_visited = "visited";
	//Table Crawl_request
	public static final String Col_leave_domain = "leave_domain";

	public static final String Col_max_doc = "max_doc";
	//Table Doc_Count
	public static final String Col_count = "count";

	// Query Terms
	public static final String Features_doc_id = Table_Features + "." + Col_docId;

	public static final String Features_word_id = Table_Features + "." + Col_wordId;
	public static final String Documents_doc_id = Table_Documents + "." + Col_docId;
	
	// Languages
	public static final String Language_other = "ot";
	public static final String Language_english = "en";
	public static final String Language_deutsch = "de";

	//Calculations
	public static final double Bm25_k = 1.5;
	public static final double Bm25_b = 0.75;
	public static final String Score_bm25_calculation =  Col_idf + "*((" + Col_term_frequency + "*("+Bm25_k+"+1))"
			+ "/(" + Col_term_frequency + "*(1-" + Bm25_b + "+" + Bm25_b + "*(" + Function_Doc_Length + "("+Col_docId+")/"
			+ Function_Average_Doc_Length +"()))))";

	public static void createTables() {
		if (isLogger) {
			System.out.println("Documents : " + createTableDocuments(false));
			System.out.println("Features : "+ createTableFeatures(false));
			System.out.println("Features_TFIDF : "+ createViewFeaturesTFIDF(false));
			System.out.println("Features_BM25 : "+ createViewFeaturesBm25(false));
			System.out.println("Links : " + createTableLinks(false));
			System.out.println("Queue : " + createTableQueue(false));
			System.out.println("Crawl Request : " + createTableCrawlRequest(false));
			System.out.println("DocCount : " + createTableDocCount(true));
		} else {
			createTableDocuments(false);
			createTableFeatures(false);
			createViewFeaturesTFIDF(false);
			createViewFeaturesBm25(false);
			createTableLinks(false);
			createTableQueue(false);
			createTableCrawlRequest(false);
			createTableDocCount(true);
		}
	}

	public static void createViews() {
		if (isLogger) {
			System.out.println("Features_TFIDF : "+ createViewFeaturesTFIDF(false));
			System.out.println("Features_BM25 : "+ createViewFeaturesBm25(false));
			System.out.println("Features_BM25_Pagerank : "+ createViewFeaturesBm25Pagerank(false));
		} else {
			createViewFeaturesTFIDF(false);
			createViewFeaturesBm25(false);
			createViewFeaturesBm25Pagerank(false);
		}
	}

	public static void createIndexes() {
		if (isLogger) {
			System.out.println("Index " + Index_queue_docId+": " + createQueueDocIDIndex(false));
			System.out.println("Index " + Index_queue_URL+": " + createQueueURLIndex(false));
			System.out.println("Index " + Index_queue_depth+": " + createQueueDepthIndex(false));
			System.out.println("Index " + Index_documents_docID+": " + createDocumentsDocIdIndex(false));
			System.out.println("Index " + Index_documents_URL+": " + createDocumentsURLIndex(false));
			System.out.println("Index " + Index_features_docID+": " + createFeaturesDocIdIndex(false));
			System.out.println("Index " + Index_features_wordID+": " + createFeaturesWordIdIndex(false));
			System.out.println("Index " + Index_features_language + ":" + createFeaturesLanguageIndex(true));
		} else {
			createQueueDocIDIndex(false);
			createQueueURLIndex(false);
			createQueueDepthIndex(false);
			createDocumentsDocIdIndex(false);
			createFeaturesDocIdIndex(false);
			createFeaturesWordIdIndex(true);
		}
	}

	public static void createFunctions() {
		if (isLogger) {
			System.out.println("DocCount Function : " + calculateDocCountFunction(false));
			System.out.println("DocTermCount function : " + calculateDocTermCountFunction(false));
			System.out.println("IDF Function : " + calculateIDFFunction(false));
			System.out.println("DocLength Function : " + getDocLength(false));
			System.out.println("AvgDocLength Function : " + getAvgDocLength(false));
			System.out.println("Pagerank Weight Function : " + getPagerankWeight(false));
			System.out.println("BM25 Weight Function : " + getBM25Weight(false));
			System.out.println("Score Function : " + calculateScores(true));
			System.out.println("Calculate BM25 Pagerank Score Function : " + calculatebm25pagerank(true));
		} else {
			calculateDocCountFunction(false);
			calculateDocTermCountFunction(false);
			calculateIDFFunction(false);
			getBM25Weight(false);
			getPagerankWeight(false);
			getDocLength(false);
			getAvgDocLength(false);
			calculateScores(true);
			calculatebm25pagerank(true);
		}
	}
	
	public static void initializeDatabase() {
		createTables();
		createFunctions();
		createViews();
	}
	
	private static boolean createTableDocCount(boolean closeDb){
		return createTable("CREATE TABLE IF NOT EXISTS " +
				Table_Doc_Count + "(" 
				+ DML.Col_wordId + " int PRIMARY key, "
				+ Col_count+ " int);",closeDb);
	}

	private static boolean createTableFeatures(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS "+Table_Features+" ("
				+ Col_docId +" integer, "
				+ Col_term + " varchar("+ maxTermLength +") NOT NULL, "
				+ Col_wordId + " integer NOT NULL, "
				+ Col_term_frequency + " integer, "
				+ Col_tf + " double precision , "
				+ Col_idf + " double precision, "
				+ Col_tf_idf + " double precision DEFAULT 0.0, "
				+ Col_bm25 + " double precision DEFAULT 0.0, "
				+ Col_pageRank + " double precision DEFAULT 0.0, "
				+ Col_bm25_pagerank + " double precision DEFAULT 0.0, "
				+ Col_language + " varchar(2) check ( " + Col_language + " IN ('en','de','ot')), "
				+ "CONSTRAINT featuresPK PRIMARY KEY("+Col_docId+","+Col_wordId+"))",closeDb);
	}

	private static boolean createViewFeaturesTFIDF(boolean closeDb) {
		return createTable("CREATE OR REPLACE VIEW " + View_Features_tfidf
				+ " AS SELECT "
				+ Col_docId +", "
				+ Col_term + ", "
				+ Col_wordId + ", "
				+ Col_term_frequency + ", "
				+ Col_tf_idf + " AS " + Col_score
				+ " FROM " + Table_Features, closeDb);
	}

	private static boolean createViewFeaturesBm25(boolean closeDb) {
		return createTable("CREATE OR REPLACE VIEW " + View_Features_bm25
				+ " AS SELECT "
				+ Col_docId +", "
				+ Col_term + ", "
				+ Col_wordId + ", "
				+ Col_term_frequency + ", "
				+ Col_bm25 + " AS " + Col_score
				+ " FROM " + Table_Features, closeDb);
	}

	private static boolean createViewFeaturesBm25Pagerank(boolean closeDb) {
		return createTable("CREATE OR REPLACE VIEW " + View_Features_bm25_pagerank
				+ " AS SELECT "
				+ Col_docId +", "
				+ Col_term + ", "
				+ Col_wordId + ", "
				+ Col_term_frequency + ", "
				+ Col_bm25_pagerank + " AS " + Col_score
				+ " FROM " + Table_Features , closeDb);
	}
	
	private static boolean createTableDocuments(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS "+Table_Documents+" ("
				+ Col_docId + " INTEGER PRIMARY KEY, "
				+ Col_url + " varchar(400), "
				+ Col_language + " varchar(2) check ( " + Col_language + " IN ('en','de','ot')), "
				+ Col_crawl_on_timestamp + " timestamp DEFAULT ('now'::text)::timestamp, "
				+ Col_length + " INTEGER, "
				+ Col_pageRank + " double precision,"
				+ Col_desc + " text, "
				+ Col_keywords + " text, "
				+ Col_content_snap + " text, "
				+ Col_title + " varchar("+Col_title_length+"), "
				+ Col_author + " varchar("+Col_author_length+"))",closeDb);
	}
	
	private static boolean createTableLinks(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Links + " ( "
				+ Col_from_doc_id + " INTEGER, "
				+ Col_to_doc_id +" INTEGER, " +
				"CONSTRAINT linksPK PRIMARY KEY("+Col_from_doc_id+","+Col_to_doc_id+"))",closeDb);
	}

	// QUE allowed to go out of domain column can be added?
	private static boolean createTableQueue(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Crawler_queue + " ( "
				+ Col_docId + " INTEGER, "
				+ Col_url + " varchar(400) UNIQUE, "
				+ Col_depth +" INTEGER, "
				+ Col_visited +" BOOLEAN DEFAULT FALSE)",closeDb);
	}
	
	private static boolean createTableCrawlRequest(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Crawl_Request + " ( "
				+ Col_docId + " INTEGER, "
				+ Col_url + " varchar(400), "
				+ Col_depth +" INTEGER, "
				+ Col_max_doc +" INTEGER, "
				+ Col_leave_domain + " BOOLEAN, "
				+ Col_crawl_on_timestamp + " timestamp DEFAULT ('now'::text)::timestamp, "
				+ Col_visited +" BOOLEAN DEFAULT FALSE)",closeDb);
	}

	private static boolean calculateDocCountFunction(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_DOC_Count+"() RETURNS bigint LANGUAGE SQL " +
				"AS $$ SELECT COUNT(" + Table_Documents +"." + Col_docId + ") " +
				"FROM " + Table_Documents + " $$",closeDb);
	}

	private static boolean calculatebm25pagerank(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_calculate_bm25_pagerank+"() RETURNS boolean LANGUAGE SQL "
				+ "AS $$ UPDATE " + Table_Features + " SET " + Col_bm25_pagerank +" = "
				+ Function_bm25_Weight + "() *" + Col_bm25
				+ "+" + Function_Pagerank_Weight + "()*" +  Col_pageRank + " RETURNING true $$",closeDb);
	}

	private static boolean getDocLength(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_Doc_Length+"(doc int) RETURNS int LANGUAGE SQL " +
				"AS $$ SELECT "+ Col_length +
				" FROM " + Table_Documents + " WHERE " + Col_docId +"=doc; $$",closeDb);
	}

	private static boolean getAvgDocLength(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_Average_Doc_Length+"() RETURNS numeric LANGUAGE SQL STABLE " +
				"AS $$ SELECT "+ "avg("+Col_length+")" +
				" FROM " + Table_Documents +  "; $$",closeDb);
	}

	private static boolean getPagerankWeight(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_Pagerank_Weight+"() RETURNS double precision LANGUAGE SQL STABLE " +
				"AS $$ SELECT "+ "1 / max("+Col_pageRank+")" +
				" FROM " + Table_Documents +  "; $$",closeDb);
	}

	private static boolean getBM25Weight(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_bm25_Weight+"() RETURNS double precision LANGUAGE SQL STABLE " +
				"AS $$ SELECT "+ "1 / max("+Col_bm25+")" +
				" FROM " + Table_Features +  "; $$",closeDb);
	}

	private static boolean calculateDocTermCountFunction(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_DOC_Term_Count+"(term_id int) RETURNS bigint LANGUAGE SQL STABLE " +
				"AS $$ SELECT COUNT(" + Table_Features +"." + Col_docId + ") " +
				"FROM " + Table_Features + " " +
				"WHERE " + Col_wordId +" = term_id GROUP BY " + Col_wordId +" $$",closeDb);
	}

	private static boolean calculateIDFFunction(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_Calc_IDF+"() RETURNS BOOLEAN LANGUAGE SQL " +
				"AS $$  " +
				"TRUNCATE " + Table_Doc_Count + "; " +
				"INSERT INTO " + Table_Doc_Count + " " +
						"SELECT " + Col_wordId + ", COUNT(" + Col_docId + ") as " + Col_count + " from " + Table_Features + " " +
						"GROUP BY " + Col_wordId + "; "+
				"UPDATE " + Table_Features + " SET " + Col_idf +" = " +
				"log( " +
				"cast(document_count() as real) / " +
				"cast(	(SELECT " + Col_count + " " +
						"FROM " + Table_Doc_Count + " " +
						"WHERE " + Table_Features + "." + Col_wordId + "= " + Table_Doc_Count + "." + Col_wordId + ") as real) " +
				") " +
				"RETURNING TRUE; $$",closeDb);
	}

	private static boolean calculateScores(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_Calc_TFIDF+"() RETURNS BOOLEAN LANGUAGE SQL " +
				"AS $$  UPDATE " + Table_Features +" SET " + Col_tf_idf +" = " +
				Col_tf +"*"+Col_idf + ", "
				+ Col_bm25 + "=" + Score_bm25_calculation
				+ " RETURNING TRUE; $$", closeDb);
	}

	private static boolean createQueueDocIDIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_queue_docId+" ON " + Table_Crawler_queue + " " +
				" ( " + Col_docId +") ",false, closeDb);
	}

	private static boolean createQueueURLIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_queue_URL+" ON " + Table_Crawler_queue + " " +
				"USING hash ( " + Col_url +") ",false, closeDb);
	}

	private static boolean createQueueDepthIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_queue_depth+" ON " + Table_Crawler_queue + " " +
				" ( " + Col_depth +") ",false, closeDb);
	}

	private static boolean createDocumentsDocIdIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_documents_docID+" ON " + Table_Documents + " " +
				"USING hash ( " + Col_docId +") ",false, closeDb);
	}

	private static boolean createDocumentsURLIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_documents_URL+" ON " + Table_Documents + " " +
				" ( " + Col_url +") ",false, closeDb);
	}

	private static boolean createFeaturesDocIdIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_features_docID+" ON " + Table_Features + " " +
				"USING hash ( " + Col_docId +") ",false, closeDb);
	}

	private static boolean createFeaturesWordIdIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+Index_features_wordID+" ON " + Table_Features + " " +
				"USING hash ( " + Col_wordId +") ",false, closeDb);
	}

	private static boolean createFeaturesLanguageIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+ Index_features_language + " ON " + Table_Features + " " +
				"USING hash ( " + Col_language +") ",false, closeDb);
	}

	private static boolean executeSQL(String sql, boolean printException, boolean closeDb) {
		
		try {
			if (connection == null)
				connection = DBConfig.getConnection();
			if (connection.isClosed())
				connection = DBConfig.getConnection();
			
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			connection.commit();
			return true;
		} catch (SQLException e) {
			if(printException)
				e.printStackTrace();
		} catch (Exception e) {
			if(printException)
				e.printStackTrace();
		} finally {
			if(connection!= null && closeDb)
				try {connection.close();} catch (SQLException e) {}
		}
		return false;
	}

	private static boolean createTable (String sql, boolean closeDb) {
		return executeSQL(sql,true, closeDb);
	}	
	
	public static void dropAllTable() {
		String query = "DROP TABLE IF EXISTS "
				+ " " + Table_Crawl_Request
				+ ", " + Table_Crawler_queue
				+ ", " + Table_Doc_Count
				+ ", " + Table_Documents
				+ ", " + Table_Features
				+ ", " + Table_Links + " CASCADE";
		System.out.println(query);
		executeSQL(query, true, true);
	}
	
	public static void dropAllFunction() {
		String query = "DROP FUNCTION  IF EXISTS " + Function_Calc_IDF + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_Calc_TFIDF + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_DOC_Count + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_DOC_Term_Count + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_Average_Doc_Length + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_Pagerank_Weight + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_bm25_Weight + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_calculate_bm25_pagerank + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_Doc_Length + "()";
		System.out.println(query);
		executeSQL(query, true, true);
	}

	public static void main (String[] args) throws SQLException {
		//dropAllTable();
		//dropAllFunction();
		//createTables();
		createFunctions();
		createViews();
	}
}