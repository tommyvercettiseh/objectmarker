package com.hes.objectmarker;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup(ObjectMarkerConfig.GROUP)
public interface ObjectMarkerConfig extends Config
{
	String GROUP = "objectmarker";

	@ConfigSection(
		name = "Player Marker",
		description = "Settings for highlighting other players",
		position = 0
	)
	String playerMarkerSection = "playerMarker";

	@ConfigItem(
		keyName = "markOtherPlayers",
		name = "Mark other players",
		description = "Highlight all other visible players",
		section = playerMarkerSection,
		position = 0
	)
	default boolean markOtherPlayers()
	{
		return false;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "otherPlayerOpacity",
		name = "Player opacity",
		description = "Fill opacity used to highlight other players",
		section = playerMarkerSection,
		position = 1
	)
	default int otherPlayerOpacity()
	{
		return 35;
	}

	@ConfigItem(
		keyName = "otherPlayerColor",
		name = "Player colour",
		description = "Colour used to highlight other players",
		section = playerMarkerSection,
		position = 2
	)
	default Color otherPlayerColor()
	{
		return new Color(57, 255, 20);
	}

	@ConfigSection(
		name = "Object Marker",
		description = "Settings for tagged object labels",
		position = 1
	)
	String objectMarkerSection = "objectMarker";

	@ConfigItem(
		keyName = "labelBoxFill",
		name = "Label box fill",
		description = "Show a black box behind tagged object labels",
		section = objectMarkerSection,
		position = 0
	)
	default boolean labelBoxFill()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "labelBoxOpacity",
		name = "Label box opacity",
		description = "Opacity of the black label box",
		section = objectMarkerSection,
		position = 1
	)
	default int labelBoxOpacity()
	{
		return 85;
	}
}
