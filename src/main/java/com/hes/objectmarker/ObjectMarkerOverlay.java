package com.hes.objectmarker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.WorldView;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ObjectMarkerOverlay extends Overlay
{
	private static final Color CYAN = Color.CYAN;
	private static final String TARGET_NAME = "Fire";
	private static final String LABEL = "FIRE";

	private final Client client;

	@Inject
	public ObjectMarkerOverlay(Client client)
	{
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
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
					if (object == null)
					{
						continue;
					}

					ObjectComposition composition = client.getObjectDefinition(object.getId());
					if (composition == null || !TARGET_NAME.equalsIgnoreCase(composition.getName()))
					{
						continue;
					}

					Point location = object.getCanvasTextLocation(graphics, LABEL, 40);
					if (location != null)
					{
						OverlayUtil.renderTextLocation(graphics, location, LABEL, CYAN);
					}
				}
			}
		}

		return null;
	}
}
