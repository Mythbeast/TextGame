import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MenuGui {
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;
    private static final int MENU_WIDTH = 1000;
    private static final int MENU_HEIGHT = 600;
    private static final Insets DEFAUL_INSETS = new Insets(10, 10, 10, 10);
    private static final int GRIDPANE_GAPS = 10;
    private static final String BORDER_STYLE = "-fx-border-color: black; -fx-border-width: 2px;";
    private Stage primaryStage;
    private DatabaseManager db;
    private GridPane menu = new GridPane();
    private Button newGameButton = new Button("New Game");
    private Button loadGameButton = new Button("Load Game");
    private Button statsButton = new Button("Statistics");
    private GridPane loadMenu = new GridPane();
    private Button loadSave = new Button("Load");
    private int activeSaveNumber;
    private int howManySaves;
    private GridPane statsMenu = new GridPane();
    private GridPane statSubMenu = new GridPane();
    private GridPane statsPane = new GridPane();

    public MenuGui(Stage primaryStage, DatabaseManager db) {
        this.primaryStage = primaryStage;
        this.db = db;

        // main layout manager
        setAllPanes();
        menu.setAlignment(Pos.CENTER);

        createMenu();

        // set the scene and show the window
        Scene scene = new Scene(menu, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("TextGame");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void onNewGame() {
        Gui gui = new Gui(primaryStage);
        GameLogic game = new GameLogic(db, gui, 0);
    }

    private void onLoadGame() {
        // clear window
        menu.getChildren().clear();
        this.howManySaves = db.howManySaves();

        // add load button and titles
        addLoadElements();
        Button backButton = createBackButton();

        loadMenu.add(backButton, 5, howManySaves + 5);

        menu.add(loadMenu, 0, 0);

        // add info for each save

        if (howManySaves == 0) {
            Label noSaves = new Label("There are no saved games");
            GridPane.setColumnSpan(noSaves, 3);
            loadMenu.add(noSaves, 1, 4);
            loadMenu.getChildren().remove(loadSave);
            return;
        }
        addDeleteButton();
        for (int i = 1; i <= howManySaves; i++) {
            showSaveDetails(i);
        }
        ;

        for (javafx.scene.Node node : loadMenu.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
    }

    private void onStats() {
        menu.getChildren().clear();
        menu.add(statsMenu, 0, 0);
        // if menu not accessed before, create elements
        if (statsMenu.getChildren().size() == 0) {
            // top menu of statistics pane
            statsMenu.add(statSubMenu, 0, 0);
            Button records = new Button("Records");
            Button areas = new Button("Areas");
            Button monsters = new Button("Monsters");
            Button items = new Button("Items");
            Button backButton = createBackButton();
            // add buttons to top menu
            statSubMenu.add(records, 0, 0);
            statSubMenu.add(areas, 1, 0);
            statSubMenu.add(monsters, 2, 0);
            statSubMenu.add(items, 3, 0);
            statsMenu.add(backButton, 4, 0);
            GridPane.setHalignment(backButton, HPos.RIGHT);

            // bind buttons
            records.setOnAction(event -> {
                createRecordPage();
            });
            areas.setOnAction(event -> {
                createAreasPage();
            });
            monsters.setOnAction(event -> {
                createMonstersPage();
            });
            items.setOnAction(event -> {
                createItemsPage();
            });

            // create pane for records
            statsMenu.add(statsPane, 0, 1);
            GridPane.setColumnSpan(statsPane, 5);
            statsPane.setStyle(BORDER_STYLE);
            statsPane.setPrefSize(MENU_WIDTH, MENU_HEIGHT);
        }
    }

    private void setAllPanes() {
        setGridPane(menu);
        setGridPane(statsPane);
        setGridPane(statsMenu);
        setGridPane(statSubMenu);
        setGridPane(loadMenu);
    }

    private void setGridPane(GridPane gridPane) {
        gridPane.setHgap(GRIDPANE_GAPS);
        gridPane.setVgap(GRIDPANE_GAPS);
        gridPane.setPadding(DEFAUL_INSETS);
    }

    private void addLoadElements() {
        loadMenu.add(new Label("Save"), 0, 0);
        loadMenu.add(new Label("Areas Found"), 1, 0);
        loadMenu.add(new Label("Level"), 2, 0);
        loadMenu.add(new Label("Game Mode"), 3, 0);

        loadMenu.add(loadSave, 4, howManySaves + 5);
        loadSave.setOnAction(event -> {
            if (this.activeSaveNumber > 0) {
                loadSave(this.activeSaveNumber);
            } else {
                Label noActiveSaveError = new Label("Please choose a save");
                GridPane.setColumnSpan(noActiveSaveError, 3);
                loadMenu.add(noActiveSaveError, 0, howManySaves + 5);
            }
        });
    }

    private Button createBackButton() {
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            returnToMainMenu();
        });
        return backButton;
    }

    private void addDeleteButton() {
        Button delButton = new Button("Delete");
        delButton.setOnAction(event -> {
            db.deleteSave(activeSaveNumber);
            loadMenu.getChildren().removeIf(node -> GridPane.getRowIndex(node) == activeSaveNumber + 1);
        });
        loadMenu.add(delButton, 6, howManySaves + 5);
    }

    private void returnToMainMenu() {
        menu.getChildren().clear();
        loadMenu.getChildren().clear();
        createMenu();
    }

    private void showSaveDetails(int saveNumber) {
        SaveInfo info = db.getSaveInfo(saveNumber);
        if (info.getAreasDiscovered() != 0) {

            String areasDiscovered = String.valueOf(info.getAreasDiscovered());
            String level = String.valueOf(info.getLevel());
            String gameMode = info.getGameMode();
            String gameModeLabelText = gameMode.substring(0, 1).toUpperCase() + gameMode.substring(1);

            Button select = new Button(String.valueOf(saveNumber));
            select.setOnAction(event -> {
                this.activeSaveNumber = saveNumber;
            });
            loadMenu.add(select, 0, saveNumber + 1);
            loadMenu.add(new Label(areasDiscovered), 1, saveNumber + 1);
            loadMenu.add(new Label(level), 2, saveNumber + 1);
            loadMenu.add(new Label(gameModeLabelText), 3, saveNumber + 1);
        }
    }

    private void loadSave(int saveNumber) {
        Gui gui = new Gui(primaryStage);
        GameLogic game = new GameLogic(db, gui, saveNumber);
    }

    private void createMenu() {
        newGameButton.setOnAction(event -> {
            onNewGame();
        });
        loadGameButton.setOnAction(event -> {
            onLoadGame();
        });
        statsButton.setOnAction(event -> {
            onStats();
        });

        menu.add(newGameButton, 0, 0);
        menu.add(loadGameButton, 0, 1);
        menu.add(statsButton, 0, 2);
    }

    private void createRecordPage() {

    }

    private void createAreasPage() {

    }

    private void createMonstersPage() {

    }

    private void createItemsPage() {

    }

}
