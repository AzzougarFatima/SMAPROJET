package Agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentCompagnie extends Agent {
    private int prixBillet = 300;

    @Override
    protected void setup() {
        System.out.println("Déploiement de l'agent compagnie : " + getAID().getLocalName());

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription services = new DFAgentDescription();
                services.setName(getAID());
                ServiceDescription description = new ServiceDescription();
                description.setName("Vente-Billets");
                description.setType("Vente-Billets");
                services.addServices(description);
                try {
                    DFService.register(myAgent, services);
                    System.out.println("Service Vente-Billets publié par " + getAID().getLocalName());
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });

        addBehaviour(new TickerBehaviour(this, 10000) {
            @Override
            protected void onTick() {
                prixBillet = (int) (200 + Math.random() * 150);
                System.out.println("[" + getAID().getLocalName() + "] Nouveau tarif mis à jour : " + prixBillet + " EUR");
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate template = MessageTemplate.MatchOntology("Achat-Vente-Billets");
                ACLMessage msg = receive(template);
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.CFP) {
                        System.out.println(getAID().getLocalName() + " a reçu une demande CFP de : " + msg.getSender().getLocalName());
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.PROPOSE);
                        reply.setContent(String.valueOf(prixBillet));
                        send(reply);
                    } else if (msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
                        System.out.println(">>> " + getAID().getLocalName() + " : Heureux ! Mon offre a été acceptée. Billet vendu à " + msg.getSender().getLocalName());
                    }
                } else {
                    block();
                }
            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Je suis en train de mourir : " + getAID().getLocalName());
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}