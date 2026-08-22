package com.hes.objectmarker;

import com.google.inject.Provides;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.KeyCode;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Object Marker",
	description = "Tag object names in-game and optionally highlight other players",
	tags = {"object", "marker", "fire", "players"}
)
public class ObjectMarkerPlugin extends Plugin
{
	static final String CONFIG_GROUP = "objectmarker";
	static final String TAGGED_NAMES_KEY = "taggedNames";
	static final String DEFAULT_NAME = "Forester's Campfire";

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

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
		if (configManager.getConfiguration(CONFIG_GROUP, TAGGED_NAMES_KEY) == null)
		{
			configManager.setConfiguration(CONFIG_GROUP, TAGGED_NAMES_KEY, DEFAULT_NAME);
		}

		overlayManager.add(objectMarkerOverlay);
		overlayManager.add(playerMarkerOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(objectMarkerOverlay);
		overlayManager.remove(playerMarkerOverlay);
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		if (!client.isKeyPressed(KeyCode.KC_SHIFT))
		{
			return;
		}

		MenuEntry entry = event.getMenuEntry();
		if (entry.getType() != MenuAction.EXAMINE_OBJECT)
		{
			return;
		}

		String name = cleanName(entry.getTarget());
		if (name.isEmpty())
		{
			return;
		}

		Set<String> names = loadNames();
		boolean tagged = containsIgnoreCase(names, name);

		client.createMenuEntry(-1)
			.setOption(tagged ? "Untag name" : "Tag name")
			.setTarget(name)
			.setType(MenuAction.RUNELITE)
			.onClick(e -> toggleName(name));
	}

	private void toggleName(String name)
	{
		Set<String> names = loadNames();
		String existing = findIgnoreCase(names, name);
		if (existing != null)
		{
			names.remove(existing);
		}
		else
		{
			names.add(name);
		}
		configManager.setConfiguration(CONFIG_GROUP, TAGGED_NAMES_KEY, String.join("\n", names));
	}

	private Set<String> loadNames()
	{
		Set<String> names = new LinkedHashSet<>();
		String saved = configManager.getConfiguration(CONFIG_GROUP, TAGGED_NAMES_KEY);
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
		return findIgnoreCase(names, name) != null;
	}

	private String findIgnoreCase(Set<String> names, String name)
	{
		for (String value : names)
		{
			if (value.equalsIgnoreCase(name))
			{
				return value;
			}
		}
		return null;
	}

	private String cleanName(String target)
	{
		if (target == null)
		{
			return "";
		}
		return target.replaceAll("<[^>]*>", "").trim();
	}
}
