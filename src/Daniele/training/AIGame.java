package Daniele.training;

import java.util.Set;

import Daniele.ai.DanieleAction;
import Daniele.ai.ITablutState;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts,Set<String> pastStates);
}
