package cn.wwerp;

import java.sql.Timestamp;

public class ItemDetail extends BaseBean {
	public int Id;
	public int TypeId;
	public int ItemId;
	public float Quantity;
	public float Price;
	public float Amount;
	public String Remark = "";
	public Timestamp Ts;
	public int getItemId() {
		return ItemId;
	}
	public void setItemId(int itemId) {
		ItemId = itemId;
	}
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getTypeId() {
		return TypeId;
	}
	public void setTypeId(int typeId) {
		TypeId = typeId;
	}
	public float getQuantity() {
		return Quantity;
	}
	public void setQuantity(float quantity) {
		Quantity = quantity;
	}
	public float getPrice() {
		return Price;
	}
	public void setPrice(float price) {
		Price = price;
	}
	public float getAmount() {
		return Amount;
	}
	public void setAmount(float amount) {
		Amount = amount;
	}
	public Timestamp getTs() {
		return Ts;
	}
	public void setTs(Timestamp ts) {
		Ts = ts;
	}

}
