package cn.wwerp;

import java.sql.Timestamp;

public class ItemType extends BaseBean {
	public static final int INCOMINGS = 1;
	public static final int OUTGOINGS = 2;
	
	public int Id;
	public int Type;
	public int Idx;
	public int ClassId;
	public String Name = "";
	public String Unit = "";
	public float Price;
	public Timestamp Ts;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public int getType() {
		return Type;
	}
	public void setType(int type) {
		Type = type;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getUnit() {
		return Unit;
	}
	public void setUnit(String unit) {
		Unit = unit;
	}
	public float getPrice() {
		return Price;
	}
	public void setPrice(float price) {
		Price = price;
	}
	public Timestamp getTs() {
		return Ts;
	}
	public void setTs(Timestamp ts) {
		Ts = ts;
	}
	public int getIdx() {
		return Idx;
	}
	public void setIdx(int idx) {
		Idx = idx;
	}
	public int getClassId() {
		return ClassId;
	}
	public void setClassId(int classId) {
		ClassId = classId;
	}
}
