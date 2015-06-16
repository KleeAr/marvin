package ar.com.klee.marvin.voiceControl.handlers;

import android.content.Context;

import java.util.Map;

import ar.com.klee.marvin.expressions.ExpressionMatcher;
import ar.com.klee.marvin.voiceControl.CommandHandlerManager;
import ar.com.klee.marvin.voiceControl.TTS;

public abstract class CommandHandler {

    protected static final String MESSAGE = "MESSAGE";
    protected static final String SET_MESSAGE = "SET_MESSAGE";
    protected static final String INPUT = "INPUT";
    protected static final String STEP = "STEP";
    protected static final String COMMAND = "COMMAND";
    protected static final String CAMERA_ACTIVITY = "CAMERA_ACTIVITY";
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

}
