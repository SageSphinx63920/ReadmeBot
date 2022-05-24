package de.sage.ready.commans;

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class NewsMessageSend extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getChannelType().equals(ChannelType.TEXT)){
            if(CreateCommand.channelMap.containsValue(event.getAuthor())){
                if(CreateCommand.channelMap.containsKey(event.getTextChannel())){

                    CreateCommand.channelMap.remove(event.getTextChannel(), event.getAuthor());

                    CreateCommand.NewsMessage message = CreateCommand.messageMap.get(event.getAuthor());

                    message.setMessageAndSend(event.getMessage().getContentRaw().strip());

                    CreateCommand.messageMap.remove(event.getAuthor());

                }
            }
        }
    }

}
