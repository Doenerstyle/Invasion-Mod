package invmod.common;

import invmod.common.creativetab.CreativeTabInvmod;
import invmod.common.entity.EntityIMBird;
import invmod.common.entity.EntityIMBolt;
import invmod.common.entity.EntityIMBoulder;
import invmod.common.entity.EntityIMBurrower;
import invmod.common.entity.EntityIMCreeper;
import invmod.common.entity.EntityIMEgg;
import invmod.common.entity.EntityIMGiantBird;
import invmod.common.entity.EntityIMImp;
import invmod.common.entity.EntityIMLiving;
import invmod.common.entity.EntityIMPigEngy;
import invmod.common.entity.EntityIMPrimedTNT;
import invmod.common.entity.EntityIMSkeleton;
import invmod.common.entity.EntityIMSpawnProxy;
import invmod.common.entity.EntityIMSpider;
import invmod.common.entity.EntityIMThrower;
import invmod.common.entity.EntityIMTrap;
import invmod.common.entity.EntityIMWolf;
import invmod.common.entity.EntityIMZombie;
import invmod.common.entity.EntityIMZombiePigman;
import invmod.common.item.ItemCatalystMixture;
import invmod.common.item.ItemDampingAgent;
import invmod.common.item.ItemDebugWand;
import invmod.common.item.ItemEngyHammer;
import invmod.common.item.ItemInfusedSword;
import invmod.common.item.ItemNexusCatalyst;
import invmod.common.item.ItemPhaseCrystal;
import invmod.common.item.ItemProbe;
import invmod.common.item.ItemRiftFlux;
import invmod.common.item.ItemSearingBow;
import invmod.common.item.ItemSmallRemnants;
import invmod.common.item.ItemStableCatalystMixture;
import invmod.common.item.ItemStableNexusCatalyst;
import invmod.common.item.ItemStrangeBone;
import invmod.common.item.ItemStrongCatalyst;
import invmod.common.item.ItemStrongDampingAgent;
import invmod.common.item.ItemTrap;
import invmod.common.nexus.BlockNexus;
import invmod.common.nexus.IEntityIMPattern;
import invmod.common.nexus.IMWaveBuilder;
import invmod.common.nexus.MobBuilder;
import invmod.common.nexus.TileEntityNexus;
import invmod.common.util.ISelect;
import invmod.common.util.RandomSelectionPool;
import invmod.common.util.spawneggs.CustomTags;
import invmod.common.util.spawneggs.DispenserBehaviorSpawnEgg;
import invmod.common.util.spawneggs.ItemSpawnEgg;
import invmod.common.util.spawneggs.SpawnEggInfo;
import invmod.common.util.spawneggs.SpawnEggRegistry;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.resources.IReloadableResourceManager;

@Mod(modid = "mod_Invasion", name = "Invasion", version = "1.2.1")
public class mod_Invasion 
{

	@SidedProxy(clientSide = "invmod.client.ProxyClient", serverSide = "invmod.common.ProxyCommon")
	public static ProxyCommon proxy;
	
	//public static ResourceLoader resourceLoader;
	public static String recentNews;
	/*public static Version versionNumber = new Version(1, 1, 5);
	public static String latestVersionNumber;*/
	public static GuiHandler guiHandler;
	public static ConfigInvasion configInvasion;
	private static File configFile;
	private static boolean runFlag;
	private static long timer;
	private static long clientElapsed;
	private static long serverElapsed;
	private static boolean serverRunFlag;
	private static int killTimer;
	private static boolean loginFlag;
	private static HashMap<String, Long> deathList = new HashMap();
	private static MobBuilder defaultMobBuilder = new MobBuilder();
	private static BufferedWriter logOut;
	private static ISelect<IEntityIMPattern> nightSpawnPool1;
	private static TileEntityNexus focusNexus;
	private static TileEntityNexus activeNexus;
	private static boolean isInvasionActive = false;
	public static final byte PACKET_SFX = 0;
	public static final byte PACKET_INV_MOB_SPAWN = 2;
	public static int entityId=250;
	public static final String modid = "mod_Invasion";
	/*NOOB HAUS: Default settings for blocks/items etc; used to write config (common.Config) on first run 
	 */
	
	//Change this before releasing
	private static final boolean DEBUG_CONFIG=false;
	private static final int DEFAULT_GUI_ID_NEXUS = 76;
	private static final boolean DEFAULT_CRAFT_ITEMS_ENABLED = true;
	private static final boolean DEFAULT_NIGHT_SPAWNS_ENABLED = false;
	private static final int DEFAULT_MIN_CONT_MODE_DAYS = 2;
	private static final int DEFAULT_MAX_CONT_MODE_DAYS = 3;
	private static final int DEFAULT_NIGHT_MOB_SIGHT_RANGE = 20;
	private static final int DEFAULT_NIGHT_MOB_SENSE_RANGE = 8;
	private static final int DEFAULT_NIGHT_MOB_SPAWN_CHANCE = 30;
	private static final int DEFAULT_NIGHT_MOB_MAX_GROUP_SIZE = 3;
	private static final int DEFAULT_NIGHT_MOB_LIMIT_OVERRIDE = 70;
	private static final float DEFAULT_NIGHT_MOB_STATS_SCALING = 1.0F;
	private static final boolean DEFAULT_NIGHT_MOBS_BURN = true;
	public static final String[] DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS = {
		"zombie_t1_any", "zombie_t2_any_basic", "zombie_t2_plain", "zombie_t2_tar",
		"zombie_t2_pigman","zombie_t3_any","zombiePigman_t1_any","zombiePigman_t2_any","zombiePigman_t3_any", "spider_t1_any", "spider_t2_any", "pigengy_t1_any", "skeleton_t1_any", "thrower_t1", "thrower_t2", "creeper_t1_basic","imp_t1" };
	public static final float[] DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS = {
		1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F };
	
