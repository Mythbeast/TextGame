import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class Area {
  private DatabaseManager db;
  private List<Object> info;
  private String ID;
  private String name;
  private List<Object> subsequentAreas;
  private int totalSubsequentAreas;
  private int discoveredAreas;
  private String numberAreasDiscovered;
  private int[] areaWeights;
  private int[] areaWeightThresholds;
  private List<Object> monsterList;
  private int[] monsterWeights;
  private int[] monsterWeightThresholds;
  private List<Object> eventList;
  private int[] eventWeights;
  private int[] eventWeightThresholds;
  private int areaChance;
  private int monsterChance;
  private int eventChance;

  // explore chance is [0, areaChance, monsterChance, itemChance] to choose event
  // on explore
  // specific event is then located afterwards
  private int[] exploreChance;
  private Random rand = new Random();

  // TODO: on area creation, check for discovered areaIDs and remove from
  // subsequent areas immediately.
  public Area(DatabaseManager db, String areaID, String name, int areaChance, int monsterChance, int eventChance,
      ArrayList<String> discoveredAreaIds) {
    this.db = db;
    this.ID = areaID;
    this.name = name;
    this.areaChance = areaChance;
    this.monsterChance = monsterChance;
    this.eventChance = eventChance;

    // create array of thresholds for the random number
    // chances are being added so that when a random number is generated between 1
    // and 100, it falls into ...<=areaC, areaC<..<=monC or monC<..<=eventC
    this.exploreChance = createThresholds(new int[] { this.areaChance, this.monsterChance, this.eventChance });

    if (this.exploreChance[3] != 100) {
      System.out.println("Error: Area chances are invalid.");
    }
    this.monsterList = db.getMonsterList(this.ID);
    this.createMonsterWeights();
    this.monsterWeightThresholds = this.createThresholds(monsterWeights);
    this.subsequentAreas = db.getAreaList(this.ID);
    // calculate total number of areas found from this one
    this.totalSubsequentAreas = subsequentAreas.size();
    // remove any discovered areas from subsequentArea List
    // use of subsequentAreaIdList to prevent concurrent modifcation exception
    ArrayList<String> subsequentAreaIdList = new ArrayList<String>();
    for (Object areaInfo : this.subsequentAreas) {
      String areaId = (String) ((List<Object>) areaInfo).get(0);
      subsequentAreaIdList.add(areaId);

    }
    // remove any subsequent areas that have already been discovered by the player
    for (String id : discoveredAreaIds) {
      this.subsequentAreas.removeIf(areaInfo -> ((List<Object>) areaInfo).get(0).equals(id));
    }

    // create String for areaStats Label
    this.discoveredAreas = this.totalSubsequentAreas - subsequentAreas.size();
    this.numberAreasDiscovered = discoveredAreas + " / " + totalSubsequentAreas;

    this.createAreaWeights();
    this.areaWeightThresholds = this.createThresholds(areaWeights);
    this.eventList = db.getEventList(this.ID);
    this.createEventWeights();
    this.eventWeightThresholds = this.createThresholds(eventWeights);
  }

  // getters and setters
  public String getName() {
    return this.name;
  }

  public int[] getExploreChance() {
    return this.exploreChance;
  }

  public int[] getAreaWeightThresholds() {
    return this.areaWeightThresholds;
  }

  public List<Object> getSubsequentAreas() {
    return this.subsequentAreas;
  }

  public void setSubsequentAreas(List<Object> areaList) {
    // method used to remove an area from the list and recreate weights and
    // thresholds
    this.subsequentAreas = areaList;
    this.discoveredAreas = this.totalSubsequentAreas - areaList.size();
    this.createAreaWeights();
    this.areaWeightThresholds = this.createThresholds(areaWeights);
  }

  public String getNumberAreasDiscovered() {
    this.numberAreasDiscovered = discoveredAreas + " / " + totalSubsequentAreas;
    return this.numberAreasDiscovered;
  }

  public int remainingAreas() {
    return this.subsequentAreas.size();
  }

  public int[] getMonsterWeightThresholds() {
    return this.monsterWeightThresholds;
  }

  public List<Object> getMonsterList() {
    return this.monsterList;
  }

  public int[] getEventWeightThresholds() {
    return this.eventWeightThresholds;
  }

  public List<Object> getEventList() {
    return this.eventList;
  }

  // other methods
  private void createMonsterWeights() {
    // method to create int[] of monsterweights
    ArrayList<Integer> arrayWeights = new ArrayList<Integer>();

    // cycle through each monster
    for (int i = 0; i <= this.monsterList.size() - 1; i++) {
      List<Object> monster = (List<Object>) this.monsterList.get(i);

      // check monster weight is a valid integer
      if (monster.get(1) instanceof Integer) {
        // add each monster weight to arrayWeights
        arrayWeights.add((Integer) monster.get(1));
      } else {
        System.out.println("Error: getMonsterWeight cast error");
      }
    }
    // convert ArrayList into an int[]
    this.monsterWeights = arrayListToIntList(arrayWeights);
  }

  private void createAreaWeights() {
    // method to create int[] of areaWeights
    ArrayList<Integer> arrayWeights = new ArrayList<Integer>();

    // cycle through each area
    for (int i = 0; i <= this.subsequentAreas.size() - 1; i++) {
      List<Object> area = (List<Object>) this.subsequentAreas.get(i);

      // check monster weight is a valid integer
      if (area.get(1) instanceof Integer) {
        // add each monster weight to arrayWeights
        arrayWeights.add((Integer) area.get(1));
      } else {
        System.out.println("Error: getAreaWeight cast error");
      }
    }
    // convert ArrayList into an int[]
    this.areaWeights = arrayListToIntList(arrayWeights);
  }

  private void createEventWeights() {
    // method to create int[] of eventWeights
    ArrayList<Integer> arrayWeights = new ArrayList<Integer>();

    // cycle through each area
    for (int i = 0; i <= this.eventList.size() - 1; i++) {
      List<Object> event = (List<Object>) this.eventList.get(i);

      // check event weight is a valid integer
      if (event.get(1) instanceof Integer) {
        // add each event weight to arrayWeights
        arrayWeights.add((Integer) event.get(1));
      } else {
        System.out.println("Error: getEventWeight cast error");
      }
    }
    // convert ArrayList into an int[]
    this.eventWeights = arrayListToIntList(arrayWeights);
  }

  private int[] createThresholds(int[] chances) {
    // method to take int[] input and output an int[] with 0 at the start and a
    // cumulative total.
    // e.g. createThresholds({1, 1, 2, 1, 5}) = {0, 1, 2, 4, 5, 10}
    int[] result = new int[chances.length + 1];
    result[0] = 0;

    for (int i = 0; i < chances.length; i++) {
      result[i + 1] = result[i] + chances[i];
    }
    return result;
  }

  private int[] arrayListToIntList(ArrayList<Integer> arraylist) {
    // function to convert Arralist<Integer> to int[]
    int[] result = new int[arraylist.size()];
    for (int i = 0; i < arraylist.size(); i++) {
      result[i] = arraylist.get(i);
    }
    return result;
  }

}
