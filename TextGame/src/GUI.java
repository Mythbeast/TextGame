

import java.util.*;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
// import javafx.event.ActionEvent;
// import javafx.event.EventHandler;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

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
  // equipment variables
  private StringProperty playerWeapon = stringToStringProperty("");;
  private StringProperty playerShield = stringToStringProperty("");;
  private StringProperty playerArmour = stringToStringProperty("");;
  private StringProperty playerBoots = stringToStringProperty("");;
  private StringProperty playerHelmet = stringToStringProperty("");;
  private StringProperty playerRing = stringToStringProperty("");;
  private GridPane playerItemStats;
  private IntegerProperty playerItemHP = intToIntegerProperty(0);
  private IntegerProperty playerItemAttack = intToIntegerProperty(0);
  private IntegerProperty playerItemDefence = intToIntegerProperty(0);
  private IntegerProperty playerItemCritChance = intToIntegerProperty(0);
  private IntegerProperty playerItemCritDamage = intToIntegerProperty(0);
     
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
  // private Button option1;
  // private Button option2;
  // private Button option3;
  // private Button option4;
  // private Button option5;

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
    SimpleBindingIntegerLabel playerLevelLabel = new SimpleBindingIntegerLabel("Level: ", this.playerLevel, "");

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

    GridPane playerStats = new GridPane();
    SimpleBindingIntegerLabel playerAttackLabel = new SimpleBindingIntegerLabel("Attack: ", this.playerAttack, "");
    SimpleBindingIntegerLabel playerDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", this.playerDefence, "");
    SimpleBindingIntegerLabel playerCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ", this.playerCritChance, "");
    SimpleBindingIntegerLabel playerCritDamageLabel = new SimpleBindingIntegerLabel("Bonus Crit Damage: ", this.playerCritDamage, "");
    SimpleBindingIntegerLabel playerGoldLabel = new SimpleBindingIntegerLabel("Gold: ", this.playerGold, "");

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
    
    
    playerStats.add(playerAttackLabel, 0, 0);
    playerStats.add(playerDefenceLabel, 0, 1);
    playerStats.add(playerCritChanceLabel, 0, 2);
    playerStats.add(playerCritDamageLabel, 0, 3);
    playerStats.add(playerXPLabel, 0, 4);
    playerStats.add(playerGoldLabel, 0, 5);

    playerItemStats = new GridPane();

    GridPane playerEquipment = new GridPane();
    SimpleBindingStringLabel playerWeaponLabel = new SimpleBindingStringLabel("Weapon: ", this.playerWeapon, "");
    SimpleBindingStringLabel playerShieldLabel = new SimpleBindingStringLabel("Shield: ", this.playerShield, "");
    SimpleBindingStringLabel playerArmourLabel = new SimpleBindingStringLabel("Armour: ", this.playerArmour, "");
    SimpleBindingStringLabel playerBootsLabel = new SimpleBindingStringLabel("Boots: ", this.playerBoots, "");
    SimpleBindingStringLabel playerHelmetLabel = new SimpleBindingStringLabel("Helmet: ", this.playerHelmet, "");
    SimpleBindingStringLabel playeRingLabel = new SimpleBindingStringLabel("Ring: ", this.playerRing, "");    
    playerEquipment.add(playerWeaponLabel, 0, 0);
    playerEquipment.add(playerShieldLabel, 0, 1);
    playerEquipment.add(playerArmourLabel, 0, 2);
    playerEquipment.add(playerBootsLabel, 0, 3);
    playerEquipment.add(playerHelmetLabel, 0, 4);
    playerEquipment.add(playeRingLabel, 0, 5);

    this.playerUI.add(playerStats, 0, 0);
    this.playerUI.add(playerEquipment, 0, 1);
    this.playerUI.add(playerItemStats, 1, 0);


    

    // Monster UI section-----------------------------------------------------
    // Create gridpane to contain monster information
    this.monsterUI = new GridPane();
    this.monsterUI.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
    this.monsterUI.setHgap(10);
    this.monsterUI.setVgap(10);

    // create Label for monstername to be edited when a monster is created
    this.monsterName = new Label();
    
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
    root.setColumnSpan(eventUI, 11);

   
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

  public void playerGoldUpdate() {
    this.playerGold.set(this.player.getGold());
  }

  public void print(String string, Color colour, String style){
    Text text = new Text();
    if (style.contains("continuous")) {
      text.setText(string);
    }
    else {
      text.setText("\n" + string);
    }
    switch (style) {
      // TODO: alter code to allow for continuous AND bold/italic etc.
      
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
        text.setFont(Font.font("System Regular", FontWeight.MEDIUM, FontPosture.REGULAR, 12));
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
      playerCurrentHPUpdate();
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

  public void playerCurrentHPUpdate() {
    this.playerCurrentHPVisible.set(player.getCurrentHP());
    double playerHPPercent = (double) this.playerCurrentHPVisible.get()/ this.playerMaxHPVisible.get(); 
    this.playerHPBar.setWidth(PLAYERHPWIDTH*playerHPPercent);
    System.out.println(this.playerCurrentHPVisible);
  }

  public void levelUp(int level, int newMaxHP) {
    print("Level Up!", Color.GOLDENROD, "bold");
    print("You are now level " + level, Color.BLACK, "");
    this.playerCurrentHPVisible.set(newMaxHP);
    this.playerMaxHPVisible.set(newMaxHP);
    this.playerHPBar.setWidth(PLAYERHPWIDTH);
    this.playerLevel.set((this.playerLevel.get() + 1));
    this.playerAttack.set(this.player.getAttack());
    this.playerDefence.set(this.player.getDefence());
    this.playerCritChance.set(this.player.getCritChance());
    this.playerCritDamage.set(this.player.getCritDamage());
    this.xpForNextLevel.set(this.player.getXPForNextLevel());
  }

  public void newEquipment(Equipment equipment, String type) {
    // get key details
    String name = equipment.getName();
    HashMap<String, Integer> combatStats = equipment.getCombatStats();
    String statString = statString(combatStats);

    // output equipment info to TextFlow
    printSpace();
    print("You have found a " + name, Color.BLACK, "");
    print(statString, Color.BLACK, "");

    switch (type) {
      case "weapon1":
        this.playerWeapon.set(name + " (" + statString + ")");
        break;
      case "shield":
        this.playerShield.set(name + " (" + statString + ")");
        break;
      case "weapon2":
        this.playerWeapon.set(name + " (" + statString + ")");
        this.playerShield.set("(N/A - Two Handed Weapon Equipped)");
        break;
      case "armour":
        this.playerArmour.set(name + " (" + statString + ")");
        break;
      case "boots":
        this.playerBoots.set(name + " (" + statString + ")");
        break;
      case "helmet":
        this.playerHelmet.set(name + " (" + statString + ")");
        break;
      case "ring":
        this.playerRing.set(name + " (" + statString + ")");
        break;
    }
    ItemStatUpdate();
  }

  public void ItemStatUpdate() {
    // clear previous itemStat Labels
    playerItemStats.getChildren().clear();

    // add new labels
    HashMap<String, Integer> itemStats = this.player.getItemStats();
    for (HashMap.Entry<String, Integer> entry : itemStats.entrySet()) {
      String stat = entry.getKey();
      switch(stat) {
        case "HP: ":
          this.playerItemHP.set(entry.getValue());
          if (this.playerItemHP.get() != 0) {
            SimpleBindingIntegerLabel playerItemHPLabel = new SimpleBindingIntegerLabel("(", this.playerItemHP, ")");
          }
          break;
        case "Attack: ":
          this.playerItemAttack.set(entry.getValue());
          if (this.playerItemAttack.get() != 0) {
            SimpleBindingIntegerLabel playerItemAttackLabel = new SimpleBindingIntegerLabel("(", this.playerItemAttack, ")");
            this.playerItemStats.add(playerItemAttackLabel, 0,1);
          }
          break;
          case "Defence: ":
          this.playerItemDefence.set(entry.getValue());
          if (this.playerItemDefence.get() != 0) {
            SimpleBindingIntegerLabel playerItemDefenceLabel = new SimpleBindingIntegerLabel("(", this.playerItemDefence, ")");
            this.playerItemStats.add(playerItemDefenceLabel, 0,2);
          }
          break;
          case "Crit Chance: ":
          this.playerItemCritChance.set(entry.getValue());
          if (this.playerItemCritChance.get() != 0) {
            SimpleBindingIntegerLabel playerItemCritChanceLabel = new SimpleBindingIntegerLabel("(", this.playerItemDefence, ")");
            this.playerItemStats.add(playerItemCritChanceLabel, 0,3);
          }
          break;
          case "Crit Damage: ":
          this.playerItemCritDamage.set(entry.getValue());
          if (this.playerItemCritDamage.get() != 0) {
            SimpleBindingIntegerLabel playerItemCritDamageLabel = new SimpleBindingIntegerLabel("(", this.playerItemCritDamage, ")");
            this.playerItemStats.add(playerItemCritDamageLabel, 0,4);
          }
          break;
      }
    }
  }

  private String statString(HashMap<String, Integer> combatStats) {
    // function to get String of all equipment stats
    String statPrint = "";
    for (HashMap.Entry<String, Integer> entry : combatStats.entrySet()) {
      String stat = entry.getKey();
      Integer value = entry.getValue();
      statPrint = statPrint + stat + value;
    }
    return statPrint;
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

    SimpleBindingIntegerLabel monsterAttackLabel = new SimpleBindingIntegerLabel("Attack: ", monsterAttack, "");
    SimpleBindingIntegerLabel monsterDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", monsterDefence, "");
    SimpleBindingIntegerLabel monsterCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ", monsterCritChance, "");
    SimpleBindingIntegerLabel monsterCritDamageLabel = new SimpleBindingIntegerLabel("Bonus Crit Damage: ", monsterCritDamage, "");
    SimpleBindingIntegerLabel monsterGoldLabel = new SimpleBindingIntegerLabel("Gold: ", monsterGold, "");
    SimpleBindingIntegerLabel monsterXPLabel = new SimpleBindingIntegerLabel("XP: ", monsterXP, "");

    GridPane monsterStats = new GridPane();
    

    this.monsterUI.add(monsterHPText, 1, 1);
    this.monsterUI.add(this.monsterMissingHPBar, 0, 1);
    this.monsterUI.add(this.monsterHPBar, 0, 1);
    this.monsterHPBar.setWidth(MONHPWIDTH);
    monsterStats.add(monsterAttackLabel, 0, 0);
    monsterStats.add(monsterDefenceLabel, 0, 1);
    monsterStats.add(monsterCritChanceLabel, 0, 2);
    monsterStats.add(monsterCritDamageLabel, 0, 3);
    monsterStats.add(monsterGoldLabel, 0, 4);
    monsterStats.add(monsterXPLabel, 0, 5);

    this.monsterUI.add(monsterStats, 0, 2);
  }

  public void removeMonster() {
    this.currentXP.set(player.getXP());
    this.playerGold.set(player.getGold());
    monsterName.textProperty().bind(stringToStringProperty("Monster: "));
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 1);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 1);
    monsterUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 2);
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

  public void removeEvent() {
    for(int i = 0; i< 5; i++) {
      final int column = i;
      eventUI.getChildren().removeIf( node -> GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == 0);
    }
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
