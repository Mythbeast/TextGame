

import javafx.animation.Interpolatable;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
// import javafx.event.ActionEvent;
// import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import java.util.*;

public class GUI extends Application{
  private static GameLogic game1;
  private Player player;
  private GridPane root;
  // variables used to manage areaSelect ComboBox
  private ObservableList<String> discoveredAreas;
  private ArrayList<String> discoveredAreasArrayList;
  // variable for text output
  private TextFlow textBox;
 
  // player UI variables
  private GridPane playerUI;
  private IntegerProperty playerAttack;
  private IntegerProperty playerDefence;
  private IntegerProperty playerCritChance;
  private IntegerProperty playerCritDamage;
  private IntegerProperty playerGold;
  // variables for managing XP
  private IntegerProperty currentXP;
  private IntegerProperty xpForNextLevel;
  // variables for managing player HP bar and level
  private IntegerProperty playerLevel;
  private Rectangle playerHPBar;
  private IntegerProperty playerCurrentHPVisible;
  private IntegerProperty playerMaxHPVisible;
     
  // monster UI variables
  private GridPane monsterUI;
  private Label monsterName;
  private Rectangle monsterHPBar;
  private Rectangle monsterMissingHPBar;
  private IntegerProperty monsterCurrentHPVisible;
  private IntegerProperty monsterMaxHPVisible;
  private IntegerProperty monsterAttack;
  private IntegerProperty monsterDefence;
  private IntegerProperty monsterCritChance;
  private IntegerProperty monsterCritDamage;
  private IntegerProperty monsterGold;
  private IntegerProperty monsterXP;

  // event UI variables
  private GridPane eventUI;
  private Button option1;
  private Button option2;
  private Button option3;
  private Button option4;
  private Button option5;

  int WINDOWWIDTH = 1000;
  int WINDOWHEIGHT = 1000;
  int TEXTBOXWIDTH = 600;
  int TEXTBOXHEIGHT = 400;
  int PLAYERHPWIDTH = 900;
  int PLAYERHPHEIGHT = 30;
  int MONHPWIDTH = 100;
  int MONHPHEIGHT = 30;
  int HPCURVE = 15;

  public static void setGameLogic(GameLogic game) {
    game1 = game;
  }

