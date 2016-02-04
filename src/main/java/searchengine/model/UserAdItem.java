package searchengine.model;

public class UserAdItem extends AdItem {

	String username;
	int click_left;
	double bugdet;
	double costPerClick;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getClick_left() {
		return click_left;
	}

	public void setClick_left(int click_left) {
		this.click_left = click_left;
	}

	public double getBugdet() {
		return bugdet;
	}

	public void setBugdet(double bugdet) {
		this.bugdet = bugdet;
	}

	public double getCostPerClick() {
		return costPerClick;
	}

	public void setCostPerClick(double costPerClick) {
		this.costPerClick = costPerClick;
	}

}
