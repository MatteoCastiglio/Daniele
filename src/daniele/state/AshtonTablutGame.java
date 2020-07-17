package daniele.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unibo.ai.didattica.competition.tablut.domain.State;

public class AshtonTablutGame implements ITablutGame{

	private List<String> citadels = new ArrayList<>(Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8"));

    @Override
	public State checkCaptureBlackKingLeft(State state, DanieleAction a) {
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
        return state;
    }
    @Override
    public State checkCaptureBlackKingRight(State state, DanieleAction a) {
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
        return state;
    }
    @Override
    public State checkCaptureBlackKingDown(State state, DanieleAction a) {
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
        return state;
    }
    @Override
    public State checkCaptureBlackKingUp(State state, DanieleAction a) {
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

        return state;
    }
    @Override
    public List<PawnPosition> checkCaptureWhite(State state, DanieleAction a) {
        // controllo se mangio a destra

        List<PawnPosition> p = new ArrayList<PawnPosition>();
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
  
            p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() + 1));

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

            p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() - 1));
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
            p.add(new PawnPosition(a.getRowTo() - 1, a.getColumnTo()));
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
            p.add(new PawnPosition(a.getRowTo() + 1, a.getColumnTo()));
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
    @Override
    public List<PawnPosition> checkCaptureBlackPawnRight(State state, DanieleAction a) {

        List<PawnPosition> p = new ArrayList<PawnPosition>();
        if (a.getColumnTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W")) {
            if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
                p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() + 1));
            }
            if (state.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("T")) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
                p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() + 1));
            }
            if (this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() + 2))) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
                p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() + 1));
            }
            if (state.getBox(a.getRowTo(), a.getColumnTo() + 2).equals("e5")) {
                state.removePawn(a.getRowTo(), a.getColumnTo() + 1);
                p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() + 1));
            }

        }

        return p;
    }
    @Override
    public List<PawnPosition> checkCaptureBlackPawnLeft(State state, DanieleAction a) {
        List<PawnPosition> p = new ArrayList<PawnPosition>();
        if (a.getColumnTo() > 1 && state.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
                && (state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
                || state.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("T")
                || this.citadels.contains(state.getBox(a.getRowTo(), a.getColumnTo() - 2))
                || (state.getBox(a.getRowTo(), a.getColumnTo() - 2).equals("e5")))) {
            state.removePawn(a.getRowTo(), a.getColumnTo() - 1);
            p.add(new PawnPosition(a.getRowTo(), a.getColumnTo() - 1));
        }
        return p;
    }
    @Override
    public List<PawnPosition> checkCaptureBlackPawnUp(State state, DanieleAction a) {
        List<PawnPosition> p = new ArrayList<PawnPosition>();
        if (a.getRowTo() > 1 && state.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
                && (state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
                || state.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("T")
                || this.citadels.contains(state.getBox(a.getRowTo() - 2, a.getColumnTo()))
                || (state.getBox(a.getRowTo() - 2, a.getColumnTo()).equals("e5")))) {
            state.removePawn(a.getRowTo() - 1, a.getColumnTo());
            // @Matteo AGGIUNTA
            p.add(new PawnPosition(a.getRowTo() - 1, a.getColumnTo()));

        }
        return p;
    }
    @Override
    public List<PawnPosition> checkCaptureBlackPawnDown(State state, DanieleAction a) {
        List<PawnPosition> p = new ArrayList<PawnPosition>();
        if (a.getRowTo() < state.getBoard().length - 2
                && state.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
                && (state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
                || state.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("T")
                || this.citadels.contains(state.getBox(a.getRowTo() + 2, a.getColumnTo()))
                || (state.getBox(a.getRowTo() + 2, a.getColumnTo()).equals("e5")))) {
            state.removePawn(a.getRowTo() + 1, a.getColumnTo());
            p.add(new PawnPosition(a.getRowTo() + 1, a.getColumnTo()));

        }
        return p;
    }



	
	
	
}
