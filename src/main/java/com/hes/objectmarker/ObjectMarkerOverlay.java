package com.hes.objectmarker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ObjectMarkerOverlay extends Overlay
{
	private static final Color CYAN = Color.CYAN;

	private final Client client;
	private final ConfigManager configManager;

	@Inject
	public ObjectMarkerOverlay(Client client, ConfigManager configManager)
	{
		this.client = client;
		this.configManager = configManager;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Set<String> taggedNames = loadNames();
		if (taggedNames.isEmpty())
		{
			return null;
		}

		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return null;
		}

		Scene scene = worldView.getScene();
		Tile[][][] tiles = scene.getTiles();
		int plane = worldView.getPlane();

		if (plane < 0 || plane >= tiles.length)
		{
			return null;
		}

		for (Tile[] column : tiles[plane])
		{
			if (column == null)
			{
				continue;
			}

			for (Tile tile : column)
			{
				if (tile == null)
				{
					continue;
				}

				for (GameObject object : tile.getGameObjects())
				{
					renderObject(graphics, object, taggedNames);
				}

				renderObject(graphics, tile.getGroundObject(), taggedNames);
				renderObject(graphics, tile.getWallObject(), taggedNames);
				renderObject(graphics, tile.getDecorativeObject(), taggedNames);
			}
		}

		return null;
	}

	private void renderObject(Graphics2D graphics, TileObject object, Set<String> taggedNames)
	{
		if (object == null)
		{
			return;
		}

		ObjectComposition composition = client.getObjectDefinition(object.getId());
		if (composition == null || !containsIgnoreCase(taggedNames, composition.getName()))
		{
			return;
		}

		String label = labelFor(composition.getName());
		Point location = object.getCanvasTextLocation(graphics, label, 40);
		if (location != null)
		{
			OverlayUtil.renderTextLocation(graphics, location, label, CYAN);
		}
	}

	private Set<String> loadNames()
	{
		Set<String> names = new LinkedHashSet<>();
		String saved = configManager.getConfiguration(ObjectMarkerPlugin.CONFIG_GROUP, ObjectMarkerPlugin.TAGGED_NAMES_KEY);
		if (saved != null)
		{
			for (String line : saved.split("\\R"))
			{
				String trimmed = line.trim();
				if (!trimmed.isEmpty())
				{
					names.add(trimmed);
				}
			}
		}
		return names;
	}

	private boolean containsIgnoreCase(Set<String> names, String name)
	{
		if (name == null)
		{
			return false;
		}

		for (String value : names)
		{
			if (value.equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}

	private String labelFor(String name)
	{
		if (ObjectMarkerPlugin.DEFAULT_NAME.equalsIgnoreCase(name))
		{
			return "CAMPFIRE";
		}
		return name.toUpperCase();
	}
}
