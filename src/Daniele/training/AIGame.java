package Daniele.training;

import java.util.Set;

import Daniele.state.DanieleAction;
import Daniele.state.ITablutState;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts,Set<String> pastStates);
}
