package de.sage.ready.commans;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.sage.ready.ReadmeBot;
import de.sage.ready.managment.ReadManager;
import de.sage.ready.sql.LiteSQL;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.sage.ready.sql.LiteSQL.onQuery;

public class TestCommand extends ListenerAdapter {

    static UUID uuid = UUID.randomUUID();
    static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    @SuppressWarnings("unused")
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("test")) {
            if (event.getChannelType().equals(ChannelType.TEXT)) {
                Member m = event.getMember();
                Guild g = event.getGuild();

                TextChannel channel = event.getTextChannel();

                Icon icon = null;

                try {
                    icon = Icon.from(event.getJDA().getSelfUser().getAvatar().download().get());
                } catch (IOException | ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }


                channel.createWebhook("News Webhook")
                        .setAvatar(icon)
                        .queue(hook -> {
                            club.minnced.discord.webhook.WebhookClient client = WebhookClient.withUrl(hook.getUrl());

                            WebhookMessageBuilder builder = new WebhookMessageBuilder();
                            builder.setUsername(m.getEffectiveName()); // use this username
                            builder.setAvatarUrl(m.getEffectiveAvatarUrl()); // use this avatar
                            builder.setContent("News: Klexi ist dumm. Wait... doch keine News " + ReadManager.generateURL() + "");

                            client.send(builder.build()).thenAccept(message -> {
                                LiteSQL.onUpdate("INSERT INTO link (id, guildID, url, author, channelID, messageID, webhookURL) VALUES ('" + uuid + "'," + g.getIdLong() + ",'" + ReadManager.generateURL() + "'," + m.getIdLong() + ", " + message.getChannelId() + "," + message.getId() + ",'" + hook.getUrl() + "')");
                                LiteSQL.onUpdate("INSERT INTO stats(messageID, guildID, channelID, linkID) VALUES (978006304960446554," + g.getIdLong() + "," + message.getChannelId() + ",'" + uuid + "')");
                            });
                        });


            } else
                event.reply("This command is only available in text channels!").setEphemeral(true).queue();
        }
    }

    public static void editMessage() {
        ResultSet getData = LiteSQL.onQuery("SELECT * FROM link WHERE id = '" + uuid + "'");

        try {
            if (getData.next()) {
                int views = getData.getInt("views");
                String url = getData.getString("webhookURL");
                long messageID = getData.getLong("messageID");
                String uuid = getData.getString("id");
                long guildID = getData.getLong("guildID");
                long channelID = getData.getLong("channelID");

                Guild guild = ReadmeBot.shardManager.getGuildById(guildID);
                TextChannel channel = guild.getTextChannelById(channelID);

                //No URL = bot message
                if (url.equalsIgnoreCase("")) {

                    //TODO : Impl Bot messages

                } else {
                    executor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            WebhookClient client = WebhookClient.withUrl(url);

                            ReadonlyMessage message = client.get(messageID).join();
                            String newURL = ReadManager.generateURL();

                            client.edit(messageID, message.getContent().substring(0, message.getContent().length() - 77) + " " + newURL + "").thenAccept(update -> {
                                LiteSQL.onUpdate("UPDATE link SET views = " + (views + 1) + " AND url = '" + newURL + "' WHERE id = '" + uuid + "'");
                                updateStatsMes(channel, uuid, views + 1);
                            });
                        }
                    }, 15, TimeUnit.SECONDS);

                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void updateStatsMes(TextChannel channel, String id, int views) {
        ResultSet getData = onQuery("SELECT * FROM stats WHERE linkID = '" + id + "'");

        try {
            if (getData.next()) {
                long messageID = getData.getLong("messageID");

                channel.editMessageById(messageID, "Views: " + views).queue();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
