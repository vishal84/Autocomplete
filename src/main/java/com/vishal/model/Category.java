package com.vishal.model;

import java.io.Serializable;

public class Category implements Serializable {

	public String id;
	public String name;

	public String getId() {
		return this.id;
	}
	
	public void setId(String value) {
		this.id = value;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String value) {
		this.name = value;
	}
}
