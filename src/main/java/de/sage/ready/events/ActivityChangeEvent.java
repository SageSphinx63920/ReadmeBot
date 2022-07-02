package de.sage.ready.events;

import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Arrays;

public class ActivityChangeEvent extends ListenerAdapter {

    public void onUserActivityStart(UserActivityStartEvent event){
        //Debug
        System.out.println("User: " + event.getUser().getName() + " changed activity to: " + event.getNewActivity().getName());
        System.out.println("Urls: " + event.getNewActivity().getUrl());
        System.out.println("Name: " + event.getNewActivity().asRichPresence().getName());
        System.out.println("State: " + event.getNewActivity().asRichPresence().getState());
        System.out.println("Details: " + event.getNewActivity().asRichPresence().getDetails());
        System.out.println("Url: " + event.getNewActivity().asRichPresence().getUrl());
        System.out.println("App ID: " + event.getNewActivity().asRichPresence().getApplicationId());
        System.out.println("Sync: " + event.getNewActivity().asRichPresence().getSyncId());
        System.out.println("Session: " + event.getNewActivity().asRichPresence().getSessionId());
        System.out.println("Time: " + event.getNewActivity().asRichPresence().getTimestamps());
        System.out.println("Flags: " + Arrays.toString(event.getNewActivity().asRichPresence().getFlagSet().parallelStream().toList().toArray()));

    }

}