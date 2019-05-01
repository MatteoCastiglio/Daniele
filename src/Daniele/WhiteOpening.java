package Daniele;

import java.io.IOException;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class WhiteOpening {

	
	
		public static Action nextMove(ITablutState state, int turn ) throws IOException
		{
		
			switch(turn) { 
		
			case 0: return new Action("f5", "f4", Turn.BLACK);
				
			case 1: return new Action("e6", "f6", Turn.BLACK);
			
			case 2: return new Action("c5", "c6", Turn.BLACK);
			
			default: return null;
			
			}
			
			
		
	}

}
