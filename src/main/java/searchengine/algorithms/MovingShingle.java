package searchengine.algorithms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import searchengine.misc.EvictingQueue;
import searchengine.misc.db.DBConfig;
import searchengine.misc.db.DML;

public class MovingShingle {
	public static final int shingleSize = 4;
	private EvictingQueue<String> content;
	private int docId;
	private Connection con;

	private PreparedStatement insertStatement;

	public MovingShingle(int docId) {
		content = new EvictingQueue<>(shingleSize);
		this.docId = docId;
		con = DBConfig.getConnection();

		try {
			insertStatement = con.prepareStatement("INSERT INTO "
					+ DML.Table_shingles + "(" + DML.Col_docId + ","
					+ DML.Col_shingle + "," + DML.Col_hash
					+ ") VALUES(?,?,?) ON CONFLICT DO NOTHING");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("resource")
	public void shingleDocument() {
		try {
			PreparedStatement ps = con.prepareStatement("SELECT "
					+ DML.Col_content_snap + " FROM " + DML.Table_Documents
					+ " WHERE " + DML.Col_docId + " = ?");

			ps.setInt(1, docId);

			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				String doc = resultSet.getString(1);
				Scanner sc = new Scanner(doc);
				while (sc.hasNext()) {
					addWord(sc.next());
				}
				close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addWord(String word) {
		content.add(word);
		if (content.isFull()) {
			saveToDB();
		}
	}

	public void close() {
		saveToDB();
		try {
			insertStatement.executeBatch();
			con.commit();
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveToDB() {
		String shingle = StringUtils.join(content, ";");
		try {

			insertStatement.setInt(1, docId);
			insertStatement.setString(2, shingle);
			insertStatement.setInt(3, shingle.hashCode());

			insertStatement.addBatch();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void shingleAllDocuments() {
		try {
			System.out.println("Calculating and saving all shingles");
			Connection connection = DBConfig.getConnection();
			PreparedStatement ps = connection.prepareStatement("SELECT "
					+ DML.Col_docId + " FROM " + DML.Table_Documents);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				MovingShingle ms = new MovingShingle(rs.getInt(1));
				ms.shingleDocument();
			}
			System.out.println("Calculating jaccard values");
			connection.createStatement().execute(
					"SELECT " + DML.Function_calculate_jaccard + "()");
			connection.commit();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		shingleAllDocuments();

		// Query for selecting all document pairs with n shingles (ordered)
		/*
		 * SELECT d.doc1 AS docl, sl.n AS nl, sl.hash as hl, d.doc2 AS docr,
		 * sr.n AS nr, sr.hash AS hr FROM docpairs d, (SELECT doc_id, hash,
		 * row_number() OVER (PARTITION BY doc_id ORDER BY hash)AS n FROM
		 * shingles) sl, (SELECT doc_id, hash, row_number() OVER (PARTITION BY
		 * doc_id ORDER BY hash)AS n FROM shingles) sr WHERE d.doc1 = sl.doc_id
		 * AND d.doc2 = sr.doc_id AND sl.n = sr.n AND sl.n <= 5 AND sr.n <= 5
		 * ORDER BY docl, docr, nl
		 */

		// Query for getting ALL Docpairs and their count/jaccard approx
		/*
		 * 
		 * SELECT doc1, doc2 , CASE WHEN count > 0 THEN count ELSE 0 END AS
		 * count, (CASE WHEN jaccard > 0.0 THEN jaccard ELSE 0.0 END) AS jaccard
		 * FROM docpairs LEFT OUTER JOIN ( SELECT docl, docr, count(hl) AS count
		 * ,(count(hl)/5.0) as jaccard FROM( SELECT d.doc1 AS docl, sl.n AS nl,
		 * sl.hash as hl, d.doc2 AS docr, sr.n AS nr, sr.hash AS hr FROM
		 * docpairs d, (SELECT doc_id, hash, row_number() OVER (PARTITION BY
		 * doc_id ORDER BY hash)AS n FROM shingles) sl, (SELECT doc_id, hash,
		 * row_number() OVER (PARTITION BY doc_id ORDER BY hash)AS n FROM
		 * shingles) sr WHERE d.doc1 = sl.doc_id AND d.doc2 = sr.doc_id AND sl.n
		 * = sr.n AND sl.n <= 5 AND sr.n <= 5 ORDER BY docl, docr, nl ) pairs
		 * WHERE hl = hr GROUP BY docl, docr ) filtered ON docpairs.doc1 =
		 * filtered.docl AND docpairs.doc2 = filtered.docr
		 */

		// Above but with arrays (right solution I think) Used for Views
		/*
		 * SELECT doc1, doc2 , CASE WHEN count > 0 THEN count ELSE 0 END AS
		 * count, (CASE WHEN jaccard > 0.0 THEN jaccard ELSE 0.0 END) AS jaccard
		 * FROM docpairs LEFT OUTER JOIN ( SELECT docl, docr, array_agg(hl) &
		 * array_agg(hr) AS inter,icount(array_agg(hl) & array_agg(hr)) AS
		 * count, (icount(array_agg(hl) & array_agg(hr))/5.0) as jaccard FROM(
		 * SELECT d.doc1 AS docl, sl.hash as hl, d.doc2 AS docr, sr.hash AS hr
		 * FROM docpairs d, (SELECT doc_id, hash, row_number() OVER (PARTITION
		 * BY doc_id ORDER BY hash)AS n FROM shingles) sl, (SELECT doc_id, hash,
		 * row_number() OVER (PARTITION BY doc_id ORDER BY hash)AS n FROM
		 * shingles) sr WHERE d.doc1 = sl.doc_id AND d.doc2 = sr.doc_id AND sl.n
		 * <= 5 AND sr.n <= 5 ORDER BY docl, docr ) pairs WHERE hl = hr GROUP BY
		 * docl, docr ) filtered ON docpairs.doc1 = filtered.docl AND
		 * docpairs.doc2 = filtered.docr order by count DESC
		 */

		// AVG, Median, first/thrid quartile
		/*
		 * SELECT AVG(error) AS avg, percentile_cont(0.5) WITHIN GROUP (ORDER BY
		 * error) as median,percentile_cont(0.25) WITHIN GROUP (ORDER BY error)
		 * AS first_quartile, percentile_cont(0.75) WITHIN GROUP (ORDER BY
		 * error) AS third_quartile FROM( SELECT j.doc1,j.doc2, abs(j.jaccard -
		 * n.jaccard) AS error FROM jaccard j, n_32 n WHERE j.doc1 = n.doc1 AND
		 * j.doc2 = n.doc2 ) err GROUP BY true
		 */
	}
}
