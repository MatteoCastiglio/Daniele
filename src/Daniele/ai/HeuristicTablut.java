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
		result+=state.WhitesCount()*10;
		result-=state.BlacksCount()*5;
		result+=state.getWhitePawnsMoved()*5;
		result+=state.getPawnsOnKingDiagonal()*30;
		result+=state.getPawnsOnKingDiagonal2()*20;
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
		return false;
		//@Matteo mancano alcuni accampamenti

			
	
		/*
		//1) il re è nel castello (4 nere lo circondano) - nota: la cattura è attiva
		if(state.getState().getPawn(4, 4).equals(Pawn.KING)) {
			int configurazione = 0;
			int possibileCattura = 0;
			
			if(state.getState().getPawn(3, 4).equals(Pawn.BLACK)) possibileCattura++;		//N up
			else configurazione = 1;
			if(state.getState().getPawn(4, 3).equals(Pawn.BLACK)) possibileCattura++;		//N left
			else configurazione = 2;
			if(state.getState().getPawn(5, 4).equals(Pawn.BLACK)) possibileCattura++;		//N down
			else configurazione = 3;
			if(state.getState().getPawn(4, 5).equals(Pawn.BLACK)) possibileCattura++;		//N right
			else configurazione = 4;
			
			if(possibileCattura!=3)
				return false;
			
			switch(configurazione) {
			case 1:
				if(!state.getState().getPawn(3, 4).equals(Pawn.EMPTY)) return false;			//c'è uno ostacolo nel percorso
				for(int i=2; i>=0; i--)
					if(state.getState().getPawn(i, 4).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(i, 4).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				return false;																	//tutto il percorso è libero
			case 2:
				if(!state.getState().getPawn(4, 3).equals(Pawn.EMPTY)) return false;			//c'è uno ostacolo nel percorso
				for(int i=2; i>=0; i--)
					if(state.getState().getPawn(4, i).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(4, i).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				return false;																	//tutto il percorso è libero
			case 3:
				if(!state.getState().getPawn(5, 4).equals(Pawn.EMPTY)) return false;			//c'è uno ostacolo nel percorso
				for(int i=6; i<9; i++)
					if(state.getState().getPawn(i, 4).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(i, 4).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				return false;																	//tutto il percorso è libero
			case 4:
				if(!state.getState().getPawn(4, 5).equals(Pawn.EMPTY)) return false;			//c'è uno ostacolo nel percorso
				for(int i=6; i<9; i++)
					if(state.getState().getPawn(4, i).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(4, i).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				return false;																	//tutto il percorso è libero
			default: return false;
			}
		}
		
	//2) il re è adiacente al castello (3 nere lo circondano) - nota: la cattura è attiva
		if(state.getState().getPawn(3, 4).equals(Pawn.KING) && state.getState().getPawn(2, 4).equals(Pawn.BLACK)) {					//KING up
			if(state.getState().getPawn(3, 3).equals(Pawn.BLACK) && state.getState().getPawn(3, 5).equals(Pawn.EMPTY)) {
				for(int i=6; i<9; i++)
					if(state.getState().getPawn(3, i).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(3, i).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			else if(state.getState().getPawn(3, 5).equals(Pawn.BLACK) && state.getState().getPawn(3, 3).equals(Pawn.EMPTY)) {
				for(int i=2; i>=0; i--)
					if(state.getState().getPawn(3, i).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(3, i).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			return false;
		}
		else if(state.getState().getPawn(5, 4).equals(Pawn.KING) && state.getState().getPawn(6, 4).equals(Pawn.BLACK)) {			//KING down
			if(state.getState().getPawn(5, 3).equals(Pawn.BLACK) && state.getState().getPawn(5, 5).equals(Pawn.EMPTY)) {
				for(int i=6; i<9; i++)
					if(state.getState().getPawn(5, i).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(5, i).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			else if(state.getState().getPawn(5, 5).equals(Pawn.BLACK) && state.getState().getPawn(5, 3).equals(Pawn.EMPTY)) {
				for(int i=2; i>=0; i--)
					if(state.getState().getPawn(5, i).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(5, i).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			return false;
		}
		else if(state.getState().getPawn(4, 3).equals(Pawn.KING) && state.getState().getPawn(4, 2).equals(Pawn.BLACK)) {			//KING left
			if(state.getState().getPawn(3, 3).equals(Pawn.BLACK) && state.getState().getPawn(5, 3).equals(Pawn.EMPTY)) {
				for(int i=6; i<9; i++)
					if(state.getState().getPawn(i, 3).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(i, 3).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			else if(state.getState().getPawn(5, 3).equals(Pawn.BLACK) && state.getState().getPawn(3, 3).equals(Pawn.EMPTY)) {
				for(int i=2; i>=0; i--)
					if(state.getState().getPawn(i, 3).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(i, 3).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			return false;
		}
		else if(state.getState().getPawn(4, 5).equals(Pawn.KING) && state.getState().getPawn(4, 6).equals(Pawn.BLACK)) {			//KING right
			if(state.getState().getPawn(3, 5).equals(Pawn.BLACK) && state.getState().getPawn(5, 5).equals(Pawn.EMPTY)) {
				for(int i=6; i<9; i++)
					if(state.getState().getPawn(i, 5).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(i, 5).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			else if(state.getState().getPawn(5, 5).equals(Pawn.BLACK) && state.getState().getPawn(3, 5).equals(Pawn.EMPTY)) {
				for(int i=2; i>=0; i--)
					if(state.getState().getPawn(i, 5).equals(Pawn.BLACK)) return true;			//c'è un N col percorso libero
					else if(!state.getState().getPawn(i, 5).equals(Pawn.EMPTY)) return false;	//c'è uno ostacolo nel percorso
				//tutto il percorso è libero -> il re non è in pericolo in questa configurazione
			}
			return false;
		}
		
	//3) re adiacente all'accampamento (è lo stesso per il cavaliere)
		if (state.getState().getPawn(2, 4).equals(Pawn.KING)) return isWhiteInDangerNearCamp(2, 4);
		if (state.getState().getPawn(4, 2).equals(Pawn.KING)) return isWhiteInDangerNearCamp(4, 2);
		if (state.getState().getPawn(6, 4).equals(Pawn.KING)) return isWhiteInDangerNearCamp(6, 4);
		if (state.getState().getPawn(4, 6).equals(Pawn.KING)) return isWhiteInDangerNearCamp(4, 6);
		
		return false;
		*/
	}

	private static boolean isWhiteInDangerNearCamp(int i, int j) {
		// TODO Auto-generated method stub
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
