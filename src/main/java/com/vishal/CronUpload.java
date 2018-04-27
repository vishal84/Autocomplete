package com.vishal;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.vishal.dao.DatastoreDao;
import com.vishal.model.Product;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;


public class CronUpload extends HttpServlet {

    /**
     * This is where backoff parameters are configured. Here it is aggressively retrying with
     * backoff, up to 10 times but taking no more that 15 seconds total to do so.
     */
    private final GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
            .initialRetryDelayMillis(10)
            .retryMaxAttempts(10)
            .totalRetryPeriodMillis(15000)
            .build());

    /**Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB */
    private static final int BUFFER_SIZE = 2 * 1024 * 1024;
    // final String bucket = "vishalapatel-sandbox.appspot.com";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
        //copy(Channels.newInputStream(readChannel), response.getOutputStream());
        //gcsService.openPrefetchingReadChannel();
        /* Figure out how to read from a GCS Bucket then stream into TaskQueues.... */

        Queue queue = QueueFactory.getQueue("products-queue");
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(new File(getClass().getClassLoader().getResource("products.json").getFile())), "UTF-8"));
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
