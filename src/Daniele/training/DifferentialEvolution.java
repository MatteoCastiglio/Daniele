package Daniele.training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

//import dk.ange.octave.*;

public class DifferentialEvolution {

	/*
	 * Non si ha una "objective function" che deve tornare un valore reale per rappresentarne l'ottimizzazione,
	 * e quindi, nella valutazione di nuove soluzioni, non si confronter� questo valore (objective function)
	 * con il valore reale della "fitness function" (objective function in un problema di massimo).
	 * Ma questo confronto viene gestito relazionando delle statistiche (rilevate al termine della partita) ->
	 * ci� permette di capire se il nuovo vettore generato � migliore del precedente - in tal caso lo si
	 * sostituisce nella popolazione
	 */

	//DE variables
	public static int n = 8;		// population size 		(ovvero dei vettori soluzione - agenti)	-	� tipicamente tra 10 e 25
	public static double F = 0.7;	// differential weight	-	un buon range � tra 0.4 e 0.95
	public static double Cr = 0.5;	// crossover rate	-	un buon range � tra 0.1 e 0.8
	public static int d = 8;		// number of parameters

	//Utility variables
	public static Random random = new Random();
	public static LinkedList<double[]> population = new LinkedList<>();
	public static int Lb = 0;
	public static int Ub = 2;
	public static int maxGenerations = 100;			//10-100-1000?
	public static int weightSize = 10;

	//Evolution's track variables
	public static int nGeneration = 0;
	public static int agentUpdate = 0;
	public static int agentsUpdatedBecauseMoves = 0;
	public static int nGames = 2;
	public static int totalGamesPlayed = 0;
	public static int totalOldWin = 0;
	public static int totalNewWin = 0;
	public static PrintStream statsOut;

	public static void main(String[] args) throws IOException {

		//		OctaveEngineFactory oef = new OctaveEngineFactory();
		//		OctaveEngine oe = oef.getScriptEngine();

		// File with some statistics
		File dataFile = new File("data/stats.txt");
		statsOut = new PrintStream(new FileOutputStream(dataFile));

		writeStatsToFile();

		//1. Initialize random population/solutions
		for (int i = 0; i < n; i++) {
			double[] newWeights = new double[d];
			for (int j = 0; j < d; j++) {
				newWeights[j] = Lb+(Ub-Lb)*random.nextDouble();
			}
			newWeights=normalize(newWeights);
			population.add(newWeights);
		}

		writePopulationToFile();

		//2. Start the iterations by differential evolution
		for (int i = 0; i < maxGenerations; i++) {
			nGeneration++;

			// For each agent x in the population
			for(int x = 0; x < n; x++) {

				//3. MUTATION: donor agent agentV[j] = agentP[j] + F * (agentQ[j] - agentR[j]);
				int p, q, r;
				// Pick 3 distinct agents p, q & r
				do {
					p = random.nextInt(n);
				} while (p == x);
				do {
					q = random.nextInt(n);
				} while (q == x || q == p);
				do {
					r = random.nextInt(n);
				} while (r == x || r == p || r == q);
				double[] agentP = population.get(p);
				double[] agentQ = population.get(q);
				double[] agentR = population.get(r);

				//4. CROSSOVER: agentU[j] =		or agentV[j] if R == j || random.nextDouble() < Cr		or agentX[j] otherwise
				int R = random.nextInt(d);
				double[] newAgent = new double[d];
				for(int j = 0; j < d; j++) {
					if (R == j || random.nextDouble() < Cr) 
						newAgent[j] =  agentP[j] + F * (agentQ[j] - agentR[j]);		//= agentV[j]
					else
						newAgent[j] =  population.get(x)[j];
				}
				newAgent=normalize(newAgent);

				//5. SELECTION: agentX[j] at next generation =	or agentU[j] if "objectfunction(agentU[j]) > fittestfunction(agentX[j])"	or agentX[j] otherwise

				// Decide how many games to play: set to 0 to keep new value
				setPlayingWeights(x, newAgent);

				if(isNewAgentBetter(x)) {
					population.remove(x);
					population.add(x, newAgent);
					agentUpdate++;
				}

				writePopulationToFile();
				writeStatsToFile();
			}

		}

	}

