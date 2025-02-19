package japi.iotcraft;

import com.google.gson.Gson;
import japi.iotcraft.config.ModConfig;
import lombok.Getter;
import lombok.Setter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.network.ClientPlayerEntity;

public class Iotcraft implements ClientModInitializer {
  public static final String MOD_ID = "iotcraft";
  public static final Gson GSON = new Gson();
  @Getter
  @Setter
  private static ModConfig config;
  @Getter
  @Setter
  private static ClientPlayerEntity player;

  @Override
  public void onInitializeClient() {
    // register config
    AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
    Iotcraft.setConfig(AutoConfig.getConfigHolder(ModConfig.class).getConfig());
    // register mqtt client
    MqttManager.connect();
  }
}
