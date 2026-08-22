package com.hes.objectmarker;

import java.awt.Color;

public class MarkerDefinition
{
	private MarkerType type;
	private String match;
	private String label;
	private Integer targetId;
	private int rgb;
	private int opacity;
	private int padding;
	private boolean enabled;

	public MarkerDefinition()
	{
	}

	public MarkerDefinition(MarkerType type, String match, String label, Color color, int opacity, boolean enabled)
	{
		this(type, match, label, null, color, opacity, 0, enabled);
	}

	public MarkerDefinition(MarkerType type, String match, String label, Color color, int opacity, int padding, boolean enabled)
	{
		this(type, match, label, null, color, opacity, padding, enabled);
	}

	public MarkerDefinition(MarkerType type, String match, String label, Integer targetId, Color color, int opacity, int padding, boolean enabled)
	{
		this.type = type;
		this.match = match;
		this.label = label;
		this.targetId = targetId;
		this.rgb = color.getRGB();
		this.opacity = Math.max(0, Math.min(100, opacity));
		this.padding = Math.max(0, Math.min(100, padding));
		this.enabled = enabled;
	}

	public MarkerType getType()
	{
		return type;
	}

	public void setType(MarkerType type)
	{
		this.type = type;
	}

	public String getMatch()
	{
		return match;
	}

	public void setMatch(String match)
	{
		this.match = match;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public Integer getTargetId()
	{
		return targetId;
	}

	public void setTargetId(Integer targetId)
	{
		this.targetId = targetId;
	}

	public Color getColor()
	{
		return new Color(rgb, true);
	}

	public void setColor(Color color)
	{
		this.rgb = color.getRGB();
	}

	public int getOpacity()
	{
		return opacity;
	}

	public void setOpacity(int opacity)
	{
		this.opacity = Math.max(0, Math.min(100, opacity));
	}

	public int getPadding()
	{
		return padding;
	}

	public void setPadding(int padding)
	{
		this.padding = Math.max(0, Math.min(100, padding));
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public boolean matches(String name)
	{
		return matches(name, -1);
	}

	public boolean matches(String name, int id)
	{
		if (!enabled)
		{
			return false;
		}

		if (targetId != null && targetId >= 0)
		{
			return id == targetId;
		}

		if (name == null || match == null)
		{
			return false;
		}

		String trimmed = match.trim();
		return "*".equals(trimmed) || name.equalsIgnoreCase(trimmed);
	}
}
