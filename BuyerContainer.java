package Containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class BuyerContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl config = new ProfileImpl();
        config.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer buyerAgentContainer = runtime.createAgentContainer(config);

        Object[] paramsAgence = new Object[]{"Casablanca-Paris"};
        AgentController agence = buyerAgentContainer.createNewAgent("AgenceAchat", "Agents.AgentAgence", paramsAgence);

        buyerAgentContainer.start();
        agence.start();
        System.out.println("BuyerContainer et l'Agence démarrés.");
    }
}