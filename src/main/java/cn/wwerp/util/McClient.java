package cn.wwerp.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

public class McClient {
	
	private List<String> mcServers;
	
	protected static MemCachedClient mcc = new MemCachedClient();
	
	public void init() {
		// grab an instance of our connection pool
		SockIOPool pool = SockIOPool.getInstance();

		// set the servers and the weights
		pool.setServers( mcServers.toArray(new String[0]) );

		// set some basic pool settings
		// 5 initial, 5 min, and 250 max conns
		// and set the max idle time for a conn
		// to 6 hours
		pool.setInitConn( 5 );
		pool.setMinConn( 5 );
		pool.setMaxConn( 250 );
		pool.setMaxIdle( 1000 * 60 * 60 * 6 );

		// set the sleep for the maint thread
		// it will wake up every x seconds and
		// maintain the pool size
		pool.setMaintSleep( 30 );

		// set some TCP settings
		// disable nagle
		// set the read timeout to 3 secs
		// and don't set a connect timeout
		pool.setNagle( false );
		pool.setSocketTO( 3000 );
		pool.setSocketConnectTO( 0 );

		// initialize the connection pool
		pool.initialize();
	}
	
	public Object get(String key) {
		return mcc.get(key);
	}
	
	public Map<String, Object> getMulti(String[] keys) {
		return mcc.getMulti(keys);
	}
	
	public boolean set(String key, Object value) {
		return mcc.set(key, value);
	}

	public boolean set(String key, Object value, Date expdate) {
		return mcc.set(key, value, expdate);
	}
	
	public boolean delete(String key) {
		return mcc.delete(key);
	}
	
	public void flushAll() {
		mcc.flushAll();
	}
	
	public void setMcServers(List<String> mcServers) {
		this.mcServers = mcServers;
	}
}
