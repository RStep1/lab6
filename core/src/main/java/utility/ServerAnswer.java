package utility;

import mods.AnswerType;

import java.io.Serializable;
import java.util.ArrayList;

public record ServerAnswer(ArrayList<String> outputInfo, ArrayList<String> userErrors, boolean commandExitStatus,
                           AnswerType answerType) implements Serializable {

    public String toString() {
        return String.format(
                """
                        __________________________________
                        Output info:         %s
                        User errors:         %s
                        Command exit status: %s
                        Answer type:         %s
                        ___________________________________
                        """,
                outputInfo, userErrors, commandExitStatus, answerType);
    }
}
