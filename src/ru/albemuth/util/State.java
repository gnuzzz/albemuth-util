package ru.albemuth.util;

public abstract class State {

    private Process process;
    private State nextState;

    public State(Process process) {
        this.process = process;
    }

    public Process getProcess() {
        return process;
    }

    public State getNextState() {
        return nextState;
    }

    public void setNextState(State nextState) {
        this.nextState = nextState;
    }

    public abstract void prepareDataForCheck() throws ProcessException;

    public abstract void check(Message message) throws ProcessException;

    private Message checkState(Message message) throws ProcessException {
        clearStateMessages(message);
        prepareDataForCheck();
        check(message);
        return message;
    }

    public abstract void activate() throws ProcessException;

    public abstract void saveChanges() throws ProcessException;

    public void clearStateMessages(Message message) {
        message.clear();
    }

    public boolean isMessageStatusPasses(Message message) {
        return message.getStatus().passes();
    }

    public State next() throws ProcessException {
        Message message = getProcess().getMessage();
        getProcess().setPreviousState(this);
        State nextState = this;
        clearStateMessages(message);
        check(message);
        if (isMessageStatusPasses(message)) {
            saveChanges();
            //getProcess().setPreviousState(this);
            for (nextState = getNextState() ; nextState != null && nextState.isMessageStatusPasses(nextState.checkState(message)); nextState = nextState.getNextState()) {}
            getProcess().setCurrentState(nextState);
            if (nextState != null) {
                nextState.activate();
            }
        }
        return nextState;
    }

    public void toState(State nextState) throws ProcessException {
        //getProcess().getMessage().clear();
        getProcess().setPreviousState(this);
        getProcess().setCurrentState(nextState);
        if (nextState != null) {
            nextState.checkState(getProcess().getMessage());
            nextState.activate();
        }
    }

    /*public void enter() throws ProcessException {}

    public void exit() throws ProcessException {}

    public void activate() throws ProcessException {}

    public void deactivate() throws ProcessException {}

    public State next() throws ProcessException {
        getProcess().getMessage().clear();
        State nextState = this;
        for (nextState.enter(); getProcess().getMessage().getStatus().accept(); nextState.enter()) {
            nextState.exit();
            nextState = nextState.getNextState();
            if (nextState == null) {
                break;
            }
        }
        if (nextState != this) {
            deactivate();
            activate(nextState);
        }
        return nextState;
    }

    protected void activate(State nextState) throws ProcessException {
        getProcess().setPreviousState(this);
        getProcess().setCurrentState(nextState);
        if (nextState != null) {
            nextState.activate();
        }
    }

    public void toState(State nextState) throws ProcessException {
        exit();
        deactivate();
        if (nextState != null) {
            nextState.enter();
        }
        activate(nextState);
    }*/
    
}