	public static void setPlayingWeights(int x, double[] weights) throws FileNotFoundException {
		File dataFile = new File("data/playing.txt");
		PrintStream dataOut = new PrintStream(new FileOutputStream(dataFile));

		// Put old first -> Player OLD
		dataOut.print("PlayerOLD ");
		double[] w = population.get(x);
		for (int j = 0; j < d; j++) {
			dataOut.print(String.format("%010f", w[j]) + " ");
		}
		dataOut.println("");

		// Put new weights -> Player NEW
		dataOut.print("PlayerNEW ");
		for (int j = 0; j < d; j++) {
			dataOut.print(String.format("%010f", weights[j]) + " ");
		}

		dataOut.flush();
		dataOut.close();
	}

	public static void writeStatsToFile() throws FileNotFoundException {
		statsOut.println("Current Generation: " + nGeneration + " Total games: " + totalGamesPlayed);
		statsOut.print("PlayerOLD wins: " + totalOldWin);
		statsOut.println(" - PlayerNEW wins: " + totalNewWin);
		statsOut.println("Total agents updated: " + agentUpdate);
		statsOut.println("Agents updated because of more moves: " + agentsUpdatedBecauseMoves);
		statsOut.flush();
	}

	private static boolean isNewAgentBetter(int x) throws IOException {

		//fare due partite : una con nero (pesi passati) e bianco (pesi aggiornati) - una con nero (pesi aggiornati) e bianco (pesi passati)

		for (int i = 0; i < nGames; i++) {
		
			ProcessBuilder server_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar:bin", "Daniele.training.Server");				//	--					//serve un server di allenamento? (solo per il conteggio delle mosse? possono farlo i client di allenamento?)
			//server_pb.inheritIO();
			server_pb.redirectOutput(new File("data", nGeneration+"_server"+x+"_"+i+".txt"));
			Process server = server_pb.start();

			// Debug output
//			System.out.println("Game " + i);

			Process clientOld;
			Process clientNew;

			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			if(i % 2 == 0) {	//OLD BLACK vs NEW WHITE
				ProcessBuilder clientOldBlack_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar:bin", /*"-Xms520m",*/ "Daniele.training.ClientDEOld", "BLACK");		//serve un client di allenamento		-- + BLACK
				//clientOldBlack_pb.inheritIO();
				clientOldBlack_pb.redirectOutput(new File("data", nGeneration+"_agent"+x+"_OBvsNW_OB.txt"));
				clientOld = clientOldBlack_pb.start();
			}
			else {				//OLD WHITE vs NEW BLACK
				ProcessBuilder clientOldWhite_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar:bin", /*"-Xms520m",*/ "Daniele.training.ClientDEOld", "WHITE");		//serve un client di allenamento		-- + WHITE
				//clientOldWhite_pb.inheritIO();
				clientOldWhite_pb.redirectOutput(new File("data", nGeneration+"_agent"+x+"_OWvsNB_OW.txt"));
				clientOld = clientOldWhite_pb.start();
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			if(i % 2 == 0) {	//OLD BLACK vs NEW WHITE
				ProcessBuilder clientNewWhite_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar:bin", /*"-Xms520m",*/ "Daniele.training.ClientDENew", "WHITE");		//serve un client di allenamento		-- + WHITE
				//clientNewWhite_pb.inheritIO();
				clientNewWhite_pb.redirectOutput(new File("data", nGeneration+"_agent"+x+"_OBvsNW_NW.txt"));
				clientNew = clientNewWhite_pb.start();
			}
			else {				//OLD WHITE vs NEW BLACK
				ProcessBuilder clientNewBlack_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar:bin", /*"-Xms520m",*/ "Daniele.training.ClientDENew", "BLACK");		//serve un client di allenamento		-- + BLACK
				//clientNewBlack_pb.inheritIO();
				clientNewBlack_pb.redirectOutput(new File("data", nGeneration+"_agent"+x+"_OWvsNB_NB.txt"));
				clientNew = clientNewBlack_pb.start();
			}

			try {
				clientOld.waitFor();
				clientNew.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
				clientOld.destroy();
				clientNew.destroy();
			}

			server.destroy(); 
		}


		//leggi file con statistiche: vittorie con pesi passati, vittorie con pesi aggiornati, pareggi, 

		int[] stats = readLogs();

		// Keep track of some stats
		totalOldWin += stats[0];
		totalNewWin += stats[1];
		totalGamesPlayed += nGames;

		if (stats[1] == stats[0] && (stats[4] > stats[3])) {
			agentsUpdatedBecauseMoves++;
			//        	System.out.println("Updated because more Moves!");
		}

		// Debug output
//		System.out.println("PlayerOLD wins: " + stats[0]);
//      System.out.println("PlayerNEW wins: " + stats[1]);
//      System.out.println("Draws: " + stats[2]);
//      System.out.println("Moves Played When Lost: " + stats[3] + " vs " + stats[4]);

		boolean newAgentIsBetter = stats[1] > stats[0] || (stats[1] == stats[0] && (stats[4] > stats[3]));		// se si vince di più o il nero sopravvive più a lungo!

		return newAgentIsBetter;	
	}

	public static int[] readLogs() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("data/outcomes.txt"));
		LinkedList<String> allLines = new LinkedList<>();

