package uk.co.harieo.chatnotifications.events;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.harieo.chatnotifications.ChatNotifications;

public class ForgeMessageReceive {

	private ResourceLocation resourceLocation;

	public ForgeMessageReceive() {
		resourceLocation = new ResourceLocation("minecraft", "entity.experience_orb.pickup");
	}

	@SubscribeEvent
	public void onMessageReceive(ServerChatEvent event) {
		ChatNotifications core = ChatNotifications.getInstance();
		if (event.getPlayer().getUniqueID().equals(core.getApi().getPlayerUUID())) {
			return; // Don't scan messages sent by the user
		}

		String message = event.getMessage();

		for (String tag : core.getTags()) {
			if (message.contains(tag)) {
				FMLClientHandler.instance().getClientPlayerEntity()
						.playSound(new SoundEvent(resourceLocation), core.getVolume(), 1);

				// Make sure that formatting isn't disabled in any way
				if (core.getSelectedFormatting() != TextFormatting.RESET) {
					// Send a new message with the detected tag in a formatted colour
					core.getApi().displayMessageInChat(message.replace(tag,
							core.getSelectedFormatting() + tag + TextFormatting.RESET));
				}

				event.setCanceled(true);
			}
		}
	}

}
