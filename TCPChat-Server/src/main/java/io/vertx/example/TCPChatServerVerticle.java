package io.vertx.example;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.VoidHandler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetSocket;

public class TCPChatServerVerticle extends AbstractVerticle {
	
	private Logger logger = LoggerFactory.getLogger(TCPChatServerVerticle.class);
	
	private NetServer server;
	private List<NetSocket> sockets;
	
	@Override
	public void start() {
		
		server = vertx.createNetServer();
		sockets = new ArrayList<>();
		
		server.connectHandler(socket -> {
			logger.info("Thread [" + Thread.currentThread().getId() + "] client connected: " + socket.remoteAddress());
			sockets.add(socket);
			//-- this handler will be called every time data is received on the socket
			socket.handler(buffer -> {
				logger.info("Thread [" + Thread.currentThread().getId() + "] client connected: " + buffer.toString());
				for (NetSocket s : sockets) {
					if (!socket.equals(s)) {
						logger.info("Thread [" + Thread.currentThread().getId() + "] client connected: " + buffer.toString());
						s.write(buffer);
					}
				}
			});
			//-- socket closed
			socket.closeHandler(new VoidHandler() {

				@Override
				protected void handle() {
					logger.info("connection closed: " + socket.remoteAddress());
					Iterator<NetSocket> it = sockets.iterator();
					while (it.hasNext()) {
						if (socket.equals(it.next())) {
							it.remove();
							break;
						}
					}
				}
			});
			//-- something went wrong
			socket.exceptionHandler(throwable -> logger.error("unexpected exception: ", throwable));
		});
		
		server.listen(8090, "localhost", asyncResult -> logger.info("bind result: " + asyncResult.succeeded()));
	}

	@Override
	public void stop() {
		if (server != null) {
			server.close();
		}
	}

}
