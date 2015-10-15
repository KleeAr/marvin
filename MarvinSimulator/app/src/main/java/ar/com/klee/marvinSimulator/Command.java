package ar.com.klee.marvinSimulator;

/*Clase para el manejo de la ayuda de los comandos de voz*/

public class Command {

    public String description;
    public String command;
    public String function;

    public Command() {
    }

    public Command(String description, String command, String function) {
        this.description = description;
        this.command = command;
        this.function = function;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }
}
