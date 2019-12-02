package Daniele.client;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import Daniele.ai.*;
import Daniele.minmaxprinter.MinMaxPrinter;
import Daniele.minmaxprinter.PrintMode;
import Daniele.state.DanieleAction;
import Daniele.state.TablutState;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class ClientDaniele extends TablutClient{


	private AIGame ai = null;
	private final int OPENING_COUNTER = 1;
	private final int STARTING_DEPTH = 1;
	private final int MAX_DEPTH = 7;
	//private Set<State> pastStates = new HashSet<State>();
	private Set<String> pastStates = new HashSet<String>();
	private int nwhites =0;
	private int nblacks = 0;
	private int coord[] = new int[2];


	public ClientDaniele(String player,int time) throws  IOException {
		super(player, "Daniele");
		long maxtime= (time-2)*1000;
		HeuristicFunction h = new HeuristicTablut();
		//Questi valori al momento quelli che permettono di ottenere risutai migliori
		ai = new AIGameSingleThread(h,maxtime,MinMaxPrinter.getPrinter(PrintMode.Simple),false,true,true);	//con -1 non c'è limite di tempo	//usa AIGameSingleThread o AIGameP
		//ai = new AIGameP2(30000,MinMaxPrinter.getPrinter(PrintMode.Simple),false,false,true);
		//ai = new AIGameP(30000,MinMaxPrinter.getPrinter(PrintMode.Simple),false);
	}


	private void setup()
	{
		nwhites =0;
		nblacks = 0;
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

	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int time = 60;
		String role = "";
		String name = "Daniele";
		// TODO: change the behavior?
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {

			time= Integer.parseInt(args[1]);
		}

		System.out.println("Selected client: " + args[0]);


		TablutClient client = new ClientDaniele(role,time);

		client.run();
		System.out.println("partita finita");


	}

}
