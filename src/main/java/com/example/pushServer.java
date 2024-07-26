package com.example;

import net.luminis.quic.QuicConnection;
import net.luminis.quic.QuicStream;
import net.luminis.quic.log.Logger;
import net.luminis.quic.log.SysOutLogger;
import net.luminis.quic.server.ApplicationProtocolConnection;
import net.luminis.quic.server.ApplicationProtocolConnectionFactory;
import net.luminis.quic.server.ServerConnectionConfig;
import net.luminis.quic.server.ServerConnector;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

public class pushServer {

    private static void usageAndExit() {
        System.err.println("Usage: cert file, cert key file, port number");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;

        Logger log = new SysOutLogger();
        log.timeFormat(Logger.TimeFormat.Long);
        log.logWarning(true);
        log.logInfo(true);

//        ServerConnectionConfig serverConnectionConfig = ServerConnectionConfig.builder()
//                // No connection configuration necessary, as client will not initiate any stream, nor send data.
//                .build();


        boolean withRetry = true;

        ServerConnectionConfig serverConnectionConfig = ServerConnectionConfig.builder()
                .maxIdleTimeoutInSeconds(30)  // maximum idle timeout for a connection to 30 seconds. if the connection is idle(no data is transmitted) for this duration, connection may be close.
                .maxUnidirectionalStreamBufferSize(1_000_000)
                .maxBidirectionalStreamBufferSize(1_000_000)
                .maxConnectionBufferSize(10_000_000)
                .maxOpenPeerInitiatedUnidirectionalStreams(10)
                .maxOpenPeerInitiatedBidirectionalStreams(100)
                .retryRequired(withRetry)
                .connectionIdLength(8)  // Sets the length of the connection ID to 8 bytes. This parameter defines the length of the connection identifier used in the protocol.
                .build();

        ServerConnector serverConnector = ServerConnector.builder()
                .withPort(port)
                .withCertificate(new FileInputStream("C:\\Users\\mylav\\Documents\\java\\SpringBoot Project\\Self signed certificate\\openssl9\\cert.pem"), new FileInputStream("C:\\Users\\mylav\\Documents\\java\\SpringBoot Project\\Self signed certificate\\openssl9\\key.pem"))
                .withConfiguration(serverConnectionConfig)
                .withLogger(log)
                .build();

        registerProtocolHandler(serverConnector, log);

        serverConnector.start();

        log.info("Started (msg) push server on port " + port);
    }

    private static void registerProtocolHandler(ServerConnector serverConnector, Logger log) {
        serverConnector.registerApplicationProtocol("h3", new PushProtocolConnectionFactory(log));
    }

    /**
     * The factory that creates the (push) application protocol connection.
     */
    static class PushProtocolConnectionFactory implements ApplicationProtocolConnectionFactory {

        private Logger log;

        public PushProtocolConnectionFactory(Logger log) {
            this.log = log;
        }

        @Override
        public ApplicationProtocolConnection createConnection(String protocol, QuicConnection quicConnection) {
            return new PushProtocolConnection(quicConnection, log);
        }
    }

    /**
     * The connection that implements the (push) application protocol.
     */
    static class PushProtocolConnection implements ApplicationProtocolConnection {

        private Logger log;

        public PushProtocolConnection(QuicConnection quicConnection, Logger log) {
            this.log = log;
            System.out.println("New \"push protocol\" connection; will create (server initiated) stream to push messages to client.");
            QuicStream quicStream = quicConnection.createStream(false);
            new Thread(() -> generatePushMessages(quicStream), "pusher").start();
        }

        private void generatePushMessages(QuicStream quicStream) {
            OutputStream outputStream = quicStream.getOutputStream();
            try {
                int i = 1;
                while (true) {
                    String currentDateTime = Instant.now().toString();
                    String mes = "hello parthu"+i;
                    i++;
                    System.out.println("Pushing message " + mes);
                    outputStream.write(mes.getBytes(StandardCharsets.US_ASCII));
                    outputStream.write("\n".getBytes(StandardCharsets.US_ASCII));
                    Thread.sleep(1000);
                }
            }
            catch (Exception e) {
                System.out.println("Pushing messages terminated with exception " + e);
            }
        }
    }
}

