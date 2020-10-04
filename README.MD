Chat Server
===========

Spring Boot App that implements a simple chat server.

Functionality:

- chat server listens on port configured by 'chat.server.port' property.

The protocol is extremely simple. Clients send and receive messages as line terminated strings.
Clients remain connected and can send any number of messages until they send a special 'bye' message.
The 'bye' message tells the server to terminate the connection. 

To run the client simply run the boot app in the usual way. For example in
STS you can just click the 'start' button in the boot dash. On the commandline you can
run `mvn spring-boot:run`.

To connect to the server use 'telnet':

```
telnet localhost 6677
```

You can open multiple client connections from different terminals. Any message sent from one client, 
will be sent by the server to all connected clients.


Implementation:
===============

This app demonstrates how you can build a simple socket-based server as a spring-boot app, 'from scratch'.

From scratch means we do not use on any external libraries except what comes with a plain 'empty' spring
boot app that just has a dependency on the base 'spring-boot-starter' dependency.

The goal here is to keep things as simple as possible. So we do not try to use non-blocking IO
or reactive patterns. The server has a very simple thread-based blocking implementation. One tread is 
used to listen for incoming client connections. Each incoming connection is then dispatched to a handler 
that runs on its own thread, one thread per client connection.

Spring/Boot Features used/demonstrated in this app:

- Using @ConfigurationProperties to configure things (i.e. what port does the chat server run on)
- Using a Spring `TaskExecutor' to start background threads.
- Server life cycle follows spring application / bean life cycle (this is done simply by 
  implementing `InitializingBean` and `DisposableBean` interfaces.
