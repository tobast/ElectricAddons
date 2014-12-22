package tobast.electricaddons.tileentities;

import ic2.api.energy.prefab.BasicSink;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;

import java.security.InvalidParameterException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityOutlet extends TileEntity implements IInventory {
	protected ItemStack[] inv;
	protected BasicSink sink;
	
	protected int tier;
	
	public TileEntityOutlet(int tier) {
		this.tier = tier;
		inv = new ItemStack[1]; // Array of size 1 to be easily extended if needed later on (multi-slot sockets?)
		sink = new BasicSink(this, (int) (Math.pow(2, tier-1) * 1024), tier);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		sink.writeToNBT(tag);
		
		NBTTagList tagList = new NBTTagList();
		for(int i=0; i < inv.length; i++) {
			ItemStack stack = inv[i];
			if(stack != null) {
				NBTTagCompound slotTag = new NBTTagCompound();
				slotTag.setByte("slot", (byte) i);
				stack.writeToNBT(slotTag);
				tagList.appendTag(slotTag);
			}
		}
		tag.setTag("inventory", tagList);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		sink.readFromNBT(tag);
		
		NBTTagList tagList = tag.getTagList("inventory",Constants.NBT.TAG_COMPOUND);
		for(int i=0; i < tagList.tagCount(); i++) {
			NBTTagCompound slotTag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = slotTag.getByte("slot");
			if(slot >= 0 && slot < getSizeInventory())
				inv[slot] = ItemStack.loadItemStackFromNBT(slotTag);
		}
	}
	
	private short ticksToUpdate = 10;
	@Override
	public void updateEntity() {
		ticksToUpdate--;
		if(ticksToUpdate < 0) {
			ticksToUpdate = 10;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		sink.updateEntity();
		
		chargeItem();
	}
	
	private void chargeItem() {
		ItemStack itemStack = getStackInSlot(0);
		IElectricItemManager itemManager = ic2.api.item.ElectricItem.manager;
		
		if(itemStack != null && itemStack.getItem() instanceof IElectricItem) {
			IElectricItem elecItem = (IElectricItem) itemStack.getItem();
			if(elecItem.getTier(itemStack) <= this.tier) {
				double missingEU = elecItem.getMaxCharge(itemStack) -  itemManager.getCharge(itemStack);
				int toTransfer = (int) Math.min(elecItem.getTransferLimit(itemStack), Math.min(sink.getEnergyStored(), missingEU));
				
				if(sink.useEnergy(toTransfer)) {
					itemManager.charge(itemStack, toTransfer, this.tier, false, false);
				}
			}
		}
	}
	
	@Override
	public int getSizeInventory() {
		return inv.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if(i >= getSizeInventory() || i < 0)
			return null;
		return inv[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int decr) {
		ItemStack stack = getStackInSlot(slot);
		if(stack != null) {
			if(stack.stackSize <= decr)
				setInventorySlotContents(slot, null);
			else
				stack = stack.splitStack(decr);
		}
		return stack;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		ItemStack stack = getStackInSlot(i);
		if(stack != null)
			setInventorySlotContents(i, null);
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemstack) {
		if(slot < 0 || slot >= inv.length)
			return;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
			itemstack.stackSize = getInventoryStackLimit();
		inv[slot] = itemstack;
	}

	@Override
	public String getInventoryName() {
		return "electricaddons.outlet";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/// Utility function that raises a number to its square
	private double sq(double nb) {
		return nb*nb;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return (sq(player.posX - this.xCoord) + sq(player.posY - this.yCoord) + sq(player.posZ - this.zCoord) < 6*6);
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(itemstack.getItem() instanceof IElectricItem) {
			IElectricItem elecItem =  (IElectricItem)(itemstack.getItem());
			if(elecItem.getTier(itemstack) <= sink.getTier())
				return true;
		}
		return false;
	}
	
	@Override
	public void onDataPacket(NetworkManager mgr, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,tag);
	}
	
	@Override
	public void invalidate() {
		sink.invalidate();
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload() {
		sink.onChunkUnload();
	}
	
	public double getEnergyStored() {
		return sink.getEnergyStored();
	}
	public double getEnergyCapacity() {
		return sink.getCapacity();
	}
}
