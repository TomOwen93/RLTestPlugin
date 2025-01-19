package com.CoxMessage;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface CoxMessageConfig extends Config
{
	@ConfigItem(
		keyName = "custom_message",
		name = "Custom Cox Message",
		description = "The message to show to the user when they get a drop at Chambers of Xeric. Use comma separated messages to add more messages."
	)
	default String customMessages()
	{
		return "Congrats you received some items: ";
	}
}
