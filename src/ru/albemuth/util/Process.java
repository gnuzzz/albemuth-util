package ru.albemuth.util;

public abstract class Process {

    private String id;
    private Message message = new Message();
    private State currentState;
    private State previousState;

    public Process(String id) {
        this.id = id;
        this.createStatesGraph();
    }

    public String getId() {
        return id;
    }

    public Message getMessage() {
        return message;
    }

    protected abstract void createStatesGraph();

    public abstract void start() throws ProcessException;

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State state) throws ProcessException {
        this.currentState = state;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }
    
}
