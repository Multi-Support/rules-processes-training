package com.multisupport.training.model;

import java.net.URI;

public class Item {

	private URI id;
	private Double price;

	public Item() {
	}
	
	public Item(URI id, Double price) {
		this.id = id;
		this.price = price;
	}
	
	public URI getId() {
		return id;
	}
	
	public void setId(URI id) {
		this.id = id;
	}
	
	public Double getPrice() {
		return price;
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", price=" + price + "]";
	}
}