  @Override
  public void start(Stage primaryStage) {
    



    game1.setGUI(this);
    getPlayer(game1);
    getPlayerStats();
        
    // layout manager
    root = new GridPane();
    root.setHgap(10);
    root.setVgap(10);
    root.setPadding(new Insets(10, 10, 10, 10));

    // Text box creation
    this.textBox = new TextFlow();
    textBox.setPrefSize(TEXTBOXWIDTH, TEXTBOXHEIGHT);
    textBox.setStyle("-fx-border-color: black; -fx-border-width: 2px;");    
    // textBox.setLineSpacing(-2.0);

    // create ScrollPane to ensure scrolling of text in textBox
    ScrollPane scrollPane = new ScrollPane(textBox);
    scrollPane.setFitToWidth(true);
    scrollPane.vvalueProperty().bind(textBox.heightProperty());
    scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
    
    // create explore button
    Button exploreButton = new Button("Explore");
    
    // bind the button to the explore event
    exploreButton.setOnAction(event -> {
      game1.onExploreButton();
    });

    // create areaSelect combobox
    this.discoveredAreasArrayList = (player.getDiscoveredAreaNames());
    this.discoveredAreas = FXCollections.observableArrayList(discoveredAreasArrayList);
    ComboBox areaSelect = new ComboBox(discoveredAreas);
    
    // areaSelect.setItems(discoveredAreas);
    areaSelect.getSelectionModel().selectFirst();

    areaSelect.setOnAction(event -> {
      // pass index of chosen area from discoveredAreas
      String selectedArea = (String) areaSelect.getValue();
      int index = discoveredAreas.indexOf(selectedArea);
      game1.onAreaSelect(index);
    });

    

    // create label for playerLevel
    SimpleBindingIntegerLabel playerLevelLabel = new SimpleBindingIntegerLabel("Level: ", this.playerLevel);

    // create player HP label
    Label playerHPText = new Label();
        
    // copy hp values to ObservableIntegerValue and bind to Label using StringBinding
    this.playerCurrentHPVisible =  intToIntegerProperty(game1.getPlayer().getCurrentHP());
    this.playerMaxHPVisible= intToIntegerProperty(game1.getPlayer().getMaxHP());
    StringBinding playerHPBinding = new StringBinding() {
      {
        super.bind(playerCurrentHPVisible, playerMaxHPVisible);
      }
      @Override
      protected String computeValue() {
        return playerCurrentHPVisible.get() + " / " + playerMaxHPVisible.get();
      }
    };
    playerHPText.textProperty().bind(playerHPBinding);

    // create player HP bar
    this.playerHPBar = new Rectangle();
    playerHPBar.setFill(Color.GREEN);
    playerHPBar.setWidth(PLAYERHPWIDTH);
    playerHPBar.setHeight(PLAYERHPHEIGHT);
    playerHPBar.setArcWidth(HPCURVE);
    playerHPBar.setArcHeight(HPCURVE);

    // Create red missing hp
    Rectangle playerMissingHPBar = new Rectangle();
    playerMissingHPBar.setFill(Color.RED);
    playerMissingHPBar.setWidth(PLAYERHPWIDTH);
    playerMissingHPBar.setHeight(PLAYERHPHEIGHT);
    playerMissingHPBar.setArcWidth(HPCURVE);
    playerMissingHPBar.setArcHeight(HPCURVE);

    // player UI section------------------------------------------------------ 
    // Create gridpane to contain player information
    this.playerUI = new GridPane();
    this.playerUI.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
    this.playerUI.setHgap(10);
    this.playerUI.setVgap(10);

    SimpleBindingIntegerLabel playerAttackLabel = new SimpleBindingIntegerLabel("Attack: ", this.playerAttack);
    SimpleBindingIntegerLabel playerDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", this.playerDefence);
    SimpleBindingIntegerLabel playerCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ", this.playerCritChance);
    SimpleBindingIntegerLabel playerCritDamageLabel = new SimpleBindingIntegerLabel("Bonus Crit Damage: ", this.playerCritDamage);
    SimpleBindingIntegerLabel playerGoldLabel = new SimpleBindingIntegerLabel("Gold: ", this.playerGold);

    // create XP Label
    Label playerXPLabel = new Label();
    // copy xp values to ObservableIntegerValue and bind to Label using StringBinding
    this.currentXP =  intToIntegerProperty(this.player.getXP());
    this.xpForNextLevel= intToIntegerProperty(this.player.getXPForNextLevel());
    StringBinding playerXPBinding = new StringBinding() {
      {
        super.bind(currentXP, xpForNextLevel);
      }
      @Override
      protected String computeValue() {
        return "XP: " + currentXP.get() + " / " + xpForNextLevel.get();
      }
    };
    playerXPLabel.textProperty().bind(playerXPBinding);
    
    
    this.playerUI.add(playerAttackLabel, 0, 2);
    this.playerUI.add(playerDefenceLabel, 0, 3);
    this.playerUI.add(playerCritChanceLabel, 0, 4);
    this.playerUI.add(playerCritDamageLabel, 0, 5);
    this.playerUI.add(playerXPLabel, 0, 6);
    this.playerUI.add(playerGoldLabel, 0, 7);


    

    // Monster UI section-----------------------------------------------------
    // Create gridpane to contain monster information
    this.monsterUI = new GridPane();
    this.monsterUI.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
    this.monsterUI.setHgap(10);
    this.monsterUI.setVgap(10);

    // create Label for monstername to be edited when a monster is created
    this.monsterName = new Label();
    // SimpleBindingIntegerLabel monsterAttackLabel = new SimpleBindingIntegerLabel("Attack: ", this.monsterAttack);
    // SimpleBindingIntegerLabel monsterDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", this.monsterDefence);
    // SimpleBindingIntegerLabel monsterCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ", this.monsterCritChance);
    // SimpleBindingIntegerLabel monsterCritDamageLabel = new SimpleBindingIntegerLabel("Crit Damage: ", this.monsterCritDamage);
    // SimpleBindingIntegerLabel monsterGoldLabel = new SimpleBindingIntegerLabel("Gold: ", this.monsterGold);
    

    
    // create monster HP bar
    this.monsterHPBar = new Rectangle();
    this.monsterHPBar.setFill(Color.GREEN);
    this.monsterHPBar.setWidth(MONHPWIDTH);
    this.monsterHPBar.setHeight(MONHPHEIGHT);
    this.monsterHPBar.setArcWidth(HPCURVE);
    this.monsterHPBar.setArcHeight(HPCURVE);

    // Create red missing hp
    this.monsterMissingHPBar = new Rectangle();
    this.monsterMissingHPBar.setFill(Color.RED);
    this.monsterMissingHPBar.setWidth(MONHPWIDTH);
    this.monsterMissingHPBar.setHeight(MONHPHEIGHT);
    this.monsterMissingHPBar.setArcWidth(HPCURVE);
    this.monsterMissingHPBar.setArcHeight(HPCURVE);

    // add items to monsterUI
    this.monsterUI.add(monsterName, 0, 0);

    // called to set window to default state
    removeMonster();

    // event UI section--------------------------------------------------------
    this.eventUI = new GridPane();
    this.eventUI.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
    this.eventUI.setHgap(10);
    this.eventUI.setVgap(10);

    // add items to window to row a col b
    root.add(areaSelect, 1, 4);
    root.add(exploreButton, 2, 4);
    root.add(scrollPane, 3, 3);
    root.setColumnSpan(scrollPane, 3);
    root.add(playerMissingHPBar, 0, 0);
    root.setColumnSpan(playerMissingHPBar, 9);
    root.setRowSpan(playerMissingHPBar, 2);
    root.add(playerHPBar, 0, 0);
    root.setColumnSpan(playerHPBar, 9);
    root.setRowSpan(playerHPBar, 2);
    root.add(playerLevelLabel, 9, 0);
    root.add(playerHPText, 9, 1);

    root.add(playerUI, 0, 3);
    root.setColumnSpan(playerUI, 3);
    root.add(monsterUI, 8, 3);
    root.setColumnSpan(monsterUI, 3);
    root.add(eventUI, 0, 10);
    root.setColumnSpan(eventUI, 9);

   
    // set the scene and show the window
    Scene scene = new Scene(root, WINDOWWIDTH, WINDOWHEIGHT);
    primaryStage.setTitle("TextGame");
    primaryStage.setScene(scene);
    primaryStage.show(); 

    startText();

  }

