/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [27/03/2016, 21:55:50 (GMT)]
 */
package vazkii.quark.tweaks.feature;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.util.ItemMetaHelper;

public class QuickArmorSwapping extends Feature {

	public static boolean offhandSwapping;
	private static String[] tempBlacklist;
	public static Set<Pair<Item, Integer>> armorBlacklist;

	@Override
	public void setupConfig() {
		offhandSwapping = loadPropBool("Swap off-hand with armor", "", true);
		tempBlacklist = loadPropStringList("Armor Blacklist", "Armor that should be prevented from being quick-swapped\n" + "Format is modid:item[:meta]", new String[] {});
	}

	@Override
	public void postPreInit() {
		armorBlacklist = ItemMetaHelper.getFromStringArray("armor swapping blacklist item", tempBlacklist).stream()
			.filter(i -> !i.isEmpty())
			.map(s -> Pair.of(s.getItem(), s.getMetadata()))
			.collect(Collectors.toSet());
	}

	@SubscribeEvent
	public void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
		Entity target = event.getTarget();
		EntityPlayer player = event.getEntityPlayer();

		if (target == null || player == null)
			return;

		if (target.world.isRemote || player.isSpectator() || player.isCreative() || !(target instanceof EntityArmorStand))
			return;

		if (player.isSneaking()) {
			event.setCanceled(true);
			EntityArmorStand armorStand = (EntityArmorStand) event.getTarget();

			swapSlot(player, armorStand, EntityEquipmentSlot.HEAD);
			swapSlot(player, armorStand, EntityEquipmentSlot.CHEST);
			swapSlot(player, armorStand, EntityEquipmentSlot.LEGS);
			swapSlot(player, armorStand, EntityEquipmentSlot.FEET);
			if (offhandSwapping)
				swapSlot(player, armorStand, EntityEquipmentSlot.OFFHAND);
		}
	}

	private void swapSlot(EntityPlayer player, EntityArmorStand armorStand, EntityEquipmentSlot slot) {
		ItemStack playerItem = player.getItemStackFromSlot(slot);
		if (armorBlacklist.contains(Pair.of(playerItem.getItem(), playerItem.getMetadata())))
			return;
		ItemStack armorStandItem = armorStand.getItemStackFromSlot(slot);
		if (armorBlacklist.contains(Pair.of(armorStandItem.getItem(), armorStandItem.getMetadata())))
			return;
		player.setItemStackToSlot(slot, armorStandItem);
		armorStand.setItemStackToSlot(slot, playerItem);
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}

	@Override
	public String[] getIncompatibleMods() {
		return new String[] { "iberia" };
	}
}
