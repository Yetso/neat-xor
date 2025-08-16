package neat.xor;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class NeatAlgorithm {
    private final int populationSize = Config.getInt("population_size");
    private final double mutationRate = Config.getDouble("mutation_rate");
    private final double fitnessThreshold = Config.getDouble("fitness_threshold");
    private int tournamentSize = Config.getInt("tournament_size");

    private final double crossoverRate = Config.getDouble("crossover_rate");
    private final int keepBest = Config.getInt("keep_best");
    private final boolean keepHalfPopulation = Config.getBool("keep_half_population");
    private int maxGenerationsWithoutSpecies = Config.getInt("max_generations_without_species");

    private final Problem problem;

    private final int INPUT_SIZE;
    private final int OUTPUT_SIZE;

    private List<Genome> population;

    private static final Random random = new Random();

    public NeatAlgorithm(Problem problem) {
        this.population = new ArrayList<Genome>();
        this.problem = problem;
        this.INPUT_SIZE = problem.getInputCount();
        this.OUTPUT_SIZE = problem.getOutputCount();
    }

    public void CreateGenomes() {
        for (int i = 0; i < populationSize; i++) {
            population.add(new Genome(INPUT_SIZE, OUTPUT_SIZE));
        }
    }

    public Genome Train() {
        // private Genome trainWithoutSpecies(double[][] inputs, double[][]
        // expectedOutputs, double fitnessThreshold,
        // double crossoverRate, int numberToKeep, boolean keepHalfPopulation) {

        int generation = 0;
        double topGenomeScore = 0.0;

        for (Genome genome : population) {
            double fitness = problem.evaluateFitness(genome);
            genome.setFitness(fitness);
        }

        while (topGenomeScore < fitnessThreshold && generation < maxGenerationsWithoutSpecies) {
            generation++;
            population.sort(Comparator.comparingDouble(Genome::getFitness).reversed());
            List<Genome> newPopulation = new ArrayList<>(population.subList(0, keepBest));

            if (keepHalfPopulation) {
                List<Genome> remainingGenomes = new ArrayList<>(population.subList(keepBest, population.size()));
                Collections.shuffle(remainingGenomes);
                newPopulation.addAll(remainingGenomes.subList(0, populationSize / 2 - keepBest));
            }

            while (newPopulation.size() < populationSize) {
                Genome parent1 = tournamentSelection(newPopulation, tournamentSize);
                newPopulation.remove(parent1);
                Genome parent2 = tournamentSelection(newPopulation, tournamentSize);
                newPopulation.add(parent1);
                Genome child = (Math.random() <= crossoverRate) ? crossover(parent1, parent2) : parent1;
                mutate(child);
                newPopulation.add(child);
            }

            population = newPopulation;
            for (Genome genome : population) {
                double fitness = problem.evaluateFitness(genome);
                genome.setFitness(fitness);
            }

            population.sort(Comparator.comparingDouble(Genome::getFitness).reversed());
            topGenomeScore = population.get(0).getFitness();
            System.out.println("Top Genome Fitness in Generation " + generation + ": " + topGenomeScore);
        }
        if (topGenomeScore < fitnessThreshold) {
            System.out.println("solution not found after " + maxGenerationsWithoutSpecies + " tries");
            return null;
        }

        System.out.println("Solution found in generation: " + generation);
        return population.get(0);

    }

    private Genome tournamentSelection(List<Genome> population, int tournamentSize) {
        List<Genome> tournament = new ArrayList<>();
        int populationSize = population.size();
        if (tournamentSize <= populationSize) {
            return findFittest(population);
        }

        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) ((Math.random() * populationSize));
            tournament.add(population.get(randomId));
        }
        Genome fittest = findFittest(tournament);
        return fittest;
    }

    private Genome findFittest(List<Genome> population) {
        Genome best = population.stream()
                .max(Comparator.comparingDouble(Genome::getFitness))
                .orElse(null);
        return best;

    }

    private Genome crossover(Genome parent1, Genome parent2) {
        Genome child = new Genome(INPUT_SIZE, OUTPUT_SIZE);
        List<ConnectionGene> childConnections = child.getConnections();

        List<ConnectionGene> p1 = new ArrayList<>(parent1.getConnections());
        List<ConnectionGene> p2 = new ArrayList<>(parent2.getConnections());

        p1.sort(Comparator.comparingInt(ConnectionGene::getInnovationNumber));
        p2.sort(Comparator.comparingInt(ConnectionGene::getInnovationNumber));

        List<ConnectionGene> parent1matchingGenes = new ArrayList<>();
        List<ConnectionGene> parent2matchingGenes = new ArrayList<>();
        List<ConnectionGene> onlyParent1Genes = new ArrayList<>();
        List<ConnectionGene> onlyParent2Genes = new ArrayList<>();

        int i = 0;
        int j = 0;

        while (i < p1.size() && j < p2.size()) {
            int inno1 = p1.get(i).getInnovationNumber();
            int inno2 = p2.get(j).getInnovationNumber();

            if (inno1 == inno2) {
                parent1matchingGenes.add(p1.get(i));
                parent2matchingGenes.add(p2.get(j));
                i++;
                j++;
            } else if (inno1 < inno2) {
                onlyParent1Genes.add(p1.get(i));
                i++;
            } else {
                onlyParent2Genes.add(p2.get(j));
                j++;
            }
        }

        while (i < p1.size())
            onlyParent1Genes.add(p1.get(i++));
        while (j < p2.size())
            onlyParent2Genes.add(p2.get(j++));

        for (int k = 0; k < parent1matchingGenes.size(); k++) {
            ConnectionGene parent1Conn = parent1matchingGenes.get(k);
            ConnectionGene parent2Conn = parent2matchingGenes.get(k);
            double weight = (parent1Conn.getWeight() + parent2Conn.getWeight()) / 2.0;
            boolean activated;
            if (parent1Conn.isEnabled() == parent2Conn.isEnabled()) {
                activated = parent1Conn.isEnabled();
            } else if (random.nextDouble() < 0.25) {
                activated = true;
            } else {
                activated = false;
            }

            if (!child.NodeAlreadyExist(parent1Conn.getInNode())) {
                child.addNode(new Node(parent1.getNode(parent1Conn.getInNode())));
            }
            if (!child.NodeAlreadyExist(parent1Conn.getOutNode())) {
                child.addNode(new Node(parent1.getNode(parent1Conn.getOutNode())));
            }
            ConnectionGene conn = new ConnectionGene(parent1Conn.getInNode(), parent1Conn.getOutNode(), weight,
                    activated);
            childConnections.add(conn);
        }

        // if same fitness, we choose randomly
        if (parent1.getFitness() == parent2.getFitness()) {
            for (ConnectionGene connectionGene : onlyParent1Genes) {
                if (random.nextBoolean()) {
                    if (!child.NodeAlreadyExist(connectionGene.getInNode())) {
                        child.addNode(new Node(parent1.getNode(connectionGene.getInNode())));
                    }
                    if (!child.NodeAlreadyExist(connectionGene.getOutNode())) {
                        child.addNode(new Node(parent1.getNode(connectionGene.getOutNode())));
                    }
                    childConnections.add(new ConnectionGene(connectionGene));
                }
            }

            for (ConnectionGene connectionGene : onlyParent2Genes) {
                if (random.nextBoolean()) {
                    if (!child.NodeAlreadyExist(connectionGene.getInNode())) {
                        child.addNode(new Node(parent2.getNode(connectionGene.getInNode())));
                    }
                    if (!child.NodeAlreadyExist(connectionGene.getOutNode())) {
                        child.addNode(new Node(parent2.getNode(connectionGene.getOutNode())));
                    }
                    childConnections.add(new ConnectionGene(connectionGene));
                }
            }
            return child;
        }

        // fitness different and parent1 > parent2
        if (parent1.getFitness() > parent2.getFitness()) {
            for (ConnectionGene connectionGene : onlyParent1Genes) {
                if (!child.NodeAlreadyExist(connectionGene.getInNode())) {
                    child.addNode(new Node(parent1.getNode(connectionGene.getInNode())));
                }
                if (!child.NodeAlreadyExist(connectionGene.getOutNode())) {
                    child.addNode(new Node(parent1.getNode(connectionGene.getOutNode())));
                }
                childConnections.add(connectionGene);
            }
            return child;
        }

        // fitness different and parent1 < parent2
        for (ConnectionGene connectionGene : onlyParent2Genes) {
            if (!child.NodeAlreadyExist(connectionGene.getInNode())) {
                child.addNode(new Node(parent2.getNode(connectionGene.getInNode())));
            }
            if (!child.NodeAlreadyExist(connectionGene.getOutNode())) {
                child.addNode(new Node(parent2.getNode(connectionGene.getOutNode())));
            }
            childConnections.add(connectionGene);
        }
        return child;
    }

    private void mutate(Genome genome) {
        // Add a new connection
        if (random.nextDouble() < mutationRate) {
            mutationAddConnection(genome);
        }

        // Mutate the weight of the connections
        for (ConnectionGene connection : genome.getConnections()) {
            if (random.nextDouble() < mutationRate) {
                int amoutToAddRemove = 2;
                double currentWeight = connection.getWeight();
                connection.setWeight(currentWeight += random.nextGaussian() * amoutToAddRemove);
            }
        }

        if (genome.getConnections().size() < 1)
            return;

        // Add a new node
        if (random.nextDouble() < mutationRate) {

            int randomConnection = (int) (random.nextDouble() * genome.getConnections().size());
            ConnectionGene connection = genome.getConnections().get(randomConnection);

            int nodeId = genome.getNodes().size();
            Node newNode = new Node(nodeId, Node.NodeType.HIDDEN);
            genome.getNodes().add(newNode);

            genome.getConnections().add(new ConnectionGene(connection.getInNode(), newNode.getId()));
            genome.getConnections().add(new ConnectionGene(newNode.getId(), connection.getOutNode()));
        }

        // Enable or disable a connection node
        if (random.nextDouble() < mutationRate) {
            int randomConnection = (int) (random.nextDouble() * genome.getConnections().size());
            ConnectionGene connection = genome.getConnections().get(randomConnection);
            connection.setEnabled(!connection.isEnabled());
        }

    }

    private void mutationAddConnection(Genome genome) {
        List<Node> potentialSources = genome.getNodes().stream()
                .filter(node -> node.getType() != Node.NodeType.OUTPUT)
                .toList();

        List<Node> potentialDestination = genome.getNodes().stream()
                .filter(node -> node.getType() != Node.NodeType.INPUT && node.getType() != Node.NodeType.BIAS)
                .toList();

        Node source;
        Node target;

        int i = 0;
        int maxAttemp = 10;
        do {
            if (i++ >= maxAttemp)
                return;

            source = potentialSources.get(random.nextInt(potentialSources.size()));
            target = potentialDestination.get(random.nextInt(potentialDestination.size()));
        } while (source == target || genome.connectionAlreadyExist(source, target));

        genome.getConnections().add(new ConnectionGene(source.getId(), target.getId()));
    }

}
