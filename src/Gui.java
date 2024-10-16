
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
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

public class Gui {
  // constants
  private static final int WINDOW_WIDTH = 1400;
  private static final int WINDOW_HEIGHT = 900;
  private static final int MAIN_TEXT_BOX_WIDTH = 600;
  private static final int MAIN_TEXT_BOX_HEIGHT = 400;
  private static final int PLAYER_HP_WIDTH = 1200;
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
  private static final int RUN_STATISTICS_START = 11;
  private static final Color recordColor = Color.GOLDENROD;

  // core variables
  private static GameLogic game1;
  private Player player;
  private Stage stage;
  private GridPane root = new GridPane();

  // variables used to manage areaSelect ComboBox
  private ArrayList<String> discoveredAreasArrayList;
  private ObservableList<String> discoveredAreas;
  // variable for text output
  private TextFlow textBox = new TextFlow();
  ScrollPane textScrollPane = new ScrollPane(textBox);
  // variables for area select and explore
  private Button exploreButton = new Button("Explore");
  private ComboBox<String> areaSelect = new ComboBox<String>();

  // player Ui variables
  private GridPane playerUi = new GridPane();
  // variables for managing player stats
  private GridPane playerStats;
  private IntegerProperty playerAttack;
  private IntegerProperty playerDefence;
  private IntegerProperty playerCritChance;
  private IntegerProperty playerCritDamage;
  private IntegerProperty playerGold;
  // variables for managing XP
  private DoubleProperty currentXp;
  private DoubleProperty xpForNextLevel;
  // variables for managing player HP bar and level
  private IntegerProperty playerLevel = intToIntegerProperty(0);
  private Rectangle playerHpBar = new Rectangle();
  private Rectangle playerMissingHpBar = new Rectangle();
  private IntegerProperty playerCurrentHpVisible;
  private IntegerProperty playerMaxHpVisible;
  // equipment variables
  private GridPane playerEquipment;
  private StringProperty playerWeapon;
  private StringProperty playerShield;
  private StringProperty playerArmour;
  private StringProperty playerBoots;
  private StringProperty playerHelmet;
  private StringProperty playerRing;
  private GridPane playerItemStats;
  private IntegerProperty playerItemHp;
  private IntegerProperty playerItemAttack;
  private IntegerProperty playerItemDefence;
  private IntegerProperty playerItemCritChance;
  private IntegerProperty playerItemCritDamage;
  // Labels to show player HP and level
  private SimpleBindingIntegerLabel playerLevelLabel;
  private Label playerHpTextLabel = new Label();
  private SimpleBindingIntegerLabel playerItemHpLabel;
  // monster Ui variables
  private GridPane monsterUi = new GridPane();
  private Label monsterName;
  private Rectangle monsterHpBar = new Rectangle();
  private Rectangle monsterMissingHpBar = new Rectangle();
  private IntegerProperty monsterCurrentHpVisible;
  private IntegerProperty monsterMaxHpVisible;
  private IntegerProperty monsterAttack;
  private IntegerProperty monsterDefence;
  private IntegerProperty monsterCritChance;
  private IntegerProperty monsterCritDamage;
  private IntegerProperty monsterGold;
  private DoubleProperty monsterXp;
  // event Ui variables
  private GridPane eventUi = new GridPane();
  // menu Ui variables
  private GridPane menuUi = new GridPane();
  private Label menuInfoText;
  private GridPane subMenu = new GridPane();
  // equipment menu
  private GridPane backpackMenu = new GridPane();
  private GridPane backpackButtonBox = new GridPane();
  private ScrollPane scrollableBackpack = new ScrollPane(backpackButtonBox);
  // key item menu
  private Equipment activeEquipment;
  private GridPane keyItemButtonBox = new GridPane();
  private ScrollPane scrollablekeyItems = new ScrollPane(keyItemButtonBox);
  // statistics menu
  private GridPane statisticsPane = new GridPane();
  private GridPane statisticsMenu = new GridPane();
  private GridPane areaStatisticsPane = new GridPane();
  private GridPane runStatisticsPane = new GridPane();
  private ScrollPane scrollableAreaStats = new ScrollPane(areaStatisticsPane);
  private ScrollPane scrollableRunStats = new ScrollPane(runStatisticsPane);
  // variables used for Run Statistics menu
  private RunStatList runStats;
  private IntegerProperty exploreCount;
  private IntegerProperty areaCount;
  private IntegerProperty eventCount;
  private IntegerProperty monFindCount;
  private IntegerProperty monKillCount;
  private IntegerProperty uniqueMonCount;
  private IntegerProperty keyCount;
  private IntegerProperty equipCount;
  private GridPane statScreen = new GridPane();

