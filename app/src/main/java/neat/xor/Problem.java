package neat.xor;

public interface Problem {
    int getInputCount();

    int getOutputCount();

    int getNumberTest();

    double[][] getInputs();

    double evaluateFitness(Genome genome);

    double evaluateFitnessWithPrint(Genome solution);
}
