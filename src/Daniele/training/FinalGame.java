package Daniele.training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FinalGame {

	public static final String finalPopulationName = "finalPopulation";	//copiare l'ultima popolazione generata rinominandola "finalPopulation" e metterla nella cartella "dataFinal"
	public static final String dirName = "finalData";	//preparare un file vuoto "outcomes.txt" nella cartella creata "finalData"
	public static PrintStream finalOut;
	
	public static void main(String[] args) throws FileNotFoundException {

		List<double[]> finalPopulation = new ArrayList<>();
		
		File dataFile = new File(dirName, "finalResult.txt");
		finalOut = new PrintStream(new FileOutputStream(dataFile));

		//leggo l'ultima popolazione generata
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(dirName+"/"+finalPopulationName+".txt"));
			for(int i = 0; i < DifferentialEvolution.n; i++) {
				String agentLine = br.readLine();
				String numbers = agentLine.substring(8).replaceAll(",",".");
				double weights[] = new double[DifferentialEvolution.d];
				for(int j = 0; j < DifferentialEvolution.d; j++){
					weights[j] = Double.valueOf(numbers.substring(1 + (DifferentialEvolution.weightSize + 1)*j, 1 + (DifferentialEvolution.weightSize + 1)*(j+1)));
				}	
				finalPopulation.add(weights);
			}
			br.close();


			//trovo l'agente/soluzione migliore
			double[] bestAgent = finalPopulation.get(0);
			finalPopulation.remove(0);
			int numberOfMatch = 0;
			finalOut.println("**FINAL MATCHES**"+'\n');
			for(double[] agent : finalPopulation) {
				numberOfMatch++;
				setPlayingWeights(bestAgent, agent);
				if(playGame(bestAgent, agent, numberOfMatch))
					bestAgent = agent;
			}
			
			//soluzione finale
			System.out.println('\n'+"The best solution for your heuristic parameters is:");
			for(double p : bestAgent) {
				System.out.print(" "+String.format("%010f", p));
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}//main

	
	public static void setPlayingWeights(double[] bestAgent, double[] agent) throws FileNotFoundException {
		File dataFile = new File(dirName+"/playing.txt");
		PrintStream dataOut = new PrintStream(new FileOutputStream(dataFile));

		// Put old first -> Player OLD
		dataOut.print("PlayerOLD ");
		for (int j = 0; j < DifferentialEvolution.d; j++) {
			dataOut.print(String.format("%010f", bestAgent[j]) + " ");
		}
		dataOut.println("");

		// Put new weights -> Player NEW
		dataOut.print("PlayerNEW ");
		for (int j = 0; j < DifferentialEvolution.d; j++) {
			dataOut.print(String.format("%010f", agent[j]) + " ");
		}

		dataOut.flush();
		dataOut.close();
	}


	private static boolean playGame(double[] bestAgent, double[] agent, int numberOfMatch) throws IOException {
		// ritorna vero se l'agent (il nuovo esaminato - NEW) è migliore del best agent (il migliore per il momento - OLD) ; falso altrimenti

		for (int i = 0; i < DifferentialEvolution.nGames; i++) {

			ProcessBuilder server_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", "Daniele.training.FinalServer");
			server_pb.redirectOutput(new File(dirName, "finalGeneration_server_"+numberOfMatch+"_"+i+".txt"));
			Process server = server_pb.start();

			Process clientOld;
			Process clientNew;

			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			if(i % 2 == 0) {	//OLD BLACK vs NEW WHITE
				ProcessBuilder clientOldBlack_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", /*"-Xms520m",*/ "Daniele.training.ClientDEFinalOld", "BLACK");
				clientOldBlack_pb.redirectOutput(new File(dirName, "finalGeneration_agent"+numberOfMatch+"_OBvsNW_OB.txt"));
				clientOld = clientOldBlack_pb.start();
			}
			else {				//OLD WHITE vs NEW BLACK
				ProcessBuilder clientOldWhite_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", /*"-Xms520m",*/ "Daniele.training.ClientDEFinalOld", "WHITE");
				clientOldWhite_pb.redirectOutput(new File(dirName, "finalGeneration_agent"+numberOfMatch+"_OWvsNB_OW.txt"));
				clientOld = clientOldWhite_pb.start();
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}

			if(i % 2 == 0) {	//OLD BLACK vs NEW WHITE
				ProcessBuilder clientNewWhite_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", /*"-Xms520m",*/ "Daniele.training.ClientDEFinalNew", "WHITE");
				clientNewWhite_pb.redirectOutput(new File(dirName, "finalGeneration_agent"+numberOfMatch+"_OBvsNW_NW.txt"));
				clientNew = clientNewWhite_pb.start();
			}
			else {				//OLD WHITE vs NEW BLACK
				ProcessBuilder clientNewBlack_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", /*"-Xms520m",*/ "Daniele.training.ClientDEFinalNew", "BLACK");
				clientNewBlack_pb.redirectOutput(new File(dirName, "finalGeneration_agent"+numberOfMatch+"_OWvsNB_NB.txt"));
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
		
		int[] stats = readLogs();
		
		finalOut.println("----------final match: "+numberOfMatch+"----------");
		finalOut.print("best current agent: ");
		for(double p : bestAgent) {
			finalOut.print(" "+String.format("%010f", p));
		}
		finalOut.println();
		finalOut.print("concurrent agent: ");
		for(double p : bestAgent) {
			finalOut.print(" "+String.format("%010f", p));
		}
		finalOut.println("best agent wins: " + stats[0]);
		finalOut.println("concurrent agent wins: " + stats[1]);
		finalOut.println("draws: " + stats[2]);
		finalOut.println("Moves Played by black when white wins: " + stats[3] + " vs " + stats[4]);

		if(stats[1] > stats[0]) {
			finalOut.println("Agent updated!");
			return true;
		}
		if (stats[1] == stats[0] && (stats[4] > stats[3])) {
			System.out.println("Agent updated because more moves: "+stats[3] + " vs " + stats[4]); //ha vinto entrabe le partite il WHITE, e il BLACK è stato più resistente
			return true;
		}
		return false;
	}


	public static int[] readLogs() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dirName+"/outcomes.txt"));
		LinkedList<String> allLines = new LinkedList<>();

		while (br.ready()) {
			allLines.add(br.readLine());
		}

		Collections.reverse(allLines);
		int[] stats = {0,0,0,0,0}; // WINS P1, WINS P2, DRAWS, P1 MOVES WHEN LOST, P2 MOVES WHEN LOST
		for (int i = 0; i < DifferentialEvolution.nGames; i++){
			String s = allLines.removeFirst();
			
			if(i % 2 == 1) {	//OLD BLACK vs NEW WHITE
				if (s.contains(",D")||s.contains(",FD")) {	//DRAW
					stats[2]++;
				} else {
					if (s.contains(",BW")) {	//OLD BLACK WIN
						stats[0]++;
					} else {	//s.contains("WW") - NEW WHITE WIN
						stats[1]++;
						stats[3] += Integer.valueOf(s.split(",")[2]);					//sono le mosse del nero
					}
				}
			}
			else {				//OLD WHITE vs NEW BLACK
				if (s.contains(",D")||s.contains(",FD")) {	//DRAW
					stats[2]++;
				} else {
					if (s.contains(",WW")) {	//OLD WHITE WIN
						stats[0]++;
						stats[4] += Integer.valueOf(s.split(",")[4]);					//sono le mosse del nero
					} else {	//s.contains("BW") - NEW BLACK WIN
						stats[1]++;
					}
				}
			}
		}
		br.close();
		return stats;
	}

}
