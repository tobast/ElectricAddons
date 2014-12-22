package tobast.electricaddons;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import tobast.electricaddons.gui.GuiHandler;
import tobast.electricaddons.machines.Crystallizer;
import tobast.electricaddons.machines.ElectricHvOutlet;
import tobast.electricaddons.machines.ElectricLvOutlet;
import tobast.electricaddons.machines.ElectricMvOutlet;
import tobast.electricaddons.tileentities.TileEntityCrystallizer;
import tobast.electricaddons.tileentities.TileEntityHvOutlet;
import tobast.electricaddons.tileentities.TileEntityLvOutlet;
import tobast.electricaddons.tileentities.TileEntityMvOutlet;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid="ElectricAddons", name="ElectricAddons", version="0.0.2", dependencies="required-after:IC2")
//@NetworkMod(clientSideRequired=true)
public class ElectricAddons {
	public static Block crystallizer, tinOutlet, copperOutlet, goldOutlet;
	
	@Instance(value="ElectricAddons")
	public static ElectricAddons instance;
	
	@SidedProxy(clientSide="tobast.electricaddons.client.ClientProxy", serverSide="tobast.electricaddons.CommonProxy")
	public static CommonProxy proxy;
	
	public static class ConfOptions {
		// BLOCK IDS - ##DEPREC##
//		public static int blockCrystallizerId=3148, blockTinOutletId=3149, blockCopperOutletId=3150, blockGoldOutletId=3151;
		
		// CRYSTALLIZER CONFIG
		public static int crystallizerEUConsumption=100, crystallizerDelay=20*90;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		Configuration conf = new Configuration(evt.getSuggestedConfigurationFile());
		conf.load();
		
		/*
		ElectricAddons.ConfOptions.blockCrystallizerId =
				conf.getBlock("crystallizer", ElectricAddons.ConfOptions.blockCrystallizerId).getInt();
		ElectricAddons.ConfOptions.blockTinOutletId =
				conf.getBlock("LVOutlet", ElectricAddons.ConfOptions.blockTinOutletId).getInt();
		ElectricAddons.ConfOptions.blockCopperOutletId =
				conf.getBlock("MVOutlet", ElectricAddons.ConfOptions.blockCopperOutletId).getInt();
		ElectricAddons.ConfOptions.blockGoldOutletId =
				conf.getBlock("HVOutlet", ElectricAddons.ConfOptions.blockGoldOutletId).getInt();
		*/
		ElectricAddons.ConfOptions.crystallizerDelay =
				conf.get("crystallizer", "delay", ElectricAddons.ConfOptions.crystallizerDelay).getInt();
		ElectricAddons.ConfOptions.crystallizerEUConsumption =
				conf.get("crystallizer", "EU_consumption", ElectricAddons.ConfOptions.crystallizerEUConsumption).getInt();
		
		conf.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		// Registering GUI Handler
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		// Registering Tile Entities
		GameRegistry.registerTileEntity(TileEntityCrystallizer.class, "Crystallizer");
		GameRegistry.registerTileEntity(TileEntityLvOutlet.class, "LV Outlet");
		GameRegistry.registerTileEntity(TileEntityMvOutlet.class, "MV Outlet");
		GameRegistry.registerTileEntity(TileEntityHvOutlet.class, "HV Outlet");
		
		// --- CRYSTALLIZER ---
		crystallizer = new Crystallizer(Material.iron);
		GameRegistry.registerBlock(crystallizer, "Crystallizer");
		
		GameRegistry.addShapedRecipe(new ItemStack(crystallizer), " e ", "cxc", "rmr",
				'r', new ItemStack(Items.redstone),
				'e', ic2.api.item.IC2Items.getItem("electronicCircuit"),
				'c', ic2.api.item.IC2Items.getItem("insulatedCopperCableItem"),
				'x', new ItemStack(ic2.api.item.IC2Items.getItem("energyCrystal").getItem(), 1, OreDictionary.WILDCARD_VALUE),
				'm', ic2.api.item.IC2Items.getItem("machine"));
		
		// --- OUTLETS ---
		tinOutlet = new ElectricLvOutlet(Material.iron);
		GameRegistry.registerBlock(tinOutlet, "Tin Outlet");
		GameRegistry.addShapedRecipe(new ItemStack(tinOutlet,1), "c c", " t ", " c ",
				'c', ic2.api.item.IC2Items.getItem("tinCableItem"),
				't', ic2.api.item.IC2Items.getItem("lvTransformer"));

		copperOutlet = new ElectricMvOutlet(Material.iron);
		GameRegistry.registerBlock(copperOutlet, "Copper Outlet");
		GameRegistry.addShapedRecipe(new ItemStack(copperOutlet,1), "c c", " t ", " c ",
				'c', ic2.api.item.IC2Items.getItem("copperCableItem"),
				't', ic2.api.item.IC2Items.getItem("mvTransformer"));

		goldOutlet = new ElectricHvOutlet(Material.iron);
		GameRegistry.registerBlock(goldOutlet, "Gold Outlet");
		GameRegistry.addShapedRecipe(new ItemStack(goldOutlet,1), "c c", " t ", " c ",
				'c', ic2.api.item.IC2Items.getItem("goldCableItem"),
				't', ic2.api.item.IC2Items.getItem("hvTransformer"));
	}
	
	@EventHandler
	public void load(FMLInitializationEvent evt) {
		proxy.registerRenderers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent evt) {}
}
