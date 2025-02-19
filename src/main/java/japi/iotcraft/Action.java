package japi.iotcraft;

import com.google.gson.JsonParser;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public final class Action {
  private static final Logger LOGGER = LoggerFactory.getLogger(Iotcraft.MOD_ID);

  private Action() {
  }

  public static void subscribeToChat() {
    MqttManager.subscribeTopic(Iotcraft.getConfig().mqtt.mainTopic + "/action/chat", (Mqtt3Publish publish) -> {

      var message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);
      var jsonObject = JsonParser.parseString(message).getAsJsonObject();

      //  Messages sent to this topic requires the keys `message` and `isCommand`.
      if (jsonObject.has("message") && jsonObject.has("isCommand")) {

        var text = jsonObject.get("message").getAsString();
        var isCommand = jsonObject.get("isCommand").getAsBoolean();

        if (isCommand) {
          //  Send a text as a command through the player without leading slash, similar to command blocks.
          Iotcraft.getPlayer().networkHandler.sendCommand(text);
        } else {
          //  Send a text as a chat message through the player.
          Iotcraft.getPlayer().networkHandler.sendChatMessage(text);
        }
      } else {
        var errorMessage = "Keys 'message' and 'isCommand' required";
        Action.LOGGER.warn(errorMessage);
      }
    });
  }

  public static void subscribeToOptions() {
    MqttManager.subscribeTopic(Iotcraft.getConfig().mqtt.mainTopic + "/action/option/fov", (Mqtt3Publish publish) -> {

      var message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

      var jsonObject = JsonParser.parseString(message).getAsJsonObject();

      if (jsonObject.has("fov")) {
        //  Sets the fov to the specified number. If outside range, nothing happens.
        Integer fov = jsonObject.get("fov").getAsInt();
        MinecraftClient.getInstance().options.getFov().setValue(fov);
      } else {
        var errorMessage = "Keys 'fov' required";
        Action.LOGGER.warn(errorMessage);
      }
    });
    MqttManager.subscribeTopic(Iotcraft.getConfig().mqtt.mainTopic + "/action/option/brightness", (Mqtt3Publish publish) -> {

      var message = new String(publish.getPayloadAsBytes(), StandardCharsets.UTF_8);

      var jsonObject = JsonParser.parseString(message).getAsJsonObject();

      if (jsonObject.has("brightness")) {
        //  Sets the fov to the specified number. If outside range, nothing happens.
        Double brightness = jsonObject.get("brightness").getAsDouble();
        MinecraftClient.getInstance().options.getGamma().setValue(brightness);
      } else {
        var errorMessage = "Keys 'brightness' required";
        Action.LOGGER.warn(errorMessage);
      }
    });
  }
}
