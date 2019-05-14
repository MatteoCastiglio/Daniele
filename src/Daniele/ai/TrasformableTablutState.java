package Daniele.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;



import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class TrasformableTablutState implements ITablutState {


	private State state;
	@Override
	public String toString() {
		return "TablutState" + System.lineSeparator() + state + System.lineSeparator() + " nwhites=" + nwhites + ", nblacks=" + nblacks +" king=" +coordKing[0] + " " +coordKing[1];
	}

	//private Game game;																//contiene un gioco per ogni istanza??
	private Pawn[][] board;
	
	private int[] coordKing;
	


	public void setNwhites(int nwhites) {
		this.nwhites = nwhites;
	}



	public void setNblacks(int nblacks) {
		this.nblacks = nblacks;
	}

	public void setCoordKing(int[] coordKing) {
		this.coordKing = coordKing;
	}

	public void setWhitePawnsMoved(int whitePawnsMoved) {
		this.whitePawnsMoved = whitePawnsMoved;
	}

	
	// @Matteo aggiunto per controlli sui pezzi mangiati
	private  List<String> citadels =new  ArrayList<String>(Arrays.asList("a4","a5","a6","b5","d1","e1","f1","e2","i4","i5","i6","h5","d9","e9","f9","e8"));
	private int nwhites; // @Matteo numero di pezzi bianchi sulla scacchiera, mettendolo come propriet� si evita si calcolarlo dinamicamente
	private int nblacks; // @Matteo numero di pezzi neri sulla scacchiera, mettendolo come propriet� si evita si calcolarlo dinamicamente
	private int whitePawnsMoved;

	
	
	
	// this.strangeCitadels = new ArrayList<String>();

	public TrasformableTablutState(State state, int WhiteCounts,int  BlackCounts,int[] king,int whitesMoved) {
		this.state = state;
	//	this.game = new GameAshtonTablut(state,0, -1, "logs", "WHITE", "BLACK");
		this.board = state.getBoard();
		nwhites= WhiteCounts;
		nblacks= BlackCounts;
		whitePawnsMoved = whitesMoved;
		coordKing = king;
	}

	@Override
	public List<Pos> trasformState(DanieleAction action) {
		
		// return game.getNextState(state ,action);
		
		return getNextState(state ,action);
		
	}

	@Override
	public void trasformStateBack(DanieleAction a, List<Pos> pawnsRemoved) {
		
		// return game.getNextState(state ,action);
		switch(state.getTurn()){
			case WHITEWIN :{
			state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.KING);
			coordKing = new int[] {a.getRowFrom(),a.getColumnFrom()};
			state.setTurn(Turn.WHITE);
			break;		}
			case BLACKWIN : {
			state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.BLACK);
			state.setTurn(Turn.BLACK);
			for(int i = 0; i< pawnsRemoved.size(); i++) {
				state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.WHITE);
			nwhites++;}
			break;
			}
			case BLACK : {
			state.setTurn(Turn.WHITE);
			if(state.getPawn(a.getRowTo(),a.getColumnTo()).equals(Pawn.KING)) {
			state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.KING);
			coordKing = new int[] {a.getRowFrom(),a.getColumnFrom()};
			}
			else state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.WHITE);
			for(int i = 0; i< pawnsRemoved.size(); i++)
					{
				state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.BLACK);
				nblacks++;	}
			break; }
		
			case WHITE:  {
			state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.BLACK);
			state.setTurn(Turn.BLACK);
			for(int i = 0; i< pawnsRemoved.size(); i++) {
				state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.WHITE);
			nwhites++;
			}
			break;}
		//solo per completezza
		case DRAW:
			break;
		default:
			break;
		}

		state.setPawn(a.getRowTo(), a.getColumnTo(), Pawn.EMPTY);
		
		
		
	}
	
	@Override
	public List<DanieleAction> getTopLeftMoves() {
		List<DanieleAction> moves = new LinkedList<DanieleAction>();

			if(state.getTurn().equals(Turn.WHITE)) {	//MAX player
				for (int i = 0; i < this.board.length/2; i++) {
					for (int j = 0; j < this.board.length/2; j++) {
						//per ogni giocatore bianco ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.WHITE) || this.board[i][j].equals(Pawn.KING)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
		/*serve?*/				else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new DanieleAction(i, j, x, j));
							for(int x = i+1; x < this.board.length/2; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new DanieleAction(i, j,x, j)) ;
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new DanieleAction(i, j,i, x));
							for(int x = j+1; x < this.board.length/2; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new DanieleAction(i, j, i, x));
						}
					}
				}
			}
			else if(state.getTurn().equals(Turn.BLACK)) {	//MIN player
				for (int i = 0; i < this.board.length/2; i++) {
					for (int j = 0; j < this.board.length/2; j++) {
						//per ogni giocatore nero ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.BLACK)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j,x, j));
							for(int x = i+1; x < this.board.length/2; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j,x, j));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j , i, x));
							for(int x = j+1; x < this.board.length/2; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j, i, x));
						}
					}	
				}
			}

		return moves;
	}
	
	@Override
	public List<DanieleAction> getAllLegalMoves() {
		List<DanieleAction> moves = new LinkedList<DanieleAction>();

			if(state.getTurn().equals(Turn.WHITE)) {	//MAX player
				for (int i = 0; i < this.board.length; i++) {
					for (int j = 0; j < this.board.length; j++) {
						//per ogni giocatore bianco ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.WHITE) || this.board[i][j].equals(Pawn.KING)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
		/*serve?*/				else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new DanieleAction(i, j,x, j));
							for(int x = i+1; x < this.board.length; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new DanieleAction(i, j,x, j));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new DanieleAction(i, j,i, x));
							for(int x = j+1; x < this.board.length; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new DanieleAction(i, j,i, x));
						}
					}
				}
			}
			else if(state.getTurn().equals(Turn.BLACK)) {	//MIN player
				for (int i = 0; i < this.board.length; i++) {
					for (int j = 0; j < this.board.length; j++) {
						//per ogni giocatore nero ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.BLACK)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j,x, j));
							for(int x = i+1; x < this.board.length; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j,x, j));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j,i, x));
							for(int x = j+1; x < this.board.length; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new DanieleAction(i, j,i, x));
						}
					}	
				}
			}
		//Collections.reverse(moves);
		return moves;
	}
	
	public boolean isPawnAccampamento(int i, int j) {
		int middle = (this.board.length-1)/2;
		
		//parti di accampamenti sui bordi
		if( (i>=middle-1 && i<=middle+1) && (j==0 || j==this.board.length-1) ) return true;
		if( (i==0 || i==this.board.length-1) && (j>=middle-1 && j<=middle+1) ) return true;
		//parti di accampamenti interni
		if( i==middle && (j==1 || j==this.board.length-2) ) return true;
		if( (i==1 || i==this.board.length-2) && j==middle ) return true;
		
		//trono 														(non dovrebbe servire) -> si presuppone che quando il re si muove questo pawn diventi Pawn.THRONE
		//if( i==middle && j==middle ) return true;
		
		return false;
	}



	@Override
	public State getState() {
		return state;
	}

	//@Matteo la gestione andrebbe fatta come per il numero delle pedine
	@Override
	public int[] getCoordKing() {
		
		/*
		int coord[] = new int[2];
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board.length; j++) {
				if(this.board[i][j].equals(Pawn.KING)) {coord[0]=i; coord[1]=j; return coord;}
			}
		}
		*/
		return coordKing; //dovrebbe semrpe esserci il re, altrimenti la partita è conclusa
	}
	
	
	
