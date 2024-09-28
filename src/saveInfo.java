public class saveInfo {
    private int areasDiscovered;
    private int level;
    private String gameMode;

    public saveInfo(int areasDiscovered, int level, String gameMode) {
        this.areasDiscovered = areasDiscovered;
        this.level = level;
        this.gameMode = gameMode;
    }

    public int getAreasDiscovered() {
        return this.areasDiscovered;
    }

    public int getLevel() {
        return this.level;
    }

    public String getGameMode() {
        return this.gameMode;

    }
}
