package Daniele.state;

import java.util.List;


import Daniele.state.AbstractTablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;



public class TablutState extends AbstractTablutState implements ITablutState {

	




	private int[] nextCoordKing;
	private int nextStateWhites; // @Matteo comodi per aggiornare il valore sul nuovo stato
	private int nextStateBlacks; // @Matteo comodi per aggiornare il valore sul nuovo stato




	// this.strangeCitadels = new ArrayList<String>();

	public TablutState(State state, int WhiteCounts,int  BlackCounts,int[] king) {
		this.state = state;
		this.board = state.getBoard();
		nwhites= WhiteCounts;
		nblacks= BlackCounts;
		coordKing = king;

	}

	@Override
	public ITablutState getChildState(DanieleAction action) {

		// return game.getNextState(state ,action);

		return getNextState(state ,action);

	}






	// @Matteo questi metodi sono pi� o meno copiati dalla classe GameAshtonTablut poi vedremo meglioc ome metterli 

	//@Matteo turn nelle action non serve a nulla, potremmo farci una nostra Action


	private ITablutState getNextState(State mystate, DanieleAction a) {
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
		if(pawn.equals(Pawn.KING))
			nextCoordKing = new int[] {a.getRowTo(),a.getColumnTo()};
		else
			nextCoordKing= coordKing;

		//@Matteo da qui in poic odice modificato
		nextStateBlacks = nblacks;
		nextStateWhites = nwhites;
		



		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			state.setTurn(State.Turn.BLACK);
			state = checkCaptureWhite(state,a);


		} else {
			state.setTurn(State.Turn.WHITE);
			state = checkCaptureBlack(state,a);
		}

		return new TrasformableTablutState(state,nextStateWhites,nextStateBlacks,nextCoordKing);

		//@ Matteo decommentare per stampe -- parametro per debug mode ???
		/*
		TablutState tmp = new TablutState(state,nwhites,nblacks);
		System.out.println(tmp);
		System.out.println("Value: " + HeuristicTablut.HeuristicFunction(tmp));
		return tmp; 
		 */
	}

	private State checkCaptureBlack(State state, DanieleAction a) {


		// @Matteo tutti i controlli a prescindere si pu� fare meglio???
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

	private State checkCaptureWhite(State state, DanieleAction a) {
		// controllo se mangio a destra
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
			nextStateBlacks=nextStateBlacks-1;
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
			nextStateBlacks=nextStateBlacks-1;
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
			nextStateBlacks=nextStateBlacks-1;
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



	private State checkCaptureBlackPawnRight(State state, DanieleAction a)	{

		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nextStateWhites=nextStateWhites-1;
			}
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nextStateWhites=nextStateWhites-1;
			}
			if (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nextStateWhites=nextStateWhites-1;
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nextStateWhites=nextStateWhites-1;
			}

		}

		return state;
	}

	private State checkCaptureBlackPawnLeft(State state, DanieleAction a){
		//mangio a sinistra
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
						|| (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5"))))

		{
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			// @Matteo AGGIUNTA
			nextStateWhites=nextStateWhites-1;
		}
		return state;
	}

	private State checkCaptureBlackPawnUp(State state, DanieleAction a){
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5"))))

		{
			state.removePawn(a.getRowTo()-1, a.getColumnTo());
			// @Matteo AGGIUNTA
			nextStateWhites=nextStateWhites-1;

		}
		return state;
	}

	private State checkCaptureBlackPawnDown(State state, DanieleAction a){
		// controllo se mangio sotto
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
						|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
						|| (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5"))))
		{
			state.removePawn(a.getRowTo()+1, a.getColumnTo());
			// @Matteo AGGIUNTA
			nextStateWhites=nextStateWhites-1;

		}
		return state;
	}


	

	












	@Override
	public List<Pos> trasformState(DanieleAction action) {
		return null;

	}



	@Override
	public void trasformStateBack(DanieleAction a, List<Pos> pawnsRemoved) {
		// TODO Auto-generated method stub

	}






}