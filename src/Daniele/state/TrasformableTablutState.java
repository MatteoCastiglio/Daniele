package Daniele.state;

import java.util.ArrayList;

import java.util.List;


import Daniele.state.AbstractTablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class TrasformableTablutState extends AbstractTablutState implements ITablutState {


	@Override
	public String toString() {
		return "TablutState" + System.lineSeparator() + state + System.lineSeparator() + " nwhites=" + nwhites + ", nblacks=" + nblacks + " king=" + coordKing[0] + " " + coordKing[1];
	}


	// this.strangeCitadels = new ArrayList<String>();

	public TrasformableTablutState(State state, int WhiteCounts, int BlackCounts, int[] king) {
		this.state = state;
		//	this.game = new GameAshtonTablut(state,0, -1, "logs", "WHITE", "BLACK");
		this.board = state.getBoard();
		nwhites = WhiteCounts;
		nblacks = BlackCounts;
		coordKing = king;
	}

	@Override
	public List<Pos> trasformState(DanieleAction action) {

		// return game.getNextState(state ,action);

		return getNextState(state, action);

	}

	@Override
	public void trasformStateBack(DanieleAction a, List<Pos> pawnsRemoved) {


		// return game.getNextState(state ,action);
		switch (state.getTurn()) {
			case WHITEWIN: {
				state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.KING);
				coordKing = new int[]{a.getRowFrom(), a.getColumnFrom()};
				state.setTurn(Turn.WHITE);
				for (int i = 0; i < pawnsRemoved.size(); i++) {
					state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.BLACK);
					nblacks++;
				}
				break;
			}
			case BLACKWIN: {
				state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.BLACK);
				state.setTurn(Turn.BLACK);
				for (int i = 0; i < pawnsRemoved.size(); i++) {
					state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.WHITE);
					nwhites++;
				}
				break;
			}
			case BLACK: {


				state.setTurn(Turn.WHITE);
				if (state.getPawn(a.getRowTo(), a.getColumnTo()).equals(Pawn.KING)) {
					state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.KING);
					coordKing = new int[]{a.getRowFrom(), a.getColumnFrom()};
				} else {
					state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.WHITE);
				}

				for (int i = 0; i < pawnsRemoved.size(); i++) {
					state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.BLACK);
					nblacks++;
				}
				break;
			}

			case WHITE: {
				state.setPawn(a.getRowFrom(), a.getColumnFrom(), Pawn.BLACK);
				state.setTurn(Turn.BLACK);
				for (int i = 0; i < pawnsRemoved.size(); i++) {
					state.setPawn(pawnsRemoved.get(i).row, pawnsRemoved.get(i).col, Pawn.WHITE);
					nwhites++;
				}
				break;
			}
			//solo per completezza
			case DRAW:
				break;
			default:
				break;
		}

		state.setPawn(a.getRowTo(), a.getColumnTo(), Pawn.EMPTY);


	}


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
		if (pawn.equals(Pawn.KING))
			coordKing = new int[]{a.getRowTo(), a.getColumnTo()};


		if (state.getTurn().equals(Turn.WHITE)) {
			state.setTurn(State.Turn.BLACK);
			p = checkCaptureWhite(state, a);


		} else {
			state.setTurn(State.Turn.WHITE);
			p = checkCaptureBlack(state, a);
		}

		return p;


	}

	private List<Pos> checkCaptureBlack(State state, DanieleAction a) {


		// @Matteo tutti i controlli a prescindere si pu� fare meglio???

		List<Pos> p = new ArrayList<>();
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

		List<Pos> p = new ArrayList<Pos>();
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
			nblacks = nblacks - 1;
			p.add(new Pos(a.getRowTo(), a.getColumnTo() + 1));

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
			nblacks = nblacks - 1;
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
			nblacks = nblacks - 1;
			p.add(new Pos(a.getRowTo() - 1, a.getColumnTo()));
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
			nblacks = nblacks - 1;
			p.add(new Pos(a.getRowTo() + 1, a.getColumnTo()));
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


	private List<Pos> checkCaptureBlackPawnRight(State state, DanieleAction a) {

		List<Pos> p = new ArrayList<Pos>();
		if (a.getColumnTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nwhites = nwhites - 1;
				p.add(new Pos(a.getRowTo(), a.getColumnTo() + 1));
			}
			if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nwhites = nwhites - 1;
				p.add(new Pos(a.getRowTo(), a.getColumnTo() + 1));
			}
			if (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nwhites = nwhites - 1;
				p.add(new Pos(a.getRowTo(), a.getColumnTo() + 1));
			}
			if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
				state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				nwhites = nwhites - 1;
				p.add(new Pos(a.getRowTo(), a.getColumnTo() + 1));
			}

		}

		return p;
	}

	private List<Pos> checkCaptureBlackPawnLeft(State state, DanieleAction a) {
		//mangio a sinistra
		List<Pos> p = new ArrayList<Pos>();
		if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
				|| state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
				|| this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
				|| (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5")))) {
			state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			p.add(new Pos(a.getRowTo(), a.getColumnTo() - 1));
			// @Matteo AGGIUNTA
			nwhites = nwhites - 1;
		}
		return p;
	}

	private List<Pos> checkCaptureBlackPawnUp(State state, DanieleAction a) {
		// controllo se mangio sopra
		List<Pos> p = new ArrayList<Pos>();
		if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
				|| state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
				|| this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
				|| (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() - 1, a.getColumnTo());
			// @Matteo AGGIUNTA
			nwhites = nwhites - 1;
			p.add(new Pos(a.getRowTo() - 1, a.getColumnTo()));

		}
		return p;
	}

	private List<Pos> checkCaptureBlackPawnDown(State state, DanieleAction a) {
		// controllo se mangio sotto
		List<Pos> p = new ArrayList<Pos>();
		if (a.getRowTo() < state.getBoard().length - 2
				&& state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
				|| state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
				|| this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
				|| (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5")))) {
			state.removePawn(a.getRowTo() + 1, a.getColumnTo());
			p.add(new Pos(a.getRowTo() + 1, a.getColumnTo()));
			// @Matteo AGGIUNTA
			nwhites = nwhites - 1;

		}
		return p;
	}


	@Override
	public ITablutState getChildState(DanieleAction action) {
		// TODO Auto-generated method stub
		return null;
	}

}