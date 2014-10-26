package cn.wwerp.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	public static final Logger log = LoggerFactory.getLogger(Util.class);

	public static final String ANONYMOUSUSER_PREFIX = "anonymoususer";

	public static final Random rd = new Random();

	private static Map<String, ThreadLocal<DateFormat>> dateFormats = new ConcurrentHashMap<String, ThreadLocal<DateFormat>>();
	private static ThreadLocal<String> currentDateformat = new ThreadLocal<String>();
	
	public static long TIME_ZERO;
	
	static {
		try {
			TIME_ZERO = new SimpleDateFormat("yyyy/MM/dd").parse("2000/01/01").getTime();
		} catch (Exception e) {
		}
	}
	

	public static void setCurrentDateformat(String format) {
		currentDateformat.set(format);
	}

	public static void removeCurrentDateformat() {
		currentDateformat.remove();
	}

	private static ThreadLocal<MessageDigest> MD5 = new ThreadLocal<MessageDigest>() {
		@Override
		protected MessageDigest initialValue() {
			try {
				return MessageDigest.getInstance("MD5");
			} catch (Exception e) {
			}
			return null;
		}
	};
	
	private static final ThreadLocal<CRC32> crc32 = new ThreadLocal<CRC32>() {
		@Override
		protected synchronized CRC32 initialValue() {
			return new CRC32();
		}
	};

	public static long crc32(byte[] b) {
		CRC32 c = crc32.get();
		c.reset();
		c.update(b);
		return c.getValue();
	}
	
	public static int str2int(String s) {
		return str2int(s, 0);
	}

	public static int str2int(String s, int defaultValue) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public static float str2float(String s, float defaultValue) {
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long str2long(String s) {
		return str2long(s, 0);
	}

	public static double str2double(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return 0;
		}
	}

	public static long str2long(String s, long defaultValue) {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static short bytes2short(byte[] b) {
		return byte2short(b[0], b[1]);
	}
	
	public static short byte2short(byte b1, byte b2) {
		return (short)((b1 << 8) | (b2 & 0xff));
	}
	
	public static int bytes2int(byte[] b) {
		return (int)((((b[0] & 0xff) << 24) |
			      ((b[1] & 0xff) << 16) |
			      ((b[2] & 0xff) <<  8) |
			      ((b[3] & 0xff) <<  0)));
	}
	
	public static long bytes2long(byte[] b) {
		return ((((long)b[0] & 0xff) << 56) |
				(((long)b[1] & 0xff) << 48) |
				(((long)b[2] & 0xff) << 40) |
				(((long)b[3] & 0xff) << 32) |
				(((long)b[4] & 0xff) << 24) |
				(((long)b[5] & 0xff) << 16) |
				(((long)b[6] & 0xff) <<  8) |
				(((long)b[7] & 0xff) <<  0));
	}
	
	public static byte[] short2bytes(short n) {
		byte[] b = new byte[2];
		b[0] = (byte)(n >> 8);
		b[1] = (byte)(n >> 0);
		return b;
	}
	
	public static byte[] int2bytes(int n) {
		byte[] b = new byte[4];
		b[0] = (byte)(n >> 24);
		b[1] = (byte)(n >> 16);
		b[2] = (byte)(n >> 8);
		b[3] = (byte)(n >> 0);
		return b;
	}
	
	public static byte[] long2bytes(long n) {
		byte[] b = new byte[8];
		b[0] = (byte)(n >> 56);
		b[1] = (byte)(n >> 48);
		b[2] = (byte)(n >> 40);
		b[3] = (byte)(n >> 32);
		b[4] = (byte)(n >> 24);
		b[5] = (byte)(n >> 16);
		b[6] = (byte)(n >> 8);
		b[7] = (byte)(n >> 0);
		return b;
	}
	
	public static boolean isDigit(String s) {
		if (s == null || s.isEmpty())
			return false;
		
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c < '0' || c > '9')
				return false;
		}
		
		return true;
	}
	
	public static long int2long(int high, int low) {
		long n = high;
		n = n << 32;
		return n + low;
	}

	public static String fromEscChar(String s) {
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("&lt;", "<");
		s = s.replaceAll("&gt;", ">");
		s = s.replaceAll("&apos;", "'");
		s = s.replaceAll("&quot;", "\"");
		return s;
	}

	public static String toEscChar(String s) {
		s = s.replaceAll("&amp;", "&");
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");
		s = s.replaceAll("\"", "&quot;");
		s = s.replaceAll("'", "&apos;");
		return s;
	}

	/**
	 * appendJsonField(sb, "key", "value", ",", ",") just append:
	 * ,"key":"value",
	 * 
	 * @param buf
	 * @param field
	 * @param value
	 * @param prefix
	 * @param suffix
	 * @return buf
	 */
	public static StringBuilder appendJsonField(StringBuilder buf, String field, String value, String prefix, String suffix) {
		if (value != null) {
			if (prefix != null)
				buf.append(prefix);
			buf.append("\"").append(field).append("\":\"").append(toJsonStr(value)).append("\"");
			if (suffix != null)
				buf.append(suffix);
		}
		return buf;
	}

	public static String toJsonStr(String value) {
		if (value == null)
			return null;
		boolean valid = true;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (c == '"' || c == '\\' || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b') {
				valid = false;
				break;
			}
		}
		if (valid)
			return value;

		StringBuilder buf = new StringBuilder(value.length());
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			switch (c) {
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
				buf.append(c);
			}
		}
		return buf.toString();
	}

	public static String desEncrypt(String message, String key) {
		if (message == null || message.length() == 0)
			return "";
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

			byte[] b = cipher.doFinal(message.getBytes("UTF-8"));
			return encodeHex(b);
		} catch (Exception e) {
			return "";
		}
	}

	public static String desDecrypt(String message, String key) {
		if (message == null || message.length() == 0)
			return "";
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

			byte[] b = cipher.doFinal(decodeHex(message.toCharArray()));
			return new String(b, "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	public static String md5(String text) {
		try {
			return md5(text.getBytes("utf-8"));
		} catch (Exception e) {
			return null;
		}
	}

	public static String md5(byte[] data) {
		MessageDigest md5 = MD5.get();
		md5.reset();
		md5.update(data);
		byte[] digest = md5.digest();
		return encodeHex(digest);
	}
	/**
	 * sha-1加密
	 */
	public static String sha1(String s){
		String tmpStr = null;
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(s.toString().getBytes());
			tmpStr = encodeHex(digest);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return tmpStr;
	}
	
	

	private static final char[] DIGITS_LOWER =
        {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
	public static String encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }
	
	private static int toDigit(final char ch, final int index) {
		final int digit = Character.digit(ch, 16);
		return (digit == -1 ? 0 : digit);
	}
	 
	public static byte[] decodeHex(final char[] data) {
        final int len = data.length;
        if ((len & 0x01) != 0)
        	return null;

        final byte[] out = new byte[len >> 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

	public static void writeSafeUtf8(DataOutputStream out, String s) throws IOException {
		if (s == null) {
			out.writeShort(-1);
		} else {
			byte[] bb = s.getBytes("utf-8");
			out.writeShort(bb.length);
			out.write(bb);
		}
	}

	public static String readSafeUtf8(DataInputStream in) throws IOException {
		int len = in.readShort();
		if (len < 0)
			return null;

		byte[] bb = new byte[len];
		in.read(bb);
		return new String(bb, "utf-8");
	}

	// s = 1,2,3,
	public static List<Integer> str2intArray(String line, String split) {
		if (line == null)
			return Collections.emptyList();
		List<Integer> list = new ArrayList<Integer>();
		for (String s : line.split(",")) {
			s = s.trim();
			if (!s.isEmpty()) {
				list.add(str2int(s, 0));
			}
		}
		return list;
	}

	public static List<Long> array2List(long[] arr) {
		List<Long> list = new ArrayList<Long>();
		for (long t : arr) {
			list.add(t);
		}
		return list;
	}

	public static final String[] str2array(String s) {
		List<String> list = str2Array(s, ",");
		return list.toArray(new String[list.size()]);
	}

	public static List<String> str2Array(String line, String split) {
		if (line == null)
			return Collections.emptyList();
		List<String> list = new ArrayList<String>();
		for (String s : line.split(",")) {
			s = s.trim();
			if (!s.isEmpty()) {
				list.add(s);
			}
		}
		return list;
	}

	public static StringBuilder trimLastComma(StringBuilder sb) {
		if (sb != null) {
			int pos = sb.lastIndexOf(",");
			if (pos > 0) {
				boolean trim = true;
				for (int i = pos + 1; i < sb.length(); i++) {
					if (sb.charAt(i) != ' ') {
						trim = false;
						break;
					}
				}
				if (trim)
					sb.setLength(pos);
			}
		}
		return sb;
	}

	public static Object getField(Object obj, String field) {
		try {
			Class<?> c = obj.getClass();
			Field f = c.getDeclaredField(field);
			AccessibleObject.setAccessible(new Field[] { f }, true);
			return f.get(obj);
		} catch (Exception e) {
			return null;
		}
	}

	public static void setNumberFieldZero(Object obj) {
		try {
			Class<?> c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);

			for (Field f : fields) {
				if (f.getType().equals(Integer.class))
					f.set(obj, Integer.valueOf(0));
				else if (f.getType().equals(Long.class))
					f.set(obj, Long.valueOf(0));
				else if (f.getType().equals(Short.class))
					f.set(obj, Short.valueOf((short) 0));
				else if (f.getType().equals(Byte.class))
					f.set(obj, Byte.valueOf((byte) 0));
				else if (f.getType().equals(Float.class))
					f.set(obj, Float.valueOf(0));
				else if (f.getType().equals(Double.class))
					f.set(obj, Double.valueOf(0));
			}
		} catch (Exception e) {
		}
	}

	public static void fillBean(Object obj, HttpServletRequest request) {
		try {
			Class<?> c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);

			for (Field f : fields) {
				Class<?> type = f.getType();
				String[] vv = request.getParameterValues(f.getName());
				if (vv != null && vv.length > 1) { // maybe checkbox
					if (type.equals(Integer.class) || type.equals(int.class)) {
						int n = 0;
						for (String s : vv) {
							if (isDigit(s))
								n |= str2int(s);
						}
						if (n > 0)
							f.setInt(obj, n);
					} else if (type.equals(String.class)) {
						String v = "";
						for (String s : vv) {
							v += s + ",";
						}
						f.set(obj, v);
					}
					continue;
				}

				String v = null;
				Object se = request.getSession().getAttribute(f.getName()); // find from session first
				if (se != null)
					v = String.valueOf(se);
				if (v == null)
					v = request.getParameter(f.getName());
				if (v == null)
					continue;
				setField(obj, f, v);
			}
		} catch (Exception e) {
			log.warn("fillBean error! bean:" + obj, e);
		}
	}

	public static void setField(Object obj, String field, String v) {
		try {
			Field f = obj.getClass().getField(field);
			if (f != null) {
				setField(obj, f, v);
			}
		} catch (Exception e) {
			log.warn("setField error! field:" + field + ", obj:" + obj + ", v:" + v);
		}
	}

	public static void setField(Object obj, Field f, String v) {
		Class<?> type = f.getType();
		try {
			if (type.equals(int.class))
				f.setInt(obj, str2int(v));
			else if (type.equals(String.class))
				f.set(obj, v);
			else if (type.equals(Integer.class))
				f.set(obj, str2int(v));
			else if (type.equals(int.class))
				f.setInt(obj, str2int(v));
			else if (type.equals(Long.class))
				f.set(obj, str2long(v));
			else if (type.equals(long.class))
				f.setLong(obj, str2long(v));
			else if (type.equals(Short.class))
				f.set(obj, (short) str2int(v));
			else if (type.equals(short.class))
				f.setShort(obj, (short) str2int(v));
			else if (type.equals(byte.class))
				f.setByte(obj, (byte) str2int(v));
			else if (type.equals(Byte.class))
				f.set(obj, (byte) str2int(v));
			else if (type.equals(Character.class))
				f.set(obj, v.length() > 0 ? v.charAt(0) : ' ');
			else if (type.equals(Date.class))
				f.set(obj, parseDate(v));
			else if (type.equals(java.sql.Date.class))
				f.set(obj, parseSqlDate(v));
			else if (type.equals(Timestamp.class))
				f.set(obj, parseTimestamp(v));
			else if (type.equals(float.class))
				f.setFloat(obj, (float) str2double(v));
			else if (type.equals(Float.class))
				f.set(obj, (float) str2double(v));
			else if (type.equals(double.class))
				f.setDouble(obj, str2double(v));
			else if (type.equals(Double.class))
				f.set(obj, str2double(v));
		} catch (Exception e) {
			log.warn("fillBean error! bean:" + obj, e);
		}
	}

	private static java.sql.Date parseSqlDate(String s) {
		Date d = parseDate(s);
		if (d != null)
			return new java.sql.Date(d.getTime());
		else
			return null;
	}

	private static Timestamp parseTimestamp(String s) {
		Date d = parseDate(s);
		if (d != null)
			return new Timestamp(d.getTime());
		else
			return null;
	}

	private static String replaceChineseDateName(String date, String name) {
		if (date.endsWith(name))
			return date.replace(name, "");
		int pos = date.indexOf(name);
		if (pos > 0) {
			char c = date.charAt(pos + 1);
			if (c == '-' || c == '/' || c == '.')
				return date.replace(name, "");
			else
				return date.replace(name, "-");
		}
		return date;
	}

	public static Date parseDate(String date) {
		Date d = doParseDate(date);
		if (d != null && d.getTime() < 0) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 2000);
			d = c.getTime();
		}
		return d;
	}

	private static Date doParseDate(String date) {
		if (date == null)
			return null;

		Date d = null;
		date = replaceChineseDateName(date, "年");
		date = replaceChineseDateName(date, "月");
		date = replaceChineseDateName(date, "日");
		if (currentDateformat.get() != null)
			d = parseDate(date, currentDateformat.get());
		if (d != null)
			return d;
		if (date.length() == "yyyy-MM".length())
			d = parseDate(date, new String[] { "yyyy-MM", "yyyy/MM", "yyyy.MM" });
		if (d != null)
			return d;
		if (date.length() <= "yy-MM-dd".length())
			d = parseDate(date, new String[] { "yy-MM-dd", "yy/MM/dd", "yy.MM.dd", "yy-M-d", "yy/M/d", "y.M", "y-M", "y/M", "yy-MM", "yy.MM", "yy/MM" });
		if (d != null)
			return d;
		if (date.length() <= "yyyy-MM-dd".length())
			d = parseDate(date, new String[] { "yyyy-MM-dd", "dd/MM/yyyy", "yyyy/MM/dd", "yyyy.MM.dd", "d/M/yyyy" });
		if (d != null)
			return d;
		if (date.length() <= "yyyy-MM-dd HH:mm".length())
			d = parseDate(date, new String[] { "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm", "yyyy.MM.dd HH:mm" });
		if (d != null)
			return d;
		if (date.length() <= "yyyy-MM-dd HH:mm:ss".length())
			d = parseDate(date, new String[] { "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyy.MM.dd HH:mm:ss" });
		if (d != null)
			return d;
		if (!date.startsWith("0"))
			return parseDate("0" + date);
		return null;
	}

	public static Date parseDate(String date, String[] patterns) {
		for (String pattern : patterns) {
			Date d = parseDate(date, pattern);
			if (d != null)
				return d;
		}
		return null;
	}

	public static Date parseDate(String date, String pattern) {
		DateFormat format = getDateFormat(pattern);
		if (date != null && format != null) {
			try {
				return format.parse(date);
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}

	public static String formatDate(Date date) {
		return formatDate(date, null);
	}

	public static String formatDate(Date date, String pattern) {
		if (date == null)
			return null;

		if (pattern == null) {
			String format = currentDateformat.get();
			if (format != null) {
				pattern = format;
			}
		}
		if (pattern == null)
			pattern = "yyyy-MM-dd HH:mm:ss";

		DateFormat format = getDateFormat(pattern);
		return (format != null ? format.format(date) : date.toString());
	}

	public static String strleft(String s, int maxLen) {
		if (s == null || s.length() <= maxLen)
			return s;
		else
			return s.substring(0, maxLen) + "...";
	}

	public static boolean isValidMobile(String mobile) {
		if (mobile == null || mobile.isEmpty())
			return false;

		if (mobile.length() == 11 && isDigit(mobile))
			return true;
		else
			return false;
	}

	public static long getJtMapLongValue(String key, Map<String, Object> map) {
		Object obj = map.get(key);
		if (obj != null && obj instanceof Number)
			return ((Number) obj).longValue();
		else
			return 0;
	}

	public static List<Cell[]> parseXls(String file, int rowBegin, int colKey) throws Exception {
		List<Cell[]> rows = new ArrayList<Cell[]>();

		FileInputStream in = new FileInputStream(file);
		Workbook wb = Workbook.getWorkbook(in);
		Sheet sheet = wb.getSheet(0);
		int rowNum = sheet.getRows();
		if (rowBegin < 0)
			rowBegin = 0;
		for (int i = rowBegin; i < rowNum; i++) {
			Cell[] cells = sheet.getRow(i);
			if (colKey > 0 && cells.length > colKey) {
				String v = cells[colKey].getContents();
				if (v == null || (v = v.trim()).isEmpty())
					break;
			}
			rows.add(cells);
		}
		wb.close();
		in.close();

		return rows;
	}

	public static DateFormat getDateFormat(final String pattern) {
		ThreadLocal<DateFormat> format = dateFormats.get(pattern);
		if (format == null) {
			format = new ThreadLocal<DateFormat>() {

				@Override
				protected DateFormat initialValue() {
					try {
						return new SimpleDateFormat(pattern);
					} catch (Exception e) {
					}
					return null;
				}
			};

			dateFormats.put(pattern, format);
		}
		return format.get();
	}

	public static String encryptPassword(String account, String password) {
		return Util.md5(account + "_" + password);
	}

	public static boolean checkPassword(String account, String password, String passwordEncrypt) {
		return passwordEncrypt.equals(encryptPassword(account, password));
	}

	public static String urlEncode(String s) {
		try {
			return URLEncoder.encode(s, "utf-8");
		} catch (Exception e) {
			return s;
		}
	}

	public static String urlDecode(String s) {
		try {
			return URLDecoder.decode(s, "utf-8");
		} catch (Exception e) {
			return s;
		}
	}

	public static String readFileAsStr(String file) {
		byte[] data = readFile(file);
		try {
			return new String(data, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] readFile(String file) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
			byte[] bb = new byte[8192];
			int len;
			while ((len = in.read(bb)) > 0) {
				out.write(bb, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			log.warn("readFile error, file:" + file, e);
		}
		return out.toByteArray();
	}

	public static JsonBuilder bean2Json(Object bean) {
		JsonBuilder json = new JsonBuilder();
		Field[] fields = bean.getClass().getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (Field f : fields) {
			try {
				Object v = f.get(bean);
				if (v != null && v instanceof Date) {
					json.append(f.getName(), Util.formatDate((Date) v));
				} else if (v instanceof Number) {
					if (!"0".equals(String.valueOf(v)))
						json.append(f.getName(), v);
				} else {
					json.append(f.getName(), v);
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return json;
	}

	public static List<Integer> str2Ints(String s) {
		List<Integer> list = new ArrayList<Integer>();
		if (s != null) {
			String[] ss = s.split(",");
			for (String str : ss) {
				str = str.trim();
				if (!str.isEmpty()) {
					int id = Util.str2int(str);
					if (id > 0)
						list.add(id);
				}
			}
		}
		return list;

	}

	// 今天是2013-09-08，则返回20130908
	public static int getToday() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
	}

	// 得到名字的首字母拼音，名字简拼
	public static String getFirstPinyin(String Name) {
		StringBuilder sb = new StringBuilder();
		char[] arr = Name.toCharArray();
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					String[] t = PinyinHelper.toHanyuPinyinStringArray(arr[i], format);
					if (t != null) {
						sb.append(t[0].charAt(0));
					}
				} catch (Exception e) {
					log.warn("getFirstPinyin error, Pinyin:" + Name, e);
				}
			} else {
				sb.append(arr[i]);
			}
		}
		return sb.toString().replaceAll("\\W", "").trim();
	}

	// 得到名字的全拼
	public static String getHanyuPinyin(String Name) {
		StringBuilder sb = new StringBuilder();
		char[] arr = Name.toCharArray();
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] > 128) {
				try {
					sb.append(PinyinHelper.toHanyuPinyinStringArray(arr[i], format)[0]);
				} catch (Exception e) {
					log.warn("getHanyuPinyin error, Pinyin:" + Name, e);
				}
			} else {
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}

	public static String list2Str(Collection<?> items) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : items) {
			sb.append(obj).append(",");
		}
		if (sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	public static String toSqlInStr(Collection<?> items) {
		return toSqlInStr(items, 0);
	}

	public static String toSqlInStr(Collection<?> items, Object emptyObj) {
		StringBuilder sb = new StringBuilder("(");
		if (items == null || items.isEmpty()) {
			sb.append(emptyObj);
		} else {
			for (Object obj : items) {
				sb.append(obj).append(",");
			}
		}
		if (sb.charAt(sb.length() - 1) == ',')
			sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	public static int getDay(long time) {
		return getDay(new Date(time));
	}

	public static int getDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
	}

	public static String removeNewlineChar(String s) {
		if (s == null)
			return s;
		return s.replaceAll("\r\n", " ").replaceAll("\n", " ");
	}

	public static String getClassResource(String path) {
		try {
			URL url = Util.class.getClassLoader().getResource(path);
			if (url != null)
				return url.getFile();
		} catch (Exception e) {
			log.warn("getClassResource error, path:" + path, e);
		}
		return null;
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}
	
	public static String getWeekOfDate(Date date) {
		  String[] weekDaysName = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
		  Calendar calendar = Calendar.getInstance();
		  calendar.setTime(date);
		  int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		 for(int i=0;i<=6;i++){
			 switch(i){
			 case 0 : weekDaysName[i]="星期日";
			 break;
			 case 1 : weekDaysName[i]="星期一";
			 break;
			 case 2 : weekDaysName[i]="星期二";
			 break;
			 case 3 : weekDaysName[i]="星期三";
			 break;
			 case 4 : weekDaysName[i]="星期四";
			 break;
			 case 5 : weekDaysName[i]="星期五";
			 break;
			 case 6 : weekDaysName[i]="星期六";
			 break;
			 }
		 }
		  return weekDaysName[intWeek];
		} 

	/** 计算年龄 */
	public static String getAgeByBirth(String birthDay) {
			if(birthDay==null || birthDay.length()==0){
					return "" ;
		     }
		     Date birthDate = parseDate(birthDay,"yyyy-MM-dd");
		     Calendar birthCal = Calendar.getInstance();
		     birthCal.setTime(birthDate);
			 Calendar todayCal = Calendar.getInstance();
			 todayCal.setTime(new Date());
			 if (todayCal.before(birthCal)) {
				 return "" ;
			 }
			 int  day = todayCal.get(Calendar.DAY_OF_MONTH) - birthCal.get(Calendar.DAY_OF_MONTH);
		     int  month = todayCal.get(Calendar.MONTH) - birthCal.get(Calendar.MONTH);
		     int  year = todayCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
		     if (day < 0) {
		            month -= 1;
		            todayCal.add(Calendar.MONTH, -1 );//得到上一个月，用来得到上个月的天数。
		            day = day + todayCal.getActualMaximum(Calendar.DAY_OF_MONTH);
		     }
		     if (month < 0) {
		            month = (month + 12) % 12;
		            year--;
		    }
			StringBuilder sb = new StringBuilder();
			if (year == 0 && month != 0) {
				sb.append(month).append("月");
			} else if (year != 0 && month == 0) {
				sb.append(year).append("岁");
			} else {
				sb.append(year).append("岁").append(month).append("月");
			}
			return sb.toString();
	}
	
	public static Dimension getImageSize(String file) {
		Dimension d = new Dimension();
		try {
			String type = null;
			int pos = file.lastIndexOf('.');
			if (pos > 0)
				type = file.substring(pos + 1);
			if (type == null)
				return getImageSizeWithoutExtName(file);
			
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(type);
		    ImageReader reader = (ImageReader)readers.next();
		    ImageInputStream iis = ImageIO.createImageInputStream(new java.io.File(file));
		    reader.setInput(iis, true);
		    d.width = reader.getWidth(0);
			d.height = reader.getHeight(0);
		} catch (Exception e) {
			log.warn("getImageSize error! file:" + file, e);
			throw new RuntimeException(e);
		}
		return d;
	}
	
	private static Dimension getImageSizeWithoutExtName(String file) {
		Dimension d = new Dimension();
		try {
			BufferedImage img =ImageIO.read(new FileInputStream(file));  
			d.width = img.getWidth();
			d.height = img.getHeight();		
		} catch (Exception e) {
			log.warn("getImageSize error! file:" + file, e);
			throw new RuntimeException(e);
		}
		return d;
	}

	public static String locateFile(String file) {
		if (file == null)
			return null;
		String f = null;
		if (file.indexOf('/') >= 0 || file.indexOf('\\') >= 0)
			f = checkFileExists(file);
		if (f == null)
			f = checkFileExists(".." + File.separator + "conf" + File.separator + file);
		if (f == null)
			f = checkFileExists("." + File.separator + file);
		if (f == null)
			f = checkFileExists(file);
		if (f == null)
			f = checkFileExists(getClassPathFile(file));
		if (f == null)
			f = ".." + File.separator + "conf" + File.separator + file;
		return checkFileExists(f);
	}
	
	private static final String checkFileExists(String file) {
		return (file != null && new File(file).exists() ? file : null);
	}
	private static final String getClassPathFile(String file) {
		try {
			URL url = Util.class.getClassLoader().getResource(file);
			if (url != null)
				return url.getFile();
		} catch (Exception e) {
			
		}
		return null;
	}
	
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
    private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
    private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
    
    public static String delHTMLTag(String htmlStr) {
        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签

        return htmlStr.trim().replaceAll("&nbsp;", ""); // 返回文本字符串
    }
    
    public static String removeImgTag(String content, List<String> imgUrls) {
    	Pattern p = Pattern.compile("<img[\\s\\S]*?/>", Pattern.CASE_INSENSITIVE);
    	Matcher m = p.matcher(content);
    	while(m.find()) {
    		String img = m.group();
    		int pos1 = img.indexOf("src=");
    		if (pos1 > 0) {
    			int pos2 = img.indexOf(" ", pos1);
    			if (pos2 < 0)
    				pos2 = img.indexOf("/", pos1);
    			if (pos2 > 0) {
    				String url = img.substring(pos1 + "src=".length(), pos2);
    				imgUrls.add(url.replaceAll("'", "").replaceAll("\"", ""));    				
    			}
    		}
    	}
    	return m.replaceAll("");
    }
    
    public static List<String> fastSplit(String text, char c) {
		if (text != null && text.length() > 0) {
			List<String> list = new LinkedList<String>();
			int pos1 = 0;
			int pos2 = text.indexOf(c);
			String token;
			while (pos2 >= 0) {
				token = text.substring(pos1, pos2);
				if (!token.isEmpty())
					list.add(token);
				pos1 = pos2 + 1;
				pos2 = text.indexOf(c, pos1);
			}

			if (pos1 < text.length() - 1) {
				list.add(text.substring(pos1));
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}
    
    public static String createSimplePwd() {
		return createPwd(6);
    }
    
    public static String createPwd(int len) {
    	if (len < 6 || len > 20)
    		len = 6;
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < len; i++) {
			sb.append(String.valueOf(rd.nextInt(10)));
		}
    	return sb.toString();
    }
     
    public static boolean isStrEmpty(String s) {
    	return (s == null || s.trim().isEmpty());
    }
    
    public static String trimNumber(double d) {
    	long n = Math.round(d * 100);
    	String s = String.valueOf(n/100.0);
    	s = s.replaceFirst("[\\.][0][0-9]*", "");
    	return s;
    			
    }
    
    public static void main(String[] args) {
		System.out.println(trimNumber(1.1));
	}
    
	/*	
	 // 获得新图片的长和宽
	private static int[] getWidthHeight(int width, int height) {
		int nWidth = 0;
		int nHeight = 0;
		if (width <= 200 && height <= 200) {
			nWidth = width;
			nHeight = height;
		} else {
			nWidth = 200;
			nHeight = 200;
		}
		return new int[] { nWidth, nHeight };
	}
	 //图片处理
	public static void processImage(String srcImage, String destImage) {
		ImageInfo info = null;
		MagickImage image = null;
		Dimension imageDim = null;
		MagickImage scaled = null;
		try {
			info = new ImageInfo(srcImage);
			image = new MagickImage(info);
			imageDim = image.getDimension(); // 获得图片大小
			int width = imageDim.width;
			int height = imageDim.height;
			int[] wh = getWidthHeight(width, height);
			width = wh[0];
			height = wh[1];
			scaled = image.scaleImage(width, height);// 小图片的大小.  
	        scaled.setFileName(destImage);  
	        scaled.writeImage(info);
		} catch (Exception ex) {
			log.warn("processImage error,srcImage" + srcImage, ex);
		} finally {
			if (scaled != null)
				scaled.destroyImages();
		}
	}*/
	/*
	 * public static void main(String[] args) {
	 * System.out.println(Util.getToday()); // Student stu = new Student(); //
	 * stu.Id = 100; // stu.Name = "张三"; //
	 * System.out.println(Util.bean2Json(stu)); // StudentXlsParser parser = new
	 * StudentXlsParser(); // List<Student> stus =
	 * parser.parseXls("G:/student_data.xls"); // for (Student stu : stus) { //
	 * System.out.println(stu); //
	 * System.out.println(parser.getGuarders().get(stu)); //
	 * System.out.println(parser.getClasses().get(stu)); // //
	 * System.out.println("==================================="); // } //
	 * System.out.println(stus.size()); // public int Gender; // public int
	 * ClassId; // public int Type; // public int Status; // public String
	 * Birthday; // public String JoinDate; // public String Address; // public
	 * Timestamp Ts;
	 * 
	 * // System.out.println(Util.parseDate("2007年1月8日"));
	 * 
	 * //String Name="张三"; //System.out.println(getNameFirstSpell(Name));
	 * //System.out.println(getNameAllSpell(Name));
	 * 
	 * }
	 */
}
