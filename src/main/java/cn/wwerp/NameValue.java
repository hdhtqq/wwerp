package cn.wwerp;

public class NameValue extends BaseBean {
	public String Name;
	public String Value;
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
}
