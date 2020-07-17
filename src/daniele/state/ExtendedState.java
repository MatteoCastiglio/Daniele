package daniele.state;

import it.unibo.ai.didattica.competition.tablut.domain.State;


public class ExtendedState extends State{
	
	
	public void setPawn(int row, int column,Pawn p) {
		this.board[row][column] = p;
	}
}
