package run;

import commands.InsertCommand;
import commands.SaveCommand;
import commands.UpdateCommand;
import mods.AnswerType;
import mods.ExecuteMode;
import mods.MessageType;
import processing.CommandInvoker;
import utility.CommandArguments;
import utility.MessageHolder;
import utility.ServerAnswer;

import java.util.ArrayList;
public class RequestHandler {
    private CommandInvoker invoker;

    public RequestHandler(CommandInvoker invoker) {
        this.invoker = invoker;
    }

    public ServerAnswer processRequest(CommandArguments commandArguments) {
        MessageHolder.clearMessages(MessageType.OUTPUT_INFO);
        MessageHolder.clearMessages(MessageType.USER_ERROR);
        // System.out.println(commandArguments + "");
        boolean exitStatus = invoker.execute(commandArguments);
        // System.out.println(commandArguments.getExtraArguments());
        // System.out.println(MessageHolder.getMessages(MessageType.OUTPUT_INFO));
        ArrayList<String> outputInfo = MessageHolder.getOutputInfo();
        ArrayList<String> userErrors = MessageHolder.getUserErrors();
        AnswerType answerType = AnswerType.EXECUTION_RESPONSE;

        //если это команды update или insert и при этом нет дополнительных аргументов и это не команда из скрипта,
        // то выставляем запрос на получение дополниетльных аргументов
        if ((commandArguments.getCommandName().equals(UpdateCommand.getName()) ||
            commandArguments.getCommandName().equals(InsertCommand.getName())) &&
            commandArguments.getExtraArguments() == null &&
             commandArguments.getExecuteMode() == ExecuteMode.COMMAND_MODE) {
                answerType = AnswerType.DATA_REQUEST;
        }
        return new ServerAnswer(outputInfo, userErrors, exitStatus, answerType); 
    }
}
