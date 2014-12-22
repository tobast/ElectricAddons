package tobast.electricaddons.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tobast.electricaddons.containers.ContainerCrystallizer;
import tobast.electricaddons.tileentities.TileEntityCrystallizer;

public class GuiCrystallizer extends GuiContainer {
private TileEntityCrystallizer tileEntity;

	public GuiCrystallizer(InventoryPlayer playerInv, TileEntityCrystallizer tileEntity) {
		super(new ContainerCrystallizer(playerInv, tileEntity));
		this.tileEntity = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		fontRendererObj.drawString("Crystallizer", 8, 6, 4210752);
		fontRendererObj.drawString("Inventory", 8, ySize-96+2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p1, int p2) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.renderEngine.bindTexture(new ResourceLocation("electricaddons","textures/gui/GUICrystallizer.png"));
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		
		// Draw energy stored
		double energy = tileEntity.getEnergyStored();
		int energyLevel = (int) (energy / tileEntity.getEnergyCapacity() * 13);
		if(energyLevel > 0) {
			this.drawTexturedModalRect(x + 56, y + 36 + 13 - energyLevel, 176, 13-energyLevel, 14, 1 + energyLevel);
		}
		
		// Draw progress
		int prog = tileEntity.getProcessScaled(22);
		if(prog > 0) {
			this.drawTexturedModalRect(x+79, y+34, 176, 14, 1+prog, 17);
		}
	}
}
