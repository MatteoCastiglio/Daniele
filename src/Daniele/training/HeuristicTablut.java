package Daniele.training;

import Daniele.ai.ITablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class HeuristicTablut {

	private double[] weights;

	public HeuristicTablut(double[] weights) {
		this.weights = weights;
	}

	/**
	 * funzione euristica:
	 * - alti valori per i bianchi
	 * - bassi valori per i neri
	 * 
	 * @param state
	 * 				stato da valutare
	 * @return
	 * 			un valore di stima dello stato
	 */


	public double value(ITablutState state) {
		
		//caso di nodo terminale
		if(state.getState().getTurn().equals(Turn.WHITEWIN)) return 1000;

		//if(state.getState().getTurn().equals(Turn.DRAW)) return 0;

		if(state.getState().getTurn().equals(Turn.BLACKWIN)) return -1000;
		
		//@Matteo ho cambiato i return e inserito questa variabile per iniziare a strutturare un euristica pi� completa
		double result  = 0;
			
		
		//caso in cui il re abbia una mossa vincente / nero perde
		if(isKingReadyToWin(state))
			//@Matteo non capisco i controlli sui turni
		//	if(state.getState().getTurn().equals(Turn.WHITE)) result-= 800;
		//	else result +=900;
			result += weights[0]*500;
		
		//caso in cui il re sia in pericolo / caso in cui un nero possa mangiare il re
		if(isKingInDanger(state))
		//	if(state.getState().getTurn().equals(Turn.WHITE)) result+= 900;
		//	else result-=800;
		result -= weights[1]*800;
		
		//@Matteo valutazioni sul numero di pezzi
		result += weights[2]*(state.WhitesCount()*40);
		result += weights[3]*(state.BlacksCount()*20);
		int[] pawnsInFlow = state.getPawnsInFlowDirection();
		result += weights[4]*pawnsInFlow[0];
		result -= weights[5]*pawnsInFlow[1];
		result += weights[6]*(state.getPawnsOnKingDiagonal()*15);
		result += weights[7]*(state.getPawnsOnKingDiagonal2()*5);
		//caso in cui un bianco sia in pericolo (se rimangono poche pedine, altrimenti privilegiare quella sotto) / caso in cui il nero possa mangiare un bianco
		
		//caso in cui un bianco pu� mangiare un nero / caso in cui il nero sia in pericolo (se rimangono poche pedine da privilegiare, rispetto quella sopra)
		
		
		
		
		//else
		return result;
	}

	//@Matteo in generale brutto usare ogni volta state.getState().getBoard.lenght  -> meglio costante !!!!
		//@Matteo isKingInDanger non sembra funzionare, inoltre le sposterei in TablutState
		
		private static boolean isKingInDanger(ITablutState state) {				//da risistemare per renderla generica? ma mi sa che vale solo per tablut
			
			
			int[] king = state.getCoordKing();
			//@Matteo versione semplificata -- quella completa non funziona

		
				int possibileCattura = 0;

				if(state.getState().getPawn(king[0] -1,king[1]).equals(Pawn.BLACK)) possibileCattura++;
				if(state.getState().getPawn(king[0] +1 ,king[1]).equals(Pawn.BLACK)) possibileCattura++;
				if(state.getState().getPawn(king[0],king[1] -1).equals(Pawn.BLACK)) possibileCattura++;
				if(state.getState().getPawn(king[0],king[1]+ 1).equals(Pawn.BLACK)) possibileCattura++;
				
			if( king[0] == 4) {
					if( king[1] ==4 && possibileCattura == 3) return true; // trono
					else if( king[1] ==3 && possibileCattura == 2) return true; //adiacente al trono
					else if( king[1] ==5 && possibileCattura == 2) return true;}//adiacente al trono
			else if( king[1] == 4) {
					if( king[0] ==3 && possibileCattura == 2) return true;//adiacente al trono
					else if( king[0] ==5 && possibileCattura == 2) return true;//adiacente al trono
			}
			else if (king[0]==2 && king[1] == 4) return true; //accampamento
			else if (king[0]==4 && king[1] == 2) return true;//accampamento
			else if (king[0]==6 && king[1] == 4)  return true;//accampamento
			else if (king[0]==4 && king[1] == 6)  return true;//accampamento
			
			else if(possibileCattura==1) return true;//altro
			
			//@Matteo mancano alcuni accampamenti
			else if((king[0]==5||king[0]==3)&&(king[1]==1||king[1]==7)) return true;
			else if((king[0]==1||king[0]==7)&&(king[1]==5||king[1]==3)) return true;
			
			return false;
			
		}



		private static boolean isKingReadyToWin(ITablutState state) {
			int king[] = state.getCoordKing();
			int x, y;
			//se il percorso in orizzontale è libero e non c'è alcun accampamento
			for (x = king[0]+1; x < state.getState().getBoard().length; x++)
				if(!state.getState().getPawn(x, king[1]).equals(Pawn.EMPTY) || state.isPawnAccampamento(x, king[1])) break;	
			if(x==state.getState().getBoard().length) return true;
			for (x = king[0]-1; x >=0; x--)
				if(!state.getState().getPawn(x, king[1]).equals(Pawn.EMPTY) || state.isPawnAccampamento(x, king[1]))	break;
			if(x<0) return true;
			//se il percorso in verticale è libero e non c'è alcun accampamento
			for (y = king[1]+1; y < state.getState().getBoard().length; y++)
				if(!state.getState().getPawn(king[0], y).equals(Pawn.EMPTY) || state.isPawnAccampamento(king[0], y))	break;
			if(y==state.getState().getBoard().length) return true;
			for (y = king[1]-1; y >= 0; y--)
				if(!state.getState().getPawn(king[0], y).equals(Pawn.EMPTY) || state.isPawnAccampamento(king[0], y))	break;
			if(y<0) return true;
			//altrimenti non è pronto a vincere	
			return false;
		}
		
//		private static boolean canYouEat(ITablutState state) {
//			
//			if(state.getState().getTurn().equals(Turn.WHITE)) {
//				for(int i=0; i<9; i++) {
//					for(int j=0; j<9; j++) {
//						if(state.getState().getPawn(i, j).equals(Pawn.WHITE))
//							if(valuesEating(i,j, Turn.WHITE, state)) return true;
//					}
//				}
//			}
////			else if(state.getState().getTurn().equals(Turn.BLACK)) {
////				for(int i=0; i<9; i++) {
////					for(int j=0; j<9; j++) {
////						if(state.getState().getPawn(i, j).equals(Pawn.BLACK))
////							if(valuesEating(i,j, Turn.BLACK, state)) return true;
////					}
////				}
////			}
	//
//			return false;
//		}
		
//		public static int canYouEat(ITablutState state) {
//			int eaten = 0;
//			
//				for(int i=0; i<9; i++) {
//					for(int j=0; j<9; j++) {
//						if(state.getState().getPawn(i, j).equals(Pawn.WHITE))
//							if(valuesEating(i,j, Turn.WHITE, state)) eaten++;
//					}
//				}
//			
////			else if(state.getState().getTurn().equals(Turn.BLACK)) {
////				for(int i=0; i<9; i++) {
////					for(int j=0; j<9; j++) {
////						if(state.getState().getPawn(i, j).equals(Pawn.BLACK))
////							if(valuesEating(i,j, Turn.BLACK, state)) return true;
////					}
////				}
////			}
//			
//				if(state.getState().getTurn().equals(Turn.WHITE)) return eaten*3;
//				else return -eaten;
//		}
	//
//		private static boolean valuesEating(int i, int j, Turn turn, ITablutState state) {
//			
////			if(turn.equals(Turn.WHITE)) {
//				if(j-1<=0); //(forse)nero sul bordo a sinistra e non lo mangia
//				else if(state.getState().getPawn(i, j-1).equals(Pawn.BLACK) && 
//								(state.getState().getPawn(i, j-2).equals(Pawn.WHITE)||
//								state.getState().getPawn(i, j-2).equals(Pawn.THRONE)||
//								state.isPawnAccampamento(i, j-2))
//								) return true;
//				if(i-1<=0); //(forse)nero sul bordo in alto e non lo mangia
//				else if(state.getState().getPawn(i-1, j).equals(Pawn.BLACK) && 
//								(state.getState().getPawn(i-2, j).equals(Pawn.WHITE)||
//								state.getState().getPawn(i-2, j).equals(Pawn.THRONE)||
//								state.isPawnAccampamento(i-2, j))
//								) return true;
//				if(j+1>=8); //(forse)nero sul bordo a destra e non lo mangia
//				else if(state.getState().getPawn(i, j+1).equals(Pawn.BLACK) && 
//								(state.getState().getPawn(i, j+2).equals(Pawn.WHITE)||
//								state.getState().getPawn(i, j+2).equals(Pawn.THRONE)||
//								state.isPawnAccampamento(i, j+2))
//								) return true;
//				if(i+1>=0); //(forse)nero sul bordo in basso e non lo mangia
//				else if(state.getState().getPawn(i+1, j).equals(Pawn.BLACK) && 
//								(state.getState().getPawn(i+2, j).equals(Pawn.WHITE)||
//								state.getState().getPawn(i+2, j).equals(Pawn.THRONE)||
//								state.isPawnAccampamento(i+2, j))
//								) return true;
//				
////			}
////			else if(turn.equals(Turn.BLACK)) {
////				
////			}
//			
//			
//			return false;
//		}


	}
