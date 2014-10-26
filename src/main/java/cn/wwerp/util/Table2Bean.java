package cn.wwerp.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Table2Bean {
	
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("yxon.xml");
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		
		List<String> tables = new ArrayList<String>();
		Connection con = ds.getConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("show tables");
		while (rs.next()) {
			tables.add(rs.getString(1).trim());
		}
		rs.close();
		st.close();
		con.close();
		
		for (String table : tables) {
			if (table.equals("TimerTask"))
				createBean("C:/yxon/yxon/src/main/java/cn/zpon/yxon/bean/", table, ds);			
		}
	}

	private static void createBean(String path, String table, DataSource ds) throws Exception {
		Connection con = ds.getConnection();
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery("select * from " + table);
		Set<String> imports = new HashSet<String>();
		List<String> fields = new ArrayList<String>();
		List<String> methods = new ArrayList<String>();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			int type = rsmd.getColumnType(i);
			String name = rsmd.getColumnName(i);
			if (type == Types.VARCHAR) {
				fields.add("	public String " + name + ";");
				
				methods.add("	public String get" + name + "() {");
				methods.add("		return " + name + ";");
				methods.add("	}");
				
				methods.add("	public void set" + name + "(String " + name + ") {");
				methods.add("		this." + name + " = " + name + ";");
				methods.add("	}");
			} else if (type == Types.INTEGER) {
				fields.add("	public int " + name + ";");
				
				methods.add("	public int get" + name + "() {");
				methods.add("		return " + name + ";");
				methods.add("	}");
				
				methods.add("	public void set" + name + "(int " + name + ") {");
				methods.add("		this." + name + " = " + name + ";");
				methods.add("	}");
			} else if (type == Types.BIGINT) {
				fields.add("	public long " + name + ";");
				
				methods.add("	public long get" + name + "() {");
				methods.add("		return " + name + ";");
				methods.add("	}");
				
				methods.add("	public void set" + name + "(long " + name + ") {");
				methods.add("		this." + name + " = " + name + ";");
				methods.add("	}");
			} else if (type == Types.TIMESTAMP) {
				imports.add("import java.sql.Timestamp;");
				
				fields.add("	public Timestamp " + name + ";");
				
				methods.add("	public Timestamp get" + name + "() {");
				methods.add("		return " + name + ";");
				methods.add("	}");
				
				methods.add("	public void set" + name + "(Timestamp " + name + ") {");
				methods.add("		this." + name + " = " + name + ";");
				methods.add("	}");
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(path + table + ".java"));
		
		bw.write("package cn.zpon.yxon.bean;");
		bw.newLine();
		bw.newLine();
		
		for (String s : imports) {
			bw.write(s);
			bw.newLine();	
		}
		
		bw.newLine();
		
		bw.write("public class " + table + " extends BaseBean {");
		bw.newLine();
		
		for (String s : fields) {
			bw.write(s);
			bw.newLine();	
		}
		
		bw.newLine();
		
		for (String s : methods) {
			bw.write(s);
			bw.newLine();	
		}
		
		bw.write("}");
		bw.close();
		rs.close();
		st.close();
		con.close();
	}
}
