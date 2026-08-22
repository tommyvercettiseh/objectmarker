package com.hes.objectmarker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class PlayerMarkerOverlay extends Overlay
{
	private static final int FILL_ALPHA = 90;

	private final Client client;
	private final ObjectMarkerConfig config;

	@Inject
	public PlayerMarkerOverlay(Client client, ObjectMarkerConfig config)
	{
		this.client = client;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.markOtherPlayers())
		{
			return null;
		}

		Player localPlayer = client.getLocalPlayer();
		Color color = config.otherPlayerColor();
		Color fill = new Color(color.getRed(), color.getGreen(), color.getBlue(), FILL_ALPHA);

		for (Player player : client.getPlayers())
		{
			if (player == null || player == localPlayer)
			{
				continue;
			}

			Shape hull = player.getConvexHull();
			if (hull == null)
			{
				continue;
			}

			graphics.setColor(fill);
			graphics.fill(hull);

			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(hull);
		}

		return null;
	}
}