  private void getPlayer(GameLogic game) {
    this.player = game.getPlayer();
  }

  public void print(String String, Color colour, String style){
    Text text = new Text("\n" + String);
    switch (style) {
      
      case "italic":
        text.setFont(Font.font("System Regular", FontWeight.MEDIUM, FontPosture.ITALIC, 12));
        break;
      case "bold":
        text.setFont(Font.font("System Regular", FontWeight.BOLD, FontPosture.REGULAR, 12));
        break;
      case "space":
        text.setFont(Font.font("System Regular", FontWeight.BOLD, FontPosture.REGULAR, 3));
        break;
      default:
        break;
    }
    text.setFill(colour);
    textBox.getChildren().add(text);
  }

  public void printSpace() { 
    print("\n", Color.BLACK, "space");
  }

  public void startText() {
    String startText = "You stand before the towering castle gates, their ancient iron bars gleaming in the midday sun. The castle itself rises majestically against the horizon, its stone walls weathered but resilient. To your left and right, the verdant fields of several farms stretch out, dotted with the bustling activity of farmers tending to their crops and animals. As you gaze at the imposing structure of the castle and the tranquil surroundings, you can't help but feel a sense of anticipation for the adventures that lie ahead.";
    print(startText, Color.BLACK, "italic");
  }

  public void discoverArea(String areaName, String areaEncounterText) {
    discoveredAreas.add(areaName);
    printSpace();
    print(areaEncounterText, Color.BLACK, "italic");
    print("You have discovered the " + areaName + ".", Color.BLACK, "");
    

  }

  public void damageUpdate(String attacker, String defender, int damage, int critSuccess, int newHP) {
    
    if (critSuccess == 1) {
      print(attacker + " scored a critical hit!", Color.RED, "");
    }
    if (defender.equals("You")) {
      print(attacker + " dealt " + damage + " damage to " + defender, Color.BLACK, "");
      playerCurrentHPUpdate(damage);
    } else {
    print(attacker + " dealt " + damage + " damage to the " + defender, Color.BLACK, "");
    monHPUpdate(damage);
    }
  }

  private void getPlayerStats() {
    this.playerLevel = intToIntegerProperty(this.player.getLevel());
    this.playerAttack = intToIntegerProperty(this.player.getAttack());
    this.playerDefence = intToIntegerProperty(this.player.getDefence());
    this.playerCritChance = intToIntegerProperty(this.player.getCritChance());
    this.playerCritDamage = intToIntegerProperty(this.player.getCritDamage());
    this.playerGold = intToIntegerProperty(this.player.getGold());

  }

  private void playerCurrentHPUpdate(int damage) {
    this.playerCurrentHPVisible.set(Math.max(0,(this.playerCurrentHPVisible.get() - damage)));
    double playerHPPercent = (double) this.playerCurrentHPVisible.get()/ this.playerMaxHPVisible.get(); 
    this.playerHPBar.setWidth(900*playerHPPercent);
  }

  public void levelUp(int level, int newMaxHP) {
    print("Level Up!", Color.GOLDENROD, "bold");
    print("You are now level " + level, Color.BLACK, "");
    this.playerCurrentHPVisible.set(newMaxHP);
    this.playerMaxHPVisible.set(newMaxHP);
    this.playerHPBar.setWidth(900);
    this.playerLevel.set((this.playerLevel.get() + 1));
    this.playerAttack.set(this.player.getAttack());
    this.playerDefence.set(this.player.getDefence());
    this.playerCritChance.set(this.player.getCritChance());
    this.playerCritDamage.set(this.player.getCritDamage());
    this.xpForNextLevel.set(this.player.getXPForNextLevel());


  }

