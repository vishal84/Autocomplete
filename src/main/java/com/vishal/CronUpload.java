package com.vishal;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

import static java.nio.charset.StandardCharsets.UTF_8;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.vishal.dao.DatastoreDao;
import com.vishal.model.Product;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.channels.Channels;


public class CronUpload extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Load file from existing Cloud Storage bucket
        // Testing this does not work locally...
        Storage storage = StorageOptions.getDefaultInstance().getService();
        BlobId blobId = BlobId.of("vish-cloud-dev.appspot.com", "products.json");

        // Read contents of Blob to a byte array
        // Create a String object from the byte array
        // Stream read the contents of the String using JsonReader
        // This avoids hitting memory peak trying to serialize the whole object at once
        byte[] content = storage.readAllBytes(blobId);
        String contentString = new String(content, UTF_8);
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(contentString)));

        // Create a Queue to place all the uploaded products on 1x1
        // Use Gson to read JSON objects
        Queue queue = QueueFactory.getQueue("products-queue");
        Gson gson = new GsonBuilder().create();

        // Read file in stream mode
        reader.beginArray();
        while (reader.hasNext()) {
            // Read data into object model and queue it as a defered task
            Product product = gson.fromJson(reader, Product.class);
            if (product.getName() != null) {
                queue.add(TaskOptions.Builder.withPayload(new CronUpload.DeferedProductAdd(product)));
            }
        }
        reader.close();

        response.getWriter().println("added all tasks on products-queue!");
        response.getWriter().close();
        response.setStatus(200);
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
