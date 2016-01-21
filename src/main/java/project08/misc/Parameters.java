package project08.misc;

public class Parameters {

	public final static int LEVENSHTEIN_DISTANCE = 3;
	public static final int CLOSE_TERM_LIMIT = 5; //levenshtein search count
	
	public static final double BM25_K = 1.2;
	public static final double BM25_b = 0.75;
	public static final double pagerank_alpha = 0.1;
	
	public static final int Image_window_size = 500; // window is in characters
	public static final float image_lambda = 0.5f;
	public static final float image_score_weight = 0.8f;
}
