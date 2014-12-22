package tobast.electricaddons.containers;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FilteredSlot extends Slot {
	IInventory inventory = null;
	public FilteredSlot(IInventory inventory, int slotId, int xDisp, int yDisp) {
		super(inventory, slotId, xDisp, yDisp);
		this.inventory = inventory;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return inventory.isItemValidForSlot(this.slotNumber, stack);
	}
	
}
