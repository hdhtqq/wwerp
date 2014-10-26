package cn.wwerp;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.Message;

public class BaseBean implements Externalizable {
	public static final byte[] SPLIT_FLAG = new byte[]{0x35, 0x77};
	private static final String PROTO_CLASS  = "cn.zpon.yxon.bean.YXonProto";
	private static final String PACKAGE_NAME = "cn.zpon.yxon.bean";
	
	public static class SerializeFilter {
		public void onReadFromProtobuf(BaseBean bean, Object protobufObj) {}
		public void onWriteToProtobuf(BaseBean bean, Builder<?> builder) {}
	}
	private SerializeFilter serializeFilter;
	public void setSerializeFilter(SerializeFilter serializeFilter) {
		this.serializeFilter = serializeFilter;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append(toJson());
		return sb.toString();
	}
	
	private static Class<?> getProtoClass(Class<?> baseBeanClass) throws ClassNotFoundException {
		String name = baseBeanClass.getName();
		name = name.replace(PACKAGE_NAME, "");
		name = name.replaceAll("\\.", "\\$");
		return Class.forName(PROTO_CLASS + name);
	}
	
	private static Class<?> getBaseBeanClass(Class<?> protoClass) throws ClassNotFoundException {
		String name = protoClass.getName();
		name = name.replace(PROTO_CLASS + "$", "");
		name = name.replaceAll("\\.", "\\$");
		return Class.forName(PACKAGE_NAME + "." + name);
	}
	
	public void parseFrom(InputStream in) {
		parseFromObj(in, InputStream.class);
	}
	
	public void parseFrom(byte[] data) {
		parseFromObj(data, data.getClass());
	}
	
