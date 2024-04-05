/*
Author: Karan
HW9 : Concentration GAME CLIENT (GUI)
 */

package controller;
import model.ConcentrationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.*;
import java.net.Socket;

/**
 * Class Controller: contains listner thread method and helper functions to
 * communicate with game server. Also the button's action method is contained
 * in this class
 */
public class ConcentrationController extends Thread {
    private String hostName;
    private int portNumber;
    public Socket echoSocket;
    public PrintWriter out;
    private ConcentrationModel model;

    /**
     * Class constructor taking the socket creation info and the game model
     * @param model ConcentrationModel
     * @param hostName String
     * @param portNumber int
     */
    public ConcentrationController(ConcentrationModel model,String hostName,String portNumber){
        this.hostName = hostName;
        this.portNumber = Integer.parseInt(portNumber);
        this.model = model;
        try{
            this.echoSocket = new Socket(this.hostName, this.portNumber);
            this.out = new PrintWriter(echoSocket.getOutputStream(), true);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * helper method to listner thread. changes the model as per the server message
     * @param fromServer
     */
    private void changeModel(String[] fromServer){
        switch (fromServer[0]){
            case "BOARD_DIM":
                this.model.fillBoard(Integer.parseInt(fromServer[1]));
                break;
            case "MATCH":
                try{
                Thread.sleep(100);
                this.model.incNumOfMatches();}catch (InterruptedException e){e.printStackTrace();}
                break;
            case "MISMATCH":
                this.model.undoMove(Integer.parseInt(fromServer[1]),Integer.parseInt(fromServer[2]),
                        Integer.parseInt(fromServer[3]),Integer.parseInt(fromServer[4]));
                break;
            case "CARD":
                this.model.makeMove(Integer.parseInt(fromServer[1]),Integer.parseInt(fromServer[2]),fromServer[3]);
                break;
            case "GAME_OVER":
                try{
                    Thread.sleep(1000);
                    this.model.gameStatus = "GAME OVER";
                    this.model.incNumOfMatches();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                break;
            case "ERROR":
                try {
                    this.model.gameStatus = "ERROR";
                    this.model.incNumOfMatches();
                    this.out.close();
                    this.echoSocket.close();
                    break;
                }catch (IOException e){
                    e.printStackTrace();
                }
        }

    }

    /**
     * Action method for buttons in the GUI
     * @param i int x coordinate
     * @param j int y coordinate
     */
    public void makeMove(int i, int j){
        System.out.println(String.format("REVEAL %d %d", i, j));
        out.println(String.format("REVEAL %d %d", i, j));
    }

    /**
     * Listner thread run method. creates try with read buffer resource.
     */
    public void run()  {
        String fromServer;
        String[] fromServerArray;
        try(BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));){
            while((fromServer = in.readLine())!=null){
                fromServerArray = fromServer.split(" ");
                System.out.println(fromServer);
                this.changeModel(fromServerArray);

            }

        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}