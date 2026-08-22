package com.hes.objectmarker;

public enum MarkerType
{
	OBJECT("Object"),
	GROUND_ITEM("Ground item"),
	NPC("NPC"),
	PLAYER("Player");

	private final String displayName;

	MarkerType(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