  public void newMonster(Monster mon) {
    setMonsterStats(mon);

    // create monster HP label
    Label monsterHPText = new Label();
    
    // copy hp values to ObservableIntegerValue and bind to Label using StringBinding
    this.monsterCurrentHPVisible =  intToIntegerProperty(mon.getCurrentHP());
    this.monsterMaxHPVisible= intToIntegerProperty(mon.getMaxHP());
    StringBinding MonsterHPBinding = new StringBinding() {
      {
        super.bind(monsterCurrentHPVisible, monsterMaxHPVisible);
      }
      @Override
      protected String computeValue() {
        return monsterCurrentHPVisible.get() + " / " + monsterMaxHPVisible.get();
      }
    };
    monsterHPText.textProperty().bind(MonsterHPBinding);

    SimpleBindingIntegerLabel monsterAttackLabel = new SimpleBindingIntegerLabel("Attack: ", monsterAttack);
    SimpleBindingIntegerLabel monsterDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", monsterDefence);
    SimpleBindingIntegerLabel monsterCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ", monsterCritChance);
    SimpleBindingIntegerLabel monsterCritDamageLabel = new SimpleBindingIntegerLabel("Bonus Crit Damage: ", monsterCritDamage);
    SimpleBindingIntegerLabel monsterGoldLabel = new SimpleBindingIntegerLabel("Gold: ", monsterGold);
    SimpleBindingIntegerLabel monsterXPLabel = new SimpleBindingIntegerLabel("XP: ", monsterXP);

    

    this.monsterUI.add(monsterHPText, 1, 1);
    this.monsterUI.add(this.monsterMissingHPBar, 0, 1);
    this.monsterUI.add(this.monsterHPBar, 0, 1);
    this.monsterHPBar.setWidth(MONHPWIDTH);
    this.monsterUI.add(monsterAttackLabel, 0, 2);
    this.monsterUI.add(monsterDefenceLabel, 0, 3);
    this.monsterUI.add(monsterCritChanceLabel, 0, 4);
    this.monsterUI.add(monsterCritDamageLabel, 0, 5);
    this.monsterUI.add(monsterGoldLabel, 0, 6);
    this.monsterUI.add(monsterXPLabel, 0, 7);
  }

  public void removeMonster() {
    this.currentXP.set(player.getXP());
    this.playerGold.set(player.getGold());
    monsterName.textProperty().bind(stringToStringProperty("Monster: "));
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 1);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 1);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 2);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 3);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 4);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 5);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 6);    
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 7);
  }

  private void setMonsterStats(Monster mon) {
    // display name
    // normal label instead of a SimpleBindingStringLabel so it can be bound to blank "Monster: " text when monster removed.
    String name = mon.getName();
    StringProperty textName = stringToStringProperty(name);
    StringBinding monNameBinding = new StringBinding() {
      {
        super.bind(textName);
      }
      @Override
      protected String computeValue() {
        return "Monster: " + textName.get();
      }
    };
    this.monsterName.textProperty().bind(monNameBinding);

    this.monsterAttack = intToIntegerProperty(mon.getAttack());
    this.monsterDefence = intToIntegerProperty(mon.getDefence());
    this.monsterCritChance = intToIntegerProperty(mon.getCritChance());
    this.monsterCritDamage = intToIntegerProperty(mon.getCritDamage());
    this.monsterGold = intToIntegerProperty(mon.getGold());
    this.monsterXP = intToIntegerProperty(mon.getXP());
  }

  private void monHPUpdate(int damage) {
    this.monsterCurrentHPVisible.set(Math.max(0,(this.monsterCurrentHPVisible.get() - damage)));
    double monsterHPPercent = (double) this.monsterCurrentHPVisible.get()/ this.monsterMaxHPVisible.get(); 
    this.monsterHPBar.setWidth(MONHPWIDTH*monsterHPPercent);
  }

  public void newEventOption(int i, List<Object> option) {
    // function to create a button and bind to the correct event in GameLogic
    Button optionButton = new Button((String) (option.get(0)));
    optionButton.setOnAction(event -> {
      game1.onOptionButton(option);
    });
    eventUI.add(optionButton, i, 0);
  }

  private IntegerProperty intToIntegerProperty(int value) {
    IntegerProperty observableValue = new SimpleIntegerProperty(value);
    return observableValue;
  }

  private StringProperty stringToStringProperty(String string) {
    StringProperty observableString = new SimpleStringProperty();
    observableString.set(string);
    return observableString;    
  }


}
