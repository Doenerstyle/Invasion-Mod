package invmod.common.entity;

import invmod.common.INotifyTask;
import invmod.common.mod_Invasion;
import invmod.common.entity.ai.EntityAIAttackNexus;
import invmod.common.entity.ai.EntityAIGoToNexus;
import invmod.common.entity.ai.EntityAIRandomBoulder;
import invmod.common.entity.ai.EntityAISimpleTarget;
import invmod.common.entity.ai.EntityAIThrowerKillEntity;
import invmod.common.entity.ai.EntityAIWanderIM;
import invmod.common.nexus.INexusAccess;
import invmod.common.util.CoordsInt;
import invmod.common.util.IPosition;
import net.minecraft.block.Block;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityIMThrower extends EntityIMMob 
{
	private int throwTime;
	private int punchTimer;
	private boolean clearingPoint;
	private IPosition pointToClear;
	private INotifyTask clearPointNotifee;
	private int tier;
	private byte metaChanged;
	
	public EntityIMThrower(World world) 
	{
		this(world, null);
	}

	public EntityIMThrower(World world, INexusAccess nexus) 
	{
		super(world, nexus);
		
		setBaseMoveSpeedStat(0.13F);
		this.attackStrength = 10;
		this.selfDamage = 0;
		this.maxSelfDamage = 0;
		this.experienceValue = 20;
		this.clearingPoint = false;
		this.tier=1;
		setMaxHealthAndHealth(mod_Invasion.getMobHealth(this));
		setName("Thrower");
		setDestructiveness(2);
		setSize(1.8F, 1.95F);
		setAI();
		
		DataWatcher dataWatcher = getDataWatcher();
		dataWatcher.addObject(29, Byte.valueOf(this.metaChanged));
		dataWatcher.addObject(30, Integer.valueOf(this.tier));
		dataWatcher.addObject(31, Integer.valueOf(1));
		
	}

	protected void setAI() {
		this.tasks = new EntityAITasks(this.worldObj.theProfiler);
		this.tasks.addTask(0, new EntityAISwimming(this));
		if(this.getTier()==1)
		{
		this.tasks.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayer.class, 55, 60.0F, 1.0F));
		this.tasks.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayerMP.class, 55, 60.0F, 1.0F));
		}else{
		this.tasks.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayer.class, 60, 90.0F, 1.5F));
		this.tasks.addTask(1, new EntityAIThrowerKillEntity(this, EntityPlayerMP.class, 60, 90.0F, 1.5F));
		}
		this.tasks.addTask(2, new EntityAIAttackNexus(this));
		this.tasks.addTask(3, new EntityAIRandomBoulder(this, 3));
		this.tasks.addTask(4, new EntityAIGoToNexus(this));
		this.tasks.addTask(7, new EntityAIWanderIM(this));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityIMCreeper.class, 12.0F));
		this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
		this.tasks.addTask(10, new EntityAILookIdle(this));

		this.targetTasks = new EntityAITasks(this.worldObj.theProfiler);
		this.targetTasks.addTask(1, new EntityAISimpleTarget(this, EntityPlayer.class, this.getSenseRange(), false));
		this.targetTasks.addTask(2, new EntityAISimpleTarget(this, EntityPlayer.class, this.getAggroRange(), true));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
	}
