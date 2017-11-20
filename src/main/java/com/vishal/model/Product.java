package com.vishal.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class Product implements Serializable {
	
	public String sku;
	public String name;
	public String type;
	public String price;
	public String upc;
	public List<Category> category;
	public String shipping;
	public String description;
	public String manufacturer;
	public String model;
	public String url;
	public String image;
	
	public String getSku() {
		return this.sku;
	}
	
	public void setSku(String value) {
		this.sku = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String value) {
		this.name = value;
	}
	
	public String getType() {
		return this.type;
	}
	
	public void setType(String value) {
		this.type = value;
	}
	
	public String getPrice() {
		return this.price;
	}
	
	public void setPrice(String value) {
		this.price = value;
	}
	
	public String getUpc() {
		return this.upc;
	}
	
	public void setUpc(String value) {
		this.upc = value;
	}
	
	public List<Category> getCategory() {
		return this.category;
	}
	
	public void setCategory(ArrayList<Category> value) {
		this.category = value;
	}
	
	public String getShipping() {
		return this.shipping;
	}
	
	public void setShipping(String value) {
		this.shipping = value;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String value) {
		this.description = value;
	}
	
	public String getManufacturer() {
		return this.manufacturer;
	}
	
	public void setManufacturer(String value) {
		this.manufacturer = value;
	}
	
	public String getModel() {
		return this.model;
	}
	
	public void setModel(String value) {
		this.model = value;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String value) {
		this.url = value;
	}
	
	public String getImage() {
		return this.image;
	}
	
	public void setImage(String value) {
		this.image = value;
	}
}
