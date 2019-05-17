package Daniele.training;

import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import Daniele.ai.DanieleAction;
import Daniele.ai.ITablutState;
import Daniele.ai.Pos;
import Daniele.ai.TraspositionTable;
import Daniele.minmaxprinter.MinMaxPrinter;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class AIGameSingleThreadDE implements AIGame {

	private final int DRAW_VALUE_WHITE = -900;
	private final int DRAW_VALUE_BLACK = 900;
	private volatile boolean timeOver;
	private volatile Timer timer;
	private long maxTime;
	private boolean useTraspositionTable;
	private boolean orderingOptimization =false;
	private MinMaxPrinter printer;
	private TraspositionTable traspositionTable = TraspositionTable.getInstance();
	private boolean useDrawCondition=false;

	private HeuristicTablut heuristicTablut;


	public AIGameSingleThreadDE(long maxTime,MinMaxPrinter printer,boolean useTraspositionTable,boolean useDrawCondition,boolean orderingOptimization, double[] weights) {	
		this(maxTime,printer,useTraspositionTable,useDrawCondition);
		this.orderingOptimization=orderingOptimization;
		this.heuristicTablut = new HeuristicTablut(weights);
	}

	public AIGameSingleThreadDE(long maxTime,MinMaxPrinter printer,boolean useTraspositionTable,boolean useDrawCondition) {		//con -1 non c'è limite di tempo
		this(maxTime,printer,useTraspositionTable);
		this.useDrawCondition=useDrawCondition;

	}
	public AIGameSingleThreadDE(long maxTime,MinMaxPrinter printer,boolean useTraspositionTable) {		//con -1 non c'è limite di tempo
		this.maxTime = maxTime;
		this.useTraspositionTable = useTraspositionTable;
		this.printer= printer;

	}

	public DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts ,Set<String> pastStates) {

		traspositionTable.clear();
		LinkedList<DanieleAction> movesToBeRemoved = new LinkedList<DanieleAction>();

		//impostazione del timer
		timeOver = false;
		timer = new Timer();
		TimerTask timeoutTask = new TimerTask() {
			public void run() {
				//System.out.println("time over !");
				timeOver = true;
			}
		};
		//timer.schedule(timeoutTask, maxTime);

		if(maxTime!=-1) timer.schedule(timeoutTask, maxTime);

		long startTime = System.nanoTime();
		double time;


		//iterative deeping con alpha-beta prunning (versione diversa per gestire il tempo)	

		if(ts.getState().getTurn().equals(Turn.WHITE)) {	//MAX player
			LinkedList<DanieleAction> moves = (LinkedList<DanieleAction>)ts.getAllLegalMoves();

			double alpha = Double.NEGATIVE_INFINITY;
			DanieleAction bestAction = null;

			//tiene traccia dei migliori valori all'ultima profondità. ritorna bestActionLastDepth alla scadenza del tempo.
			DanieleAction bestActionLastDepth = null;
			//double bestAlphaLastDepth = Double.NEGATIVE_INFINITY;
			traspositionTable.clear();
			for(int depth = startingDepth; depth <= maxDepth; depth++) {

				alpha = Double.NEGATIVE_INFINITY;

				// ----
				//System.out.println("DEPTH = "+depth+" -----------------------------");
				// ----
				//int i = 0;

				double v = 0;
				if(orderingOptimization)
				{
					moves.removeAll(movesToBeRemoved);
					for(DanieleAction a : movesToBeRemoved)
						moves.addFirst(a);
					movesToBeRemoved.clear();
				}
				//si provano tutte le mosse possibili		 - primo livello di minmax
				for(DanieleAction m : moves) {
					//i++;


					ITablutState childState = ts.getChildState(m);
					if(useDrawCondition&&pastStates.contains(childState.getState().toLinearString())) {
						v=DRAW_VALUE_WHITE;
						//System.out.print("------- trovata mossa pareggio: "); printer.printMove(m,childState,v);
					}
					else {
						//if(depth==3) System.out.println("Depth=3 ---> mossa : " +m.toString());
						v = MinValue(depth - 1, alpha, Double.POSITIVE_INFINITY, childState, printer);
						// -----
						//System.out.println("v = "+v+ "  -  mossa "+i+": "+m.toString());
						// -----
					}

					//gestione del tempo : restituisce il migliore finora
					// l'ultima mossa possibile non viene presa in considerazione,
					// in quanto potrebbe essere errata a causa dell'arresto precoce dei calcoli.
					if (timeOver) {
						timer.cancel();
						// -----
						time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
						//System.out.println("TIMEOVER: time=" + time + ", depth = " + depth + " -  bestActionLastDepth: " + bestActionLastDepth.toString());
						// -----
						return bestActionLastDepth;
					}

					//caso normale: aggiorna la nuova mossa migliore se si raggiunge 
					// un punteggio migliore assumendolo.
					//Nessuna potatura al livello superiore!
					if (useTraspositionTable)
						traspositionTable.add(childState.getState(),maxDepth -depth,v); 


					if (v > alpha) { //pruning
						alpha = v;
						bestAction = m;
						//printer.printMove(m,childState,alpha);

						if(orderingOptimization) {
							movesToBeRemoved.addLast(m);
							bestActionLastDepth = m;
						}
						// -----
						//System.out.println("v = "+v+ "  -  bestAction "+i+": "+bestAction.toString());
						// -----
						if (depth == startingDepth) {    //potrebbe servire nel caso si decidesse di partire con una profondità già abbastanza notevole -
							bestActionLastDepth = m;    //in modo che se scade il timer si avrà una già una bestActionLastDepth prima di finire il for-moves_same_depth
							//bestAlphaLastDepth = alpha;
							// -----
							//System.out.println("v = "+v+ "  - startingDepth = "+startingDepth+" - bestActionLastDepth "+i+": "+bestActionLastDepth.toString());
							// -----
						}
					}//pruning

				}//for-moves_same_depth

				//si tiene traccia della migliore mossa alla profondità esaminata
				//bestAlphaLastDepth = alpha;
				bestActionLastDepth = bestAction;
				// -----
				//System.out.println("Depth = "+ --depth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
				//System.out.println("------------------------------------------------");
				// -----

			}//for-depths

			//raggiunta la profondità massima senza il timeout
			timer.cancel();
			time = (double)(System.nanoTime()-startTime) / 1_000_000_000.0;
			//System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
			return bestActionLastDepth;		
		}

		else if(ts.getState().getTurn().equals(Turn.BLACK)) {	//MIN player
			LinkedList<DanieleAction> moves = (LinkedList<DanieleAction>) ts.getAllLegalMoves();

			double beta = Double.POSITIVE_INFINITY;
			DanieleAction bestAction = null;

			//tiene traccia dei migliori valori all'ultima profondità. ritorna bestActionLastDepth alla scadenza del tempo.
			DanieleAction bestActionLastDepth = null;
			//double bestBetaLastDepth = Double.POSITIVE_INFINITY;

			for(int depth = startingDepth; depth <= maxDepth; depth++) {

				beta = Double.POSITIVE_INFINITY;
				double v =0;
				// ----
				//System.out.println("DEPTH = "+depth+" -----------------------------");
				// ----
				//int i = 0;
				if(orderingOptimization)
				{
					moves.removeAll(movesToBeRemoved);
					for(DanieleAction a : movesToBeRemoved)
						moves.addFirst(a);
					movesToBeRemoved.clear();
				}
				//si provano tutte le mosse possibili		 - primo livello di minmax
				for(DanieleAction m : moves) {
					//i++;

					ITablutState childState = ts.getChildState(m);
					if(useDrawCondition&&pastStates.contains(childState.getState().toLinearString())) {
						v=DRAW_VALUE_BLACK;
						//System.out.print("------- trovata mossa pareggio: "); printer.printMove(m,childState,v);
					}
					else {

						v = MaxValue(depth - 1, Double.NEGATIVE_INFINITY, beta, childState, printer);
						// -----
						//System.out.println("Depth = "+depth+" - v = "+v+" - bestAction = "+m.toString());
						// -----
					}

					//gestione del tempo : restituisce il migliore finora
					// l'ultima mossa possibile non viene presa in considerazione,
					// in quanto potrebbe essere errata a causa dell'arresto precoce dei calcoli.
					if (timeOver) {
						timer.cancel();
						// -----
						time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
						//System.out.println("TIMEOVER: time=" + time + ", depth = " + depth + " -  bestActionLastDepth: " + bestActionLastDepth.toString());
						// -----
						return bestActionLastDepth;
					}

					//caso normale: aggiorna la nuova mossa migliore se si raggiunge
					// un punteggio migliore assumendolo.
					//Nessuna potatura al livello superiore!
					if (useTraspositionTable)
						traspositionTable.add(childState.getState(),maxDepth -depth,v); 
					if (v < beta) { //pruning
						beta = v;
						bestAction = m;
						//printer.printMove(m,childState,beta);
						if(orderingOptimization) {
							movesToBeRemoved.addLast(m);
							bestActionLastDepth =m;

						}

						// -----
						//System.out.println("v = "+v+ "  -  bestAction "+i+": "+bestAction.toString());
						// -----
						if (depth == startingDepth) {    //potrebbe servire nel caso si decidesse di partire con una profondità già abbastanza notevole -
							bestActionLastDepth = m;    //in modo che se scade il timer si avrà una già una bestActionLastDepth prima di finire il for-moves_same_depth
							//bestBetaLastDepth = beta;
							// -----
							//System.out.println("v = "+v+ "  - startingDepth = "+startingDepth+" - bestActionLastDepth "+i+": "+bestActionLastDepth.toString());
							// -----
						}
					} //pruning

				}//for-moves_same_depth
				//si tiene traccia della migliore mossa alla profondità esaminata
				//bestBetaLastDepth = beta;
				bestActionLastDepth = bestAction;	
				// -----
				//System.out.println("Depth = "+ --depth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
				//System.out.println("------------------------------------------------");
				// -----

			}//for-depths

			//raggiunta la profondità massima senza il timeout
			timer.cancel();
			time = (double)(System.nanoTime()-startTime) / 1_000_000_000.0;
			//System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
			return bestActionLastDepth;
		}

		return null;	//non dovremmo mai arrivarci

	}


	/**
	 * funzione di valutazione del valore massimo di AlphaBetaSearch
	 * 
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param state
	 * @return
	 */
	//@Matteo manca il confronto sul percorso minimo , forse la mappa non basta , per ora provo a usare depth in cutoff
	private double MaxValue(int depth, double alpha, double beta, ITablutState state, MinMaxPrinter printer) {
		//all'interruzione si ritorna un valore 
		if (cutoff(depth, state)) {
			return heuristicTablut.value(state) - depth;
		}
		if(useTraspositionTable){
			double val = traspositionTable.valueOver(state.getState(), depth);
			if(!Double.isNaN(val))
				return val;
		}

		double v = Double.NEGATIVE_INFINITY;

		List<DanieleAction> moves = state.getAllLegalMoves();
		//List<Action> moves = state.getTopLeftMoves();

		for (DanieleAction m : moves) {											//= per ogni coppia <azione, stato>
			//		ITablutState childState = state.getChildState(m);
			List<Pos> p = state.trasformState(m);

			//if(depth==1) System.out.println("Depth=1 ---> mossa : " +m.toString());
			//if(depth==3) System.out.println("Depth=3 ---> mossa : " +m.toString());
			//@Matteo controllo su null dovrebbe essere inutile
			//if(state!=null) {//  della funzione successore
			v=Math.max(MinValue(depth - 1, alpha, beta, state,printer),v);
			state.trasformStateBack(m, p);	

			//@Matteo si possono invertire queste due istruzioni??
			// -----
			//v = Math.max(v, tmp);
			//if(depth==1 && tmp>v) {System.out.print("Depth=1  ->  "); printer.printMove(m,childState,tmp);}
			//if(depth==3 && tmp>v) {System.out.print("Depth=3  ->  "); printer.printMove(m,childState,tmp);}

			//if(depth==startingDepth-1) { System.out.print("Depth = "+depth+" -> "); printer.printMove(m,childState,tmp);}

			// -----

			if (v >= beta) {
				if(useTraspositionTable) {
					traspositionTable.add(state.getState(), depth, v);
				}
				return v;
			}

			alpha = Math.max(alpha, v);
		}

		if(useTraspositionTable) {
			traspositionTable.add(state.getState(), depth, v);
		}
		return v;
	}


	/**
	 * funzione di valutazione del valore minimo di AlphaBetaSearch
	 * 
	 * @param depth
	 * @param alpha
	 * @param beta
	 * @param state
	 * @return
	 */
	private double MinValue(int depth, double alpha, double beta, ITablutState state,MinMaxPrinter printer) {
		//all'interuzione si ritorna un valore 
		//provo aggiungere -depth per favorire percorsi pi� corti
		if (cutoff(depth, state)) {
			return heuristicTablut.value(state) + depth;
		}
		if(useTraspositionTable)
		{double val = traspositionTable.valueOver(state.getState(), depth);
		if(!Double.isNaN(val))
			return val;
		}

		double v = Double.POSITIVE_INFINITY;

		List<DanieleAction> moves = state.getAllLegalMoves();
		//List<Action> moves = state.getTopLeftMoves();

		for (DanieleAction m : moves) {											//= per ogni coppia <azione, stato>
			//	ITablutState childState = state.getChildState(m);				//  della funzione successore
			List<Pos> p = state.trasformState(m);

			//if(depth==2) System.out.println("Depth=2 ---> mossa : " +m.toString());
			//	if(childState!=null) {
			v=Math.min(MaxValue(depth - 1, alpha, beta, state,printer),v);
			state.trasformStateBack(m, p);
			// -----
			//v = Math.min(v,tmp);
			//if(depth==2 && tmp<v) {System.out.print("Depth=2  ->  "); printer.printMove(m,childState,tmp);}

			//if(depth==startingDepth-1) { System.out.print("Depth = "+depth+" -> "); printer.printMove(m,childState,tmp);}

			// -----

			if (v <= alpha) {
				if(useTraspositionTable) {
					traspositionTable.add(state.getState(), depth, v);
				}
				return v;
			}
			beta = Math.min(beta, v);
		}

		if(useTraspositionTable) {
			traspositionTable.add(state.getState(), depth, v);
		}
		return v;

	}


	/**
	 * funzione di per fermarsi nella ricerca in profondità di AlphaBetaSearch
	 * 
	 * @param depth
	 * @param state
	 * @return
	 */
	private boolean cutoff(int depth, ITablutState state) {
		//ci si blocca quando si raggiunge una certa profondità o si è in un nodo
		//foglia -> quindi si è determinato una vittoria o sconfitta o pareggio
		//o è finito il tempo a disposizione
		return depth <= 0 
				|| (!state.getState().getTurn().equals(Turn.WHITE) && !state.getState().getTurn().equals(Turn.BLACK))
				|| timeOver;
	}


}