package com.hes.objectmarker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ObjectMarkerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ObjectMarkerPlugin.class);
		RuneLite.main(args);
	}
}
