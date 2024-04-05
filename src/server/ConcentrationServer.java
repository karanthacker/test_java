package server;
/*
author : Karan Thacker
Server Creation for Concentration Game
 */
import java.io.IOException;
import java.net.ServerSocket;

import common.ConcentrationException;

/**
 * Class ConcentrationServer
 * Creates server with given port number and gameboard size
 * spawns multiple threads as per client request
 */
public class ConcentrationServer {

    private static boolean listening = true;

    /**
     * method creates multiple server threads when demanded for a client connection
     * @param portNumber int
     * @param args String[] command line args
     */
    private static void createServerThread(int portNumber,String[]args){
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            if(Integer.parseInt(args[1])>6) throw new ConcentrationException("ERROR Maximum Size of Matrix: 6");
            while (listening) {
                new ConcentrationClientServerThread(serverSocket.accept(),Integer.parseInt(args[1])).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        } catch (ConcentrationException e){
            System.out.println(e.getMessage());
        }

    }
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java ConcentrationServer <port number> <matrix dimension>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        ConcentrationServer.createServerThread(portNumber,args);



    }
}
