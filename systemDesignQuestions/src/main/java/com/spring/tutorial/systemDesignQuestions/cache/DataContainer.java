package com.spring.tutorial.systemDesignQuestions.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataContainer<K, V> {
	private final int DEFAULT_CAPACITY = 100;
	private final int THREAD_POOL_CAPACITY = 2;

	private int capacity;
	private Queue<Data<K, V>> queue;
	private Map<K, Iterator<Data<K, V>>> map;
	private Queue<Data<K, V>> logs;
	private Iterator<Data<K, V>> lastEl;

	private ExecutorService executor;

	public DataContainer() {
		init(DEFAULT_CAPACITY);
	}

	public DataContainer(int capacity) {
		init(capacity);
	}

	private void init(int capacity) {
		this.capacity = capacity;
		this.queue = new LinkedList<>();
		this.map = new HashMap<>();
		this.logs = new LinkedList<>();
		this.lastEl = logs.iterator();
		this.executor = Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
	}

	public V lookup(K key) {
		V val = null;
		if (map.containsKey(key)) {
			Iterator<Data<K, V>> iterator = map.get(key);
			val = iterator.next().value;
		}
		return val;
	}

	public void insert(K key, V value) {
		logs.add(new Data<K, V>(key, value));
		this.executor.execute(new LogThread());
	}

	public void commitLogs() {
		if (!logs.isEmpty()) {
			synchronized (logs) {
				while (!logs.isEmpty()) {
					Data<K, V> data = logs.poll();
					if (!map.containsKey(data.key)) {
						if (queue.size() >= capacity) {
							Data<K, V> toRemove = queue.poll();
							map.remove(toRemove.key);
						}
						addRecord(data.key, data.value);
					}
				}
			}
		}
	}

	private void addRecord(K key, V value) {
		queue.add(new Data<>(key, value));
		lastEl.next();
		map.put(key, lastEl);
	}

	private static class Data<K, V> {
		public K key;
		public V value;

		Data(K key, V value) {
			this.key = key;
			this.value = value;
		}
	}

	private class LogThread implements Runnable {

		@Override
		public void run() {
			commitLogs();
		}

	}
}
