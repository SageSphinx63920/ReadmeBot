package de.sage.ready.commans;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EmptyCommand extends ListenerAdapter {

    @SuppressWarnings("unused")
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if(event.getName().equalsIgnoreCase("COMMAND")) {
            if(event.getChannelType().equals(ChannelType.TEXT)) {
                Member m = event.getMember();
                Guild g = event.getGuild();


            }else
                event.reply("This command is only available in text channels!").setEphemeral(true).queue();
        }
    }
}
