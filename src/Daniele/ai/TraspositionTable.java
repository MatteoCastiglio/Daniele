package Daniele.ai;

public class TraspositionTable {
    private static TraspositionTable ourInstance = new TraspositionTable();

    public static TraspositionTable getInstance() {
        return ourInstance;
    }

    private TraspositionTable() {
    }
}
