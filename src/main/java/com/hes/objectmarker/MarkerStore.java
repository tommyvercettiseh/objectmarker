package com.hes.objectmarker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.config.ConfigManager;

@Singleton
public class MarkerStore
{
	private static final String CONFIG_GROUP = "objectmarker";
	private static final String CONFIG_KEY = "sidebarMarkers";
	private static final Type LIST_TYPE = new TypeToken<ArrayList<MarkerDefinition>>() { }.getType();

	private final ConfigManager configManager;
	private final Gson gson;
	private final List<MarkerDefinition> markers = new ArrayList<>();

	@Inject
	public MarkerStore(ConfigManager configManager, Gson gson)
	{
		this.configManager = configManager;
		this.gson = gson;
		load();
	}

	public List<MarkerDefinition> getMarkers()
	{
		return Collections.unmodifiableList(markers);
	}

	public void add(MarkerDefinition marker)
	{
		markers.add(marker);
		save();
	}

	public void upsert(MarkerDefinition marker)
	{
		for (int i = 0; i < markers.size(); i++)
		{
			MarkerDefinition existing = markers.get(i);
			if (existing.getType() == marker.getType()
				&& existing.getTargetId() != null
				&& existing.getTargetId().equals(marker.getTargetId()))
			{
				marker.setColor(existing.getColor());
				marker.setOpacity(existing.getOpacity());
				marker.setPadding(existing.getPadding());
				marker.setLabel(existing.getLabel());
				markers.set(i, marker);
				save();
				return;
			}
		}

		markers.add(marker);
		save();
	}

	public void remove(MarkerDefinition marker)
	{
		markers.remove(marker);
		save();
	}

	public void save()
	{
		configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, gson.toJson(markers));
	}

	private void load()
	{
		markers.clear();
		String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY);
		if (json != null && !json.trim().isEmpty())
		{
			List<MarkerDefinition> loaded = gson.fromJson(json, LIST_TYPE);
			if (loaded != null)
			{
				markers.addAll(loaded);
			}
		}

		if (markers.isEmpty())
		{
			markers.add(new MarkerDefinition(
				MarkerType.OBJECT,
				"Forester's Campfire",
				"CAMPFIRE",
				new Color(255, 140, 0),
				40,
				true));
			save();
		}
	}
}
