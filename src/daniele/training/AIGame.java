package daniele.training;

import java.util.Set;

import daniele.state.DanieleAction;
import daniele.state.ITablutState;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts, Set<String> pastStates);
}
