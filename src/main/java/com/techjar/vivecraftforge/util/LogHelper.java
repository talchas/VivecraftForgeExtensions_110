package com.techjar.vivecraftforge.util;

import net.minecraftforge.fml.relauncher.FMLRelaunchLog;
import org.apache.logging.log4j.Level;

public class LogHelper {
	public static void info(String message, Object... data) {
		FMLRelaunchLog.log("Vivecraft Forge Extensions", Level.INFO, message, data);
	}

	public static void warning(String message, Object... data) {
		FMLRelaunchLog.log("Vivecraft Forge Extensions", Level.WARN, message, data);
	}

	public static void severe(String message, Object... data) {
		FMLRelaunchLog.log("Vivecraft Forge Extensions", Level.ERROR, message, data);
	}

	public static void debug(String message, Object... data) {
		FMLRelaunchLog.log("Vivecraft Forge Extensions", Level.INFO, "[DEBUG] " + message, data);
	}
}
