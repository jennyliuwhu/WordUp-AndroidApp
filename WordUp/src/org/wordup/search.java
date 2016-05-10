package org.wordup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
/**
 * This class is used for searching word
 * @author jialingliu
 *
 */
public class search extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public search() {
		super();
	}
	// request form: search?word=luminous
	// write form: JSONArray.toString()
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String word = request.getParameter("word");
		String description = queryFromDB(word);
		if ("".equals(description) || description == null) {
			description = queryFromApi(word);
			insertIntoDB(word, description);
		}
		response.getWriter().append(description);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void insertIntoDB(String word, String description) {
		word = word.replace("'", "\\'");
		description = description.replace("'", "\\'");
		String sql = String.format("insert into wordup.words (word, description) values (\'%s\', \'%s\')", word, description);
//		System.out.println(sql);
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBConnectionHandler.getConnection();
			// create the java statement
			stmt = con.createStatement();
			// execute the query
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {}
			}
		}
	}
	
	private String queryFromDB(String word) {
		try {
			word = word.replace("'", "\\'");
		} catch (NullPointerException e) {
			return "No Such Word";
		}
		String description = "";
		String sql = "select description from wordup.words where word = \'"+word+"\'";
//		System.out.println(sql);
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			con = DBConnectionHandler.getConnection();
			// create the java statement
			stmt = con.createStatement();
			// execute the query
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				description = rs.getString("description");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {}
			}
			if (con != null) {
				try {
					con.close();
				} catch (Exception e) {}
			}
		}
		
		return description.replace("\\'", "'");
	}
	private String queryFromApi(String word) {
		HttpResponse<JsonNode> response;
        try {
            response = Unirest.get("https://wordsapiv1.p.mashape.com/words/" + word)
                    .header("X-Mashape-Key", note.key)
                    .header("Accept", "application/json")
                    .asJson();
            JSONObject jsonObj = new JSONObject(response);
            JSONObject body = jsonObj.getJSONObject("body");
            JSONArray array = body.getJSONArray("array");
            JSONObject obj = array.getJSONObject(0);
            JSONArray results = obj.getJSONArray("results");

            JSONArray want = new JSONArray();
            want.put(0, word);
            for (Object o : results) {
                JSONObject jo = (JSONObject) o;
                JSONObject newJo = new JSONObject();
                try {
                    newJo.put("definition", jo.getString("definition"));
                } catch (JSONException e) {
                    newJo.put("definition", "");
                }
                try {
                    newJo.put("partOfSpeech", jo.getString("partOfSpeech"));
                } catch (JSONException e) {
                    newJo.put("partOfSpeech", "");
                }
                try {
                    newJo.put("example", jo.getJSONArray("examples").getString(0));
                } catch (JSONException e) {
                    newJo.put("example", "");
                }
                want.put(newJo);
            }
            return want.toString();
        } catch (Exception e) {}
        return "No Such Word";
	}
}