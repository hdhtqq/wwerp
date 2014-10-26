package cn.wwerp;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import cn.wwerp.util.Constants;
import cn.wwerp.util.JsonBuilder;
import cn.wwerp.util.Util;

public class JspUtil extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final static Logger log = LoggerFactory.getLogger(JspUtil.class);

	public static WebApplicationContext ctx;
	public static WWService wwService;
	
	private static ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<HttpServletResponse>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ctx = (WebApplicationContext) getServletContext().getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		wwService = ctx.getBean("wwService", WWService.class);
	}

	public static void set(HttpServletRequest request, HttpServletResponse response) {
		currentRequest.set(request);
		currentResponse.set(response);
	}
	
	public static void remove() {
		currentRequest.remove();
		currentResponse.remove();
	}
	
	public static String createConstansSelect(Constants.Name name, String selName, String initValue, String notSelKey,
			String notSelValue) {
		if (initValue == null)
			initValue = notSelValue;
		if (initValue == null)
			initValue = "";
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"" + selName + "\">");
		if (notSelKey != null && notSelValue != null)
			sb.append("<option value=\"" + notSelKey + "\"" + (initValue.equals(notSelKey) ? "selected" : "") + ">"
					+ notSelValue + "</option>");
		for (Map.Entry<Integer, String> entry : Constants.getItems(name).entrySet()) {
			sb.append("<option value=\"" + entry.getKey() + "\"" + (initValue.equals(entry.getKey()) ? "selected" : "")
					+ ">" + entry.getValue() + "</option>");
		}
		sb.append("</select>");
		return sb.toString();
	}

	public static String params2Json(HttpServletRequest request) {
		Map<String, String> m = Collections.emptyMap();
		return params2Json(request, m);
	}

	public static List<NameValue> getParamsNameValues(HttpServletRequest request) {
		List<NameValue> list = new ArrayList<NameValue>();
		Map<?, ?> map = request.getParameterMap();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String[] vv = (String[]) entry.getValue();
			for (String v : vv) {
				NameValue nv = new NameValue();
				nv.Name = String.valueOf(entry.getKey());
				nv.Value = v;
				list.add(nv);
			}
		}
		return list;
	}

	public static String params2Json(HttpServletRequest request, Map<String, String> initValues) {
		JsonBuilder json = new JsonBuilder();
		Map<?, ?> map = request.getParameterMap();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String[] v = (String[]) entry.getValue();
			json.append(String.valueOf(entry.getKey()), v[0]);
		}
		for (Map.Entry<String, String> entry : initValues.entrySet()) {
			if (!map.containsKey(entry.getKey()))
				json.append(String.valueOf(entry.getKey()), entry.getValue());
		}

		return json.toString();
	}

	public static String paramValues2Str(HttpServletRequest request, String name) {
		StringBuilder sb = new StringBuilder();
		String[] vv = request.getParameterValues(name);
		if (vv != null) {
			for (String v : vv) {
				sb.append(v).append(",");
			}
		}
		return sb.toString();
	}
	

	public static String getCookie(HttpServletRequest request, String name) {
		if (name == null)
			return null;
		Cookie[] cs = request.getCookies();
		if (cs != null) {
			for (Cookie c : cs) {
				if (c != null && name.equals(c.getName())) {
					return c.getValue();
				}
			}			
		}
		return null;
	}

	public static String htmlStr(String s) {
		if (s == null || s.trim().isEmpty())
			return "&nbsp;";
		s = s.replaceAll("\r\n", "<br/>").replaceAll("\n", "<br/>");
		return s;
	}

	public static <T> List<List<T>> createLineShowList(List<T> all, int lineCount) {
		if (all == null)
			return Collections.emptyList();
		
		List<List<T>> list = new ArrayList<List<T>>();

		List<T> ls = new ArrayList<T>();
		for (T t : all) {
			ls.add(t);

			if (ls.size() >= lineCount) {
				list.add(ls);
				ls = new ArrayList<T>();
			}
		}
		if (!ls.isEmpty())
			list.add(ls);
		return list;
	}

	public static boolean isIE6(HttpServletRequest request) {
		return (request.getHeader("user-agent").indexOf("MSIE 6.") > -1);
	}
	
	public static boolean isIE(HttpServletRequest request) {
		return (request.getHeader("user-agent").indexOf("MSIE") > -1);
	}

	public static String encryptId(int id) {
		return encryptId(session(), id);
	}
	
	public static String encryptId(HttpSession session, int id) {
		return encryptId(session, String.valueOf(id));
	}
	
	public static String encryptId(HttpSession session ,long id){
		return encryptId(session,String.valueOf(id));
	}
	
	public static String encryptId(HttpSession session, String id) {
		String key = (String)session.getAttribute("ID_ENCRYPT_KEY");
		if (key == null) {
			long n = Util.rd.nextLong();
			if (n < 0)
				n = -n;
			n += 100000000;
			key  = String.valueOf(n).substring(0, 8);
			session.setAttribute("ID_ENCRYPT_KEY", key);
		}
		if (key != null)
			return Util.desEncrypt(id, key);
		else
			return id;
	}
	
	public static int getIntParam(String name) {
		return getIntParam(name, true);
	}
	
	public static String getStrParam(String name) {
		String v = currentRequest.get().getParameter(name);
		if (v == null)
			v = "";
		return v.trim();
	}
	
	public static int getIntParam(String name, boolean decrypt) {
		String v = currentRequest.get().getParameter(name);
		if (v == null)
			return 0;
		
		if (decrypt)
			return decryptIdInt(v);
		else
			return Util.str2int(v);
	}
	
	public static String decryptId(HttpSession session, String id) {
		if (session == null)
			return id;
		String key = (String)session.getAttribute("ID_ENCRYPT_KEY");
		if (key != null)
			return Util.desDecrypt(id, key);
		else
			return id;
	}
	
	public static int decryptIdInt(String id) {
		return decryptIdInt(session(), id);
	}
	
	public static int decryptIdInt(HttpSession session, String id) {
		return Util.str2int(decryptId(session, id));
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
							if (Util.isDigit(s))
								n |= Util.str2int(s);
						}
						if (n > 0)
							f.setInt(obj, n);
					}
					continue;
				}

				String v = null;
				Object se = request.getSession().getAttribute(f.getName());
				if (se != null)
					v = String.valueOf(se);
				if (v == null)
					v = request.getParameter(f.getName());
				if (v == null)
					continue;
				Util.setField(obj, f, v);
			}
		} catch (Exception e) {
			log.warn("fillBean error! bean:" + obj, e);
		}
	}
	
	public static HttpServletRequest request() {
		return currentRequest.get();
	}
	
	public static HttpServletResponse response() {
		return currentResponse.get();
	}
	
	public static HttpSession session() {
		return currentRequest.get().getSession();
	}
	
	public static void processResult(boolean success) throws IOException {
		processResult(0, success, null, null);
	}
	
	public static void processResult(int type, boolean success, String text, String backUrl) throws IOException {
		String url = "/process_result.jsp?Success=" + success;
		if (type > 0) 
			url += "&Type=" + type;
		if (text != null)
			url += "&Text=" + Util.urlEncode(text);
		if (backUrl != null)
			url += "&BackUrl=" + Util.urlEncode(backUrl);
		
		JspUtil.response().sendRedirect(url);
	}
	
	public static String getIp() {  
		HttpServletRequest request = request();
		if (request == null)
			return null;
        String ip = request.getHeader("x-forwarded-for");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip;  
    } 
}
