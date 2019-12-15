package uk.co.harieo.chatnotifications.formatting;

import net.minecraft.util.text.TextFormatting;

/**
 * A less resource-intensive enum due to an issue with feeding {@link TextFormatting} into a LabyMod element
 */
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

	/**
	 * @return the instance of {@link TextFormatting} which represents the selected colour
	 */
	public TextFormatting getMatchingFormatting() {
		return match;
	}
}
