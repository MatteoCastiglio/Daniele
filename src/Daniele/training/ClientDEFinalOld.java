package Daniele.training;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

public class ClientDEFinalOld {

	public static void main(String[] args) throws UnknownHostException, ClassNotFoundException, IOException {

		BufferedReader br;
		double[] weights = null;

		try {
			br = new BufferedReader(new FileReader(FinalGame.dirName + "/playing.txt"));
			String firstLine = br.readLine();
			String numbers = firstLine.substring(9);
			System.out.println("weights " + numbers);
			System.out.flush();

			weights = new double[DifferentialEvolution.d];

			numbers = numbers.replaceAll(",", ".");

			for (int i = 0; i < DifferentialEvolution.d; i++) {
				weights[i] = Double.valueOf(numbers.substring(1 + (DifferentialEvolution.weightSize + 1) * i, 1 + (DifferentialEvolution.weightSize + 1) * (i + 1)));
				System.out.println("--- " + weights[i]);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		ClientDETraining.train(weights, args[0], "PlayerOLD");
	}

}
