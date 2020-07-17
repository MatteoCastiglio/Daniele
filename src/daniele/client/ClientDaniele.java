package daniele.client;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import daniele.ai.*;
import daniele.minmaxprinter.MinMaxPrinter;
import daniele.minmaxprinter.PrintMode;
import daniele.state.AshtonTablutGame;
import daniele.state.DanieleAction;
import daniele.state.ITablutGame;
import daniele.state.ITablutState;
import daniele.state.TransformableTablutState;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class ClientDaniele extends BasicClient{


	private AIGame ai = null;
	private final int OPENING_COUNTER = 1;
	private final int STARTING_DEPTH = 1;
	private final int MAX_DEPTH = 8;
	private Set<String> pastStates = new HashSet<String>();



	public ClientDaniele(String player,int time,PrintMode mode) throws  IOException {
		super(player, "Daniele");
		long maxtime= (time-2)*1000;
		HeuristicFunction h = new HeuristicTablut();
	    // AIGameSingleThread(HeuristicFunction h, long maxTime, MinMaxPrinter printer,  boolean useDrawCondition,
		// boolean orderingOptimization)
		//Use -1 for no time limit 
		ai = new AIGameSingleThread(h,maxtime,MinMaxPrinter.getPrinter(mode),true,true);	

	}


	@Override
	public void run() {
		try {
			// name declaration
			this.declareName();
			this.read();
			// GAME START
			try {
				if(this.getPlayer().equals(Turn.WHITE)) runWhite();
				else if(this.getPlayer().equals(Turn.BLACK)) runBlack();
			}
			catch(SocketException e){}
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//@turnCounter for hardcoded starting moves
	private void runBlack() throws ClassNotFoundException, IOException {
		DanieleAction action = null;
		ITablutGame game = new AshtonTablutGame();
		try {
			while(true) {	
				//reading opponent move
				this.read();
				pastStates.add(currentState.toLinearString());
				if(currentState== null || currentState.getTurn().equals(Turn.BLACKWIN) || currentState.getTurn().equals(Turn.WHITEWIN) || currentState.getTurn().equals(Turn.DRAW) )
					return;
				ITablutState  presentState = new TransformableTablutState(currentState,game);
				action = ai.chooseBestMove(STARTING_DEPTH, MAX_DEPTH, presentState ,pastStates);
				//writes selected move
				this.write(new Action(DanieleAction.coord(action.getRowFrom(),action.getColumnFrom()),DanieleAction.coord(action.getRowTo(),action.getColumnTo()),Turn.BLACK));
				//read updated state
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
		ITablutGame game = new AshtonTablutGame();
		try {
			while(true) {
				ITablutState  presentState = new TransformableTablutState(currentState,game);
				if(turnCounter<OPENING_COUNTER)
					action = WhiteOpening.nextMove(presentState, turnCounter);
				else {	
					action = ai.chooseBestMove(STARTING_DEPTH, MAX_DEPTH, presentState,pastStates);
				
				}
				turnCounter++;
				//writes selected move
				this.write(new Action(DanieleAction.coord(action.getRowFrom(),action.getColumnFrom()),DanieleAction.coord(action.getRowTo(),action.getColumnTo()),Turn.WHITE));
				//reads updated state
				this.read();
				pastStates.add(currentState.toLinearString());

				this.read();
				pastStates.add(currentState.toLinearString());
				//reads opponent move
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
		PrintMode mode = PrintMode.None;

		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println("Player= " + args[0].toUpperCase());
			role = (args[0]);
		}
		if (args.length > 1) {

			time= Integer.parseInt(args[1]);
			System.out.println("Time to choose action= " + args[1]);
		}
		
		if (args.length > 2) {
			
			if(args[2].equals("verbose")) {
				 mode = PrintMode.Verbose;
				 System.out.println("Verbose Mode Enabled");
			}
		}
		
		System.out.println("Selected client: " + args[0]);
		BasicClient client = new ClientDaniele(role,time,mode);
		System.out.println("GAME START");
		client.run();
		System.out.println("GAME END");


	}

}
