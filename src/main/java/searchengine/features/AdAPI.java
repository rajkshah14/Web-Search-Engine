package searchengine.features;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import searchengine.misc.Log;
import searchengine.misc.Parameters;
import searchengine.misc.db.DBConfig;
import searchengine.misc.db.DML;
import searchengine.model.AdClickItem;
import searchengine.model.AdItem;
import searchengine.model.UserAdItem;

public class AdAPI {
	
	public List<AdItem> getAds(String query) {
		List<AdItem> ads = new ArrayList<AdItem>();
		if (query.length() < 2)
			return ads;
		
		String sql = " SELECT * FROM " + DML.Table_ad 
				+ " WHERE " + DML.Col_n_grams + " LIKE ANY " + getClause(query) 
				+ " AND " + DML.Col_click_left + " > 0";
		
		try {
			Connection con = DBConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				AdItem item = new AdItem();
				item.setId(rs.getString(DML.Col_ad_id));
				item.setDescription(rs.getString(DML.Col_description));
				item.setUrl(rs.getString(DML.Col_url));
				item.setImage(rs.getString(DML.Col_ad_image));
				item.setNgrams(rs.getString(DML.Col_n_grams));
				item.calculateScore(query);
				ads.add(item);
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		Collections.sort(ads, new Comparator<AdItem>() {
			@Override
			public int compare(AdItem o1, AdItem o2) {
				return o2.getScore() - o1.getScore();
			}
		});

		if (ads.size()>4)
			while (ads.size()>4)
				ads.remove(ads.size()-1);
		return ads;
	}
	
	public List<UserAdItem> getUserAdOverview(String username) {
		List<UserAdItem> items = new ArrayList<UserAdItem>();
		Connection con = DBConfig.getConnection();
		
		try {
			PreparedStatement ps = con.prepareStatement("Select * from " + DML.Table_ad + " WHERE " + DML.Col_user + " = '" + username +"'");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				UserAdItem item = new UserAdItem();
				item.setUsername(username);
				item.setBugdet(rs.getDouble(DML.Col_budget));
				item.setUrl(rs.getString(DML.Col_url));
				item.setClick_left(rs.getInt(DML.Col_click_left));
				item.setCostPerClick(rs.getDouble(DML.Col_cost_per_click));
				item.setDescription(rs.getString(DML.Col_description));
				item.setId(rs.getString(DML.Col_ad_id));
				items.add(item);
			}
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}
	
	public List<AdClickItem> getAdClicks(String id) {
		List<AdClickItem> clicks = new ArrayList<AdClickItem>();
		Connection con = DBConfig.getConnection();
		
		try {
			PreparedStatement ps = con.prepareStatement("Select * from " + DML.Table_ad_click + 
					" WHERE " + DML.Col_ad_id + " = '" + id + "' limit 100");
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				AdClickItem item = new AdClickItem();
				item.setIp(rs.getString(DML.Col_ip));
				item.setTimestamp(rs.getString(DML.Col_at_time));
				clicks.add(item);
			}
			con.close();
		} catch (Exception e) {
			
		}
		return clicks;
	}
	