// @Matteo questi metodi sono pi� o meno copiati dalla classe GameAshtonTablut poi vedremo meglioc ome metterli 

//@Matteo turn nelle action non serve a nulla, potremmo farci una nostra Action
	
	
private List<Pos> getNextState(State mystate, DanieleAction a) {

		List<Pos> p = null;
		Pawn pawn = state.getPawn(a.getRowFrom(), a.getColumnFrom());
		Pawn[][] newBoard = state.getBoard();
		// State newState = new State();
		// libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = Pawn.THRONE;
		} else {
			newBoard[a.getRowFrom()][a.getColumnFrom()] = Pawn.EMPTY;
		}
		
		// metto nel nuovo tabellone la pedina mossa
		newBoard[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		

		
		
		//@Matteo da riguardare
		if (pawn.equals(Pawn.WHITE)&&(a.getColumnFrom() == 4 || a.getRowFrom() == 4)) {
			whitePawnsMoved++;
		}
		if (pawn.equals(Pawn.WHITE)&&(a.getColumnTo() == 4 || a.getRowTo() == 4)) {
			whitePawnsMoved--;
		}

		if(pawn.equals(Pawn.KING))
		coordKing = new int[] {a.getRowTo(),a.getColumnTo()};
		
		
		if (state.getTurn().equals(Turn.WHITE)) {
			state.setTurn(State.Turn.BLACK);
			p = checkCaptureWhite(state,a);
			
			
		} else {
			state.setTurn(State.Turn.WHITE);
			p = checkCaptureBlack(state,a);
		}

		return p;
		
	
}
	
