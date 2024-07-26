package com.example;
import net.luminis.quic.QuicConnection;
import net.luminis.quic.QuicStream;
import net.luminis.quic.log.FileLogger;
import net.luminis.quic.log.Logger;
import net.luminis.quic.log.SysOutLogger;
import net.luminis.quic.run.KwikVersion;
import net.luminis.quic.server.ApplicationProtocolConnection;
import net.luminis.quic.server.ApplicationProtocolConnectionFactory;
import net.luminis.quic.server.ServerConnectionConfig;
import net.luminis.quic.server.ServerConnector;
import net.luminis.quic.core.Version;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class server {
    public static void main(String[] args) throws Exception{

        File certificateFile = new File("C:\\Users\\mylav\\Documents\\java\\SpringBoot Project\\Self signed certificate\\openssl9\\cert.pem");
        File certificateKeyFile = new File("C:\\Users\\mylav\\Documents\\java\\SpringBoot Project\\Self signed certificate\\openssl9\\key.pem");
        File wwwDir = new File("C:\\Users\\mylav\\Documents\\java\\SpringBoot Project\\quic_5\\quic_5\\src\\main\\resources");

        boolean withRetry = true;
        Logger log;
        File logDir = new File("C:\\Users\\mylav\\Documents\\java\\SpringBoot Project\\quic_5\\quic_5");

        // Log file
        if(logDir.exists() && logDir.isDirectory() && logDir.canWrite()){
            log = new FileLogger(new File(logDir, "kwikserver.log"));  // create file "kwikserver.log"
            System.out.println("inside for loop");
        }else{
            log = new SysOutLogger();
            System.out.println("inside else loop");
        }
        log.timeFormat(Logger.TimeFormat.Long);
        log.logWarning(true);
        log.logInfo(true);
                                    /*
                                        1. logDir.exists() -> Checks if the "logDir" (a 'File' object representing the '/logs' directory) exits.
                                        2. logDir.isDirectory() -> Checks if 'logDir' is a directory
                                        3. logDir.canWrite() -> Checks if the program has write permission to logDir.
                                     */

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
                .withPort(8082)
                .withCertificate(new FileInputStream(certificateFile), new FileInputStream(certificateKeyFile))  // certificate file
//                .withSupportedVersions(coreVersions)
                .withConfiguration(serverConnectionConfig)
                .withLogger(log)
                .build();

        registerProtocolHandler(serverConnector,log);
//        registerHttp3(serverConnector, wwwDir, supportedVersions, log);
        serverConnector.start();
    }

    private static void registerProtocolHandler(ServerConnector serverConnector, Logger log){
        serverConnector.registerApplicationProtocol("h3", new protocolconnectionFactory(log));
    }
    
    static class protocolconnectionFactory implements ApplicationProtocolConnectionFactory{
        private final Logger log;

        public protocolconnectionFactory(Logger log){
            this.log = log;
        }

        @Override
        public ApplicationProtocolConnection createConnection(String s, QuicConnection quicConnection) {
            return new protocolConnection(quicConnection, log);
        }

        @Override
        public int maxConcurrentPeerInitiatedUnidirectionalStreams() {
            return 0;
        }

        @Override
        public int maxConcurrentPeerInitiatedBidirectionalStreams() {
            return 10;
        }
    }

    static class protocolConnection implements ApplicationProtocolConnection{
        private Logger log;

        public protocolConnection(QuicConnection quicConnection, Logger log){
            // quicConnection -> represents the QUIC connection
            this.log = log;
            
            new Thread(() -> generatePushMessage(quicConnection), "h3").start();
                                                            /*
                                                               new Thread(...) -> instantiates a new 'thread' object. the 'Thread' class represents a thread of execution in a program.
                                                               () -> ... -> lambda expression defines the 'runnable' that the thread will execute. runnable is a functional interface with a single method run().

                                                               h3 -> name of the thread. Naming threads can be used for debugging and monitoring purpose. 
                                                               .start() -> method is called on the 'thread' object.
                                                             */
        }

        private void generatePushMessage(QuicConnection quicConnection){
            QuicStream quicStream = quicConnection.createStream(false);  // Creates a new QUIC stream from the provided 'quicConnection' object. 
                                                                         // true -> unidirectional owned only send data
                                                                         // false -> bidirectional (both) send data
            OutputStream outputStream = quicStream.getOutputStream();  // Retrieves the output stream associated with the provided 'quicStream'
                                                            /*
                                                               OutputStream -> is a type representing a stream within a QUIC connection. 
                                                             */
            File fileToSend = new File("C:\\Users\\mylav\\Desktop\\Samsung\\webRTC\\Checking with file size\\ARITHMETIC (PROFIT AND LOSS).zip");
            try(InputStream FileInputStream = new FileInputStream(fileToSend)){
                byte[] buffer = new byte[64*1024];
                int bytesRead;
                int i=1;
                while((bytesRead = FileInputStream.read(buffer)) != -1){
//                    buffer = "hello parthu".getBytes();
//                    System.out.println(i);
//                    i++;
                    outputStream.write(buffer, 0, bytesRead);
//                    System.out.println("sending -------- " + new String(buffer, 0, bytesRead));
                }
                outputStream.flush();
                System.out.println("File send sucessfully");
            }catch(Exception e){
                System.out.println("Error in sending file "+ e);
            }
                                                /*
                                                    try(InputStream FileInputStream = new FileInputStream(fileToSend))  ->  
                                                    new FileInputStream(fileToSend) ->   creates a 'FileInputStream' to read the contents of the file.
                                                    FileInputStream                 ->   is wrapped in a try-with-resources statement, ensuring that it is closed automatically when the try block exits.
                                                    new byte[8192]                  -> This creates a buffer 8kb. The buffer will be closed to transfer chunks of data from the file to the output stream.
                                                    FileInputStream.read(buffer)    -> This reads up to 8KB from the file and stores them in the buffer. The method retuns the numner of bytes read, or '-1' if the end of the file is reached.
                                                    outputStream.write(buffer, 0, bytesRead) -> This writes the bytes read from the file to output stream. The parameters specify the buffer, the start offset in the buffer, and the number of bytes to write.
                                                    outputStrea.flush()             -> This forces any buffered output bytes to be written out. This ensures that all data is sent, even if it hasn't filled the internal buffer yet.

                                                 */




            // try{
            //     int i=1;
            //     Random rand = new Random();

            //     // while(i<10){
            //         int ranNum = rand.nextInt(100);
            //         String currentDateTime = "Hello parthu "+i+"  "+ranNum;
            //         i++;
            //         System.out.println("Pushing message " + currentDateTime);
            //         outputStream.write(currentDateTime.getBytes(StandardCharsets.US_ASCII)); // Writes the message to the output stream in UR_ASCII encoding
            //         outputStream.write("\n".getBytes(StandardCharsets.US_ASCII));  // writes a newline character to the output stream
            //         Thread.sleep(1000); // pauses the thread for 1 second(=1000 milliseconds)
            //     // }
            // }catch (Exception e){
            //     System.out.println("Pushing messages terminated with exception"+ e);
            // }
        }

//        @Override
//        public void acceptPeerInitiatedStream(QuicStream quicStream){
//            new Thread(() -> handleRequest(quicStream)).start();
//        }
//
//        private void handleRequest(QuicStream quicStream){
//            try {
//                byte[] bytesRead = quicStream.getInputStream().readAllBytes();
//                System.out.println("read request with "+bytesRead.length +"bytes of data");
//                quicStream.getOutputStream().write(bytesRead);
//                quicStream.getOutputStream().close();
//            }catch(IOException e){
//                log.error("reading quic stream failed",e);
//            }
//        }
    }

}

