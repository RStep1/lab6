package utility;

import mods.AnswerType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerAnswer implements Serializable {
    private final List<String> outputInfo;
    private final List<String> userErrors;
    private final boolean commandExitStatus;
    private final AnswerType answerType;

    public ServerAnswer(ArrayList<String> outputInfo, ArrayList<String> userErrors,
                        boolean commandExitStatus, AnswerType answerType) {
        this.outputInfo = outputInfo;
        this.userErrors = userErrors;
        this.commandExitStatus = commandExitStatus;
        this.answerType = answerType;
    }

    public List<String> getOutputInfo() {
        return outputInfo;
    }

    public List<String> getUserErrors() {
        return userErrors;
    }

    public boolean getCommandExitStatus() {
        return commandExitStatus;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }
}
