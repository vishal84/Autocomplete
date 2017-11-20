package com.vishal.dao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.vishal.model.AutoCompleteResults;
import com.vishal.model.Product;

import java.util.ArrayList;
import java.util.List;

import java.util.Collections;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import org.codehaus.jackson.map.ObjectMapper;

public class DatastoreDao  {

	private DatastoreService datastore;
	private static final String PRODUCTS_KIND = "Products";

	public DatastoreDao() {
		datastore = DatastoreServiceFactory.getDatastoreService(); // Authorized Datastore service
	}
  
	public Long createProduct(Product product) {
		Entity incProductEntity = new Entity(PRODUCTS_KIND);  // Key will be assigned once written
		incProductEntity.setProperty("sku", product.getSku());
		incProductEntity.setProperty("name", (product.getName() != null) ? product.getName().toLowerCase() : product.getName());
		incProductEntity.setProperty("label", product.getName());
		incProductEntity.setProperty("type", product.getType());
		incProductEntity.setProperty("price", product.getPrice());
		incProductEntity.setProperty("upc", product.getUpc());
	    incProductEntity.setProperty("shipping", product.getShipping());
	    incProductEntity.setProperty("description", product.getDescription());
	    incProductEntity.setProperty("manufacturer", product.getManufacturer());
	    incProductEntity.setProperty("model", product.getModel());
	    incProductEntity.setProperty("url", product.getUrl());
	    incProductEntity.setProperty("image", product.getImage());
	    
	    Key productKey = datastore.put(incProductEntity); // Save the Entity
	    return productKey.getId();                     // The ID of the Key
	}

	public String getProducts(String term) {
		
		Cache cache;
		String resultsJSON;
		
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
            
	        // no like query in datastore, using filters and 
	    		// setting highest unicode char uFFFD sets the upper 
	    		// limit for term match, composite filter can only use and on two filters
	    		Filter greater = new FilterPredicate("name", FilterOperator.GREATER_THAN_OR_EQUAL, term);
	    	    Filter less = new FilterPredicate("name", FilterOperator.LESS_THAN, term + "\uFFFD"); 
	    		Filter filter = CompositeFilterOperator.and(greater, less);
	    		
	    		Query q = new Query(PRODUCTS_KIND);
	    		q.addSort("name", Query.SortDirection.ASCENDING);
	    		q.setFilter(filter);
	    		
	    		// check cache before getting taking a roundtrip to the backend...
	    		if (cache.containsKey(term)) {
	    			
	    			System.out.println("Got a cache hit for term: " + term);
	    			return (String) cache.get(term);
	    			
	    		} else {
	    			
		    		List<Entity> products = datastore.prepare(q).asList(FetchOptions.Builder.withLimit(500));
		    		resultsJSON = entityToAutoCompleteResults(products);
		    		
		    		// cache the results..
		    		cache.put(term, resultsJSON);
		    		
		    		return resultsJSON;
	    		}
        } catch (CacheException e) {
            e.printStackTrace();
        }
		return null;
	}
  
	public String entityToAutoCompleteResults(List<Entity> products) {
		
		List<AutoCompleteResults> productList = new ArrayList<AutoCompleteResults> ();
		
		String json = "";
		try {
			for (Entity product : products) {
				AutoCompleteResults result = new AutoCompleteResults();
				result.setLabel((String) product.getProperty("label"));
				result.setValue((String) product.getProperty("name"));
				
				productList.add(result);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			json = mapper.writeValueAsString(productList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return json;
	}
	
	public String entityToHtml(List<Entity> products) {
		String options = "";
		for (Entity product: products) {
			options += "<option value='" + (String) product.getProperty("label") + "'>";
		}
		return options;
	}
  
}
