import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class menuGui {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 500;
    private static final Insets DEFAUL_INSETS = new Insets(10, 10, 10, 10);
    private static final int GRIDPANE_GAPS = 10;
    private Stage primaryStage;
    private DatabaseManager db;
    private GridPane menu = new GridPane();
    private Button newGameButton = new Button("New Game");
    private Button loadGameButton = new Button("Load Game");
    private GridPane loadMenu = new GridPane();
    private int activeSaveNumber;
    private int howManySaves;

    public menuGui(Stage primaryStage, DatabaseManager db) {
        this.primaryStage = primaryStage;
        this.db = db;

        // main layout manager
        menu.setHgap(GRIDPANE_GAPS);
        menu.setVgap(GRIDPANE_GAPS);
        menu.setPadding(DEFAUL_INSETS);
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

        loadMenu.setHgap(GRIDPANE_GAPS);
        loadMenu.setVgap(GRIDPANE_GAPS);
        loadMenu.setPadding(DEFAUL_INSETS);

        // add load button and titles
        addLoadElements();
        addBackButton();
        addDeleteButton();

        // add info for each save

        if (howManySaves == 0) {
            Label noSaves = new Label("There are no saved games");
            GridPane.setColumnSpan(noSaves, 3);
            loadMenu.add(noSaves, 1, 4);
        }
        for (int i = 1; i <= howManySaves; i++) {
            showSaveDetails(i);
        }
        ;
        menu.add(loadMenu, 0, 0);

        for (javafx.scene.Node node : loadMenu.getChildren()) {
            GridPane.setHalignment(node, HPos.CENTER);
        }
    }

    private void addLoadElements() {
        loadMenu.add(new Label("Save"), 0, 0);
        loadMenu.add(new Label("Areas Found"), 1, 0);
        loadMenu.add(new Label("Level"), 2, 0);
        loadMenu.add(new Label("Game Mode"), 3, 0);

        Button loadSave = new Button("Load");
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

    private void addBackButton() {
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            returnToMainMenu();
        });
        loadMenu.add(backButton, 5, howManySaves + 5);
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
        saveInfo info = db.getSaveInfo(saveNumber);
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

        menu.add(newGameButton, 0, 0);
        menu.add(loadGameButton, 0, 1);
    }

}
