public class RunStatList {
    private Gui gui;
    private int statEventsDiscovered;
    private int statAreasDiscovered;
    private int statTimesExplored;
    private int statMonstersKilled;
    private int statMonstersFound;
    private int statDifferentMonsters;
    private int statItemsFound;
    private int statKeyItemsFound;

    public RunStatList(Gui gui) {
        this.gui = gui;
    }

    public int getStatAreasDiscovered() {
        return statAreasDiscovered;
    }

    public int getStatEventsDiscovered() {
        return statEventsDiscovered;
    }

    public int getStatDifferentMonsters() {
        return statDifferentMonsters;
    }

    public int getStatItemsFound() {
        return statItemsFound;
    }

    public int getStatKeyItemsFound() {
        return statKeyItemsFound;
    }

    public int getStatMonstersKilled() {
        return statMonstersKilled;
    }

    public int getStatMonstersFound() {
        return statMonstersFound;
    }

    public int getStatTimesExplored() {
        return statTimesExplored;
    }

    public void setStatAreasDiscovered(int statAreasDiscovered) {
        this.statAreasDiscovered = statAreasDiscovered;
    }

    public void setStatDifferentMonsters(int statDifferentMonsters) {
        this.statDifferentMonsters = statDifferentMonsters;
    }

    public void setStatEventsDiscovered(int statEventsDiscovered) {
        this.statEventsDiscovered = statEventsDiscovered;
    }

    public void setStatItemsFound(int statItemsFound) {
        this.statItemsFound = statItemsFound;
    }

    public void setStatKeyItemsFound(int statKeyItemsFound) {
        this.statKeyItemsFound = statKeyItemsFound;
    }

    public void setStatMonstersKilled(int statMonstersKilled) {
        this.statMonstersKilled = statMonstersKilled;
    }

    public void setStatMonstersFound(int statMonstersFound) {
        this.statMonstersFound = statMonstersFound;
    }

    public void setStatTimesExplored(int statTimesExplored) {
        this.statTimesExplored = statTimesExplored;
    }

    public void addStatAreasDiscovered() {
        this.statAreasDiscovered += 1;
        gui.setAreaCount(this.statAreasDiscovered);
    }

    public void addStatDifferentMonsters() {
        this.statDifferentMonsters += 1;
        gui.setUniqueMonCount(this.statDifferentMonsters);
    }

    public void addStatEventsDiscovered() {
        this.statEventsDiscovered += 1;
        gui.setEventCount(this.statEventsDiscovered);
    }

    public void addStatItemsFound() {
        this.statItemsFound += 1;
        gui.setEquipCount(this.statItemsFound);
    }

    public void addStatKeyItemsFound() {
        this.statKeyItemsFound += 1;
        gui.setKeyCount(this.statKeyItemsFound);
    }

    public void addStatMonstersKilled() {
        this.statMonstersKilled += 1;
        gui.setMonKillCount(this.statMonstersKilled);
    }

    public void addStatMonstersFound() {
        this.statMonstersFound += 1;
        gui.setMonFindCount(this.statMonstersFound);
    }

    public void addStatTimesExplored() {
        this.statTimesExplored += 1;
        gui.setExploreCount(this.statTimesExplored);
    }

}
