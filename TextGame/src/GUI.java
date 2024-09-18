
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class GUI extends Application {
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
  private DoubleProperty currentXp;
  private DoubleProperty xpForNextLevel;
  // variables for managing player HP bar and level
  private IntegerProperty playerLevel;
  private Rectangle playerHpBar;
  private IntegerProperty playerCurrentHpVisible;
  private IntegerProperty playerMaxHpVisible;
  // equipment variables
  private StringProperty playerWeapon = stringToStringProperty("");;
  private StringProperty playerShield = stringToStringProperty("");;
  private StringProperty playerArmour = stringToStringProperty("");;
  private StringProperty playerBoots = stringToStringProperty("");;
  private StringProperty playerHelmet = stringToStringProperty("");;
  private StringProperty playerRing = stringToStringProperty("");;
  private GridPane playerItemStats;
  private IntegerProperty playerItemHp = intToIntegerProperty(0);
  private IntegerProperty playerItemAttack = intToIntegerProperty(0);
  private IntegerProperty playerItemDefence = intToIntegerProperty(0);
  private IntegerProperty playerItemCritChance = intToIntegerProperty(0);
  private IntegerProperty playerItemCritDamage = intToIntegerProperty(0);

  // monster UI variables
  private GridPane monsterUI;
  private Label monsterName;
  private Rectangle monsterHpBar;
  private Rectangle monsterMissingHpBar;
  private IntegerProperty monsterCurrentHpVisible;
  private IntegerProperty monsterMaxHpVisible;
  private IntegerProperty monsterAttack;
  private IntegerProperty monsterDefence;
  private IntegerProperty monsterCritChance;
  private IntegerProperty monsterCritDamage;
  private IntegerProperty monsterGold;
  private DoubleProperty monsterXp;

  // event UI variables
  private GridPane eventUI;

  // menu UI variables
  private GridPane menuUI;
  private Label menuInfoText;
  private GridPane subMenu;
  private GridPane backpackMenu;
  private GridPane backpackButtonBox;
  private ScrollPane scrollableBackpack;
  private Equipment activeEquipment;
  private GridPane keyItemButtonBox;
  private ScrollPane scrollablekeyItems;

  private static final int WINDOW_WIDTH = 1000;
  private static final int WINDOW_HEIGHT = 1000;
  private static final int MAIN_TEXT_BOX_WIDTH = 600;
  private static final int MAIN_TEXT_BOX_HEIGHT = 400;
  private static final int PLAYER_HP_WIDTH = 900;
  private static final int PLAYER_HP_HEIGHT = 30;
  private static final int MON_HP_WIDTH = 100;
  private static final int MON_HP_HEIGHT = 30;
  private static final int HP_CURVE = 15;
  private static final int MENU_INFO_WIDTH = 500;
  private static final int MENU_INFO_HEIGHT = 30;
  private static final int BACKPACK_WIDTH = 900;
  private static final int BACKPACK_HEIGHT = 250;
  private static final int BACKPACK_COLS = 6;
  private static final Insets DEFAUL_INSETS = new Insets(10, 10, 10, 10);
  private static final int GRIDPANE_GAPS = 10;
  private static final String BORDER_STYLE = "-fx-border-color: black; -fx-border-width: 2px;";

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
    root.setHgap(GRIDPANE_GAPS);
    root.setVgap(GRIDPANE_GAPS);
    root.setPadding(DEFAUL_INSETS);

    // Text box creation
    this.textBox = new TextFlow();
    textBox.setPrefSize(MAIN_TEXT_BOX_WIDTH, MAIN_TEXT_BOX_HEIGHT);
    textBox.setStyle(BORDER_STYLE);
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
    ComboBox<String> areaSelect = new ComboBox<String>(discoveredAreas);

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
    Label playerHpTextLabel = new Label();
    SimpleBindingIntegerLabel playerItemHpLabel = new SimpleBindingIntegerLabel("(", this.playerItemHp, ")");

    // copy hp values to ObservableIntegerValue and bind to Label using
    // StringBinding
    this.playerCurrentHpVisible = intToIntegerProperty(game1.getPlayer().getCurrentHp());
    this.playerMaxHpVisible = intToIntegerProperty(game1.getPlayer().getMaxHp());
    StringBinding playerHpBinding = new StringBinding() {
      {
        super.bind(playerCurrentHpVisible, playerMaxHpVisible);
      }

      @Override
      protected String computeValue() {
        return playerCurrentHpVisible.get() + " / " + playerMaxHpVisible.get();
      }
    };
    playerHpTextLabel.textProperty().bind(playerHpBinding);

    // create player HP bar
    this.playerHpBar = new Rectangle();
    playerHpBar.setFill(Color.GREEN);
    playerHpBar.setWidth(PLAYER_HP_WIDTH);
    playerHpBar.setHeight(PLAYER_HP_HEIGHT);
    playerHpBar.setArcWidth(HP_CURVE);
    playerHpBar.setArcHeight(HP_CURVE);

    // Create red missing hp
    Rectangle playerMissingHpBar = new Rectangle();
    playerMissingHpBar.setFill(Color.RED);
    playerMissingHpBar.setWidth(PLAYER_HP_WIDTH);
    playerMissingHpBar.setHeight(PLAYER_HP_HEIGHT);
    playerMissingHpBar.setArcWidth(HP_CURVE);
    playerMissingHpBar.setArcHeight(HP_CURVE);

    // player UI section------------------------------------------------------
    // Create gridpane to contain player information
    this.playerUI = new GridPane();
    this.playerUI.setStyle(BORDER_STYLE);
    this.playerUI.setHgap(GRIDPANE_GAPS);
    this.playerUI.setVgap(GRIDPANE_GAPS);

    GridPane playerStats = new GridPane();
    Label playerStatsTitleLabel = new Label("Player Stats:");
    SimpleBindingIntegerLabel playerAttackLabel = new SimpleBindingIntegerLabel("Attack: ", this.playerAttack, "");
    SimpleBindingIntegerLabel playerDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", this.playerDefence, "");
    SimpleBindingIntegerLabel playerCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ",
        this.playerCritChance, "");
    SimpleBindingIntegerLabel playerCritDamageLabel = new SimpleBindingIntegerLabel("Bonus Crit Damage: ",
        this.playerCritDamage, "");
    SimpleBindingIntegerLabel playerGoldLabel = new SimpleBindingIntegerLabel("Gold: ", this.playerGold, "");

    // create XP Label
    Label playerXpLabel = new Label();
    // copy xp values to ObservableIntegerValue and bind to Label using
    // StringBinding
    this.currentXp = doubleToDoubleProperty(this.player.getXp());
    this.xpForNextLevel = doubleToDoubleProperty(this.player.getXpForNextLevel());
    StringBinding playerXpBinding = new StringBinding() {
      {
        super.bind(currentXp, xpForNextLevel);
      }

      @Override
      protected String computeValue() {
        return "XP: " + currentXp.get() + " / " + xpForNextLevel.get();
      }
    };
    playerXpLabel.textProperty().bind(playerXpBinding);

    playerStats.add(playerStatsTitleLabel, 0, 0);
    playerStats.add(playerAttackLabel, 0, 1);
    playerStats.add(playerDefenceLabel, 0, 2);
    playerStats.add(playerCritChanceLabel, 0, 3);
    playerStats.add(playerCritDamageLabel, 0, 4);
    playerStats.add(playerXpLabel, 0, 5);
    playerStats.add(playerGoldLabel, 0, 6);

    playerItemStats = new GridPane();
    Label itemStatsTitleLabel = new Label("Item Stats:");
    SimpleBindingIntegerLabel playerItemAttackLabel = new SimpleBindingIntegerLabel("(", this.playerItemAttack, ")");
    SimpleBindingIntegerLabel playerItemDefenceLabel = new SimpleBindingIntegerLabel("(", this.playerItemDefence, ")");
    SimpleBindingIntegerLabel playerItemCritChanceLabel = new SimpleBindingIntegerLabel("(", this.playerItemCritChance,
        ")");
    SimpleBindingIntegerLabel playerItemCritDamageLabel = new SimpleBindingIntegerLabel("(", this.playerItemCritDamage,
        ")");

    this.playerItemStats.add(itemStatsTitleLabel, 0, 0);
    this.playerItemStats.add(playerItemAttackLabel, 0, 1);
    this.playerItemStats.add(playerItemDefenceLabel, 0, 2);
    this.playerItemStats.add(playerItemCritChanceLabel, 0, 3);
    this.playerItemStats.add(playerItemCritDamageLabel, 0, 4);
    for (int row = 0; row < 5; row++) {
      GridPane.setHalignment(this.playerItemStats.getChildren().get(row), HPos.CENTER);
    }

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
    this.monsterUI.setStyle(BORDER_STYLE);
    this.monsterUI.setHgap(GRIDPANE_GAPS);
    this.monsterUI.setVgap(GRIDPANE_GAPS);

    // create Label for monstername to be edited when a monster is created
    this.monsterName = new Label();

    // create monster HP bar
    this.monsterHpBar = new Rectangle();
    this.monsterHpBar.setFill(Color.GREEN);
    this.monsterHpBar.setWidth(MON_HP_WIDTH);
    this.monsterHpBar.setHeight(MON_HP_HEIGHT);
    this.monsterHpBar.setArcWidth(HP_CURVE);
    this.monsterHpBar.setArcHeight(HP_CURVE);

    // Create red missing hp
    this.monsterMissingHpBar = new Rectangle();
    this.monsterMissingHpBar.setFill(Color.RED);
    this.monsterMissingHpBar.setWidth(MON_HP_WIDTH);
    this.monsterMissingHpBar.setHeight(MON_HP_HEIGHT);
    this.monsterMissingHpBar.setArcWidth(HP_CURVE);
    this.monsterMissingHpBar.setArcHeight(HP_CURVE);

    // add items to monsterUI
    this.monsterUI.add(monsterName, 0, 0);

    // called to set window to default state
    removeMonster();

    // event UI section--------------------------------------------------------
    this.eventUI = new GridPane();
    this.eventUI.setStyle(BORDER_STYLE);
    this.eventUI.setHgap(GRIDPANE_GAPS);
    this.eventUI.setVgap(GRIDPANE_GAPS);

    // menu UI Section---------------------------------------------------------
    this.menuUI = new GridPane();
    this.menuUI.setStyle(BORDER_STYLE);
    this.menuUI.setHgap(GRIDPANE_GAPS);
    this.menuUI.setVgap(GRIDPANE_GAPS);
    this.menuUI.setPadding(DEFAUL_INSETS);

    Button keyItemsButton = new Button("Key Items");
    Button backpackButton = new Button("Backpack");
    this.menuInfoText = new Label("");
    this.menuInfoText.setStyle(BORDER_STYLE);
    this.menuInfoText.setPrefWidth(MENU_INFO_WIDTH);
    this.menuInfoText.setPrefHeight(MENU_INFO_HEIGHT);
    this.menuInfoText.setPadding(DEFAUL_INSETS);

    this.subMenu = new GridPane();
    // GridPane creation for sub menu
    this.backpackMenu = new GridPane();
    Button equipButton = new Button("Equip");
    equipButton.setOnAction(event -> {
      this.player.equip(activeEquipment);
      newEquip(activeEquipment, activeEquipment.getType());
      removeFromBackpack(activeEquipment);
    });
    backpackMenu.add(equipButton, 0, 0);

    // GridPane creation for key items
    this.backpackButtonBox = new GridPane();
    this.backpackButtonBox.setPrefSize(BACKPACK_WIDTH, BACKPACK_HEIGHT);
    this.backpackButtonBox.setStyle(BORDER_STYLE);
    this.backpackButtonBox.setVgap(GRIDPANE_GAPS);
    this.backpackButtonBox.setHgap(GRIDPANE_GAPS);

    // GridPane creation for key items
    this.keyItemButtonBox = new GridPane();
    this.keyItemButtonBox.setPrefSize(BACKPACK_WIDTH, BACKPACK_HEIGHT);
    this.keyItemButtonBox.setStyle(BORDER_STYLE);
    this.keyItemButtonBox.setVgap(GRIDPANE_GAPS);
    this.keyItemButtonBox.setHgap(GRIDPANE_GAPS);

    // create ScrollPane to ensure scrolling of text in ButtonBox
    this.scrollableBackpack = new ScrollPane(this.backpackButtonBox);
    scrollableBackpack.setFitToWidth(true);
    scrollableBackpack.vvalueProperty().bind(backpackButtonBox.heightProperty());
    scrollableBackpack.setHbarPolicy(ScrollBarPolicy.NEVER);

    // create ScrollPane to ensure scrolling of text in ButtonBox
    this.scrollablekeyItems = new ScrollPane(this.keyItemButtonBox);
    scrollablekeyItems.setFitToWidth(true);
    scrollablekeyItems.vvalueProperty().bind(keyItemButtonBox.heightProperty());
    scrollablekeyItems.setHbarPolicy(ScrollBarPolicy.NEVER);

    this.menuUI.add(backpackButton, 0, 0);
    this.menuUI.add(keyItemsButton, 1, 0);
    this.menuUI.add(menuInfoText, 5, 0);

    GridPane.setColumnSpan(subMenu, 11);
    this.menuUI.add(subMenu, 0, 1);

    backpackButton.setOnAction(event -> {
      this.subMenu.getChildren().clear();
      this.subMenu.add(backpackMenu, 0, 0);
      this.subMenu.add(scrollableBackpack, 0, 1);
    });

    keyItemsButton.setOnAction(event -> {
      this.subMenu.getChildren().clear();
      // this.subMenu.add(keyItemsMenu, 0, 0);
      this.subMenu.add(scrollablekeyItems, 0, 1);
    });

    // add items to window to row a col b
    root.add(areaSelect, 1, 4);
    root.add(exploreButton, 2, 4);
    GridPane.setColumnSpan(scrollPane, 3);
    root.add(scrollPane, 3, 3);
    GridPane.setColumnSpan(playerMissingHpBar, 9);
    GridPane.setRowSpan(playerMissingHpBar, 2);
    root.add(playerMissingHpBar, 0, 0);
    GridPane.setColumnSpan(playerHpBar, 9);
    GridPane.setRowSpan(playerHpBar, 2);
    root.add(playerHpBar, 0, 0);
    root.add(playerLevelLabel, 9, 0);
    root.add(playerHpTextLabel, 9, 1);
    root.add(playerItemHpLabel, 10, 1);

    GridPane.setColumnSpan(playerUI, 3);
    root.add(playerUI, 0, 3);
    GridPane.setColumnSpan(monsterUI, 3);
    root.add(monsterUI, 8, 3);
    GridPane.setColumnSpan(eventUI, 11);
    root.add(eventUI, 0, 10);
    GridPane.setColumnSpan(menuUI, 11);
    root.add(menuUI, 0, 15);

    // set the scene and show the window
    Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
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

  public void print(String string, Color colour, String style) {
    if (style == null) {
      style = "";
    }
    Text text = new Text();
    if (style.contains("continuous")) {
      text.setText(string);
    } else {
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

  public void damageUpdate(String attacker, String defender, int damage, int critSuccess) {

    if (critSuccess == 1) {
      print(attacker + " scored a critical hit!", Color.RED, "");
    }
    if (defender.equals("You")) {
      print(attacker + " dealt " + damage + " damage to " + defender, Color.BLACK, "");
      playerCurrentHpUpdate();
    } else {
      print(attacker + " dealt " + damage + " damage to the " + defender, Color.BLACK, "");
      monHpUpdate(damage);
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

  public void playerCurrentHpUpdate() {
    this.playerCurrentHpVisible.set(player.getCurrentHp());
    double playerHpPercent = (double) this.playerCurrentHpVisible.get() / this.playerMaxHpVisible.get();
    this.playerHpBar.setWidth(PLAYER_HP_WIDTH * playerHpPercent);
  }

  public void levelUp(int level, int newMaxHp) {
    print("Level Up!", Color.GOLDENROD, "bold");
    print("You are now level " + level, Color.BLACK, "");
    this.playerCurrentHpVisible.set(newMaxHp);
    this.playerMaxHpVisible.set(newMaxHp);
    this.playerHpBar.setWidth(PLAYER_HP_WIDTH);
    this.playerLevel.set((this.playerLevel.get() + 1));
    this.playerAttack.set(this.player.getAttack());
    this.playerDefence.set(this.player.getDefence());
    this.playerCritChance.set(this.player.getCritChance());
    this.playerCritDamage.set(this.player.getCritDamage());
    this.xpForNextLevel.set(this.player.getXpForNextLevel());
  }

  public void newEquip(Equipment equipment, String type) {
    // get key details
    String name = equipment.getName();
    HashMap<String, Integer> combatStats = equipment.getCombatStats();
    String statString = statString(combatStats);

    // update correct item type stats
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

  public void equipCheck(Equipment equipment) {
    // output equipment info to TextFlow
    String name = equipment.getName();
    HashMap<String, Integer> combatStats = equipment.getCombatStats();
    String statString = statString(combatStats);
    printSpace();
    print("You have found a " + name, Color.BLACK, "");
    print("Would you like to equip the " + name + "?", Color.BLACK, "");
    print(statString, Color.BLACK, "");
    addEquipChoice(equipment);
  }

  public void ItemStatUpdate() {
    // clear previous itemStat Labels
    // playerItemStats.getChildren().clear();

    // add new labels
    HashMap<String, Integer> itemStats = this.player.getItemStats();
    for (HashMap.Entry<String, Integer> entry : itemStats.entrySet()) {
      String stat = entry.getKey();
      switch (stat) {
        case "HP: ":
          this.playerItemHp.set(entry.getValue());
          break;
        case "Attack: ":
          this.playerItemAttack.set(entry.getValue());
          break;
        case "Defence: ":
          this.playerItemDefence.set(entry.getValue());
          break;
        case "Crit Chance: ":
          this.playerItemCritChance.set(entry.getValue());
          break;
        case "Crit Damage: ":
          this.playerItemCritDamage.set(entry.getValue());
          break;
      }
    }
  }

  public void addToKeyItems(KeyItem keyItem) {
    Button button = new Button(keyItem.getName());
    button.setOnAction(event -> {
      this.activeEquipment = null;
      this.menuInfoText.setText(keyItem.getName());
    });
    int keyItemSize = keyItemButtonBox.getChildren().size();
    int col = keyItemSize / BACKPACK_COLS;
    int row = keyItemSize % BACKPACK_COLS;
    keyItemButtonBox.add(button, row, col);
  }

  public void removeFromKeyItems(KeyItem keyItem) {
    keyItemButtonBox.getChildren().removeIf(node -> ((Button) node).getText().equals(keyItem.getName()));
  }

  public void addToBackpack(Equipment equipment) {
    Button button = new Button(equipment.getName());
    button.setOnAction(event -> {
      this.activeEquipment = equipment;
      this.menuInfoText.setText(statString(equipment.getCombatStats()));
    });
    int backpackSize = backpackButtonBox.getChildren().size();
    int col = backpackSize / BACKPACK_COLS;
    int row = backpackSize % BACKPACK_COLS;
    backpackButtonBox.add(button, row, col);
  }

  public void removeFromBackpack(Equipment equipment) {
    backpackButtonBox.getChildren().removeIf(node -> ((Button) node).getText().equals(equipment.getName()));
  }

  private String statString(HashMap<String, Integer> combatStats) {
    // function to get String of all equipment stats
    String statPrint = "";
    for (HashMap.Entry<String, Integer> entry : combatStats.entrySet()) {
      String stat = entry.getKey();
      Integer value = entry.getValue();
      statPrint = statPrint + stat + value + " ";
    }
    return statPrint;
  }

  public void newMonster(Monster mon) {
    setMonsterStats(mon);

    // create monster HP label
    Label monsterHpTextLabel = new Label();

    // copy hp values to ObservableIntegerValue and bind to Label using
    // StringBinding
    this.monsterCurrentHpVisible = intToIntegerProperty(mon.getCurrentHp());
    this.monsterMaxHpVisible = intToIntegerProperty(mon.getMaxHp());
    StringBinding monsterHpBinding = new StringBinding() {
      {
        super.bind(monsterCurrentHpVisible, monsterMaxHpVisible);
      }

      @Override
      protected String computeValue() {
        return monsterCurrentHpVisible.get() + " / " + monsterMaxHpVisible.get();
      }
    };
    monsterHpTextLabel.textProperty().bind(monsterHpBinding);

    SimpleBindingIntegerLabel monsterAttackLabel = new SimpleBindingIntegerLabel("Attack: ", monsterAttack, "");
    SimpleBindingIntegerLabel monsterDefenceLabel = new SimpleBindingIntegerLabel("Defence: ", monsterDefence, "");
    SimpleBindingIntegerLabel monsterCritChanceLabel = new SimpleBindingIntegerLabel("Crit Chance: ", monsterCritChance,
        "");
    SimpleBindingIntegerLabel monsterCritDamageLabel = new SimpleBindingIntegerLabel("Bonus Crit Damage: ",
        monsterCritDamage, "");
    SimpleBindingIntegerLabel monsterGoldLabel = new SimpleBindingIntegerLabel("Gold: ", monsterGold, "");
    SimpleBindingDoubleLabel monsterXpLabel = new SimpleBindingDoubleLabel("XP: ", monsterXp, "");

    GridPane monsterStats = new GridPane();

    this.monsterUI.add(monsterHpTextLabel, 1, 1);
    this.monsterUI.add(this.monsterMissingHpBar, 0, 1);
    this.monsterUI.add(this.monsterHpBar, 0, 1);
    this.monsterHpBar.setWidth(MON_HP_WIDTH);
    monsterStats.add(monsterAttackLabel, 0, 0);
    monsterStats.add(monsterDefenceLabel, 0, 1);
    monsterStats.add(monsterCritChanceLabel, 0, 2);
    monsterStats.add(monsterCritDamageLabel, 0, 3);
    monsterStats.add(monsterGoldLabel, 0, 4);
    monsterStats.add(monsterXpLabel, 0, 5);

    this.monsterUI.add(monsterStats, 0, 2);
  }

  public void removeMonster() {
    this.currentXp.set(player.getXp());
    this.playerGold.set(player.getGold());
    monsterName.textProperty().bind(stringToStringProperty("Monster: "));
    monsterUI.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 1);
    monsterUI.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 1);
    monsterUI.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 2);
  }

  private void setMonsterStats(Monster mon) {
    // display name
    // normal label instead of a SimpleBindingStringLabel so it can be bound to
    // blank "Monster: " text when monster removed.
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
    this.monsterXp = doubleToDoubleProperty(mon.getXp());
  }

  private void monHpUpdate(int damage) {
    this.monsterCurrentHpVisible.set(Math.max(0, (this.monsterCurrentHpVisible.get() - damage)));
    double monsterHpPercent = (double) this.monsterCurrentHpVisible.get() / this.monsterMaxHpVisible.get();
    this.monsterHpBar.setWidth(MON_HP_WIDTH * monsterHpPercent);
  }

  public void removeEvent() {
    for (int i = 0; i < 5; i++) {
      final int column = i;
      eventUI.getChildren()
          .removeIf(node -> GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == 0);
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

  public void addEquipChoice(Equipment equipment) {
    removeEvent();
    Button yesButton = new Button("Yes");
    yesButton.setOnAction(event -> {
      player.onEquipBoolean(true, equipment);
    });
    Button noButton = new Button("No");
    noButton.setOnAction(event -> {
      player.onEquipBoolean(false, equipment);
    });
    eventUI.add(yesButton, 0, 0);
    eventUI.add(noButton, 1, 0);

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

  private DoubleProperty doubleToDoubleProperty(double value) {
    DoubleProperty observableValue = new SimpleDoubleProperty(value);
    return observableValue;
  }

}
