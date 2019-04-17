package Daniele;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.GameTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.exceptions.ActionException;
import it.unibo.ai.didattica.competition.tablut.exceptions.BoardException;
import it.unibo.ai.didattica.competition.tablut.exceptions.CitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingCitadelException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ClimbingException;
import it.unibo.ai.didattica.competition.tablut.exceptions.DiagonalException;
import it.unibo.ai.didattica.competition.tablut.exceptions.OccupitedException;
import it.unibo.ai.didattica.competition.tablut.exceptions.PawnException;
import it.unibo.ai.didattica.competition.tablut.exceptions.StopException;
import it.unibo.ai.didattica.competition.tablut.exceptions.ThroneException;

public class TablutState implements ITablutState{

	
	private State state;
	@Override
	public String toString() {
		return "TablutState" + System.lineSeparator() + state + System.lineSeparator() + " nwhites=" + nwhites + ", nblacks=" + nblacks ;
	}

	//private Game game;																//contiene un gioco per ogni istanza??
	private Pawn[][] board;
	
	private int[] coordKing;
	private int[] nextCoordKing;
	
	
	// @Matteo aggiunto per controlli sui pezzi mangiati
	private static List<String> citadels =new  ArrayList<String>(Arrays.asList("a4","a5","a6","b5","d1","e1","f1","e2","i4","i5","i6","h5","d9","e9","f9","e8"));
	
	private int nwhites; // @Matteo numero di pezzi bianchi sulla scacchiera, mettendolo come proprietà si evita si calcolarlo dinamicamente
	private int nblacks; // @Matteo numero di pezzi neri sulla scacchiera, mettendolo come proprietà si evita si calcolarlo dinamicamente
	private int nextStateWhites; // @Matteo comodi per aggiornare il valore sul nuovo stato
	private int nextStateBlacks; // @Matteo comodi per aggiornare il valore sul nuovo stato
	private int whitePawnsMoved;
	private int nextWhitePawnsMoved;
	
	
	
	// this.strangeCitadels = new ArrayList<String>();

	public TablutState(State state, int WhiteCounts,int  BlackCounts,int[] king,int whitesMoved) {
		this.state = state;
	//	this.game = new GameAshtonTablut(state,0, -1, "logs", "WHITE", "BLACK");
		this.board = state.getBoard();
		nwhites= WhiteCounts;
		nblacks= BlackCounts;
		whitePawnsMoved = whitesMoved;
		coordKing = king;
	}

	@Override
	public ITablutState getChildState(Action action) {
		//try {
		return getNextState(state ,action);
			//return new TablutState(game.checkMove(state, action));
			// @Matteo rifare il check è superfluo se si presuppone che le mosse siano tutte valide
		/*} catch (BoardException | ActionException | StopException | PawnException | DiagonalException
				| ClimbingException | ThroneException | OccupitedException | ClimbingCitadelException
				| CitadelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;														//se ritorna null allora la mossa Ã¨ sbagliata		-> ?????
		} */
	}

	
	@Override
	public List<Action> getTopLeftMoves() {
		List<Action> moves = new ArrayList<Action>();
		try {
			if(state.getTurn().equals(Turn.WHITE)) {	//MAX player
				for (int i = 0; i < this.board.length/2; i++) {
					for (int j = 0; j < this.board.length/2; j++) {
						//per ogni giocatore bianco ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.WHITE) || this.board[i][j].equals(Pawn.KING)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
		/*serve?*/				else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new Action(coord(i, j), coord(x, j), Turn.WHITE));
							for(int x = i+1; x < this.board.length/2; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new Action(coord(i, j), coord(x, j), Turn.WHITE));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.WHITE));
							for(int x = j+1; x < this.board.length/2; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.WHITE));
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
								else moves.add(new Action(coord(i, j), coord(x, j), Turn.BLACK));
							for(int x = i+1; x < this.board.length/2; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(x, j), Turn.BLACK));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.BLACK));
							for(int x = j+1; x < this.board.length/2; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.BLACK));
						}
					}	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return moves;
	}
	
	@Override
	public List<Action> getAllLegalMoves() {
		List<Action> moves = new ArrayList<Action>();
		try {
			if(state.getTurn().equals(Turn.WHITE)) {	//MAX player
				for (int i = 0; i < this.board.length; i++) {
					for (int j = 0; j < this.board.length; j++) {
						//per ogni giocatore bianco ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.WHITE) || this.board[i][j].equals(Pawn.KING)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
		/*serve?*/				else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new Action(coord(i, j), coord(x, j), Turn.WHITE));
							for(int x = i+1; x < this.board.length; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || isPawnAccampamento(x, j)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new Action(coord(i, j), coord(x, j), Turn.WHITE));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.WHITE));
							for(int x = j+1; x < this.board.length; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || isPawnAccampamento(i, x)) break; 	//non posso scavalcare o terminare su altre pedine o accampamento o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.WHITE));
						}
					}
				}
			} //@Matteo dovrebbe servire una correzione sui neri
			else if(state.getTurn().equals(Turn.BLACK)) {	//MIN player
				for (int i = 0; i < this.board.length; i++) {
					for (int j = 0; j < this.board.length; j++) {
						//per ogni giocatore nero ci si salva tutte le possibili azioni..
						if(this.board[i][j].equals(Pawn.BLACK)) {
							//..in verticale
							for(int x = i-1; x >= 0; x--)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(x, j), Turn.BLACK));
							for(int x = i+1; x < this.board.length; x++)
								if(!this.board[x][j].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(x, j), Turn.BLACK));
							//..in orizzontale
							for(int x = j-1; x >= 0; x--)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.BLACK));
							for(int x = j+1; x < this.board.length; x++)
								if(!this.board[i][x].equals(Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x))) break; 	//non posso scavalcare o terminare su altre pedine o (accampamento) o castello
								else moves.add(new Action(coord(i, j), coord(i, x), Turn.BLACK));
						}
					}	
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	private String coord(int i, int j) {
		return "" + (char)(j+97) + (i+1);
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
		return coordKing; //dovrebbe semrpe esserci il re, altrimenti la partita Ã¨ conclusa
	}
	
	
	
