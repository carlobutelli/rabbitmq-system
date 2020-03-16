package com.queue.worker;

import com.queue.worker.client.Consumer;
import com.queue.worker.config.ConsumerConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkerApplication {

	public static void main(String[] args) {
		try {
			new Consumer(ConsumerConfig.fromEnvironment()).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
