/*
author : Karan Thacker
Server Thread for Concentration Game
 */
package server;

import common.ConcentrationException;
import game.ConcentrationBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import game.ConcentrationBoard.CardMatch;

/**
 * Class ConcentrationClientServerThread
 * Establishes connection with client and uses helper Class ConcentrationBoard
 * executing the gameplay
 *
 */
public class ConcentrationClientServerThread extends Thread {
    private Socket socket = null;
    private ConcentrationBoard gameBoard;
    private int dim ;
    private boolean exit = false;

    /**
     * Creates Server thread with board game dimension and socket
     * @param socket
     * @param dim
     */
    public ConcentrationClientServerThread(Socket socket,int dim) {
        super("ConcentrationClientServerThread");
        this.socket = socket;
        this.dim = dim;

    }

    /**
     * method takes in message from client, modifies game board and sends message back to client
     * @param fromClient
     * @param out
     * @throws ConcentrationException
     */
    public void processInput(String fromClient,PrintWriter out) throws ConcentrationException{
        String[] fromClientArray = fromClient.split(" ");
        String outputLine;
        try {
            if (fromClientArray.length != 3 || (!fromClientArray[0].equals("REVEAL"))) {
                outputLine = String.format("ERROR %s", "incorrect reply protocol");
                throw new ConcentrationException(outputLine);
            }
            int row = Integer.parseInt(fromClientArray[1]);
            int col = Integer.parseInt(fromClientArray[2]);
            try {
                Thread.sleep(200);
                out.println(String.format("CARD %d %d %s", row, col, gameBoard.getCard(row, col).getLetter()));
            } catch (InterruptedException e){

            }
            CardMatch result = gameBoard.reveal(row, col);
            if (result != null) {
                if (result.isMatch()) {
                    out.println(String.format("MATCH %d %d %d %d", row, col, result.getCard1().getRow(), result.getCard1().getCol()));
                } else {
                    try{
                        Thread.sleep(1000);
                        out.println(String.format("MISMATCH %d %d %d %d", row, col, result.getCard1().getRow(), result.getCard1().getCol()));
                        result.getCard1().hide();
                        result.getCard2().hide();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                if(this.gameBoard.gameOver()){
                    throw new ConcentrationException("GAME_OVER");
                }
            }
        }catch(ConcentrationException e){
            out.println(e.getMessage());
            System.out.println(e.getMessage());
            this.exit = true;
        }
    }

    /**
     * Run method of thread executing try with resources
     */
    public void run()   {
        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
        ) {
            String fromClient;
            this.gameBoard = new ConcentrationBoard(this.dim);
            out.println(String.format("BOARD_DIM %d",this.dim));
            while(((fromClient = in.readLine()) != null) && !this.exit){
                this.processInput(fromClient,out);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }catch (ConcentrationException e){
            System.out.println(e.getMessage());

        }
    }

}
