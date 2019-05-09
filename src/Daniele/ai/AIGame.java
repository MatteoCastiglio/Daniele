package Daniele.ai;

import Daniele.minmaxprinter.MinMaxPrinter;

public interface AIGame {

    DanieleAction chooseBestMove(int startingDepth, int maxDepth, ITablutState ts);
}