	//NOOB HAUS: Declare them values.. Declare em good
	private static boolean alreadyNotified;
	private static boolean updateNotifications;
	private static boolean enableLog;
	private static boolean destructedBlocksDrop;
	private static boolean mobsDropSmallRemnants;
	private static boolean craftItemsEnabled;
	private static boolean debugMode;
	private static int guiIdNexus;
	private static int minContinuousModeDays;
	private static int maxContinuousModeDays;
	private static boolean nightSpawnsEnabled;
	private static int nightMobSightRange;
	private static int nightMobSenseRange;
	private static int nightMobSpawnChance;
	private static int nightMobMaxGroupSize;
	private static int maxNightMobs;
	private static float nightMobStatsScaling;
	private static boolean nightMobsBurnInDay;
	
	//mobhealth
	public static HashMap<String, Integer> mobHealthNightspawn = new HashMap();
	public static HashMap<String, Integer> mobHealthInvasion = new HashMap();
	//Creative tab declaration
	public static CreativeTabInvmod tabInvmod;
	
	//NOOB HAUS: The almighty Nexus Block declaration
	public static BlockNexus blockNexus;
	
	//NOOB HAUS: Item declarations
	public static Item itemPhaseCrystal;
	public static Item itemRiftFlux;
	public static Item itemSmallRemnants;
	public static Item itemNexusCatalyst;
	public static Item itemInfusedSword;
	public static Item itemIMTrap;
	public static Item itemSearingBow;
	public static Item itemCatalystMixture;
	public static Item itemStableCatalystMixture;
	public static Item itemStableNexusCatalyst;
	public static Item itemDampingAgent;
	public static Item itemStrongDampingAgent;
	public static Item itemStrangeBone;
	public static Item itemProbe;
	public static Item itemStrongCatalyst;
	public static Item itemEngyHammer;
	public static Item itemDebugWand;
    public static ItemSpawnEgg itemSpawnEgg;
	public static mod_Invasion instance;

