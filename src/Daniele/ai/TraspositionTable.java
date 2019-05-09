package Daniele.ai;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.HashSet;
import java.util.Set;

public class TraspositionTable {

    private Set<State> table = new HashSet<State>();
    private static TraspositionTable ourInstance = new TraspositionTable();

    public static TraspositionTable getInstance() {
        return ourInstance;
    }

    private TraspositionTable() {
    }

    public boolean add(State s)
    {
        return table.add(s);
    }

    public void clear()
    {
        table = new HashSet<>();
        //table.clear();
    }

    public synchronized boolean threadSafeAdd(State s)
    {
        return table.add(s);
    }

    public synchronized  void threadSafeClear()
    {
        table = new HashSet<State>();
    }
}
