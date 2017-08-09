package com.multisupport.training.model;

import java.util.LinkedList;
import java.util.List;

public class Order {

	private List<OrderItem> items = new LinkedList<>();
	private String discountCode;
	
	public Order(Object... items) {
		if (items.length %2 != 0) {
			throw new IllegalArgumentException("Need an even amout of parameters in the format (item, qty) pairs");
		}
		for (int index = 0; index < items.length; index+=2) {
			Item item = (Item) items[index];
			Integer qty = (Integer) items[index+1];
			this.items.add(new OrderItem(item, qty));
		}
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public String getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}
}
