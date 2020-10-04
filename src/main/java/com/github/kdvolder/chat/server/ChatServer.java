package com.github.kdvolder.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;


@Component
public class ChatServer implements InitializingBean, DisposableBean {
	
	private static final Logger log = LoggerFactory.getLogger(ChatServer.class);

	@Autowired
	ChatServerProperties props;
	
	@Autowired
	TaskExecutor executor;
	
	private ServerSocket socket;
	
	private AtomicInteger counter = new AtomicInteger();

	private boolean closeRequested = false;
	
	private List<Consumer<String>> activeConnections = Collections.synchronizedList(new ArrayList<>());	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		socket = new ServerSocket(props.getPort());
		executor.execute(() -> this.acceptIncoming());
		log.info("Chat Server listening on port {}", props.getPort());
	}

	private void acceptIncoming() {
		try {
			do {
				Socket clientSock = socket.accept();
				executor.execute(() -> handleRequest(clientSock));
			} while (!closeRequested);
		} catch (SocketException e) {
			if (closeRequested) {
				//This is expected. We close the serverSocket to shut down the server.
				//This causes SocketException (this is good as it 'unblocks' the thread
				//listening for incoming client connections, which is exactly what we want
				//when shutting down the server. So o
			} else {
				log.error("", e);
			}
		} catch (IOException e) {
			log.error("", e);
		}
	}
	
	private void handleRequest(Socket clientSock) {
		try {
			try (PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true)) {
				int id = counter.incrementAndGet();
				Consumer<String> sendMsg = (s) -> out.println(s);
				synchronized (activeConnections) {
					activeConnections.add(sendMsg);
				}
				try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()))) {
					String req = in.readLine();
					while (req!=null && !req.equals("bye") && !closeRequested) {
						for (Consumer<String> consumer : activeConnections) {
							consumer.accept("["+id+"]: "+req);
						}
						req = in.readLine();
					}
				} finally {
					activeConnections.remove(sendMsg);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}

	@Override
	public void destroy() throws Exception {
		log.info("Server is shutting down...");
		closeRequested = true;
		socket.close();
	}
}
