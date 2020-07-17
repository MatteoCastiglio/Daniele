package daniele.ai;

import daniele.state.ITablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class HeuristicTablut implements HeuristicFunction {

	/**
	 * heuristic function:

	 * @param state
	 * state to evaluate
	 * @return the estimation
	 */

	private static final double WEIGTH1 = 500;
	private static final double WEIGTH2 = 800;
	private static final double WEIGTH3 = 40;
	private static final double WEIGTH4 = 20;
	private static final double WEIGTH5 = 1;
	private static final double WEIGTH6 = 1;
	private static final double WEIGTH7 = 15;
	private static final double WEIGTH8 = 5;

	public double computeValue(ITablutState state) {

		if (state.getState().getTurn().equals(Turn.WHITEWIN)) return 10000;

		if (state.getState().getTurn().equals(Turn.BLACKWIN)) return -10000;

		double result = 0;

		if (isKingReadyToWin(state))

			result += WEIGTH1;

		if (isKingInDanger(state))

			result -= WEIGTH2;
		// evaluating number of pieces (high value for whites, low values for blacks)
		result += state.WhitesCount() * WEIGTH3;
		result -= state.BlacksCount() * WEIGTH4;
		//c !!!! pawns around king
		int[] pawnsInFlow = state.getPawnsInFlowDirection();
		result += pawnsInFlow[0] * WEIGTH5;
		result -= pawnsInFlow[1] * WEIGTH6;
		// pawns on king's diagonal
		result += state.getPawnsOnKingDiagonal() * WEIGTH7;
		result += state.getPawnsOnKingDiagonal2() * WEIGTH8;
		return result;
	}

	private static boolean isKingInDanger(ITablutState state) {


		int[] king = state.getCoordKing();
		//@Matteo versione semplificata -- quella completa non funziona


		int possibileCattura = 0;

		if (state.getState().getPawn(king[0] - 1, king[1]).equals(Pawn.BLACK)) possibileCattura++;
		if (state.getState().getPawn(king[0] + 1, king[1]).equals(Pawn.BLACK)) possibileCattura++;
		if (state.getState().getPawn(king[0], king[1] - 1).equals(Pawn.BLACK)) possibileCattura++;
		if (state.getState().getPawn(king[0], king[1] + 1).equals(Pawn.BLACK)) possibileCattura++;

		if (king[0] == 4) {
			if (king[1] == 4 && possibileCattura == 3) return true; // trono
			else if (king[1] == 3 && possibileCattura == 2) return true; //adiacente al trono
			else if (king[1] == 5 && possibileCattura == 2) return true;
		}//adiacente al trono
		else if (king[1] == 4) {
			if (king[0] == 3 && possibileCattura == 2) return true;//adiacente al trono
			else if (king[0] == 5 && possibileCattura == 2) return true;//adiacente al trono
		} else if (king[0] == 2 && king[1] == 4) return true; //accampamento
		else if (king[0] == 4 && king[1] == 2) return true;//accampamento
		else if (king[0] == 6 && king[1] == 4) return true;//accampamento
		else if (king[0] == 4 && king[1] == 6) return true;//accampamento

		else if (possibileCattura == 1) return true;//altro

		else if ((king[0] == 5 || king[0] == 3) && (king[1] == 1 || king[1] == 7)) return true;
		else if ((king[0] == 1 || king[0] == 7) && (king[1] == 5 || king[1] == 3)) return true;

		return false;

	}


	private static boolean isKingReadyToWin(ITablutState state) {
		int king[] = state.getCoordKing();
		int x, y;
		//line free of pawns or citadels
		for (x = king[0] + 1; x < state.getState().getBoard().length; x++)
			if (!state.getState().getPawn(x, king[1]).equals(Pawn.EMPTY) || state.isCellACitadel(x, king[1])) break;
		if (x == state.getState().getBoard().length) return true;
		for (x = king[0] - 1; x >= 0; x--)
			if (!state.getState().getPawn(x, king[1]).equals(Pawn.EMPTY) || state.isCellACitadel(x, king[1])) break;
		if (x < 0) return true;
		//columns free of pawsn or citadels
		for (y = king[1] + 1; y < state.getState().getBoard().length; y++)
			if (!state.getState().getPawn(king[0], y).equals(Pawn.EMPTY) || state.isCellACitadel(king[0], y)) break;
		if (y == state.getState().getBoard().length) return true;
		for (y = king[1] - 1; y >= 0; y--)
			if (!state.getState().getPawn(king[0], y).equals(Pawn.EMPTY) || state.isCellACitadel(king[0], y)) break;
		if (y < 0) return true;
		// else the king is not ready to win
		return false;
	}

	
	
//	private static boolean canYouEat(ITablutState state) {
//		
//		if(state.getState().getTurn().equals(Turn.WHITE)) {
//			for(int i=0; i<9; i++) {
//				for(int j=0; j<9; j++) {
//					if(state.getState().getPawn(i, j).equals(Pawn.WHITE))
//						if(valuesEating(i,j, Turn.WHITE, state)) return true;
//				}
//			}
//		}
////		else if(state.getState().getTurn().equals(Turn.BLACK)) {
////			for(int i=0; i<9; i++) {
////				for(int j=0; j<9; j++) {
////					if(state.getState().getPawn(i, j).equals(Pawn.BLACK))
////						if(valuesEating(i,j, Turn.BLACK, state)) return true;
////				}
////			}
////		}
//
//		return false;
//	}
	
//	public static int canYouEat(ITablutState state) {
//		int eaten = 0;
//		
//			for(int i=0; i<9; i++) {
//				for(int j=0; j<9; j++) {
//					if(state.getState().getPawn(i, j).equals(Pawn.WHITE))
//						if(valuesEating(i,j, Turn.WHITE, state)) eaten++;
//				}
//			}
//		
////		else if(state.getState().getTurn().equals(Turn.BLACK)) {
////			for(int i=0; i<9; i++) {
////				for(int j=0; j<9; j++) {
////					if(state.getState().getPawn(i, j).equals(Pawn.BLACK))
////						if(valuesEating(i,j, Turn.BLACK, state)) return true;
////				}
////			}
////		}
//		
//			if(state.getState().getTurn().equals(Turn.WHITE)) return eaten*3;
//			else return -eaten;
//	}
//
//	private static boolean valuesEating(int i, int j, Turn turn, ITablutState state) {
//		
////		if(turn.equals(Turn.WHITE)) {
//			if(j-1<=0); //(forse)nero sul bordo a sinistra e non lo mangia
//			else if(state.getState().getPawn(i, j-1).equals(Pawn.BLACK) && 
//							(state.getState().getPawn(i, j-2).equals(Pawn.WHITE)||
//							state.getState().getPawn(i, j-2).equals(Pawn.THRONE)||
//							state.isCellACitadel(i, j-2))
//							) return true;
//			if(i-1<=0); //(forse)nero sul bordo in alto e non lo mangia
//			else if(state.getState().getPawn(i-1, j).equals(Pawn.BLACK) && 
//							(state.getState().getPawn(i-2, j).equals(Pawn.WHITE)||
//							state.getState().getPawn(i-2, j).equals(Pawn.THRONE)||
//							state.isCellACitadel(i-2, j))
//							) return true;
//			if(j+1>=8); //(forse)nero sul bordo a destra e non lo mangia
//			else if(state.getState().getPawn(i, j+1).equals(Pawn.BLACK) && 
//							(state.getState().getPawn(i, j+2).equals(Pawn.WHITE)||
//							state.getState().getPawn(i, j+2).equals(Pawn.THRONE)||
//							state.isCellACitadel(i, j+2))
//							) return true;
//			if(i+1>=0); //(forse)nero sul bordo in basso e non lo mangia
//			else if(state.getState().getPawn(i+1, j).equals(Pawn.BLACK) && 
//							(state.getState().getPawn(i+2, j).equals(Pawn.WHITE)||
//							state.getState().getPawn(i+2, j).equals(Pawn.THRONE)||
//							state.isCellACitadel(i+2, j))
//							) return true;
//			
////		}
////		else if(turn.equals(Turn.BLACK)) {
////			
////		}
//		
//		
//		return false;
//	}


}
