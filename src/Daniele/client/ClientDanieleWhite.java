package Daniele;



import java.io.IOException;


public class ClientDanieleWhite {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String[] array = new String[]{"WHITE"};
		if (args.length>0){
			array = new String[]{"WHITE", args[0]};
		}
		ClientDaniele.main(array);
	}
}
