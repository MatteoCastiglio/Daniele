package Daniele.training;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;


import Daniele.ai.BlackOpening;
import Daniele.ai.DanieleAction;
import Daniele.ai.TablutState;
import Daniele.ai.WhiteOpening;
import Daniele.minmaxprinter.MinMaxPrinter;
import Daniele.minmaxprinter.PrintMode;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class ClientDETraining extends TablutClient{
	//private AlphaBetaPruning ab = null;							//scommenta per AlphaBetaPruning
	private AIGame ai = null;										//usa AIGameSingleThread o AIGameP
	private final int OPENING_COUNTER = 0;
	private final int STARTING_DEPTH = 5;
	private final int MAX_DEPTH = 9;
	//private Set<State> pastStates = new HashSet<State>();
	private Set<String> pastStates = new HashSet<String>();
	//@Matteo
	private int nwhites =0;
	private int nblacks = 0;
	private int coord[] = new int[2];
	private int whitesMovedinFlowDirection = 0;
	private int blacksMovedinFlowDirection =0;


	public ClientDETraining(double[] weights, String player, String name) throws  IOException {
		super(player, name);
		//ab = new AlphaBetaPruning();									//scommenta per AlphaBetaPrunin
		ai = new AIGameSingleThreadDE(18000,MinMaxPrinter.getPrinter(PrintMode.None),false,true,true, weights);	//con -1 non c'è limite di tempo	//usa AIGameSingleThread o AIGameP

	}


	private void setup()
	{

		nwhites =0;
		nblacks = 0;
		whitesMovedinFlowDirection = 0;
		blacksMovedinFlowDirection = 0;
		for(int i =0; i< 9; i++)
			for(int j =0; j< 9; j++)
			{if(currentState.getPawn(i, j).equals(Pawn.WHITE))
				nwhites++;
			else if( currentState.getPawn(i, j).equals(Pawn.BLACK))
				nblacks++;
			else if(currentState.getPawn(i, j).equals(Pawn.KING)) {coord[0]=i; coord[1]=j;}
			}

	}

	@Override
	public void run() {

		/* generalizzazione per altri giochi */

		try {
			//comunica nome al server
			this.declareName();

			//lettura stato attuale server
			this.read();

			//INIZIO PARTITA
			try {
				if(this.getPlayer().equals(Turn.WHITE)) runWhite();
				else if(this.getPlayer().equals(Turn.BLACK)) runBlack();
			}
			catch(SocketException e)
			{

			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//@turnCounter serve per gestire le mosse di apertura
	private void runBlack() throws ClassNotFoundException, IOException {
		DanieleAction action = null;
		int turnCounter = 0;
		try {
			while(true) {	
				//legge stato corrente dal server (mossa avversario)
				this.read();
				pastStates.add(currentState.toLinearString());
				if(currentState== null || currentState.getTurn().equals(Turn.BLACKWIN) || currentState.getTurn().equals(Turn.WHITEWIN) || currentState.getTurn().equals(Turn.DRAW) )
					return;
				setup();


				if(turnCounter<OPENING_COUNTER)
					action = BlackOpening.nextMove(new TablutState(currentState,nwhites,nblacks,coord), turnCounter);
				else
					//action = ab.AlphaBetaSearch(depth, new TablutState(currentState,nwhites,nblacks,coord,whitesMoved),MinMaxPrinter.getPrinter(PrintMode.Simple));			//scommenta per AlphaBetaPrunin
					action = ai.chooseBestMove(STARTING_DEPTH, MAX_DEPTH, new TablutState(currentState,nwhites,nblacks,coord),pastStates);
				turnCounter++;
				//comunica l'azione al server
				this.write(new Action(DanieleAction.coord(action.getRowFrom(),action.getColumnFrom()),DanieleAction.coord(action.getRowTo(),action.getColumnTo()),Turn.BLACK));
				//legge stato corrente modificato dal server
				this.read();
				pastStates.add(currentState.toLinearString());
			}
		} catch (EOFException e) {
			System.out.println("Partita finita!");
		}
	}

	private void runWhite() throws ClassNotFoundException, IOException {
		DanieleAction action = null;
		int turnCounter=0;
		try {
			while(true) {
				//scelta mossa
				// @Matteo conteggio iniziale questo penso sia inevitabile, ma si fa una volta sola!!!!!


				setup();


				if(turnCounter<OPENING_COUNTER)
					action = WhiteOpening.nextMove(new TablutState(currentState,nwhites,nblacks,coord), turnCounter);
				else
					//action = ab.AlphaBetaSearch(depth, new TablutState(this.getCurrentState(),nwhites,nblacks,coord,whitesMoved),MinMaxPrinter.getPrinter(PrintMode.Simple));			//scommenta per AlphaBetaPrunin
					action = ai.chooseBestMove(STARTING_DEPTH, MAX_DEPTH, new TablutState(currentState,nwhites,nblacks,coord),pastStates);
				turnCounter++;
				//comunica l'azione al server
				this.write(new Action(DanieleAction.coord(action.getRowFrom(),action.getColumnFrom()),DanieleAction.coord(action.getRowTo(),action.getColumnTo()),Turn.WHITE));
				//legge stato corrente modificato dal server
				this.read();
				pastStates.add(currentState.toLinearString());

				this.read();
				pastStates.add(currentState.toLinearString());
				//legge stato corrente dal server (mossa avversario)
				if(currentState== null || currentState.getTurn().equals(Turn.BLACKWIN) || currentState.getTurn().equals(Turn.WHITEWIN) || currentState.getTurn().equals(Turn.DRAW) )
					return;
			}
		} catch (EOFException e) {
			System.out.println("Partita finita!");
		}
	}

	public static void train(double[] weights, String role, String name) throws UnknownHostException, IOException, ClassNotFoundException {

		System.out.println("Selected client: " + role);


		TablutClient client = new ClientDETraining(weights, role, name);

		client.run();
		System.out.println("partita finita");


	}
}
