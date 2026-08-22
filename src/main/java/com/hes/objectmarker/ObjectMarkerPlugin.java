package com.hes.objectmarker;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Object Marker",
	description = "Marks selected game objects with a simple text label",
	tags = {"object", "marker", "fire"}
)
public class ObjectMarkerPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ObjectMarkerOverlay overlay;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}
}
