package com.springJadeProject.framework.service.jade.spring.core.behaviour;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springJadeProject.framework.service.jade.spring.core.agent.AgentInterface;
import jade.core.Agent;
import jade.core.behaviours.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Alejandro Reyes
 *
 * This is a factory class to create custom behaviours in an easy way if you are new into JADE
 * Only very simple behaviours can be implemented this way.
 */

//@Stateless
@Service
@Qualifier("SimpleBehaviourFactory")
public class SimpleBehaviourFactory{
    /**Consumer if it returns nothing -> https://docs.oracle.com/javase/8/docs/api/java/util/function/Consumer.html
     * Supplier if it takes nothing -> https://docs.oracle.com/javase/8/docs/api/java/util/function/Supplier.html
     * Runnable if it does neither -> https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
     * Function if it takes and returns
     */

    @FunctionalInterface
    public interface ActionInterface {
        void action();
    }

    private Behaviour currentBehaviour;

    public Behaviour addOneShotBehaviour(ActionInterface actionInterface, AgentInterface agentInterface) {
        currentBehaviour = new FactoryOneShotBehaviour(actionInterface);
        agentInterface.addBehaviourToAgent(currentBehaviour);
        return currentBehaviour;
    }


    public Behaviour addCyclicBehaviour(ActionInterface actionInterface, AgentInterface agentInterface) {
        currentBehaviour = new FactoryCyclicBehaviour(actionInterface);
        agentInterface.addBehaviourToAgent(currentBehaviour);
        return currentBehaviour;
    }


    public Behaviour addTickerBehaviour(ActionInterface actionInterface, AgentInterface agentInterface, long period) {
        currentBehaviour = new FactoryTickerBehaviour(agentInterface.getAgentInstance(), period, actionInterface);
        agentInterface.addBehaviourToAgent(currentBehaviour);
        return currentBehaviour;
    }


    public Behaviour addWakerBehaviour(ActionInterface actionInterface, AgentInterface agentInterface, long timeout) {
        currentBehaviour = new FactoryWakerBehaviour(agentInterface.getAgentInstance(), timeout, actionInterface);
        agentInterface.addBehaviourToAgent(currentBehaviour);
        return currentBehaviour;
    }


    public Behaviour addWakerBehaviour(ActionInterface actionInterface, AgentInterface agentInterface, Date wakeupDate) {
        currentBehaviour = new FactoryWakerBehaviour(agentInterface.getAgentInstance(), wakeupDate, actionInterface);
        agentInterface.addBehaviourToAgent(currentBehaviour);
        return currentBehaviour;
    }


    public <T extends SharedVariableInterface> Behaviour addSimpleBehaviour(Consumer<T> actionIn,
                                        Function<T, Boolean> doneIn, T sharedVariable,
                                        AgentInterface agentInterface) {

        currentBehaviour = new FactorySimpleBehaviour<T>(actionIn, doneIn, sharedVariable);
        agentInterface.addBehaviourToAgent(currentBehaviour);
        return currentBehaviour;
    }

    private class FactoryOneShotBehaviour extends OneShotBehaviour implements BehaviourWithFactoryInterface{
        ActionInterface actionInterface;

        FactoryOneShotBehaviour(ActionInterface actionInterfaceIn){
            super();
            actionInterface = actionInterfaceIn;
        }

        @Override
        public void action() {
            actionInterface.action();
        }

        public String getAgentLocalName(){ //only for api purposes.
            String res = null;
            if (myAgent != null && myAgent instanceof AgentInterface){
                res = ((AgentInterface)myAgent).getNickname();
            }
            return res;
        }

        @JsonIgnore //only for api purposes.
        @Override
        public Agent getAgent() {
            return super.getAgent();
        }

        @Override
        public Behaviour getInstance() {
            FactoryOneShotBehaviour newInstance = new FactoryOneShotBehaviour(this.actionInterface);
            newInstance.setBehaviourName(getBehaviourName());
            return newInstance;
        }

        @Override
        public Behaviour getInstance(Agent agent) {
            return getInstance();
        }
    }


    private class FactoryCyclicBehaviour extends CyclicBehaviour implements BehaviourWithFactoryInterface{
        ActionInterface actionInterface;

        FactoryCyclicBehaviour(ActionInterface actionInterfaceIn){
            super();
            actionInterface = actionInterfaceIn;
        }

