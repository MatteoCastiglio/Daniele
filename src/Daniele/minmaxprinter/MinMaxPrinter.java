package Daniele.minmaxprinter;

import Daniele.ITablutState;

public interface MinMaxPrinter {

	public static MinMaxPrinter getPrinter(PrintMode mode)
	{
		if(mode.equals(PrintMode.None)) return new DumbPrinter();
		if(mode.equals(PrintMode.Simple)) return new SimplePrinter();
		if(mode.equals(PrintMode.Verbose)) return new VerbosePrinter();
		else return null;
	}
	
	public void printDecision(double value);
	public void printReturn(double v);
	public void printChild(ITablutState state, double value);
}

 class DumbPrinter implements MinMaxPrinter
{

	@Override
	public void printDecision(double value) {
		
		
	}

	@Override
	public void printReturn(double v) {
		
		
	}

	@Override
	public void printChild(ITablutState state, double value) {
	
		
	}
}
	class SimplePrinter implements MinMaxPrinter
	 {

	 	@Override
	 	public void printDecision(double value) {
	 		System.out.println("valore deciso :" +value);
	 	}
	 		
	 		
	 	

	 	@Override
	 	public void printReturn(double v) {
	 		System.out.println("valore restituito :" +v);
	 		
	 	}

	 	@Override
	 	public void printChild(ITablutState state, double value) {
	 	
	 		System.out.println(state.toString());
	 		System.out.println("valore =" + value);
	 		
	 	}
	 }
	
	class VerbosePrinter extends SimplePrinter implements MinMaxPrinter
		 {

		 	@Override
		 	public void printDecision(double value) {
		 		super.printDecision(value);
		 	}
		 		
		 		
		 	

		 	@Override
		 	public void printReturn(double v) {
		 		
		 		super.printReturn(v);
		 		
		 	}

		 	@Override
		 	public void printChild(ITablutState state, double value) {
		 	
		 		System.out.println("-------");
		 		super.printChild(state, value);
		 		System.out.println("------");
		 		
		 	}
		 }
		 