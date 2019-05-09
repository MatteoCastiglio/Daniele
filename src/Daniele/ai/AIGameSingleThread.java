package Daniele.ai;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Daniele.minmaxprinter.MinMaxPrinter;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class AIGameSingleThread implements AIGame {

	private volatile boolean timeOver;
	private volatile Timer timer;
	private long maxTime;
	private boolean useTraspositionTable;
	private MinMaxPrinter printer;
	private TraspositionTable traspositionTable = TraspositionTable.getInstance();

	public AIGameSingleThread(long maxTime,MinMaxPrinter printer,boolean useTraspositionTable) {		//con -1 non c'è limite di tempo
		this.maxTime = maxTime;
		this.useTraspositionTable = useTraspositionTable;
		this.printer= printer;

	}

	public DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts ) {

		traspositionTable.clear();
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
			List<DanieleAction> moves = ts.getAllLegalMoves();

			double alpha = Double.NEGATIVE_INFINITY;
			DanieleAction bestAction = null;

			//tiene traccia dei migliori valori all'ultima profondità. ritorna bestActionLastDepth alla scadenza del tempo.
			DanieleAction bestActionLastDepth = null;
			//double bestAlphaLastDepth = Double.NEGATIVE_INFINITY;

			for(int depth = startingDepth; depth <= maxDepth; depth++) {

				alpha = Double.NEGATIVE_INFINITY;

				// ----
				//System.out.println("DEPTH = "+depth+" -----------------------------");
				// ----
				//int i = 0;
				traspositionTable.clear();
				//si provano tutte le mosse possibili		 - primo livello di minmax
				for(DanieleAction m : moves) {
					//i++;


					ITablutState childState = ts.getChildState(m);

					if (!useTraspositionTable || traspositionTable.add(childState.getState()) == true) {


						//if(depth==3) System.out.println("Depth=3 ---> mossa : " +m.toString());
						double v = MinValue(depth - 1, alpha, Double.POSITIVE_INFINITY, childState, printer);
					// -----
					//System.out.println("v = "+v+ "  -  mossa "+i+": "+m.toString());
					// -----

					//gestione del tempo : restituisce il migliore finora
					// l'ultima mossa possibile non viene presa in considerazione,
					// in quanto potrebbe essere errata a causa dell'arresto precoce dei calcoli.
					if (timeOver) {
						timer.cancel();
						// -----
						time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
						System.out.println("TIMEOVER: time=" + time + ", depth = " + depth + " -  bestActionLastDepth: " + bestActionLastDepth.toString());
						// -----
						return bestActionLastDepth;
					}

					//caso normale: aggiorna la nuova mossa migliore se si raggiunge 
					// un punteggio migliore assumendolo.
					//Nessuna potatura al livello superiore!
					if (v > alpha) {
						alpha = v;
						bestAction = m;
						printer.printMove(m,childState,alpha);
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
					}
				}
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
			System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
			return bestActionLastDepth;		
		}

		else if(ts.getState().getTurn().equals(Turn.BLACK)) {	//MIN player
			List<DanieleAction> moves = ts.getAllLegalMoves();

			double beta = Double.POSITIVE_INFINITY;
			DanieleAction bestAction = null;

			//tiene traccia dei migliori valori all'ultima profondità. ritorna bestActionLastDepth alla scadenza del tempo.
			DanieleAction bestActionLastDepth = null;
			//double bestBetaLastDepth = Double.POSITIVE_INFINITY;

			for(int depth = startingDepth; depth <= maxDepth; depth++) {

				beta = Double.POSITIVE_INFINITY;
				traspositionTable.clear();
				// ----
				//System.out.println("DEPTH = "+depth+" -----------------------------");
				// ----
				//int i = 0;

				//si provano tutte le mosse possibili		 - primo livello di minmax
				for(DanieleAction m : moves) {
					//i++;

						ITablutState childState = ts.getChildState(m);
					if (!useTraspositionTable || traspositionTable.add(childState.getState()) == true) {
						double v = MaxValue(depth - 1, Double.NEGATIVE_INFINITY, beta, childState, printer);
						// -----
						//System.out.println("Depth = "+depth+" - v = "+v+" - bestAction = "+m.toString());
						// -----

						//gestione del tempo : restituisce il migliore finora
						// l'ultima mossa possibile non viene presa in considerazione,
						// in quanto potrebbe essere errata a causa dell'arresto precoce dei calcoli.
						if (timeOver) {
							timer.cancel();
							// -----
							time = (double) (System.nanoTime() - startTime) / 1_000_000_000.0;
							System.out.println("TIMEOVER: time=" + time + ", depth = " + depth + " -  bestActionLastDepth: " + bestActionLastDepth.toString());
							// -----
							return bestActionLastDepth;
						}

						//caso normale: aggiorna la nuova mossa migliore se si raggiunge
						// un punteggio migliore assumendolo.
						//Nessuna potatura al livello superiore!
						if (v < beta) {
							beta = v;
							bestAction = m;
							printer.printMove(m,childState,beta);
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
						}

					}//for-moves_same_depth
				}
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
			System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepth = "+bestActionLastDepth.toString());
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
			return HeuristicTablut.HeuristicFunction(state) - depth;
		}

		double tmp;
		double v = Double.NEGATIVE_INFINITY;

		List<DanieleAction> moves = state.getAllLegalMoves();
		//List<Action> moves = state.getTopLeftMoves();

		for (DanieleAction m : moves) {											//= per ogni coppia <azione, stato>
			ITablutState childState = state.getChildState(m);

			if(useTraspositionTable)
			{ if(traspositionTable.add(childState.getState())==false)
				return 1000;}
			//if(depth==1) System.out.println("Depth=1 ---> mossa : " +m.toString());
			//if(depth==3) System.out.println("Depth=3 ---> mossa : " +m.toString());
			//@Matteo controllo su null dovrebbe essere inutile
			if(childState!=null) {//  della funzione successore
				tmp=MinValue(depth - 1, alpha, beta, childState,printer);

				//@Matteo si possono invertire queste due istruzioni??
				// -----
				//v = Math.max(v, tmp);
				//if(depth==1 && tmp>v) {System.out.print("Depth=1  ->  "); printer.printMove(m,childState,tmp);}
				//if(depth==3 && tmp>v) {System.out.print("Depth=3  ->  "); printer.printMove(m,childState,tmp);}
				if(tmp>v) {
					v=tmp;
					//if(depth==startingDepth-1) { System.out.print("Depth = "+depth+" -> "); printer.printMove(m,childState,tmp);}
				}
				// -----
				if (v >= beta) {
					return v;
				}

				alpha = Math.max(alpha, v);
			}
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
			return HeuristicTablut.HeuristicFunction(state) + depth;
		}

		double tmp;
		double v = Double.POSITIVE_INFINITY;

		List<DanieleAction> moves = state.getAllLegalMoves();
		//List<Action> moves = state.getTopLeftMoves();

		for (DanieleAction m : moves) {											//= per ogni coppia <azione, stato>
			ITablutState childState = state.getChildState(m);				//  della funzione successore


			if(useTraspositionTable)
			{ if(traspositionTable.add(childState.getState())==false)
				return -1000;}




			//if(depth==2) System.out.println("Depth=2 ---> mossa : " +m.toString());
			if(childState!=null) {
				tmp=MaxValue(depth - 1, alpha, beta, childState,printer);

				// -----
				//v = Math.min(v,tmp);
				//if(depth==2 && tmp<v) {System.out.print("Depth=2  ->  "); printer.printMove(m,childState,tmp);}
				if(tmp<v) {
					v=tmp;
					//if(depth==startingDepth-1) { System.out.print("Depth = "+depth+" -> "); printer.printMove(m,childState,tmp);}
				}
				// -----
				if (v <= alpha) {
					return v;
				}
				beta = Math.min(beta, v);
			}
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