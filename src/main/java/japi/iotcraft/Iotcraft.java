package japi.iotcraft;

import com.google.gson.Gson;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import japi.iotcraft.component.Action;
import japi.iotcraft.component.Trigger;
import japi.iotcraft.config.ModConfig;
import lombok.Getter;
import lombok.Setter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Iotcraft implements ClientModInitializer {
  public static final String MOD_ID = "iotcraft";
  public static final Gson GSON = new Gson();
  private static final Logger LOGGER = LoggerFactory.getLogger(Iotcraft.MOD_ID);
  @Getter
  @Setter
  private static ModConfig config;
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

  public static void connectMqttBroker() {
    LOGGER.info("Attempting to connect to MQTT broker...");
    client = MqttClient.builder()
      .useMqttVersion3()
      .identifier(UUID.randomUUID().toString())
      .serverHost(Iotcraft.getConfig().mqtt.broker)
      .serverPort(Iotcraft.getConfig().mqtt.port)
      .buildAsync();
    client.connect().whenComplete((Mqtt3ConnAck connAck, Throwable throwable) -> {
      if (throwable != null) {
        LOGGER.warn("Unable to connect to MQTT broker. Ensure the configuration is correct and broker is running.");
      } else {
        LOGGER.info("Client is successfully connected to MQTT broker.");
        Trigger.listenToClientEvents();
        Action.subscribeToChat();
        Action.subscribeToOptions();
      }
    });
  }

  @Override
  public void onInitializeClient() {
    // register config
    AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    Iotcraft.setConfig(AutoConfig.getConfigHolder(ModConfig.class).getConfig());
    // connect to MQTT broker
    Iotcraft.connectMqttBroker();
  }

  public interface Subscribe {
    void runAction(Mqtt3Publish publish);
  }
}
