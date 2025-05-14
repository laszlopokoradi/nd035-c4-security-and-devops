package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModifyCartRequest {
	@JsonProperty
	private long itemId;
	
	@JsonProperty
	private int quantity;

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