// @Matteo questi metodi sono più o meno copiati dalla classe GameAshtonTablut poi vedremo meglioc ome metterli 
	
private TablutState getNextState(State mystate, Action a) {
		State state = mystate.clone();
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
		
		
		//@Matteo da qui in poic odice modificato
		
		nextStateBlacks = nblacks;
		nextStateWhites = nwhites;
		nextWhitePawnsMoved = whitePawnsMoved;
		
		//@Matteo da riguardare
		if (pawn.equals(Pawn.WHITE)&&(a.getColumnFrom() == 4 || a.getRowFrom() == 4)) {
			nextWhitePawnsMoved++;
		}
		if (pawn.equals(Pawn.WHITE)&&(a.getColumnTo() == 4 || a.getRowTo() == 4)) {
			nextWhitePawnsMoved--;
		}

		if(pawn.equals(Pawn.KING))
		nextCoordKing = new int[] {a.getRowTo(),a.getColumnTo()};
		else
		nextCoordKing= coordKing;
		
		
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			state.setTurn(State.Turn.BLACK);
			state = checkCaptureWhite(state,a);
			
			
		} else {
			state.setTurn(State.Turn.WHITE);
			state = checkCaptureBlack(state,a);
		}
		
		// @Matteo qui manca controllo su pareggio per stato ripetuto
		return new TablutState(state,nextStateWhites,nextStateBlacks,nextCoordKing,nextWhitePawnsMoved);
		
		//@ Matteo decommentare per stampe -- parametro per debug mode ???
		/*
		TablutState tmp = new TablutState(state,nwhites,nblacks);
		System.out.println(tmp);
		System.out.println("Value: " + HeuristicTablut.HeuristicFunction(tmp));
		return tmp; 
		*/
}
	
private State checkCaptureBlack(State state, Action a) {
		
		
		// @Matteo tutti i controlli a prescindere si può fare meglio???
		this.checkCaptureBlackPawnRight(state, a);
		this.checkCaptureBlackPawnLeft(state, a);
		this.checkCaptureBlackPawnUp(state, a);
		this.checkCaptureBlackPawnDown(state, a);
		this.checkCaptureBlackKingRight(state, a);
		this.checkCaptureBlackKingLeft(state, a);
		this.checkCaptureBlackKingDown(state, a);
		this.checkCaptureBlackKingUp(state, a);
		return state;
}

