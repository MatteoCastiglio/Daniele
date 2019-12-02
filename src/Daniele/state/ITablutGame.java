package Daniele.state;

public interface ITablutGame {
	TablutState getNextState(TablutState mystate, DanieleAction a);
}
