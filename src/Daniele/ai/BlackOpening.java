package Daniele.ai;

import java.io.IOException;




public class BlackOpening {

	public static DanieleAction nextMove(ITablutState state, int turn ) throws IOException
	{
	
		switch(turn) { 
	
		case 0: return new DanieleAction(8,5,4,5);
			
		case 1: return new DanieleAction(4,7,4,5);
		
		case 2: return null;
		
		default: return null;
		
		}
		
		
	}
}
