package org.linguafranca.statemachine.xstate;

import java.util.*;

/**
 * Simple representation of a state of a finite state machine
 */
public class State {

    public String name;

    public String type = "atomic";
    public String initialState;
    public Map<String, State> states = new HashMap<>();
    public List<Transition> transitions = new ArrayList<>();
    public String id; // https://xstate.js.org/docs/guides/ids.html#custom-ids
    public List<String> tags = new ArrayList<>();

    public State(String name) {
        this.name = name;
    }

    public State addState(State state){
        states.put(state.name, state);
        return state;
    }

    public boolean isFinalState() {
        return type.equals("final");
    }

    /**
     * Simple representation of a transition between states
     */
    public static class Transition {
        public String event;
        public State from;
        public String to;
        public String guard;
        public List<String> actions = new ArrayList<>();

        public Transition(State from, String stateName) {
            this.from = from;
            this.to = Objects.isNull(stateName) ? from.name : stateName;
        }
    }
}
