package cn.wwerp;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import cn.wwerp.NotDBField;

public class WWDao {
		
	private final static Logger log = LoggerFactory.getLogger(WWDao.class);

	public JdbcTemplate jt;
	
	public <T> T queryObject(Class<T> c, Number id) {
		return queryObject("select * from " + c.getSimpleName() + " where Id=?", new Object[]{id}, new BeanPropertyRowMapper<T>(c));
	}
	
	public <T> T queryObject(Class<T> c, String[] fields, Object[] args) {
		String sql = createSql(c, fields);
		return queryObject(sql, args, new BeanPropertyRowMapper<T>(c));
	}
	
	public <T> T queryObject(Class<T> c, String sql, Object[] args) {
		List<T> list = query(sql, args, new BeanPropertyRowMapper<T>(c));
		return (list.isEmpty() ? null : list.get(0));
	}
	
	private <T> T queryObject(String sql, Object[] args, RowMapper<T> mapper) {
		List<T> list = query(sql, args, mapper);
		return (list.isEmpty() ? null : list.get(0));
	}
	
	public <T> T queryObject(T obj, String[] keyFields) {
		QueryCond qc = new QueryCond();
		qc.setKeyFields(keyFields);
		List<T> list = queryList(obj, qc);
		return (list.isEmpty() ? null : list.get(0));
	}
	private <T> List<T> query(String sql, Object[] args, RowMapper<T> mapper) {
		return jt.query(sql, args, mapper);		
	}
	
