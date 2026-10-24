package Agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class AgentAgence extends Agent {
    private AID[] sellerAids;

    @Override
    protected void setup() {
        System.out.println("Déploiement de l'agent agence : " + getAID().getLocalName());

        ParallelBehaviour parallelBehaviour = new ParallelBehaviour();
        addBehaviour(parallelBehaviour);

        parallelBehaviour.addSubBehaviour(new WakerBehaviour(this, 3000) {
            @Override
            protected void onWake() {
                System.out.println("Tâche planifiée : recherche des vendeurs dans le DF...");
                DFAgentDescription services = new DFAgentDescription();
                ServiceDescription description = new ServiceDescription();
                description.setName("Vente-Billets");
                description.setType("Vente-Billets");
                services.addServices(description);
                try {
                    DFAgentDescription[] research = DFService.search(myAgent, services);
                    sellerAids = new AID[research.length];
                    System.out.println("Nombre de vendeurs trouvés : " + research.length);
                    for (int i = 0; i < research.length; i++) {
                        sellerAids[i] = research[i].getName();
                        System.out.println("Vendeur n°" + (i + 1) + " : " + sellerAids[i].getLocalName());
                    }
                    
                    lancerAppelOffres();
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void lancerAppelOffres() {
        if (sellerAids == null || sellerAids.length == 0) {
            System.out.println("Aucun vendeur disponible pour le moment !");
            return;
        }

        ACLMessage cfpMsg = new ACLMessage(ACLMessage.CFP);
        cfpMsg.setContent("Veuillez svp nous donner une proposition de prix pour le billet Casablanca-Paris");
        cfpMsg.setLanguage("Fr");
        cfpMsg.setOntology("Achat-Vente-Billets");
        for (AID aid : sellerAids) {
            cfpMsg.addReceiver(aid);
        }
        send(cfpMsg);
        System.out.println("Appel d'offres (CFP) envoyé à tous les vendeurs.");

        addBehaviour(new Behaviour() {
            private int reponsesRecues = 0;
            private int meilleurPrix = Integer.MAX_VALUE;
            private AID meilleurVendeur = null;

            @Override
            public void action() {
                MessageTemplate template = MessageTemplate.MatchOntology("Achat-Vente-Billets");
                ACLMessage msg = receive(template);
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.PROPOSE) {
                        int prixPropose = Integer.parseInt(msg.getContent());
                        System.out.println("Proposition reçue de " + msg.getSender().getLocalName() + " : " + prixPropose + " EUR");
                        
                        if (prixPropose < meilleurPrix) {
                            meilleurPrix = prixPropose;
                            meilleurVendeur = msg.getSender();
                        }
                        reponsesRecues++;
                    }
                } else {
                    block();
                }
            }

            @Override
            public boolean done() {
                return reponsesRecues >= sellerAids.length;
            }

            @Override
            public int onEnd() {
                if (meilleurVendeur != null) {
                    System.out.println(">>> Meilleure offre sélectionnée : " + meilleurPrix + " EUR chez " + meilleurVendeur.getLocalName());
                    
                    ACLMessage acceptMsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                    acceptMsg.addReceiver(meilleurVendeur);
                    acceptMsg.setOntology("Achat-Vente-Billets");
                    acceptMsg.setContent("Votre offre est acceptée!");
                    send(acceptMsg);
                }
                return super.onEnd();
            }
        });
    }

    @Override
    protected void beforeMove() {
        System.out.println("Avant la migration de l'agent " + getAID().getLocalName());
    }

    @Override
    protected void afterMove() {
        System.out.println("Après la migration de l'agent " + getAID().getLocalName());
    }

    @Override
    protected void takeDown() {
        System.out.println("Je suis en train de mourir : " + getAID().getLocalName());
    }
}
