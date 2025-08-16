package neat.xor;

import lombok.Data;

@Data
public class Node {
    public enum NodeType {
        INPUT, HIDDEN, OUTPUT, BIAS
    }

    private final int id;
    private final NodeType type;
    private double value;

    public Node(int id, NodeType type) {
        this.id = id;
        this.type = type;
        this.value = 0.0;
        this.value = (type == NodeType.BIAS) ? 1.0 : 0.0;
    }

    public Node(Node node) {
        this.id = node.id;
        this.type = node.type;
        this.value = node.value;
	}

	public String toString() {
        return "Id : " + id + ", type : " + type + ", value : " + value;

    }
}
