package japi.iotcraft.config;

import japi.iotcraft.Iotcraft;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = Iotcraft.MOD_ID)
public class ModConfig extends PartitioningSerializer.GlobalData {
  @ConfigEntry.Category("mqtt")
  @ConfigEntry.Gui.TransitiveObject
  public final Mqtt mqtt = new Mqtt();
}