private List<Pos> checkCaptureBlack(State state, DanieleAction a) {
		
		
		// @Matteo tutti i controlli a prescindere si pu� fare meglio???
		
		List<Pos> p = new ArrayList<Pos>();
		p.addAll(this.checkCaptureBlackPawnRight(state, a));
		p.addAll(this.checkCaptureBlackPawnLeft(state, a));
		p.addAll(this.checkCaptureBlackPawnUp(state, a));
		p.addAll(this.checkCaptureBlackPawnDown(state, a));
		
		this.checkCaptureBlackKingRight(state, a);
		this.checkCaptureBlackKingLeft(state, a);
		this.checkCaptureBlackKingDown(state, a);
		this.checkCaptureBlackKingUp(state, a);
		return p;
}

private List<Pos> checkCaptureWhite(State state, DanieleAction a) {
	// controllo se mangio a destra
	
	List<Pos> p = new ArrayList<Pos>() ;
	if (a.getColumnTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
			|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
			|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
			|| (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))
			&& !(a.getColumnTo() + 2 == 8 && a.getRowTo() == 4)
			&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 0)
			&& !(a.getColumnTo() + 2 == 4 && a.getRowTo() == 8)
			&& !(a.getColumnTo() + 2 == 0 && a.getRowTo() == 4)))) {
		state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
		nblacks=nblacks-1;
		p.add(new Pos (a.getRowTo(), a.getColumnTo() + 1));
		
	}
	// controllo se mangio a sinistra
	if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
			|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
			|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
			|| (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
			&& !(a.getColumnTo() - 2 == 8 && a.getRowTo() == 4)
			&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 0)
			&& !(a.getColumnTo() - 2 == 4 && a.getRowTo() == 8)
			&& !(a.getColumnTo() - 2 == 0 && a.getRowTo() == 4)))) {
		state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		nblacks=nblacks-1;
		p.add(new Pos(a.getRowTo(), a.getColumnTo() - 1));
	}
	// controllo se mangio sopra
	if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
			&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
			|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
			|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
			|| (this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
			&& !(a.getColumnTo() == 8 && a.getRowTo() - 2 == 4)
			&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 0)
			&& !(a.getColumnTo() == 4 && a.getRowTo() - 2 == 8)
			&& !(a.getColumnTo() == 0 && a.getRowTo() - 2 == 4)))) {
		state.removePawn(a.getRowTo() - 1, a.getColumnTo());
		nblacks=nblacks-1;
		p.add(new Pos (a.getRowTo() -1 , a.getColumnTo() ));
	}
	// controllo se mangio sotto
	if (a.getRowTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
			&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
			|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
			|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
			|| (this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
			&& !(a.getColumnTo() == 8 && a.getRowTo() + 2 == 4)
			&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 0)
			&& !(a.getColumnTo() == 4 && a.getRowTo() + 2 == 8)
			&& !(a.getColumnTo() == 0 && a.getRowTo() + 2 == 4)))) {
		state.removePawn(a.getRowTo() + 1, a.getColumnTo());
		nblacks=nblacks-1;
		p.add(new Pos (a.getRowTo() + 1 , a.getColumnTo() ));
	}
	// controllo se ho vinto
	if (a.getRowTo() == 0 || a.getRowTo() == state.getBoard().length - 1 || a.getColumnTo() == 0
			|| a.getColumnTo() == state.getBoard().length - 1) {
		if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
			state.setTurn(State.Turn.WHITEWIN);

		}
	}


	return p;
}

