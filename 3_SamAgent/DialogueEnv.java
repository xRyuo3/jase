import org.eclipse.paho.client.mqttv3.*;
import jason.environment.Environment;
import jason.asSyntax.*;

public class DialogueEnv extends Environment{
    private String broker = "tcp://localhost:1883";
    private String sub_topic = "rasa_channel";
    private String pub_topic = "sam_channel"; 
    
    private Literal last_msg_recv;

    MqttClient client;
    boolean waiting_msg;

    // Agent action on environment:
    final static String answer="answer";
    
   @Override
    public void init(String[] args) {
        waiting_msg=true;
        try {
            this.client = new MqttClient(broker, MqttClient.generateClientId());
            this.client.connect();
            this.client.subscribe(this.sub_topic);
        } catch (MqttException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        updatePercepts();
    }

    void updatePercepts() {
        System.out.println("sto facendo updatePercepts");
        while(waiting_msg){
            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Received message on topic '" + topic + "': " + new String(message.getPayload()));
                    last_msg_recv=Literal.parseLiteral(new String(message.getPayload()));
                    System.out.println("Received message"+ last_msg_recv);
                    // questo addPercept scatena cose solo la prima volta
                    //addPercept("samAgent",Literal.parseLiteral("request("+new String(message.getPayload())+")"));
                    addPercept("samAgent",last_msg_recv);
                    waiting_msg=false;
                }
            
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
                // this.waiting_msg=false;
            });
            // this.waiting_msg=false;
            try { Thread.sleep(500); } catch (Exception e) {}

        }
        System.out.println("Im out");
        // this.waiting_msg=true;
    }
    
    @Override
    public boolean executeAction(String ag, Structure action) {
        System.out.println("sto facendo executeAction");

        if(action.getFunctor().equals(answer)){
            // String msg="CIAO";
            removePercept("samAgent",last_msg_recv);
            String samAnswer=Term2Json(action.getTerm(0), action.getTerm(1));
            System.out.println("Sending message '" + samAnswer + "' on topic '" + pub_topic);

            MqttMessage new_message = new MqttMessage(samAnswer.getBytes());
            try {
                this.client.publish(pub_topic, new_message);
                System.out.println("Pub message '" + samAnswer + "' on topic '" + pub_topic);
                waiting_msg=true;
                // updatePercepts();
            } catch (MqttPersistenceException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MqttException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
           // removePerceptsByUnif("samAgent",Literal.parseLiteral("request(_,_)"));
            // removePercept("samAgent",Literal.parseLiteral("ciao"));
        }
        updatePercepts();
        try { Thread.sleep(250); } catch (Exception e) {}
        informAgsEnvironmentChanged();

       return true;
    }
    private String Term2Json(Term t1,Term t2){  
        String t12j="["+t1.toString().replaceAll(",","\",\"").replaceAll("\\[","\"").replaceAll("\\]", "\"")+"]";
        return String.format("{\"input\":%s,\"output\":%s}",t12j.toString(),t2.toString()); 
    }
    
    
}
