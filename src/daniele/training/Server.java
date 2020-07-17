package daniele.training;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.*;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.gui.Gui;
import it.unibo.ai.didattica.competition.tablut.util.StreamUtils;

import com.google.gson.Gson;

/**
 * this class represent the server of the match: 2 clients with TCP connection
 * can connect and start to play
 * 
 * @author A.Piretti, Andrea Galassi
 *
 */
public class Server implements Runnable {

	//---DE---
	private int movesOLD = 0;
	private int movesNEW = 0;
	private int maxMoves = 50;
	private int timesKingIsSurrondedFrom7 = 0;
	private boolean forcedDraw = false;
	public static String data_dir = "data";
	protected static final String OUTCOME_FILE = "outcomes.txt";
	//--------

	/**
	 * State of the game
	 */
	private State state;
	/**
	 * Number of seconds allowed for a decision
	 */
	private int time;
	/**
	 * Number of states kept in memory for the detection of a draw
	 */
	private int moveCache;
	/**
	 * Whether the gui must be enabled or not
	 */
	private boolean enableGui;

	/**
	 * JSON string used to communicate
	 */
	private String theGson;
	/**
	 * Action chosen by a player
	 */
	private Action move;
	/**
	 * Errors allowed
	 */
	private int errors;
	/**
	 * Repeated positions allowed
	 */
	private int repeated;

	private ServerSocket socketWhite;
	private ServerSocket socketBlack;

	/**
	 * Counter for the errors of the black player
	 */
	private int blackErrors;
	/**
	 * Counter for the errors of the white player
	 */
	private int whiteErrors;

	private int cacheSize;

	private Game game;
	private Gson gson;
	private Gui theGui;
	/**
	 * Integer that represents the game type
	 */
	private int gameC;

	public Server(int timeout, int cacheSize, int numErrors, int repeated, int game, boolean gui) {
		this.gameC = game;
		this.enableGui = gui;
		this.time = timeout;
		this.moveCache = cacheSize;
		this.errors = numErrors;
		this.cacheSize = cacheSize;
		this.gson = new Gson();
	}

	public void initializeGUI(State state) {
		this.theGui = new Gui(this.gameC);
		this.theGui.update(state);
	}

	/**
	 * Server initialiazer.
	 * 
	 * @param args
	 *            the time for the move, the size of the cache for monitoring
	 *            draws, the number of errors allowed, the type of game, whether
	 *            the GUI should be used or not
	 * 
	 */
	public static void main(String[] args) {
		int time = 20;
		int moveCache = -1;
		int repeated = 0;
		int errors = 0;
		int gameChosen = 4;
		boolean enableGui = true;

		String usage = "Usage: java Server [-t <time>] [-c <cache>] [-e <errors>] [-s <repeatedState>] [-r <game rules>] [-g <enableGUI>]\n"
				+ "\tenableGUI must be >0 for enabling it; default 1"
				+ "\tgame rules must be an integer; 1 for Tablut, 2 for Modern, 3 for Brandub, 4 for Ashton; default: 4\n"
				+ "\trepeatedStates must be an integer >= 0; default: 0\n"
				+ "\terrors must be an integer >= 0; default: 0\n"
				+ "\tcache must be an integer, negative value means infinite; default: infinite\n"
				+ "time must be an integer (number of seconds); default: 60";
		for (int i = 0; i < args.length - 1; i++) {

			if (args[i].equals("-t")) {
				i++;
				try {
					time = Integer.parseInt(args[i]);
					if (time < 1) {
						System.out.println("Time format not allowed!");
						System.out.println(args[i]);
						System.out.println(usage);
						System.exit(1);
					}
				} catch (Exception e) {
					System.out.println("The time format is not correct!");
					System.out.println(args[i]);
					System.out.println(usage);
					System.exit(1);
				}
			}

			if (args[i].equals("-c")) {
				i++;
				try {
					moveCache = Integer.parseInt(args[i]);
				} catch (Exception e) {
					System.out.println("Number format is not correct!");
					System.out.println(args[i]);
					System.out.println(usage);
					System.exit(1);
				}
			}

			if (args[i].equals("-e")) {
				i++;
				try {
					errors = Integer.parseInt(args[i]);
					if (errors < 0) {
						System.out.println("Error format not allowed!");
						System.out.println(args[i]);
						System.out.println(usage);
						System.exit(1);
					}
				} catch (Exception e) {
					System.out.println("The error format is not correct!");
					System.out.println(args[i]);
					System.out.println(usage);
					System.exit(1);
				}

			}
			if (args[i].equals("-s")) {
				i++;
				try {
					repeated = Integer.parseInt(args[i]);
					if (repeated < 0) {
						System.out.println("RepeatedStates format not allowed!");
						System.out.println(args[i]);
						System.out.println(usage);
						System.exit(1);
					}
				} catch (Exception e) {
					System.out.println("The RepeatedStates format is not correct!");
					System.out.println(args[i]);
					System.out.println(usage);
					System.exit(1);
				}

			}
			if (args[i].equals("-r")) {
				i++;
				try {
					gameChosen = Integer.parseInt(args[i]);
					if (gameChosen < 0 || gameChosen > 4) {
						System.out.println("Game format not allowed!");
						System.out.println(args[i]);
						System.out.println(usage);
						System.exit(1);
					}
				} catch (Exception e) {
					System.out.println("The game format is not correct!");
					System.out.println(args[i]);
					System.out.println(usage);
					System.exit(1);
				}
			}

			if (args[i].equals("-g")) {
				i++;
				try {
					int gui = Integer.parseInt(args[i]);
					if (gui <= 0) {
						enableGui = false;
					}
				} catch (Exception e) {
					System.out.println("The enableGUI format is not correct!");
					System.out.println(args[i]);
					System.out.println(usage);
					System.exit(1);
				}
			}

		}

		// Start the server
		Server engine = new Server(time, moveCache, errors, repeated, gameChosen, enableGui);
		engine.run();
	}

