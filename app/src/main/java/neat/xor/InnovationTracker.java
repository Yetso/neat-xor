package neat.xor;

import java.util.HashMap;
import java.util.Map;

public class InnovationTracker {

    private static final Map<String, Integer> innovationMap = new HashMap<>();
    private static int nextInnovation = 1;

    public static int getInnovationTracker(int inNode, int outNode) {
        String key = inNode + "-" + outNode;
        return innovationMap.computeIfAbsent(key, _ -> nextInnovation++);
    }

    public static void reset() {
        innovationMap.clear();
        nextInnovation = 1;
    }
}