  public Gui(Stage primaryStage) {
    this.stage = primaryStage;

    createVariables();

    // main layout manager
    root.setHgap(GRIDPANE_GAPS);
    root.setVgap(GRIDPANE_GAPS);
    root.setPadding(DEFAUL_INSETS);

    // Text box set up
    setTextBox();

    // sort several main UI elements
    setScrollPane(textScrollPane, textBox);

    // Monster Ui section-----------------------------------------------------
    createMonsterGridPane();

    // event Ui section--------------------------------------------------------
    this.eventUi = new GridPane();
    this.eventUi.setStyle(BORDER_STYLE);
    this.eventUi.setHgap(GRIDPANE_GAPS);
    this.eventUi.setVgap(GRIDPANE_GAPS);

    // menu Ui Section---------------------------------------------------------
    setGridPane(menuUi);
    createMenuUi();

    // set the scene and show the window
    Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
    primaryStage.setTitle("TextGame");
    primaryStage.setScene(scene);
    primaryStage.show();

    startText();
  }

  public void addAllToWindow() {
    root.getChildren().clear();
    // add items to window to row a col b
    root.add(areaSelect, 1, 4);
    root.add(exploreButton, 2, 4);
    GridPane.setColumnSpan(textScrollPane, 3);
    root.add(textScrollPane, 3, 3);
    GridPane.setColumnSpan(playerMissingHpBar, 9);
    GridPane.setRowSpan(playerMissingHpBar, 2);
    root.add(playerMissingHpBar, 0, 0);
    GridPane.setColumnSpan(playerHpBar, 9);
    GridPane.setRowSpan(playerHpBar, 2);
    root.add(playerHpBar, 0, 0);
    root.add(playerLevelLabel, 9, 0);
    root.add(playerHpTextLabel, 9, 1);
    root.add(playerItemHpLabel, 10, 1);

    GridPane.setColumnSpan(playerUi, 3);
    root.add(playerUi, 0, 3);
    GridPane.setColumnSpan(monsterUi, 3);
    root.add(monsterUi, 8, 3);
    GridPane.setColumnSpan(eventUi, 11);
    root.add(eventUi, 0, 10);
    GridPane.setColumnSpan(menuUi, 11);
    root.add(menuUi, 0, 15);
  }

