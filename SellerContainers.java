package Containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;

public class SellerContainers {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl config = new ProfileImpl();
        config.setParameter(ProfileImpl.MAIN_HOST, "localhost");
        AgentContainer sellerContainer = runtime.createAgentContainer(config);

        AgentController comp1 = sellerContainer.createNewAgent("RoyalAirMaroc", "Agents.AgentCompagnie", new Object[]{});
        AgentController comp2 = sellerContainer.createNewAgent("AirFrance", "Agents.AgentCompagnie", new Object[]{});
        AgentController comp3 = sellerContainer.createNewAgent("Ryanair", "Agents.AgentCompagnie", new Object[]{});

        sellerContainer.start();
        comp1.start();
        comp2.start();
        comp3.start();
        System.out.println("SellerContainer et les compagnies démarrés.");
    }
}
