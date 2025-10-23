package es.javier.timerPlugin.utils;

import java.util.HashMap;
import java.util.Map;

public class NumberPatterns {
    public static final Map<Integer, boolean[][]> numberPatterns = new HashMap<>();

    public void initializeNumberPatterns() {
        // Patrones de números 0-9 (3x5) optimizados
        numberPatterns.put(0, new boolean[][]{
                {true, true, true},
                {true, false, true},
                {true, false, true},
                {true, false, true},
                {true, true, true}
        });

        numberPatterns.put(1, new boolean[][]{
                {false, true, false},
                {true, true, false},
                {false, true, false},
                {false, true, false},
                {true, true, true}
        });

        numberPatterns.put(2, new boolean[][]{
                {true, true, true},
                {false, false, true},
                {true, true, true},
                {true, false, false},
                {true, true, true}
        });

        numberPatterns.put(3, new boolean[][]{
                {true, true, true},
                {false, false, true},
                {true, true, true},
                {false, false, true},
                {true, true, true}
        });

        numberPatterns.put(4, new boolean[][]{
                {true, false, true},
                {true, false, true},
                {true, true, true},
                {false, false, true},
                {false, false, true}
        });

        numberPatterns.put(5, new boolean[][]{
                {true, true, true},
                {true, false, false},
                {true, true, true},
                {false, false, true},
                {true, true, true}
        });

        numberPatterns.put(6, new boolean[][]{
                {true, true, true},
                {true, false, false},
                {true, true, true},
                {true, false, true},
                {true, true, true}
        });

        numberPatterns.put(7, new boolean[][]{
                {true, true, true},
                {false, false, true},
                {false, false, true},
                {false, false, true},
                {false, false, true}
        });

        numberPatterns.put(8, new boolean[][]{
                {true, true, true},
                {true, false, true},
                {true, true, true},
                {true, false, true},
                {true, true, true}
        });

        numberPatterns.put(9, new boolean[][]{
                {true, true, true},
                {true, false, true},
                {true, true, true},
                {false, false, true},
                {true, true, true}
        });
    }
}