private State checkCaptureWhite(State state, Action a) {
	// controllo se mangio a destra
	if (a.getColumnTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
					|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")
					|| state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
					|| (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))&&!(state.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B")) &&!(a.getColumnTo()+2==8&&a.getRowTo()==4)&&!(a.getColumnTo()+2==4&&a.getRowTo()==0)&&!(a.getColumnTo()+2==4&&a.getRowTo()==0)&&!(a.getColumnTo()+2==0&&a.getRowTo()==4)))) {
		state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
		// @Matteo AGGIUNTA
		nextStateBlacks=nextStateBlacks-1;

	}
	// controllo se mangio a sinistra
	if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
					|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
					|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
					|| (citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))&&!(state.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B")) &&!(a.getColumnTo()-2==8&&a.getRowTo()==4)&&!(a.getColumnTo()-2==4&&a.getRowTo()==0)&&!(a.getColumnTo()-2==4&&a.getRowTo()==0)&&!(a.getColumnTo()-2==0&&a.getRowTo()==4)))) {
		state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		// @Matteo AGGIUNTA
		nextStateBlacks=nextStateBlacks-1;

	}
	// controllo se mangio sopra
	if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
			&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
					|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
					|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
					|| (citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))&&!(state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B"))&&!(a.getColumnTo()==8&&a.getRowTo()-2==4)&&!(a.getColumnTo()==4&&a.getRowTo()-2==0)&&!(a.getColumnTo()==4&&a.getRowTo()-2==0)&&!(a.getColumnTo()==0&&a.getRowTo()-2==4)) )) {
		state.removePawn(a.getRowTo() - 1, a.getColumnTo());
		// @Matteo AGGIUNTA
		nextStateBlacks=nextStateBlacks-1;

	}
	// controllo se mangio sotto
	if (a.getRowTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
			&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
					|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
					|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
					|| (citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))&&!(state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B"))&&!(a.getColumnTo()==8&&a.getRowTo()+2==4)&&!(a.getColumnTo()==4&&a.getRowTo()+2==0)&&!(a.getColumnTo()==4&&a.getRowTo()+2==0)&&!(a.getColumnTo()==0&&a.getRowTo()+2==4)))) {
		state.removePawn(a.getRowTo() + 1, a.getColumnTo());
		// @Matteo AGGIUNTA
		nextStateBlacks=nextStateBlacks-1;

	}
	// controllo se ho vinto
	if (a.getRowTo() == 0 || a.getRowTo() == state.getBoard().length - 1 || a.getColumnTo() == 0
			|| a.getColumnTo() == state.getBoard().length - 1) {
		if (state.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
			state.setTurn(State.Turn.WHITEWIN);

		}
	}


	return state;
}

