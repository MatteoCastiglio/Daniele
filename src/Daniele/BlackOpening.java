package Daniele;

import java.io.IOException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class BlackOpening {

	public static Action nextMove(ITablutState state, int turn ) throws IOException
	{
	
		switch(turn) { 
	
		case 0: return new Action("d1", "d4", Turn.BLACK);
			
		case 1: return new Action("f1", "f4", Turn.BLACK);
		
		case 2: return new Action("e8", "e7", Turn.BLACK);
		
		default: return null;
		
		}
		
		
	}
}
