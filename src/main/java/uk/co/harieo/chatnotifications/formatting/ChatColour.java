package uk.co.harieo.chatnotifications.formatting;

import net.minecraft.util.text.TextFormatting;

public enum ChatColour {

	NONE(TextFormatting.RESET),
	YELLOW(TextFormatting.YELLOW),
	BLUE(TextFormatting.BLUE),
	RED(TextFormatting.RED),
	GREEN(TextFormatting.GREEN),
	PURPLE(TextFormatting.LIGHT_PURPLE);

	private TextFormatting match;

	ChatColour(TextFormatting match) {
		this.match = match;
	}

	public TextFormatting getMatchingFormatting() {
		return match;
	}
}
