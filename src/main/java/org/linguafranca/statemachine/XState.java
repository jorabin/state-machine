package org.linguafranca.statemachine;

import org.linguafranca.statemachine.xstate.CatRender;
import org.linguafranca.statemachine.xstate.State;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static org.linguafranca.statemachine.xstate.XStateProcessor.processMachine;

/**
 * Read a json file representing an XState state machine and render
 */
public class XState {

    public static void main(String[] args) throws IOException {
        try (InputStream is = XState.class.getClassLoader().getResourceAsStream("calculator.json");
             JsonReader reader = Json.createReader(is)) {
            JsonObject json = reader.readObject();
            // load JSON
            State state = processMachine(json);
            // render JSON
            StringBuilder builder = new StringBuilder();
            CatRender.renderMachine(state, builder);
            System.out.println(builder);
        }
    }

    /**
     * Fetch a JsonObject from a stream (must be the only content of the stream)
     * @param stream stream to read
     * @return JsonObject
     */
    public static JsonObject fromStream(InputStream stream) {
        try (JsonParser parser = Json.createParser(stream)) {
            Event event = parser.next();

            if (Objects.requireNonNull(event) == Event.START_OBJECT) {
                return parser.getObject();
            } else {
                throw new IllegalStateException("Expecting Object");
            }
        }
    }
}
