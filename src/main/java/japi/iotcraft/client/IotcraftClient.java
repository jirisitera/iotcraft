package japi.iotcraft.client;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import japi.iotcraft.Iotcraft;
import japi.iotcraft.component.Action;
import japi.iotcraft.component.Trigger;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class IotcraftClient implements ClientModInitializer {
  public static final Gson gson = new Gson();
  private static final Logger LOGGER = LoggerFactory.getLogger(Iotcraft.MOD_ID);
  @Getter
  @Setter
  private static ClientPlayerEntity player;
  private static Mqtt3AsyncClient client;

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

  @Override
  public void onInitializeClient() {
    LOGGER.info("Attempting to connect to MQTT broker...");
    client = MqttClient.builder()
      .useMqttVersion3()
      .identifier(UUID.randomUUID().toString())
      .serverHost(Iotcraft.CONFIG.broker())
      .serverPort(Iotcraft.CONFIG.port())
      .buildAsync();
    client.connect().whenComplete((Mqtt3ConnAck connAck, Throwable throwable) -> {
      if (throwable != null) {
        var errorMessage = "Unable to connect to MQTT broker. Ensure the configuration is correct and broker is running.";
        LOGGER.warn(errorMessage);
      } else {
        var successMessage = "Client is successfully connected to MQTT broker.";
        LOGGER.info(successMessage);
        Trigger.listenToClientEvents();
        Action.subscribeToChat();
        Action.subscribeToOptions();
      }
    });
  }

  public interface Subscribe {
    void runAction(Mqtt3Publish publish);
  }
}
