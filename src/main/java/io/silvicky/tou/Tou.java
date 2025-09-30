package io.silvicky.tou;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
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
            for(ServerPlayerEntity player:id.getPlayers())
            {
                if((!player.isSpectator())&&isOutOfWorld(player)&&hasTotem(player))
                {
                    TeleportTarget target=new TeleportTarget(
                            player.getEntityWorld(),
                            new Vec3d(player.getX(),
                                    player.getEntityWorld().getTopYInclusive(),
                                    player.getZ()),new Vec3d(0,0,0),0,0,TeleportTarget.NO_OP);
                    player.teleportTo(target);
                    player.clearStatusEffects();
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING,1200,0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE,800,0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,900,0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION,100,0));
                    player.getEntityWorld().spawnParticles(ParticleTypes.TOTEM_OF_UNDYING,player.getX(),player.getY(),player.getZ(),1,0,0,0,0);
                    if(player.getInventory().getStack(player.getInventory().getSelectedSlot()).getItem()==Items.TOTEM_OF_UNDYING)
                        player.getInventory().setStack(player.getInventory().getSelectedSlot(),ItemStack.EMPTY);
                    else player.getInventory().setStack(PlayerInventory.OFF_HAND_SLOT,ItemStack.EMPTY);
                }
            }
        });
    }
    public boolean hasTotem(ServerPlayerEntity player)
    {
        return player.getInventory().getStack(player.getInventory().getSelectedSlot()).getItem()==Items.TOTEM_OF_UNDYING
                ||player.getInventory().getStack(PlayerInventory.OFF_HAND_SLOT).getItem()==Items.TOTEM_OF_UNDYING;
    }
    public boolean isOutOfWorld(ServerPlayerEntity player)
    {
        return player.getEntityWorld().getBottomY()-player.getY()>32;
    }
}
