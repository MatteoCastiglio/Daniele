package daniele.state;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class TransformableTablutState  implements ITablutState {

	private ITablutGame game;
	private State state;															
	private State.Pawn[][] board;
	private int[] coordKing;
	private int nwhites; 
	private int nblacks; 

    @Override
    public String toString() {
        return "TablutState" + System.lineSeparator() + state + System.lineSeparator() + " nwhites=" + nwhites + ", nblacks=" + nblacks + " king=" + coordKing[0] + " " + coordKing[1];
    }

	private void setup()
	{
		coordKing = new int[2];
		nwhites =0;
		nblacks = 0;
		for(int i =0; i< 9; i++)
			for(int j =0; j< 9; j++)
			{if(state.getPawn(i, j).equals(Pawn.WHITE))
				nwhites++;
			else if( state.getPawn(i, j).equals(Pawn.BLACK))
				nblacks++;
			else if(state.getPawn(i, j).equals(Pawn.KING)) {coordKing[0]=i; coordKing[1]=j;}
			}
	}

	// Constructor
    public TransformableTablutState(State state, ITablutGame game) {
        this.state = state;
        this.board = state.getBoard();
        this.game = game;
        setup();

    }

    
    // Update board state with an action
    @Override
    public List<PawnPosition> trasformState(DanieleAction action) {

    	//update board state
    	
    	List<PawnPosition> p = null;
        Pawn pawn = state.getPawn(action.getRowFrom(), action.getColumnFrom());
        Pawn[][] newBoard = state.getBoard();

        if (action.getColumnFrom() == 4 && action.getRowFrom() == 4) {
            newBoard[action.getRowFrom()][action.getColumnFrom()] = Pawn.THRONE;
        } else {
            newBoard[action.getRowFrom()][action.getColumnFrom()] = Pawn.EMPTY;
        }

        newBoard[action.getRowTo()][action.getColumnTo()] = pawn;
        state.setBoard(newBoard);

        
        if (pawn.equals(Pawn.KING))
            coordKing = new int[]{action.getRowTo(), action.getColumnTo()};


        if (state.getTurn().equals(Turn.WHITE)) {
            state.setTurn(State.Turn.BLACK);
            p = checkCaptureWhite(state, action);


        } else {
            state.setTurn(State.Turn.WHITE);
            p = checkCaptureBlack(state, action);
        }

        return p;


    }

    

    @Override
    public void trasformStateBack(DanieleAction a,List<PawnPosition> pawnsRemoved) {

    	 Pawn[][] newBoard = state.getBoard();
        switch (state.getTurn()) {
            case WHITEWIN: {
            	newBoard[a.getRowFrom()][ a.getColumnFrom()] =Pawn.KING;
                coordKing = new int[]{a.getRowFrom(), a.getColumnFrom()};
                state.setTurn(Turn.WHITE);
                for (int i = 0; i < pawnsRemoved.size(); i++) {
                	newBoard[pawnsRemoved.get(i).row][ pawnsRemoved.get(i).col] =  Pawn.BLACK;
                    nblacks++;
                }
                break;
            }
            case BLACKWIN: {
            	newBoard[a.getRowFrom()][ a.getColumnFrom()] =Pawn.BLACK;
                state.setTurn(Turn.BLACK);
                for (int i = 0; i < pawnsRemoved.size(); i++) {
                	newBoard[pawnsRemoved.get(i).row][ pawnsRemoved.get(i).col] =  Pawn.WHITE;
                    nwhites++;
                }
                break;
            }
            case BLACK: {


                state.setTurn(Turn.WHITE);
                if (state.getPawn(a.getRowTo(), a.getColumnTo()).equals(Pawn.KING)) {
                	newBoard[a.getRowFrom()][ a.getColumnFrom()] = Pawn.KING;
                    coordKing = new int[]{a.getRowFrom(), a.getColumnFrom()};
                } else {
                	newBoard[a.getRowFrom()][ a.getColumnFrom()] = Pawn.WHITE;
                }

                for (int i = 0; i < pawnsRemoved.size(); i++) {
                	newBoard[pawnsRemoved.get(i).row][ pawnsRemoved.get(i).col] =  Pawn.BLACK;
                    nblacks++;
                }
                break;
            }

            case WHITE: {
            	newBoard[a.getRowFrom()][ a.getColumnFrom()] = Pawn.BLACK;
                state.setTurn(Turn.BLACK);
                for (int i = 0; i < pawnsRemoved.size(); i++) {
                	newBoard[pawnsRemoved.get(i).row][ pawnsRemoved.get(i).col] =  Pawn.WHITE;
                    nwhites++;
                }
                break;
            }
            // 
            case DRAW:
                break;
            default:
                break;
        }
        newBoard[a.getRowTo()][ a.getColumnTo()] =  Pawn.EMPTY;
        state.setBoard(newBoard);
    }

    // Rules defined by game    
    private List<PawnPosition> checkCaptureWhite(State state, DanieleAction a) {
    	
    	List<PawnPosition> l = game.checkCaptureWhite(state, a);
    	nblacks-=l.size();
    	return l;
    	
    }
 
    private List<PawnPosition> checkCaptureBlack(State state, DanieleAction a) {


        List<PawnPosition> l = new ArrayList<>();
        l.addAll(game.checkCaptureBlackPawnRight(state, a));
        l.addAll(game.checkCaptureBlackPawnLeft(state, a));
        l.addAll(game.checkCaptureBlackPawnUp(state, a));
        l.addAll(game.checkCaptureBlackPawnDown(state, a));
        nwhites-=l.size();
        
        
        
        game.checkCaptureBlackKingRight(state, a);
        game.checkCaptureBlackKingLeft(state, a);
        game.checkCaptureBlackKingDown(state, a);
        game.checkCaptureBlackKingUp(state, a);
        return l;
    }

    
    // get Legal moves from a board state
    @Override
    public List<DanieleAction> getAllLegalMoves() {
        List<DanieleAction> moves = new LinkedList<DanieleAction>();

        if (state.getTurn().equals(State.Turn.WHITE)) {    //MAX player
            for (int i = 0; i < this.board.length; i++) {
                for (int j = 0; j < this.board.length; j++) {
                   // all moves for white player
                    if (this.board[i][j].equals(State.Pawn.WHITE) || this.board[i][j].equals(State.Pawn.KING)) {
                        //.. verticals
                        for (int x = i - 1; x >= 0; x--)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || isCellACitadel(x, j))
                                break;    //stop when there are other pawn or citadels or Throne
  
                            else /*if(this.board[x][j].equals(Pawn.EMPTY))*/ moves.add(new DanieleAction(i, j, x, j));
                        for (int x = i + 1; x < this.board.length; x++)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || isCellACitadel(x, j))
                                break;    ///stop when there are other pawn or citadels or Throne
                            else moves.add(new DanieleAction(i, j, x, j));
                        //.. horizontal
                        for (int x = j - 1; x >= 0; x--)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || isCellACitadel(i, x))
                                break;    //stop when there are other pawn or citadels or Throne
                            else moves.add(new DanieleAction(i, j, i, x));
                        for (int x = j + 1; x < this.board.length; x++)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || isCellACitadel(i, x))
                                break;    //stop when there are other pawn or citadels or Throne
                            else moves.add(new DanieleAction(i, j, i, x));
                    }
                }
            }
        } else if (state.getTurn().equals(State.Turn.BLACK)) {    //MIN player
            for (int i = 0; i < this.board.length; i++) {
                for (int j = 0; j < this.board.length; j++) {
                    // all moves for black player
                    if (this.board[i][j].equals(State.Pawn.BLACK)) {
                        //.. vertical
                        for (int x = i - 1; x >= 0; x--)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || (!isCellACitadel(i, j) && isCellACitadel(x, j)))
                                break;     //stop when there are other pawn or citadels or Throne
                            else 
                                moves.add(new DanieleAction(i, j, x, j));
                        for (int x = i + 1; x < this.board.length; x++)
                            if (!this.board[x][j].equals(State.Pawn.EMPTY) || (!isCellACitadel(i, j) && isCellACitadel(x, j)))
                                break;   //stop when there are other pawn or citadels or Throne
                            else //if (x != 8 && (j != 0 || j != 8))
                                moves.add(new DanieleAction(i, j, x, j));
                        //.. horizontal
                        for (int x = j - 1; x >= 0; x--)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || (!isCellACitadel(i, j) && isCellACitadel(i, x)))
                                break;    //stop when there are other pawn or citadels or Throne
                            else //if (x != 0 && (i != 0 || i != 8))
                                moves.add(new DanieleAction(i, j, i, x));
                        for (int x = j + 1; x < this.board.length; x++)
                            if (!this.board[i][x].equals(State.Pawn.EMPTY) || (!isCellACitadel(i, j) && isCellACitadel(i, x)))
                                break;    //stop when there are other pawn or citadels or Throne
                            else
                                moves.add(new DanieleAction(i, j, i, x));
                    }
                }
            }
        }
        //Collections.reverse(moves);
        return moves;
    }

    

    @Override
    public State getState() {
        return state;
    }

    
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
        return coordKing; 
    }

    // value computed based on King position
    public double getFlow() {
  
        double res = 0;
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

	
    @Override
	public boolean isCellACitadel(int i, int j) {
		       
		int middle = (this.board.length - 1) / 2;

        //citadels on border
        if ((i >= middle - 1 && i <= middle + 1) && (j == 0 || j == this.board.length - 1)) return true;
        if ((i == 0 || i == this.board.length - 1) && (j >= middle - 1 && j <= middle + 1)) return true;
        //other citadels
        if (i == middle && (j == 1 || j == this.board.length - 2)) return true;
        if ((i == 1 || i == this.board.length - 2) && j == middle) return true;


        return false;
	}
}

