package Daniele.state;

public interface ITablutGame {
	public TablutState getNextState(TablutState mystate, DanieleAction a);
}
