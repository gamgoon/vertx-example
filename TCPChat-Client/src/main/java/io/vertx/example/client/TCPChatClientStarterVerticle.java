package io.vertx.example.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

public class TCPChatClientStarterVerticle extends AbstractVerticle {

	@Override
	public void start() {
		vertx.deployVerticle("io.vertx.example.client.TCPChatClientVerticle");
		DeploymentOptions options = new DeploymentOptions().setWorker(true);
		vertx.deployVerticle("io.vertx.example.client.TCPChatClientWorkerVerticle", options);
	}

}
