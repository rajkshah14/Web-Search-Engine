package project08.model;

import java.util.ArrayList;
import java.util.List;

import project08.indexer.Stemmer;
import project08.misc.Parameters;

public class ImageSearchResultItem extends SearchResultItem {

	int pageIndex, position;
	String extension, alt_text, image;
	double imageBasedScore;

	public ImageSearchResultItem() {
		super();
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getAlt_text() {
		return alt_text;
	}

	public void setAlt_text(String alt_text) {
		this.alt_text = alt_text;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public double getImageBasedScore() {
		return imageBasedScore;
	}

	public void copy(SearchResultItem page) {
		setTitle(page.getTitle());
		setContent_snap(page.getContent_snap());
		setUrl(page.getUrl());
		setRank(page.getRank());
		setTitle(page.getTitle());
		setRank(page.getRank());

	}

	public void calculateScore(String query) {
		
		Stemmer stemmer = new Stemmer();
		String text = stemmer.stemString(getContent_snap());
		String[] features = query.split(" ");

		List<Integer> positions = new ArrayList<Integer>(); // contains position of all words of all features occuring in page

		for (int i = 0; i < features.length; i++) {
			int lastIndex = 0;
			int count = 0;
			
			while (lastIndex != -1) {
				lastIndex = text.indexOf(stemmer.stemString(features[0]), lastIndex);

				if (lastIndex != -1) {
					count++;
					positions.add(lastIndex);
					lastIndex += stemmer.stemString(features[0]).length();
				}
			}

		}
		
		if (positions.size() < 1 ) {
			imageBasedScore = 0;
		} else  {
		
			// find out least position of query word from image
			int best = 0, best_difference = -1;
			for (int i = 0; i < positions.size(); i++) {
				best_difference = Math.abs(positions.get(best) - position);
				int cur_difference = Math.abs(positions.get(i) - position);
				
				if (cur_difference < best_difference )
					best = i;			
			}
			if (best_difference == -1 || best_difference > Parameters.Image_window_size) {
				imageBasedScore = 0;
			} else {
				float x = best_difference/text.length();
				float lambda = Parameters.image_lambda;
				imageBasedScore = lambda * Math.exp(- lambda * x);
				
				float weight = Parameters.image_score_weight;
				imageBasedScore = (imageBasedScore * weight + getTf_idf() * (1-weight))/2;
			}	
		}
	}
}