	/**
	 * This class represents the stream who is waiting for the move from the
	 * client (JSON format)
	 * 
	 * @author A.Piretti
	 *
	 */
	private class TCPInput implements Runnable {
		private DataInputStream theStream;

		public TCPInput(DataInputStream theS) {
			this.theStream = theS;
		}

		public void run() {
			try {
				theGson = StreamUtils.readString(this.theStream);

			} catch (Exception e) {
			}
		}
	}

	/**
	 * This method starts the proper game. It waits the connections from 2
	 * clients, check the move and update the state. There is a timeout that
	 * interrupts games that last too much
	 */
	public void run() {
		/**
		 * Number of hours that a game can last before the timeout
		 */
		int hourlimit = 5;
		/**
		 * Endgame state reached?
		 */
		boolean endgame = false;
		/**
		 * Name of the systemlog
		 */
		String logs_folder = "logs";
		Path p = Paths.get(logs_folder + File.separator + new Date().getTime() + "_systemLog.txt");
		p = p.toAbsolutePath();
		String sysLogName = p.toString();
		Logger loggSys = Logger.getLogger("SysLog");
		try {
			new File(logs_folder).mkdirs();
			System.out.println(sysLogName);
			File systemLog = new File(sysLogName);
			if (!systemLog.exists()) {
				systemLog.createNewFile();
			}
			FileHandler fh = null;
			fh = new FileHandler(sysLogName, true);
			loggSys.addHandler(fh);
			fh.setFormatter(new SimpleFormatter());
			loggSys.setLevel(Level.FINE);
			loggSys.fine("Accensione server");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		switch (this.gameC) {
		case 1:
			loggSys.fine("Partita di ClassicTablut");
			break;
		case 2:
			loggSys.fine("Partita di ModernTablut");
			break;
		case 3:
			loggSys.fine("Partita di Brandub");
			break;
		case 4:
			loggSys.fine("Partita di Tablut");
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}

		Date starttime = new Date();
		Thread t;
		Socket white = null;
		Socket black = null;
		/**
		 * Channel to receive the move of the white player
		 */
		DataInputStream whiteMove = null;
		/**
		 * Channel to receive the move of the black player
		 */
		DataInputStream blackMove = null;
		/**
		 * Channel to send the state to the white player
		 */
		DataOutputStream whiteState = null;
		/**
		 * Channel to send the state to the black player
		 */
		DataOutputStream blackState = null;
		System.out.println("Waiting for connections...");

		String whiteName = "WP";
		String blackName = "BP";

		/**
		 * Socket of the current player
		 */
		TCPInput tin = null;
		TCPInput Turnwhite = null;
		TCPInput Turnblack = null;

		// ESTABLISH CONNECTIONS AND NAME READING
		try {
			this.socketWhite = new ServerSocket(5800);
			this.socketBlack = new ServerSocket(5801);

			white = this.socketWhite.accept();
			loggSys.fine("Accettata connessione con client giocatore Bianco");
			whiteMove = new DataInputStream(white.getInputStream());
			whiteState = new DataOutputStream(white.getOutputStream());
			Turnwhite = new TCPInput(whiteMove);

			// NAME READING
			t = new Thread(Turnwhite);
			t.start();
			loggSys.fine("Lettura nome player bianco in corso..");
			try {
				// timer for the move
				int counter = 0;
				while (counter < time && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// timeout for name declaration
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				loggSys.warning("Chiusura sistema per timeout");
				System.exit(0);
			}

			whiteName = this.gson.fromJson(theGson, String.class);
			// SECURITY STEP: dropping unproper characters
			String temp = "";
			for (int i = 0; i < whiteName.length() && i < 10; i++) {
				char c = whiteName.charAt(i);
				if (Character.isAlphabetic(c) || Character.isDigit(c))
					temp += c;
			}
			whiteName = temp;
			System.out.println("White player name:\t" + whiteName);
			loggSys.fine("White player name:\t" + whiteName);

			black = this.socketBlack.accept();
			loggSys.fine("Accettata connessione con client giocatore Nero");
			blackMove = new DataInputStream(black.getInputStream());
			blackState = new DataOutputStream(black.getOutputStream());
			Turnblack = new TCPInput(blackMove);

			// NAME READING
			t = new Thread(Turnblack);
			t.start();
			loggSys.fine("Lettura nome player nero in corso..");
			try {
				// timer for the move
				int counter = 0;
				while (counter < time && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// timeout for name declaration
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				loggSys.warning("Chiusura sistema per timeout");
				System.exit(0);
			}

			blackName = this.gson.fromJson(theGson, String.class);
			// SECURITY STEP: dropping unproper characters
			temp = "";
			for (int i = 0; i < blackName.length() && i < 10; i++) {
				char c = blackName.charAt(i);
				if (Character.isAlphabetic(c) || Character.isDigit(c))
					temp += c;
			}
			System.out.println("Black player name:\t" + blackName);
			loggSys.fine("Black player name:\t" + blackName);

		} catch (IOException e) {
			System.out.println("Socket error....");
			loggSys.warning("Errore connessioni");
			loggSys.warning("Chiusura sistema");
			System.exit(1);
		}

		switch (this.gameC) {
		case 1:
			state = new StateTablut();
			this.game = new GameTablut(moveCache);
			break;
		case 2:
			state = new StateTablut();
			this.game = new GameModernTablut(moveCache);
			break;
		case 3:
			state = new StateBrandub();
			this.game = new GameTablut(moveCache);
			break;
		case 4:
			state = new StateTablut();
			state.setTurn(State.Turn.WHITE);
			this.game = new GameAshtonTablut(state, repeated, this.cacheSize, "logs", whiteName, blackName);
			break;
		default:
			System.out.println("Error in game selection");
			System.exit(4);
		}
		if (this.enableGui) {
			this.initializeGUI(state);
		}
		System.out.println("Clients connected..");

		// SEND INITIAL STATE

		try {
			theGson = gson.toJson(state);
			StreamUtils.writeString(whiteState, theGson);
			StreamUtils.writeString(blackState, theGson);
			loggSys.fine("Invio messaggio ai giocatori");
			if (enableGui) {
				theGui.update(state);
			}
		} catch (IOException e) {
			e.printStackTrace();
			loggSys.fine("Errore invio messaggio ai giocatori");
			loggSys.warning("Chiusura sistema");
			System.exit(1);
		}

		// GAME CYCLE
		while (!endgame) {
			// RECEIVE MOVE

			// System.out.println("State: \n"+state.toString());
			System.out.println("Waiting for " + state.getTurn() + "...");
			Date ti = new Date();
			long hoursoccurred = (ti.getTime() - starttime.getTime()) / 60 / 60 / 1000;
			if (hoursoccurred > hourlimit) {
				System.out.println("TIMEOUT! END OF THE GAME...");
				loggSys.warning("Chiusura programma per timeout di cinque ore");
			}

			switch (state.getTurn()) {
			case WHITE:
				tin = Turnwhite;
				break;
			case BLACK:
				tin = Turnblack;
				break;
			case BLACKWIN:
				break;
			case WHITEWIN:
				break;
			case DRAW:
				break;
			default:
				loggSys.warning("Chiusura sistema per errore turno");
				System.exit(4);
			}
			// create the process that listen the answer
			t = new Thread(tin);
			t.start();
			loggSys.fine("Lettura mossa player " + state.getTurn() + " in corso..");
			try {
				// timer for the move
				int counter = 0;
				while (counter < time && t.isAlive()) {
					Thread.sleep(1000);
					counter++;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// loss for timeout
			if (t.isAlive()) {
				System.out.println("Timeout!!!!");
				System.out.println("Player " + state.getTurn().toString() + " has lost!");
				loggSys.warning("Timeout! Player " + state.getTurn() + " lose!");
				loggSys.warning("Chiusura sistema per timeout");
				
				// Append the outcome to the outcome file											<- DE
				try {
					PrintStream out = new PrintStream(new FileOutputStream(new File(data_dir, OUTCOME_FILE), true));
					String delim = ",";
					out.print(System.currentTimeMillis() + delim + "PlayerOLD" + delim + movesOLD + delim + "PlayerNEW" + delim + movesNEW + delim);


					if (state.getTurn().equalsTurn(StateTablut.Turn.DRAW.toString())) {
						System.out.println("RESULT: DRAW");
						out.println("F"+StateTablut.Turn.DRAW.toString());
					}

					out.close();
				} catch (Exception e) {
					System.err.println("Failed to append outcome to '" + OUTCOME_FILE + "': ");
					e.printStackTrace();
				}			//																		<- DE
				
				System.exit(0);
			}

			// APPLY MOVE
			// translate the string into an action object
			move = this.gson.fromJson(theGson, Action.class);
			loggSys.fine("Move received.\t" + move.toString());
			System.out.println("Suggested move: " + move.toString());

			try {
				//---DE---
				if(whiteName.equals("PlayerOLD") && state.getTurn().equals(Turn.WHITE)) this.movesOLD++;
				if(blackName.equals("PlayerOLD") && state.getTurn().equals(Turn.BLACK)) this.movesOLD++;
				if(whiteName.equals("PlayerNEW") && state.getTurn().equals(Turn.WHITE)) this.movesNEW++;
				if(blackName.equals("PlayerNEW") && state.getTurn().equals(Turn.BLACK)) this.movesNEW++;
				//--------
				// aggiorna tutto e determina anche eventuali fine partita
				state = this.game.checkMove(state, move);
				//---DE---
				if(movesNEW+movesOLD>maxMoves*2 /*&& isKingSurronded(state)
						&& (state.getTurn().equals(Turn.BLACK)||state.getTurn().equals(Turn.WHITE))*/) {state.setTurn(Turn.DRAW); forcedDraw=true;}
				//--------
			} catch (Exception e) {
				// exception means error, therefore increase the error counters
				if (state.getTurn().equalsTurn("B")) {
					this.blackErrors++;

					if (this.blackErrors > errors) {
						System.out.println("TOO MANY ERRORS FOR BLACK PLAYER; PLAYER WHITE WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore nero");
						System.exit(1);
					} else {
						System.out.println("Error for black player...");
					}
				}
				if (state.getTurn().equalsTurn("W")) {
					this.whiteErrors++;
					if (this.whiteErrors > errors) {
						System.out.println("TOO MANY ERRORS FOR WHITE PLAYER; PLAYER BLACK WIN!");
						e.printStackTrace();
						loggSys.warning("Chiusura sistema per troppi errori giocatore bianco");
						System.exit(1);
					} else {
						System.out.println("Error for white player...");
					}
				}
			}

			// SEND STATE TO PLAYERS
			try {
				theGson = gson.toJson(state);
				StreamUtils.writeString(whiteState, theGson);
				StreamUtils.writeString(blackState, theGson);
				loggSys.fine("Invio messaggio ai client");
				if (enableGui) {
					theGui.update(state);
				}
			} catch (IOException e) {
				e.printStackTrace();
				loggSys.warning("Errore invio messaggio ai client");
				loggSys.warning("Chiusura sistema");
				System.exit(1);
			}

			// CHECK END OF GAME
			if (!state.getTurn().equalsTurn("W") && !state.getTurn().equalsTurn("B")) {
				System.out.println("END OF THE GAME");

				// Append the outcome to the outcome file											<- DE
				try {
					PrintStream out = new PrintStream(new FileOutputStream(new File(data_dir, OUTCOME_FILE), true));
					String delim = ",";
					out.print(System.currentTimeMillis() + delim + "PlayerOLD" + delim + movesOLD + delim + "PlayerNEW" + delim + movesNEW + delim);


					if (state.getTurn().equalsTurn(StateTablut.Turn.DRAW.toString())) {
						System.out.println("RESULT: DRAW");
						if(forcedDraw) out.println("F"+StateTablut.Turn.DRAW.toString());
						else out.println(StateTablut.Turn.DRAW.toString());
					}
					if (state.getTurn().equalsTurn(StateTablut.Turn.WHITEWIN.toString())) {
						System.out.println("RESULT: PLAYER WHITE WIN");
						out.println(StateTablut.Turn.WHITEWIN.toString());
					}
					if (state.getTurn().equalsTurn(StateTablut.Turn.BLACKWIN.toString())) {
						System.out.println("RESULT: PLAYER BLACK WIN");
						out.println(StateTablut.Turn.BLACKWIN.toString());
					}
					endgame = true;


					out.close();
				} catch (Exception e) {
					System.err.println("Failed to append outcome to '" + OUTCOME_FILE + "': ");
					e.printStackTrace();
				}
			}
		}
		System.exit(0);
	}

	private boolean isKingSurronded(State state2) {
		int[] coordKing = getCoordKing(state2);
		int count = 0;
		Pawn p = state2.getBoard()[coordKing[0]-1][coordKing[1]];	//sopra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0]-1,coordKing[1],state2)) count++;
		p = state2.getBoard()[coordKing[0]+1][coordKing[1]];		//sotto
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0]+1,coordKing[1],state2)) count++;
		p = state2.getBoard()[coordKing[0]][coordKing[1]-1];		//sinistra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0],coordKing[1]-1,state2)) count++;
		p = state2.getBoard()[coordKing[0]][coordKing[1]+1];		//destra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0],coordKing[1]+1,state2)) count++;
		p = state2.getBoard()[coordKing[0]-1][coordKing[1]-1];		//sopra a sinistra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0]-1,coordKing[1]-1,state2)) count++;
		p = state2.getBoard()[coordKing[0]-1][coordKing[1]+1];		//sopra a destra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0]-1,coordKing[1]+1,state2)) count++;
		p = state2.getBoard()[coordKing[0]+1][coordKing[1]-1];		//sotto a sinistra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0]+1,coordKing[1]-1,state2)) count++;
		p = state2.getBoard()[coordKing[0]+1][coordKing[1]+1];		//sotto a sinistra
		if(!p.equals(Pawn.EMPTY) || isPawnAccampamento(coordKing[0]+1,coordKing[1]+1,state2)) count++;

		else if(count>=7 && this.timesKingIsSurrondedFrom7>=3) return true;
		else if(count>=7) this.timesKingIsSurrondedFrom7++;
		else if(count<7) this.timesKingIsSurrondedFrom7=0;
		return false;
	}
	
	private int[] getCoordKing(State state2) {

		int coord[] = new int[2];
		for (int i = 0; i < state2.getBoard().length; i++) {
			for (int j = 0; j < state2.getBoard().length; j++) {
				if(state2.getBoard()[i][j].equals(Pawn.KING)) {coord[0]=i; coord[1]=j; return coord;}
			}
		}
		return coord; //dovrebbe semrpe esserci il re, altrimenti la partita è conclusa
	}
	
	private boolean isPawnAccampamento(int i, int j, State state2) {
		int middle = (state2.getBoard().length-1)/2;

		//parti di accampamenti sui bordi
		if( (i>=middle-1 && i<=middle+1) && (j==0 || j==state2.getBoard().length-1) ) return true;
		if( (i==0 || i==state2.getBoard().length-1) && (j>=middle-1 && j<=middle+1) ) return true;
		//parti di accampamenti interni
		if( i==middle && (j==1 || j==state2.getBoard().length-2) ) return true;
		if( (i==1 || i==state2.getBoard().length-2) && j==middle ) return true;

		//trono 														(non dovrebbe servire) -> si presuppone che quando il re si muove questo pawn diventi Pawn.THRONE
		//if( i==middle && j==middle ) return true;

		return false;
	}

}