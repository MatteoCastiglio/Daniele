package Daniele.ai;

import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.HashMap;
import java.util.Map;


public class TraspositionTable {

    private Map<State,Couple> table = new HashMap<State,Couple>();
    private static TraspositionTable ourInstance = new TraspositionTable();

    public static TraspositionTable getInstance() {
        return ourInstance;
    }

    private TraspositionTable() {
    }

    public void add(State s,int distanceFromLeaves,double val)
    {
 
        if(table.containsKey(s)) 
        {
        	int d = table.get(s).depth;
        	if(d<distanceFromLeaves)
        	table.put(s,new Couple(distanceFromLeaves,val));
        }
        else
        {
        	
         	table.put(s,new Couple(distanceFromLeaves,val));
        }	
        	
    }

    public double valueOver(State s,Integer distanceFromLeaves)
    {
    	 if(table.containsKey(s)) {
    	int d = table.get(s).depth;
    	if(d> distanceFromLeaves)
    	 		return table.get(s).val;
    	 }
    	return Double.NaN;


        	
    }
    public void clear()
    {
        table.clear();
    }

    public synchronized void  threadSafeAdd(State s,int depth,double val)
    {
        add(s,depth,val);
    }

    public synchronized  void threadSafeClear()
    {
    	table.clear();
    }



private class Couple
{
private int depth;
private double val;

public Couple(int depth, double val) {
	super();
	this.depth = depth;
	this.val = val;
}


}
}