		while (br.ready()) {
			allLines.add(br.readLine());
		}

		Collections.reverse(allLines);
		int[] stats = {0,0,0,0,0}; // WINS P1, WINS P2, DRAWS, P1 MOVES WHEN LOST, P2 MOVES WHEN LOST
		for (int i = 0; i < nGames; i++){
			String s = allLines.removeFirst();
			
			if(i % 2 == 1) {	//OLD BLACK vs NEW WHITE								// s[0]=='2'									--
				if (s.contains(",D")||s.contains(",FD")) {	//DRAW
					stats[2]++;
				} else {
					if (s.contains(",BW")) {	//OLD BLACK WIN
						stats[0]++;
					} else {	//s.contains("WW") - NEW WHITE WIN
						stats[1]++;
						stats[3] += Integer.valueOf(s.split(",")[2]);					//come? sono le mosse del nero					--
					}
				}
			}
			else {				//OLD WHITE vs NEW BLACK								// s[0]=='1'									--
				if (s.contains(",D")||s.contains(",FD")) {	//DRAW
					stats[2]++;
				} else {
					if (s.contains(",WW")) {	//OLD WHITE WIN
						stats[0]++;
						stats[4] += Integer.valueOf(s.split(",")[4]);					//come? sono le mosse del nero					--
					} else {	//s.contains("BW") - NEW BLACK WIN
						stats[1]++;
					}
				}
			}
		}
		br.close();
		return stats;
	}

	public static void writePopulationToFile() throws FileNotFoundException {
		File logDir = new File("data");
		File logFile = new File(logDir, "population" + nGeneration + ".txt");
		PrintStream dataOut = new PrintStream(new FileOutputStream(logFile));

		for (int i = 0; i < n; i++) {
			dataOut.print("agent" + String.format("%03d", i) + " ");
			double[] weights = population.get(i);
			for (int j = 0; j < d; j++) {
				dataOut.print(String.format("%010f", weights[j]) + " ");
			}
			dataOut.println("");
		}
		dataOut.flush();
		dataOut.close();
	}
	
	public static double[] normalize(double[] weights) {
		double sum = 0;
		for (double i : weights) {
			sum += Math.abs(i);
		}
		double[] newWeights = weights.clone();
		for (int i = 0; i < weights.length; i++) {
			newWeights[i] = weights[i] / sum;
		}
		return newWeights;
	}

}
