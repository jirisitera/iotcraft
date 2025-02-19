package japi.iotcraft;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class MqttManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(Iotcraft.MOD_ID);
  @Getter
  @Setter
  private static Mqtt3AsyncClient client;

  public static void connect() {
    if (MqttManager.getClient() != null) {
      return;
    }
    LOGGER.info("Connecting to MQTT broker...");
    MqttManager.setClient(MqttClient.builder()
      .useMqttVersion3()
      .identifier(UUID.randomUUID().toString())
      .serverHost(Iotcraft.getConfig().mqtt.broker)
      .serverPort(Iotcraft.getConfig().mqtt.port)
      .buildAsync());
    MqttManager.getClient().connect().whenComplete((Mqtt3ConnAck connAck, Throwable throwable) -> {
      if (throwable != null) {
        LOGGER.info("Unable to connect to MQTT broker. Ensure the configuration is correct and broker is running.");
      } else {
        LOGGER.info("Client is successfully connected to MQTT broker.");
        Trigger.listenToClientEvents();
        Action.subscribeToChat();
        Action.subscribeToOptions();
      }
    });
  }

  public static void subscribeTopic(String topic, Subscribe subscribe) {
    client.subscribeWith()
      .topicFilter(topic)
      .callback(subscribe::runAction)
      .send();
  }

  public static void publishMessage(String topic, String message) {
    client.publishWith()
      .topic(topic)
      .payload(message.getBytes())
      .qos(MqttQos.EXACTLY_ONCE)
      .send();
  }

  public interface Subscribe {
    void runAction(Mqtt3Publish publish);
  }
}
