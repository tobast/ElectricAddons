package tobast.electricaddons.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tobast.electricaddons.tileentities.TileEntityCrystallizer;

public class ContainerCrystallizer extends Container {
	protected TileEntityCrystallizer tileEntity;
	
	public ContainerCrystallizer(InventoryPlayer playerInv, TileEntityCrystallizer tileEntity) {
		this.tileEntity = tileEntity;
		
		// Machine slots
		addSlotToContainer(new FilteredSlot(tileEntity, 0, 56,17));
		addSlotToContainer(new FilteredSlot(tileEntity, 1, 56, 53));
		addSlotToContainer(new FilteredSlot(tileEntity, 2, 116, 35));

		bindPlayerInventory(playerInv);
	}
	
	protected void bindPlayerInventory(InventoryPlayer inv) {
		// Inventory
		for(int row=0; row < 3; row++) {
			for(int col=0; col < 9; col++) {
				addSlotToContainer(new Slot(inv, col + row*9 + 9, 8 + col*18, 84+row*18));
			}
		}
		// Hotbar
		for(int col=0; col < 9; col++) {
			addSlotToContainer(new Slot(inv, col, 8 + 18 * col, 142));
		}
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotId);
		
		if(slot != null && slot.getHasStack()) {
			ItemStack stackInSlot = slot.getStack();
			stack = stackInSlot.copy();
			
			if(slotId < 3) { // Crystallizer's slot, try to merge into inventory
				if(!this.mergeItemStack(stackInSlot, 3, 39, true))
					return null;
			}
			else if(slotId >= 3) { // Inventory slot : move the item wisely!
				if(tileEntity.isItemValidForSlot(0, stackInSlot)) {
					if(!this.mergeItemStack(stackInSlot, 0, 1, true))
						return null;
				}
				else if(tileEntity.isItemValidForSlot(1, stackInSlot)) {
					if(!this.mergeItemStack(stackInSlot, 1, 2, true))
						return null;
				}
			}
			
			if(stackInSlot.stackSize == 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();
			
			if(stackInSlot.stackSize == stack.stackSize) // nothing has changed
				return null;
			slot.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}
