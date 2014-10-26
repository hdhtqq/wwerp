package cn.wwerp.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.ehcache.Element;

public class Cache<V> {

	private String keySuffix;
	
	private net.sf.ehcache.Cache localCache;
	private McClient memCache;
	
	private long memCacheTimeToLiveMs = 24 * 3600 * 1000L;
	private Date dMemCacheTimeToLiveMs = new Date(memCacheTimeToLiveMs);
	
	private boolean cacheNullObj = false;
	
	public V get(int id) {
		return get(String.valueOf(id));
	}
	
	public V get(long id) {
		return get(String.valueOf(id));
	}
	
	@SuppressWarnings("unchecked")
	public V get(String key) {
		V v = null;
		key = getFullKey(key);
		Element e;
		if (localCache != null && (e = localCache.get(key)) != null) {
			v = (V)e.getObjectValue();
		} else if (memCache != null) {
			v = (V)memCache.get(key);
			if (v != null) {
				if (localCache != null)
					localCache.put(new Element(key, v));
			}
		}
		return v;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, V> get(Collection<String> keys) {
		if (memCache != null) {
			String[] ss = new String[keys.size()];
			int i = 0;
			for (String key : keys) {
				ss[i++] = getFullKey(key);
			}
			Map<String, V> map = (Map<String, V>)memCache.getMulti(ss);
			if (keySuffix != null && !keySuffix.isEmpty()) {
				Map<String, V> m = new HashMap<String, V>();
				for (Map.Entry<String, V> entry : map.entrySet()) {
					m.put(getBareKey(entry.getKey()), entry.getValue());
				}
				map = m;
			}
			return map;
		} else {
			return new HashMap<String, V>();
		}
	}
	
	public Element set(int id, V v) {
		return set(String.valueOf(id), v);
	}
	
	public Element set(long id, V v) {
		return set(String.valueOf(id), v);
	}
	
	public Element set(String key, V v) {
		return set(key, v, 0);
	}
	
	public Element set(String key, V v, int liveTimeSeconds) {
		if (v == null && !cacheNullObj)
			return null;
		
		String fullkey = getFullKey(key);
		if (memCache != null) {
			if (liveTimeSeconds > 0)
				memCache.set(fullkey, v, new Date(liveTimeSeconds * 1000L));
			else
				memCache.set(fullkey, v, dMemCacheTimeToLiveMs);
		}
		
		if (localCache != null) {
			Element e = new Element(fullkey, v);
			localCache.put(e);
			return e;
		} else {
			return null;
		}
	}
	
	public void remove(int id) {
		remove(String.valueOf(id));
	}
	
	public void remove(long id) {
		remove(String.valueOf(id));
	}
	
	public void remove(String key) {
		key = getFullKey(key);
		
		if (localCache != null)
			localCache.remove(key);
		if (memCache != null)
			memCache.delete(key);
	}
	
	public boolean removeLocal(String key) {
		if (localCache != null) {
			return localCache.remove(getFullKey(key));
		} else {
			return false;
		}
	}
	
	public String getFullKey(String key) {
		return (keySuffix == null ? key : key + keySuffix);
	}
	
	private final String getBareKey(String fullKey) {
		return fullKey.substring(0, fullKey.length() - keySuffix.length());
	}

	public void setLocalCache(net.sf.ehcache.Cache localCache) {
		this.localCache = localCache;
	}

	public void setMemCache(McClient memCache) {
		this.memCache = memCache;
	}

	public void setKeySuffix(String keySuffix) {
		this.keySuffix = keySuffix;
	}

	public void setMemCacheTimeToLiveMs(long memCacheTimeToLiveMs) {
		this.memCacheTimeToLiveMs = memCacheTimeToLiveMs;
		dMemCacheTimeToLiveMs = new Date(memCacheTimeToLiveMs);
	}
	
	public void setCacheNullObj(boolean cacheNullObj) {
		this.cacheNullObj = cacheNullObj;
	}
}
