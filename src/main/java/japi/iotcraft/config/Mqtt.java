package japi.iotcraft.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "mqtt_module")
public class Mqtt implements ConfigData {
  @ConfigEntry.Gui.Tooltip
  public String broker = "localhost";
  @ConfigEntry.Gui.Tooltip
  public int port = 1883;
  @ConfigEntry.Gui.Tooltip
  public String mainTopic = "minecraft";
}
