package org.wordup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
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
 * This class is used for main page recommendation
 * @author jialingliu
 *
 */
public class recommend extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public recommend() {
        super();
    }
	// request form: searchgroup?words=word1,word2,word3,word4,...word10
	// write form: JSONObject.toString()
	// {word, definition, partOfSpeech, example}
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	String[] words = new String[10];
    	try {
    		words = request.getParameter("words").split(",");
    	} catch (NullPointerException e) {}
		int n = words.length;
		Random rand = new Random();
        String word = words[rand.nextInt(n)];
        String description = queryFromDB(word);
//        System.out.println("description="+description);
		if ("".equals(description) || description == null) {
			description = queryFromApi(word);
			insertIntoDB(word, description);
		}
//		System.out.println(word);
//      System.out.println("returned description="+description);
		response.getWriter().append(description);
	}
    
	private String queryFromDB(String word) {
		try {
			word = word.replace("'", "\\'");
		} catch (NullPointerException e) {
			return "No Such Word";
		}
		String description = "";
		String sql = String.format("SELECT description FROM rec WHERE word = \'%s\' LIMIT 1", word);
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
	private void insertIntoDB(String word, String description) {
		word = word.replace("'", "\\'");
		description = description.replace("'", "\\'");
		String sql = String.format("insert into wordup.rec (word, description) values (\'%s\', \'%s\')", word, description);
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
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

            JSONObject want = new JSONObject();
            want.put("word", word);
            for (Object o : results) {
                JSONObject jo = (JSONObject) o;
                //JSONObject newJo = new JSONObject();
                String definition = "";
                String partOfSpeech = "";
                String example = "";
                try {
                    definition = jo.getString("definition");
                    want.put("explanation", definition);
                } catch (JSONException e) {
                    want.put("explanation", "");
                }
                try {
                    partOfSpeech = jo.getString("partOfSpeech");
                    want.put("speech", partOfSpeech);
                } catch (JSONException e) {
                    want.put("speech", "");
                }
                try {
                    example = jo.getJSONArray("examples").getString(0);
                    want.put("example", example);
                } catch (JSONException e) {
                    want.put("example", "");
                }
                if (definition.length() != 0 && partOfSpeech.length() != 0 && example.length() != 0) {
                    return want.toString();
                }
            }
        } catch (Exception e) {}
        return "No Such Word";
	}
}
