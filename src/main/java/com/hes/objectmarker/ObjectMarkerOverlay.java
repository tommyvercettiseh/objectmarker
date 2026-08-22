package com.hes.objectmarker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
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
	private static final Color LABEL_COLOR = Color.CYAN;
	private static final Color BOX_TEXT_COLOR = Color.BLACK;
	private static final int TEXT_HEIGHT = 40;
	private static final int BOX_PADDING_X = 4;
	private static final int BOX_PADDING_Y = 2;

	private final Client client;
	private final ConfigManager configManager;
	private final ObjectMarkerConfig config;

	@Inject
	public ObjectMarkerOverlay(Client client, ConfigManager configManager, ObjectMarkerConfig config)
	{
		this.client = client;
		this.configManager = configManager;
		this.config = config;
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
		Point location = object.getCanvasTextLocation(graphics, label, TEXT_HEIGHT);
		if (location == null)
		{
			return;
		}

		if (config.labelBoxFill())
		{
			renderLabelBox(graphics, location, label);
			OverlayUtil.renderTextLocation(graphics, location, label, BOX_TEXT_COLOR);
		}
		else
		{
			OverlayUtil.renderTextLocation(graphics, location, label, LABEL_COLOR);
		}
	}

	private void renderLabelBox(Graphics2D graphics, Point location, String label)
	{
		FontMetrics metrics = graphics.getFontMetrics();
		int width = metrics.stringWidth(label) + (BOX_PADDING_X * 2);
		int height = metrics.getHeight() + (BOX_PADDING_Y * 2);
		int x = location.getX() - BOX_PADDING_X;
		int y = location.getY() - metrics.getAscent() - BOX_PADDING_Y;
		int alpha = Math.round(255f * config.labelBoxOpacity() / 100f);

		graphics.setColor(new Color(0, 255, 255, alpha));
		graphics.fillRect(x, y, width, height);
	}

	private Set<String> loadNames()
	{
		Set<String> names = new LinkedHashSet<>();
		String saved = configManager.getConfiguration(
			ObjectMarkerPlugin.CONFIG_GROUP,
			ObjectMarkerPlugin.TAGGED_NAMES_KEY
		);

		if (saved == null)
		{
			return names;
		}

		for (String line : saved.split("\\R"))
		{
			String name = line.trim();
			if (!name.isEmpty())
			{
				names.add(name);
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

		for (String taggedName : names)
		{
			if (taggedName.equalsIgnoreCase(name))
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