	public void onAdClick(String id, String ipAddress) {
		Connection con = DBConfig.getConnection(true);
		try {
			String sql = "Update "  + DML.Table_ad + 
					" SET " + DML.Col_click_left + " = " + DML.Col_click_left + " - 1" +
					" WHERE " + DML.Col_ad_id + " = " + id + ";" +
					
					"Insert into " + DML.Table_ad_click + 
					" ( " + DML.Col_ad_id + " , " + DML.Col_ip + 
					" ) Values  ('" + id + "','" + ipAddress + "')";
			System.out.println(sql);
			PreparedStatement ps = con.prepareStatement(sql);
			ps.execute();
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String getClause(String query) {
		StringBuilder builder = new StringBuilder();
		builder.append(" ('{");
		String[] terms = query.split(" ");

		for (String s : Arrays.asList(terms)) {
			builder.append("\"%");
			builder.append(s);
			builder.append("%\",");
		}
		builder.deleteCharAt(builder.length()-1);
		builder.append("}') ");
		return builder.toString();
	}
	
	public boolean register(String username, String ngrams, String url, String description, 
			double budget, double costPerClick) {
		Connection con = DBConfig.getConnection(); 
		
		try {
			int clickLeft = (int)(budget/Parameters.COSTPERCLICK);
			String sql = "insert into " + DML.Table_ad + " (" 
					+ DML.Col_user + ", " 
					+ DML.Col_n_grams + ", " 
					+ DML.Col_url + ", "
					+ DML.Col_description + ","
					+ DML.Col_click_left + ","
					+ DML.Col_cost_per_click + ", "
					+ DML.Col_budget +  ", "
					+ DML.Col_ad_image + ")" 
					+ " values ('" + username + "', '" + ngrams + "','" 
					+ url + "','" + description + "'," + clickLeft + "," 
					+ costPerClick + "," + budget + ",'') ";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.execute();
			con.commit();
			con.close();
		} catch (Exception e) {
			Log.logException(e);
			return false;
		}
		return true;
	}
	
	public boolean register(String username, String ngrams, String url, String description, 
			double budget, double costPerClick, String image) {
		Connection con = DBConfig.getConnection(); 
		try {
			int clickLeft = (int)(budget/Parameters.COSTPERCLICK);
			String sql = "insert into " + DML.Table_ad + " (" 
					+ DML.Col_user + ", " 
					+ DML.Col_n_grams + ", " 
					+ DML.Col_url + ", "
					+ DML.Col_description + ","
					+ DML.Col_click_left + ","
					+ DML.Col_cost_per_click + ", "
					+ DML.Col_budget + ", "
					+ DML.Col_ad_image + ")" 
					+ " values ('" + username + "', '" + ngrams + "','" 
					+ url + "','" + description + "'," + clickLeft + "," 
					+ costPerClick + "," + budget + ",'" + image + "') ";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.execute();
			con.commit();
			con.close();
		} catch (Exception e) {
			Log.logException(e);
			return false;
		}
		return true;
	}
	
	public static void main (String[] args) {
		new AdAPI().getAds("android apple news usa");
//		register("engadget", "mobile computer game news technology", "http://www.engadget.com/", "Engadget | Technology News, Advice and Features", 20, 0.02);
//		register("engadget", "computer game ", "http://www.engadget.com/gaming/", "Engadget | Gaming articles, stories, news and information.", 10, 0.02);
//		register("engadget", "technology science ", "http://www.engadget.com/science/", "Engadget | Gaming articles, stories, news and information.", 10, 0.02);
//		
//		register("pcworld", "pcworld news reveiw computer server laptop", "http://www.pcworld.com", "PCWorld - News, tips and reviews from the experts on PCs, Windows, and more", 15, 0.30);
//		register("pcworld", "pcworld windows mac laptop", "http://www.pcworld.com/category/laptop-computers/", "Laptops reviews, how to advice, and news", 15, 0.30);
//		register("pcworld", "pcworld android tablet ipad", "http://www.pcworld.com/category/tablets/", "Tablets reviews, how to advice, and news", 15, 0.30);
//		register("pcworld", "pcworld android mobile iphone", "http://www.pcworld.com/category/phones/", "Phones reviews, how to advice, and news", 15, 0.30);
//		register("pcworld", "pcworld android ios gadget", "http://www.pcworld.com/category/gadgets/", "Gadgets reviews, how to advice, and news", 15, 0.30);
//		
//		register("nytimes","usa newyork times news","http://www.nytimes.com/", "The New York Times - Breaking News, World News & Multimedia", 5,0.3,"https://pbs.twimg.com/profile_images/2044921128/finals_400x400.png");
//		register("nytimes","mobile computer security network news","http://www.nytimes.com/pages/technology/index.html", "The New York Times - Technology", 5,0.3, "https://pbs.twimg.com/profile_images/2044921128/finals_400x400.png");
//		register("nytimes","health medicine news","http://www.nytimes.com/pages/health/index.html", "The New York Times - Health", 5,0.3, "https://pbs.twimg.com/profile_images/2044921128/finals_400x400.png");
		
		
	}

}
