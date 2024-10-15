
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
  private Gui gui;
  private GameLogic game1;

  @Override
  public void start(Stage primaryStage) {

    DatabaseManager db = new DatabaseManager();
    MenuGui menu = new MenuGui(primaryStage, db);

    primaryStage.show();

    // TODO: create classes for db returns to avoid casting
    // TODO: create game end on death - remove explore button and combobox and
    // button for stats/main menu
    // TODO: create roguelike mode - edit new game menu to create (similar to load
    // menu)

  }

  public static void main(String[] args) {
    launch(args);
  }

}