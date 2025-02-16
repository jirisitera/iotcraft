package japi.iotcraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import japi.iotcraft.Iotcraft;
import japi.iotcraft.MqttManager;

public class TriggerUpdate {

  private final String topic;
  private final String key;
  private String prevValue;
  private String prevValue1;
  private String key1;

  public TriggerUpdate(String triggerTopic, String keyName) {
    topic = triggerTopic;
    key = keyName;
  }

  public TriggerUpdate(String triggerTopic, String keyName, String keyName1) {
    topic = triggerTopic;
    key = keyName;
    key1 = keyName1;
  }

  public void update(String newValue) {
    if (!newValue.equals(prevValue)) {
      var obj = new JsonObject();
      obj.add(key, new JsonPrimitive(newValue));
      sendMqttMessage(obj);
      prevValue = newValue;
    }
  }

  public void update(String newValue, String newValue1) {
    if (!newValue.equals(prevValue) || !newValue1.equals(prevValue1)) {
      var obj = new JsonObject();
      obj.add(key, new JsonPrimitive(newValue));
      obj.add(key1, new JsonPrimitive(newValue1));
      sendMqttMessage(obj);
      prevValue = newValue;
      prevValue1 = newValue1;
    }
  }

  private void sendMqttMessage(JsonObject payloadObj) {
    MqttManager.publishMessage(topic, Iotcraft.GSON.toJson(payloadObj));
  }
}
