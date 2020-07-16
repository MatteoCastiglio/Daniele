package Daniele.ai;

import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import Daniele.minmaxprinter.MinMaxPrinter;
import Daniele.state.DanieleAction;
import Daniele.state.ITablutState;
import Daniele.state.Pos;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class AIGameSingleThread implements AIGame {

    private final int DRAW_VALUE_WHITE = -9000;
    private final int DRAW_VALUE_BLACK = 9000;
    private volatile boolean timeOver;
    private volatile Timer timer;
    private long maxTime;
    private boolean useTraspositionTable;
    private boolean orderingOptimization = false;
    private MinMaxPrinter printer;
    private TraspositionTable traspositionTable = TraspositionTable.getInstance();
    private boolean useDrawCondition = false;
    private HeuristicFunction heuristic;


    public AIGameSingleThread(HeuristicFunction h, long maxTime, MinMaxPrinter printer, boolean useTraspositionTable, boolean useDrawCondition, boolean orderingOptimization) {
        this(h, maxTime, printer, useTraspositionTable, useDrawCondition);
        this.orderingOptimization = orderingOptimization;

    }

    public AIGameSingleThread(HeuristicFunction h, long maxTime, MinMaxPrinter printer, boolean useTraspositionTable, boolean useDrawCondition) {
        this(h, maxTime, printer, useTraspositionTable);
        this.useDrawCondition = useDrawCondition;

    }

    public AIGameSingleThread(HeuristicFunction h, long maxTime, MinMaxPrinter printer, boolean useTraspositionTable) {        //if maxTime = -1 there is no time limit
        this.heuristic = h;
        this.maxTime = maxTime;
        this.useTraspositionTable = useTraspositionTable;
        this.printer = printer;

    }
    //TO-DO refactoring del codice per evitare duplicazione
    public DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts, Set<String> pastStates) {

        traspositionTable.clear();
        LinkedList<DanieleAction> movesToBeRemoved = new LinkedList<DanieleAction>();

        //timer setting
        timeOver = false;
        timer = new Timer();
        TimerTask timeoutTask = new TimerTask() {
            public void run() {
                //System.out.println("time over !");
                timeOver = true;
            }
        };

        if (maxTime != -1) timer.schedule(timeoutTask, maxTime);

        long startTime = System.nanoTime();
        double time;


        //iterative deeping con alpha-beta prunning (versione diversa per gestire il tempo

        //MAX player
        if (ts.getState().getTurn().equals(Turn.WHITE)) {

            LinkedList<DanieleAction> moves = (LinkedList<DanieleAction>) ts.getAllLegalMoves();

            double alpha = Double.NEGATIVE_INFINITY;
            DanieleAction bestAction = null;
            DanieleAction bestActionLastDepth = null;    //keeps track of the best action at the last depth. returns bestActionLastDepth when time runs out.
            traspositionTable.clear();

            for (int depth = startingDepth; depth <= maxDepth; depth++) {

                alpha = Double.NEGATIVE_INFINITY;
                double v = 0;

                if (orderingOptimization) {    //the most interesting moves are put on top
                    moves.removeAll(movesToBeRemoved);
                    for (DanieleAction a : movesToBeRemoved)
                        moves.addFirst(a);
                    movesToBeRemoved.clear();
                }

                //trying all the possible moves - first level of minmax
                for (DanieleAction m : moves) {

                    ITablutState childState = ts.getChildState(m);

                    if (useDrawCondition && pastStates.contains(childState.getState().toLinearString())) {    //a move has been found that leads to a draw
                        v = DRAW_VALUE_WHITE;
                    } else {
                        v = MinValue(depth - 1, alpha, Double.POSITIVE_INFINITY, childState, printer);
                    }

                    //time management: returns the best move so far, that is bestActionLastDepth
                    //without orderingOptimization the last possible move is not taken into account, as it may be incorrect due to the early stoppage of the calculations.
                    if (timeOver) {
                        timer.cancel();
                        time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
                        //debug// System.out.println("TIMEOVER: time=" + time + ", depth = " + depth + " -  bestActionLastDepth: " + bestActionLastDepth.toString());
                        return bestActionLastDepth;
                    }

                    if (useTraspositionTable)    //to save the current state in the Transposition table
                        traspositionTable.add(childState.getState(), maxDepth - depth, v);

                    //normal case: update the new best move if you reach a better score by hiring it.
                    // no pruning on the upper level!
                    if (v > alpha) {
                        alpha = v;
                        bestAction = m;
                        //debug
                        printer.printMove(m,childState,alpha);

                        if (orderingOptimization) {    //with orderingOptimization the last possible move is taken into account
                            movesToBeRemoved.addLast(m);
                            bestActionLastDepth = m;
                        }

                        if (depth == startingDepth) {    //in case you decide to leave with a depth already remarkable: if the timer expires you will already have a bestActionLastDepth before finishing the for-moves_same_depth
                            bestActionLastDepth = m;
                        }
                    }

                }//for-moves_same_depth

                //the best move is kept at the examined depth
                bestActionLastDepth = bestAction;
                //debug// System.out.println("Depth = "+ --depth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
                //debug// System.out.println("------------------------------------------------");

            }//for-depths

            //reached the maximum depth without the timeout
            timer.cancel();
            time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
            //debug// System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
            return bestActionLastDepth;
        } else if (ts.getState().getTurn().equals(Turn.BLACK)) {    //MIN player

            LinkedList<DanieleAction> moves = (LinkedList<DanieleAction>) ts.getAllLegalMoves();

            double beta = Double.POSITIVE_INFINITY;
            DanieleAction bestAction = null;
            DanieleAction bestActionLastDepth = null;    //keeps track of the best action at the last depth. returns bestActionLastDepth when time runs out.

            for (int depth = startingDepth; depth <= maxDepth; depth++) {

                beta = Double.POSITIVE_INFINITY;
                double v = 0;

                if (orderingOptimization) {    //the most interesting moves are put on top
                    moves.removeAll(movesToBeRemoved);
                    for (DanieleAction a : movesToBeRemoved)
                        moves.addFirst(a);
                    movesToBeRemoved.clear();
                }

                //trying all the possible moves - first level of minmax
                for (DanieleAction m : moves) {

                    ITablutState childState = ts.getChildState(m);

                    if (useDrawCondition && pastStates.contains(childState.getState().toLinearString())) {    //a move has been found that leads to a draw
                        v = DRAW_VALUE_BLACK;
                    } else {
                        v = MaxValue(depth - 1, Double.NEGATIVE_INFINITY, beta, childState, printer);
                    }

                    //time management: returns the best move so far, that is bestActionLastDepth
                    //without orderingOptimization the last possible move is not taken into account, as it may be incorrect due to the early stoppage of the calculations.
                    if (timeOver) {
                        timer.cancel();
                        time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
                        //debug// System.out.println("TIMEOVER: time=" + time + ", depth = " + depth + " -  bestActionLastDepth: " + bestActionLastDepth.toString());
                        return bestActionLastDepth;
                    }

                    if (useTraspositionTable)    //to save the current state in the Transposition table
                        traspositionTable.add(childState.getState(), maxDepth - depth, v);

                    //normal case: update the new best move if you reach a better score by hiring it.
                    // no pruning on the upper level!
                    if (v < beta) {
                        beta = v;
                        bestAction = m;
                        //debug// 
                        printer.printMove(m,childState,beta);

                        if (orderingOptimization) {    //with orderingOptimization the last possible move is taken into account
                            movesToBeRemoved.addLast(m);
                            bestActionLastDepth = m;
                        }

                        if (depth == startingDepth) {    //in case you decide to leave with a depth already remarkable: if the timer expires you will already have a bestActionLastDepth before finishing the for-moves_same_depth
                            bestActionLastDepth = m;
                        }
                    }

                }//for-moves_same_depth

                //the best move is kept at the examined depth
                bestActionLastDepth = bestAction;
                //debug// System.out.println("Depth = "+ --depth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
                //debug// System.out.println("------------------------------------------------");

            }//for-depths

            //reached the maximum depth without the timeout
            timer.cancel();
            time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
            //debug// System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
            return bestActionLastDepth;
        }

        return null;    //we should never get there

    }


    /**
     * evaluation function of the maximum value of AlphaBetaSearch
     *
     * @param depth
     * @param alpha
     * @param beta
     * @param state
     * @return
     */
    private double MaxValue(int depth, double alpha, double beta, ITablutState state, MinMaxPrinter printer) {
        //a value is returned upon interruption
        if (cutoff(depth, state)) {
            return heuristic.HeuristicFunction(state) - depth;
        }

        if (useTraspositionTable) {
            double val = traspositionTable.valueOver(state.getState(), depth);
            if (!Double.isNaN(val)) {
                //debug// System.out.println("-----------------------> Trasposition Table used: depth="+depth+", val="+val);
                return val;
            }
        }

        double v;
        v = Double.NEGATIVE_INFINITY;

        List<DanieleAction> moves = state.getAllLegalMoves();

        //for each pair <action, status>
        for (int i = 0, movesSize = moves.size(); i < movesSize; i++) {
            DanieleAction m = moves.get(i);

            //for efficiency reasons, we prefer to transform the state by applying the move rather than apply it to a clone state.
            // This will relieve the work of the garbage collector
            List<Pos> p =state.trasformState(m);        //instead of:	ITablutState childState = state.getChildState(m);

            v = Math.max(MinValue(depth - 1, alpha, beta, state, printer), v);

            //it restores the state by applying the move in reverse
            state.trasformStateBack(m,p);


            if (v >= beta) {    //pruning
                if (useTraspositionTable) {
                    traspositionTable.add(state.getState(), depth, v);
                }
                return v;
            }

            alpha = Math.max(alpha, v);
        }

        if (useTraspositionTable) {
            traspositionTable.add(state.getState(), depth, v);
        }

        return v;
    }


    /**
     * evaluation function of the minimum value of AlphaBetaSearch
     *
     * @param depth
     * @param alpha
     * @param beta
     * @param state
     * @return
     */
    private double MinValue(int depth, double alpha, double beta, ITablutState state, MinMaxPrinter printer) {
        //a value is returned upon interruption
        if (cutoff(depth, state)) {
            return heuristic.HeuristicFunction(state) + depth;
        }

        if (useTraspositionTable) {
            double val = traspositionTable.valueOver(state.getState(), depth);
            if (!Double.isNaN(val)) {
                //debug// System.out.println("-----------------------> Trasposition Table used: depth="+depth+", val="+val);
                return val;
            }
        }

        double v = Double.POSITIVE_INFINITY;

        List<DanieleAction> moves = state.getAllLegalMoves();

        //for each pair <action, status>
        for (int i = 0, movesSize = moves.size(); i < movesSize; i++) {
            DanieleAction m = moves.get(i);

            //for efficiency reasons, we prefer to transform the state by applying the move rather than apply it to a clone state.
            // This will relieve the work of the garbage collector
            List<Pos> p =state.trasformState(m);        //instead of:	ITablutState childState = state.getChildState(m);

            v = Math.min(MaxValue(depth - 1, alpha, beta, state, printer), v);

            //it restores the state by applying the move in reverse
            state.trasformStateBack(m,p);


            if (v <= alpha) {    //pruning
                if (useTraspositionTable) {
                    traspositionTable.add(state.getState(), depth, v);
                }
                return v;
            }
            beta = Math.min(beta, v);
        }

        if (useTraspositionTable) {
            traspositionTable.add(state.getState(), depth, v);
        }

        return v;
    }

    /**
     * function to stop in the deep search of AlphaBetaSearch
     *
     * @param depth
     * @param state
     * @return
     */
    private boolean cutoff(int depth, ITablutState state) {
        //you get stuck when you reach a certain depth
        //or you are in a leaf node (so you have determined a victory or defeat or draw)
        //or the time available is over
        return depth <= 0
                || (!state.getState().getTurn().equals(Turn.WHITE) && !state.getState().getTurn().equals(Turn.BLACK))
                || timeOver;
    }


}