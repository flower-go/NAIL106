package mas.cv1;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.util.Date;


//a simple agent which sends "hi" message every 5 seconds to the messaging server
public class SimpleChatClient extends Agent {

    @Override
    protected void setup() {
        super.setup();

        //messaging-server service description
        ServiceDescription sd = new ServiceDescription();
        sd.setType("messaging-client");
        sd.setName("client");

        //description of this agents and the services it provides
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(this.getAID());
        dfd.addServices(sd);

        //registration to DF
        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        //add the sending behavior
        this.addBehaviour(new MsgSendingBehavior(this));
        this.addBehaviour(new MessageReceivingBehaviour());
    }

    @Override
    protected void takeDown() {
        super.takeDown();
        //derigister at the end
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    //simple behavior which sends a message every 5 seconds
    class MsgSendingBehavior extends TickerBehaviour {

        public MsgSendingBehavior(Agent a) {
            super(a, 5000);
        }

        @Override
        public void onTick() {

            //messaging-server service description
            ServiceDescription sd = new ServiceDescription();
            sd.setType("messaging-server");

            //server agent description
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.addServices(sd);

            try {
                //search for all servers
                DFAgentDescription[] servers = DFService.search(myAgent, dfd);
                if (servers.length == 0) {
                    System.err.println("No servers found");
                    return;
                }
                //create the message and send it to the first server in the list
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(servers[0].getName());
                msg.setContent("Hi");
                myAgent.send(msg);
            } catch (FIPAException e) {
                e.printStackTrace();
            }

        }
    }

    //a behavior which receives, prints and forwards the messages
    class MessageReceivingBehaviour extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg == null) {
                block();
                return;
            }

            //print the received message
            System.out.println("[" + new Date().toString() + "] " + msg.getSender().getName() + ": " + msg.getContent());

            //description of the messaging-client service (for searching)
            ServiceDescription sd = new ServiceDescription();
            sd.setType("messaging-client");

            //description of the client agents
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.addServices(sd);
            

            block();
        }
    }

}
