package org.linguafranca.statemachine.xstate;

import java.util.Iterator;
import java.util.Objects;

/**
 * Render state machine in "State Machine Cat" format.
 * <p>
 * See <a href=https://state-machine-cat.js.org>state machine cat</a> online renderer.
 * <p>
 * See <a href="https://github.com/sverweij/state-machine-cat">GitHub</a>
 */
public class CatRender {
    /**
     * Render a top level state
     * @param state state
     * @param b StringBuffer
     */
    public static void renderMachine(State state, StringBuilder b) {
        b.append(state.name.replace(" ", ""));
        render(state, 1, b);
        b.append(";\n");
        for (State.Transition t : state.transitions) {
            render(t, 0, b);
        }
    }

        /**
         * Render the passed state to the supplied builder
         * @param state a state
         * @param level the level what level of substate is this
         * @param b a string builder
         */
    public static void render(State state, int level, StringBuilder b) {
        // nothing else to do
        if (state.states.size() == 0) {
            return;
        }

        Iterator<State> it = state.states.values().iterator();
        // substates in braces
        b.append("{\n");


        // put in a pseudo state named initial ... for StateCat
        if (!state.initialState.isEmpty()) {
            b.append(" ".repeat(level * 3));
            b.append("initial.").append(state.name.replace(" ", "_")).append(",\n");
        }

        // comma separated list of states, terminated with semicolon
        while (it.hasNext()) {
            State s = it.next();
            b.append(" ".repeat(level * 3));
            b.append(s.name);
            // process any substates
            render(s, level+1, b);

            if (it.hasNext()){
                b.append(",\n");
            }
        }
        b.append(";\n");

        if (!state.initialState.isEmpty()) {
            render(new State.Transition(new State("initial."+state.name), state.initialState), level, b);
        }
        // process any substates and render their transitions at this level
        for (State s: state.states.values()) {
            for (State.Transition t : s.transitions) {
                render(t, level, b);
            }
            // add a transition to a state named "final"+something for stateCat
            if (s.isFinalState()) {
                render(new State.Transition(s, "final"+s.name.replace(" ", "_")), level, b);
            }
        }
        // close out substates

        b.append(" ".repeat((level-1) * 3)).append("}");
    }

    /**
     * Render transition
     * @param transition transition to render
     * @param level for indentation
     * @param b StringBuilder to use
     */
    public static void render(State.Transition transition, int level, StringBuilder b){
        render(transition, level, b, "");
    }
    public static void render(State.Transition transition, int level, StringBuilder b, String comment) {
        b.append(" ".repeat(level * 3));
        if (!comment.isEmpty()){
            b.append("# ").append(comment).append("\n");
        }

        // translate ID based destination
        String destination = transition.to;
        if (destination.startsWith("#") || destination.startsWith(".")){
            destination = destination.substring(destination.lastIndexOf(".")+1);
        }
        b.append(String.format("%s -> %s :", transition.from.name.replace(" ", "_"), destination));
        if (Objects.nonNull(transition.event)) {
            b.append(String.format(" %s ", transition.event));
        }
        if (Objects.nonNull(transition.guard)) {
            b.append(String.format(" [%s] ", transition.guard));
        }
        if (transition.actions.size() > 0) {
            b.append("/ ");
            b.append(String.join(",", transition.actions));
        }
        b.append(";");
        b.append("\n");
    }
}
