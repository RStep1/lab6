package processing;

import mods.AnswerType;
import utility.CommandArguments;
import utility.ServerAnswer;

public class CommandValidator {
    private AnswerType answerType;

    public CommandValidator(AnswerType answerType) {
        this.answerType = answerType;
    }

    public boolean validate(CommandArguments commandArguments) {

        return true;
    }

}
