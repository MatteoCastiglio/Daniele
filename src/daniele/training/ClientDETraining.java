package daniele.training;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import daniele.ai.WhiteOpening;
import daniele.client.BasicClient;
import daniele.minmaxprinter.MinMaxPrinter;
import daniele.minmaxprinter.PrintMode;
import daniele.state.AshtonTablutGame;
import daniele.state.DanieleAction;
import daniele.state.ITablutGame;
import daniele.state.ITablutState;
import daniele.state.TransformableTablutState;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class ClientDETraining extends BasicClient {
	//private AlphaBetaPruning ab = null;							//scommenta per AlphaBetaPruning
	private AIGame ai = null;                                        //usa AIGameSingleThread o AIGameP
	private final int OPENING_COUNTER = 0;
	private final int STARTING_DEPTH = 2;
	private final int MAX_DEPTH = 11;
	//private Set<State> pastStates = new HashSet<State>();
	private Set<String> pastStates = new HashSet<String>();
	//@Matteo
	private int nwhites = 0;
	private int nblacks = 0;
	private int coord[] = new int[2];


	public ClientDETraining(double[] weights, String player, String name) throws IOException {
		super(player, name);
		//ab = new AlphaBetaPruning();									//scommenta per AlphaBetaPrunin
		ai = new AIGameSingleThreadDE(15000, MinMaxPrinter.getPrinter(PrintMode.None), false, true, true, weights);    //con -1 non c'è limite di tempo	//usa AIGameSingleThread o AIGameP

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
				if (this.getPlayer().equals(Turn.WHITE)) runWhite();
				else if (this.getPlayer().equals(Turn.BLACK)) runBlack();
			} catch (SocketException e) {

			}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//@turnCounter serve per gestire le mosse di apertura
	private void runBlack() throws ClassNotFoundException, IOException {
		DanieleAction action = null;
		ITablutGame game = new AshtonTablutGame();
		try {
			while (true) {
				//legge stato corrente dal server (mossa avversario)
				this.read();
				ITablutState presentState = new TransformableTablutState(currentState,game);
				pastStates.add(currentState.toLinearString());
				if (currentState == null || currentState.getTurn().equals(Turn.BLACKWIN) || currentState.getTurn().equals(Turn.WHITEWIN) || currentState.getTurn().equals(Turn.DRAW))
					return;
				else
					//action = ab.AlphaBetaSearch(depth, new TablutState(currentState,nwhites,nblacks,coord,whitesMoved),MinMaxPrinter.getPrinter(PrintMode.Simple));			//scommenta per AlphaBetaPrunin
					action = ai.chooseBestMove(STARTING_DEPTH, MAX_DEPTH, presentState, pastStates);
				//comunica l'azione al server
				this.write(new Action(DanieleAction.coord(action.getRowFrom(), action.getColumnFrom()), DanieleAction.coord(action.getRowTo(), action.getColumnTo()), Turn.BLACK));
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
		int turnCounter = 0;
		try {
			while (true) {
				//scelta mossa

				ITablutGame game = new AshtonTablutGame();
				ITablutState presentState = new TransformableTablutState(currentState,game);



				if (turnCounter < OPENING_COUNTER)
					action = WhiteOpening.nextMove(presentState, turnCounter);
				else
					//action = ab.AlphaBetaSearch(depth, new TablutState(this.getCurrentState(),nwhites,nblacks,coord,whitesMoved),MinMaxPrinter.getPrinter(PrintMode.Simple));			//scommenta per AlphaBetaPrunin
					action = ai.chooseBestMove(STARTING_DEPTH, MAX_DEPTH, presentState, pastStates);
				turnCounter++;
				//comunica l'azione al server
				this.write(new Action(DanieleAction.coord(action.getRowFrom(), action.getColumnFrom()), DanieleAction.coord(action.getRowTo(), action.getColumnTo()), Turn.WHITE));
				//legge stato corrente modificato dal server
				this.read();
				pastStates.add(currentState.toLinearString());

				this.read();
				pastStates.add(currentState.toLinearString());
				//legge stato corrente dal server (mossa avversario)
				if (currentState == null || currentState.getTurn().equals(Turn.BLACKWIN) || currentState.getTurn().equals(Turn.WHITEWIN) || currentState.getTurn().equals(Turn.DRAW))
					return;
			}
		} catch (EOFException e) {
			System.out.println("Partita finita!");
		}
	}

	public static void train(double[] weights, String role, String name) throws UnknownHostException, IOException, ClassNotFoundException {

		System.out.println("Selected client: " + role);


		BasicClient client = new ClientDETraining(weights, role, name);

		client.run();
		System.out.println("partita finita");


	}
}
