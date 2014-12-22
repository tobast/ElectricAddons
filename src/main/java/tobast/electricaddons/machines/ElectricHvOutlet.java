package tobast.electricaddons.machines;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ElectricHvOutlet extends ElectricOutlet {
	public ElectricHvOutlet(Material mat) {
		super(mat, 3);
		setBlockName("goldElectricOutlet");
	}
	
	@SideOnly(Side.CLIENT)
	public static IIcon topBottIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		topBottIcon = icon.registerIcon("electricaddons:outlet_topbottom");
		sideIcon = icon.registerIcon("electricaddons:outlet_tier3");
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(side == 0 || side == 1) // top/bottom
			return topBottIcon;
		else
			return sideIcon;
	}
}
