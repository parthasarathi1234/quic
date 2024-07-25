package com.example;
import net.luminis.quic.QuicClientConnection;
import net.luminis.quic.QuicStream;
import net.luminis.quic.log.SysOutLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Duration;
import static net.luminis.quic.QuicClientConnection.newBuilder;
public class pushClient {
    private int serverPort;
    private static QuicClientConnection connection;
    private static SysOutLogger log;

    public static void main(String[] args) throws IOException, InterruptedException {
        log = new SysOutLogger();
        connection = newBuilder()
                .uri(URI.create("https://localhost:" + 8080))
                .applicationProtocol("h3")
                .logger(log)
                .noServerCertificateCheck()
                .build();
        connection.setPeerInitiatedStreamCallback(quicStream -> new Thread(() -> handlePushMessages(quicStream)).start());
        connection.connect();
        Duration runningTime = Duration.ofMinutes(3);
        Thread.sleep(runningTime.toMillis());
        System.out.println("Client has been running for " + runningTime + "; now terminating.");
    }
    private static void handlePushMessages(QuicStream quicStream) {
        System.out.println("Server opens stream.");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(quicStream.getInputStream()));
        try {
            while (true) {
                String line = inputStream.readLine();
                System.out.println("Received " + line);
            }
        }
        catch (Exception e) {
            System.out.println("error in receiving message");
        }
    }
}