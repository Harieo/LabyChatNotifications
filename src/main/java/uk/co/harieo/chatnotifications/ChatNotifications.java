package uk.co.harieo.chatnotifications;

import java.util.ArrayList;
import java.util.List;
import net.labymod.api.LabyModAddon;
import net.labymod.api.events.MessageReceiveEvent;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.StringElement;
import net.labymod.utils.Material;

public class ChatNotifications extends LabyModAddon {

	private List<String> tags = new ArrayList<>();
	private boolean isEnabled = true;

	@Override
	public void onEnable() {
		getApi().getEventManager().register((s, s1) -> false);
	}

	@Override
	public void loadConfig() {

	}

	@Override
	protected void fillSettings(List<SettingsElement> list) {
		BooleanElement enabledElement = new BooleanElement("Enabled",
				new ControlElement.IconData(Material.REDSTONE_LAMP_ON), this::setEnabled, true);
		StringElement tagsElement = new StringElement("Tag List", new IconData(Material.PAPER),
				getApi().getPlayerUsername(), newValue -> parseTags(newValue.trim()));

		list.add(enabledElement);
		list.add(tagsElement);
	}

	private void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	private void parseTags(String trimmedString) {
		List<String> tempTags = new ArrayList<>(); // If this fails, we don't want to ruin the perfect values

		String[] splitConcise = trimmedString.split(","); // Concise means without spaces, e.g "," not ", "
		for (String tag : splitConcise) {
			if (!tag.contains(",")) { // If the tag contains a comma, concise split was likely not applicable
				tempTags.add(tag); // Accepted value
			}
		}

		String[] splitFull = trimmedString.split(", "); // Users are prone to putting spaces after commas
		for (String tag : splitFull) {
			if (!tag.contains(",")) { // If it still contains a comma, the value is illegal or concise
				tempTags.add(tag);
			}
		}

		if (!tempTags.isEmpty()) { // If we received no values, the config was likely illegal
			tags.clear(); // Old values have been overwritten or replaced
			tags.addAll(tempTags); // Add newly parsed values
		}
	}

}
