package de.sage.ready.events;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {

       if(event.isFromGuild()){

           //Do validations for stats
           if(!event.isWebhookMessage() && !event.getAuthor().isBot() && !event.getAuthor().isSystem()){

           }

       }

    }

}
