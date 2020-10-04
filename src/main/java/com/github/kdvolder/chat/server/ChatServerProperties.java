package com.github.kdvolder.chat.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("chat.server")
public class ChatServerProperties {

	/**
	 * The port the chat server listens on.
	 */
	private int port = 4567;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
