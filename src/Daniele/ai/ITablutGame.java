package Daniele.ai;

public interface ITablutGame {
	public TablutState getNextState(TablutState mystate, DanieleAction a);
}
