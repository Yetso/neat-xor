package neat.xor;

import lombok.*;

import java.util.*;

@Data
public class Genome {
    private final List<Node> nodes;
    private final List<ConnectionGene> connections;
    private double fitness;
    private int inputSize;
    private int outputSize;

    private static final Random random = new Random();

    public Genome(int inputSize, int outputSize) {
        this.nodes = new ArrayList<Node>();
        this.connections = new ArrayList<ConnectionGene>();
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.initializeNodes();
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public Node getRandomNode() {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("The node list is empty !");
        }
        int nodeId = random.nextInt(nodes.size());
        return nodes.get(nodeId);
    }

    private Node getNodeById(int nodeId) {
        for (Node node : nodes) {
            if (node.getId() == nodeId)
                return node;
        }
        return null;
    }

    private void initializeNodes() {
        for (int i = 0; i < inputSize; i++) {
            nodes.add(new Node(i, Node.NodeType.INPUT));
        }
        nodes.add(new Node(inputSize, Node.NodeType.BIAS));
        for (int i = 0; i < outputSize; i++) {
            nodes.add(new Node(inputSize + 1 + i, Node.NodeType.OUTPUT));
        }
    }

    public double[] evaluate(double[] inputs) {
        double[] output = new double[outputSize];
        int indexOutput = 0;

        for (int i = 0; i < inputSize; i++) {
            nodes.get(i).setValue(inputs[i]);
        }
        List<Node> sortedNodes = topologicalSort();
        for (Node node : sortedNodes) {
            double sum = 0;

            if (node.getType() == Node.NodeType.INPUT || node.getType() == Node.NodeType.BIAS) continue;

            for (ConnectionGene conn : getIncomingConnections(node)) {
                if (conn.isEnabled()) {
                    Node inputNode = getNodeById(conn.getInNode());
                    sum += inputNode.getValue() * conn.getWeight();
                }
            }
            node.setValue(sigmoid(sum));
            if (node.getType() == Node.NodeType.OUTPUT) {
                output[indexOutput] = node.getValue();
                indexOutput++;
            }

        }

        return output;
    }

    private List<Node> topologicalSort() {
        List<Node> sorted = new ArrayList<>();
        Set<Node> visited = new HashSet<>();

        for (Node node : nodes) {
            if (!visited.contains(node)) {
                visitNode(node, visited, sorted);
            }
        }
        return sorted;
    }

    private void visitNode(Node node, Set<Node> visited, List<Node> sorted) {
        if (visited.contains(node))
            return;

        visited.add(node);

        List<ConnectionGene> incomingConnection = getIncomingConnections(node);

        for (ConnectionGene conn : incomingConnection) {

            Node inputNode = getNodeById(conn.getInNode());
            if (inputNode == null) {
                System.out.println("conn : " + conn);
                System.out.println("all nodes : ");
                for (Node nodet : nodes) {
                    System.out.println(nodet);
                }
            }

            if (!visited.contains(inputNode)) {
                visitNode(inputNode, visited, sorted);
            }
        }

        sorted.add(node);
    }

    private List<ConnectionGene> getIncomingConnections(Node node) {
        // System.out.println("node : " + node);
        List<ConnectionGene> incomingConnections = new ArrayList<>();
        for (ConnectionGene conn : connections) {
            if (conn.getOutNode() == node.getId()) {
                incomingConnections.add(conn);
            }
        }
        return incomingConnections;
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-4.9 * x));
    }

    private double tanh(double x) {
        return Math.tanh(x);
    }

    public boolean connectionAlreadyExist(Node source, Node target) {
        return connections.stream()
                .anyMatch(c -> c.getInNode() == source.getId() && c.getOutNode() == target.getId());
    }

    public boolean NodeAlreadyExist(int NodeId) {
        return nodes.stream().anyMatch(c -> c.getId() == NodeId);
    }

	public boolean hasNode(int id) {
        for (Node node : nodes) {
            if (node.getId() == id) return true;
        }
        return false;
	}

	public Node getNode(int NodeId) {
        for (Node node : nodes) {
            if (node.getId() == NodeId) return node;
        }
        return null;
	}
}
