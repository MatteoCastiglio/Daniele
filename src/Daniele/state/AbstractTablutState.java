package Daniele.state;

import Daniele.state.DanieleAction;
import Daniele.state.ITablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public  abstract class AbstractTablutState implements ITablutState {

    protected State state;


    //private Game game;																//contiene un gioco per ogni istanza??
    protected State.Pawn[][] board;

    protected int[] coordKing;
    protected int nwhites; // @Matteo numero di pezzi bianchi sulla scacchiera, mettendolo come propriet� si evita si calcolarlo dinamicamente
    protected int nblacks; // @Matteo numero di pezzi neri sulla scacchiera, mettendolo come propriet� s

    protected List<String> citadels = new ArrayList<>(Arrays.asList("a4", "a5", "a6", "b5", "d1", "e1", "f1", "e2", "i4", "i5", "i6", "h5", "d9", "e9", "f9", "e8"));


    public void setNwhites(int nwhites) {
        this.nwhites = nwhites;
    }


    public void setNblacks(int nblacks) {
        this.nblacks = nblacks;
    }

    public void setCoordKing(int[] coordKing) {
        this.coordKing = coordKing;
    }



    public List<DanieleAction> getTopLeftMoves() {
        List<DanieleAction> moves = new LinkedList<DanieleAction>();

        if (state.getTurn().equals(State.Turn.WHITE)) {    //MAX player
            for (int i = 0; i < this.board.length / 2; i++) {
                for (int j = 0; j < this.board.length / 2; j++) {
                    //per ogni giocatore bianco ci si salva tutte le possibili azioni..
                    if (this.board[i][j].equals(State.Pawn.WHITE) || this.board[i][j].equals(State.Pawn.KING)) {
                        //..in verticale
                        for (int x = i - 1; x >= 0; x--)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || isPawnAccampamento(x, j))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                                /*serve?*/
                            else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new DanieleAction(i, j, x, j));
                        for (int x = i + 1; x < this.board.length / 2; x++)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || isPawnAccampamento(x, j))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                            else moves.add(new DanieleAction(i, j, x, j));
                        //..in orizzontale
                        for (int x = j - 1; x >= 0; x--)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || isPawnAccampamento(i, x))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                            else moves.add(new DanieleAction(i, j, i, x));
                        for (int x = j + 1; x < this.board.length / 2; x++)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || isPawnAccampamento(i, x))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                            else moves.add(new DanieleAction(i, j, i, x));
                    }
                }
            }
        } else if (state.getTurn().equals(State.Turn.BLACK)) {    //MIN player
            for (int i = 0; i < this.board.length / 2; i++) {
                for (int j = 0; j < this.board.length / 2; j++) {
                    //per ogni giocatore nero ci si salva tutte le possibili azioni..
                    if (this.board[i][j].equals(State.Pawn.BLACK)) {
                        //..in verticale
                        for (int x = i - 1; x >= 0; x--)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else moves.add(new DanieleAction(i, j, x, j));
                        for (int x = i + 1; x < this.board.length / 2; x++)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else moves.add(new DanieleAction(i, j, x, j));
                        //..in orizzontale
                        for (int x = j - 1; x >= 0; x--)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else moves.add(new DanieleAction(i, j, i, x));
                        for (int x = j + 1; x < this.board.length / 2; x++)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
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

        if (state.getTurn().equals(State.Turn.WHITE)) {    //MAX player
            for (int i = 0; i < this.board.length; i++) {
                for (int j = 0; j < this.board.length; j++) {
                    //per ogni giocatore bianco ci si salva tutte le possibili azioni..
                    if (this.board[i][j].equals(State.Pawn.WHITE) || this.board[i][j].equals(State.Pawn.KING)) {
                        //..in verticale
                        for (int x = i - 1; x >= 0; x--)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || isPawnAccampamento(x, j))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                                /*serve?*/
                            else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new DanieleAction(i, j, x, j));
                        for (int x = i + 1; x < this.board.length; x++)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || isPawnAccampamento(x, j))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                            else moves.add(new DanieleAction(i, j, x, j));
                        //..in orizzontale
                        for (int x = j - 1; x >= 0; x--)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || isPawnAccampamento(i, x))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                            else moves.add(new DanieleAction(i, j, i, x));
                        for (int x = j + 1; x < this.board.length; x++)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || isPawnAccampamento(i, x))
                                break;    //non posso scavalcare o terminare su altre pedine o accampamento o castello
                            else moves.add(new DanieleAction(i, j, i, x));
                    }
                }
            }
        } else if (state.getTurn().equals(State.Turn.BLACK)) {    //MIN player
            for (int i = 0; i < this.board.length; i++) {
                for (int j = 0; j < this.board.length; j++) {
                    //per ogni giocatore nero ci si salva tutte le possibili azioni..
                    if (this.board[i][j].equals(State.Pawn.BLACK)) {
                        //..in verticale
                        for (int x = i - 1; x >= 0; x--)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else if (x != 0 && (j != 0 || j != 8)) moves.add(new DanieleAction(i, j, x, j));
                        for (int x = i + 1; x < this.board.length; x++)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(x, j)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else if (x != 8 && (j != 0 || j != 8)) moves.add(new DanieleAction(i, j, x, j));
                        //..in orizzontale
                        for (int x = j - 1; x >= 0; x--)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else if (x != 0 && (i != 0 || i != 8)) moves.add(new DanieleAction(i, j, i, x));
                        for (int x = j + 1; x < this.board.length; x++)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || (!isPawnAccampamento(i, j) && isPawnAccampamento(i, x)))
                                break;    //non posso scavalcare o terminare su altre pedine o (accampamento) o castello
                            else if (x != 8 && (i != 0 || i != 8)) moves.add(new DanieleAction(i, j, i, x));
                    }
                }
            }
        }
        //Collections.reverse(moves);
        return moves;
    }

    public boolean isPawnAccampamento(int i, int j) {
        int middle = (this.board.length - 1) / 2;

        //parti di accampamenti sui bordi
        if ((i >= middle - 1 && i <= middle + 1) && (j == 0 || j == this.board.length - 1)) return true;
        if ((i == 0 || i == this.board.length - 1) && (j >= middle - 1 && j <= middle + 1)) return true;
        //parti di accampamenti interni
        if (i == middle && (j == 1 || j == this.board.length - 2)) return true;
        if ((i == 1 || i == this.board.length - 2) && j == middle) return true;

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

    @Override
    public String toString() {
        return "TablutState" + System.lineSeparator() + state + System.lineSeparator() + " nwhites=" + nwhites + ", nblacks=" + nblacks + " king=" + coordKing[0] + " " + coordKing[1];
    }

    protected State checkCaptureBlackKingLeft(State state, DanieleAction a) {
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

    protected State checkCaptureBlackKingRight(State state, DanieleAction a) {
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

    protected State checkCaptureBlackKingDown(State state, DanieleAction a) {
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

    protected State checkCaptureBlackKingUp(State state, DanieleAction a) {
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


    public double getFlow() {
        //	4|1
        //	3|2

        double res = 0;
        //troppo pesante
//		int n1W=0, n2W=0, n3W=0, n4W=0;
//		int n1B=0, n2B=0, n3B=0, n4B=0;
        int n1 = 0, n2 = 0, n3 = 0, n4 = 0;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
//				if(this.board[i][j].equals(Pawn.BLACK)) n4B++;
//				else if(this.board[i][j].equals(Pawn.WHITE)) n4W++;
                if (!this.board[i][j].equals(State.Pawn.EMPTY)) n4++;
            }
            for (int j = 5; j < 9; j++) {
                if (!this.board[i][j].equals(State.Pawn.EMPTY)) n1++;
            }
        }
        for (int i = 5; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                if (!this.board[i][j].equals(State.Pawn.EMPTY)) n3++;
            }
            for (int j = 5; j < 9; j++) {
                if (!this.board[i][j].equals(State.Pawn.EMPTY)) n2++;
            }
        }

//		int asse14=0, asse12=0, asse23=0, asse34=0;
//		if(coordKing[0]==4 && coordKing[1]>=0 && coordKing[1]<=3) asse34++;
//		if(coordKing[0]==4 && coordKing[1]>=5 && coordKing[1]<=8) asse12++;
//		if(coordKing[1]==4 && coordKing[0]>=0 && coordKing[0]<=3) asse14++;
//		if(coordKing[1]==4 && coordKing[0]>=0 && coordKing[0]<=3) asse23++;

        int max = Math.max(n1, n2);
        max = Math.max(max, n3);
        max = Math.max(max, n4);
        if (max == n1) res = n1 + (n2 > n4 ? (n2 + 0.5 * n4) : (0.5 * n2 + n4));
        if (max == n2) res = n2 + (n1 > n3 ? (n1 + 0.5 * n3) : (0.5 * n1 + n3));
        if (max == n3) res = n3 + (n2 > n4 ? (n2 + 0.5 * n4) : (0.5 * n2 + n4));
        if (max == n4) res = n4 + (n1 > n3 ? (n1 + 0.5 * n3) : (0.5 * n1 + n3));

        return res;

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
    public int getPawnsOnKingDiagonal() {
        int res = 0;
        if (coordKing[0] - 1 >= 0) {
            if (coordKing[1] - 1 >= 0)
                if (state.getPawn(coordKing[0] - 1, coordKing[1] - 1).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] - 1, coordKing[1] - 1).equals(State.Pawn.BLACK)) res--;
            if (coordKing[1] + 1 <= 8)
                if (state.getPawn(coordKing[0] - 1, coordKing[1] + 1).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] - 1, coordKing[1] + 1).equals(State.Pawn.BLACK)) res--;
        }
        if (coordKing[0] + 1 <= 8) {
            if (coordKing[1] - 1 >= 0)
                if (state.getPawn(coordKing[0] + 1, coordKing[1] - 1).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] + 1, coordKing[1] - 1).equals(State.Pawn.BLACK)) res--;
            if (coordKing[1] + 1 <= 8)
                if (state.getPawn(coordKing[0] + 1, coordKing[1] + 1).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] + 1, coordKing[1] + 1).equals(State.Pawn.BLACK)) res--;
        }
        return res;
    }


    @Override
    public int getPawnsOnKingDiagonal2() {
        int res = 0;
        if (coordKing[0] - 2 >= 0) {
            if (coordKing[1] - 2 >= 0)
                if (state.getPawn(coordKing[0] - 2, coordKing[1] - 2).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] - 2, coordKing[1] - 2).equals(State.Pawn.BLACK)) res--;
            if (coordKing[1] + 2 <= 8)
                if (state.getPawn(coordKing[0] - 2, coordKing[1] + 2).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] - 2, coordKing[1] + 2).equals(State.Pawn.BLACK)) res--;
        }
        if (coordKing[0] + 2 <= 8) {
            if (coordKing[1] - 2 >= 0)
                if (state.getPawn(coordKing[0] + 2, coordKing[1] - 2).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] + 2, coordKing[1] - 2).equals(State.Pawn.BLACK)) res--;
            if (coordKing[1] + 2 <= 8)
                if (state.getPawn(coordKing[0] + 2, coordKing[1] + 2).equals(State.Pawn.WHITE)) res++;
                else if (state.getPawn(coordKing[0] + 2, coordKing[1] + 2).equals(State.Pawn.BLACK)) res--;
        }
        return res;
    }

    @Override
    public int[] getPawnsInFlowDirection() {

        int whitePawnsOnflow = 0;
        int blackPawnsOnFlow = 0;
        if (coordKing[0] < 4) {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 9; j++) {
                    if (state.getPawn(i, j).equals(State.Pawn.WHITE))
                        whitePawnsOnflow++;
                    else if (state.getPawn(i, j).equals(State.Pawn.BLACK))
                        blackPawnsOnFlow++;
                }
        } else if (coordKing[0] > 4) {
            for (int i = 5; i < 9; i++)
                for (int j = 0; j < 9; j++) {
                    if (state.getPawn(i, j).equals(State.Pawn.WHITE))
                        whitePawnsOnflow++;
                    else if (state.getPawn(i, j).equals(State.Pawn.BLACK))
                        blackPawnsOnFlow++;
                }

        }
        if (coordKing[1] < 4) {
            for (int i = 0; i < 9; i++)
                for (int j = 0; j < 4; j++) {
                    if (state.getPawn(i, j).equals(State.Pawn.WHITE))
                        whitePawnsOnflow++;
                    else if (state.getPawn(i, j).equals(State.Pawn.BLACK))
                        blackPawnsOnFlow++;
                }
        } else if (coordKing[1] > 4) {
            for (int i = 0; i < 9; i++)
                for (int j = 5; j < 9; j++) {
                    if (state.getPawn(i, j).equals(State.Pawn.WHITE))
                        whitePawnsOnflow++;
                    else if (state.getPawn(i, j).equals(State.Pawn.BLACK))
                        blackPawnsOnFlow++;
                }

        }
        return new int[]{whitePawnsOnflow, blackPawnsOnFlow};
    }
}