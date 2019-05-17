package Daniele.ai;

import java.util.Set;

import Daniele.minmaxprinter.MinMaxPrinter;
import Daniele.state.DanieleAction;
import Daniele.state.ITablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts,Set<String> pastStates);
}
