package cn.wwerp;

import java.sql.Timestamp;

public class TypeClass extends BaseBean {
	public int Id;
	public String Name = "";
	public Timestamp Ts;
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public Timestamp getTs() {
		return Ts;
	}
	public void setTs(Timestamp ts) {
		Ts = ts;
	}
}
