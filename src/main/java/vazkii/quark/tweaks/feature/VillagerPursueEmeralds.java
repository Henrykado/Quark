package vazkii.quark.tweaks.feature;

import com.google.common.collect.Sets;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.tweaks.ai.EntityAITemptFixed;
import vazkii.quark.world.entity.EntityArchaeologist;

public class VillagerPursueEmeralds extends Feature {

	@SubscribeEvent
	public void onEntityTick(LivingUpdateEvent event) {
		if(event.getEntity() instanceof EntityArchaeologist) {
			EntityArchaeologist villager = (EntityArchaeologist) event.getEntity();
			for(EntityAITaskEntry task : villager.tasks.taskEntries)
				if(task.action instanceof EntityAITempt)
					return;

			villager.tasks.addTask(2, new EntityAITemptFixed(villager, 0.6, false,
					Sets.newHashSet(Item.getItemFromBlock(Blocks.EMERALD_BLOCK),
							Item.getItemFromBlock(Blocks.BONE_BLOCK))));
		} else if(event.getEntity() instanceof IMerchant && event.getEntity() instanceof EntityLiving) {
			EntityLiving merchant = (EntityLiving) event.getEntity();
			for(EntityAITaskEntry task : merchant.tasks.taskEntries)
				if(task.action instanceof EntityAITempt)
					return;
					
			merchant.tasks.addTask(2, new EntityAITemptFixed(merchant, 0.6, Item.getItemFromBlock(Blocks.EMERALD_BLOCK), false));
		}
	}
	
	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}
	
	@Override
	public boolean hasSubscriptions() {
		return true;
	}
	
	@Override
	public String getFeatureIngameConfigName() {
		return "Villagers Pursue Emeralds";
	}
	
}
