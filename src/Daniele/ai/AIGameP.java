package Daniele.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import Daniele.minmaxprinter.MinMaxPrinter;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;


public class AIGameP implements AIGame {

	private volatile boolean timeOver;
	private volatile Timer timer;
	private long maxTime;
	
	private final Object monitor;
	private double alphaM;
	private double betaM;
	private DanieleAction bestActionM;
	DanieleAction bestActionLastDepthM;
	double bestAlphaLastDepthM;
	private boolean useTraspositionTable;
	private MinMaxPrinter printer;
	private TraspositionTable traspositionTable = TraspositionTable.getInstance();

	public AIGameP(long maxTime,MinMaxPrinter printer,boolean useTraspositionTable) {		//con -1 non c'è limite di tempo
		this.maxTime = maxTime;
		monitor = new Object();
		alphaM = Double.NEGATIVE_INFINITY;
		betaM = Double.POSITIVE_INFINITY;
		bestActionM = null;
		bestActionLastDepthM = null;
		bestAlphaLastDepthM = Double.NEGATIVE_INFINITY;
		this.useTraspositionTable = useTraspositionTable;
		this.printer= printer;
	}

	public DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts,Set<State> pastStates){

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

			//double alphaM = Double.NEGATIVE_INFINITY;
			//DanieleAction bestActionM = null;

			//tiene traccia dei migliori valori all'ultima profondità. ritorna bestActionLastDepthM alla scadenza del tempo.
			//DanieleAction bestActionLastDepthM = null;
			//double bestAlphaLastDepthM = Double.NEGATIVE_INFINITY;

			for(int depth = startingDepth; depth <= maxDepth; depth++) {

				alphaM = Double.NEGATIVE_INFINITY;

				List<AIThread> threads = new ArrayList<AIThread>();
				int numberOfProcessors = Runtime.getRuntime().availableProcessors();
				int numberOfMoves = moves.size() / numberOfProcessors;
				//System.out.println("numberOfProcessors="+numberOfProcessors+", moves="+moves.size()+", numberOfMoves="+numberOfMoves);

				for(int i=0, t=1; i<moves.size(); i+=numberOfMoves, t++) {
					List<DanieleAction> threadMoves;
					if(t==numberOfProcessors) {threadMoves = moves.subList(i, moves.size()); i+=(moves.size()%numberOfProcessors);}
					else threadMoves = moves.subList(i, i+numberOfMoves);

					AIThread thread = new AIThread(threadMoves, depth, alphaM, Double.POSITIVE_INFINITY, ts, printer, startingDepth );
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

				if(timeOver) {
					timer.cancel();
					// -----
					time = (double)(System.nanoTime()-startTime) / 1_000_000_000.0;
					System.out.println("TIMEOVER: time="+time+", depth = "+depth+" -  bestActionLastDepthM: "+bestActionLastDepthM.toString());
					// -----
					return bestActionLastDepthM;
				}
				else {
					//si tiene traccia della migliore mossa alla profondità esaminata
					//bestAlphaLastDepthM = alphaM;
					//bestActionLastDepthM = bestActionM;
					for(AIThread t: threads) {
						if(t.getAlpha() > alphaM) {
							alphaM = t.getAlpha();
							bestActionLastDepthM = t.getBestAction();
						}
						
					}
				}

			}//for-depths

			//raggiunta la profondità massima senza il timeout
			timer.cancel();
			time = (double)(System.nanoTime()-startTime) / 1_000_000_000.0;
			System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepthM = "+bestActionLastDepthM.toString());
			return bestActionLastDepthM;		
		}

		else if(ts.getState().getTurn().equals(Turn.BLACK)) {	//MIN player
			List<DanieleAction> moves = ts.getAllLegalMoves();

			//double betaM = Double.POSITIVE_INFINITY;
			//DanieleAction bestActionM = null;

			//tiene traccia dei migliori valori all'ultima profondità. ritorna bestActionLastDepthM alla scadenza del tempo.
			//DanieleAction bestActionLastDepthM = null;
			//double bestBetaLastDepth = Double.POSITIVE_INFINITY;

			for(int depth = startingDepth; depth <= maxDepth; depth++) {

				betaM = Double.POSITIVE_INFINITY;

				List<AIThread> threads = new ArrayList<AIThread>();
				int numberOfProcessors = Runtime.getRuntime().availableProcessors();
				int numberOfMoves = moves.size() / numberOfProcessors;
				//System.out.println("numberOfProcessors="+numberOfProcessors+", moves="+moves.size()+", numberOfMoves="+numberOfMoves);

				for(int i=0, t=1; i<moves.size(); i+=numberOfMoves, t++) {
					List<DanieleAction> threadMoves;
					if(t==numberOfProcessors){threadMoves = moves.subList(i, moves.size()); i+=numberOfMoves;}
					else threadMoves = moves.subList(i, i+numberOfMoves);

					AIThread thread = new AIThread(threadMoves, depth, Double.NEGATIVE_INFINITY, betaM, ts, printer, startingDepth );
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

				if(timeOver) {
					timer.cancel();
					// -----
					time = (double)(System.nanoTime()-startTime) / 1_000_000_000.0;
					System.out.println("TIMEOVER: time="+time+", depth = "+depth+" -  bestActionLastDepthM: "+bestActionLastDepthM.toString());
					// -----
					return bestActionLastDepthM;
				}
				else {
					//si tiene traccia della migliore mossa alla profondità esaminata
					//bestAlphaLastDepthM = alphaM;
					//bestActionLastDepthM = bestActionM;
					for(AIThread t: threads) {
						if(t.getBeta() < betaM) {
							betaM = t.getBeta();
							bestActionLastDepthM = t.getBestAction();
						}
						
					}
				}

			}//for-depths

			//raggiunta la profondità massima senza il timeout
			timer.cancel();
			time = (double)(System.nanoTime()-startTime) / 1_000_000_000.0;
			System.out.println("Time="+time+", depth = "+ maxDepth +" - bestActionLastDepthM = "+bestActionLastDepthM.toString());
			return bestActionLastDepthM;
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


	
	
	
	
	public class AIThread extends Thread{

		private List<DanieleAction> threadMoves;
		private int depth;
		private double alpha;
		private double beta;
		private DanieleAction bestAction;
		private ITablutState state;
		private MinMaxPrinter printer;
		private int startingDepth;

		public AIThread(List<DanieleAction> threadMoves, int depth, double alpha, double beta, ITablutState state,
				MinMaxPrinter printer, int startingDepth) {
			super();
			this.threadMoves = threadMoves;
			this.depth = depth;
			this.alpha = alpha;
			this.beta = beta;
			this.state = state;
			this.printer = printer;
			this.startingDepth = startingDepth;
		}

		public double getAlpha() {
			return alpha;
		}
		
		public double getBeta() {
			return beta;
		}

		public DanieleAction getBestAction() {
			return bestAction;
		}

		@Override
		public void run(){
			
			if(state.getState().getTurn().equals(Turn.WHITE)) {	//MAX player
				//si provano tutte le mosse possibili		 - primo livello di minmax
				for(DanieleAction m : threadMoves) {

					ITablutState childState = state.getChildState(m);
					double v = MinValue(depth-1, alpha, Double.POSITIVE_INFINITY, childState,printer);

					//gestione del tempo : restituisce il migliore finora
					// l'ultima mossa possibile non viene presa in considerazione,
					// in quanto potrebbe essere errata a causa dell'arresto precoce dei calcoli.
					if(timeOver) { break; }

					//caso normale: aggiorna la nuova mossa migliore se si raggiunge 
					// un punteggio migliore assumendolo.
					//Nessuna potatura al livello superiore!
					if(v > alpha) {
						alpha = v;
						bestAction = m;
						if(alpha>alphaM && depth == startingDepth) {
							synchronized(monitor) {
								if(alpha>alphaM) {				//potrebbe servire nel caso si decidesse di partire con una profondità già abbastanza notevole - 
									bestActionLastDepthM = m;	//in modo che se scade il timer si avrà una già una bestActionLastDepthM prima di finire il for-moves_same_depth
									alphaM = alpha;
									//bestAlphaLastDepthM = alpha;
								}
							}
						}
						
					}

				}//for-moves_same_depth	
			}
			else if(state.getState().getTurn().equals(Turn.BLACK)) {	//MIN player
				//si provano tutte le mosse possibili		 - primo livello di minmax
				for(DanieleAction m : threadMoves) {

					ITablutState childState = state.getChildState(m);
					double v = MaxValue(depth-1, Double.NEGATIVE_INFINITY, beta, childState,printer);

					//gestione del tempo : restituisce il migliore finora
					// l'ultima mossa possibile non viene presa in considerazione,
					// in quanto potrebbe essere errata a causa dell'arresto precoce dei calcoli.
					if(timeOver) { break; }

					//caso normale: aggiorna la nuova mossa migliore se si raggiunge 
					// un punteggio migliore assumendolo.
					//Nessuna potatura al livello superiore!
					if(v < beta) {
						beta = v;
						bestAction = m;
						if(beta<betaM && depth == startingDepth) {
							synchronized(monitor) {
								if(beta<betaM) {				//potrebbe servire nel caso si decidesse di partire con una profondità già abbastanza notevole - 
									bestActionLastDepthM = m;	//in modo che se scade il timer si avrà una già una bestActionLastDepthM prima di finire il for-moves_same_depth
									betaM = beta;
									//bestAlphaLastDepthM = beta;
								}
							}
						}
					}

				}//for-moves_same_depth
			}
		}
		

	}
	
	
}