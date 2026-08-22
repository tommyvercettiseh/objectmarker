package com.hes.objectmarker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemLayer;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.runelite.api.WorldView;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ObjectMarkerOverlay extends Overlay
{
	private final Client client;
	private final MarkerStore markerStore;

	@Inject
	public ObjectMarkerOverlay(Client client, MarkerStore markerStore)
	{
		this.client = client;
		this.markerStore = markerStore;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		renderSceneMarkers(graphics);
		renderNpcMarkers(graphics);
		renderPlayerMarkers(graphics);
		return null;
	}

	private void renderSceneMarkers(Graphics2D graphics)
	{
		WorldView worldView = client.getTopLevelWorldView();
		if (worldView == null)
		{
			return;
		}

		Scene scene = worldView.getScene();
		Tile[][][] tiles = scene.getTiles();
		int plane = worldView.getPlane();
		if (plane < 0 || plane >= tiles.length)
		{
			return;
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
					if (object != null)
					{
						renderObject(graphics, object);
					}
				}

				renderObject(graphics, tile.getWallObject());
				renderObject(graphics, tile.getDecorativeObject());
				renderObject(graphics, tile.getGroundObject());

				for (TileItem item : tile.getGroundItems())
				{
					renderGroundItem(graphics, tile, item);
				}
			}
		}
	}

	private void renderObject(Graphics2D graphics, TileObject object)
	{
		if (object == null)
		{
			return;
		}

		ObjectComposition composition = client.getObjectDefinition(object.getId());
		if (composition != null)
		{
			renderTileObject(graphics, object, composition.getName(), MarkerType.OBJECT);
		}
	}

	private void renderGroundItem(Graphics2D graphics, Tile tile, TileItem item)
	{
		if (item == null)
		{
			return;
		}

		ItemComposition composition = client.getItemDefinition(item.getId());
		if (composition == null)
		{
			return;
		}

		for (MarkerDefinition marker : markerStore.getMarkers())
		{
			if (marker.getType() != MarkerType.GROUND_ITEM || !marker.matches(composition.getName()))
			{
				continue;
			}

			ItemLayer itemLayer = tile.getItemLayer();
			int height = itemLayer == null ? 0 : itemLayer.getHeight();
			Shape shape = Perspective.getCanvasTilePoly(client, tile.getLocalLocation(), height);
			renderShape(graphics, shape, marker);

			String label = marker.getLabel();
			if (label != null && !label.trim().isEmpty())
			{
				Point location = Perspective.getCanvasTextLocation(client, graphics, tile.getLocalLocation(), label, height + 20);
				if (location != null)
				{
					OverlayUtil.renderTextLocation(graphics, location, label, marker.getColor());
				}
			}
		}
	}

	private void renderTileObject(Graphics2D graphics, TileObject object, String name, MarkerType type)
	{
		for (MarkerDefinition marker : markerStore.getMarkers())
		{
			if (marker.getType() != type || !marker.matches(name))
			{
				continue;
			}

			Shape shape = object.getClickbox();
			if (shape == null)
			{
				shape = object.getCanvasTilePoly();
			}
			renderShape(graphics, shape, marker);
			renderLabel(graphics, object, marker);
		}
	}

	private void renderNpcMarkers(Graphics2D graphics)
	{
		for (NPC npc : client.getNpcs())
		{
			if (npc == null)
			{
				continue;
			}

			for (MarkerDefinition marker : markerStore.getMarkers())
			{
				if (marker.getType() != MarkerType.NPC || !marker.matches(npc.getName()))
				{
					continue;
				}

				Shape shape = npc.getConvexHull();
				if (shape == null)
				{
					shape = npc.getCanvasTilePoly();
				}
				renderShape(graphics, shape, marker);
				renderLabel(graphics, npc, marker);
			}
		}
	}

	private void renderPlayerMarkers(Graphics2D graphics)
	{
		Player local = client.getLocalPlayer();
		for (Player player : client.getPlayers())
		{
			if (player == null || player == local)
			{
				continue;
			}

			for (MarkerDefinition marker : markerStore.getMarkers())
			{
				if (marker.getType() != MarkerType.PLAYER || !marker.matches(player.getName()))
				{
					continue;
				}

				Shape shape = player.getConvexHull();
				if (shape == null)
				{
					shape = player.getCanvasTilePoly();
				}
				renderShape(graphics, shape, marker);
				renderLabel(graphics, player, marker);
			}
		}
	}

	private void renderShape(Graphics2D graphics, Shape shape, MarkerDefinition marker)
	{
		if (shape == null)
		{
			return;
		}

		Color color = marker.getColor();
		int alpha = Math.round(255f * marker.getOpacity() / 100f);
		if (alpha > 0)
		{
			graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
			graphics.fill(shape);
		}

		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 255));
		graphics.draw(shape);
	}

	private void renderLabel(Graphics2D graphics, TileObject object, MarkerDefinition marker)
	{
		String label = marker.getLabel();
		if (label == null || label.trim().isEmpty())
		{
			return;
		}

		Point location = object.getCanvasTextLocation(graphics, label, 40);
		if (location != null)
		{
			OverlayUtil.renderTextLocation(graphics, location, label, marker.getColor());
		}
	}

	private void renderLabel(Graphics2D graphics, Actor actor, MarkerDefinition marker)
	{
		String label = marker.getLabel();
		if (label == null || label.trim().isEmpty())
		{
			return;
		}

		Point location = actor.getCanvasTextLocation(graphics, label, actor.getLogicalHeight() + 20);
		if (location != null)
		{
			OverlayUtil.renderTextLocation(graphics, location, label, marker.getColor());
		}
	}
}
