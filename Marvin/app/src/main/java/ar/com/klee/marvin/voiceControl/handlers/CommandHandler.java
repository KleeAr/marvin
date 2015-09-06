package ar.com.klee.marvin.voiceControl.handlers;

import android.app.Activity;
import android.content.Context;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import java.util.ArrayList;
import java.util.List;

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

    private List<ExpressionMatcher> expressionMatchers;
    private TTS textToSpeech;


    private Context context;
    private CommandHandlerManager commandHandlerManager;

    protected CommandHandler(List<String> expressions, TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        this.expressionMatchers = new ArrayList<>(expressions.size());
        this.textToSpeech = textToSpeech;
        this.context = context;
        this.commandHandlerManager = commandHandlerManager;
        for (String expression : expressions) {
            expressionMatchers.add(new ExpressionMatcher(expression));
        }
    }

    protected CommandHandler(String expression, TTS textToSpeech, Context context, CommandHandlerManager commandHandlerManager) {
        this.textToSpeech = textToSpeech;
        this.context = context;
        this.commandHandlerManager = commandHandlerManager;
        this.expressionMatchers = new ArrayList<>();
        this.expressionMatchers.add(new ExpressionMatcher(expression));
    }


    public boolean matches(String command) {
        for (ExpressionMatcher matcher : expressionMatchers) {
            if (matcher.matches(command)) {
                return true;
            }
        }
        return false;
    }

    public abstract CommandHandlerContext drive(CommandHandlerContext currentContext);

    public ExpressionMatcher getExpressionMatcher(String command) {
        for (ExpressionMatcher matcher : expressionMatchers) {
            if (matcher.matches(command)) {
                return matcher;
            }
        }
        throw new RuntimeException("The expressionMatcher for this command wasn't found");
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
        if(currentContext != null && currentContext.getInteger(STEP) != 0) {
            return currentContext.put(COMMAND, command);
        }
        CommandHandlerContext commandHandlerContext = new CommandHandlerContext();
        commandHandlerContext.put(ACTIVITY, activity);
        commandHandlerContext.put(COMMAND, command);
        commandHandlerContext.put(STEP, 1);
        addSpecificCommandContext(commandHandlerContext);
        return commandHandlerContext;
    }

    protected abstract void addSpecificCommandContext(CommandHandlerContext commandHandlerContext);

    public String getSuggestion(String command) {
        return getSimilarExpressionMatcher(command).getSuggestion();
    }

    public boolean isSimilar(String command) {
        for (ExpressionMatcher matcher : expressionMatchers) {
            if (matcher.isSimilar(command)) {
                return true;
            }
        }
        return false;
    }

    public void addSpecificContextData(CommandHandlerContext context) {
        addSpecificCommandContext(context);
    }

    public ExpressionMatcher getSimilarExpressionMatcher(String command) {
        for (ExpressionMatcher matcher : expressionMatchers) {
            if (matcher.isSimilar(command)) {
                return matcher;
            }
        }
        return null;
    }
}
