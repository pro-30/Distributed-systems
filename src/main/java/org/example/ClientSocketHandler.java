package org.example;

import jdk.internal.util.xml.impl.Input;
import org.example.utils.BackendServers;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * purpose of this socket is to create a connection with client and accept data from client
 * then forward that data to backend service and then forward the response to client.
 *
 * @author prashantkumar
 */
public class ClientSocketHandler implements Runnable {

    // JAVA Sockets Class allow for communication between processes running on
    // different machines over a network, typically using TCP/IP as the underlying protocol.
    private Socket clientSocket;

    public ClientSocketHandler(final Socket socket){
        this.clientSocket = socket;
    }
    @Override
    public void run() {

        try {
            // create input and output stream with client to accept data and send the response
            final InputStream clientToLoadBalancerInputStream = clientSocket.getInputStream();
            final OutputStream loadBalancerToClientOutputStream = clientSocket.getOutputStream();

            // get the backend host now to which we will forward current client's request.
            String backendHost = BackendServers.getHost();
            System.out.println("Host selected for handler this request: "+ backendHost);

            // create the socket request(TCP Connection) to the backend server
            Socket backendSocket = new Socket(backendHost, 8080);

            // now create input stream and output stream to backend server to share data with it.

            // stream to accept response from backend server
            final InputStream backendServerToLoadBalancerInputStream = backendSocket.getInputStream();

            // stream to send client data to backend server from load balancer
            final OutputStream loadBalancerToBakcendServerOutputStream = backendSocket.getOutputStream();

            //This thread will get all the data byte by byte from client and pass it to the backend server byte by byte
            Thread clientDataHandler = new Thread(){
                public void run(){
                    try {
                        int data;
                        while((data = clientToLoadBalancerInputStream.read()) != -1 ){
                            // reading from input stream client -> LB
                            // writing to output stream LB -> backend server
                         loadBalancerToBakcendServerOutputStream.write(data);
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            clientDataHandler.start();

            //This thread will get response (all the data) byte by byte from the backend server, through the lb and pass it to the client.

            Thread backendDataHandler = new Thread(){
                public void run() {
                    try {
                        int data;
                        while((data = backendServerToLoadBalancerInputStream.read()) != -1){
                            // reading from input stream backend server -> LB
                            // writing to output stream lb -> client
                            loadBalancerToClientOutputStream.write(data);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            };
            backendDataHandler.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
