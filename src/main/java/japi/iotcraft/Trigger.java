package japi.iotcraft;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.world.LightType;

public final class Trigger {
  private Trigger() {
  }

  public static void listenToClientEvents() {
    var playerUsernameUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/profile", "username", "uuid"
    );
    var playerHealthUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/health", "health"
    );
    var playerHungerUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/hunger", "hunger"
    );
    var playerArmorUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/armor", "armor"
    );
    var playerExperienceUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/experience", "experience"
    );
    var playerItemMainHandUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/inventory/main_hand", "mainHand"
    );
    var playerItemOffHandUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/inventory/off_hand", "offHand"
    );
    var playerIsSleepingUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/condition/is_sleeping", "isSleeping"
    );
    var playerIsOnFireUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/condition/is_on_fire", "isOnFire"
    );
    var playerBlockUnderUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/player/other/block_under", "blockUnder"
    );
    var worldDimensionUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/world/environment/dimension", "dimension"
    );
    var worldLightLevelBlockUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/world/environment/light_level_block", "lightLevel"
    );
    var worldLightLevelSkyUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/world/environment/light_level_sky", "lightLevel"
    );
    var worldBiomeUpdate = new TriggerUpdate(
      Iotcraft.getConfig().mqtt.mainTopic + "/trigger/world/biome", "name", "type"
    );
    ClientTickEvents.START_WORLD_TICK.register((ClientWorld world) -> {
      if (Iotcraft.getPlayer() != null) {
        return;
      }
      Iotcraft.setPlayer(MinecraftClient.getInstance().player);

      String playerUsername = Iotcraft.getPlayer().getGameProfile().getName();
      var playerUUID = Iotcraft.getPlayer().getUuidAsString();

      float playerHealth = Iotcraft.getPlayer().getHealth();
      int playerHunger = Iotcraft.getPlayer().getHungerManager().getFoodLevel();
      int playerArmor = Iotcraft.getPlayer().getArmor();
      int playerExperience = Iotcraft.getPlayer().experienceLevel;

      var playerItemMainHand = Iotcraft.getPlayer().getStackInHand(Hand.MAIN_HAND).getItem().toString();
      var playerItemOffHand = Iotcraft.getPlayer().getStackInHand(Hand.OFF_HAND).getItem().toString();

      boolean playerIsSleeping = Iotcraft.getPlayer().isSleeping();
      boolean playerIsOnFire = Iotcraft.getPlayer().isOnFire();

      var playerBlockUnder = world.getBlockState(
        Iotcraft.getPlayer().getBlockPos().down()
      ).getRegistryEntry().getKey().get().getValue().toString();
      playerHealthUpdate.update(Float.toString(playerHealth));
      playerHungerUpdate.update(Integer.toString(playerHunger));
      playerArmorUpdate.update(Integer.toString(playerArmor));
      playerExperienceUpdate.update(Integer.toString(playerExperience));

      playerItemMainHandUpdate.update(playerItemMainHand);
      playerItemOffHandUpdate.update(playerItemOffHand);

      playerUsernameUpdate.update(playerUsername, playerUUID);

      playerIsSleepingUpdate.update(Boolean.toString(playerIsSleeping));
      playerIsOnFireUpdate.update(Boolean.toString(playerIsOnFire));

      playerBlockUnderUpdate.update(playerBlockUnder);

      var worldDimension = world.getRegistryKey().getValue().toString();
      int worldLightLevelBlock = world.getLightLevel(LightType.BLOCK, Iotcraft.getPlayer().getBlockPos());
      int worldLightLevelSky = world.getLightLevel(LightType.SKY, Iotcraft.getPlayer().getBlockPos());
      var worldBiome = world.getBiome(Iotcraft.getPlayer().getBlockPos()).getKey().get().getValue().toString();

      worldDimensionUpdate.update(worldDimension);

      worldLightLevelBlockUpdate.update(Integer.toString(worldLightLevelBlock));
      worldLightLevelSkyUpdate.update(Integer.toString(worldLightLevelSky));

      worldBiomeUpdate.update(worldBiome);
    });
  }
}
