package Daniele.training;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

//import dk.ange.octave.*;

public class DifferentialEvolution {

	/*
	 * Non si ha una "objective function" che deve tornare un valore reale per rappresentarne l'ottimizzazione,
	 * e quindi, nella valutazione di nuove soluzioni, non si confronterà questo valore (objective function)
	 * con il valore reale della "fitness function" (objective function in un problema di massimo).
	 * Ma questo confronto viene gestito relazionando delle statistiche (rilevate al termine della partita) ->
	 * ciò permette di capire se il nuovo vettore generato è migliore del precedente - in tal caso lo si
	 * sostituisce nella popolazione
	 */

	//DE variables
	public static int n = 10;		// population size 		(ovvero dei vettori soluzione - agenti)	-	è tipicamente tra 10 e 25
	public static double F = 0.7;	// differential weight	-	un buon range è tra 0.4 e 0.95
	public static double Cr = 0.5;	// crossover rate	-	un buon range è tra 0.1 e 0.8
	public static int d = 6;		// number of parameters

	//Utility variables
	public static Random random = new Random();
	public static LinkedList<double[]> population = new LinkedList<>();
	public static int Lb = 0;
	public static int Ub = 2;
	public static int maxGenerations = 100;

	//Evolution's track variables
	public static int nGeneration = 0;
	public static int agentUpdate = 0;

	public static void main(String[] args) throws IOException {

		//		OctaveEngineFactory oef = new OctaveEngineFactory();
		//		OctaveEngine oe = oef.getScriptEngine();

		//1. Initialize random population/solutions
		for (int i = 0; i < n; i++) {
			double[] newWeights = new double[d];
			for (int j = 0; j < d; j++) {
				newWeights[j] = Lb+(Ub-Lb)*random.nextDouble();
				System.out.println(i +" "+ newWeights[j]);
			}
			population.add(newWeights);
		}

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
				
				//5. SELECTION: agentX[j] at next generation =	or agentU[j] if "objectfunction(agentU[j]) > fittestfunction(agentX[j])"	or agentX[j] otherwise
				if(isNewAgentBetter()) {
					population.remove(x);
					population.add(x, newAgent);
					agentUpdate++;
				}
				

			}

		}

	}

	private static boolean isNewAgentBetter() {
		// TODO Auto-generated method stub
		return false;
	}


}
