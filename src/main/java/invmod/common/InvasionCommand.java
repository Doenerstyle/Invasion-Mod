package invmod.common;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class InvasionCommand extends CommandBase {
	
	public void processCommand(ICommandSender sender, String[] args) {
		String username = sender.getCommandSenderName();
		if ((args.length > 0) && (args.length <= 7)) {
			if (args[0].equalsIgnoreCase("help")) {
				sendCommandHelp(sender);
			} else if (args[0].equalsIgnoreCase("begin") || args[0].equalsIgnoreCase("start")) {
				if (args.length == 2) {
					int startWave = Integer.parseInt(args[1]);
					if (mod_Invasion.getFocusNexus() != null) {
						if(startWave > 0) {
							mod_Invasion.getFocusNexus().debugStartInvasion(startWave);
						} else {
							sender.addChatMessage(new ChatComponentText("There are no waves before the first wave.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("stop")) {
				if (mod_Invasion.getActiveNexus() != null) {
					mod_Invasion.getActiveNexus().emergencyStop();
					mod_Invasion.broadcastToAll(EnumChatFormatting.RED, username + " has ended the invasion!");
				} else {
					sender.addChatMessage(new ChatComponentText("There is no invasion to end.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				}
			} else if (args[0].equalsIgnoreCase("range")) {
				if (args.length == 2) {
					int radius = Integer.parseInt(args[1]);
					if (mod_Invasion.getFocusNexus() != null) {
						if ((radius >= 32) && (radius <= 128)) {
							if (mod_Invasion.getFocusNexus().setSpawnRadius(radius)) {
								sender.addChatMessage(new ChatComponentText("Set Nexus range to " + EnumChatFormatting.DARK_GREEN + radius).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
							} else {
								sender.addChatMessage(new ChatComponentText("Can't change range while Nexus is active.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
							}
						} else {
							sender.addChatMessage(new ChatComponentText("Range must be between 32 and 128.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
						}
					} else {
						sender.addChatMessage(new ChatComponentText("Right-click the Nexus first to set target for commands.").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
					}
				}
			} else if (args[0].equalsIgnoreCase("spawnertest")) {
				int startWave = 1;
				int endWave = 11;

				if (args.length >= 4)
					return;
				if (args.length >= 3)
					endWave = Integer.parseInt(args[2]);
				if (args.length >= 2) {
					startWave = Integer.parseInt(args[1]);
				}
				Tester tester = new Tester();
				tester.doWaveSpawnerTest(startWave, endWave);
			} else if (args[0].equalsIgnoreCase("pointcontainertest")) {
				Tester tester = new Tester();
				tester.doSpawnPointSelectionTest();
			} else if (args[0].equalsIgnoreCase("wavebuildertest")) {
				float difficulty = 1.0F;
				float tierLevel = 1.0F;
				int lengthSeconds = 160;

				if (args.length >= 5)
					return;
				if (args.length >= 4)
					lengthSeconds = Integer.parseInt(args[3]);
				if (args.length >= 3)
					tierLevel = Float.parseFloat(args[2]);
				if (args.length >= 2) {
					difficulty = Float.parseFloat(args[1]);
				}
				Tester tester = new Tester();
				tester.doWaveBuilderTest(difficulty, tierLevel, lengthSeconds);
			} else if (args[0].equalsIgnoreCase("nexusstatus")) {
				if (mod_Invasion.getFocusNexus() != null)
					mod_Invasion.getFocusNexus().debugStatus();
			} else if (args[0].equalsIgnoreCase("bolt")) {
				if (mod_Invasion.getFocusNexus() != null) {
					int x = mod_Invasion.getFocusNexus().getXCoord();
					int y = mod_Invasion.getFocusNexus().getYCoord();
					int z = mod_Invasion.getFocusNexus().getZCoord();
					int time = 40;
					if (args.length >= 6)
						return;
					if (args.length >= 5)
						time = Integer.parseInt(args[4]);
					if (args.length >= 4)
						z += Integer.parseInt(args[3]);
					if (args.length >= 3)
						y += Integer.parseInt(args[2]);
					if (args.length >= 2) {
						x += Integer.parseInt(args[1]);
					}
					mod_Invasion.getFocusNexus().createBolt(x, y, z, time);
				}
			} else if (args[0].equalsIgnoreCase("status")) {
				sender.addChatMessage(new ChatComponentText("Nexus status: " + EnumChatFormatting.DARK_GREEN + mod_Invasion.getFocusNexus().isActive()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
			}else{
				sender.addChatMessage(new ChatComponentText("Command not recognized, use /invasion help for a list of all available commands").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			}
				
		} else {
			sendCommandHelp(sender);
		}
	}

	public String getCommandName() {
		return "invasion";
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return "";
	}
	
	public static void sendCommandHelp(ICommandSender sender) {
		sender.addChatMessage(new ChatComponentText("--- Showing Invasion help page 1 of 1 ---").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
		sender.addChatMessage(new ChatComponentText("/invasion begin x" + EnumChatFormatting.GRAY + " - start a wave").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
		sender.addChatMessage(new ChatComponentText("/invasion end" + EnumChatFormatting.GRAY + " - end the current invasion").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
		sender.addChatMessage(new ChatComponentText("/invasion range x" + EnumChatFormatting.GRAY + " - set the spawn range").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
	}
	
}