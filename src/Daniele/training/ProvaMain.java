package Daniele.training;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import Daniele.state.ITablutState;
import Daniele.state.TablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

public class ProvaMain {
	
	public static void main(String[] args) throws IOException {
		
//		ProcessBuilder server_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", "Daniele.training.Server");				//	--					//serve un server di allenamento? (solo per il conteggio delle mosse? possono farlo i client di allenamento?)
//		server_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//
//		Process server = server_pb.start();
//		
//		ProcessBuilder clientOldBlack_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", "-Xms520m", "Daniele.training.ClientDEOld", "BLACK");		//serve un client di allenamento		-- + BLACK
//		//clientOldBlack_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//		Process clientOld = clientOldBlack_pb.start();
//		
//		ProcessBuilder clientNewWhite_pb = new ProcessBuilder("java", "-cp", "lib/gson-2.2.2.jar;bin", "-Xms520m", "Daniele.training.ClientDENew", "WHITE");		//serve un client di allenamento		-- + WHITE
//		//clientOldBlack_pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//		Process clientNew = clientNewWhite_pb.start();
//		
//		try {
//			clientOld.waitFor();
//			clientNew.waitFor();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//			clientOld.destroy();
//			clientNew.destroy();
//		}
//
//		server.destroy(); 
//		
//		System.out.println("fine!");
		
		
		Set<State> pastStates = new HashSet<State>();
		Set<String> pastStatesS = new HashSet<>();
		
		State s = new StateTablut();
		Turn t = Turn.BLACK;
		s.setTurn(t);
		//s.getState().setTurn(t);
		Pawn[][] board = {
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.BLACK, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.WHITE, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.THRONE, Pawn.KING, Pawn.BLACK, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.WHITE, Pawn.EMPTY, Pawn.EMPTY, Pawn.BLACK, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.BLACK, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,},
				{Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY, Pawn.EMPTY,}
				};
		s.setBoard(board);
		System.out.println(s);
		
		State s2 = s.clone();
		pastStates.add(s);
		pastStatesS.add(s.toLinearString());
		System.out.println(pastStates.contains(s2));
		System.out.println(pastStatesS.contains(s2.toLinearString()));
		
	}

}
