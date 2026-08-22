package com.hes.objectmarker;

import com.google.inject.Provides;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Object Marker",
	description = "Marks selected game objects and optionally highlights other players",
	tags = {"object", "marker", "highlight", "players"}
)
public class ObjectMarkerPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ObjectMarkerOverlay objectMarkerOverlay;

	@Inject
	private PlayerMarkerOverlay playerMarkerOverlay;

	@Provides
	ObjectMarkerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ObjectMarkerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(objectMarkerOverlay);
		overlayManager.add(playerMarkerOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(objectMarkerOverlay);
		overlayManager.remove(playerMarkerOverlay);
	}
}
