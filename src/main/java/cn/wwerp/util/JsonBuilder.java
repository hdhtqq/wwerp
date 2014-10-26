package cn.wwerp.util;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class JsonBuilder {
	
	private StringBuilder sb;
	private boolean flip = false;
	
	public JsonBuilder() {
		sb = new StringBuilder();
		sb.append("{");
	}
	
	public JsonBuilder(String s) {
		sb = new StringBuilder(s);
	}
	
	public JsonBuilder(int initCapacity) {
		sb = new StringBuilder(initCapacity);
		sb.append("{");
	}
	
	public JsonBuilder append(String name, boolean value) {
		if (name == null)
			return this;
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}
	
	public JsonBuilder append(String name, String value) {
		if (name == null || value == null)
			return this;
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":\"").append(toJsonStr(value)).append("\"");
		return this;
	}
	
	public JsonBuilder append(String name, long value) {
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}
	
	public JsonBuilder append(String name, int value) {
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":").append(value);
		return this;
	}
	
	public JsonBuilder append(String name, JsonBuilder value) {
		return appendJsonValue(name, value.toString());
	}
	
	public JsonBuilder append(String name, Object value) {
		if (name == null || value == null)
			return this;
		if (value instanceof String)
			append(name, value.toString());
		else if (value instanceof Number)
			append(name, ((Number)value).longValue());
		else if (value instanceof java.util.Date)
			append(name, Util.formatDate((Date)value));
		else
			appendJsonValue(name, value.toString());
		return this;
	}
	
	public JsonBuilder appendJsonValue(String name, String jsonValue) {
		if (name == null || jsonValue == null)
			return this;
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":").append(jsonValue);
		return this;
	}
	
	public <T> JsonBuilder appendArray(String name, Collection<T> objs) {
		if (objs == null)
			return this;
		
		if (sb.length() > 1)
			sb.append(",");
		sb.append("\"").append(name).append("\":[");
		for (Object obj : objs) {
			if (obj instanceof Map<?, ?>) {
				JsonBuilder json = new JsonBuilder();
				Map<?, ?> map = (Map<?, ?>)obj;
				for (Map.Entry<?, ?> entry : map.entrySet()) {
					json.append(entry.getKey().toString(), entry.getValue());
				}
				sb.append(json.toString());
			} else if (obj instanceof Date) {
				sb.append(toJsonStr(Util.formatDate((Date)obj)));
			} else {
				boolean asStr = !(obj instanceof Number || obj instanceof JsonBuilder);
				if (asStr)
					sb.append("\"");
				sb.append(toJsonStr(String.valueOf(obj)));
				if (asStr)
					sb.append("\"");
			}
			sb.append(",");
		}
		if (sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		sb.append("]");
		return this;
	}
	
	public JsonBuilder flip() {
		sb.append('}');
		flip = true;
		return this;
	}
	
	public JsonBuilder reset() {
		sb.setLength(0);
		sb.append("{");
		return this;
	}
	
	private static String toJsonStr(String value) {
		if (value == null)
			return null;
		boolean valid = true;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c < 32 || c == '"' || c == '\\' || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b') {
				valid = false;
				break;
			}
		}
		if (valid)
			return value;
		
		StringBuilder buf = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
            switch(c) {
                case '"':
                	buf.append("\\\"");
                    break;
                case '\\':
                    buf.append("\\\\");
                    break;
                case '\n':
                    buf.append("\\n");
                    break;
                case '\r':
                    buf.append("\\r");
                    break;
                case '\t':
                    buf.append("\\t");
                    break;
                case '\f':
                    buf.append("\\f");
                    break;
                case '\b':
                    buf.append("\\b");
                    break;
                    
                default:
                	if (c < 32) {
                		buf.append("\\u00");
                		String str = Integer.toHexString(c);
                		if (str.length() == 1)
                			buf.append('0');
                		buf.append(str);
                	} else {
                		buf.append(c);
                	}
            }
		}
		return buf.toString();
	}

	@Override
	public String toString() {
		if (!flip)
			flip();
		return sb.toString();
	}

}