  public static void setGameLogic(GameLogic game) {
    game1 = game;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public void setPlayerGui() {
    getPlayerStats();

    // create list for areaSelect combobox
    discoveredAreasArrayList = (player.getDiscoveredAreaNames());
    discoveredAreas = FXCollections.observableArrayList(discoveredAreasArrayList);
    areaSelect.setItems(discoveredAreas);

    // add areas to the statistics page
    setAreaStatsTitle();
    setStartingAreaStats();

    setExploreAndAreaSelect();
    setHpBarAndLevel();

    // player Ui section------------------------------------------------------
    // Create gridpane to contain player information
    setGridPane(playerUi);
    createPlayerGridPane();

    // called to set window to default state
    removeMonster();
  }

  public void playerGoldUpdate() {
    this.playerGold.set(this.player.getGold());
  }

  public void setAreaCount(int areaCount) {
    this.areaCount.set(areaCount);
  }

  public void setEquipCount(int equipCount) {
    this.equipCount.set(equipCount);
  }

  public void setEventCount(int eventCount) {
    this.eventCount.set(eventCount);
  }

  public void setKeyCount(int keyCount) {
    this.keyCount.set(keyCount);
  }

  public void setMonKillCount(int monKillCount) {
    this.monKillCount.set(monKillCount);
  }

  public void setUniqueMonCount(int uniqueMonCount) {
    this.uniqueMonCount.set(uniqueMonCount);
  }

  public void setMonFindCount(int monFindCount) {
    this.monFindCount.set(monFindCount);
  }

  public void setExploreCount(int exploreCount) {
    this.exploreCount.set(exploreCount);
  }

  public void print(String string, Color colour, String style) {
    // method used for all printing to the UI
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

  public void discoverArea(Area area, String areaEncounterText) {
    String areaName = area.getName();
    discoveredAreas.add(areaName);
    printSpace();
    print(areaEncounterText, Color.BLACK, "italic");
    print("You have discovered the " + areaName + ".", Color.BLACK, "bold");
    // -1 used as impossible index to be overwritten
    createAreaStatLabel(area, -1);
  }

  public void updateArea(String areaName) {
    // TODO: use Observable variables and a StringBinding to do this more
    // efficiently
    // get which row in areaStatistics to update
    int row = -1;
    for (var label : areaStatisticsPane.getChildren()) {
      if (((Label) label).getText().equals(areaName)) {
        row = GridPane.getRowIndex(label);
      }
    }
    // change the label to update area discovery
    for (var label : areaStatisticsPane.getChildren()) {
      if (GridPane.getColumnIndex(label) == 1 && GridPane.getRowIndex(label) == row) {
        String text = ((Label) label).getText();
        String newText = incrementDiscoveries(text);
        ((Label) label).setText(newText);
      }
    }
  }

  public void updateAreaMonsters(String areaName) {
    // get which row in areaStatistics to update
    int row = -1;
    for (var label : areaStatisticsPane.getChildren()) {
      if (((Label) label).getText().equals(areaName)) {
        row = GridPane.getRowIndex(label);
      }
    }
    // change the label to update monster discovery
    for (var label : areaStatisticsPane.getChildren()) {
      if (GridPane.getColumnIndex(label) == 2 && GridPane.getRowIndex(label) == row) {
        String text = ((Label) label).getText();
        String newText = incrementDiscoveries(text);
        ((Label) label).setText(newText);
      }
    }
  }

  public void damageUpdate(String attacker, String defender, int damage, int critSuccess) {

    if (critSuccess == 1) {
      print(attacker + " scored a critical hit!", Color.RED, null);
    }
    if (defender.equals("You")) {
      print(attacker + " dealt " + damage + " damage to " + defender, Color.BLACK, null);
      playerCurrentHpUpdate();
    } else {
      print(attacker + " dealt " + damage + " damage to the " + defender, Color.BLACK, null);
      monHpUpdate(damage);
    }
  }

  public void playerCurrentHpUpdate() {
    this.playerCurrentHpVisible.set(player.getCurrentHp());
    double playerHpPercent = (double) this.playerCurrentHpVisible.get() / this.playerMaxHpVisible.get();
    this.playerHpBar.setWidth(PLAYER_HP_WIDTH * playerHpPercent);
  }

  public void levelUp(int level, int newMaxHp) {
    if (this.playerCurrentHpVisible == null) {
      setHpBarAndLevel();
      getPlayerStats();
      return;
    }
    print("Level Up!", Color.GOLDENROD, "bold");
    print("You are now level " + level, Color.BLACK, "bold");
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
    print("You have found a " + name + ", you place it in your backpack.", Color.BLACK, "bold");
    print("Would you like to equip the " + name + "?", Color.BLACK, null);
    print(statString, Color.BLACK, null);
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

    this.monsterUi.add(monsterHpTextLabel, 1, 1);
    this.monsterUi.add(this.monsterMissingHpBar, 0, 1);
    this.monsterUi.add(this.monsterHpBar, 0, 1);
    this.monsterHpBar.setWidth(MON_HP_WIDTH);
    monsterStats.add(monsterAttackLabel, 0, 0);
    monsterStats.add(monsterDefenceLabel, 0, 1);
    monsterStats.add(monsterCritChanceLabel, 0, 2);
    monsterStats.add(monsterCritDamageLabel, 0, 3);
    monsterStats.add(monsterGoldLabel, 0, 4);
    monsterStats.add(monsterXpLabel, 0, 5);

    this.monsterUi.add(monsterStats, 0, 2);
  }

  public void removeMonster() {
    this.currentXp.set(player.getXp());
    this.playerGold.set(player.getGold());
    monsterName.textProperty().bind(stringToStringProperty("Monster: "));
    monsterUi.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 1);
    monsterUi.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == 1 && GridPane.getRowIndex(node) == 1);
    monsterUi.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == 0 && GridPane.getRowIndex(node) == 2);
  }

  public void removeEvent() {
    for (int i = 0; i < 5; i++) {
      final int column = i;
      eventUi.getChildren()
          .removeIf(node -> GridPane.getColumnIndex(node) == column && GridPane.getRowIndex(node) == 0);
    }
    game1.setEvent(false);
  }

  public void newEventOption(int i, EventOption option) {
    // function to create a button and bind to the correct event in GameLogic
    Button optionButton = new Button((String) (option.getButtonText()));
    optionButton.setOnAction(event -> {
      game1.onOptionButton(option);
    });
    eventUi.add(optionButton, i, 0);
  }

  public void addEquipChoice(Equipment equipment) {
    removeEvent();
    game1.setEvent(true);
    Button yesButton = new Button("Yes");
    yesButton.setOnAction(event -> {
      player.onEquipBoolean(true, equipment);
      menuInfoText.setText("");
      activeEquipment = null;
    });
    Button noButton = new Button("No");
    noButton.setOnAction(event -> {
      player.onEquipBoolean(false, equipment);
    });
    eventUi.add(yesButton, 0, 0);
    eventUi.add(noButton, 1, 0);
  }

  public void death(HashMap<String, Integer> record) {
    printSpace();
    print("You have died.", Color.BLACK, "bold");
    Button statScreenButton = new Button("Run Summary");
    statScreenButton.setOnAction(event -> {
      statScreen(record);
    });
    ObservableList<Node> elements = root.getChildren();
    elements.remove(exploreButton);
    elements.remove(areaSelect);
    backpackMenu.getChildren().clear();
    root.add(statScreenButton, 2, 4);
  }

  private void statScreen(HashMap<String, Integer> record) {
    root.getChildren().clear();
    setGridPane(statScreen);
    statScreen.setPadding(DEFAUL_INSETS);
    createStatScreenText();
    createStatsForRecords(record);
    createBackAndMenuButtons();

    root.add(statScreen, 0, 0);

    ObservableList<Node> labels = statScreen.getChildren();
    // runStatisticsStart is 11 as there are 11 labels to set up the statsScreen
    // (0-10)
    int runStatisticsEnd = labels.size() - 1;

    for (int i = RUN_STATISTICS_START; i <= runStatisticsEnd; i++) {
      GridPane.setHalignment(labels.get(i), HPos.CENTER);
    }
  }

  private void createStatScreenText() {
    statScreen.add(new Label("This Run"), 1, 0);
    statScreen.add(new Label("Previous Record"), 2, 0);

    statScreen.add(new Label("Level:"), 0, 1);
    statScreen.add(new Label("Times Explored:"), 0, 2);
    statScreen.add(new Label("Areas Found:"), 0, 3);
    statScreen.add(new Label("Events Found:"), 0, 4);
    statScreen.add(new Label("Total Monsters Found:"), 0, 5);
    statScreen.add(new Label("Total Monsters Killed:"), 0, 6);
    statScreen.add(new Label("Unique Monsters Killed:"), 0, 7);
    statScreen.add(new Label("Key Items Found:"), 0, 8);
    statScreen.add(new Label("Equipment Found:"), 0, 9);
  }

  private void createStatsForRecords(HashMap<String, Integer> record) {
    // create labels for this run stats
    int level = this.playerLevel.get();
    int explore = this.exploreCount.get();
    int area = this.areaCount.get();
    int event = this.eventCount.get();
    int monFind = this.monFindCount.get();
    int monKill = this.monKillCount.get();
    int uniqueMon = this.uniqueMonCount.get();
    int key = this.keyCount.get();
    int equip = this.equipCount.get();

    Label thisRunLevelLabel = new Label(String.valueOf(level));
    Label thisRunExploreLabel = new Label(String.valueOf(explore));
    Label thisRunAreaLabel = new Label(String.valueOf(area));
    Label thisRunEventLabel = new Label(String.valueOf(event));
    Label thisRunMonFindLabel = new Label(String.valueOf(monFind));
    Label thisRunMonKillLabel = new Label(String.valueOf(monKill));
    Label thisRunUniqueMonLabel = new Label(String.valueOf(uniqueMon));
    Label thisRunKeyLabel = new Label(String.valueOf(key));
    Label thisRunEquipLabel = new Label(String.valueOf(equip));

    statScreen.add(thisRunLevelLabel, 1, 1);
    statScreen.add(thisRunExploreLabel, 1, 2);
    statScreen.add(thisRunAreaLabel, 1, 3);
    statScreen.add(thisRunEventLabel, 1, 4);
    statScreen.add(thisRunMonFindLabel, 1, 5);
    statScreen.add(thisRunMonKillLabel, 1, 6);
    statScreen.add(thisRunUniqueMonLabel, 1, 7);
    statScreen.add(thisRunKeyLabel, 1, 8);
    statScreen.add(thisRunEquipLabel, 1, 9);
    // create labels for record stats
    int levelRecord = record.getOrDefault("level", 0);
    int exploreRecord = record.getOrDefault("explore", 0);
    int areaRecord = record.getOrDefault("area", 0);
    int eventsRecord = record.getOrDefault("event", 0);
    int monFindRecord = record.getOrDefault("monFind", 0);
    int monKillRecord = record.getOrDefault("monKill", 0);
    int uniqueMonKillRecord = record.getOrDefault("uniqueMonKill", 0);
    int keyRecord = record.getOrDefault("key", 0);
    int equipRecord = record.getOrDefault("equip", 0);
    RecordLabel levelRecordLabel = new RecordLabel(intToIntegerProperty(levelRecord));
    RecordLabel exploreRecordLabel = new RecordLabel(intToIntegerProperty(exploreRecord));
    RecordLabel areaRecordLabel = new RecordLabel(intToIntegerProperty(areaRecord));
    RecordLabel eventsRecordLabel = new RecordLabel(intToIntegerProperty(eventsRecord));
    RecordLabel monFindRecordLabel = new RecordLabel(intToIntegerProperty(monFindRecord));
    RecordLabel monKillRecordLabel = new RecordLabel(intToIntegerProperty(monKillRecord));
    RecordLabel uniqueMonKillRecordLabel = new RecordLabel(intToIntegerProperty(uniqueMonKillRecord));
    RecordLabel keyRecordLabel = new RecordLabel(intToIntegerProperty(keyRecord));
    RecordLabel equipRecordLabel = new RecordLabel(intToIntegerProperty(equipRecord));
    // add labels
    statScreen.add(levelRecordLabel, 2, 1);
    statScreen.add(exploreRecordLabel, 2, 2);
    statScreen.add(areaRecordLabel, 2, 3);
    statScreen.add(eventsRecordLabel, 2, 4);
    statScreen.add(monFindRecordLabel, 2, 5);
    statScreen.add(monKillRecordLabel, 2, 6);
    statScreen.add(uniqueMonKillRecordLabel, 2, 7);
    statScreen.add(keyRecordLabel, 2, 8);
    statScreen.add(equipRecordLabel, 2, 9);
    // add colour to thisRun and save if this run > record
    if (level >= levelRecord && level != 0) {
      game1.saveRecord("level", level);
      thisRunLevelLabel.setTextFill(recordColor);
    }
    if (explore >= exploreRecord && explore != 0) {
      game1.saveRecord("explore", explore);
      thisRunExploreLabel.setTextFill(recordColor);
    }
    if (area >= areaRecord && area != 0) {
      game1.saveRecord("area", area);
      thisRunAreaLabel.setTextFill(recordColor);
    }
    if (event >= eventsRecord && event != 0) {
      game1.saveRecord("event", event);
      thisRunEventLabel.setTextFill(recordColor);
    }
    if (monFind > monFindRecord && monFind != 0) {
      game1.saveRecord("monFind", monFind);
      thisRunMonFindLabel.setTextFill(recordColor);
    }
    if (monKill >= monKillRecord && monKill != 0) {
      game1.saveRecord("monKill", monKill);
      thisRunMonKillLabel.setTextFill(recordColor);
    }
    if (uniqueMon >= uniqueMonKillRecord && uniqueMon != 0) {
      game1.saveRecord("uniqueMonKill", uniqueMon);
      thisRunUniqueMonLabel.setTextFill(recordColor);
    }
    if (key >= keyRecord && key != 0) {
      game1.saveRecord("key", key);
      thisRunKeyLabel.setTextFill(recordColor);
    }
    if (equip >= equipRecord && equip != 0) {
      game1.saveRecord("equip", equip);
      thisRunEquipLabel.setTextFill(recordColor);
    }
    // add colour to record if thisRun < record
    if (level <= levelRecord) {
      levelRecordLabel.setTextFill(recordColor);
    }
    if (explore <= exploreRecord) {
      exploreRecordLabel.setTextFill(recordColor);
    }
    if (area <= areaRecord) {
      areaRecordLabel.setTextFill(recordColor);
    }
    if (event <= eventsRecord) {
      eventsRecordLabel.setTextFill(recordColor);
    }
    if (monFind <= monFindRecord) {
      monFindRecordLabel.setTextFill(recordColor);
    }
    if (monKill <= monKillRecord) {
      monKillRecordLabel.setTextFill(recordColor);
    }
    if (uniqueMon <= uniqueMonKillRecord) {
      uniqueMonKillRecordLabel.setTextFill(recordColor);
    }
    if (key <= keyRecord) {
      keyRecordLabel.setTextFill(recordColor);
    }
    if (equip <= equipRecord) {
      equipRecordLabel.setTextFill(recordColor);
    }
  }

  private void createBackAndMenuButtons() {
    Button backButton = new Button("Main Menu");
    backButton.setOnAction(event -> {
      returnToMainMenu();
    });
    Button retry = new Button("Retry");
    retry.setOnAction(event -> {
      root.getChildren().clear();
      resetGui();
      game1.restartGame();
    });

    statScreen.add(retry, 0, 10);
    statScreen.add(backButton, 1, 10);
  }

  private void resetGui() {
    removeEvent();
    removeMonster();
    this.discoveredAreas.removeAll();
    this.areaStatisticsPane.getChildren().clear();
    this.backpackButtonBox.getChildren().clear();
    this.playerStats.getChildren().clear();
    this.playerEquipment.getChildren().clear();
    this.playerItemStats.getChildren().clear();
    this.keyItemButtonBox.getChildren().clear();
    this.textBox.getChildren().clear();
    this.runStatisticsPane.getChildren().clear();
    this.statScreen.getChildren().clear();
    createVariables();

  }

  private void createVariables() {
    this.discoveredAreasArrayList = new ArrayList<String>();
    this.playerEquipment = new GridPane();
    this.playerWeapon = stringToStringProperty("");
    this.playerShield = stringToStringProperty("");
    this.playerArmour = stringToStringProperty("");
    this.playerBoots = stringToStringProperty("");
    this.playerHelmet = stringToStringProperty("");
    this.playerRing = stringToStringProperty("");
    this.playerItemStats = new GridPane();
    this.playerItemHp = intToIntegerProperty(0);
    this.playerItemAttack = intToIntegerProperty(0);
    this.playerItemDefence = intToIntegerProperty(0);
    this.playerItemCritChance = intToIntegerProperty(0);
    this.playerItemCritDamage = intToIntegerProperty(0);
    this.playerLevelLabel = new SimpleBindingIntegerLabel("Level: ", this.playerLevel, "");
    this.playerItemHpLabel = new SimpleBindingIntegerLabel("(", this.playerItemHp, ")");
  }

  private void returnToMainMenu() {
    root.getChildren().clear();
    MenuGui menuGui = new MenuGui(stage, game1.getDatabaseManager());
  }

  private void getPlayer(GameLogic game) {
    this.player = game.getPlayer();
  }

  private void setAllMenuPanes() {
    // set GridPane settings
    setGridPane(this.backpackButtonBox);
    setSubMenuSize(this.backpackButtonBox);
    setGridPane(this.keyItemButtonBox);
    setSubMenuSize(this.keyItemButtonBox);
    setGridPane(this.statisticsPane);
    setSubMenuSize(this.statisticsPane);
    setGridPane(this.areaStatisticsPane);
    setSubMenuSize(this.areaStatisticsPane);
    setGridPane(this.runStatisticsPane);
    setSubMenuSize(this.runStatisticsPane);

    // set ScrollPane settings
    setScrollPane(this.scrollableBackpack, this.backpackButtonBox);
    setScrollPane(this.scrollablekeyItems, this.keyItemButtonBox);
    setScrollPane(this.scrollableAreaStats, this.areaStatisticsPane);
    setScrollPane(this.scrollableRunStats, this.runStatisticsPane);
  }

  private void setGridPane(GridPane gridpane) {
    gridpane.setStyle(BORDER_STYLE);
    gridpane.setVgap(GRIDPANE_GAPS);
    gridpane.setHgap(GRIDPANE_GAPS);
  }

  private void setSubMenuSize(GridPane gridpane) {
    gridpane.setPrefSize(BACKPACK_WIDTH, BACKPACK_HEIGHT);
  }

  private void setTextBox() {
    textBox.setPrefSize(MAIN_TEXT_BOX_WIDTH, MAIN_TEXT_BOX_HEIGHT);
    textBox.setStyle(BORDER_STYLE);
  }

  private void setScrollPane(ScrollPane scrollpane, GridPane gridpane) {
    scrollpane.setFitToWidth(true);
    scrollpane.vvalueProperty().bind(gridpane.heightProperty());
    scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
  }

  private void setScrollPane(ScrollPane scrollpane, TextFlow textBox) {
    scrollpane.setFitToWidth(true);
    scrollpane.vvalueProperty().bind(textBox.heightProperty());
    scrollpane.setHbarPolicy(ScrollBarPolicy.NEVER);
  }

  private void setHpBar(Rectangle rectangle, int height, int width, Color color) {
    rectangle.setFill(color);
    rectangle.setWidth(width);
    rectangle.setHeight(height);
    rectangle.setArcWidth(HP_CURVE);
    rectangle.setArcHeight(HP_CURVE);
  }

  private void setSubMenu(GridPane topMenu, ScrollPane scrollpane) {
    this.subMenu.getChildren().clear();
    if (topMenu != null) {
      this.subMenu.add(topMenu, 0, 0);
    }
    this.subMenu.add(scrollpane, 0, 1);
  }

  private void createMenuInfoTextLabel() {
    this.menuInfoText = new Label("");
    this.menuInfoText.setStyle(BORDER_STYLE);
    this.menuInfoText.setPrefWidth(MENU_INFO_WIDTH);
    this.menuInfoText.setPrefHeight(MENU_INFO_HEIGHT);
    this.menuInfoText.setPadding(DEFAUL_INSETS);
  }

  private void createPlayerGridPane() {
    this.playerStats = new GridPane();
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
        // code to avoid scientific notation:
        DecimalFormat format1 = new DecimalFormat("0");
        String currentXpOutput = format1.format(currentXp.get());
        String nextXpOutput = format1.format(xpForNextLevel.get());
        return "XP: " + currentXpOutput + " / " + nextXpOutput;
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

    this.playerUi.add(playerStats, 0, 0);
    this.playerUi.add(playerEquipment, 0, 1);
    this.playerUi.add(playerItemStats, 1, 0);
  }

  private void setExploreAndAreaSelect() {
    // bind the button to the explore event
    exploreButton.setOnAction(event -> {
      game1.onExploreButton();
    });

    // choose initial value for the ComboBox
    this.areaSelect.getSelectionModel().selectFirst();

    // bind the ComboBox to choose areas in GameLogic
    this.areaSelect.setOnAction(event -> {
      // pass index of chosen area from discoveredAreas
      String selectedArea = (String) areaSelect.getValue();
      int index = discoveredAreas.indexOf(selectedArea);
      game1.onAreaSelect(index);
    });
  }

  private void setAreaStatsTitle() {
    Label areaStatsTitleLabel1 = new Label("Area");
    Label areaStatsTitleLabel2 = new Label("Areas Discovered");
    Label areaStatsTitleLabel3 = new Label("Monsters Killed");
    areaStatisticsPane.add(areaStatsTitleLabel1, 0, 0);
    areaStatisticsPane.add(areaStatsTitleLabel2, 1, 0);
    areaStatisticsPane.add(areaStatsTitleLabel3, 2, 0);
    GridPane.setHalignment(areaStatsTitleLabel1, HPos.CENTER);
    GridPane.setHalignment(areaStatsTitleLabel2, HPos.CENTER);
    GridPane.setHalignment(areaStatsTitleLabel3, HPos.CENTER);

    areaStatisticsPane.setPadding(DEFAUL_INSETS);
  }

  private void setStartingAreaStats() {
    ArrayList<Area> startingAreas = player.getDiscoveredAreas();
    int i = 1;
    for (Area area : startingAreas) {
      createAreaStatLabel(area, i);
      i += 1;
    }
  }

  private void createAreaStatLabel(Area area, int row) {
    // current row value is only used in setStartingAreaStats
    Label areaLabel = new Label(area.getName());
    Label areasDiscovered = new Label(area.getNumberAreasDiscovered());
    int totalMonsters = area.getMonsterList().size();
    int monstersKilled = player.monsterKillCheck(area);
    Label monstersDiscovered = new Label(monstersKilled + "/" + totalMonsters);
    // -1 used as impossible index to be overwritten in all other cases
    if (row == -1) {
      row = discoveredAreas.size() + 1;
    }
    areaStatisticsPane.add(areaLabel, 0, row);
    areaStatisticsPane.add(areasDiscovered, 1, row);
    areaStatisticsPane.add(monstersDiscovered, 2, row);

    GridPane.setHalignment(areasDiscovered, HPos.CENTER);
    GridPane.setHalignment(monstersDiscovered, HPos.CENTER);

  }

  private void setHpBarAndLevel() {
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
    setHpBar(playerHpBar, PLAYER_HP_HEIGHT, PLAYER_HP_WIDTH, Color.GREEN);

    // Create red missing hp
    setHpBar(playerMissingHpBar, PLAYER_HP_HEIGHT, PLAYER_HP_WIDTH, Color.RED);
  }

  private void createMonsterGridPane() {
    // Create gridpane to contain monster information
    setGridPane(monsterUi);

    // create Label for monstername to be edited when a monster is created
    this.monsterName = new Label();

    // create monster HP bar
    setHpBar(monsterHpBar, MON_HP_HEIGHT, MON_HP_WIDTH, Color.GREEN);
    // Create red missing hp
    setHpBar(monsterMissingHpBar, MON_HP_HEIGHT, MON_HP_WIDTH, Color.RED);

    // add items to monsterUi
    this.monsterUi.add(monsterName, 0, 0);
  }

  private void createMenuUi() {
    this.menuUi.setPadding(DEFAUL_INSETS);

    Button keyItemsButton = new Button("Key Items");
    Button backpackButton = new Button("Backpack");
    Button statisticsButton = new Button("Statistics");

    createMenuInfoTextLabel();

    this.subMenu = new GridPane();
    // GridPane creation for backpack sub menu
    this.backpackMenu = new GridPane();
    Button equipButton = new Button("Equip");
    equipButton.setOnAction(event -> {
      if (activeEquipment != null) {
        this.player.equip(activeEquipment);
        newEquip(activeEquipment, activeEquipment.getType());
        removeFromBackpack(activeEquipment);
        menuInfoText.setText("");
      }
    });
    backpackMenu.add(equipButton, 0, 0);

    // Gridpane creation for statistics sub menu
    Button areaStatsButton = new Button("Areas");
    areaStatsButton.setOnAction(event -> {
      this.subMenu.getChildren().clear();
      this.subMenu.add(statisticsMenu, 0, 0);
      this.subMenu.add(scrollableAreaStats, 0, 1);
    });
    Button runStatsButton = new Button("This Run");
    runStatsButton.setOnAction(event -> {
      this.subMenu.getChildren().clear();
      this.subMenu.add(statisticsMenu, 0, 0);
      this.subMenu.add(scrollableRunStats, 0, 1);
    });

    statisticsMenu.add(areaStatsButton, 0, 0);
    statisticsMenu.add(runStatsButton, 1, 0);

    setAllMenuPanes();

    this.menuUi.add(backpackButton, 0, 0);
    this.menuUi.add(keyItemsButton, 1, 0);
    this.menuUi.add(statisticsButton, 2, 0);
    this.menuUi.add(menuInfoText, 5, 0);

    GridPane.setColumnSpan(subMenu, 11);
    this.menuUi.add(subMenu, 0, 1);

    backpackButton.setOnAction(event -> {
      setSubMenu(backpackMenu, scrollableBackpack);
    });

    keyItemsButton.setOnAction(event -> {
      GridPane keyItemsMenu = null;
      setSubMenu(keyItemsMenu, scrollablekeyItems);
    });

    statisticsButton.setOnAction(event -> {
      setSubMenu(statisticsMenu, scrollableAreaStats);
    });
  }

  public void createScrollableRunStats() {
    this.runStats = player.getRunStats();
    this.exploreCount = intToIntegerProperty(runStats.getStatTimesExplored());
    this.areaCount = intToIntegerProperty(runStats.getStatAreasDiscovered());
    this.eventCount = intToIntegerProperty(runStats.getStatEventsDiscovered());
    this.monFindCount = intToIntegerProperty(runStats.getStatMonstersFound());
    this.monKillCount = intToIntegerProperty(runStats.getStatMonstersKilled());
    this.uniqueMonCount = intToIntegerProperty(runStats.getStatDifferentMonsters());
    this.keyCount = intToIntegerProperty(runStats.getStatKeyItemsFound());
    this.equipCount = intToIntegerProperty(runStats.getStatItemsFound());
    Label playerLevelLabel2 = new SimpleBindingIntegerLabel("Level: ", this.playerLevel, "");

    runStatisticsPane.add(playerLevelLabel2, 0, 1);

    SimpleBindingIntegerLabel exploreCountLabel = new SimpleBindingIntegerLabel("Times Explored: ", this.exploreCount,
        "");
    SimpleBindingIntegerLabel areaCountLabel = new SimpleBindingIntegerLabel("Areas Found: ", this.areaCount, "");
    SimpleBindingIntegerLabel eventCountLabel = new SimpleBindingIntegerLabel("Events Found: ", this.eventCount, "");
    SimpleBindingIntegerLabel monFindCountLabel = new SimpleBindingIntegerLabel("Monsters Found: ",
        this.monFindCount, "");
    SimpleBindingIntegerLabel monKillCountLabel = new SimpleBindingIntegerLabel("Total Monsters Killed: ",
        this.monKillCount, "");
    SimpleBindingIntegerLabel uniqueMonCountLabel = new SimpleBindingIntegerLabel("Unique Monsters Killed: ",
        this.uniqueMonCount, "");
    SimpleBindingIntegerLabel keyCountLabel = new SimpleBindingIntegerLabel("Key Items Found: ", this.keyCount, "");
    SimpleBindingIntegerLabel equipCountLabel = new SimpleBindingIntegerLabel("Equipment Found: ", this.equipCount, "");

    runStatisticsPane.add(exploreCountLabel, 0, 2);
    runStatisticsPane.add(areaCountLabel, 0, 3);
    runStatisticsPane.add(eventCountLabel, 0, 4);
    runStatisticsPane.add(monFindCountLabel, 0, 5);
    runStatisticsPane.add(monKillCountLabel, 0, 6);
    runStatisticsPane.add(uniqueMonCountLabel, 0, 7);
    runStatisticsPane.add(keyCountLabel, 0, 8);
    runStatisticsPane.add(equipCountLabel, 0, 9);
  }

  private void getPlayerStats() {
    this.playerLevel.set(this.player.getLevel());
    this.playerAttack = intToIntegerProperty(this.player.getAttack());
    this.playerDefence = intToIntegerProperty(this.player.getDefence());
    this.playerCritChance = intToIntegerProperty(this.player.getCritChance());
    this.playerCritDamage = intToIntegerProperty(this.player.getCritDamage());
    this.playerGold = intToIntegerProperty(this.player.getGold());
    this.xpForNextLevel = doubleToDoubleProperty(this.player.getXpForNextLevel());

  }

  private String statString(HashMap<String, Integer> combatStats) {
    // function to get String of all equipment stats
    String statPrint = "";
    for (HashMap.Entry<String, Integer> entry : combatStats.entrySet()) {
      String stat = entry.getKey();
      Integer value = entry.getValue();
      // space added to separate stats
      statPrint = statPrint + stat + value + ", ";
    }
    // -2 used to remove comma and excess space from final stat
    statPrint = statPrint.substring(0, statPrint.length() - 2);
    return statPrint;
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

  private String incrementDiscoveries(String text) {
    // used to add one to the number at the start of the string
    int numberDiscovered = Character.getNumericValue(text.charAt(0));
    numberDiscovered += 1;
    String newText = numberDiscovered + text.substring(1);
    return newText;
  }

}
