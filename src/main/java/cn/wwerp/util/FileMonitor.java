package cn.wwerp.util;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileMonitor {
	
	private final static Logger log = LoggerFactory.getLogger(FileMonitor.class);
	
	private String filename;
	private long lastModified;
	private static Timer timer = new Timer();
	
	public static interface Listener {
		
		public void onFileMofity(String file, long ts);
		
	}
	
	private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();

	public FileMonitor(String filename, long checkModifyIntervalMs) {
		if (log.isDebugEnabled())
			log.debug("FileMonitor init, filename:"+ new File(filename).getAbsolutePath() + ", checkModifyIntervalMs:" + checkModifyIntervalMs);
		
		this.filename = filename;
		lastModified = new File(filename).lastModified();
		
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				try {
					checkModify();
				} catch (Exception e) {
					log.warn("checkModify error!!", e);
				}
			}
		}, checkModifyIntervalMs, checkModifyIntervalMs);
	}
	
	private void checkModify() {
		File f = new File(filename);
		if (!f.exists())
			return;
		
		long lastModifiedNew = f.lastModified();
		if (lastModifiedNew > lastModified) {
			log.info("found file modify, file:" + filename);
			lastModified = lastModifiedNew;
			for (Listener listener : listeners) {
				listener.onFileMofity(filename, lastModified);
			}
		}
	}

	public void addListener(Listener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
			listeners.add(listener);
		}
	}

	public void removeListener(Listener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
}
