package com.hes.objectmarker;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Object Marker",
	description = "Live markers for objects, ground items, NPCs and players",
	tags = {"object", "marker", "highlight", "players", "npc", "ground"}
)
public class ObjectMarkerPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ObjectMarkerOverlay objectMarkerOverlay;

	@Inject
	private PlayerMarkerOverlay playerMarkerOverlay;

	@Inject
	private MarkerStore markerStore;

	private ObjectMarkerPanel panel;
	private NavigationButton navigationButton;

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

		panel = new ObjectMarkerPanel(markerStore);
		navigationButton = NavigationButton.builder()
			.tooltip("Object Marker")
			.icon(createIcon())
			.priority(5)
			.panel(panel)
			.build();
		clientToolbar.addNavigation(navigationButton);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(objectMarkerOverlay);
		overlayManager.remove(playerMarkerOverlay);
		if (navigationButton != null)
		{
			clientToolbar.removeNavigation(navigationButton);
		}
		panel = null;
		navigationButton = null;
	}

	private BufferedImage createIcon()
	{
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(new Color(255, 140, 0));
		graphics.fillOval(2, 2, 12, 12);
		graphics.setColor(Color.WHITE);
		graphics.drawOval(4, 4, 8, 8);
		graphics.dispose();
		return image;
	}
}
