package daniele.state;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface ITablutState {


	List<DanieleAction> getAllLegalMoves();

	State getState();
	
	boolean isCellACitadel(int i,int j);

	int[] getCoordKing();

	int BlacksCount();

	int WhitesCount();

	int getPawnsOnKingDiagonal();

	int getPawnsOnKingDiagonal2();

	int[] getPawnsInFlowDirection();

	double getFlow();

	List<PawnPosition> trasformState(DanieleAction action);

	void trasformStateBack(DanieleAction a,List<PawnPosition> p);
	

}
