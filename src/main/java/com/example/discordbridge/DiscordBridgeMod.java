package com.example.discordbridge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.players.PlayerList;

@Mod(DiscordBridgeMod.MODID)
public class DiscordBridgeMod {
    public static final String MODID = "discordbridge";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordBridgeMod.class);
    private JDA jda;
    private MinecraftServer server;

    public DiscordBridgeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Placeholder for setup
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // Placeholder for client-side setup
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        server = event.getServer();
        try {
            jda = JDABuilder.createDefault("YOUR_DISCORD_BOT_TOKEN").build().awaitReady();
            jda.addEventListener(new DiscordListener());
        } catch (InterruptedException e) {
            LOGGER.error("Initialization interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize JDA", e);
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (jda != null) {
            jda.shutdown();
        }
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent event) {
        if (jda != null) {
            jda.getTextChannelById("YOUR_DISCORD_CHANNEL_ID")
                    .sendMessage(event.getUsername() + ": " + event.getMessage())
                    .queue();
        }
    }

    private class DiscordListener extends ListenerAdapter {
        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            if (event.getAuthor().isBot()) {
                return;
            }

            if (event.getChannel().getId().equals("YOUR_DISCORD_CHANNEL_ID")) {
                Component message = new TextComponent(event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
                PlayerList playerList = server.getPlayerList();
                playerList.broadcastMessage(message, net.minecraft.network.chat.ChatType.SYSTEM, UUID.randomUUID());
            }
        }
    }
}
