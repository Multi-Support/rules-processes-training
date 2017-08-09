package com.multisupport.training.model;

public class OrderItem {

	private Item item;
	private Integer quantity;
	
	public OrderItem(Item item, Integer quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "OrderItem [item=" + item + ", quantity=" + quantity + "]";
	}
}
