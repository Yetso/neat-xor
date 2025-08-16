package neat.xor;

public class App {

    public static void main(String[] args) {
        Problem problem = new XORProblem();
        NeatAlgorithm neatAlgorithm = new NeatAlgorithm(problem);
        neatAlgorithm.CreateGenomes();
        Genome solution = neatAlgorithm.Train();

        System.out.println("Final solution:");
        double score = solution.getFitness();
        System.out.println("score = " + score);

        problem.evaluateFitnessWithPrint(solution);
    }
}
