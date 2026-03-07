package io.silvicky.tou;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tou implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    public static final String MOD_ID = "TouVoid";
    public static final Logger LOGGER = LoggerFactory.getLogger("tou-void");
    @Override
    public void onInitialize() {
        LOGGER.info("Loading TouVoid...");
        ServerTickEvents.START_WORLD_TICK.register((id)->
        {
            for(ServerPlayer player:id.players())
            {
                if((!player.isSpectator())&&isOutOfWorld(player)&&hasTotem(player))
                {
                    TeleportTransition target=new TeleportTransition(
                            player.level(),
                            new Vec3(player.getX(),
                                    player.level().getMaxY(),
                                    player.getZ()),new Vec3(0,0,0),0,0, TeleportTransition.DO_NOTHING);
                    player.teleport(target);
                    player.removeAllEffects();
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,1200,0));
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,800,0));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,900,0));
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,100,0));
                    player.level().sendParticles(ParticleTypes.TOTEM_OF_UNDYING,player.getX(),player.getY(),player.getZ(),1,0,0,0,0);
                    if(player.getInventory().getItem(player.getInventory().getSelectedSlot()).getItem()== Items.TOTEM_OF_UNDYING)
                        player.getInventory().setItem(player.getInventory().getSelectedSlot(), ItemStack.EMPTY);
                    else player.getInventory().setItem(Inventory.SLOT_OFFHAND, ItemStack.EMPTY);
                }
            }
        });
    }
    public boolean hasTotem(ServerPlayer player)
    {
        return player.getInventory().getItem(player.getInventory().getSelectedSlot()).getItem()== Items.TOTEM_OF_UNDYING
                ||player.getInventory().getItem(Inventory.SLOT_OFFHAND).getItem()== Items.TOTEM_OF_UNDYING;
    }
    public boolean isOutOfWorld(ServerPlayer player)
    {
        return player.level().getMinY()-player.getY()>32;
    }
}
