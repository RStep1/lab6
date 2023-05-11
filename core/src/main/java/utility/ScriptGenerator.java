package utility;

import mods.FileType;

public class ScriptGenerator {
    private final int count;

    public ScriptGenerator(int count) {
        this.count = count;
    }

    public void generateInserts() {
        for (int i = 0; i < count; i++) {
            String element = "insert " + i + "\n";
            for (int j = 0; j < 7; j++) {
                element += 1 + "\n";
            }
            // System.out.println(element);
            FileHandler.writeToFile(element, FileType.TEST_SCRIPT);
        }
    }
    
}
