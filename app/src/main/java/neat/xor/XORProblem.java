package neat.xor;

public class XORProblem implements Problem {
    private static final double[][] INPUTS = { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } };
    private static final double[] OUTPUTS = { 0, 1, 1, 0 };

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 1;
    }

    @Override
    public int getNumberTest() {
        return INPUTS.length;
    }

    @Override
    public double[][] getInputs() {
        return INPUTS;
    }

    @Override
    public double evaluateFitness(Genome genome) {
        double fitness = 0.0;
        for (int i = 0; i < getNumberTest(); i++) {
            double[] output = genome.evaluate(INPUTS[i]);
            double error = Math.abs(OUTPUTS[i] - output[0]);
            fitness += 1.0 - error;
        }
        return fitness / getNumberTest();   //to have a fitness btw 0 and 1
    }

    @Override
    public double evaluateFitnessWithPrint(Genome genome) {
        double fitness = 0.0;
        for (int i = 0; i < getNumberTest(); i++) {
            double[] output = genome.evaluate(INPUTS[i]);
            System.out.println("output : " + output[0]);
            double error = Math.abs(OUTPUTS[i] - output[0]);
            fitness += 1.0 - error;
        }
        return fitness / getNumberTest();   //to have a fitness btw 0 and 1
    }

}
