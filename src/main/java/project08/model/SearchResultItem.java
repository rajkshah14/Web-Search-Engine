package project08.model;

public class SearchResultItem {

	private final int content_snap_size = 800;
	private int rank;
	private String url;
	private double tf_idf;
	private String title;
	private String content_snap;

	public SearchResultItem() {
		super();
	}

	public SearchResultItem(int rank, String url, double tf_idf) {
		super();
		this.rank = rank;
		this.url = url;
		this.tf_idf = tf_idf;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getTf_idf() {
		return tf_idf;
	}

	public void setTf_idf(double tf_idf) {
		this.tf_idf = tf_idf;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent_snap() {
		return content_snap;
	}

	public void setContent_snap(String content_snap) {
		this.content_snap = content_snap.replaceAll("(?<=.{"+content_snap_size+"})\\b.*", "...");;
	}

	@Override
	public String toString() {
		return "Rank " + getRank() + ". Score " + getTf_idf() + ": " + getUrl();
	}
}
