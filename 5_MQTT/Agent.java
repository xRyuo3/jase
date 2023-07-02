import org.eclipse.paho.client.mqttv3.*;

public class Agent {
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883";
        String sub_topic = "here_agent_is_listening";
        String pub_topic = "here_rasa_is_listening";

        String messageContent = "Hello Python, here is Java!";

        try {
            try (MqttClient client = new MqttClient(broker, MqttClient.generateClientId())) {
                client.connect();

                client.setCallback(new MqttCallback() {
                    public void connectionLost(Throwable cause) {
                        System.out.println("Connection lost: " + cause.getMessage());
                    }

                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        System.out.println("Received message on topic '" + topic + "': " + new String(message.getPayload()));

                        System.out.println("Sending message '" + messageContent + "' on topic '" + pub_topic);
                        MqttMessage new_message = new MqttMessage(messageContent.getBytes());
                        client.publish(pub_topic, new_message);
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                    }
                });

                client.subscribe(sub_topic);
            }
            // Keep the program running to receive messages
            while (true) {
                // Do other tasks if needed
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
