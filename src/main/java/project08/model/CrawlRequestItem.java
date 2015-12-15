package project08.model;

public class CrawlRequestItem {

	private int docId;
	public String url;
	public int depth;
	public int max_doc;
	public boolean leave_domain;
	public String timeStamp;
	public boolean visited;

	public CrawlRequestItem() {
		super();
	}
	
	public CrawlRequestItem(int docId, String url, int depth, int max_doc,
			boolean leave_domain, String timeStamp, boolean visited) {
		super();
		this.docId = docId;
		this.url = url;
		this.depth = depth;
		this.max_doc = max_doc;
		this.leave_domain = leave_domain;
		this.timeStamp = timeStamp;
		this.visited = visited;
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getMax_doc() {
		return max_doc;
	}

	public void setMax_doc(int max_doc) {
		this.max_doc = max_doc;
	}

	public boolean isLeave_domain() {
		return leave_domain;
	}

	public void setLeave_domain(boolean leave_domain) {
		this.leave_domain = leave_domain;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	public void process() {
		System.out.println("Request Received");
	}

}
