package searchengine.algorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.la4j.Matrix;
import org.la4j.Vector;
import org.la4j.Vectors;
import org.la4j.matrix.DenseMatrix;
import org.la4j.vector.dense.BasicVector;

import searchengine.misc.Parameters;
import searchengine.misc.db.DBConfig;

public class PageRank {

	static final String query_select_unique_doc_id = "SELECT DISTINCT doc_id FROM documents";
	static final String query_select_links = "SELECT * FROM links where to_docid IN (SELECT DISTINCT doc_id from documents) AND from_docid IN (SELECT DISTINCT doc_id from documents)";
	HashMap<Integer,Integer> doc_ids;
	Matrix transactionMatrix;
	
	private void getAllDocID(Connection conn) {
		doc_ids = new HashMap<Integer, Integer>();
		int i = 0;
		try {
			PreparedStatement ps = conn.prepareStatement(query_select_unique_doc_id);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				int docId = rs.getInt("doc_id");
				doc_ids.put(docId,i++);
			}
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void generateAdjacencyMatrix(Connection conn) {
		int matrSize = doc_ids.size();
		transactionMatrix = DenseMatrix.zero(matrSize, matrSize);
		int[][] links = null;
		try {
			System.out.println("Getting Links");
			PreparedStatement ps = conn.prepareStatement(query_select_links,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = ps.executeQuery();
			rs.last();
			links = new int[rs.getRow()][2];
			int i = 0;
			rs.beforeFirst();
			while (rs.next()) {
				links[i][0] = rs.getInt("from_docid");
				links[i][1] = rs.getInt("to_docid");
				i++;
			}
			ps.close();
			System.out.println("Got Links");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (links == null)
			return;
		
		int links_count = links.length;

		System.out.println("Starting link init of " + links_count + " Links");
		for (int i = 0; i < links_count; i++) {
			System.out.println("Initializing link " + i + " of " + links_count );
			int from_docId = links[i][0];
			int to_docId = links[i][1];
			Integer from_pos = doc_ids.get(from_docId);
			Integer to_pos = doc_ids.get(to_docId);
			if(from_pos != null && to_pos != null) {
				transactionMatrix.set(from_pos, to_pos, transactionMatrix.get(from_pos,to_pos)+1);
			}
		}

		//Calculate transition probability
		for(int i = 0; i < matrSize; i++) {
			System.out.println("Calculating row " + i + " of " + matrSize );
			Vector row = transactionMatrix.getRow(i);
			double outDegree = row.sum();
			if (outDegree > 0) {
				//Calculate LinkProbability
				row = row.divide(outDegree);
				row = row.multiply((1 - Parameters.pagerank_alpha));
				//Add random Jump
				row = row.add(Parameters.pagerank_alpha/matrSize);
			}else //If no outgoing links, jump to every page with same probability
			{
				row = row.add(1.0/matrSize);
			}

			transactionMatrix.setRow(i, row);
		}

/*		//Normalize rows
		for (int i = 0; i < links_count; i++) {
			Vector row = transactionMatrix.getRow(i);
			double rowSum = row.sum();
			if (rowSum!=0) {
				row = row.divide(rowSum);
				transactionMatrix.setRow(i, row);
			}
		}*/
		
//		for (int i = 0; i < links_count; i++) {
//			for (int j = 0; j <links_count; j++)
//				if (0.0!=transactionMatrix.get(i, j))
//					System.out.println(""+transactionMatrix.get(i, j));
//		}
	}

	private void calculatePageRank(Connection con)
	{
		if (doc_ids.size() < 1)
			return;
		Vector rank = BasicVector.zero(doc_ids.size());
		rank.set(0, 1f);

		double distance = 1f;
		int iterations = 0;
		while( iterations < 60 || distance > 0.001)
		{
			iterations++;
			System.out.println("Iterating algorithm " + iterations + " of 60 times. Distance: " + distance);

			Vector newRank = rank.multiply(transactionMatrix);
			distance = rank.subtract(newRank).fold(Vectors.mkManhattanNormAccumulator());
			rank = newRank;
		}

		PreparedStatement stmt = null;
		PreparedStatement stmtfeatures = null;
		try {
			System.out.println("Updating DB");
			stmt = con.prepareStatement("UPDATE documents set page_rank = ? WHERE doc_id = ?;");
			stmtfeatures = con.prepareStatement("UPDATE features set page_rank = ? WHERE doc_id = ?;");
			int count = 0;
			for (Map.Entry<Integer, Integer> matrixPos : doc_ids.entrySet()) {
				count++;
				stmt.setDouble(1,rank.get(matrixPos.getValue()));
				stmt.setInt(2, matrixPos.getKey());

				stmtfeatures.setDouble(1,rank.get(matrixPos.getValue()));
				stmtfeatures.setInt(2, matrixPos.getKey());

				stmt.addBatch();
				stmtfeatures.addBatch();
				if(count % 100 == 0)
				{
					stmt.executeBatch();
					stmtfeatures.executeBatch();

				}
			}
			stmt.executeBatch();
			stmtfeatures.executeBatch();

			con.commit();
			stmt.close();
			stmtfeatures.close();
			System.out.println("Updated DB");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PageRank() throws SQLException{
		Connection conn = DBConfig.getConnection();
		getAllDocID(conn);
		generateAdjacencyMatrix(conn);
		calculatePageRank(conn);
		conn.close();
	}
	
	public static void main(String[] args) throws SQLException {
		new PageRank();
	}
}
