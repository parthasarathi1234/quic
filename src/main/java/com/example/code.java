package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class code {


    public static void main(String[] args){
        String content = "hello world";

        String filePath = "C:\\Users\\mylav\\Desktop\\Samsung\\webRTC\\Checking with file size\\cut.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            writer.write(content);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



//        List<QuicConnection.QuicVersion> supportedVersions = new ArrayList<>();
//        supportedVersions.add(QuicConnection.QuicVersion.V1);
//        supportedVersions.add(QuicConnection.QuicVersion.V2);
//        List<net.luminis.quic.core.Version> coreVersions = convertQuicVersionsToCoreVersions(supportedVersions);
//        private static List<Version> convertQuicVersionsToCoreVersions(List<QuicConnection.QuicVersion> quicVersions) {
//            List<Version> coreVersions = new ArrayList<>();
//            for (QuicConnection.QuicVersion quicVersion : quicVersions) {
//                Version coreVersion = mapToCoreVersion(quicVersion);
//                coreVersions.add(coreVersion);
//            }
//            return coreVersions;
//        }
//        private static Version mapToCoreVersion(QuicConnection.QuicVersion quicVersion) {
//            switch (quicVersion) {
//                case V1: return Version.QUIC_version_1;
//                case V2: return Version.QUIC_version_2;
//                default: throw new IllegalArgumentException("Unknown QUIC version: " + quicVersion);
//            }
//        }



//        private static void registerHttp3(ServerConnector serverConnector, File wwwDir, List<QuicConnection.QuicVersion> supportedVersions, Logger log){
//            ApplicationProtocolConnectionFactory http3ApplicationProtocolConnectFactory = null;
//            try{
//                Class<?> http3FactoryClass = server.class.getClassLoader().loadClass("net.luminis.http3.server.Http3ApplicationProtocolFactory");
//                                            /*
//                                                     "Http3ApplicationProtocolFactory" -> Uses the class loader to load the class dynamically
//                                                     "server.class.getClassLoader()" -> gets the class loader that loaded the "server" class
//                                                     "loadClass("net.luminis.http3.server.Http3ApplicationProtocolFactory")" -> attempts to load the class by its quilified name
//                                             */
//                http3ApplicationProtocolConnectFactory = (ApplicationProtocolConnectionFactory)
//                        http3FactoryClass.getDeclaredConstructor(new Class[]{File.class}).newInstance(wwwDir);
//                log.info("loading fluke H3 server plugin");
//                System.out.println("loading fluke H3 server plugin");
//                serverConnector.registerApplicationProtocol("h3", http3ApplicationProtocolConnectFactory);
//            } catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
//                log.error("No H3 protocol: Flupke plugin not found.");
//                System.out.println("No H3 protocol: Flupke plugin not found.");
//                System.exit(1);
//            }
//        }


}
