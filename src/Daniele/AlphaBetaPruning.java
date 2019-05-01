package Daniele;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Daniele.minmaxprinter.MinMaxPrinter;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class AlphaBetaPruning {

	
	//@Matteo non � meglio usare int??
	private Map<Double, DanieleAction> mapMoves;
	private int maxDepth;
	
	/**
	 * algoritmo minmax con potature alpha-beta a profondità limitata
	 * 
	 * @params maxDepth
	 * 					livelli di profondità da esplorare
	 * @params ts
	 * 				rappresentazione dello stato
	 * @return
	 * 			ritorna la migliore azione 
	 */
	public DanieleAction AlphaBetaSearch(int maxDepth, ITablutState ts,MinMaxPrinter printer) {

		
		mapMoves= new HashMap<Double, DanieleAction>();
		this.maxDepth = maxDepth;
		
		if(ts.getState().getTurn().equals(Turn.WHITE)) {	//MAX player
			double v = MaxValue(maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ts,printer);
			//@Matteo stampa per debug
			//System.out.println("Valore scelto: " + v);
			printer.printDecision(v);
			return mapMoves.get(v);	//si recupera l'azione con il valore v più alto
		}
		else if(ts.getState().getTurn().equals(Turn.BLACK)) {	//MIN player
			double v = MinValue(maxDepth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ts,printer);
			//@Matteo stampa per debug
			printer.printDecision(v);
			return mapMoves.get(v);	//si recupera l'azione con il valore v più basso
		}
		//else System.out.println("partita conclusa: "+ ts.getState().getTurn());
		
		return null;
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
		//all'interuzione si ritorna un valore 
		if (cutoff(depth, state)) {
			return HeuristicTablut.HeuristicFunction(state) - depth;
		}
		double tmp;
		double v = Double.NEGATIVE_INFINITY;

		List<DanieleAction> moves = state.getAllLegalMoves();
	//	List<Action> moves = state.getTopLeftMoves();
		for (DanieleAction m : moves) {											//= per ogni coppia <azione, stato>
			ITablutState childState = state.getChildState(m);
			//@Matteo
			
			//@Matteo controllo su null dovrebbe essere inutile
			if(childState!=null) {//  della funzione successore		
			//	v = Math.max(v, MinValue(depth - 1, alpha, beta, childState));
			tmp=MinValue(depth - 1, alpha, beta, childState,printer);
			if(this.maxDepth==depth) { this.mapMoves.put(tmp, m);					//ci si salva in mappa le coppie <valore, mossa> del primo livello di profondità
			//@Matteo Debug
			printer.printChild(childState,tmp);
			}
			//@Matteo si possono invertire queste due istruzioni??
			v = Math.max(v, tmp);
			if (v >= beta) {
				printer.printReturn(v);
				return v;
			}

			alpha = Math.max(alpha, v);
			}
		}
		printer.printReturn(v);
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
			if(childState!=null) {
			//v = Math.min(v, MaxValue(depth - 1, alpha, beta, childState));
			tmp=MaxValue(depth - 1, alpha, beta, childState,printer);
			if(this.maxDepth==depth) {
				this.mapMoves.put(tmp, m);	
				//ci si salva in mappa le coppie <valore, mossa> del primo livello di profondità
				//@Matteo Debug
				printer.printChild(childState,tmp);
			//	System.out.println(childState);
			}
			v = Math.min(v,tmp);
			if (v <= alpha) {
				printer.printReturn(v);
				return v;
			}
			beta = Math.min(beta, v);
			}
		}
		printer.printReturn(v);
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
		return depth <= 0 || (!state.getState().getTurn().equals(Turn.WHITE) && !state.getState().getTurn().equals(Turn.BLACK));
	}
	
}
