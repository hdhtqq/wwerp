package cn.wwerp;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;

import cn.wwerp.util.Cache;
import cn.wwerp.util.Util;

public class WWService {
		
	private final static Logger log = LoggerFactory.getLogger(WWService.class);

	private WWDao dao;
	
	private Cache<List<ItemType>> cacheTypes;
	private Cache<List<TypeClass>> cacheTypeClasses;
	private Cache<List<ItemDetail>> cacheItemDetail;
	
	public List<TypeClass> getTypeClasses() {
		String key = TypeClass.class.getSimpleName();
		List<TypeClass> cs = cacheTypeClasses.get(key);
		if (cs == null) {
			cs = dao.queryList(TypeClass.class, "select * from TypeClass order by Id", new Object[]{});
			cacheTypeClasses.set(key, cs);
		}
		return cs;
	}
	
	public TypeClass getTypeClass(int id) {
		List<TypeClass> cs = getTypeClasses();
		for (TypeClass t : cs) {
			if (t.Id == id)
				return t;
		}
		return null;
	}
	
	private String getCacheTypeKey() {
		return ItemType.class.getSimpleName();
	}
	
	public List<ItemType> getTypes() {
		String key = getCacheTypeKey();
		List<ItemType> types = cacheTypes.get(key);
		if (types == null) {
			String sql = "select a.* from ItemType a, TypeClass b where a.ClassId=b.Id order by b.Idx,a.Id";
			types = dao.queryList(ItemType.class, sql, new Object[]{});
			cacheTypes.set(key, types);
		}
		return types;
	}
	
	public List<ItemType> getTypes(int type) {
		List<ItemType> all = getTypes();
		List<ItemType> list = new ArrayList<ItemType>();
		for (ItemType item : all) {
			if (item.Type == type)
				list.add(item);
		}
		return list;
	}
	
	public ItemType getItemType(int id) {
		List<ItemType> all = getTypes();
		for (ItemType item : all) {
			if (item.Id == id)
				return item;
		}
		return null;
	}
	
	public List<Item> getItems(String month) {
		return dao.queryList(Item.class, "select * from Item where ItemDate like ? order by ItemDate desc", new Object[]{month + "%"});
	}
	
	public void updateItemTypePrice(int id, float price) {
		ItemType type = getItemType(id);
		if (type == null || Math.round(type.Price * 100) == Math.round(price * 100))
			return;
		type.Price = price;
		dao.update("update ItemType set Price=? where Id=?", new Object[]{price, id});
	}
	
	public void updateItemType(ItemType item) {
		dao.update(item, new String[]{"Id"});
		cacheTypes.remove(getCacheTypeKey());
	}
	
	public void deleteItemType(int id) {
		dao.jt.update("delete from ItemType where Id=?", new Object[]{id});	 
		cacheTypes.remove(getCacheTypeKey());
	}
	
	public void addItemType(ItemType item) {
		dao.insert(item);
		cacheTypes.remove(getCacheTypeKey());
	}
	
	public ItemDetail getItemDetail(int itemId, int typeId, int type) {
		List<ItemDetail> all = getItemDetails(itemId, type);
		for (ItemDetail d : all) {
			if (d.TypeId == typeId)
				return d;
		}
		return null;
	}
	
	public ItemDetail getItemDetail(int itemId, int id) {
		List<ItemDetail> all = getItemDetails(itemId, 0);
		for (ItemDetail d : all) {
			if (d.Id == id)
				return d;
		}
		return null;
	}
	
	public List<ItemDetail> getItemDetails(int itemId, int type) {
		List<ItemDetail> all = cacheItemDetail.get(itemId);
		if (all == null) {
			String sql = "select * from ItemDetail where ItemId=?";
			all = dao.queryList(ItemDetail.class, sql,	new Object[]{itemId});
			cacheItemDetail.set(itemId, all);
		}
		
		if (type == 0)
			return all;
		
		List<ItemType> types = getTypes(type);
		List<ItemDetail> list = new ArrayList<ItemDetail>();
		for (ItemDetail d : all) {
			for (ItemType t : types) {
				if (d.TypeId == t.Id) {
					list.add(d);
					break;
				}
			}
		}
		return list;
	}
	
	private boolean isFloadEqual(float f1, float f2) {
		return ((int)(100 * f1) == (int)(100 * f2));
	}
	
