
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
    // TODO: create roguelike mode - edit new game menu to create (similar to load
    // menu)
    // TODO: move 'attack' from 'explore' button and add different attack options
    // TODO: add move events and items
    // TODO: add stats to main stats menu

  }

  public static void main(String[] args) {
    launch(args);
  }

}

// TODO: check hedgehog scout text
// TODO: trail warden stat tracking check?
// TODO: add level up options / perks
// perk list:
// stat plus or stat multi (incl xp, drop rate, gold etc, attack, defence etc)
// area/drop find chance etc - reverse drop rarity etc?
// first strike (unimplemented)
// TODO: add more to monster UI - killcount, drops etc (unlocked with high
// killcounts?)
// TODO: monster drops / event items as item list for each area track in stats?
// TODO: turn counter (explores only) with damage multiplier if too slow -
// roguelike mode
// TODO: run button and text when fleeing (90% chance maybe?)
// TODO: create map?
