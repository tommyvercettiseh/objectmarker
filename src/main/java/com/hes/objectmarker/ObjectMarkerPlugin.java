package com.hes.objectmarker;

import com.google.inject.Provides;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.NPC;
import net.runelite.api.ObjectComposition;
import net.runelite.api.Player;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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
	private Client client;

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

	private final Set<String> addedMenuTargets = new HashSet<>();
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
		addedMenuTargets.clear();
		panel = null;
		navigationButton = null;
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		addedMenuTargets.clear();
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		MenuEntry entry = event.getMenuEntry();
		MenuAction action = entry.getType();

		if (isObjectAction(action))
		{
			int id = entry.getIdentifier();
			ObjectComposition composition = client.getObjectDefinition(id);
			if (composition != null)
			{
				addMarkMenu(MarkerType.OBJECT, id, composition.getName());
			}
			return;
		}

		if (isGroundItemAction(action))
		{
			int id = entry.getIdentifier();
			ItemComposition composition = client.getItemDefinition(id);
			if (composition != null)
			{
				addMarkMenu(MarkerType.GROUND_ITEM, id, composition.getName());
			}
			return;
		}

		NPC npc = entry.getNpc();
		if (npc != null && isNpcAction(action))
		{
			addMarkMenu(MarkerType.NPC, npc.getId(), npc.getName());
			return;
		}

		Player player = entry.getPlayer();
		if (player != null && isPlayerAction(action))
		{
			addMarkMenu(MarkerType.PLAYER, null, player.getName());
		}
	}

	private void addMarkMenu(MarkerType type, Integer id, String name)
	{
		if (name == null || name.trim().isEmpty())
		{
			return;
		}

		String key = type.name() + ":" + (id == null ? name : id);
		if (!addedMenuTargets.add(key))
		{
			return;
		}

		client.createMenuEntry(-1)
			.setOption("Mark this")
			.setTarget(name)
			.setType(MenuAction.RUNELITE)
			.onClick(e ->
			{
				markerStore.add(new MarkerDefinition(
					type,
					name,
					"",
					id,
					Color.CYAN,
					40,
					0,
					true));

				if (panel != null)
				{
					SwingUtilities.invokeLater(panel::rebuild);
				}
			});
	}

	private boolean isObjectAction(MenuAction action)
	{
		return action == MenuAction.GAME_OBJECT_FIRST_OPTION
			|| action == MenuAction.GAME_OBJECT_SECOND_OPTION
			|| action == MenuAction.GAME_OBJECT_THIRD_OPTION
			|| action == MenuAction.GAME_OBJECT_FOURTH_OPTION
			|| action == MenuAction.GAME_OBJECT_FIFTH_OPTION
			|| action == MenuAction.EXAMINE_OBJECT;
	}

	private boolean isGroundItemAction(MenuAction action)
	{
		return action == MenuAction.GROUND_ITEM_FIRST_OPTION
			|| action == MenuAction.GROUND_ITEM_SECOND_OPTION
			|| action == MenuAction.GROUND_ITEM_THIRD_OPTION
			|| action == MenuAction.GROUND_ITEM_FOURTH_OPTION
			|| action == MenuAction.GROUND_ITEM_FIFTH_OPTION
			|| action == MenuAction.EXAMINE_ITEM_GROUND;
	}

	private boolean isNpcAction(MenuAction action)
	{
		return action == MenuAction.NPC_FIRST_OPTION
			|| action == MenuAction.NPC_SECOND_OPTION
			|| action == MenuAction.NPC_THIRD_OPTION
			|| action == MenuAction.NPC_FOURTH_OPTION
			|| action == MenuAction.NPC_FIFTH_OPTION
			|| action == MenuAction.EXAMINE_NPC;
	}

	private boolean isPlayerAction(MenuAction action)
	{
		return action == MenuAction.PLAYER_FIRST_OPTION
			|| action == MenuAction.PLAYER_SECOND_OPTION
			|| action == MenuAction.PLAYER_THIRD_OPTION
			|| action == MenuAction.PLAYER_FOURTH_OPTION
			|| action == MenuAction.PLAYER_FIFTH_OPTION
			|| action == MenuAction.PLAYER_SIXTH_OPTION
			|| action == MenuAction.PLAYER_SEVENTH_OPTION
			|| action == MenuAction.PLAYER_EIGHTH_OPTION;
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
