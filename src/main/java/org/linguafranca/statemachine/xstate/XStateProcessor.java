package org.linguafranca.statemachine.xstate;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class XStateProcessor {
    static Logger logger = LoggerFactory.getLogger(XStateProcessor.class.getSimpleName());

    public static State processMachine(JsonObject json){
        State state = new State(json.getString("id", "root"));
        processState(json, state);
        return state;
    }

    public static void processState(JsonObject stateObject, State state) {
        for (String key: stateObject.keySet()) {
            switch (key) {
                case "type" -> state.type = stateObject.getString("type");
                case "id" -> state.id = stateObject.getString("id");
                case "initial" -> state.initialState = stateObject.getString("initial");
                case "tags" -> {
                    if (stateObject.getValue("/tags") instanceof JsonArray a) {
                        a.forEach(s -> state.tags.add(((JsonString)s).getString()));
                    } else {
                        state.tags.add(stateObject.getString("tags"));
                    }
                }
                // process substates
                case "states" -> {
                    JsonObject states = stateObject.getJsonObject("states");
                    for (Map.Entry<String, JsonValue> entry : states.entrySet()) {
                        State currentState = state.addState(new State(entry.getKey()));
                        JsonObject subStateObject = entry.getValue().asJsonObject();
                        processState(subStateObject, currentState);
                    }
                }
                // process transitions
                case "on" -> {
                    JsonObject on = stateObject.getJsonObject("on");
                    for (Map.Entry<String, JsonValue> transition : on.entrySet()) {
                        if (transition.getValue() instanceof JsonObject o) {
                            addTransition(state, transition.getKey(), o);
                        } else {
                            for (JsonValue t : transition.getValue().asJsonArray()) {
                                addTransition(state, transition.getKey(), t.asJsonObject());
                            }
                        }
                    }
                }
                // TODO always, onDone, after, numeric, parallel, invoke
                // history/deep, history/shallow, entry, exit, meta, tags
                default -> logger.warn("State contains key '{}' which was not processed", key);
            }
        }
    }

    private static void addTransition(State state, String key, JsonObject transitionDef) {
        // default self transition
        String destination = state.name;
        if (transitionDef.containsKey("target")) {
            destination = transitionDef.getString("target");
        }
        State.Transition t = new State.Transition(state, destination);
        t.event = key;

        if (transitionDef.containsKey("cond")) {
            t.guard = transitionDef.getString("cond");
        }
        // in
        t.actions.clear();
        if (transitionDef.containsKey("actions")) {
            if (transitionDef.getValue("/actions") instanceof JsonObject o) {
                t.actions.add(o.getString("type"));
            } else {
                JsonArray actions = transitionDef.getValue("/actions").asJsonArray();
                for (JsonValue action : actions) {
                    t.actions.add(action.asJsonObject().getString("type"));
                }
            }
        }
        state.transitions.add(t);
    }
}