private void checkCaptureBlackKingLeft(State state, DanieleAction a){
// ho il re sulla sinistra
	if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("K")) {
		// re sul trono
		if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")) {
			if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 3).equalsPawn("B")
					&& state.getPawn(5, 4).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);
	
			}
		}
		// re adiacente al trono
		if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")) {
			if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
			if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")) {
			if (state.getPawn(6, 4).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// sono fuori dalle zone del trono
		if (!state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e5")
				&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e6")
				&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("e4")
				&& !state.getBox(a.getRowTo(), a.getColumnTo() - 1).equals("f5")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
					|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
	}

}

private void checkCaptureBlackKingRight(State state, DanieleAction a){
	// ho il re sulla destra
	if (a.getColumnTo() < state.getBoard().length - 2
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("K"))) {
		// re sul trono
		if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
			if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
					&& state.getPawn(5, 4).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// re adiacente al trono
		if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")) {
			if (state.getPawn(2, 4).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")) {
			if (state.getPawn(5, 5).equalsPawn("B") && state.getPawn(6, 4).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")) {
			if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// sono fuori dalle zone del trono
		if (!state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("d5")
				&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e6")
				&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e4")
				&& !state.getBox(a.getRowTo(), a.getColumnTo() + 1).equals("e5")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
					|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
	}

}

private void checkCaptureBlackKingDown(State state, DanieleAction a){
	// ho il re sotto
	if (a.getRowTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("K")) {
	
		// re sul trono
		if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
			if (state.getPawn(5, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
					&& state.getPawn(4, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// re adiacente al trono
		if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")) {
			if (state.getPawn(3, 3).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")) {
			if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(5, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")) {
			if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// sono fuori dalle zone del trono
		if (!state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("d5")
				&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e4")
				&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("f5")
				&& !state.getBox(a.getRowTo() + 1, a.getColumnTo()).equals("e5")) {
			if (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
					|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
	}

}

private void checkCaptureBlackKingUp(State state, DanieleAction a){
	// ho il re sopra
	if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("K")) {
		// re sul trono
		if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
			if (state.getPawn(3, 4).equalsPawn("B") && state.getPawn(4, 5).equalsPawn("B")
					&& state.getPawn(4, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// re adiacente al trono
		if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e6")) {
			if (state.getPawn(5, 3).equalsPawn("B") && state.getPawn(5, 5).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")) {
			if (state.getPawn(4, 2).equalsPawn("B") && state.getPawn(3, 3).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if (state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")) {
			if (state.getPawn(4, 6).equalsPawn("B") && state.getPawn(3, 5).equalsPawn("B")) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		// sono fuori dalle zone del trono
		if (!state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("d5")
				&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e4")
				&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("f5")
				&& !state.getBox(a.getRowTo() - 1, a.getColumnTo()).equals("e5")) {
			if (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
					|| this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))) {
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
	}


}

private List<Pos> checkCaptureBlackPawnRight(State state, DanieleAction a)	{

	List<Pos> p = new ArrayList<Pos>() ;	
	if (a.getColumnTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
		if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			nwhites=nwhites-1;
			p.add(new Pos (a.getRowTo()  , a.getColumnTo() +1));
		}
		if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			nwhites=nwhites-1;
			p.add(new Pos (a.getRowTo()  , a.getColumnTo() +1));
		}
		if (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			nwhites=nwhites-1;
			p.add(new Pos (a.getRowTo()  , a.getColumnTo() +1));
		}
		if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			nwhites=nwhites-1;
			p.add(new Pos (a.getRowTo()  , a.getColumnTo() +1 ));
		}
		
	}
	
	return p;
}

private List<Pos> checkCaptureBlackPawnLeft(State state, DanieleAction a){
	//mangio a sinistra
	List<Pos> p = new ArrayList<Pos>() ;
	if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
			|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
			|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
			|| (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5"))))

	{
		state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		p.add(new Pos (a.getRowTo()  , a.getColumnTo() -1));
		// @Matteo AGGIUNTA
		nwhites=nwhites-1;
	}
	return p;
}

private List<Pos> checkCaptureBlackPawnUp(State state, DanieleAction a){
	// controllo se mangio sopra
	List<Pos> p = new ArrayList<Pos>() ;
	if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
			&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
			|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
			|| this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
			|| (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5"))))

	{
		state.removePawn(a.getRowTo()-1, a.getColumnTo());
		// @Matteo AGGIUNTA
		nwhites=nwhites-1;
		p.add(new Pos (a.getRowTo() -1 , a.getColumnTo() ));

	}
	return p;
}

private List<Pos> checkCaptureBlackPawnDown(State state, DanieleAction a){
	// controllo se mangio sotto
	List<Pos> p = new ArrayList<Pos>() ;
	if (a.getRowTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
			&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
			|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
			|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
			|| (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5"))))
	{
		state.removePawn(a.getRowTo()+1, a.getColumnTo());
		p.add(new Pos (a.getRowTo() +1 , a.getColumnTo() ));
		// @Matteo AGGIUNTA
		nwhites=nwhites- 1;
	
	}
	return p;
}

//@Matteo test in caso da cambiare
public int getWhitePawnsMoved()
{
	/*int res =0;
		for (int j = 2; j < 7; j++) 
			if(!this.board[4][j].equals(Pawn.WHITE)) res++;
		for (int j = 2; j < 7; j++) 
			if(!this.board[j][4].equals(Pawn.WHITE)) res++;
			
		return res;
	 */
	return whitePawnsMoved;
}

@Override
public int BlacksCount() {
	// TODO Auto-generated method stub
	return nblacks;
}

@Override
public int WhitesCount() {
	// TODO Auto-generated method stub
	return nwhites;
}


	@Override
	public int getPawnsOnKingDiagonal()
	{
		int res=0;
		if(coordKing[0]-1>=0)
		{
			if(coordKing[1]-1>=0)
				if(state.getPawn(coordKing[0]-1,coordKing[1]-1).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]-1,coordKing[1]-1).equals(Pawn.BLACK)) res--;
			if(coordKing[1]+1<=8)
				if(state.getPawn(coordKing[0]-1,coordKing[1]+1).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]-1,coordKing[1]+1).equals(Pawn.BLACK)) res--;
		}
		if(coordKing[0]+1<=8){
			if(coordKing[1]-1>=0)
				if(state.getPawn(coordKing[0]+1,coordKing[1]-1).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]+1,coordKing[1]-1).equals(Pawn.BLACK)) res--;
			if(coordKing[1]+1<=8)
				if(state.getPawn(coordKing[0]+1,coordKing[1]+1).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]+1,coordKing[1]+1).equals(Pawn.BLACK)) res--;
		}
		return res;
	}



	@Override
	public int getPawnsOnKingDiagonal2()
	{
		int res=0;
		if(coordKing[0]-2>=0)
		{
			if(coordKing[1]-2>=0)
				if(state.getPawn(coordKing[0]-2,coordKing[1]-2).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]-2,coordKing[1]-2).equals(Pawn.BLACK)) res--;
			if(coordKing[1]+2<=8)
				if(state.getPawn(coordKing[0]-2,coordKing[1]+2).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]-2,coordKing[1]+2).equals(Pawn.BLACK)) res--;
		}
		if(coordKing[0]+2<=8){
			if(coordKing[1]-2>=0)
				if(state.getPawn(coordKing[0]+2,coordKing[1]-2).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]+2,coordKing[1]-2).equals(Pawn.BLACK)) res--;
			if(coordKing[1]+2<=8)
				if(state.getPawn(coordKing[0]+2,coordKing[1]+2).equals(Pawn.WHITE)) res++;
				else if (state.getPawn(coordKing[0]+2,coordKing[1]+2).equals(Pawn.BLACK)) res--;
		}
		return res;
	}



	@Override
	public ITablutState getChildState(DanieleAction action) {
		// TODO Auto-generated method stub
		return null;
	}


}

class Pos {

 int col;

public Pos(int row, int col) {
	this.col = col;
	this.row = row;

}
int row;

}