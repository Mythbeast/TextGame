

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
// import javafx.event.ActionEvent;
// import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.*;

public class GUI extends Application{
  private static GameLogic game1;
  private Player player;
  private ObservableList<String> discoveredAreas;
  private ArrayList<String> discoveredAreasArrayList;

  public static void setGameLogic(GameLogic game) {
    game1 = game;
  }

  @Override
  public void start(Stage primaryStage) {
    game1.setGUI(this);
    getPlayer(game1);
        
    // layout manager
    GridPane root = new GridPane();
    root.setHgap(10);
    root.setVgap(10);

    // create explore button
    Button exploreButton = new Button("Explore");
    
    // bind the button to the explore event
    exploreButton.setOnAction(event -> {
      game1.onExploreButton();
    });

    // create area combobox
    this.discoveredAreasArrayList = (player.getDiscoveredAreaNames());
    this.discoveredAreas = FXCollections.observableArrayList(discoveredAreasArrayList);
    ComboBox areaSelect = new ComboBox(discoveredAreas);
    
    // areaSelect.setItems(discoveredAreas);
    areaSelect.getSelectionModel().selectFirst();

    areaSelect.setOnAction(event -> {
      // pass index of chosen area from discoveredAreas
      String selectedArea = (String) areaSelect.getValue();
      int index = discoveredAreas.indexOf(selectedArea);
      System.out.println("test");
      System.out.println(index);
      game1.onAreaSelect(index);
    });

    // add items to window to row a col b
    root.add(areaSelect, 1, 0);
    root.add(exploreButton, 2, 0);
    
    // set the scene and show the window
    Scene scene = new Scene(root, 1000, 1000);
    primaryStage.setTitle("TextGame");
    primaryStage.setScene(scene);
    primaryStage.show(); 

    startText();

  }

  private void getPlayer(GameLogic game) {
    this.player = game.getPlayer();
  }

  public void startText() {
    String startText = "You stand before the towering castle gates, their ancient iron bars gleaming in the midday sun. The castle itself rises majestically against the horizon, its stone walls weathered but resilient. To your left and right, the verdant fields of several farms stretch out, dotted with the bustling activity of farmers tending to their crops and animals. As you gaze at the imposing structure of the castle and the tranquil surroundings, you can't help but feel a sense of anticipation for the adventures that lie ahead.";
    System.out.println(startText);
  }

  public void discoverArea(String areaName) {
    discoveredAreas.add(areaName);
    

  }

  public void damageUpdate(String attacker, String defender, int damage, int critSuccess, int newHP) {
    
    if (critSuccess == 1) {
      System.out.println(attacker + " scored a critical hit!");
    }
    System.out.println(attacker + " dealt " + damage + " to the " + defender);
    System.out.println(defender + " now has " + newHP + " health remaining.");
    }

  public void levelUp(int level) {
    System.out.println("Level Up!");
    System.out.println("You are now level " + level);
  }

}
