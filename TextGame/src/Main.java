
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
  private Gui gui;
  private GameLogic game1;

  @Override
  public void start(Stage primaryStage) {
    this.gui = new Gui(primaryStage);
    DatabaseManager db = new DatabaseManager();
    this.game1 = new GameLogic(db, gui);

    primaryStage.setTitle("TextGame");
    primaryStage.show();

    // TODO: create classes for db returns to avoid casting
    // TODO: change main class to application
  }

  public static void main(String[] args) {
    launch(args);
  }

}
