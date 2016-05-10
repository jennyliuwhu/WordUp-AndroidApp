package org.wordup;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
/**
 * 
 * @author jialingliu
 * This class is used for register
 */
public class register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public register() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Enumeration<String> paramNames = request.getParameterNames();
		if (paramNames.hasMoreElements()) {
			String info = (String) paramNames.nextElement();
			String[] paramValues = info.split(",");
			if (paramValues.length == 2){
				String sql = "insert into wordup.user (name,password) values ('"+paramValues[0]+"','"+paramValues[1]+"')";
//				System.out.println(sql);
				Connection con = DBConnectionHandler.getConnection();
				try {
					PreparedStatement ps = con.prepareStatement(sql);
					ps.execute();
					if(ps!=null){
						ps.close();
					}
					con.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}     
		}		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