	public void saveItem(Item item, List<ItemDetail> details) {
		long t = System.currentTimeMillis();
		if (item.Id > 0) {
			dao.update(item, new String[]{"Id"});
		} else {
			AtomicLong idHolder = new AtomicLong();
			dao.insert(item, idHolder);
			item.Id = (int)idHolder.get();
		}
		
		int update = 0;
		int insert = 0;
		if (item.Id > 0) {
			for (ItemDetail d : details) {
				if (d.Id > 0) {
					if (d.Id == 788)
						d.Id = 788;
					ItemDetail old = getItemDetail(item.Id, d.Id);
					if (!isFloadEqual(old.Price, d.Price) || !isFloadEqual(old.Quantity, d.Quantity)
							|| !isFloadEqual(old.Amount, d.Amount) || !old.Remark.equals(d.Remark)) {
						dao.update(d, new String[]{"Id"});
						updateItemTypePrice(d.TypeId, d.Price);
						update++;						
					}
				} else {
					d.setItemId(item.Id);
					dao.insert(d);
					updateItemTypePrice(d.TypeId, d.Price);
					insert++;
				}
			}
		}
		
		cacheItemDetail.remove(item.Id);
		t = System.currentTimeMillis() - t;
		log.info("saveItem, time:" + t + ", item:" + item + ", details:" + details.size() + "/" + insert + "/" + update + details);
	}
	
	public void deleteItemDetails(int itemId, List<Integer> detailIds) {
		String in = Util.toSqlInStr(detailIds);
		dao.update("delete from ItemDetail where ItemId=? and Id in " + in, new Object[]{itemId});
		cacheItemDetail.remove(itemId);
		log.info("deleteItemDetails, item:" + itemId + ", detailIds:" + detailIds);
	}
	
	public Map<String, String> getStatMonths() {
		final Map<String, String> m = new LinkedHashMap<String, String>();
		m.put(Util.formatDate(new Date(), "yyyy-MM"), Util.formatDate(new Date(), "yyyy年MM月"));
		dao.getJt().query("select distinct left(ItemDate, 7) t1, left(ItemDate, 7) t2 "
				+ " from Item order by t1 desc limit 12", new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				m.put(rs.getString(1), rs.getString(2));
			}
			
		});
		
		return m;
	}
	
	
	public List<StatItem> getStatItems(String month) {
		final Map<String, StatItem> items = new LinkedHashMap<String, StatItem>();
		
		/*
		//收入
		dao.jt.query("select date_format(a.Ts, '%Y-%m-%d') d, sum(Amount) a " 
				+ " from ItemDetail a, ItemType b "
				+ " where a.TypeId=b.Id and b.Type=? and date_format(a.Ts, '%Y-%m')=?"
				+ " order by d desc", 
				new Object[] {ItemType.INCOMINGS, month}, new RowCallbackHandler() {

					@Override
					public void processRow(ResultSet rs) throws SQLException {
						String date = rs.getString("d");
						StatItem item = items.get(date);
						if (item == null) {
							item = new StatItem();
							item.time = date;
							item.Incoming = rs.getInt("a");
							items.put(date, item);
						}
					}
					
				});
				*/
		
		List<ItemDetail> list = dao.queryList(ItemDetail.class, "select * from ItemDetail a where date_format(Ts, '%Y-%m')=? order by Ts desc", new Object[] {month});
		for (ItemDetail d : list) {
			String time = Util.formatDate(d.Ts, "yyyy-MM-dd");
			StatItem item = items.get(time);
			if (item == null) {
				item = new StatItem();
				item.time = time;
				item.IncomingDesc = "";
				item.OutgoingDesc = "";
				items.put(time, item);
			}
			ItemType t = getItemType(d.TypeId);
			String desc = t.Name + String.valueOf(d.Quantity).replace(".0", "") + t.Unit + "&nbsp;";
			if (t.Type == ItemType.INCOMINGS) {
				item.Incoming += d.Amount;
				item.IncomingDesc += desc;
			} else {
				item.Outgoing += d.Amount;
				item.OutgoingDesc += desc;
			}
		}
		return new ArrayList<StatItem>(items.values());
	}
	
	public void setCacheTypes(Cache<List<ItemType>> cacheTypes) {
		this.cacheTypes = cacheTypes;
	}

	public void setCacheTypeClasses(Cache<List<TypeClass>> cacheTypeClasses) {
		this.cacheTypeClasses = cacheTypeClasses;
	}

	public void setCacheItemDetail(Cache<List<ItemDetail>> cacheItemDetail) {
		this.cacheItemDetail = cacheItemDetail;
	}

	public void setDao(WWDao dao) {
		this.dao = dao;
	}
}
