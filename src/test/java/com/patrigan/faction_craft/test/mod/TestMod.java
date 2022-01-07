package com.patrigan.faction_craft.test.mod;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Meant for gameplay testing & example implementations.
 *
 * @note This test mod is only available when you run runTestClient/runTestServer.
 */
@Mod(TestMod.MOD_ID)
public class TestMod {

	public static final String MOD_ID = "test_faction_craft";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final List<Supplier<List<ItemStack>>> ITEM_GROUP_ITEM_SUPPLIERS = new ArrayList<>();
	public static final ItemGroup ITEM_GROUP = new ItemGroup(MOD_ID) {
		@OnlyIn(Dist.CLIENT)
		public ItemStack makeIcon() {
			return new ItemStack(Items.RED_BANNER);
		}

		@Override
		public void fillItemList(@Nonnull NonNullList<ItemStack> items) {
			super.fillItemList(items);
			for (Supplier<List<ItemStack>> supplier : ITEM_GROUP_ITEM_SUPPLIERS) {
				items.addAll(supplier.get());
			}
		}
	};

	public TestMod() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
	}

	private void setup(final FMLCommonSetupEvent event) {

	}

}
