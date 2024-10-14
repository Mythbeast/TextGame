import javafx.application.Application;
import javafx.stage.Stage;

public class main extends Application {
  private Gui gui;
  private GameLogic game1;

  @Override
  public void start(Stage primaryStage) {

    DatabaseManager db = new DatabaseManager();
    MenuGui menu = new MenuGui(primaryStage, db);

    primaryStage.show();

  }

  public static void main(String[] args) {
    launch(args);
  }

}

// TODO: create classes for db returns to avoid casting
// TODO: create roguelike mode - edit new game menu to create (similar to load
// menu)
// TODO: add sprites for monsters
// TODO: add statistics to stats menu in main menu
// TODO: implement extra item stats and create more items
// TODO: improve visual
// TODO: add buttons for different attack types and move 'attack' from'explore'
// button
// TODO: allow events to keep buttons there for shops etc.