private State checkCaptureBlackKingLeft(State state, Action a){
	//ho il re sulla sinistra
	if (a.getColumnTo()>1&&state.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K"))
	{
		//re sul trono
		if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e5"))
		{
			if(state.getPawn(3, 4).equalsPawn("B")
					&& state.getPawn(4, 3).equalsPawn("B")
					&& state.getPawn(5, 4).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
		
			}
		}
		//re adiacente al trono
		if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e4"))
		{
			if(state.getPawn(2, 4).equalsPawn("B")
					&& state.getPawn(3, 3).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
	
			}
		}
		if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e6"))
		{
			if(state.getPawn(5, 3).equalsPawn("B")
					&& state.getPawn(6, 4).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if(state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("f5"))
		{
			if(state.getPawn(3, 5).equalsPawn("B")
					&& state.getPawn(5, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		//sono fuori dalle zone del trono
		if(!state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e5")
				&& !state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e6")
				&& !state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e4")
				&& !state.getBox(a.getRowTo(), a.getColumnTo()-1).equals("f5"))
		{
			if(state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
					|| citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()-2)))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}					
		}
	}		
	return state;
}

private State checkCaptureBlackKingRight(State state, Action a){
	//ho il re sulla destra
	if (a.getColumnTo()<state.getBoard().length-2&&(state.getPawn(a.getRowTo(),a.getColumnTo()+1).equalsPawn("K")))				
	{
		//re sul trono
		if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e5"))
		{
			if(state.getPawn(3, 4).equalsPawn("B")
					&& state.getPawn(4, 5).equalsPawn("B")
					&& state.getPawn(5, 4).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		//re adiacente al trono
		if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e4"))
		{
			if(state.getPawn(2, 4).equalsPawn("B")
					&& state.getPawn(3, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e6"))
		{
			if(state.getPawn(5, 5).equalsPawn("B")
					&& state.getPawn(6, 4).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if(state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("d5"))
		{
			if(state.getPawn(3, 3).equalsPawn("B")
					&& state.getPawn(3, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		//sono fuori dalle zone del trono
		if(!state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("d5")
				&& !state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e6")
				&& !state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e4")
				&& !state.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e5"))
		{
			if(state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
					|| TablutState.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo()+2)))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}					
		}
	}
	return state;
}

private State checkCaptureBlackKingDown(State state, Action a){
	//ho il re sotto
	if (a.getRowTo()<state.getBoard().length-2&&state.getPawn(a.getRowTo()+1,a.getColumnTo()).equalsPawn("K"))
	{
		//System.out.println("Ho il re sotto");
		//re sul trono
		if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e5"))
		{
			if(state.getPawn(5, 4).equalsPawn("B")
					&& state.getPawn(4, 5).equalsPawn("B")
					&& state.getPawn(4, 3).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		//re adiacente al trono
		if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e4"))
		{
			if(state.getPawn(3, 3).equalsPawn("B")
					&& state.getPawn(3, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("d5"))
		{
			if(state.getPawn(4, 2).equalsPawn("B")
					&& state.getPawn(5, 3).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		if(state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("f5"))
		{
			if(state.getPawn(4, 6).equalsPawn("B")
					&& state.getPawn(5, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);

			}
		}
		//sono fuori dalle zone del trono
		if(!state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("d5")
				&& !state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e4")
				&& !state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("f5")
				&& !state.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e5"))
		{
			if(state.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")
					|| TablutState.citadels.contains(state.getBox(a.getRowTo()+2, a.getColumnTo())))
			{
				state.setTurn(State.Turn.BLACKWIN);
				
			}					
		}			
	}		
	return state;
}

private State checkCaptureBlackKingUp(State state, Action a){
	//ho il re sopra
	if (a.getRowTo()>1&&state.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K"))
	{
		//re sul trono
		if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e5"))
		{
			if(state.getPawn(3, 4).equalsPawn("B")
					&& state.getPawn(4, 5).equalsPawn("B")
					&& state.getPawn(4, 3).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
			;
			}
		}
		//re adiacente al trono
		if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e6"))
		{
			if(state.getPawn(5, 3).equalsPawn("B")
					&& state.getPawn(5, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
				
			}
		}
		if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("d5"))
		{
			if(state.getPawn(4, 2).equalsPawn("B")
					&& state.getPawn(3, 3).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
			
			}
		}
		if(state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("f5"))
		{
			if(state.getPawn(4, 4).equalsPawn("B")
					&& state.getPawn(3, 5).equalsPawn("B"))
			{
				state.setTurn(State.Turn.BLACKWIN);
				
			}
		}
		//sono fuori dalle zone del trono
		if(!state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("d5")
				&& !state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e4")
				&& !state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("f5")
				&& !state.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e5"))
		{
			if(state.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")
					|| TablutState.citadels.contains(state.getBox(a.getRowTo()-2, a.getColumnTo())))
			{
				state.setTurn(State.Turn.BLACKWIN);
				
			}					
		}	
	}
	return state;
}

private State checkCaptureBlackPawnRight(State state, Action a)	{
	//mangio a destra
	if (a.getColumnTo() < state.getBoard().length - 2 && state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W"))
	{
		if(state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B"))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			
		}
		if(state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T"))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);

		}
		if(TablutState.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2)))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);

		}
		if(state.getBox(a.getRowTo(), a.getColumnTo()+2).equals("e5"))
		{
			state.removePawn(a.getRowTo(), a.getColumnTo() + 1);

		}
		
	}
	
	return state;
}

private State checkCaptureBlackPawnLeft(State state, Action a){
	//mangio a sinistra
	if (a.getColumnTo() > 1
			&& state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
			&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
					|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
					|| TablutState.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
					|| (state.getBox(a.getRowTo(), a.getColumnTo()-2).equals("e5"))))
	{
		state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
		// @Matteo AGGIUNTA
		nextStateWhites=nextStateWhites-1;
	}
	return state;
}

private State checkCaptureBlackPawnUp(State state, Action a){
	// controllo se mangio sopra
	if (a.getRowTo() > 1
			&& state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
			&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
					|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
					|| TablutState.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
					|| (state.getBox(a.getRowTo()-2, a.getColumnTo()).equals("e5"))))
	{
		state.removePawn(a.getRowTo()-1, a.getColumnTo());
		// @Matteo AGGIUNTA
		nextStateWhites=nextStateWhites-1;

	}
	return state;
}

private State checkCaptureBlackPawnDown(State state, Action a){
	// controllo se mangio sotto
	if (a.getRowTo() < state.getBoard().length - 2
			&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
			&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
					|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
					|| TablutState.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
					|| (state.getBox(a.getRowTo()+2, a.getColumnTo()).equals("e5"))))
	{
		state.removePawn(a.getRowTo()+1, a.getColumnTo());
		// @Matteo AGGIUNTA
		nextStateWhites=nextStateWhites-1;
	
	}
	return state;
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

}
