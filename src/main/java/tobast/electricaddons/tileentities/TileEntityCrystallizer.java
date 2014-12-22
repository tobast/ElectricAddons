package tobast.electricaddons.tileentities;

import tobast.electricaddons.ElectricAddons;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCrystallizer extends TileEntity implements IInventory {
	protected ItemStack inv[];
	protected short processProg = 0;
	protected BasicSink sink = new BasicSink(this, 2048, 2);
	
	private final int PROCESS_DELAY = ElectricAddons.ConfOptions.crystallizerDelay;
	private final int PROCESS_CONSUMPTION = ElectricAddons.ConfOptions.crystallizerEUConsumption;
	public static final class Slots {
		public static final int INPUT = 0;
		public static final int ENERGY_INPUT = 1;
		public static final int OUTPUT = 2;
	}
	
	public TileEntityCrystallizer() {
		inv = new ItemStack[3];
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
		tag.setShort("processProg", processProg);
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
		processProg = tag.getShort("processProg");
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
		return "electricaddons.crystallizer";
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
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if(itemstack == null)
			return true;
		
		switch(slot) {
		case 0: // Input
			if(itemstack.getItem() == ic2.api.item.IC2Items.getItem("energiumDust").getItem())
				return true;
			return false;
		case 1: // Energy input
			if(itemstack.getItem() instanceof IElectricItem) {
				IElectricItem elecItem =  (IElectricItem)(itemstack.getItem());
				if(elecItem.canProvideEnergy(itemstack) && elecItem.getTier(itemstack) <= sink.getTier())
					return true;
			}
			return false;
		case 2: // Output
			return false;
		default:
			return false;
		}
	}
	
	private short ticksToUpdate=10;
	@Override
	public void updateEntity() {
		ticksToUpdate--;
		if(ticksToUpdate < 0) {
			ticksToUpdate = 10;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		sink.updateEntity();
		
		fillEnergyFromInput();
		advanceProcess();
	}
	
	private void fillEnergyFromInput() {
		ItemStack energyStack = getStackInSlot(Slots.ENERGY_INPUT);
		if(sink.getEnergyStored() < sink.getCapacity() &&
				energyStack != null && energyStack.getItem() instanceof IElectricItem) {
			IElectricItem powerSource = (IElectricItem)(energyStack.getItem());
			if(powerSource.canProvideEnergy(energyStack)) {
				int toTransfer = (int) Math.min(powerSource.getTransferLimit(energyStack), (int) (sink.getCapacity() - sink.getEnergyStored()));
				if(toTransfer > 0) {
					double energyRetrieved = ic2.api.item.ElectricItem.manager.discharge(energyStack, toTransfer, 1, false, true, false);
					sink.injectEnergy(ForgeDirection.UNKNOWN, energyRetrieved, 42); //FIXME what's voltage?!
				}
			}
		}
	}
	
	private void setTurnedOn(boolean state) {
		if(!worldObj.isRemote) {
			int oldMeta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			if(state == true && ((oldMeta & 0x01) == 0)) {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, oldMeta | 0x01, 0x01 | 0x02);
			}
			else if(state == false && (oldMeta & 0x01) != 0) {
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, oldMeta & (~0x01), 0x01 | 0x02);
			}
		}
	}
	
	private void advanceProcess() {
		ItemStack inputStack = getStackInSlot(Slots.INPUT);
		ItemStack outputStack = getStackInSlot(Slots.OUTPUT);
		
		if(inputStack != null
				&& inputStack.getItem() == ic2.api.item.IC2Items.getItem("energiumDust").getItem()
				&& inputStack.stackSize >= 3
				&& (outputStack == null || outputStack.stackSize < outputStack.getMaxStackSize()
					&& outputStack.getItem() == ic2.api.item.IC2Items.getItem("energyCrystal").getItem())) {
			if(sink.useEnergy(PROCESS_CONSUMPTION)) {
				// Can be processed into a crystal
				setTurnedOn(true);
				processProg++;
				
				if(processProg >= PROCESS_DELAY) { // Process ended
					processProg = 0;
					if(outputStack == null) {
						outputStack = ic2.api.item.IC2Items.getItem("energyCrystal");
						outputStack.setItemDamage(0);
						outputStack.stackSize = 1;
						this.decrStackSize(0, 3);
					}
					else
						outputStack.stackSize++;
					setInventorySlotContents(Slots.OUTPUT, outputStack);
				}
			}
			else
				setTurnedOn(false);
		}
		else {
			processProg = 0;
			setTurnedOn(false);
		}
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
	public int getProcessScaled(int scale) {
		return (int)((double)processProg / (double)PROCESS_DELAY * scale);
	}
}
