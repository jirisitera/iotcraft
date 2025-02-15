package japi.iotcraft.config;

import io.wispforest.owo.config.annotation.Modmenu;
import japi.iotcraft.Iotcraft;

@Modmenu(modId = Iotcraft.MOD_ID)
@io.wispforest.owo.config.annotation.Config(name = "iotcraft", wrapperName = "IotcraftConfig")
public class IotcraftConfigModel {
  public String broker = "localhost";
  public int port = 1883;
  public String mainTopic = "minecraft";
}
