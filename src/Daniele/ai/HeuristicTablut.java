package Daniele.ai;

import Daniele.ai.ITablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class HeuristicTablut {

	
	
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
	
	
	

	public static double HeuristicFunction(ITablutState state) {
		
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
			result +=500;
		
		//caso in cui il re sia in pericolo / caso in cui un nero possa mangiare il re
		if(isKingInDanger(state))
		//	if(state.getState().getTurn().equals(Turn.WHITE)) result+= 900;
		//	else result-=800;
		result-=800;
		
		//@Matteo valutazioni sul numero di pezzi
		result+=state.WhitesCount()*40;
		result-=state.BlacksCount()*25;
	//	result+=state.getFlow()*5;
		result+=state.getWhitePawnsInFlowDirection()*20;
		result-=state.getBlackPawnsInFlowDirection()*20;
		result+=state.getPawnsOnKingDiagonal()*30;
		result+=state.getPawnsOnKingDiagonal2()*10;
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
		else if((king[0]==5||king[0]==3)&&king[1]==1||king[1]==7) return true;
		else if((king[0]==1||king[0]==7)&&king[1]==5||king[1]==3) return true;
		
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


}
