package ar.com.klee.marvin.voiceControl.handlers;

import ar.com.klee.marvin.expressions.ExpressionMatcher;

public abstract class CommandHandler {

    public abstract boolean validateCommand();
    public abstract int drive(int step, String input);

}
