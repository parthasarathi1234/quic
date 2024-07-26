package com.example;
import net.luminis.quic.QuicClientConnection;
import net.luminis.quic.QuicStream;
import net.luminis.quic.log.SysOutLogger;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import static net.luminis.quic.QuicClientConnection.newBuilder;
public class client {
    private static QuicClientConnection connection;
    private static SysOutLogger log;
    public static void main(String[] args) throws Exception{
        log = new SysOutLogger();
//        log.logPackets(true);
//        log.logInfo(true);
                                /* new SysOutLogger() -> is likely used for logging different types of messages
                                                         1. packet logs
                                                         2. informational logs
                                                         SysOutLogger has at least 2 methods
                                                         1. logPackets(boolean enable) -> Enables or Disables packet logging
                                                         2. LogInfo(boolean enable) -> Enables or disables info logging
                                 */

//        QuicClientConnection.Builder builder = QuicClientConnection.newBuilder();
                                    // QuicClientConnection.Builder builder -> Declares a variable 'builder' of type "QuicClientConnection.Builder"
                                    // QuicClientConnection.newBuilder -> Calls a static method 'newBuilder" on the "QuicClientConnection" class to get and instance of it's "Builder".
        connection = newBuilder()
                .uri(URI.create("https://localhost:"+8082))  // https://localhost:8080 -> is the server address to connect , It send connection request to this address. Server receive the request and may accept and reject.
                .applicationProtocol("h3")
                .logger(log)
                .noServerCertificateCheck()
                .build();

        connection.setPeerInitiatedStreamCallback(quicStream -> new Thread(() -> handlePushMessages(quicStream)).start());

        System.out.println("Connection rquest send successfully");
        connection.connect();  // send request to server
        Duration runningTime = Duration.ofMinutes(3);
        Thread.sleep(runningTime.toMillis());
//        echo("hello worls", connection);
        System.out.println("Connection with server establishment done");
                                    /*
                                            .uri(new URI(args[0])) -> Sets the URI for the "QuicClientConnection" using the builder's uri method
                                            .applicationProtocol("hq-interop")  ->
                                            .build() -> Finalized the configuration and builds the "QuicClientConnection" instance.


                                            connection.connect() -> Calls the "connect" method on the "QuicClientConnection" instance to establish the connection to the specified URI.
                                     */

        // After connection establishment

//        QuicStream stream = connection.createStream(true);    //  creating stream
//        BufferedOutputStream outputStream = new BufferedOutputStream(stream.getOutputStream());
//        String message = "Hello, server";
//        outputStream.write("Hello from client".getBytes(StandardCharsets.UTF_8));
//
//        outputStream.flush();
//
//        long transferred = stream.getInputStream().transferTo(new FileOutputStream("kwik_client_output"));
//
//        connection.close();
//
//        System.out.println("Connection close");
    }

    private static void handlePushMessages(QuicStream quicStream){

        //    -------------------- Sending File -------------------------------------
        File receivedFile = new File("C:\\Users\\mylav\\Desktop\\Samsung\\webRTC\\Checking with file size\\5ARITHMETIC (PROFIT AND LOSS).zip");
        try (InputStream inputStream = quicStream.getInputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(receivedFile)) {

            byte[] buffer = new byte[64*1024];
            int bytesRead;
            int i=1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                System.out.println("receving --------- "+ new String(buffer, 0, bytesRead));
//                System.out.println(i);
//                i++;
               fileOutputStream.write(buffer, 0, bytesRead);
            }
            fileOutputStream.flush();
            System.out.println("File received and saved successfully.");
        } catch (Exception e) {
            System.out.println("Error in receiving file: " + e);
        }

        //    -------------------- Sending message ---------------------------------
        // BufferedReader inputStream = new BufferedReader(new InputStreamReader(quicStream.getInputStream()));
        // try{
        //     while(true){
        //         String line = inputStream.readLine();
        //         System.out.println(("received "+line));
        //     }
        // }catch (Exception e){
        //     System.out.println("error in reciveing message");
        // }
    }

//    private static void echo(String payload, QuicClientConnection connection) throws IOException {
//        QuicStream quicStream = connection.createStream(true);
//        byte[] requestData = payload.getBytes(StandardCharsets.US_ASCII);
//        quicStream.getOutputStream().write(requestData);
//        quicStream.getOutputStream().close();
//
//        System.out.print("Response from server: ");
//        quicStream.getInputStream().transferTo(System.out);
//        System.out.println();
//    }
}





