package searchengine.model;

import searchengine.misc.CommonFunction;
import searchengine.misc.Parameters;

public class AdItem {

	String id;
	String url;
	String description;
	String clickURL;
	String image;
	String ngrams;
	int score = 0;
	
	public AdItem () {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		setClickURL(url);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getClickURL() {
		return clickURL;
	}

	public void setClickURL(String e) {
		clickURL = "/project08/adclick?id=" + id + "&url=" + e;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getNgrams() {
		return ngrams;
	}

	public void setNgrams(String ngrams) {
		this.ngrams = ngrams;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
	public void calculateScore(String query) {
		String[] ngrams = getNgrams().split(" ");
		String[] terms = query.split(" ");
		
		for (int i = 0; i < ngrams.length; i++) {
			for (int j = 0; j < terms.length; j++) {
				if (Parameters.ad_k > CommonFunction.levenshteinDistance(ngrams[i], terms[j])) {
					score++;
				}		
			}
		}	
	}

}
