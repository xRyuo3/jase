# Server

Best one to use: https://www.emqx.io/

`docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083 emqx/emqx`

Script bash to handle the server:

```bash
#!/usr/bin/bash

emqx_id=$(docker ps --filter "name=emqx" -aq)

if [ "$1" == "start" ]; then
    if [ -n "$emqx_id" ]; then
        echo "The Server is already running."
    else
        echo "Starting the MQTT Server ..."
        docker run -d --name emqx -p 1883:1883 -p 8083:8083 -p 8084:8084 -p 8883:8883 -p 18083:18083 emqx/emqx
    fi
elif [ "$1" == "stop" ]; then
    if [ -n "$emqx_id" ]; then
        echo "Stopping the Server ..."
        docker kill $(docker ps --filter "name=emqx" -aq)
        docker rm $(docker ps --filter "name=emqx" -aq)
    else
        echo "The Server is already stopped."
    fi
elif [ "$1" == "status" ]; then
    if [ -n "$emqx_id" ]; then
        echo "The Server is running."
    else
        echo "The Server is down."
    fi
else
    echo "Invalid argument. Please provide 'start', 'stop' or 'status'."
fi
```

# Python Client

Install mqtt package

`pip install paho-mqtt`

Simple Python script:

```python
import paho.mqtt.client as mqtt

# MQTT broker information
broker_address = "localhost"
broker_port = 1883

sub_topic = "here_rasa_is_listening"
pub_topic = "here_agent_is_listening"

# Callback function when a connection is established
def on_connect(client, userdata, flags, rc):
    print("Connected to MQTT broker")

    print(f"Subscribing to '{sub_topic}'")
    client.subscribe(sub_topic)

    message = "Hello Java, here is Python!"
    client.publish(pub_topic, message)

# Callback function when a message is received
def on_message(client, userdata, msg):
    print("Received message: " + str(msg.payload))

# Create MQTT client instance
client = mqtt.Client()

# Set the callback functions
client.on_connect = on_connect
client.on_message = on_message

# Connect to MQTT broker
client.connect(broker_address, broker_port, 60)

# Loop continuously to handle MQTT network traffic and callbacks
client.loop_forever()

```

# Java Client

Official link:

https://www.eclipse.org/paho/

Github link:

https://github.com/eclipse/paho.mqtt.java

Jar Download folder:

Root: https://repo.eclipse.org/content/repositories/paho-releases/

<!-- Final: https://repo.eclipse.org/content/repositories/paho-releases/org/eclipse/paho/mqtt-client/0.4.0/ -->
Final: https://repo.eclipse.org/content/repositories/paho-releases/org/eclipse/paho/org.eclipse.paho.client.mqttv3/1.2.5/


Simple Java code snippet:

```java
import org.eclipse.paho.client.mqttv3.*;

public class Agent {
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883";
        String sub_topic = "here_agent_is_listening";
        String pub_topic = "here_rasa_is_listening";

        String messageContent = "Hello Python, here is Java!";

        try {
            MqttClient client = new MqttClient(broker, MqttClient.generateClientId());
            client.connect();

            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost: " + cause.getMessage());
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("Received message on topic '" + topic + "': " + new String(message.getPayload()));

                    System.out.println("Sending message '" + messageContent + "' on topic '" + topic);
                    MqttMessage new_message = new MqttMessage(messageContent.getBytes());
                    client.publish(pub_topic, new_message);
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            client.subscribe(sub_topic);

            // Keep the program running to receive messages
            while (true) {
                // Do other tasks if needed
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

```

Put the `.jar` file inside a `lib` folder at the same level of the Java code in which the file is located and the command is executed

Commands to compile and execute:

`javac -cp .:lib/org.eclipse.paho.client.mqttv3-1.2.5.jar Agent.java`

`java -cp .:lib/org.eclipse.paho.client.mqttv3-1.2.5.jar Agent`

Clean Java file:

`rm *.class; rm -rf paho*`

# Other Links

- https://github.com/emqx/emqx
- https://www.emqx.io/docs/en/v5.0/getting-started/getting-started.html#start-emqx