	private boolean isLetterOrDigit(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isLetterOrDigit(s.charAt(i)))
				return false;
		}
		return true;
	}
	
	private <T> String createSql(Class<T> c, String[] fields) {
		String sql = "select * from " + c.getSimpleName() + " where ";
		for (int i = 0; i < fields.length; i++) {
			if (i > 0)
				sql += " and ";
			if (isLetterOrDigit(fields[i]))
				sql += fields[i] + "=?";
			else
				sql += fields[i] + "?";
		}
		return sql;
	}
	
	public <T> List<T> queryList(Class<T> c, String[] fields, Object[] args) {
		return queryList(c, fields, args, null, null);
	}
	
	public <T> List<T> queryList(Class<T> c, String[] fields, Object[] args, String clause, String order) {
		String sql = createSql(c, fields);
		if (clause != null) {
			if (fields.length > 0)
				sql += " and";
			sql += " " + clause;
		}
		if (order != null)
			sql += " order by " + order;
		return query(sql, args, new BeanPropertyRowMapper<T>(c));
	}
	
	public <T> List<T> queryList(Class<T> c, String sql, Object[] args) {
		return query(sql, args, new BeanPropertyRowMapper<T>(c));
	}
	
	public int queryCount(String sql, Object[] args) {
		return queryForInt(sql, args);
	}
	
	public <T> int queryCount(T obj, String[] keyFields) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> c = (Class<T>)obj.getClass();
			
			Object[] args = new Object[keyFields.length];
			Field[] fields = c.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			String sql = "select count(*) from " + c.getSimpleName();
			if (keyFields.length > 0) {
				sql += " where ";
				for (int i = 0; i < keyFields.length; i++) {
					if (i > 0)
						sql += " and ";
					sql += keyFields[i] + "=?";
					args[i] = c.getField(keyFields[i]).get(obj);
				}
			}
			return jt.queryForInt(sql, args);
		} catch (Exception e) {
			log.warn("queryList error! obj:" + obj + ", fields:" + Arrays.asList(keyFields), e);
			return 0;
		}
	}
	
	private <T> String createSqlByQueryCond(T obj, QueryCond cond, boolean queryCount, List<Object> args) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> c = (Class<T>)obj.getClass();
			Field[] fields = c.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			String sql = "select * from " + c.getSimpleName();
			if (cond.getKeyFields().length > 0) {
				sql += " where 1=1 ";
				for (int i = 0; i < cond.getKeyFields().length; i++) {
					String f = cond.getKeyFields()[i];
					Object v = c.getField(f).get(obj);
					if (cond.isLikeField(f)) {
						if (v != null && !v.toString().trim().isEmpty()) {
							sql += (" and " + f + " like ?");
							
							args.add("%" + v + "%");
						}
					} else {
						if (!cond.isIgnore(f, v)) {
							sql += (" and " + f + "=?");	
							
							args.add(v);
						}
					}
				}
			}
			if (cond.getClause() != null) {
				if (fields.length > 0)
					sql += " and";
				sql += " " + cond.getClause();
			}
			
			if (queryCount)
				return sql.replace("*", "count(*)");
			
			if (cond.getOrder() != null)
				sql += " order by " + cond.getOrder();
			sql += " limit ?,?";
			args.add(cond.getStart());
			args.add(cond.getLimit());
			return sql;
		} catch (Exception e) {
			log.warn("createSqlByQueryCond error! obj:" + obj + ", cond:" + cond, e);
			return null;
		}
	}
	
	public <T> List<T> queryList(T obj, QueryCond cond) {
		try {
			@SuppressWarnings("unchecked")
			Class<T> c = (Class<T>)obj.getClass();
			
			List<Object> args = new ArrayList<Object>();
			String sql = createSqlByQueryCond(obj, cond, false, args);
			return queryList(c, sql, args.toArray());
		} catch (Exception e) {
			log.warn("queryList error! obj:" + cond + ", fields:" + cond, e);
			return null;
		}
	}
	
	public <T> int queryNum(T obj, QueryCond cond) {
		List<Object> args = new ArrayList<Object>();
		String sql = createSqlByQueryCond(obj, cond, true, args);
		return queryNum(sql, args.toArray(), 0);
	}
	
	public int queryNum(String sql, Object[] args, int defaultValue) {
		try {
			return jt.queryForInt(sql, args);			
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public List<Map<String, Object>> queryForMapList(String sql, Object... args) {
		return jt.queryForList(sql, args);
	}
	
	public Map<String, Object> queryForMap(String sql, Object... args) {
		try {
			return jt.queryForMap(sql, args);			
		} catch (Exception e) {
			return Collections.emptyMap();
		}
	}
	
	private int indexOf(String[] ss, String key) {
		for (int i = 0; i < ss.length; i++) {
			if (key.equals(ss[i]))
				return i;
		}
		return -1;
	}
	
	public int deleteMulti(String tableName, String field, Object[] args) {
		int ret = 0;
		try {
			String sql = "delete from " + tableName + " where " + field + "=?";
			for (Object arg : args) {
				ret += jt.update(sql, new Object[]{arg});
			}
		} catch (Exception e) {
			log.warn("delete error! tableName:" + tableName + ", field:" + field + ", args:" + Arrays.asList(args), e);
		}
		return ret;
	}
	
	public int delete(String tableName, String[] fields, Object[] args) {
		try {
			String sql = "delete from " + tableName + " where ";
			for (int i = 0; i < fields.length; i++) {
				if (i > 0)
					sql += " and ";
				sql += fields[i] + "=?";
			}
			return jt.update(sql, args);
		} catch (Exception e) {
			log.warn("delete error! tableName:" + tableName + ", fields:" + Arrays.asList(fields) + ", args:" + Arrays.asList(args), e);
			return -1;
		}
	}
	
	public int delete(Object obj, String[] keyFields) {
		try {
			Object[] args = new Object[keyFields.length];
			Class<?> c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			
			String sql = "delete from " + c.getSimpleName() + " where ";
			for (int i = 0; i < keyFields.length; i++) {
				if (i > 0)
					sql += " and ";
				sql += keyFields[i] + "=?";
				args[i] = c.getField(keyFields[i]).get(obj);
			}
			
			return jt.update(sql, args);
		} catch (Exception e) {
			log.warn("delete error! obj:" + obj + ", fields:" + Arrays.asList(keyFields), e);
			return -1;
		}
	}
	
	public int update(Object obj, String[] keyFields) {
		List<String> updateFields = new ArrayList<String>();
		
		try {
			Class<?> c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			AccessibleObject.setAccessible(fs, true);
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				Class<?> type = f.getType();
				if (Modifier.isStatic(f.getModifiers())) //static field
					continue;
				if (f.getAnnotation(NotDBField.class) != null)
					continue;
				
				Object v = f.get(obj);
				if (type.equals(Integer.class) || type.equals(int.class)) {
					if (f.getInt(obj) == 0)
						continue;
				} else if (type.equals(Short.class) || type.equals(short.class)) {
					if (f.getShort(obj) == 0)
						continue;
				} else if (type.equals(Long.class) || type.equals(long.class)) {
					if (f.getLong(obj) == 0)
						continue;
				} else {
					if (v == null)
						continue;
				}
				
				updateFields.add(f.getName());
			}
		} catch (Exception e) {
			log.warn("update error! obj:" + obj + ", fields:" + Arrays.asList(keyFields), e);
			return -1;
		}
		
		for (String f : keyFields) {
			updateFields.remove(f);
		}
		
		return update(obj, keyFields, updateFields.toArray(new String[updateFields.size()]));
	}
	
	public int update(Object obj, String[] keyFields, String[] updateFields) {
		try {
			List<Object> keyArgs = new ArrayList<Object>(keyFields.length);
			List<Object> updateArgs = new ArrayList<Object>(updateFields.length);
			for (int i = 0; i < keyFields.length; i++)
				keyArgs.add(null);
			for (int i = 0; i < updateFields.length; i++)
				updateArgs.add(null);
			
			Class<?> c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			AccessibleObject.setAccessible(fields, true);
			
			int idx;
			String sql = "update " + c.getSimpleName() + " set ";
			for (int i = 0; i < updateFields.length; i++) {
				if (i > 0)
					sql += ", ";
				sql += updateFields[i] + "=?";					
			}
			sql += " where ";
			for (int i = 0; i < keyFields.length; i++) {
				if (i > 0)
					sql += " and ";
				sql += keyFields[i] + "=?";
			}
			
			for (Field f : fields) {
				String name = f.getName();
				if ((idx = indexOf(keyFields, name)) >= 0)
					keyArgs.set(idx, f.get(obj));
				else if ((idx = indexOf(updateFields, name)) >= 0)
					updateArgs.set(idx, f.get(obj));
			}
			
			List<Object> args = new ArrayList<Object>();
			args.addAll(updateArgs);
			args.addAll(keyArgs);
			
			return jt.update(sql, args.toArray(new Object[args.size()]));
		} catch (Exception e) {
			log.warn("update " + obj + " error!", e);
			return -1;
		}
	}
	
	public int insert(Object obj) {
		return insert(obj, (AtomicLong)null);
	}
	public int insert(Object obj, AtomicLong idHolder) {
		return insertIgnoreFields(obj, null, idHolder);
	}
	
	public int insertIgnoreFields(Object obj, String[] ignoreFields, AtomicLong idHolder) {
		try {
			List<Object> args = new ArrayList<Object>();
			Set<String> sIgnoreFields = new HashSet<String>();
			if (ignoreFields != null){
				for (String f : ignoreFields)
					sIgnoreFields.add(f);
			}
			
			Class<?> c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			AccessibleObject.setAccessible(fs, true);
			
			String sql = "insert into " + c.getSimpleName() + "(";
			String values = "";
			for (int i = 0; i < fs.length; i++) {
				Field f = fs[i];
				if (f.getAnnotation(NotDBField.class) != null)
					continue;
				if (sIgnoreFields.contains(f.getName()) || Modifier.isStatic(f.getModifiers()))
					continue;
				
				Class<?> type = f.getType();
				Object v = f.get(obj);
				if (type.equals(Integer.class) || type.equals(int.class)) {
					if (f.getInt(obj) == 0)
						continue;
				} else if (type.equals(Short.class) || type.equals(short.class)) {
					if (f.getShort(obj) == 0)
						continue;
				} else if (type.equals(Long.class) || type.equals(long.class)) {
					if (f.getLong(obj) == 0)
						continue;
				} else {
					if (v == null)
						continue;
				}
				
				if (!args.isEmpty()) {
					sql += ", ";
					values += ", ";
				}
				sql += f.getName();
				values += "?";
				args.add(v);
			}
			sql += ") values (" + values + ")";
			if (idHolder != null)
				return doInsert(sql, args.toArray(), idHolder);
			else
				return jt.update(sql, args.toArray(new Object[args.size()]));
		} catch (Exception e) {
			log.warn("insert " + obj + " error!", e);
			return -1;
		}
	}
	
	public int insert(Object obj, String[] fields) {
		return insert(obj, fields, null);
	}
	
	public int insert(Object obj, String[] fields, AtomicLong idHolder) {
		try {
			List<Object> args = new ArrayList<Object>();
			
			Class<?> c = obj.getClass();
			Field[] fs = c.getDeclaredFields();
			AccessibleObject.setAccessible(fs, true);
			
			String sql = "insert into " + c.getSimpleName() + "(";
			for (int i = 0; i < fields.length; i++) {
				Field f = c.getDeclaredField(fields[i]);
				if (f == null)
					continue;
				if (f.getAnnotation(NotDBField.class) != null)
					continue;
				if (i > 0)
					sql += ", ";
				sql += fields[i];
				args.add(f.get(obj));
			}
			sql += ") values (";
			for (int i = 0; i < fields.length; i++) {
				if (i > 0)
					sql += ", ";
				sql += "?";
			}
			sql += ")";
			if (idHolder != null)
				return doInsert(sql, args.toArray(), idHolder);
			else
				return jt.update(sql, args.toArray(new Object[args.size()]));
		} catch (Exception e) {
			log.warn("insert " + obj + " error!", e);
			return -1;
		}
	}
	
	public int insert(String table, String[] fields, final Object[] values, AtomicLong idHolder) {
		try {
			String sql = "insert into " + table + "(";
			for (int i = 0; i < fields.length; i++) {
				if (i > 0)
					sql += ", ";
				sql += fields[i];
			}
			sql += ") values (";
			for (int i = 0; i < fields.length; i++) {
				if (i > 0)
					sql += ", ";
				sql += "?";
			}
			sql += ")";
			if (idHolder != null)
				return doInsert(sql, values, idHolder);
			else
				return jt.update(sql, values);
		} catch (Exception e) {
			log.warn("insert " + table + " error!", e);
			return -1;
		}
	}

	private int doInsert(String sql, final Object[] values, AtomicLong idHolder) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final String _sql = sql;
		int ret = jt.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(_sql, Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < values.length; i++) {
					StatementCreatorUtils.setParameterValue(ps, i+1, SqlTypeValue.TYPE_UNKNOWN, values[i]);							
				}
				return ps;
			}
		}, keyHolder);
		if (keyHolder.getKey() != null)
			idHolder.set(keyHolder.getKey().longValue());
		return ret;
	}
	
	public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
		return jt.batchUpdate(sql, batchArgs);
	}
	
	public int queryForInt(String sql, Object... args) {
		try {
			return jt.queryForInt(sql, args);	
		} catch (Exception e) {
			//log.warn("queryForInt error! sql:" + sql + ", args:" + args, e);
			return 0;
		}
	}
	
	public long queryForLong(long defaultValue, String sql, Object... args) {
		try {
			return jt.queryForLong(sql, args);	
		} catch (EmptyResultDataAccessException ex) {
			return defaultValue;
		} catch (Exception e) {
			log.warn("queryForLong error! sql:" + sql + ", args:" + args, e);
			return defaultValue;
		}
	}
	
	public long queryForLong(String sql, Object... args) {
		return queryForLong(0, sql, args);
	}
	
	public List<Integer> queryForIntList(String sql, Object... args) {
		final List<Integer> list = new ArrayList<Integer>();
		try {
			jt.query(sql, args, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					list.add(rs.getInt(1));
				}
				
			});	
		} catch (Exception e) {
			log.warn("queryForIntList error! sql:" + sql + ", args:" + args, e);
		}
		return list;
	}
	
	public List<Long> queryForLongList(String sql, Object... args) {
		final List<Long> list = new ArrayList<Long>();
		try {
			jt.query(sql, args, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					list.add(rs.getLong(1));
				}
				
			});	
		} catch (Exception e) {
			log.warn("queryForLongList error! sql:" + sql + ", args:" + args, e);
		}
		return list;
	}
	
	public List<String> queryForStrList(String sql, Object... args) {
		final List<String> list = new ArrayList<String>();
		try {
			jt.query(sql, args, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					list.add(rs.getString(1));
				}
				
			});	
		} catch (Exception e) {
			log.warn("queryForStrList error! sql:" + sql + ", args:" + args, e);
		}
		return list;
	}
	
	public String queryForString(String sql, Object... args) {
		final List<String> list = new ArrayList<String>();
		try {
			jt.query(sql, args, new RowCallbackHandler() {

				@Override
				public void processRow(ResultSet rs) throws SQLException {
					list.add(rs.getString(1));
				}
				
			});	
		} catch (Exception e) {
			log.warn("queryForString error! sql:" + sql + ", args:" + args, e);
		}
		return (list.isEmpty() ? null : list.get(0));
	}
	
	public int update(String sql, Object... args) {
		return jt.update(sql, args);
	}
	
	public int createId() {
		AtomicLong idHolder = new AtomicLong();
		insert("IdCreator", new String[]{}, new Object[]{}, idHolder);
		return idHolder.intValue();
	}
	
	public boolean isAvatarExist(long id) {
		return isBlobExist(id, "Avatar");
	}
	
	public boolean isBlobExist(long id, String table) {
		return (queryForInt("select Id from  " + table + " where Id=?", new Object[]{id}) > 0);
	}
	
	public boolean setBlob(final long id, String table, byte[] data) {
		if (data == null || data.length == 0)
			return false;
		
		int c = jt.queryForInt("select count(Id) from " + table + " where Id=?", new Object[]{id});
		if (c == 0)
			return jt.update("insert into " + table + "(Id, Data) values(?,?)", new Object[]{id, data}) > 0;
		else
			return jt.update("update " + table + " set Data=? where Id=?", new Object[]{data, id}) > 0;		
	}
	
	public long addBlob(String table, final byte[] data) {
		if (data == null || data.length == 0)
			return 0;
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = "insert into " + table + "(Data) values(?)";
		int ret = jt.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				StatementCreatorUtils.setParameterValue(ps, 1, SqlTypeValue.TYPE_UNKNOWN, data);
				return ps;
			}
		}, keyHolder);
		if (keyHolder.getKey() != null)
			return keyHolder.getKey().longValue();
		else
			return ret;
	}
	
	public byte[] getBlob(final long id, String table) {
		final List<byte[]> list = new ArrayList<byte[]>();			
		jt.query("select Data from " + table + " where Id=?", new Object[]{id},  new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				list.add(rs.getBytes(1));
			}
			
		});
		if (list.size() > 0)
			return list.get(0);
		else
			return new byte[0];
	}
	
	public byte[] getSignSnap(int DeviceId, long id) {
		final List<byte[]> list = new ArrayList<byte[]>();			
		jt.query("select Data from SignSnap where DeviceId=? and Id=?", new Object[]{DeviceId, id},  new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				list.add(rs.getBytes(1));
			}
			
		});
		if (list.size() > 0)
			return list.get(0);
		else
			return new byte[0];
	}

	public JdbcTemplate getJt() {
		return jt;
	}
	
	public void setJt(JdbcTemplate jt) {
		this.jt = jt;
	}
}
