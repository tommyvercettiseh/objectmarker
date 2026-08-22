package com.hes.objectmarker;

import java.awt.Color;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ObjectMarkerConfig.GROUP)
public interface ObjectMarkerConfig extends Config
{
	String GROUP = "objectmarker";

	@ConfigItem(
		keyName = "labelPosition",
		name = "Label position",
		description = "Choose where object labels are shown"
	)
	default LabelPosition labelPosition()
	{
		return LabelPosition.CENTER;
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
