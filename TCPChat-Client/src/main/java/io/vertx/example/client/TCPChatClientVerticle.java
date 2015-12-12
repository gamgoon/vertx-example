package io.vertx.example.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.VoidHandler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import io.vertx.core.buffer.Buffer;

public class TCPChatClientVerticle extends AbstractVerticle {
	
	private Logger logger = LoggerFactory.getLogger(TCPChatClientVerticle.class);
	private NetClient client;
	private EventBus eb;
	private String writeHandlerId;
	
	@Override
	public void start() {
		eb = vertx.eventBus();
		client = vertx.createNetClient();
		
		client.connect(8090,  "localhost", asyncResult -> {
			logger.info("connect result: " + asyncResult.succeeded());
			if (asyncResult.succeeded()) {
				final NetSocket socket = asyncResult.result();
				writeHandlerId = socket.writeHandlerID();
				
				//-- this handler will be called every time data is received on the socket
				socket.handler(buffer -> logger.info("received data: " + buffer.toString()));
				//-- socket closed				
				socket.closeHandler(new VoidHandler() {
					@Override
					protected void handle() {
						logger.info("connection closed: " + socket.remoteAddress());
					}
				});
				//-- something went wrong
				socket.exceptionHandler(throwable -> logger.error("unexpected exception: ", throwable));
			}
		});
		
		eb.consumer("com.devop.vertx.chat", (Message<String> message) -> {
			if (writeHandlerId != null) {
				eb.send(writeHandlerId,  Buffer.buffer(message.body()));
			}
		});
		
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
	}
	
	
}
