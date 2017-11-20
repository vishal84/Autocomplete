package com.vishal;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.vishal.dao.DatastoreDao;

@SuppressWarnings("serial")
public class GetProducts extends HttpServlet {

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
	    
		String term = request.getParameter("term");
	    DatastoreDao dao = new DatastoreDao();

	    try {
	      String productNames = dao.getProducts(term);
	      
	      response.setContentType("application/json");
	      response.setCharacterEncoding("UTF-8");
	      response.getWriter().write(productNames);
	      
	    } catch (Exception e) {
	      throw new ServletException("Error getting product(s)", e);
	    }
	  }
}
