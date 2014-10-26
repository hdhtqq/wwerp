package cn.wwerp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool.impl.GenericObjectPool.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisClient {
	
	private final static Logger log = LoggerFactory.getLogger(RedisClient.class);
	
	private JedisPool pool;
	private List<JedisPool> pools = new ArrayList<JedisPool>();
	private List<String> servers;
	private Config config;
	private boolean lowVersion; 
	
	public void init() {
		log.info("init, servers:" + servers);
		
		Config cfg = new JedisPoolConfig();
		if (this.config != null)
			cfg = this.config;
		for (String server : servers) {
			String[] ss = server.split(":");
			JedisPool pool = new JedisPool(cfg, ss[0], Integer.parseInt(ss[1]), 2000);
			pools.add(pool);
		}
		
		if (pools.size() == 1)
			pool = pools.get(0);
	}

	public void destroy() {
		for (JedisPool pool : pools) {
			pool.destroy();
		}
	}

	public void setServers(String[] servers) {
		this.servers = new ArrayList<String>();
		for (String s : servers) {
			this.servers.add(s);
		}
	}
	
	public List<String> getServerList() {
		return servers;
	}
	
	public void setConfig(Config config) {
		this.config = config;
	}
	
	private long hash(byte[] key) {
		return Util.crc32(key);
	}
	
	private long hash(String key) {
		return Util.crc32(key.getBytes());
	}
	
	final private JedisPool getPool(byte[] key) {
		if (pool != null)
			return pool; 
		else
			return pools.get((int)(hash(key) % pools.size()));
	}
	
	final private JedisPool getPool(String key) {
		if (pool != null)
			return pool; 
		else
			return pools.get((int)(hash(key) % pools.size()));
	}
	
	public void set(byte[] key, byte[] value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			jedis.set(key, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	public void set(String key, String value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			jedis.set(key, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long del(String... keys) {
		long ret = 0;
		if (pool != null) {
			JedisPool pool = getPool(keys[0]);
			Jedis jedis = pool.getResource();
			try {
				ret = jedis.del(keys);
			} catch (JedisConnectionException e) {
				pool.returnBrokenResource(jedis);
				jedis = null;
				throw e;
			} finally {
				if (jedis != null)
					pool.returnResource(jedis);
			}
		} else {
			for (String key : keys) {
				JedisPool pool = getPool(key);
				Jedis jedis = pool.getResource();
				try {
					ret += jedis.del(key);
				} catch (JedisConnectionException e) {
					pool.returnBrokenResource(jedis);
					jedis = null;
					throw e;
				} finally {
					if (jedis != null)
						pool.returnResource(jedis);
				}
			}
		}
		return ret;
	}
	
	public long del(byte[]... keys) {
		long ret = 0;
		if (pool != null) {
			JedisPool pool = getPool(keys[0]);
			Jedis jedis = pool.getResource();
			try {
				ret = jedis.del(keys);
			} catch (JedisConnectionException e) {
				pool.returnBrokenResource(jedis);
				jedis = null;
				throw e;
			} finally {
				if (jedis != null)
					pool.returnResource(jedis);
			}
		} else {
			for (byte[] key : keys) {
				JedisPool pool = getPool(key);
				Jedis jedis = pool.getResource();
				try {
					ret += jedis.del(key);
				} catch (JedisConnectionException e) {
					pool.returnBrokenResource(jedis);
					jedis = null;
					throw e;
				} finally {
					if (jedis != null)
						pool.returnResource(jedis);
				}
			}
		}
		return ret;
	}
	
	public Long hset(final String key, final String field, final String value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hset(key, field, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long hset(byte[] key, byte[] field, byte[] value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hset(key, field, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public boolean hexists(byte[] key, byte[] field) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hexists(key, field);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public String hget(final String key, final String field) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hget(key, field);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public byte[] hget(byte[] key, byte[] field) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hget(key, field);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Map<String, String> hgetAll(final String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hgetAll(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Map<byte[], byte[]> hgetAll(byte[] key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hgetAll(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long hdel(final String key, final String field) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hdel(key, field);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long hdel(byte[] key, byte[] field) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hdel(key, field);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long hincrBy(final String key, final String field, final long value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hincrBy(key, field, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public List<String> hmget(final String key, final String... fields) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.hmget(key, fields);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public byte[] get(byte[] key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.get(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public String get(String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.get(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public boolean exists(byte[] key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.exists(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public boolean exists(String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.exists(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public List<String> mget(final String... keys) {
		if (pool != null) {
			JedisPool pool = getPool(keys[0]);
			Jedis jedis = pool.getResource();
			try {
				return jedis.mget(keys);
			} catch (JedisConnectionException e) {
				pool.returnBrokenResource(jedis);
				jedis = null;
				throw e;
			} finally {
				if (jedis != null)
					pool.returnResource(jedis);
			}
		} else {
			List<String> list = new ArrayList<String>(keys.length);
			for (String key : keys) {
				list.add(get(key));
			}
			return list;
			/*
			 * TODO: should use mget to query
			List<String>[] kk = new ArrayList[servers.size()];
			List<Integer>[] idxs = new ArrayList[servers.size()];
			for (int i = 0; i < keys.length; i++) {
				kk[i] = new ArrayList<String>();
				idxs[i] = new ArrayList<Integer>();
			}
			for (int i = 0; i < keys.length; i++) {
				String key = keys[i];
				JedisPool pool = getPool(key);
			}*/
		}
	}
	
	public Long incrBy(final String key, final long value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.incrBy(key, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long incr(final String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.incr(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public static interface PipelinedCallback {
		public Response<?> execute(Pipeline pipeline, String key);
	}
	
	public static interface PipelinedCallbackX<T> {
		
		public Response<?> execute(Pipeline pipeline, String key, T t);
		
		public String getKey(T t);
	}
	
	private static class JedisPipeline {
		Jedis jedis;
		Pipeline pipeline;
		
		JedisPipeline(Jedis jedis, Pipeline pipeline) {
			this.jedis = jedis;
			this.pipeline = pipeline;
		}
	}
	
	public class JPipeline {
		
		Map<JedisPool, JedisPipeline> resources = new HashMap<JedisPool, RedisClient.JedisPipeline>();
		
		public Pipeline get(String key) {
			JedisPool pool = getPool(key);
			return get(pool);
		}
		
		public Pipeline get(byte[] key) {
			JedisPool pool = getPool(key);
			return get(pool);
		}

		private Pipeline get(JedisPool pool) {
			JedisPipeline jp = resources.get(pool);
			if (jp != null)
				return jp.pipeline;
			Jedis jedis = pool.getResource();
			Pipeline pipeline = jedis.pipelined();
			resources.put(pool, new JedisPipeline(jedis, pipeline));
			return pipeline;
		}
		
		public void sync() {
			for (JedisPipeline jp : resources.values()) {
				jp.pipeline.sync();
			}
			
			release();
		}
		
		public void release() {
			for (Map.Entry<JedisPool, JedisPipeline> entry : resources.entrySet()) {
				entry.getKey().returnResource(entry.getValue().jedis);
			}
			resources.clear();
		}
	}
	
	public JPipeline pipelined() {
		return new JPipeline();
	}
	
	public <T> List<Object> pipelined(Collection<T> objs, PipelinedCallbackX<T> callback) {
		JPipeline jp = pipelined();
		try {
			List<Response<?>> responses = new ArrayList<Response<?>>(objs.size());
			for (T t : objs) {
				String key = callback.getKey(t);
				Pipeline pipeline = jp.get(key);
				responses.add(callback.execute(pipeline, key, t));
			}
			jp.sync();
			List<Object> values = new ArrayList<Object>();
			for (Response<?> r : responses) {
				values.add(r.get());
			}
			return values;
		} finally {
			jp.release();
		}
	}
	
	public List<Object> pipelined(List<String>  keys, PipelinedCallback callback) {
		JPipeline jp = pipelined();
		try {
			List<Response<?>> responses = new ArrayList<Response<?>>(keys.size());
			for (String key : keys) {
				Pipeline pipeline = jp.get(key);
				responses.add(callback.execute(pipeline, key));
			}
			jp.sync();
			List<Object> values = new ArrayList<Object>();
			for (Response<?> r : responses) {
				values.add(r.get());
			}
			return values;
		} finally {
			jp.release();
		}
	}
	
	public List<Object> pipelinedGet(List<String>  keys, PipelinedCallback callback) {
		return pipelined(keys, callback);
	}
	
	public List<String> lrange(String key, long start, long end) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.lrange(key, start, end);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long lpush(String key, String... values) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			if (lowVersion) {
				long n = 0;
				for (String s : values) {
					n = jedis.lpush(key, s);
				}
				return n;
			} else {
				return jedis.lpush(key, values);
			}
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long rpush(String key, String... values) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			if (lowVersion) {
				long n = 0;
				for (String s : values) {
					n = jedis.rpush(key, s);
				}
				return n;
			} else {
				return jedis.rpush(key, values);
			}
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long expireAt(String key, long unixTime) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.expireAt(key, unixTime);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Long expire(String key, int seconds) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.expire(key, seconds);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long llen(String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.llen(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public String lpop(String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.lpop(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public String rpop(String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.rpop(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long lrem(String key, long count, String value) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.lrem(key, count, value);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public void setLowVersion(boolean lowVersion) {
		this.lowVersion = lowVersion;
	}
	
	public long sadd(String key, String element) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.sadd(key, element);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long srem(String key, String element) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.srem(key, element);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public boolean sismember(String key, String element) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.sismember(key, element);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Set<String> smembers(String key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.smembers(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long zadd(String key, double score, String member) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zadd(key, score, member);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long zadd(byte[] key, double score, byte[] member) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zadd(key, score, member);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long zrem(byte[] key, byte[] member) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrem(key, member);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long zcard(byte[] key) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zcard(key);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long zremrangeByRank(byte[] key, int start, int end) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zremrangeByRank(key, start, end);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public Set<byte[]> zrangeByScore(byte[] key, double min, double max, int offset, int count) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zrangeByScore(key, min, max, offset, count);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}
	
	public long zcount(byte[] key, double min, double max) {
		JedisPool pool = getPool(key);
		Jedis jedis = pool.getResource();
		try {
			return jedis.zcount(key, min, max);
		} catch (JedisConnectionException e) {
			pool.returnBrokenResource(jedis);
			jedis = null;
			throw e;
		} finally {
			if (jedis != null)
				pool.returnResource(jedis);
		}
	}

	public static void main(String[] args) {
		RedisClient client = new RedisClient();
		client.setServers(new String[]{"127.0.0.1:6379"/*, "192.168.43.178:6379"*/});
		client.init();
		System.out.println(client.get("1111"));
		/*
		for (int i = 0; i < 20; i++) {
			client.set("k" + i, "v" + i);
		}
		
		List<Object> values = client.pipelined(Arrays.asList("k1", "k4", "k5", "k8"),
				new PipelinedCallback() {
					
					@Override
					public Response<?> execute(Pipeline pipeline, String key) {
						return pipeline.get(key);
					}
				});
		for (Object obj : values) {
			System.out.println(obj);
		}
		
		values = client.pipelined(Arrays.asList("k2", "k7", "k3", "k11"),
				new PipelinedCallback() {
					
					@Override
					public Response<?> execute(Pipeline pipeline, String key) {
						return pipeline.get(key);
					}
				});
		for (Object obj : values) {
			System.out.println(obj);
		}*/
	}
}
