package Daniele;

import java.util.List;


import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface ITablutState {

	ITablutState getChildState(DanieleAction action);
	 List<DanieleAction> getAllLegalMoves();
	 List<DanieleAction> getTopLeftMoves();
	 State getState();
	int[] getCoordKing();
	boolean isPawnAccampamento(int i, int j);
	
	//@Matteo
	int BlacksCount();
	int WhitesCount();
	int getWhitePawnsMoved();
	int getPawnsOnKingDiagonal();
	int getPawnsOnKingDiagonal2();
	
	
}
