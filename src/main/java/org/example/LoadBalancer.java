package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Load Balancer class
 *
 * @author prashantkumar
 */
public class LoadBalancer
{
    public static void main( String[] args ) throws IOException {
        // create a server socket to listen for incoming client connections to our load balancer
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println( "Load Balancer Started at port : "+ 8081 );


        // always open to receive client request.
        while(true){
            //wait for a client , once client comes create a TCP Connection after 3 way handshake
            Socket socket = serverSocket.accept();
            System.out.println( "TCP Connection established with client: " + socket.toString());

            //Create a thread for each client. By this we can serve them concurrently.
            //This thread will connect to backend host and get the response and forward that response to
            // corresponding  client
            handleSocket(socket);
        }
    }

    /**
     * Created thread for each incoming client and handles them
     *
     * @param socket
     */
    private static void handleSocket(Socket socket) {
        ClientSocketHandler clientSocketHandler = new ClientSocketHandler(socket);
        Thread clientSocketHandlerThread = new Thread(clientSocketHandler);
        clientSocketHandlerThread.start();

    }
}
