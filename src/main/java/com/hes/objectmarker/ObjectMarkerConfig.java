package com.hes.objectmarker;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup(ObjectMarkerConfig.GROUP)
public interface ObjectMarkerConfig extends Config
{
	String GROUP = "objectmarker";

	@ConfigItem(
		keyName = "labelBoxFill",
		name = "Label box fill",
		description = "Show a cyan box behind tagged object labels"
	)
	default boolean labelBoxFill()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "labelBoxOpacity",
		name = "Label box opacity",
		description = "Opacity of the cyan label box"
	)
	default int labelBoxOpacity()
	{
		return 85;
	}

	@ConfigItem(
		keyName = "markOtherPlayers",
		name = "Mark other players",
		description = "Highlight all other visible players"
	)
	default boolean markOtherPlayers()
	{
		return false;
	}

	@ConfigItem(
		keyName = "otherPlayerColor",
		name = "Player colour",
		description = "Colour used to highlight other players"
	)
	default Color otherPlayerColor()
	{
		return new Color(57, 255, 20);
	}
}
