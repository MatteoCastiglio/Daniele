package Daniele.ai;

import java.util.Set;

import Daniele.minmaxprinter.MinMaxPrinter;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts,Set<String> pastStates);
}
