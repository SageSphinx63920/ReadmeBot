package de.sage.ready.commans;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.apache.commons.collections4.map.HashedMap;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class CreateCommand extends ListenerAdapter {

    public static HashMap<TextChannel, User> channelMap = new HashMap<>();
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public static HashMap<User, NewsMessage> messageMap = new HashMap<>();

    @SuppressWarnings("unused")
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("create")) {
            if(event.getChannelType().equals(ChannelType.TEXT)) {
                Member m = event.getMember();
                Guild g = event.getGuild();

                TextChannel sendChannel = event.getOptions().get(0).getAsTextChannel();
                TextChannel logChannel = event.getOptions().get(1).getAsTextChannel();

                Type type = Type.getType(event.getOptions().get(2).getAsString());

                if(type == null) {
                    event.reply("Invalid type! Report this to the developer: \"CreateCommand Illegal Type\"").setEphemeral(true).queue();
                    return;
                }

                //event.reply("Please send the news message in this channel! The next message from you in this channel within 15 seconds will be the message, which will be send in " + sendChannel.getAsMention() + "! *Note: I cant use the `@here` or `@everyone` ping, in the message itself do to discord limitations. I'll send those pings as bot message after the readme message! Thx :)* ").setEphemeral(true).queue();

               // channelMap.put(event.getTextChannel(), m.getUser());
                //messageMap.put(m.getUser(), new NewsMessage(sendChannel, logChannel, type));

                TextInput body = TextInput.create("message-body", "Message content", TextInputStyle.PARAGRAPH)
                        .setMaxLength(4000)
                        .setPlaceholder("Here goes your message you want to track. You can also use any kind of ping!")
                        .setRequired(true)
                        .build();

                Modal modal = Modal.create("news-message-create;news:" + sendChannel.getIdLong() + ",log:" + logChannel.getIdLong() + ",mode:" + type.name(), "Readme Message")
                        .addActionRow(body)
                                .build();

                event.replyModal(modal).queue();

                /*executor.schedule(() -> {
                    if(channelMap.containsKey(event.getTextChannel())) {
                        channelMap.remove(event.getTextChannel());
                        messageMap.remove(m.getUser());
                        event.getTextChannel().sendMessage("You didn't send a message in time! Cancelling the creation of the news message!").queue();
                    }
                }, 15000, java.util.concurrent.TimeUnit.MILLISECONDS);*/

            }else
                event.reply("This command is only available in text channels!").setEphemeral(true).queue();
        }
    }

    public enum Type {

        WEBHOOK, BOT, USER;

        public static Type getType(String type) {
            return switch (type.toLowerCase()) {
                case "webhook" -> WEBHOOK;
                case "bot" -> BOT;
                case "user" -> USER;
                default -> null;
            };
        }
    }

    public class NewsMessage {
        private TextChannel logChannel;
        private TextChannel sendChannel;

        private String message;
        private Type type;

        UUID uuid = UUID.randomUUID();

        public NewsMessage(TextChannel log, TextChannel send, Type t) {
            logChannel = log;
            sendChannel = send;
            type = t;
        }

        public void setMessageAndSend(String message) {
            this.message = message;

            sendNews();
        }

        private void sendNews(){

        }

    }
}
