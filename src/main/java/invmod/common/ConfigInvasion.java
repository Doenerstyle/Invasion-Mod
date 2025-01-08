package invmod.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConfigInvasion extends Config
{
  public void saveConfig(File saveFile, HashMap<Integer, Float> strengthOverrides, boolean debug)
  {
    try
    {
      BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
      Iterator mobNightSpawnHealth = mod_Invasion.mobHealthNightspawn.entrySet().iterator();
      Iterator mobInvasionSpawnHealth = mod_Invasion.mobHealthInvasion.entrySet().iterator();
      try
      {
        writer.write("# Invasion Mod config");
        writer.newLine();
        writer.write("# Delete this file to restore defaults");
        writer.newLine();
        writer.newLine();
        writer.write("# General settings and IDs");
        writer.newLine();
        writer.write("# update-messages-enabled is currently unused");
        writer.newLine();
        writeProperty(writer, "update-messages-enabled");
        writeProperty(writer, "enable-log-file");
        writeProperty(writer, "mobs-drop-small-remnants");        
        writeProperty(writer, "destructed-blocks-drop");
        writeProperty(writer, "craft-items-enabled");
        writeProperty(writer, "guiID-Nexus");
        if (debug)
        {
          writeProperty(writer, "debug");
          
        }
        writer.newLine();

        writer.write("# Nexus Continuous Mode");
        writer.newLine();
        writeProperty(writer, "min-days-to-attack");
        writeProperty(writer, "max-days-to-attack");
        writer.newLine();
        writer.write("# Mob health during invasion");
        writer.newLine();
        
        while (mobInvasionSpawnHealth.hasNext())
        {
        	  Map.Entry pairs = (Map.Entry)mobInvasionSpawnHealth.next();
              writeProperty(writer,pairs.getKey().toString());
              //mobInvasionSpawnHealth.remove(); // avoids a ConcurrentModificationException

        }
        writer.newLine();
        
        //Block strengt options
//        writer.write("# Block strengths");
//        writer.newLine();
//        writer.write("# Add entries here for other mods' blocks");
//        writer.newLine();
//        writer.write("# Reference values: dirt=3.125, gravel=2.5, obsidian=7.7, stone=5.5 (plus up to 50% from special)");
//        writer.newLine();
//        writer.write("# Format:  block<id>-strength=<strength>");
//        writer.newLine();
//        if (strengthOverrides.size() == 0)
//        {
//          writer.write("# First example, reinforced stone from IC2 (remove comment symbol '#')");
//          writer.newLine();
//          writer.write("# block231-strength=10.5");
//          writer.newLine();
//        }
//        else
//        {
//					for (Map.Entry entry : strengthOverrides.entrySet())
//          {
//            writer.write("block" + entry.getKey() + "-strength=" + entry.getValue());
//            writer.newLine();
//          }
//        }
//        writer.newLine();

        writer.write("# Nighttime mob spawning behaviour (does not affect the nexus)");
        writer.newLine();
        writer.write("# mob-limit-override: The maximum number of randomly spawned mobs that may exist in the world. This applies to ALL of minecraft (default: 70)");
        writer.newLine();
        writeProperty(writer, "mob-limit-override");
        writer.newLine();
        writer.write("# night-spawns-enabled: Currently does not remove any default mobs, only adds new spawns");
        writer.newLine();
        writeProperty(writer, "night-spawns-enabled");
        writer.newLine();
        writer.write("# night-mob-spawn-chance: Higher number means mobs are more common");
        writer.newLine();
        writeProperty(writer, "night-mob-spawn-chance");
        writer.newLine();
        writer.write("# night-mob-group-size: The maximum number of mobs that may spawn together");
        writer.newLine();
        writeProperty(writer, "night-mob-max-group-size");
        writer.newLine();
        writer.write("# night-mob-sight-range: How far mobs can see a player from");
        writer.newLine();
        writeProperty(writer, "night-mob-sight-range");
        writer.newLine();
        writer.write("# night-mob-sense-range: How far mobs can smell a player (trough walls)");
        writer.newLine();
        writeProperty(writer, "night-mob-sense-range");
        writer.newLine();
        writer.newLine();
        
        
        writer.write("# Nightime mob spawning tables (also does not affect the nexus)");
        writer.newLine();
        writer.write("# A spawnpool contains mobs that can possibly spawn, and the probability weight of them spawning.");
        writer.newLine();
        writer.write("# Expenation: zombie_t2_any_basic has all T2, zombie_t2_plain excludes tar zombies");
        writer.newLine();
        for (int i = 0; i < mod_Invasion.DEFAULT_NIGHT_MOB_PATTERN_1_SLOTS.length; i++)
        {
          writeProperty(writer, "nm-spawnpool1-slot" + (1 + i));
          writeProperty(writer, "nm-spawnpool1-slot" + (1 + i) + "-weight");
        }
        
        writer.newLine();
        writer.write("# Nightspawn mob health");
        writer.newLine();
        while (mobNightSpawnHealth.hasNext())
        {
        	  Map.Entry pairs = (Map.Entry)mobNightSpawnHealth.next();
              writeProperty(writer,pairs.getKey().toString());
              // mobNightSpawnHealth.remove(); // avoids a ConcurrentModificationException

        }
        
        
        
        writer.flush();
      }
      finally
      {
        writer.close();
      }
    }
    catch (IOException e)
    {
      mod_Invasion.log(e.getMessage());
    }
  }
}