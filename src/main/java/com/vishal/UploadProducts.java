package com.vishal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.vishal.dao.DatastoreDao;
import com.vishal.model.Product;

@SuppressWarnings("serial")
public class UploadProducts extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String i = request.getParameter("i");

		Queue queue = QueueFactory.getQueue("products-queue");
		JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(new File(getClass().getClassLoader().getResource("test.json").getFile())), "UTF-8"));
		Gson gson = new GsonBuilder().create();

		// Read file in stream mode
        reader.beginArray();
        while (reader.hasNext()) {
            // Read data into object model and queue it as a defered task
            Product product = gson.fromJson(reader, Product.class);
            if (product.getName() != null) {
            		queue.add(TaskOptions.Builder.withPayload(new DeferedProductAdd(product)));
            }
        }
        reader.close();
		
		response.getWriter().println("added all tasks on default queue!");
		response.getWriter().close();
	}
	
	public static class DeferedProductAdd implements DeferredTask {

		private Product product;
		
		private DeferedProductAdd(Product product) {
			this.product = product;
		}

		@Override
		public void run() {
			// Add product passed to defered task to DataStore
			DatastoreDao dao = new DatastoreDao();
			dao.createProduct(product);
		}
	}
}
