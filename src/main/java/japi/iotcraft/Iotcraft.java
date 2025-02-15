package japi.iotcraft;

import japi.iotcraft.config.IotcraftConfig;
import net.fabricmc.api.ModInitializer;

public class Iotcraft implements ModInitializer {
  public static final String MOD_ID = "iotcraft";
  public static final IotcraftConfig CONFIG = IotcraftConfig.createAndLoad();

  @Override
  public void onInitialize() {
  }
}
