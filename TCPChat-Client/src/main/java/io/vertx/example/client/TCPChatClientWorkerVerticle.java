package io.vertx.example.client;

import java.io.Console;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class TCPChatClientWorkerVerticle extends AbstractVerticle {
	private EventBus eb;
	private boolean readline;
	@Override
	public void start() {
		eb = vertx.eventBus();
		readline = true;
		
		Console console = System.console();
		if (console != null) {
			while (readline) {
				String line = console.readLine();
				if (line.equals("exit"))
					readline = false;
				else
					eb.send("com.devop.vertx.chat", line);
			}
		}
	}
	@Override
	public void stop() {
		readline = false;
	}
	
}