	private void parseFromObj(Object data, Class<?> cc) {
		try {
			Class<?> cProto = getProtoClass(getClass());
			Object pbObj = cProto.getMethod("parseFrom", cc).invoke(cProto, data);
			fromPbObject(pbObj);
				
			if (serializeFilter != null)
				serializeFilter.onReadFromProtobuf(this, pbObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] toByteArray() {
		Message m = toPbObject();
		return (m != null ? m.toByteArray() : null);
	}
	
	protected void fromPbObject(Object pbObj) {
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);

			Class<?> objClass = pbObj.getClass();
			for (Field f : fields) {
				try {
					if (f.getAnnotation(NotPBField.class) != null)
						continue;
					
					if (f.getType().equals(List.class)) {
						setListField(f, pbObj, objClass);
					} else if (f.getType().equals(Timestamp.class)) {
						Method m = objClass.getDeclaredMethod("get" + f.getName());
						Object time = m.invoke(pbObj);
						f.set(this, new Timestamp(((Integer)time) * 1000L));
					} else {
						Method m = objClass.getDeclaredMethod("get" + f.getName());
						Object v = m.invoke(pbObj);
						if (v instanceof Message) {
							Class<?> c = getBaseBeanClass(v.getClass());
							BaseBean bean = (BaseBean)c.newInstance();
							bean.fromPbObject(v);
							v = bean;
						} 
						f.set(this, v);	
					}
				} catch (NoSuchMethodException e) {
					//e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
				
			if (serializeFilter != null)
				serializeFilter.onReadFromProtobuf(this, pbObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getFieldGetMethodName(String fildeName) {
		byte[] items = fildeName.getBytes();
		items[0] = (byte) ((char) items[0] - 'a' + 'A');
		return new String(items);
	}
	
	protected Message toPbObject() {
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			
			Class<?> cProto = getProtoClass(getClass());
			Builder<?> builder = (Builder<?>)cProto.getMethod("newBuilder").invoke(cProto);
			Class<?> bc = builder.getClass();
			for (Field f : fields) {
				try {
					if (f.getAnnotation(NotDBField.class) != null)
						continue;
					
					Object obj = f.get(this);
					if (obj != null) {
						if (f.getType().equals(List.class)) {
							setListField(f, (List<?>)obj, builder, bc);
						} else if (f.getType().equals(Timestamp.class)) {
							Timestamp t = (Timestamp)obj;
							Method m = bc.getDeclaredMethod("set" + f.getName(), int.class);
							m.invoke(builder, (int)(t.getTime() / 1000));	
						} else if (obj instanceof BaseBean) {
							BaseBean b = (BaseBean)obj;
							obj = b.toPbObject();
							Method m = bc.getDeclaredMethod("set" + getFieldGetMethodName(f.getName()), obj.getClass());
							m.invoke(builder, obj);	
						} else {
							Method m = bc.getDeclaredMethod("set" + f.getName(), f.getType());
							m.invoke(builder, obj);							
						}
					}
				} catch (NoSuchMethodException e) {
//					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (serializeFilter != null)
				serializeFilter.onWriteToProtobuf(this, builder);
			return builder.build();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void setListField(Field f, Object objPb, Class<?> objPbClass) throws Exception {
		Method mCount = objPbClass.getDeclaredMethod("get" + f.getName() + "Count");
		Method mGet = objPbClass.getDeclaredMethod("get" + f.getName(), int.class);
		Integer count = (Integer)mCount.invoke(objPb);
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < count; i++) {
			Object v = mGet.invoke(objPb, i);
			if (v instanceof Message) {
				Class<?> c = getBaseBeanClass(v.getClass());
				BaseBean bean = (BaseBean)c.newInstance();
				bean.fromPbObject(v);
				
				v = bean;
			}
			list.add(v);
		}
		f.set(this, list);		
	}
	
	private void setListField(Field f, List<?> values, Builder<?> builder, Class<?> bc) throws Exception {
		if (values.isEmpty())
			return;
		Object o = values.get(0);
		Class<?> paramType;
		if (o instanceof BaseBean) {
			BaseBean b = (BaseBean)o;
			o = b.toPbObject();
			paramType = o.getClass();
			
			Method m = bc.getDeclaredMethod("add" + f.getName(), paramType);
			for (Object obj : values) {
				b = (BaseBean)obj;
				o = b.toPbObject();
				m.invoke(builder, o);
			}
		} else {
			if (o instanceof Integer)
				paramType = int.class;
			else if (o instanceof Long)
				paramType = long.class;
			else if (o instanceof Short)
				paramType = short.class;
			else if (o instanceof Byte)
				paramType = byte.class;
			else if (o instanceof BaseBean)
				paramType = byte.class;
			else
				paramType = o.getClass();
			
			Method m = bc.getDeclaredMethod("add" + f.getName(), paramType);
			for (Object obj : values) {
				m.invoke(builder, obj);
			}
		}
	}
	
	/*
	public void parseFromJson(String json) {
		try {
			JsonNode root = new JsonTypeMapper().read(new JsonFactory().createJsonParser(new StringReader(json)));	
			parseFromJson(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseFromJson(JsonNode root) throws Exception {
		Class<?> c = getClass();
		for (Iterator<String> iterator = root.getFieldNames(); iterator.hasNext();) {
			String field = iterator.next();
			JsonNode node = root.getFieldValue(field);
			Field f = c.getField(field);
			if (node instanceof ValueNode) {
				setBasicField(this, f, node.getValueAsText());
			} else if (node instanceof ArrayNode) {
				//TODO:
			} else {
			}
		}
	}*/
	
	public String toJson() {
		Field[] fields = getClass().getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		try {
			for (Field f : fields) {
				if ("serializeFilter".equals(f.getName()))
					continue;
				
				if (!Modifier.isStatic(f.getModifiers())) {
					Object v = f.get(this);
					if (v != null) {
						if (v instanceof Number && ((Number)v).longValue() == 0) //ignore 0
							continue;
						String vJson = toJsonValue(v);
						if ("password".equalsIgnoreCase(f.getName()) || "pwd".equalsIgnoreCase(f.getName()))
							vJson = "***";
						sb.append("\"").append(f.getName()).append("\"").append(":").append(vJson).append(",");
					}
				}
			}
		} catch (Exception e) {
		}
		if (sb.length() > 1 && sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		sb.append("}");
		return sb.toString();
	}
	
	private String toJsonValue(Object o) {
		if (o instanceof BaseBean) {
			BaseBean b = (BaseBean)o;
			return b.toJson();
		} 
		if (o instanceof String)
			return "\"" + toJsonStr(o.toString()) + "\"";
		else if (o instanceof Date)
			return String.valueOf(((Date)o).getTime());
		else if (o instanceof Collection)
			return collectionToJson((Collection<?>)o);
		else
			return toJsonStr(String.valueOf(o));
	}
	
	private String collectionToJson(Collection<?> c) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Object obj : c) {
			sb.append(toJsonValue(obj)).append(",");
		}
		if (sb.length() > 1 && sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		sb.append("]");
		return sb.toString();
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
	
	public static void setBasicField(Object obj, Field f, String v) {
		Class<?> type = f.getType();
		try {
			if (type.equals(int.class))
				f.setInt(obj, Integer.parseInt(v));
			else if (type.equals(String.class))
				f.set(obj, v);
			else if (type.equals(Integer.class))
				f.set(obj, Integer.parseInt(v));
			else if (type.equals(int.class))
				f.setInt(obj, Integer.parseInt(v));
			else if (type.equals(Long.class))
				f.set(obj, Long.parseLong(v));
			else if (type.equals(long.class))
				f.setLong(obj, Long.parseLong(v));
			else if (type.equals(Short.class))
				f.set(obj, Short.parseShort(v));
			else if (type.equals(short.class))
				f.setShort(obj, Short.parseShort(v));
			else if (type.equals(byte.class))
				f.setByte(obj, Byte.parseByte(v));
			else if (type.equals(Byte.class))
				f.set(obj, Byte.parseByte(v));					
			else if (type.equals(Character.class))
				f.set(obj, v.length() > 0 ? v.charAt(0) : ' ');
			else if (type.equals(Date.class))
				f.set(obj, new Date(Long.parseLong(v)));
			else if (type.equals(java.sql.Date.class))
				f.set(obj, new java.sql.Date(Long.parseLong(v)));
			else if (type.equals(Timestamp.class))
				f.set(obj, new Timestamp(Long.parseLong(v)));
			else if (type.equals(float.class))
				f.setFloat(obj, Float.parseFloat(v));
			else if (type.equals(Float.class))
				f.set(obj, Float.parseFloat(v));
			else if (type.equals(double.class))
				f.setDouble(obj, Double.parseDouble(v));
			else if (type.equals(Double.class))
				f.set(obj, Double.parseDouble(v));
		} catch (Exception e) {
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		byte[] data = toByteArray();
		out.writeInt(data.length);
		out.write(data);
	}

	private static byte[] readLenData(ObjectInput in, final int len) throws IOException {
		byte[] data = new byte[len];
		int readLen = 0;
		int n = 0;
		while ((n = in.read(data, readLen, len - readLen)) > 0) {
			readLen += n;
			if (readLen >= len)
				break;
		}
		return data;
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int len = in.readInt();
		byte[] data = readLenData(in, len);
		parseFrom(data);
	}
}
