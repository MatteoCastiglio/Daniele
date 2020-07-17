package daniele.state;

import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface ITablutGame {

	
	State checkCaptureBlackKingLeft(State state, DanieleAction a); 
	
	State checkCaptureBlackKingRight(State state, DanieleAction a);

	State checkCaptureBlackKingDown(State state, DanieleAction a);
	
	State checkCaptureBlackKingUp(State state, DanieleAction a);

	List<PawnPosition> checkCaptureWhite(State state, DanieleAction a);
	
	List<PawnPosition> checkCaptureBlackPawnRight(State state, DanieleAction a); 
	
	List<PawnPosition> checkCaptureBlackPawnLeft(State state, DanieleAction a);

	List<PawnPosition> checkCaptureBlackPawnUp(State state, DanieleAction a); 
	
	List<PawnPosition> checkCaptureBlackPawnDown(State state, DanieleAction a) ;
	

}
