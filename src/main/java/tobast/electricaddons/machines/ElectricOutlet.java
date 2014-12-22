package tobast.electricaddons.machines;

import java.security.InvalidParameterException;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tobast.electricaddons.ElectricAddons;
import tobast.electricaddons.gui.GuiHandler;
import tobast.electricaddons.tileentities.TileEntityHvOutlet;
import tobast.electricaddons.tileentities.TileEntityLvOutlet;
import tobast.electricaddons.tileentities.TileEntityMvOutlet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ElectricOutlet extends BlockContainer {
	private final int tier;
	public ElectricOutlet(Material mat, int tier) {
		super(mat);
		this.tier = tier;
		
		if(tier <= 0 || tier > 3) { //invalid tier
			throw new InvalidParameterException("Tier must be in range 1-3 inclusive.");
		}
		
		setHardness(5);
		setStepSound(Block.soundTypeMetal);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(tier) {
		case 1:
			return new TileEntityLvOutlet();
		case 2:
			return new TileEntityMvOutlet();
		case 3:
			return new TileEntityHvOutlet();
		default:
			throw new InvalidParameterException("Tier must be in range 1-3 inclusive!");
		}
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(player.isSneaking())
			return false;
		player.openGui(ElectricAddons.instance, GuiHandler.GuiId.OUTLET, world, x, y, z);
		return true;
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		dropItems(world, x,y,z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	private void dropItems(World world, int x, int y, int z) {
		Random rand = new Random();

        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof IInventory)) {
                return;
        }
        IInventory inventory = (IInventory) tileEntity;

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack item = inventory.getStackInSlot(i);

            if (item != null && item.stackSize > 0) {
                float rx = rand.nextFloat() * 0.8F + 0.1F;
                float ry = rand.nextFloat() * 0.8F + 0.1F;
                float rz = rand.nextFloat() * 0.8F + 0.1F;

                EntityItem entityItem = new EntityItem(world,
                                x + rx, y + ry, z + rz,
                                new ItemStack(item.getItem(), item.stackSize, item.getItemDamage()));

                if (item.hasTagCompound()) {
                        entityItem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                }

                float factor = 0.05F;
                entityItem.motionX = rand.nextGaussian() * factor;
                entityItem.motionY = rand.nextGaussian() * factor + 0.2F;
                entityItem.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entityItem);
                item.stackSize = 0;
            }
        }
	}
}
