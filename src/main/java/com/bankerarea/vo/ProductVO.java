package com.bankerarea.vo;

public class ProductVO {
	private String partner_order_id;
	private String partner_user_id;
	private String item_name;
	private String total_amount;
	private String quantity;
	
	public String getPartner_order_id() {
		return partner_order_id;
	}
	public void setPartner_order_id(String partner_order_id) {
		this.partner_order_id = partner_order_id;
	}
	public String getPartner_user_id() {
		return partner_user_id;
	}
	public void setPartner_user_id(String partner_user_id) {
		this.partner_user_id = partner_user_id;
	}
	public String getItem_name() {
		return item_name;
	}
	public void setItem_name(String item_name) {
		this.item_name = item_name;
	}
	public String getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public String toString() {
		return "ProductVO [partner_order_id=" + partner_order_id + ", partner_user_id=" + partner_user_id
				+ ", item_name=" + item_name + ", total_amount=" + total_amount + ", quantity=" + quantity + "]";
	}
}
