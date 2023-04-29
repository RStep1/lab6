package processing;

import commands.Command;
import mods.AnswerType;
import utility.CommandArguments;
import utility.ServerAnswer;

import java.util.Map;

public class CommandValidator {
    private AnswerType answerType;

    public CommandValidator(AnswerType answerType) {
        this.answerType = answerType;
    }

    public boolean validate(CommandArguments commandArguments) {
        Map<String, Command> commandMap  = CommandInvoker.getCommandMap();
        commandMap.forEach();
        return true;
    }

}
