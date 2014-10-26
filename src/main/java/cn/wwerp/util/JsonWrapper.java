package cn.wwerp.util;

import java.io.StringReader;
import java.util.StringTokenizer;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.JsonNode;
import org.codehaus.jackson.map.JsonTypeMapper;
import org.codehaus.jackson.map.node.NumericNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class JsonWrapper {
	
	private static final JsonWrapper emptyJsonWrapper = new JsonWrapper((JsonNode)null);
	
	private JsonNode root;
	
	public JsonWrapper(String json) {
		if (json != null) {
			try {
				root = new JsonTypeMapper().read(new JsonFactory().createJsonParser(new StringReader(json)));				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public JsonWrapper(JsonNode root) {
		this.root = root;
	}
	
	@Override
	public String toString() {
		return (root != null ? root.toString() : "null");
	}

	public String get(String name) {
		JsonNode node = getJsonNode(name);
		return (node == null ? null : node.getValueAsText());
	}
	
	public int getInt(String name) {
		return getInt(name, 0);
	}
	
	public int getInt(String name, int defaultValue) {
		JsonNode node = getJsonNode(name);
		if (node == null)
			return defaultValue;
		else if (node instanceof NumericNode)
			return ((NumericNode)node).getIntValue();
		else
			return str2int(node.getValueAsText(), defaultValue);
	}
	
	public long getLong(String name) {
		return getLong(name, 0);
	}
	
	public long getLong(String name, long defaultValue) {
		JsonNode node = getJsonNode(name);
		if (node == null)
			return defaultValue;
		else if (node instanceof NumericNode)
			return ((NumericNode)node).getLongValue();
		else
			return str2long(node.getValueAsText(), defaultValue);
	}
	
	public JsonWrapper getNode(String name) {
		return new JsonWrapper(getJsonNode(name));
	}
	
	public JsonWrapper getArrayNode(int idx) {
		if (root != null && root.isArray()) {
			if (idx >= 0 && idx < root.size())
				return  new JsonWrapper(root.getElementValue(idx));
		}
		return emptyJsonWrapper;
	}
	
	public String getArrayNodeValue(int idx) {
		if (root != null && root.isArray()) {
			if (idx >= 0 && idx < root.size()) {
				JsonNode node = root.getElementValue(idx);
				return (node == null ? null : node.getValueAsText());
			}
		}
		return null;
	}
	
	public int getArrayNodeIntValue(int idx, int defaultValue) {
		if (root != null && root.isArray()) {
			if (idx >= 0 && idx < root.size()) {
				JsonNode node = root.getElementValue(idx);
				if (node == null)
					return defaultValue;
				else if (node instanceof NumericNode)
					return ((NumericNode)node).getIntValue();
				else
					return str2int(node.getValueAsText(), defaultValue);
			}
		}
		return defaultValue;
	}
	
	public long getArrayNodeLongValue(int idx, long defaultValue) {
		if (root != null && root.isArray()) {
			if (idx >= 0 && idx < root.size()) {
				JsonNode node = root.getElementValue(idx);
				if (node == null)
					return defaultValue;
				else if (node instanceof NumericNode)
					return ((NumericNode)node).getLongValue();
				else
					return str2long(node.getValueAsText(), defaultValue);
			}
		}
		return defaultValue;
	}
	
	public boolean isArray() {
		return (root != null && root.isArray());
	}
	
	public int size() {
		if (root != null)
			return root.size();
		else
			return 0;
	}
	
	public Map<String, String> values() {
		Map<String, String> map = new HashMap<String, String>();
		for (Iterator<String> iterator = root.getFieldNames(); iterator.hasNext();) {
			String field = iterator.next();
			String value = null;
			JsonNode node = root.getFieldValue(field);
			if (node != null)
				value = node.getValueAsText();
			map.put(field, value);
		}
		return map;
	}
	
	public boolean isEmpty() {
		return (root == null || root.size() == 0);
	}
	
	public boolean isNull() {
		return (root == null);
	}
	
	private JsonNode getJsonNode(String name) {
		if (name == null || root == null)
			return null;
		
		JsonNode n = root.getFieldValue(name); 
		if (n != null)
			return n;
		
		JsonNode node = root;
		StringTokenizer st = new StringTokenizer(name, ".");
		while (st.hasMoreTokens()) {
			String key = st.nextToken().trim();
			if (key.isEmpty() || (node = node.getFieldValue(key)) == null)
				return null;
		}
		return node;
	}
	
	private int str2int(String s, int defaultValue) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	private long str2long(String s, long defaultValue) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			try {
				double d = Double.parseDouble(s);
				return (long)d;
			} catch (Exception ex) {
				//ignore
			}
			return defaultValue;
		}
	}
	
	public static void main(String[] args) throws Exception {
		JsonWrapper json = new JsonWrapper("{\"errorno\":\"1.67\", \"rst\":[[\"u1\",22],[\"u2\",\"t2\"]]}");
		System.out.println(json.getLong("errorno"));
		//System.out.println(json.getNode("rst").getArrayNode(0).getArrayNodeIntValue(1, 10));
	}

}
