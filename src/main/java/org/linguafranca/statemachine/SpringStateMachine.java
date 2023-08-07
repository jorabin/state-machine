package org.linguafranca.statemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.monitor.AbstractStateMachineMonitor;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Evaluation of use of Spring State Machine without any of the Spring goodness ...
 */
public class SpringStateMachine {
    static Logger logger = LoggerFactory.getLogger(SpringStateMachine.class.getSimpleName());

    public static class TestStateMachineMonitor extends AbstractStateMachineMonitor<String, String> {

        @Override
        public void transition(StateMachine<String, String> stateMachine,
                               Transition<String, String> transition,
                               long duration) {
            logger.info(stateMachine.toString());
        }

        @Override
        public void action(StateMachine<String, String> stateMachine,
                           Function<StateContext<String, String>, Mono<Void>> action, long duration) {
            logger.info(stateMachine.toString());
        }
    }

    public static class SML<S, E> extends StateMachineListenerAdapter<S, E> {

        @Override
        public void stateEntered(State<S, E> state) {
            logger.info("Entered {}", state.getId());
        }

        @Override
        public void stateExited(State<S, E> state) {
            logger.info("Exited {}",state.getId());
        }

        @Override
        public void stateMachineStarted(StateMachine<S, E> stateMachine) {
            logger.info("Started {}",stateMachine.toString());
        }

        @Override
        public void stateMachineStopped(StateMachine<S, E> stateMachine) {
            logger.info("Stopped {}",stateMachine.toString());
        }

    }


    public static void main(String[] args) throws Exception {

        StateMachineBuilder.Builder<String, String> builder
                = StateMachineBuilder.builder();
        builder.configureStates().withStates()
                .initial("SI")
                .state("S1")
                .end("SF");

        builder.configureTransitions()
                .withExternal()
                .source("SI").target("S1").event("E1")
                .and().withExternal()
                .source("S1").target("SF").event("E2");



        builder.configureConfiguration()
                .withMonitoring()
                .monitor(new TestStateMachineMonitor());

        builder.configureConfiguration()
                        .withConfiguration()
                                .listener(new SML<>());
        StateMachine<String, String> machine = builder.build();

        logger.info("Starting ...");
        machine.startReactively().subscribe();
        logger.info(machine.sendEvent(Mono.just(MessageBuilder
                .withPayload("E1").build())).blockLast().getMessage().getPayload());
        logger.info(machine.sendEvent(Mono.just(MessageBuilder
                .withPayload("E2").build())).blockLast().getMessage().getPayload());

        logger.info("{}", machine.stopReactively().block());
    }
}