	public mod_Invasion() 
	{
		instance = this;
		runFlag = true;
		serverRunFlag = true;
		loginFlag = false;
		timer = 0L;
		clientElapsed = 0L;
		guiHandler = new GuiHandler();
	}
	//NOOB HAUS: End wtf? I am not certain what this method is for..

	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) 
	{

		//First up, we check for the config file, write it if it doesn't exist; or capture and return an error to the log

		File logFile = proxy.getFile("/logs/invasion_log.log");
		try 
		{
			if (!logFile.exists())
				logFile.createNewFile();
			logOut = new BufferedWriter(new FileWriter(logFile));
		} 
		catch (Exception e) 
		{
			logOut = null;
			log("Couldn't write to log file");
			log(e.getMessage());
		}

		//NOOB HAUS: Get the config file - store it into a variable; pass all that shiz thru common.configInvasion
		configFile = proxy.getFile("/config/invasion_config.cfg");
		alreadyNotified = false;
		
		configInvasion = new ConfigInvasion();
		configInvasion.loadConfig(configFile);
		
		updateNotifications = configInvasion.getPropertyValueBoolean("update-messages-enabled", false);
		enableLog = configInvasion.getPropertyValueBoolean("enable-log-file", false);
		destructedBlocksDrop = configInvasion.getPropertyValueBoolean("destructed-blocks-drop", true);
		mobsDropSmallRemnants = configInvasion.getPropertyValueBoolean("mobs-drop-small-remnants", true);
		//soundsEnabled = configInvasion.getPropertyValueBoolean("sounds-enabled", true);
		craftItemsEnabled = configInvasion.getPropertyValueBoolean("craft-items-enabled", true);
		debugMode = configInvasion.getPropertyValueBoolean("debug", false);
		guiIdNexus = configInvasion.getPropertyValueInt("guiID-Nexus", 76);

		minContinuousModeDays = configInvasion.getPropertyValueInt("min-days-to-attack", 2);
		maxContinuousModeDays = configInvasion.getPropertyValueInt("max-days-to-attack", 3);

		nightSpawnConfig();
		loadHealthConfig();
		//NOOB HAUS: Here a HashMap is done up for the block strength (what it takes for IM mob to dig through it)
		HashMap strengthOverrides = new HashMap();
		for (int i = 1; i < 4096; i++) 
		{
			String property = configInvasion.getProperty("block" + i + "-strength", "null");
			if (property != "null") 
			{
				float strength = Float.parseFloat(property);
				if (strength > 0.0F) 
				{
					strengthOverrides.put(Integer.valueOf(i), Float.valueOf(strength));
					EntityIMLiving.putBlockStrength(Block.getBlockById(i), strength);
					float pathCost = 1.0F + strength * 0.4F;
					EntityIMLiving.putBlockCost(Block.getBlockById(i), pathCost);
				}
			}

		}

		configInvasion.saveConfig(configFile, strengthOverrides, DEBUG_CONFIG);
		
		//Load the things!
		loadCreativeTabs();
		loadBlocks();
		loadItems();
		loadEntities();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) 
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, guiHandler);
		
		//removed along VersionChecker
		//new ThreadGetData();
		//Register to receive subscribed events
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
		FMLInterModComms.sendMessage("Waila", "register", "invmod.common.util.IMWailaProvider.callbackRegister");
		
		if (craftItemsEnabled) 
		{
			addRecipes();
		}
		
		if (nightSpawnsEnabled) 
		{
			BiomeGenBase[] biomes = { BiomeGenBase.plains, BiomeGenBase.extremeHills, BiomeGenBase.forest, BiomeGenBase.taiga, BiomeGenBase.swampland, BiomeGenBase.forestHills, BiomeGenBase.taigaHills, BiomeGenBase.extremeHillsEdge, BiomeGenBase.jungle, BiomeGenBase.jungleHills };
			EntityRegistry.addSpawn(EntityIMSpawnProxy.class, nightMobSpawnChance, 1, 1, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityZombie.class, 1, 1, 1, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntitySpider.class, 1, 1, 1, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntitySkeleton.class, 1, 1, 1, EnumCreatureType.monster, biomes);
		}

		if (maxNightMobs != 70) 
		{
			try 
			{
				Class c = EnumCreatureType.class;
				Object[] consts = c.getEnumConstants();
				Class sub = consts[0].getClass();
				Field field = sub.getDeclaredField("maxNumberOfCreature");
				field.setAccessible(true);
				field.set(EnumCreatureType.monster, Integer.valueOf(maxNightMobs));
			} 
			catch (Exception e) 
			{
				log(e.getMessage());
			}
		}

	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent evt) {
		/*((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new IResourceManagerReloadListener() {
			private boolean ranOnce = false;

			@Override
			public void onResourceManagerReload(IResourceManager resourcemanager) {
				// FML forces a TexturePack reload after MC has finished initialising, allowing for mod icons to be registered at any point in init
				// we only want to run on the second and later icon reloads
				if (!ranOnce) {
					ranOnce = true;
					return;
				}
				StringTranslate.inject(new ByteArrayInputStream(("item.upgrade.structural."  + ".name="   + " "  + " ("  + ")").getBytes()));
			}
		});*/
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent event) 
	{
		ICommandManager commandManager = FMLCommonHandler.instance().getMinecraftServerInstance().getCommandManager();
		if ((commandManager instanceof CommandHandler)) 
		{
			((CommandHandler) commandManager).registerCommand(new InvasionCommand());
		}
	}

	/*@SubscribeEvent
    public void PlayerLoggedInEvent (cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event)
    {
		try {
			if(getUpdateNotifications() && !alreadyNotified)
			{
				alreadyNotified=true;
				//VersionChecker.checkForUpdates((EntityPlayerMP)event.player);
			}
		} catch(Exception e) {}
    }*/
	
	//load mobhealth config
	private void loadHealthConfig()
	{
    	//Invasion spawns
		mobHealthInvasion.put("IMCreeper-T1-invasionSpawn-health", mod_Invasion.configInvasion.getPropertyValueInt("IMCreeper-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMVulture-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMVulture-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMImp-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMImp-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMPigManEngineer-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMPigManEngineer-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMSkeleton-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSkeleton-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMSpider-T1-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Spider-invasionSpawn-health", 18));
		mobHealthInvasion.put("IMSpider-T1-Baby-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Baby-Spider-invasionSpawn-health", 3));
		mobHealthInvasion.put("IMSpider-T2-Jumping-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Jumping-Spider-invasionSpawn-health", 18));
		mobHealthInvasion.put("IMSpider-T2-Mother-Spider-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Mother-Spider-invasionSpawn-health", 23));
		mobHealthInvasion.put("IMThrower-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T1-invasionSpawn-health", 50));
		mobHealthInvasion.put("IMThrower-T2-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T2-invasionSpawn-health", 70));
		mobHealthInvasion.put("IMZombie-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMZombie-T2-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T2-invasionSpawn-health", 30));
		mobHealthInvasion.put("IMZombie-T3-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T3-invasionSpawn-health", 65));
		mobHealthInvasion.put("IMZombiePigman-T1-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T1-invasionSpawn-health", 20));
		mobHealthInvasion.put("IMZombiePigman-T2-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T2-invasionSpawn-health", 30));
		mobHealthInvasion.put("IMZombiePigman-T3-invasionSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T3-invasionSpawn-health", 65));
		
		//Night spawns
		mobHealthNightspawn.put("IMCreeper-T1-nightSpawn-health", mod_Invasion.configInvasion.getPropertyValueInt("IMCreeper-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMVulture-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMVulture-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMImp-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMImp-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMPigManEngineer-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMPigManEngineer-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMSkeleton-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSkeleton-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMSpider-T1-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Spider-nightSpawn-health", 18));
		mobHealthNightspawn.put("IMSpider-T1-Baby-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T1-Baby-Spider-nightSpawn-health", 3));
		mobHealthNightspawn.put("IMSpider-T2-Jumping-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Jumping-Spider-nightSpawn-health", 18));
		mobHealthNightspawn.put("IMSpider-T2-Mother-Spider-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMSpider-T2-Mother-Spider-nightSpawn-health", 23));
		mobHealthNightspawn.put("IMThrower-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T1-nightSpawn-health", 50));
		mobHealthNightspawn.put("IMThrower-T2-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMThrower-T2-nightSpawn-health", 70));
		mobHealthNightspawn.put("IMZombie-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMZombie-T2-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T2-nightSpawn-health", 30));
		mobHealthNightspawn.put("IMZombie-T3-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombie-T3-nightSpawn-health", 65));	
		mobHealthNightspawn.put("IMZombiePigman-T1-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T1-nightSpawn-health", 20));
		mobHealthNightspawn.put("IMZombiePigman-T2-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T2-nightSpawn-health", 30));
		mobHealthNightspawn.put("IMZombiePigman-T3-nightSpawn-health",  mod_Invasion.configInvasion.getPropertyValueInt("IMZombiePigman-T3-nightSpawn-health", 65));

	}
	//load Creativetab
	protected void loadCreativeTabs()
	{
		 tabInvmod = new CreativeTabInvmod();
	}
	
	//Load Blocks
	protected void loadBlocks() 
	{
		blockNexus = new BlockNexus();
		GameRegistry.registerBlock(blockNexus, blockNexus.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityNexus.class, "Nexus");
	}

	//Load Items
	protected void loadItems() 
	{
		itemPhaseCrystal = new ItemPhaseCrystal();
		itemRiftFlux = new ItemRiftFlux();
		itemSmallRemnants = new ItemSmallRemnants();
		itemNexusCatalyst = new ItemNexusCatalyst();
		itemInfusedSword = new ItemInfusedSword();
		itemSearingBow = new ItemSearingBow();
		itemCatalystMixture = new ItemCatalystMixture();
		itemStableCatalystMixture = new ItemStableCatalystMixture();
		itemStableNexusCatalyst = new ItemStableNexusCatalyst();
		itemDampingAgent = new ItemDampingAgent();
		itemStrongDampingAgent = new ItemStrongDampingAgent();
		itemStrangeBone = new ItemStrangeBone();
		itemStrongCatalyst = new ItemStrongCatalyst();
		itemEngyHammer = new ItemEngyHammer();
		itemProbe = new ItemProbe();
		itemIMTrap = new ItemTrap();
		itemSpawnEgg = new ItemSpawnEgg();

		
		GameRegistry.registerItem(itemPhaseCrystal, itemPhaseCrystal.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemRiftFlux, itemRiftFlux.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemSmallRemnants, itemSmallRemnants.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemNexusCatalyst, itemNexusCatalyst.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemInfusedSword, itemInfusedSword.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemSearingBow, itemSearingBow.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemCatalystMixture, itemCatalystMixture.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemStableCatalystMixture, itemStableCatalystMixture.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemStableNexusCatalyst, itemStableNexusCatalyst.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemDampingAgent, itemDampingAgent.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemStrongDampingAgent, itemStrongDampingAgent.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemStrangeBone, itemStrangeBone.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemStrongCatalyst, itemStrongCatalyst.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemEngyHammer, itemEngyHammer.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemProbe, itemProbe.getUnlocalizedName().substring(5));
		GameRegistry.registerItem(itemIMTrap, itemIMTrap.getUnlocalizedName().substring(5));
		
		if (debugMode) 
		{
			itemDebugWand = new ItemDebugWand();
			GameRegistry.registerItem(itemDebugWand, itemDebugWand.getUnlocalizedName().substring(5));
		} 
		else 
		{
			itemDebugWand = null;
		}
		
		
		
	}

	
	//Load Entities
	protected void loadEntities() 
	{
		  
		//Register Entities
		EntityRegistry.registerModEntity(EntityIMZombie.class, "IMZombie", 5, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMSkeleton.class, "IMSkeleton", 6, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMSpider.class, "IMSpider", 7, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMPigEngy.class, "IMPigEngy", 8, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMWolf.class, "IMWolf", 9, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMEgg.class, "IMEgg", 10, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMCreeper.class, "IMCreeper", 11, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMImp.class, "IMImp", 12, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMZombiePigman.class, "IMZombiePigman", 13, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMThrower.class, "IMThrower", 14, this, 128, 1, true);
        EntityRegistry.registerModEntity(EntityIMBoulder.class, "IMBoulder", 1, this, 36, 4, true);
        EntityRegistry.registerModEntity(EntityIMBolt.class, "IMBolt", 2, this, 36, 5, false);
        EntityRegistry.registerModEntity(EntityIMTrap.class, "IMTrap", 3, this, 36, 5, false);
        EntityRegistry.registerModEntity(EntityIMPrimedTNT.class, "IMPrimedTNT", 4, this, 36, 4, true);

		if (debugMode) 
		{
			
			EntityRegistry.registerModEntity(EntityIMBird.class, "IMBird", 15, this, 128, 1, true);
            EntityRegistry.registerModEntity(EntityIMGiantBird.class, "IMGiantBird", 16, this, 128, 1, true);
		}
        
        //spawneggs' needed things and dispenser behavior
        GameRegistry.registerItem(itemSpawnEgg, itemSpawnEgg.getUnlocalizedName());
        BlockDispenser.dispenseBehaviorRegistry.putObject(itemSpawnEgg, new DispenserBehaviorSpawnEgg());
        
        //Add spawneggs
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 1, "mod_Invasion.IMZombie", "Zombie T1", CustomTags.IMZombie_T1(), 0x6B753F, 0x281B0A));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 2, "mod_Invasion.IMZombie", "Zombie T2", CustomTags.IMZombie_T2(), 0x497533, 0x7C7C7C));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 3, "mod_Invasion.IMZombie", "Tar Zombie T2", CustomTags.IMZombie_T2_tar(), 0x3A4225, 0x191C13));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 4, "mod_Invasion.IMZombie", "Zombie Brute T3", CustomTags.IMZombie_T3(), 0x586146, 0x1E4639));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 5, "mod_Invasion.IMSkeleton", "Skeleton T1", new NBTTagCompound(), 0x9B9B9B, 0x797979));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 6, "mod_Invasion.IMSpider", "Spider T1", new NBTTagCompound(), 0x504A3E, 0xA4121C));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 7, "mod_Invasion.IMSpider", "Spider T1 Baby", CustomTags.IMSpider_T1_baby(), 0x504A3E, 0xA4121C));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 8, "mod_Invasion.IMSpider", "Spider T2 Jumper", CustomTags.IMSpider_T2(), 0x444167, 0x0A0328));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 9, "mod_Invasion.IMSpider", "Spider T2 Mother", CustomTags.IMSpider_T2_mother(), 0x444167, 0x0A0328));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 10, "mod_Invasion.IMCreeper", "Creeper T1", new NBTTagCompound(), 0x238F1F, 0xA5AAA6));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 11, "mod_Invasion.IMPigEngy", "Pigman Engineer T1", new NBTTagCompound(), 0xEC9695, 0x420000));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 12, "mod_Invasion.IMThrower", "Thrower T1", new NBTTagCompound(), 0x545F37, 0x1D2D3E));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 13, "mod_Invasion.IMThrower", "Thrower T2", CustomTags.IMThrower_T2(), 0x5303814, 0x632808));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 14, "mod_Invasion.IMImp", "Imp T1", new NBTTagCompound(), 0xB40113, 0xFF0000));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 15, "mod_Invasion.IMZombiePigman", "Zombie Pigman T1", CustomTags.IMZombiePigman_T1(), 0xEB8E91, 0x49652F));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 16, "mod_Invasion.IMZombiePigman", "Zombie Pigman T2", CustomTags.IMZombiePigman_T2(), 0xEB8E91, 0x49652F));
        SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 17, "mod_Invasion.IMZombiePigman", "Zombie Pigman T3", CustomTags.IMZombiePigman_T3(), 0xEB8E91, 0x49652F));
        
        if (debugMode)
        {   
            SpawnEggRegistry.registerSpawnEgg(new SpawnEggInfo((short) 18, "mod_Invasion.IMGiantBird", "Vulture T1", new NBTTagCompound(), 0x2B2B2B, 0xEA7EDC));
        }

		
		//preload Textures
		proxy.preloadTexture("/mods/invmod/textures/zombie_old.png");
		proxy.preloadTexture("/mods/invmod/textures/zombieT1a.png");
		proxy.preloadTexture("/mods/invmod/textures/zombieT2.png");
		proxy.preloadTexture("/mods/invmod/textures/zombieT2a.png");
		proxy.preloadTexture("/mods/invmod/textures/zombietar.png");
		proxy.preloadTexture("/mods/invmod/textures/zombieT3.png");
		proxy.preloadTexture("/mods/invmod/textures/spiderT2.png");
		proxy.preloadTexture("/mods/invmod/textures/spiderT2b.png");
		proxy.preloadTexture("/mods/invmod/textures/throwerT1.png");
		proxy.preloadTexture("/mods/invmod/textures/throwerT2.png");
		proxy.preloadTexture("/mods/invmod/textures/pigengT1.png");
		proxy.preloadTexture("/mods/invmod/textures/nexusgui.png");
		proxy.preloadTexture("/mods/invmod/textures/boulder.png");
		proxy.preloadTexture("/mods/invmod/textures/trap.png");
		proxy.preloadTexture("/mods/invmod/textures/testmodel.png");
		proxy.preloadTexture("/mods/invmod/textures/burrower.png");
		proxy.preloadTexture("/mods/invmod/textures/spideregg.png");
		proxy.preloadTexture("/mods/invmod/textures/imp.png");
		
		proxy.preloadTexture("/mods/invmod/textures/vulture.png");
		
		//Animations and rendering
		proxy.loadAnimations();
		proxy.registerEntityRenderers();
	}

	//Register Recipes
	protected void addRecipes() 
	{
		GameRegistry.addRecipe(new ItemStack(blockNexus, 1), new Object[] { " X ", "#D#", " # ", Character.valueOf('X'), itemPhaseCrystal, Character.valueOf('#'), Items.redstone, Character.valueOf('D'), Blocks.obsidian });

		GameRegistry.addRecipe(new ItemStack(itemPhaseCrystal, 1), new Object[] { " X ", "#D#", " X ", Character.valueOf('X'), new ItemStack(Items.dye, 1, 4), Character.valueOf('#'), Items.redstone, Character.valueOf('D'), Items.diamond });

		GameRegistry.addRecipe(new ItemStack(itemPhaseCrystal, 1), new Object[] { " X ", "#D#", " X ", Character.valueOf('X'), Items.redstone, Character.valueOf('#'), new ItemStack(Items.dye, 1, 4), Character.valueOf('D'), Items.diamond });

		GameRegistry.addRecipe(new ItemStack(itemRiftFlux, 1), new Object[] { "XXX", "XXX", "XXX", Character.valueOf('X'), new ItemStack(itemSmallRemnants, 1) });

		GameRegistry.addRecipe(new ItemStack(itemInfusedSword, 1), new Object[] { "X  ", "X# ", "X  ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1), Character.valueOf('#'),new ItemStack(Items.diamond_sword, 1, OreDictionary.WILDCARD_VALUE) });

		GameRegistry.addRecipe(new ItemStack(itemCatalystMixture, 1), new Object[] { "   ", "D#H", " X ", Character.valueOf('X'), Items.bowl, Character.valueOf('#'), Items.redstone, Character.valueOf('D'), Items.bone, Character.valueOf('H'), Items.rotten_flesh });

		GameRegistry.addRecipe(new ItemStack(itemCatalystMixture, 1), new Object[] { "   ", "H#D", " X ", Character.valueOf('X'), Items.bowl, Character.valueOf('#'), Items.redstone, Character.valueOf('D'), Items.bone, Character.valueOf('H'), Items.rotten_flesh });

		GameRegistry.addRecipe(new ItemStack(itemStableCatalystMixture, 1), new Object[] { "   ", "D#D", " X ", Character.valueOf('X'), Items.bowl, Character.valueOf('#'), Items.coal, Character.valueOf('D'), Items.bone, Character.valueOf('H'), Items.rotten_flesh });

		GameRegistry.addRecipe(new ItemStack(itemDampingAgent, 1), new Object[] { "   ", "#X#", "   ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1), Character.valueOf('#'), new ItemStack(Items.dye, 1, 4) });

		GameRegistry.addRecipe(new ItemStack(itemStrongDampingAgent, 1), new Object[] { " X ", " X ", " X ", Character.valueOf('X'), itemDampingAgent });

		GameRegistry.addRecipe(new ItemStack(itemStrongDampingAgent, 1), new Object[] { "   ", "XXX", "   ", Character.valueOf('X'), itemDampingAgent });

		GameRegistry.addRecipe(new ItemStack(itemStrangeBone, 1), new Object[] { "   ", "X#X", "   ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1), Character.valueOf('#'), Items.bone });

		GameRegistry.addRecipe(new ItemStack(itemSearingBow, 1), new Object[] { "XXX", "X# ", "X  ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1), Character.valueOf('#'),new ItemStack(Items.bow, 1, OreDictionary.WILDCARD_VALUE) });

		GameRegistry.addRecipe(new ItemStack(Items.gunpowder, 16), new Object[] { " X ", " X ", " X ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });
		GameRegistry.addRecipe(new ItemStack(Items.gunpowder, 16), new Object[] { "   ", "XXX", "   ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });

		GameRegistry.addRecipe(new ItemStack(Items.diamond, 1), new Object[] { " X ", "X X", " X ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });

		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot, 4), new Object[] { "   ", " X ", "   ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });

		GameRegistry.addRecipe(new ItemStack(Items.redstone, 24), new Object[] { "   ", "X X", "   ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });

		GameRegistry.addRecipe(new ItemStack(Items.dye, 12, 4), new Object[] { " X ", "   ", " X ", Character.valueOf('X'), new ItemStack(itemRiftFlux, 1) });

		GameRegistry.addRecipe(new ItemStack(itemIMTrap, 1, 0), new Object[] { " X ", "X#X", " X ", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('#'), new ItemStack(itemRiftFlux, 1) });

		GameRegistry.addRecipe(new ItemStack(itemIMTrap, 1, 2), new Object[] { "   ", " # ", " X ", Character.valueOf('X'), new ItemStack(itemIMTrap, 1, 0), Character.valueOf('#'), Items.lava_bucket });

		GameRegistry.addRecipe(new ItemStack(itemProbe, 1, 0), new Object[] { " X ", "XX ", "XX ", Character.valueOf('X'), Items.iron_ingot });

		GameRegistry.addRecipe(new ItemStack(itemProbe, 1, 1), new Object[] { " D ", " # ", " X ", Character.valueOf('X'), Items.blaze_rod, Character.valueOf('#'), itemPhaseCrystal, Character.valueOf('D'), new ItemStack(itemProbe, 1, 0) });

		GameRegistry.addSmelting(itemCatalystMixture, new ItemStack(itemNexusCatalyst), 1.0F);
		GameRegistry.addSmelting(itemStableCatalystMixture, new ItemStack(itemStableNexusCatalyst), 1.0F);
	}

	protected void nightSpawnConfig() 
	{
		nightSpawnsEnabled = configInvasion.getPropertyValueBoolean("night-spawns-enabled", false);
		nightMobSightRange = configInvasion.getPropertyValueInt("night-mob-sight-range", 20);
		nightMobSenseRange = configInvasion.getPropertyValueInt("night-mob-sense-range", 12);
		nightMobSpawnChance = configInvasion.getPropertyValueInt("night-mob-spawn-chance", 30);
		nightMobMaxGroupSize = configInvasion.getPropertyValueInt("night-mob-max-group-size", 3);
		maxNightMobs = configInvasion.getPropertyValueInt("mob-limit-override", 70);
		nightMobsBurnInDay = configInvasion.getPropertyValueBoolean("night-mobs-burn-in-day", true);
		
		String[] pool1Patterns = new String[DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length];
		float[] pool1Weights = new float[DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS.length];
		RandomSelectionPool mobPool = new RandomSelectionPool();
		nightSpawnPool1 = mobPool;
		if (DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length == DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS.length) 
		{
			for (int i = 0; i < DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length; i++) 
			{
				pool1Patterns[i] = configInvasion.getPropertyValueString("nm-spawnpool1-slot" + (1 + i), DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS[i]);
				pool1Weights[i] = configInvasion.getPropertyValueFloat("nm-spawnpool1-slot" + (1 + i) + "-weight", DEFAULT_NIGHT_MOB_PATTERN_1_SLOT_WEIGHTS[i]);
				
				if (IMWaveBuilder.isPatternNameValid(pool1Patterns[i])) 
				{
					log("Added entry for pattern 1 slot " + (i + 1));
					mobPool.addEntry(IMWaveBuilder.getPattern(pool1Patterns[i]), pool1Weights[i]);
				} 
				else 
				{
					log("Pattern 1 slot " + (i + 1) + " in config not recognized. Proceeding as blank.");
					configInvasion.setProperty("nm-spawnpool1-slot" + (1 + i), "none");
				}
			}
		} 
		else 
		{
			log("Mob pattern table element mismatch. Ensure each slot has a probability weight");
		}
	}

	//broken after update, need to check tick stuff
//	public static boolean onClientTick() 
//	{
//		if (runFlag) 
//		{
//			if ((soundsEnabled) && (!soundHandler.soundsInstalled())) 
//			{
//				proxy.printGuiMessage("Invasion Mod Warning: Failed to auto-install sounds. You can disable this process in config or give a bug report");
//			}
//			runFlag = false;
//		}
//
//		return true;
//	}

	//broken after update, need to fix tick stuff
//	@Override
//	public static boolean onServerTick() 
//	{
//		if (serverRunFlag)
//		{
//			timer = System.currentTimeMillis();
//			serverRunFlag = false;
//		}
//
//		serverElapsed -= timer;
//		timer = System.currentTimeMillis();
//		serverElapsed += timer;
//		if (serverElapsed >= 100L) 
//		{
//			serverElapsed -= 100L;
//
//			if (loginFlag) 
//			{
//				killTimer += 1;
//			}
//
//			if (killTimer > 35) 
//			{
//				killTimer = 0;
//				loginFlag = false;
//				for (Map.Entry entry : deathList.entrySet()) 
//				{
//					if (System.currentTimeMillis() - ((Long) entry.getValue()).longValue() > 300000L) 
//					{
//						deathList.remove(entry.getKey());
//					}
//					else 
//					{
//						for (World world : DimensionManager.getWorlds()) 
//						{
//							EntityPlayer player = world.getPlayerEntityByName((String) entry.getKey());
//							if (player != null) 
//							{
//								player.attackEntityFrom(DamageSource.magic, 500.0F);
//								player.setDead();
//								deathList.remove(player.getDisplayName());
//								broadcastToAll("Nexus energies caught up to " + player.getDisplayName());
//							}
//						}
//					}
//				}
//			}
//		}
//
//		return true;
//	}

	public static void addToDeathList(String username, long timeStamp) 
	{
		deathList.put(username, Long.valueOf(timeStamp));
	}
	@Override
	public String toString() 
	{
		return "mod_Invasion";
	}
	@Override
	protected void finalize() throws Throwable 
	{
		try
		{
			if (logOut != null)
				logOut.close();
		} 
		catch (Exception e) 
		{
			logOut = null;
			log("Error closing invasion log file");
		} 
		finally
		{
			super.finalize();
		}
	}

	public static boolean isInvasionActive() 
	{
		return isInvasionActive;
	}

	public static boolean tryGetInvasionPermission(TileEntityNexus nexus)
	{
		if (nexus == activeNexus)
		{
			return true;
		}
		if (nexus == null)
		{
			String s = "Nexus entity invalid";
			log(s);
		}
		else
		{
			activeNexus = nexus;
			isInvasionActive = true;
			return true;
		}
		return false;
	}

	public static void setInvasionEnded(TileEntityNexus nexus) 
	{
		if (activeNexus == nexus)
		{
			isInvasionActive = false;
		}
	}

	public static void setNexusUnloaded(TileEntityNexus nexus)
	{
		if (activeNexus == nexus) 
		{
			nexus = null;
			isInvasionActive = false;
		}
	}

	public static void setNexusClicked(TileEntityNexus nexus) 
	{
		focusNexus = nexus;
	}

	public static TileEntityNexus getActiveNexus() 
	{
		return activeNexus;
	}

	public static TileEntityNexus getFocusNexus() 
	{
		return focusNexus;
	}

	public static Entity[] getNightMobSpawns1(World world) 
	{
		ISelect mobPool = getMobSpawnPool();
		int numberOfMobs = world.rand.nextInt(nightMobMaxGroupSize) + 1;
		Entity[] entities = new Entity[numberOfMobs];
		for (int i = 0; i < numberOfMobs; i++) {
			EntityIMLiving mob = getMobBuilder().createMobFromConstruct(((IEntityIMPattern) mobPool.selectNext()).generateEntityConstruct(), world, null);
			mob.setEntityIndependent();
			//also set in entityLiving constructor, is needed for AI to function properly, I believe
			mob.setAggroRange(getNightMobSightRange());
			mob.setSenseRange(getNightMobSenseRange());
			mob.setBurnsInDay(getNightMobsBurnInDay());
			entities[i] = mob;
		}
		return entities;
	}

	public static MobBuilder getMobBuilder() 
	{
		return defaultMobBuilder;
	}

	public static ISelect<IEntityIMPattern> getMobSpawnPool() 
	{
		return nightSpawnPool1;
	}

	public static int getMinContinuousModeDays() 
	{
		return minContinuousModeDays;
	}

	public static int getMaxContinuousModeDays() 
	{
		return maxContinuousModeDays;
	}

	public static int getNightMobSightRange() 
	{
		return nightMobSightRange;
	}

	public static int getNightMobSenseRange() 
	{
		return nightMobSenseRange;
	}

	public static boolean getNightMobsBurnInDay() 
	{
		return nightMobsBurnInDay;
	}

	public static ItemStack getRenderHammerItem() 
	{
		return new ItemStack(itemEngyHammer, 1);
	}

	public static int getGuiIdNexus() 
	{
		return guiIdNexus;
	}

	public static mod_Invasion getLoadedInstance() 
	{
		return instance;
	}

	public static void broadcastToAll(EnumChatFormatting color, String message) 
	{
		FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendChatMsg(new ChatComponentText(message).setChatStyle(new ChatStyle().setColor(color)));
	}

	public static void sendMessageToPlayers(HashMap<String, Long> hashMap, EnumChatFormatting color, String message, Object... formatArgs) {
		if (hashMap != null) {
			for (Map.Entry entry : hashMap.entrySet())
	        {
	          sendMessageToPlayer((EntityPlayerMP) FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a((String) entry.getKey()), color, message, formatArgs);
	        }
		}
	}
	
	public static void sendMessageToPlayer(EntityPlayerMP player, EnumChatFormatting color, String message, Object... formatArgs) 
	{
		if (player != null) {
			player.addChatMessage(new ChatComponentTranslation(message, formatArgs).setChatStyle(new ChatStyle().setColor(color)));
		}
	}

	public static void log(String s) 
	{
		if (mod_Invasion.enableLog) {
			if (s == null) 
			{
				return;
			}
			try
			{
				if (logOut != null) 
				{
					logOut.write(s);
					logOut.newLine();
					logOut.flush();
				}
				else 
				{
					System.out.println(s);
				}
			} 
			catch (IOException e) 
			{
				System.out.println("Couldn't write to invasion log file");
				System.out.println(s);
			}
		}
	}

	public static boolean isDebug() 
	{
		return debugMode;
	}
	
	public static int getMobHealth(EntityIMLiving mob)
	{
		int health=0;
		if(mob.isNexusBound())
		{
			if(mobHealthInvasion.get(mob.toString() + "-invasionSpawn-health")!=null)
			{
			health = mobHealthInvasion.get(mob.toString() + "-invasionSpawn-health");
			}else{
				return 20;
			}
		}else{
			if(mobHealthNightspawn.get(mob.toString() + "-nightSpawn-health")!=null)
			{
			health = mobHealthNightspawn.get(mob.toString() + "-nightSpawn-health");
			}else{
				return 20;
			}
		}
		return health;
	}

    
    public static boolean getUpdateNotifications()
    {
    	return updateNotifications;
    }
    /*public static Version getVersionNumber()
    {
    	return versionNumber;
    }
    
    public static String getLatestVersionNumber()
    {
    	return latestVersionNumber;
    }*/
    public static String getRecentNews()
    {
    	return recentNews;
    }
    
	public static boolean getDestructedBlocksDrop()
	{
		return destructedBlocksDrop;
	}
	
	public static boolean getMobsDropSmallRemnants()
	{
		return mobsDropSmallRemnants;
	}
	
}