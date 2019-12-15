package uk.co.harieo.chatnotifications.events;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.harieo.chatnotifications.ChatNotifications;

public class ForgeMessageReceive {

	private ResourceLocation resourceLocation;

	public ForgeMessageReceive() {
		// Adds the location of the sound which is EXP orb pickup from Vanilla
		resourceLocation = new ResourceLocation("minecraft", "entity.experience_orb.pickup");
	}

	@SubscribeEvent
	public void onMessageReceive(ServerChatEvent event) {
		ChatNotifications core = ChatNotifications.getInstance();
		if (event.getPlayer().getUniqueID().equals(core.getApi().getPlayerUUID()) || core.isMuted()) {
			return; // Don't scan messages sent by the user
		}

		String message = event.getMessage();
		for (String tag : core.getTags()) { // Loop through all tags to scan for
			if (core.isCaseSensitive() && message.contains(tag)) { // Case sensitive scanning
				pingAndFormat(core, event, tag);
			} else if (!core.isCaseSensitive() && message.toLowerCase().contains(tag.toLowerCase())) { // Not case sensitive scanning
				String lowercaseMessage = message.toLowerCase();
				// Uses substring to compensate for the improper case validation, or else all messages would become
				// lower case regardless of their actual casing
				int tagStart = lowercaseMessage.indexOf(tag.toLowerCase());
				int tagFinish = tagStart + tag.length();
				pingAndFormat(core, event, message.substring(tagStart, tagFinish));
			}
		}
	}

	/**
	 * Assuming that all data has been already verified, pings the player and formats the chat based on their requested
	 * settings from {@link ChatNotifications} core
	 *
	 * @param core to retrieve user settings from
	 * @param event which the message came from
	 * @param tag to be formatted, if applicable
	 */
	private void pingAndFormat(ChatNotifications core, ServerChatEvent event, String tag) {
		FMLClientHandler.instance().getClientPlayerEntity()
				.playSound(new SoundEvent(resourceLocation), core.getVolume(), 1);

		// Make sure that formatting isn't disabled in any way
		if (core.getSelectedFormatting() != TextFormatting.RESET) {
			// Send a new message with the detected tag in a formatted colour
			event.setComponent(new TextComponentString(event.getComponent().getFormattedText()
					.replace(tag, core.getSelectedFormatting() + tag + TextFormatting.RESET)));
		}
	}

}
