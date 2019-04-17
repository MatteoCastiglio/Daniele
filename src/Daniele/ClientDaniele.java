package Daniele;

import java.io.IOException;
import java.net.UnknownHostException;

import Daniele.minmaxprinter.MinMaxPrinter;
import Daniele.minmaxprinter.PrintMode;
import it.unibo.ai.didattica.competition.tablut.client.TablutClient;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class ClientDaniele extends TablutClient{

	private int depth;
	private AlphaBetaPruning ab = null;
	
	public ClientDaniele(String player) throws UnknownHostException, IOException {
		super(player, "Daniele");
		this.depth = 3;
		ab = new AlphaBetaPruning();
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
			if(this.getPlayer().equals(Turn.WHITE)) runWhite();
			else if(this.getPlayer().equals(Turn.BLACK)) runBlack();
		
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void runBlack() throws ClassNotFoundException, IOException {
		Action action = null;
		while(true) {	
			//legge stato corrente dal server (mossa avversario)
			this.read();
			//scelta mossa
			
			// @Matteo conteggio iniziale questo penso sia inevitabile, ma si fa una volta sola!!!!!
			int nwhites =0;
			int nblacks = 0;
			for(int i =0; i< 9; i++)
				for(int j =0; j< 9; j++)
			{if(currentState.getPawn(i, j).equals(Pawn.WHITE))
				nwhites++;
			else if( currentState.getPawn(i, j).equals(Pawn.BLACK))
				nblacks++;
			}
			action = ab.AlphaBetaSearch(depth, new TablutState(this.getCurrentState(),nwhites,nblacks),MinMaxPrinter.getPrinter(PrintMode.Simple));
			//comunica l'azione al server
			this.write(action);
			//legge stato corrente modificato dal server
			this.read();
		}
	}

	private void runWhite() throws ClassNotFoundException, IOException {
		Action action = null;
		while(true) {
			//scelta mossa
			// @Matteo conteggio iniziale questo penso sia inevitabile, ma si fa una volta sola!!!!!
			int nwhites =0;
			int nblacks = 0;
			for(int i =0; i< 9; i++)
				for(int j =0; j< 9; j++)
			{if(currentState.getPawn(i, j).equals(Pawn.WHITE))
				nwhites++;
			else if( currentState.getPawn(i, j).equals(Pawn.BLACK))
				nblacks++;
			}
			action = ab.AlphaBetaSearch(depth, new TablutState(this.getCurrentState(),nwhites,nblacks),MinMaxPrinter.getPrinter(PrintMode.Simple));
			//comunica l'azione al server
			this.write(action);
			//legge stato corrente modificato dal server
			this.read();
			//legge stato corrente dal server (mossa avversario)
			this.read();
		}
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		int gametype = 4;
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
			System.out.println(args[1]);
			gametype = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			name = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		TablutClient client = new ClientDaniele(role);
		client.run();
	}

}
