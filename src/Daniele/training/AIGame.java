package Daniele.training;

import java.util.Set;

import Daniele.ai.DanieleAction;
import Daniele.ai.ITablutState;
import it.unibo.ai.didattica.competition.tablut.domain.State;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts,Set<State> pastStates);
}
