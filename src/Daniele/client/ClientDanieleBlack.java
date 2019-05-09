package Daniele.client;



import Daniele.client.ClientDaniele;

import java.io.IOException;


public class ClientDanieleBlack{

	//aggiunta
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String[] array = new String[]{"BLACK"};
		if (args.length>0){
			array = new String[]{"BLACK", args[0]};
		}
		ClientDaniele.main(array);
	}
}
