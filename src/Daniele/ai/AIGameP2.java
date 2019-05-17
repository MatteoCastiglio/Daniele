package Daniele.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import Daniele.state.DanieleAction;
import Daniele.state.ITablutState;
import Daniele.minmaxprinter.MinMaxPrinter;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class AIGameP2 implements AIGame {


	private final int NTHREAD=2;
	private long maxTime;
	private boolean orderingOptimization =false;
	private MinMaxPrinter printer;
	private TraspositionTable traspositionTable = TraspositionTable.getInstance();
	private volatile boolean timeOver;
	private boolean useDrawCondition=false;
	private boolean useTraspositionTable;


	public AIGameP2(long maxTime,MinMaxPrinter printer,boolean useTraspositionTable,boolean useDrawCondition, boolean useOrderingOptimitazion) {		//con -1 non c'è limite di tempo
		this.maxTime = maxTime;



	this.orderingOptimization=useOrderingOptimitazion;
	this.useDrawCondition=useDrawCondition;
		this.useTraspositionTable = useTraspositionTable;
		this.printer= printer;
	}

	public DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts, Set<String> pastStates){

			DanieleAction bestAction = null;
			double alpha = Integer.MIN_VALUE;
			double beta = Integer.MAX_VALUE;
			List<DanieleAction> moves = ts.getAllLegalMoves();
			List<ITablutState> states = new ArrayList<>();
			for(DanieleAction m : moves)

				states.add(ts.getChildState(m));

				List<AIThread> threads = new ArrayList<AIThread>();
				int numberOfStates = moves.size() / NTHREAD;
				//System.out.println("numberOfProcessors="+numberOfProcessors+", moves="+moves.size()+", numberOfMoves="+numberOfMoves);

				for(int i=0; i< NTHREAD;i++) {


					List<ITablutState> threadstates;
					if(i==NTHREAD-1) threadstates = states.subList(i, moves.size());
					else threadstates= states.subList(i*numberOfStates, (i+1)*numberOfStates);

					AIThread thread = new AIThread(maxTime,printer,useTraspositionTable,useDrawCondition,orderingOptimization,startingDepth,  maxDepth -1, states , pastStates);
					threads.add(thread);

					thread.start();
				}

				for(AIThread t: threads) {
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			if(ts.getState().getTurn().equals(Turn.WHITE))
					for(AIThread t: threads) {
						if(t.getValue() > alpha) {
							alpha = t.getValue();
							bestAction = t.getResult();
						}
						
					}
		if(ts.getState().getTurn().equals(Turn.BLACK))
			for(AIThread t: threads) {
				if(t.getValue() < beta) {
					beta = t.getValue();
					bestAction = t.getResult();
				}

			}






		return bestAction;	//non dovremmo mai arrivarci

	}







	
	
	
	
	}