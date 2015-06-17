package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public abstract class CommandHandler {

    public static final String MESSAGE = "MESSAGE";
    public static final String SET_MESSAGE = "SET_MESSAGE";
    public static final String COMMAND = "COMMAND";
    public static final String STEP = "STEP";
    public static final String ACTIVITY = "ACTIVITY";
    public static final String SET_CONTACT = "SET_CONTACT";
    public static final String CONTACT = "CONTACT";

    private ExpressionMatcher expressionMatcher;
    private TTS textToSpeech;


    private Context context;
    private CommandHandlerManager commandHandlerManager;

    protected CommandHandler(String expressionTemplate, TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        this.expressionMatcher = new ExpressionMatcher(expressionTemplate);
        this.textToSpeech = textToSpeech;
        this.context = context;
        this.commandHandlerManager = commandHandlerManager;
    }


    public boolean matches(String command) {
        return this.expressionMatcher.matches(command);
    }

    public abstract CommandHandlerContext drive(CommandHandlerContext currentContext);

    public ExpressionMatcher getExpressionMatcher() {
        return expressionMatcher;
    }

    public TTS getTextToSpeech() {
        return textToSpeech;
    }

    public Context getContext() {
        return context;
    }

    public CommandHandlerManager getCommandHandlerManager() {
        return commandHandlerManager;
    }

    public CommandHandlerContext createContext(CommandHandlerContext currentContext, Activity activity, String command) {
        if(currentContext != null && currentContext.get(STEP, Integer.class) != 0) {
            return currentContext;
        }
        CommandHandlerContext commandHandlerContext = new CommandHandlerContext();
        commandHandlerContext.put(ACTIVITY, activity);
        commandHandlerContext.put(COMMAND, command);
        addSpecificCommandContext(commandHandlerContext);
        return commandHandlerContext;
    }

    protected abstract void addSpecificCommandContext(CommandHandlerContext commandHandlerContext);

    public String getSuggestion() {
        return getExpressionMatcher().getSuggestion();
    }

    public boolean isSimilar(String command) {
        return getExpressionMatcher().isSimilar(command);
    }
}
