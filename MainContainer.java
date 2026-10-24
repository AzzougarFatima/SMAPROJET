package Containers;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.ControllerException;

public class MainContainer {
    public static void main(String[] args) throws ControllerException {
        Runtime runtime = Runtime.instance();
        ProfileImpl profileImpl = new ProfileImpl();
        profileImpl.setParameter(ProfileImpl.GUI, "true");
        AgentContainer mainAgentContainer = runtime.createMainContainer(profileImpl);
        mainAgentContainer.start();
        System.out.println("MainContainer démarré avec succès.");
    }
}
