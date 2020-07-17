package daniele.minmaxprinter;

import daniele.state.DanieleAction;
import daniele.state.ITablutState;

public interface MinMaxPrinter {

	static MinMaxPrinter getPrinter(PrintMode mode) {
		if (mode.equals(PrintMode.None)) return new DumbPrinter();
		if (mode.equals(PrintMode.Simple)) return new SimplePrinter();
		if (mode.equals(PrintMode.Verbose)) return new VerbosePrinter();
		else return null;
	}
	
	//print selected Move
	void printDecision(DanieleAction a,int depth);
	
	//print evaluated move
	void printMove(DanieleAction a, ITablutState state, double value);

}

class DumbPrinter implements MinMaxPrinter {

	@Override
	public void printDecision( DanieleAction a,int depth) {

	}

	@Override
	public void printMove(DanieleAction a, ITablutState state, double value) {
	}


}

class SimplePrinter implements MinMaxPrinter {

	@Override
	public void printDecision( DanieleAction a,int depth) {
		System.out.println("SELECTED ACTION: " + a + ", DEPTH: " + depth);
	}

	@Override
	public void printMove(DanieleAction a, ITablutState state, double value) {
		printMove(a, value);
	}

	public void printMove(DanieleAction a, double value) {
		System.out.println("Evaluated Action " + a + ", VALUE =  " + value);
	}
}

class VerbosePrinter extends SimplePrinter implements MinMaxPrinter {

	@Override
	public void printDecision( DanieleAction a,int depth) {
		super.printDecision(a,depth);
	}


	@Override
	public void printMove(DanieleAction a, ITablutState state, double value) {
		System.out.println("--------------------------");
		printMove(a, value);
		printChild(state, value);
	}

	public void printChild(ITablutState state, double value) {

		System.out.println("");
		System.out.println(state);
		System.out.println("");

	}
}
