package run;

import commands.InsertCommand;
import commands.UpdateCommand;
import mods.AnswerType;
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
        System.out.println("execute command...");
        boolean exitStatus = invoker.execute(commandArguments);
        System.out.println("process request");
        System.out.println(commandArguments.getExtraArguments());
        System.out.println(MessageHolder.getMessages(MessageType.OUTPUT_INFO));
        ArrayList<String> outputInfo = MessageHolder.getOutputInfo();
        ArrayList<String> userErrors = MessageHolder.getUserErrors();
        return new ServerAnswer(outputInfo, userErrors, exitStatus,
                ((commandArguments.getCommandName().equals(UpdateCommand.getName()) ||
                        commandArguments.getCommandName().equals(InsertCommand.getName())) &&
                        commandArguments.getExtraArguments() == null ?
                        AnswerType.DATA_REQUEST : AnswerType.EXECUTION_RESPONSE));
    }
}
