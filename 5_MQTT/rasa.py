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
