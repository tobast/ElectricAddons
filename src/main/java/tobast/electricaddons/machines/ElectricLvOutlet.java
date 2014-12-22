package tobast.electricaddons.machines;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class ElectricLvOutlet extends ElectricOutlet {
	public ElectricLvOutlet(Material mat) {
		super(mat, 1);
		setBlockName("tinElectricOutlet");
	}

	@SideOnly(Side.CLIENT)
	public static IIcon topBottIcon;
	@SideOnly(Side.CLIENT)
	public static IIcon sideIcon;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister icon) {
		topBottIcon = icon.registerIcon("electricaddons:outlet_topbottom");
		sideIcon = icon.registerIcon("electricaddons:outlet_tier1");
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
