package uk.co.harieo.chatnotifications;

import java.util.ArrayList;
import java.util.List;
import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.*;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.utils.Material;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import uk.co.harieo.chatnotifications.events.ForgeMessageReceive;
import uk.co.harieo.chatnotifications.formatting.ChatColour;

public class ChatNotifications extends LabyModAddon {

	private static ChatNotifications instance;
	private List<String> tags = new ArrayList<>();
	private boolean isCaseSensitive = false;
	private boolean isMuted = false;
	private ChatColour selectedColour = ChatColour.YELLOW;
	// Volume is accepted as an integer out of 100, or a whole percentage, which then is divided by 100 to get the float
	private float volume = 0.5F;
	private int rawVolume = 50;

	@Override
	public void onEnable() {
		instance = this;

		MinecraftForge.EVENT_BUS.register(new ForgeMessageReceive());
	}

	@Override
	public void loadConfig() {
		if (getConfig().has("tags")) {
			tags.clear(); // Just to be safe
			parseTags(getConfig().get("tags").getAsString());
		}

		if (tags.isEmpty()) { // Either the config wasn't available or it wasn't valid
			tags.add(getApi().getPlayerUsername()); // Default value for invalid config
		}

		// Both default to false
		this.isMuted = getConfig().has("muted") && getConfig().get("muted").getAsBoolean();
		this.isCaseSensitive = getConfig().has("caseSensitivity") && getConfig().get("caseSensitivity").getAsBoolean();

		if (getConfig().has("volume")) {
			this.rawVolume = getConfig().get("volume").getAsInt(); // For display matters as decimals are ugly
			setVolume(rawVolume);
		}
	}

	@Override
	protected void fillSettings(List<SettingsElement> list) {
		StringElement tagsElement = new StringElement("Tag List", this, new IconData(Material.PAPER),
				"tags", serializeCurrentTags());
		tagsElement.setDescriptionText("Separate each tag with a comma (e.g Tag1, Tag2, Tag3)");

		BooleanElement caseSensitiveElement = new BooleanElement("Use Case Sensitivity?", this,
				new ControlElement.IconData(Material.LEVER), "caseSensitivity", this.isCaseSensitive);

		SliderElement volumeElement = new SliderElement("Ping Volume", this, new IconData(Material.EXP_BOTTLE),
				"volume", rawVolume);
		volumeElement.setRange(1, 100);
		volumeElement.setSteps(5);

		DropDownMenu<ChatColour> colourDropDownMenu = new DropDownMenu<ChatColour>(
				"Tag Formatting Colour", 0, 0, 0, 0)
				.fill(ChatColour.values());
		DropDownElement<ChatColour> colourDropDown = new DropDownElement<>("Tag Formatting Colour",
				colourDropDownMenu);
		colourDropDownMenu.setSelected(ChatColour.YELLOW);
		colourDropDown.setCallback(colour -> this.selectedColour = colour);

		BooleanElement mutedElement = new BooleanElement("Mute Addon", this,
				new ControlElement.IconData(Material.BARRIER), "muted", this.isMuted);

		list.add(colourDropDown);
		list.add(tagsElement);
		list.add(caseSensitiveElement);
		list.add(volumeElement);
		list.add(mutedElement);
	}

	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public TextFormatting getSelectedFormatting() {
		return selectedColour.getMatchingFormatting();
	}

	public ChatColour getSelectedColour() {
		return selectedColour;
	}

	private void setVolume(int percentage) {
		this.volume = percentage / 100F;
	}

	public float getVolume() {
		return volume;
	}

	private void parseTags(String trimmedString) {
		List<String> tempTags = new ArrayList<>(); // If this fails, we don't want to ruin the perfect values

		String[] splitFull = trimmedString.split(", "); // Users are prone to putting spaces after commas
		for (String tag : splitFull) {
			if (!tag.contains(",")) { // If it still contains a comma, the value is illegal or concise
				tempTags.add(tag.trim());
			}
		}

		String[] splitConcise = trimmedString.split(","); // Concise means without spaces, e.g "," not ", "
		for (String tag : splitConcise) {
			if (tag.startsWith(" ")) {
				tag = tag.replaceFirst(" ", "");
			}

			if (!tempTags.contains(tag) && !tag.isEmpty()) { // Make sure this isn't being confused with the full split
				tempTags.add(tag.trim()); // Accepted value
			}
		}

		if (!tempTags.isEmpty()) { // If we received no values, the config was likely illegal
			tags.clear(); // Old values have been overwritten or replaced
			tags.addAll(tempTags); // Add newly parsed values
		}
	}

	private String serializeCurrentTags() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < tags.size(); i++) {
			builder.append(tags.get(i));
			if (i + 1 < tags.size()) { // If there is another tag to go
				builder.append(", "); // Separate with a comma
			}
		}
		return builder.toString();
	}

	public List<String> getTags() {
		return tags;
	}

	public static ChatNotifications getInstance() {
		return instance;
	}
}
