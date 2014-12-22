package tobast.electricaddons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tobast.electricaddons.containers.ContainerOutlet;
import tobast.electricaddons.tileentities.TileEntityOutlet;

public class GuiOutlet extends GuiContainer {
	TileEntityOutlet tileEntity;
	
	public GuiOutlet(InventoryPlayer playerInv, TileEntityOutlet tileEntity) {
		super(new ContainerOutlet(playerInv, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		fontRendererObj.drawString("Outlet", 8, 6, 4210752);
		fontRendererObj.drawString("Inventory", 8, ySize-96+2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.renderEngine.bindTexture(new ResourceLocation("electricaddons","textures/gui/GUIOutlet.png"));
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		double energy = tileEntity.getEnergyStored();
		double energyLevel = energy / tileEntity.getEnergyCapacity();
		if(energyLevel > 0.05) {
			this.drawTexturedModalRect(x + 18, y + 18, 176, 0, 9, 9);
		}
	}
}
