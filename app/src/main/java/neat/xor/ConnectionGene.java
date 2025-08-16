package neat.xor;

import java.util.Random;

import lombok.Data;

@Data
public class ConnectionGene {
    private int innovationNumber;
    private int inNode;
    private int outNode;
    private double weight;
    private boolean enabled;


    private static final Random random = new Random();

    public ConnectionGene(int i, int j) {
        this.inNode = i;
        this.outNode = j;
        this.weight = random.nextGaussian();
        this.innovationNumber = InnovationTracker.getInnovationTracker(i, j);
        this.enabled = true;
    }

    public ConnectionGene(int i, int j, double weight, boolean enabled) {
        this.inNode = i;
        this.outNode = j;
        this.weight = weight;
        this.innovationNumber = InnovationTracker.getInnovationTracker(i, j);
        this.enabled = enabled;
    }

    public ConnectionGene(ConnectionGene connectionGene) {
        this.innovationNumber = connectionGene.innovationNumber;
        this.inNode = connectionGene.inNode;
        this.outNode = connectionGene.outNode;
        this.weight = connectionGene.weight;
        this.enabled = connectionGene.enabled;
    }

}
