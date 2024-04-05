/*
Author: Karan
HW9 : Concentration GAME CLIENT (GUI)
 */

package view;
import controller.ConcentrationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ConcentrationModel;
import model.Observer;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.util.*;

/**
 * Class ConcentartionGUI runs the GUI GAME APPLICATION.
 */
public class ConcentartionGUI extends Application implements Observer<ConcentrationModel> {
    private ConcentrationController controller;
    private ConcentrationModel model;
    public GridPane gridPane;
    private Label gameStatus;
    private  Label matches;
    private  Label moves;
    private Image pokeball = new Image(getClass().getResourceAsStream("images/pokeball.png"));
    private static final Background BLUE =
            new Background( new BackgroundFill(Color.BLUE, null, null));
    private static final Background WHITE =
            new Background( new BackgroundFill(Color.WHITE, null, null));
    public ConcentartionGUI.PokemonButton[][] buttons ;

    /**
     * initialization method. creating the model and controller objects.
     * also starting the listener thread
     */
    public void init(){
        try {
            List<String> portDetails = getParameters().getRaw();
            this.model = new ConcentrationModel(this);
            this.model.addObserver(this);
            this.controller = new ConcentrationController(model, portDetails.get(0), portDetails.get(1));
            this.controller.start();
            while(this.gridPane == null) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * initializes gridapane with 2D buttons
     * @param dim int number of rows and columns
     */
    public void makeGridPane(int dim){
        this.gridPane = new GridPane();
        this.buttons = new PokemonButton[dim][dim];
        for(int i =0;i<dim;i++){
            for(int j =0;j<dim;j++){
                buttons[i][j] = new PokemonButton(this.pokeball);
                int k = j;
                int l = i;
                buttons[i][j].setOnAction((event->{
                    if(!buttons[l][k].isRevealed()){
                    controller.makeMove(l,k);}}));
                gridPane.add(buttons[i][j], j, i);
            }
        }

    }

    /**
     * Pokemon Button Class
     */
    public class PokemonButton extends Button{
        private boolean reveal = false;
        private Image image;

        /**
         * Constructor creating pokeball image button
         * @param image
         */
        public PokemonButton(Image image){
            this.image = image;
            this.setGraphic(new ImageView(image));
            this.setBackground(BLUE);
        }

        /**
         * method called in refresh to change the image of the button
         * @param pokemonImage
         */
        public void reveal(String pokemonImage){
            this.reveal = true;
            Image image = new Image(getClass().getResourceAsStream(pokemonImage));
            this.setGraphic(new ImageView((image)));
            if(pokemonImage.equals("images/pokeball.png")){
                this.setBackground(BLUE);
                this.reveal = false;
            }else {
                this.setBackground(WHITE);
            }
         }
        public boolean isRevealed(){
            return this.reveal;
        }
    }

    /**
     * Creating stage to be displayed. Heirarchy
     * stage -> scene -> pane -> button
     * @param stage Stage stage -> scene -> pane -> button
     */
    public void start(Stage stage){
        System.out.println("start");
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(this.gridPane);
        GridPane statusGrid = new GridPane();
        this.moves = new Label("Moves: "+String.valueOf(this.model.numOfMoves));
        this.gameStatus = new Label("OK");
        this.matches = new Label("Matches: "+this.model.numOfMatches);

        statusGrid.add(moves,0,0);
        statusGrid.add(matches,1,0);
        statusGrid.add(gameStatus,2,0);
        matches.setPadding(new Insets(0,10,0,0));
        gameStatus.setPadding(new Insets(0,0,0,30));
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(33);
        statusGrid.getColumnConstraints().addAll(column1,column1,column1);
        statusGrid.setAlignment(Pos.BOTTOM_CENTER);
        borderPane.setBottom(statusGrid);
        Scene scene = new Scene(borderPane);
        stage.setTitle("Concentration GUI");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

    }

    /**
     *Updation in the GUI on the basis of values returned from the model
     * @param model ConcentrationModel
     */
    public void refresh(ConcentrationModel model){
        if(this.model.updateMatch) {
            this.matches.setText("Matches: " + this.model.numOfMatches);
            if(this.model.gameStatus.equals("GAME OVER")) gameStatus.setPadding(new Insets(0,0,0,0));
            this.gameStatus.setText(this.model.gameStatus);
        }else {
            int i = model.cardOneXPos;
            int j = model.cardOneYPos;
            String image = this.model.pokemap.get(this.model.gameBoard[i][j]);
            this.moves.setText("Moves: " + String.valueOf(this.model.numOfMoves));

            this.buttons[i][j].reveal(image);
            if (this.model.misMatch) {
                int k = model.cardTwoXPos;
                int l = model.getCardTwoYPos;
                String imageTwo = this.model.pokemap.get(this.model.gameBoard[k][l]);
                this.buttons[k][l].reveal(imageTwo);
            }
        }
    }


    @Override
    public void update(ConcentrationModel model) {
        /*
         * Note that just calling setText directly is OK because
         * we know that in this system the update call was triggered
         * by a GUI event. But in the more general case we do not know
         * that, so the more universal approach is shown here for reference.
         */
        if ( Platform.isFxApplicationThread() ) {
            this.refresh( model );
        }
        else {
            Platform.runLater( () -> this.refresh( model ) );
        }
    }

    /**
     * method called at closing the window. clean up activity and closing the
     * listener thread socket
     */
    @Override
    public void stop(){
        try{
            this.controller.out.close();
            this.controller.echoSocket.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Application.launch(args);
    }
}
