package searchengine.misc.db;

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
	public static final String Table_Images = "images";
	public static final String Table_shingles = "shingles";
	public static final String Table_Jaccard = "jaccard";
	public static final String Table_Searchengines = "searchengines";
	public static final String Table_Searchengine_terms = "searchengine_terms";
	public static final String Table_ad = "ads";
	public static final String Table_ad_click = "ad_click";

	//Views
	public static final String View_DocPairs = "docPairs";
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
	public static final String Function_calculate_jaccard = "calculate_jaccard";
	public static final String Function_similarDocuments = "similarDocuments";
	public static final String Function_avg_cw = "avg_cw";
	public static final String Function_engine_count = "engine_count";
	public static final String Function_cf = "cf";

	//Indexes
	public static final String Index_queue_docId = "queue_docID";

	public static final String Index_queue_URL = "queue_URL";
	public static final String Index_queue_depth = "queue_Depth";
	public static final String Index_documents_docID = "documents_docID";
	public static final String Index_documents_URL = "documents_URL";
	public static final String Index_features_docID = "features_docID";
	public static final String Index_features_wordID = "features_wordID";
	public static final String Index_features_term = "features_term";
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
	public static final String Col_position = "position";
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

	//Table Images
	public static final String Col_image = "image";
	public static final String Col_pageindex = "pageindex";
	public static final String Col_altText = "alt";
	public static final String Col_src = "src";
	public static final String Col_type = "type";

	//Table Shingle
	public static final String Col_shingle = "shingle";
	public static final String Col_hash = "hash";

	//Table Jaccard
	public static final String Col_doc1 = "doc1";
	public static final String Col_doc2 = "doc2";
	public static final String Col_jaccard = "jaccard";

	//Table ads
	public static final String Col_ad_id = "ad_id";
	public static final String Col_user = "users";
	public static final String Col_n_grams = "ngrams";
	public static final String Col_description = "description";
	public static final String Col_budget = "budget";
	public static final String Col_click_left = "click_left";
	public static final String Col_cost_per_click = "cost_per_click";
	public static final String Col_at_time = "at_time";
	public static final String Col_ip = "ip";
	public static final String Col_ad_image = "image";

	//Table searchengines
	public static final String Col_id = "id";
	public static final String Col_active = "active";
	public static final String Col_queryKeyword = "queryWord";
	public static final String Col_kKeyword = "kWord";
	public static final String Col_querySeperator = "querySeperator";
	public static final String Col_addConf = "addConfig";
	public static final String Col_df = "df";
	public static final String Col_cw = "cw";

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
			System.out.println("DocCount : " + createTableDocCount(false));
			System.out.println("Images : " + createTableImages(false));
			System.out.println("Shingles : " + createTableShingle(false));
			System.out.println("Jaccard : " + createTableJaccard(false));
			System.out.println("Searchengines : " + createTableSearchengines(false));
			System.out.println("Searchengine Terms : " + createTableSearchengineTerms(false));
			System.out.println("Ad clicks : " + createTableAdClicks(false) );
			System.out.println("Ads : " + createTableAds(true));
		} else {
			createTableDocuments(false);
			createTableFeatures(false);
			createViewFeaturesTFIDF(false);
			createViewFeaturesBm25(false);
			createTableLinks(false);
			createTableQueue(false);
			createTableCrawlRequest(false);
			createTableDocCount(false);
			createTableImages(false);
			createTableShingle(false);
			createTableJaccard(false);
			createTableSearchengines(false);
			createTableSearchengineTerms(false);
			createTableAdClicks(false);
			createTableAds(true);
		}
	}

	public static void createViews() {
		if (isLogger) {
			System.out.println("Features_TFIDF : "+ createViewFeaturesTFIDF(false));
			System.out.println("Features_BM25 : "+ createViewFeaturesBm25(false));
			System.out.println("Features_BM25_Pagerank : "+ createViewFeaturesBm25Pagerank(true));
		} else {
			createViewFeaturesTFIDF(false);
			createViewFeaturesBm25(false);
			createViewFeaturesBm25Pagerank(true);
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
			System.out.println("Index " + Index_features_term+": " + createFeaturesTermIndex(false));
			System.out.println("Index " + Index_features_language + ":" + createFeaturesLanguageIndex(true));
		} else {
			createQueueDocIDIndex(false);
			createQueueURLIndex(false);
			createQueueDepthIndex(false);
			createDocumentsDocIdIndex(false);
			createDocumentsURLIndex(false);
			createFeaturesDocIdIndex(false);
			createFeaturesWordIdIndex(true);
			createFeaturesTermIndex(false);
			createFeaturesLanguageIndex(true);
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
			System.out.println("Score Function : " + calculateScores(false));
			System.out.println("DocPairs : "+ createViewDocPairs(false));
			System.out.println("Jaccard Function : " + calculateJaccard(false));
			System.out.println("SimilarDocuments Function : " + similarDocuments(false));
			System.out.println("Calculate BM25 Pagerank Score Function : " + calculatebm25pagerank(false));
			System.out.println("engine count Function : " + engine_count(false));
			System.out.println("CF Function : " + cf(false));
			System.out.println("avg cw: " + avg_cw(false));
			System.out.println("Calculate BM25 Pagerank Score Function : " + calculatebm25pagerank(true));
		} else {
			calculateDocCountFunction(false);
			calculateDocTermCountFunction(false);
			calculateIDFFunction(false);
			getDocLength(false);
			getAvgDocLength(false);
			getPagerankWeight(false);
			getBM25Weight(false);
			calculateScores(false);
			createViewDocPairs(false);
			calculateJaccard(false);
			similarDocuments(false);
			engine_count(false);
			cf(false);
			avg_cw(false);
			calculatebm25pagerank(true);
		}
	}
	
	public static void initializeDatabase() {
		createTables();
		createViews();
		createFunctions();
	}
	
	private static boolean createTableDocCount(boolean closeDb){
		return createTable("CREATE TABLE IF NOT EXISTS " +
				Table_Doc_Count + "(" 
				+ DML.Col_wordId + " int PRIMARY key, "
				+ Col_count+ " int);",closeDb);
	}
	private static boolean createTableJaccard(boolean closeDb){
		return createTable("CREATE TABLE IF NOT EXISTS " +
				Table_Jaccard + "("
				+ Col_doc1 + " int , "
				+ Col_doc2+ " int,"
				+ Col_jaccard + " double precision , "
				+ "CONSTRAINT JaccardPK PRIMARY KEY("+Col_doc1+","+Col_doc2+"))",closeDb);
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

	private static boolean createTableImages(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS "+Table_Images+" ("
				+ Col_docId +" integer, "
				+ Col_pageindex +" integer, "
				+ Col_position + " integer, "
				+ Col_src + " text, "
				+ Col_altText + " text, "
				+ Col_type + " varchar, "
				+ Col_image + " text NOT NULL, "
				+ "CONSTRAINT imagePK PRIMARY KEY("+Col_docId+","+Col_pageindex+"))",closeDb);
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

	private static boolean createViewDocPairs(boolean closeDb) {
		return createTable("CREATE OR REPLACE VIEW "+View_DocPairs
				+" AS SELECT d1."+Col_docId+" AS doc1, d2."+Col_docId+" AS doc2 FROM "+Table_Documents
				+" d1 CROSS JOIN "+Table_Documents+" d2 WHERE d1."+Col_docId+" <> d2."+Col_docId, closeDb);
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

	private static boolean createTableAds(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_ad + " ( "
				+ Col_ad_id + " serial primary key, "
				+ Col_user + " text, "
				+ Col_n_grams + " text, "
				+ Col_url + " text, "
				+ Col_description + " text, "
				+ Col_ad_image + " text, "
				+ Col_click_left + " INTEGER, "
				+ Col_cost_per_click + " NUMERIC, "
				+ Col_budget + " NUMERIC) " , closeDb);
	}
	
	private static boolean createTableAdClicks(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_ad_click + " ( "
				+ Col_ad_id + " INTEGER, "
				+ Col_ip + " varchar(20), "
				+ Col_at_time + " timestamp DEFAULT ('now'::text)::timestamp) " , closeDb);
	}

	// QUE allowed to go out of domain column can be added?
	private static boolean createTableQueue(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Crawler_queue + " ( "
				+ Col_docId + " INTEGER, "
				+ Col_url + " varchar(400) UNIQUE, "
				+ Col_depth +" INTEGER, "
				+ Col_visited +" BOOLEAN DEFAULT FALSE)", closeDb);
	}
	
	private static boolean createTableCrawlRequest(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Crawl_Request + " ( "
				+ Col_docId + " INTEGER, "
				+ Col_url + " varchar(400), "
				+ Col_depth +" INTEGER, "
				+ Col_max_doc +" INTEGER, "
				+ Col_leave_domain + " BOOLEAN, "
				+ Col_crawl_on_timestamp + " timestamp DEFAULT ('now'::text)::timestamp, "
				+ Col_visited +" BOOLEAN DEFAULT FALSE)", closeDb);
	}

	//
	private static boolean createTableShingle(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_shingles + " ( "
				+ Col_docId + " INTEGER, "
				+ Col_shingle + " varchar, "
				+ Col_hash +" INTEGER, "
				+"CONSTRAINT shinglePK PRIMARY KEY("+Col_docId+","+Col_hash+"))", closeDb);
	}

	private static boolean createTableSearchengines(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Searchengines + " ( "
				+ Col_id + " INTEGER PRIMARY KEY, "
				+ Col_url + " varchar, "
				+ Col_queryKeyword + " varchar, "
				+ Col_querySeperator + " varchar, "
				+ Col_kKeyword + " varchar, "
				+ Col_addConf + " varchar, "
				+ Col_active +" BOOLEAN,"
				+ Col_cw + " INTEGER DEFAULT -1)",closeDb);
	}

	private static boolean createTableSearchengineTerms(boolean closeDb) {
		return createTable("CREATE TABLE IF NOT EXISTS " + Table_Searchengine_terms + " ( "
				+ Col_id + " INTEGER , "
				+ Col_hash + " INTEGER, "
				+ Col_df + " INTEGER , "
				+"CONSTRAINT engineTerms PRIMARY KEY("+Col_id+","+Col_hash+"))",closeDb);
	}

	private static boolean calculateDocCountFunction(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_DOC_Count+"() RETURNS bigint LANGUAGE SQL " +
				"AS $$ SELECT COUNT(" + Table_Documents +"." + Col_docId + ") " +
				"FROM " + Table_Documents + " $$", closeDb);
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

	private static boolean avg_cw(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_avg_cw+"() RETURNS NUMERIC LANGUAGE SQL STABLE " +
				"AS $$ SELECT AVG("+Col_cw+")" +
				" FROM " + Table_Searchengines +  "; $$",closeDb);
	}

	private static boolean engine_count(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_engine_count+"() RETURNS BIGINT LANGUAGE SQL STABLE " +
				"AS $$ SELECT COUNT(*)" +
				" FROM " + Table_Searchengines +  "; $$",closeDb);
	}

	private static boolean cf(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_cf+"(term_id int) RETURNS NUMERIC LANGUAGE SQL STABLE " +
				"AS $$ SELECT (1.0*engine_count())/COUNT(*)" +
				" FROM " + Table_Searchengine_terms  +  " WHERE "+DML.Col_hash+"=term_id; $$",closeDb);
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

	private static boolean calculateJaccard(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_calculate_jaccard+"() RETURNS BOOLEAN LANGUAGE SQL " +
				"AS $$  INSERT INTO "+Table_Jaccard+" "
				+ "SELECT "+Col_doc1+", "+Col_doc2+", (CAST(intersection AS double precision)/ unio) AS jaccard "
				+ "FROM (SELECT "+Col_doc1+", "+Col_doc2+", "
				+ "(SELECT COUNT("+Col_hash+") FROM "+Table_shingles+" WHERE "+Table_shingles+"."+Col_docId+" = doc1 "
				+ "OR "+Table_shingles+"."+Col_docId+" = doc2) AS unio, "
				+ " (SELECT COUNT(s1."+Col_hash+") FROM "+Table_shingles+" s1, "+Table_shingles+" s2 "
				+ " WHERE s1."+Col_docId+" = doc1 AND s2."+Col_docId+" = doc2 AND s1."+Col_hash+" = s2."+Col_hash+") AS intersection "
				+ "FROM docPairs GROUP BY "+Col_doc1+","+Col_doc2+") AS pairs "
				+ " RETURNING TRUE $$", closeDb);
	}

	private static boolean similarDocuments(boolean closeDb){
		return createTable("CREATE OR REPLACE FUNCTION "+Function_similarDocuments+"(integer, double precision) RETURNS " +
				"TABLE("+Col_docId+" int, "+Col_jaccard+" double precision) LANGUAGE SQL " +
				"AS $$  SELECT "+Col_doc2+" AS "+Col_docId+", "+Col_jaccard+" FROM "+Table_Jaccard+" " +
				"WHERE "+Col_doc1+" = $1 AND "+Col_jaccard+" >= $2 $$", closeDb);
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
	private static boolean createFeaturesTermIndex(boolean closeDb){
		return executeSQL("CREATE INDEX "+ Index_features_term + " ON " + Table_Features + " " +
				"USING hash ( " + Col_term +") ",false, closeDb);
	}



	private static boolean executeSQL(String sql, boolean printException, boolean closeDb) {
		System.out.println(sql);
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
				+ ", " + Table_Images
				+ ", " + Table_shingles
				+ ", " + Table_Jaccard
				+ ", " + Table_Searchengines
				+ ", " + Table_Searchengine_terms
				+ ", " + Table_ad
				+ ", " + Table_ad_click
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
				+ "; DROP FUNCTION  IF EXISTS " + Function_Doc_Length + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_similarDocuments + "(integer, double precision)"
				+ "; DROP FUNCTION  IF EXISTS " + Function_calculate_jaccard + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_engine_count + "()"
				+ "; DROP FUNCTION  IF EXISTS " + Function_cf + "(integer)"
				+ "; DROP FUNCTION  IF EXISTS " + Function_avg_cw + "()";
		System.out.println(query);
		executeSQL(query, true, true);
	}

	public static void main (String[] args) throws SQLException {
		dropAllTable();
		dropAllFunction();
		createTables();
		createFunctions();
		createViews();
	}
}