@Override
	public void onUpdate(){
		super.onUpdate();
		if ((this.worldObj.isRemote) && (this.metaChanged !=Byte.valueOf((byte)getDataWatcher().getWatchableObjectByte(29)))) {
			DataWatcher data = getDataWatcher();
			this.metaChanged = data.getWatchableObjectByte(29);
			setTexture(data.getWatchableObjectInt(31));

			if (this.tier != data.getWatchableObjectInt(30))
			{
				setTier(data.getWatchableObjectInt(30));
			}
		}
	}
	@Override
	public void updateAITick() {
		super.updateAITick();
		this.throwTime -= 1;
		if (this.clearingPoint) {
			if (clearPoint()) {
				this.clearingPoint = false;
				if (this.clearPointNotifee != null)
					this.clearPointNotifee.notifyTask(0);
			}
		}
	}

	@Override
	public void knockBack(Entity par1Entity, float par2, double par3, double par5) 
	{
		if (this.tier == 2) 
		{
			return;
		}
		this.isAirBorne = true;
		float f = MathHelper.sqrt_double(par3 * par3 + par5 * par5);
		float f1 = 0.2F;
		this.motionX /= 2.0D;
		this.motionY /= 2.0D;
		this.motionZ /= 2.0D;
		this.motionX -= par3 / f * f1;
		this.motionY += f1;
		this.motionZ -= par5 / f * f1;

		if (this.motionY > 0.4000000059604645D) 
		{
			this.motionY = 0.4000000059604645D;
		}
	}
	
	@Override
	public boolean isAIEnabled() {
		return true;
	}

	public boolean canThrow() {
		return this.throwTime <= 0;
	}

	@Override
	public boolean onPathBlocked(Path path, INotifyTask notifee) {
		if (!path.isFinished()) {
			PathNode node = path.getPathPointFromIndex(path.getCurrentPathIndex());
			this.clearingPoint = true;
			this.clearPointNotifee = notifee;
			this.pointToClear = new CoordsInt(node.xCoord, node.yCoord, node.zCoord);
			return true;
		}
		return false;
	}
	
	public void setTier(int tier) {
		this.tier = tier;
		getDataWatcher().updateObject(30, Integer.valueOf(tier));
		this.selfDamage = 0;
		this.maxSelfDamage = 0;
		this.clearingPoint = false;
		if (tier == 1) {
			setBaseMoveSpeedStat(0.13F);
			this.attackStrength = 10;
			this.experienceValue = 20;
			setMaxHealthAndHealth(mod_Invasion.getMobHealth(this));
			setName("Thrower");
			setDestructiveness(2);
			setSize(1.8F, 1.95F);
			setAI();
			
		} else if (tier == 2) {
			setBaseMoveSpeedStat(0.23F);
			this.attackStrength = 15;
			this.experienceValue = 25;
			setMaxHealthAndHealth(mod_Invasion.getMobHealth(this));
			setName("Big Thrower");
			setDestructiveness(4);
			setSize(2F, 2F);
			setAI();
		}
		
		if (getDataWatcher().getWatchableObjectInt(31) == 1) {
			if (tier == 1) {
				setTexture(1);
			} else if (tier == 2) {
				setTexture(2);
			}
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("tier", this.tier);
		super.writeEntityToNBT(nbttagcompound);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		setTexture(nbttagcompound.getInteger("tier"));
		this.tier = nbttagcompound.getInteger("tier");
		setTier(this.tier);
	}
	
	@Override
	public String getSpecies() {
		return "Zombie";
	}
	
	@Override
	public int getTier() {
		return this.tier;
	}

	@Override
	public int getGender() {
		return 1;
	}

	@Override
	protected String getLivingSound() {
		return "mob.zombie.say";
	}

	@Override
	protected String getHurtSound() {
		return "mob.zombie.hurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.zombie.death";
	}

	protected boolean clearPoint() {
		if (--this.punchTimer <= 0) {
			//this is a cheat, I should fix it where it get's the point to clear
			int x = this.pointToClear.getXCoord()+1;
			int y = this.pointToClear.getYCoord();
			int z = this.pointToClear.getZCoord();
			int mobX = MathHelper.floor_double(this.posX);
			int mobZ = MathHelper.floor_double(this.posZ);
			int xOffsetR = 0;
			int zOffsetR = 0;
			int axisX = 0;
			int axisZ = 0;

			float facing = this.rotationYaw % 360.0F;
			if (facing < 0.0F) {
				facing += 360.0F;
			}
			if ((facing >= 45.0F) && (facing < 135.0F)) {
				zOffsetR = -1;
				axisX = -1;
			} else if ((facing >= 135.0F) && (facing < 225.0F)) {
				xOffsetR = -1;
				axisZ = -1;
			} else if ((facing >= 225.0F) && (facing < 315.0F)) {
				zOffsetR = -1;
				axisX = 1;
			} else {
				xOffsetR = -1;
				axisZ = 1;
			}
			if (((this.worldObj.getBlock(x, y, z) != null) && (this.worldObj.getBlock(x, y, z).getMaterial().isSolid())) || ((this.worldObj.getBlock(x, y + 1, z) != null) && (this.worldObj.getBlock(x, y + 1, z).getMaterial().isSolid()))
					|| ((this.worldObj.getBlock(x + xOffsetR, y, z + zOffsetR) != null) && (this.worldObj.getBlock(x + xOffsetR, y, z + zOffsetR).getMaterial().isSolid())) || ((this.worldObj.getBlock(x + xOffsetR, y + 1, z + zOffsetR) != null) && (this.worldObj.getBlock(x + xOffsetR, y + 1, z + zOffsetR).getMaterial().isSolid()))) {
				tryDestroyBlock(x, y, z);
				tryDestroyBlock(x, y + 1, z);
				tryDestroyBlock(x + xOffsetR, y, z + zOffsetR);
				tryDestroyBlock(x + xOffsetR, y + 1, z + zOffsetR);
				this.punchTimer = 160;
			} else if (((this.worldObj.getBlock(x - axisX, y + 1, z - axisZ) != null) && (this.worldObj.getBlock(x - axisX, y + 1, z - axisZ).getMaterial().isSolid())) || ((this.worldObj.getBlock(x - axisX + xOffsetR, y + 1, z - axisZ + zOffsetR) != null) && (this.worldObj.getBlock(x - axisX + xOffsetR, y + 1, z - axisZ + zOffsetR).getMaterial().isSolid()))) {
				tryDestroyBlock(x - axisX, y + 1, z - axisZ);
				tryDestroyBlock(x - axisX + xOffsetR, y + 1, z - axisZ + zOffsetR);
				this.punchTimer = 160;
			} else if (((this.worldObj.getBlock(x - 2 * axisX, y + 1, z - 2 * axisZ) != null) && (this.worldObj.getBlock(x - 2 * axisX, y + 1, z - 2 * axisZ).getMaterial().isSolid())) || ((this.worldObj.getBlock(x - 2 * axisX + xOffsetR, y + 1, z - 2 * axisZ + zOffsetR) != null) && (this.worldObj.getBlock(x - 2 * axisX + xOffsetR, y + 1, z - 2 * axisZ + zOffsetR).getMaterial().isSolid()))) {
				tryDestroyBlock(x - 2 * axisX, y + 1, z - 2 * axisZ);
				tryDestroyBlock(x - 2 * axisX + xOffsetR, y + 1, z - 2 * axisZ + zOffsetR);
				this.punchTimer = 160;
			} else {
				return true;
			}
		}
		return false;
	}

	protected void tryDestroyBlock(int x, int y, int z) {
		Block block = this.worldObj.getBlock(x, y, z);
		//if ((block != null) && ((isNexusBound()) || (this.j != null))) {
		if ((block != null) || (this.j != null)) {
			if ((block == mod_Invasion.blockNexus) && (this.attackTime == 0) && (x == this.targetNexus.getXCoord()) && (y == this.targetNexus.getYCoord()) && (z == this.targetNexus.getZCoord())) {
				this.targetNexus.attackNexus(5);
				this.attackTime = 60;
			} else if (block != mod_Invasion.blockNexus) {
				int meta = this.worldObj.getBlockMetadata(x, y, z);
				this.worldObj.setBlock(x, y, z, Blocks.air);
				block.onBlockDestroyedByPlayer(this.worldObj, x, y, z, meta);
				
				if(mod_Invasion.getDestructedBlocksDrop())
				{
				block.dropBlockAsItem(this.worldObj, x, y, z, meta, 0);
				}
				if (this.throttled == 0) {
					this.worldObj.playSoundAtEntity(this, "random.explode", 1.0F, 0.4F);

					this.throttled = 5;
				}
			}
		}
	}

	
	@Override
	protected void attackEntity(Entity entity, float f) {
		if ((this.throwTime <= 0) && (f > 4.0F)) {
			this.throwTime = 120;
			//f is the throwdistance
			if (f < 50.0F) {
				throwBoulder(entity.posX, entity.posY + entity.getEyeHeight() - 0.7D, entity.posZ, false);			
			}
		} else {
			super.attackEntity(entity, f);
		}
	}

	protected void throwBoulder(double entityX, double entityY, double entityZ, boolean forced) {
		float launchSpeed = 1.0F;
		double dX = entityX - this.posX;
		double dZ = entityZ - this.posZ;
		double dXY = MathHelper.sqrt_double(dX * dX + dZ * dZ);

		if ((0.025D * dXY / (launchSpeed * launchSpeed) <= 1.0D) && (this.attackTime == 0)) {
			EntityIMBoulder entityBoulder = new EntityIMBoulder(this.worldObj, this, launchSpeed);
			double dY = entityY - entityBoulder.posY;
			double angle = 0.5D * Math.asin(0.025D * dXY / (launchSpeed * launchSpeed));
			dY += dXY * Math.tan(angle);
			entityBoulder.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
			this.worldObj.spawnEntityInWorld(entityBoulder);
		} else if (forced) {
			EntityIMBoulder entityBoulder = new EntityIMBoulder(this.worldObj, this, launchSpeed);
			double dY = entityY - entityBoulder.posY;
			dY += dXY * Math.tan(0.7853981633974483D);
			entityBoulder.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
			this.worldObj.spawnEntityInWorld(entityBoulder);
		}
		
	}

	public void throwBoulder(double entityX, double entityY, double entityZ) {
		this.throwTime = 40;
		float launchSpeed = 1.0F;
		double dX = entityX - this.posX;
		double dZ = entityZ - this.posZ;
		double dXY = MathHelper.sqrt_double(dX * dX + dZ * dZ);
		double p = 0.025D * dXY / (launchSpeed * launchSpeed);
		double angle;
		if (p <= 1.0D)
			angle = 0.5D * p;
		else {
			angle = 0.7853981633974483D;
		}
		EntityIMBoulder entityBoulder = new EntityIMBoulder(this.worldObj, this, launchSpeed);
		double dY = entityY - entityBoulder.posY;
		dY += dXY * Math.tan(angle);
		entityBoulder.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
		this.worldObj.spawnEntityInWorld(entityBoulder);
	}
	
	public void throwTNT(double entityX, double entityY, double entityZ) {
		this.throwTime = 40;
		float launchSpeed = 1.0F;
		double dX = entityX - this.posX;
		double dZ = entityZ - this.posZ;
		double dXY = MathHelper.sqrt_double(dX * dX + dZ * dZ);
		double p = 0.025D * dXY / (launchSpeed * launchSpeed);
		double angle;
		if (p <= 1.0D)
			angle = 0.5D * p;
		else {
			angle = 0.7853981633974483D;
		}
		EntityIMPrimedTNT entityTNT = new EntityIMPrimedTNT(this.worldObj, this, launchSpeed);
		double dY = entityY - entityTNT.posY;
		dY += dXY * Math.tan(angle);
		entityTNT.setBoulderHeading(dX, dY, dZ, launchSpeed, 0.05F);
		this.worldObj.spawnEntityInWorld(entityTNT);
	}

	@Override
	protected void dropFewItems(boolean flag, int bonus) {
		super.dropFewItems(flag, bonus);
		if (mod_Invasion.getMobsDropSmallRemnants()) {
			entityDropItem(new ItemStack(mod_Invasion.itemSmallRemnants, 1), 0.0F);
		}
	}
	
	@Override
	public String toString() 
	{
		return "IMThrower-T" + this.tier;
	}

	public void setTexture(int textureId) {
		getDataWatcher().updateObject(31, Integer.valueOf(textureId));
	}
	
	public int getTextureId() {
		return getDataWatcher().getWatchableObjectInt(31);
	}
}