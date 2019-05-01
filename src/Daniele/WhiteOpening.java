package Daniele;

import java.io.IOException;




public class WhiteOpening {

	
	
		public static DanieleAction nextMove(ITablutState state, int turn ) throws IOException
		{
		
			switch(turn) { 
		
			case 0: return new DanieleAction(4,5,3,5);
				
			case 1: return new DanieleAction(5,4,5,5);

			case 2: return null;
			
			default: return null;
			
			}
			
			
		
	}

}
