package Daniele;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface ITablutState {

	public ITablutState getChildState(Action action);
	public List<Action> getAllLegalMoves();
	public List<Action> getTopLeftMoves();
	public State getState();
	public int[] getCoordKing();
	public boolean isPawnAccampamento(int i, int j);
	
	//@Matteo
	public int BlacksCount();
	public int WhitesCount();
	public int getWhitePawnsMoved();
	
	
}
