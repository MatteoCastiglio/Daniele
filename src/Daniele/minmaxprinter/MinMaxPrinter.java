package Daniele.minmaxprinter;

import Daniele.state.DanieleAction;
import Daniele.state.ITablutState;

public interface MinMaxPrinter {

	public static MinMaxPrinter getPrinter(PrintMode mode)
	{
		if(mode.equals(PrintMode.None)) return new DumbPrinter();
		if(mode.equals(PrintMode.Simple)) return new SimplePrinter();
		if(mode.equals(PrintMode.Verbose)) return new VerbosePrinter();
		else return null;
	}
	
	 void printDecision(double value, DanieleAction a);
	void printMove(DanieleAction a, ITablutState state, double value);


}

 class DumbPrinter implements MinMaxPrinter
{

	@Override
	public void printDecision(double value,DanieleAction a) {
		
		
	}

	@Override
	public void printMove(DanieleAction a, ITablutState state, double value) {

	}


}
class SimplePrinter implements MinMaxPrinter
	 {

	 	@Override
	 	public void printDecision(double value,DanieleAction a )
		{
	 		System.out.println("valore deciso :" +value +" -- Azione " + a);
	 	}

		 @Override
		 public void printMove(DanieleAction a, ITablutState state, double value) {
	 		printMove(a,value);

		 }


		 public void printMove(DanieleAction a, double value) {
			 System.out.println("Aggiunta azione " +a +" con valore " + value);
		 }
	 }
	
	class VerbosePrinter extends SimplePrinter implements MinMaxPrinter
		 {

		 	@Override
		 	public void printDecision(double value,DanieleAction a) {
		 		super.printDecision(value,a);
		 	}


			 @Override
			 public void printMove(DanieleAction a, ITablutState state, double value) {
				 printMove(a,value);
				 printChild(state,value);

			 }




		 	public void printChild(ITablutState state, double value) {

		 		System.out.println("------");
		 		System.out.println(state);
				System.out.println("------");
		 		
		 	}
		 }
		 