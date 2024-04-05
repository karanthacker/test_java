package model;
import view.ConcentartionGUI;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * class creating the game board and implementing the rules of game. The GUI board is
 * refreshed as per the changes in this class object made by the controller
 */
public class ConcentrationModel  {
    public String[][] gameBoard ;
    private int DIM;
    private ConcentartionGUI gui;
    public int numOfMoves = 0;
    public int cardOneXPos;
    public int cardOneYPos;
    public int cardTwoXPos;
    public int getCardTwoYPos;
    public boolean misMatch = false;
    public int numOfMatches = 0;
    public boolean updateMatch = false;
    public  String gameStatus = "OK";

    /** the collection of observers of this model */
    private List < Observer< ConcentrationModel > > observers = new LinkedList<>();
    public HashMap<String,String> pokemap;

    /**
     * Constructor
     * @param gui ConcentartionGUI as instance variable to create grid object
     */
    public ConcentrationModel(ConcentartionGUI gui){
        this.gui = gui;
        this.pokemap = createPokeMap();
    }
    /**
     *
     * @param dim dimension of the matrix
     */
    public void fillBoard(int dim){
        this.DIM = dim;
        this.gameBoard = new String[dim][dim];
        for(int i=0;i<dim;i++){
            for(int j=0;j<dim;j++){
                this.gameBoard[i][j]= ".";
            }
        }
        this.gui.makeGridPane(dim);
    }

    /**
     *  creating Hashmap for (letter,image) pairs
     * @return HashMap pokemap
     */
    private HashMap createPokeMap(){
        HashMap<String,String> pokemap = new HashMap<>();
        pokemap.put("A","images/abra.png");
        pokemap.put("B","images/bulbasaur.png");
        pokemap.put("C","images/charizard.png");
        pokemap.put("D","images/diglett.png");
        pokemap.put("E","images/golbat.png");
        pokemap.put("F","images/golem.png");
        pokemap.put("G","images/jigglypuff.png");
        pokemap.put("H","images/magikarp.png");
        pokemap.put("I","images/meowth.png");
        pokemap.put("J","images/mewtwo.png");
        pokemap.put("K","images/natu.png");
        pokemap.put("L","images/pidgey.png");
        pokemap.put("M","images/pikachu.png");
        pokemap.put("N","images/poliwag.png");
        pokemap.put("O","images/psyduck.png");
        pokemap.put("P","images/rattata.png");
        pokemap.put("Q","images/slowpoke.png");
        pokemap.put("R","images/snorlak.png");
        pokemap.put("S","images/squirtle.png");
        pokemap.put(".","images/pokeball.png");
        return pokemap;
    }

    /**
     * Action performed on revelation of card value from server
     * @param i int x co-ordinate
     * @param j int y co-ordinate
     * @param alphabet String
     */
    public void makeMove(int i , int j,String alphabet){
        this.updateMatch = false;
        this.cardOneXPos = i;
        this.cardOneYPos = j;
        this.numOfMoves+=1;
        this.gameBoard[i][j] = alphabet;
        this.misMatch = false;
        this.alertObservers();
    }

    /**
     *  method invoked on a mismatch
     * @param i int x co-ordinate for card 1
     * @param j int y co-ordinate for card 1
     * @param k int x co-ordinate  for card 2
     * @param l int y co-ordinate  for card 2
     */
    public void undoMove(int i, int j, int k, int l){
        this.cardOneXPos = i;
        this.cardOneYPos = j;
        this.cardTwoXPos = k;
        this.getCardTwoYPos = l;
        this.gameBoard[i][j] = ".";
        this.gameBoard[k][l] = ".";
        this.misMatch = true;
        this.alertObservers();
    }


    public void incNumOfMatches() {
        this.updateMatch = true;
        if(this.gameStatus.equals("OK"))this.numOfMatches +=1;
        this.alertObservers();

    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver( Observer< ConcentrationModel > observer ){
        observers.add( observer );
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(){
        for ( Observer< ConcentrationModel > observer: observers ) {
            observer.update( this );
        }
    }
}
