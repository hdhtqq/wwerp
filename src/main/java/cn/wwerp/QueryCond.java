package cn.wwerp;

import java.util.HashMap;
import java.util.Map;

public class QueryCond {

	private String[] keyFields;
	private Object[] keyFieldValues;
	private String[] likeFields;
	private Object[] likeFieldValues;
	private String clause;
	private String order;
	private int start;
	private int limit;
	
	private Map<String, Object> ignoreValues;
	
	public String[] getKeyFields() {
		return keyFields;
	}
	public void setKeyFields(String[] keyFields) {
		this.keyFields = keyFields;
	}
	public String[] getLikeFields() {
		return likeFields;
	}
	public void setLikeFields(String[] likeFields) {
		this.likeFields = likeFields;
	}
	
	public boolean isLikeField(String field) {
		if (likeFields != null) {
			for (String f : likeFields) {
				if (f.equals(field))
					return true;
			}
		}
		return false;
	}
	
	public void setIgnoreValues(String[] fields, Object[] values) {
		ignoreValues = new HashMap<String, Object>();
		
		for (int i = 0; i < fields.length; i++) {
			ignoreValues.put(fields[i], values[i]);
		}
	}
	
	public boolean isIgnore(String field, Object v) {
		if (ignoreValues != null) {
			Object obj = ignoreValues.get(field);
			return (obj != null && obj.equals(v));			
		}
		return false;
	}
	
	public Object[] getKeyFieldValues() {
		return keyFieldValues;
	}
	public void setKeyFieldValues(Object[] keyFieldValues) {
		this.keyFieldValues = keyFieldValues;
	}
	public Object[] getLikeFieldValues() {
		return likeFieldValues;
	}
	public void setLikeFieldValues(Object[] likeFieldValues) {
		this.likeFieldValues = likeFieldValues;
	}

	public String getClause() {
		return clause;
	}
	public void setClause(String clause) {
		this.clause = clause;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
}
