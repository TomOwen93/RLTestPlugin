package com.CoxMessage;

import com.google.inject.Provides;

import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = "CoxMessage"
)
public class CoxMessagePlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    ChatMessageManager chatMessageManager;

    @Inject
    private CoxMessageConfig config;

    private boolean chestLooted;
    private ItemContainer container;

    @Override
    protected void startUp() throws Exception {
        log.info("CoxMessage started!");
        chestLooted = false;
    }

    @Override
    protected void shutDown() throws Exception {
        log.info("CoxMessage stopped!");
    }

    @Subscribe
    public void onGameStateChanged(final GameStateChanged event){

        if(event.getGameState() == GameState.LOGGED_IN){
            List<String> messages = new ArrayList<>();
            messages.addAll(Text.fromCSV(config.customMessages()));
            messages.forEach(message -> log.info(message));
        }
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded widgetLoaded)
    {
        if (widgetLoaded.getGroupId() == InterfaceID.CHAMBERS_OF_XERIC_REWARD) {

            if (chestLooted) {
                return;
            }

            container = client.getItemContainer(InventoryID.CHAMBERS_OF_XERIC_CHEST);

            if (container != null) {

                final Collection<ItemStack> items = Arrays.stream(container.getItems())
                        .filter(item -> item.getId() > 0)
                        .map(item -> new ItemStack(item.getId(), item.getQuantity()))
                        .collect(Collectors.toList());

                List<String> messages = new ArrayList<>();

                messages.addAll(Text.fromCSV(config.customMessages()));

                messages.forEach(message -> log.info(message));

                if (!items.isEmpty()) {
                    for (String message : messages) {
                        ChatMessageBuilder chatMessageBuilder = new ChatMessageBuilder().append(message);

                        String chatMessage = chatMessageBuilder.build();

                        chatMessageManager.queue(QueuedMessage.builder()
                                .type(ChatMessageType.FRIENDSCHATNOTIFICATION)
                                .runeLiteFormattedMessage(chatMessage)
                                .build());
                    }
                }
            }
            chestLooted = true;
        }
    }

    @Provides
    CoxMessageConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CoxMessageConfig.class);
    }

}
