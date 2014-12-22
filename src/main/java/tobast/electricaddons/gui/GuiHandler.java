package tobast.electricaddons.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tobast.electricaddons.containers.ContainerCrystallizer;
import tobast.electricaddons.containers.ContainerOutlet;
import tobast.electricaddons.tileentities.TileEntityCrystallizer;
import tobast.electricaddons.tileentities.TileEntityOutlet;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public static final class GuiId {
		public static final int CRYSTALLIZER = 0,
				OUTLET = 1;
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID == GuiId.CRYSTALLIZER) {
			TileEntity tileEntity = world.getTileEntity(x,y,z);
			if(tileEntity instanceof TileEntityCrystallizer) {
				return new ContainerCrystallizer(player.inventory, (TileEntityCrystallizer) tileEntity);
			}
		}
		else if(ID == GuiId.OUTLET) {
			TileEntity tileEntity = world.getTileEntity(x,y,z);
			if(tileEntity instanceof TileEntityOutlet)
				return new ContainerOutlet(player.inventory, (TileEntityOutlet) tileEntity);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		if(ID == GuiId.CRYSTALLIZER) {
			TileEntity tileEntity = world.getTileEntity(x,y,z);
			if(tileEntity instanceof TileEntityCrystallizer)
				return new GuiCrystallizer(player.inventory, (TileEntityCrystallizer) tileEntity);
		}
		else if(ID == GuiId.OUTLET) {
			TileEntity tileEntity = world.getTileEntity(x,y,z);
			if(tileEntity instanceof TileEntityOutlet)
				return new GuiOutlet(player.inventory, (TileEntityOutlet) tileEntity);
		}
		return null;
	}

}