        @Override
        public void action() {
            actionInterface.action();
        }

        public String getAgentLocalName(){ //only for api purposes.
            String res = null;
            if (myAgent != null && myAgent instanceof AgentInterface){
                res = ((AgentInterface)myAgent).getNickname();
            }
            return res;
        }

        @JsonIgnore //only for api purposes.
        @Override
        public Agent getAgent() {
            return super.getAgent();
        }

        @Override
        public Behaviour getInstance() {
            FactoryCyclicBehaviour newInstance = new FactoryCyclicBehaviour(this.actionInterface);
            newInstance.setBehaviourName(getBehaviourName());
            return newInstance;
        }

        @Override
        public Behaviour getInstance(Agent agent) {
            return getInstance();
        }
    }


    private class FactoryTickerBehaviour extends TickerBehaviour implements BehaviourWithFactoryInterface{
        ActionInterface actionInterface;

        FactoryTickerBehaviour(Agent a, long period, ActionInterface actionInterfaceIn) {
            super(a, period);
            actionInterface = actionInterfaceIn;
        }

        @Override
        protected void onTick() {
            actionInterface.action();
        }

        public String getAgentLocalName(){ //only for api purposes.
            String res = null;
            if (myAgent != null && myAgent instanceof AgentInterface){
                res = ((AgentInterface)myAgent).getNickname();
            }
            return res;
        }

        @JsonIgnore //only for api purposes.
        @Override
        public Agent getAgent() {
            return super.getAgent();
        }

        @Override
        public Behaviour getInstance() {
            return getInstance(this.getAgent());
        }

        @Override
        public Behaviour getInstance(Agent agent) {
            FactoryTickerBehaviour newInstance = new FactoryTickerBehaviour(agent, this.getPeriod(), this.actionInterface);
            newInstance.setBehaviourName(getBehaviourName());
            return newInstance;
        }

    }


    private class FactoryWakerBehaviour extends WakerBehaviour {
        ActionInterface actionInterface;

        FactoryWakerBehaviour(Agent a, Date wakeupDate, ActionInterface actionInterfaceIn) {
            super(a, wakeupDate);
            actionInterface = actionInterfaceIn;
        }

        FactoryWakerBehaviour(Agent a, long timeout, ActionInterface actionInterfaceIn) {
            super(a, timeout);
            actionInterface = actionInterfaceIn;
        }

        @Override
        protected void onWake() {
            actionInterface.action();
        }

        //only for api purposes.
        public String getAgentLocalName(){
            String res = null;
            if (myAgent != null && myAgent instanceof AgentInterface){
                res = ((AgentInterface)myAgent).getNickname();
            }
            return res;
        }

        @JsonIgnore //only for api purposes.
        @Override
        public Agent getAgent() {
            return super.getAgent();
        }
    }


    private class FactorySimpleBehaviour<T extends SharedVariableInterface> extends SimpleBehaviour
            implements BehaviourWithFactoryInterface {
        Consumer<T> action;
        Function<T, Boolean> done;
        T sharedVariable;

        FactorySimpleBehaviour(Consumer<T> actionIn, Function<T, Boolean> doneIn,
                               T sharedVariableIn) {
            action = actionIn;
            done = doneIn;
            sharedVariable = sharedVariableIn;
        }

        @Override
        public void action() {
            action.accept(sharedVariable);
        }

        @Override
        public boolean done() {
            return done.apply(sharedVariable);
        }

        @Override
        public void reset() {
            super.reset();
            sharedVariable.setFinished(false);
        }

        public String getAgentLocalName() { //only for api purposes.
            String res = null;
            if (myAgent != null && myAgent instanceof AgentInterface) {
                res = ((AgentInterface) myAgent).getNickname();
            }
            return res;
        }

        @JsonIgnore //only for api purposes.
        @Override
        public Agent getAgent() {
            return super.getAgent();
        }

        @Override
        public Behaviour getInstance() {
            FactorySimpleBehaviour<T> newInstance = new FactorySimpleBehaviour<>(this.action, this.done, this.sharedVariable);
            newInstance.setBehaviourName(getBehaviourName());
            return newInstance;
        }

        @Override
        public Behaviour getInstance(Agent agent) {
            return getInstance();
        }
    }

}
