package de.sage.ready.events;

import de.sage.ready.ReadmeBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @author SageSphinx63920
 * <p>
 * Copyright (c) 2019 - 2022 by SageSphinx63920 to present. All rights reserved
 */
public class readyListener extends ListenerAdapter {

	/*
		Placeholder:
	@GloGuilds = Alle Server
	@ShGuilds = Shard Guilds
	@ShId = ID von Shard
	@ShTo = Alle Shards
	@GloPing = Avarage Ping
	@ShIdUntil = Shard Id / Total Ids

	   Online Modes:
	@Online = Default, online(green)
	@Offline = Offline(gray)
	@Idle = Idle, (yellow;moon)
	@DnD = Do not disturb(red)

		Status:
	@Play = Playing
	@Watch = Watching
	@List = Listening to
	@Comp = Compating in
	@Stream (link) =  Streaming; Ignores online mode (purple); Link = Streaming link

	*/

    private final List<String> stats = Arrays.asList("@Idle your messages!");
    private final Random random = new Random();
    private boolean started = false;
    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);
    int startedShards = 0;

    public void onReady(ReadyEvent event) {

        StringBuilder out = new StringBuilder("\nThe Shard " + (event.getJDA().getShardInfo().getShardId() + 1) + "/" + ReadmeBot.shardManager.getShardsTotal() + " handle following servers: \n");

        for (Guild g : event.getJDA().getGuilds()) {
            try {
                out.append(g.getName()).append(" (").append(g.getId()).append(") \n ").append(g.getOwner().getUser().getAsTag()).append(" (").append(g.getOwnerIdLong()).append(") \n \n");
            } catch (Exception ex) {
                out.append(g.getName()).append(" (").append(g.getId()).append(") \n Error while reading data! \n \n");
            }

        }

        System.out.println(out.toString());

        startSwitch(event.getJDA());
        ReadmeBot.registerSlashCommands(event.getJDA(), true);

    }

    private void switchActivity(List<JDA> jdas) {

        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i <= (ReadmeBot.shardCount - 1); i++) {
                    updateStatus(jdas.get(i));

                   //TODO : Impl Top.gg
                }

            }
        }, 5, 15, TimeUnit.SECONDS);

    }

    private void updateStatus(JDA jda) {
        String RandomStatus = stats.get(random.nextInt(stats.size()));
        String firstReplaceStatus = RandomStatus.replace("@GloGuilds", ReadmeBot.shardManager.getGuilds().size() + "").replace("@ShId", (jda.getShardInfo().getShardId() + 1) + "").replace("@ShGuilds", jda.getGuildCache().size() + "").replace("@ShTo", ReadmeBot.shardManager.getShards().size() + "").replace("@GloPing", String.valueOf((int) ((ReadmeBot.shardManager.getAverageGatewayPing() - (ReadmeBot.shardManager.getAverageGatewayPing() % 10)) / 10)).replace(".0", "")).replace("@ShIdUntil", jda.getShardInfo().getShardId() + "/" + ReadmeBot.shardCount);

        OnlineStatus status = OnlineStatus.IDLE;
        Activity.ActivityType activityType = Activity.ActivityType.WATCHING;
        String streamingUrl = "";

        if (firstReplaceStatus.contains("@Online")) {
            status = OnlineStatus.ONLINE;
        } else if (firstReplaceStatus.contains("@Offline")) {
            status = OnlineStatus.OFFLINE;
        } else if (firstReplaceStatus.contains("@Idle")) {
            status = OnlineStatus.IDLE;
        } else if (firstReplaceStatus.contains("@DnD")) {
            status = OnlineStatus.DO_NOT_DISTURB;
        }

        if (firstReplaceStatus.contains("@Play")) {
            activityType = Activity.ActivityType.PLAYING;
        } else if (firstReplaceStatus.contains("@Watch")) {
            activityType = Activity.ActivityType.WATCHING;
        } else if (firstReplaceStatus.contains("@List")) {
            activityType = Activity.ActivityType.LISTENING;
        } else if (firstReplaceStatus.contains("@Comp")) {
            activityType = Activity.ActivityType.COMPETING;
        } else if (firstReplaceStatus.contains("@Stream")) {
            activityType = Activity.ActivityType.STREAMING;
            streamingUrl = firstReplaceStatus.substring(firstReplaceStatus.indexOf("@Stream") + 8, firstReplaceStatus.length());
        }

        String replcePlaceHolders = firstReplaceStatus.replace(streamingUrl, "").replace("@Online", "").replace("@Offline", "").replace("@Idle", "").replace("@DnD", "").replace("@Play", "").replace("@Watch", "").replace("@List", "").replace("@Comp", "").replace("@Stream", "");

        jda.getPresence().setStatus(status);
        jda.getPresence().setActivity(Activity.of(activityType, replcePlaceHolders, streamingUrl));
    }

    private void startSwitch(JDA jda) {

        startedShards = startedShards++;

        if (startedShards == ReadmeBot.shardManager.getShardsTotal()) {
            started = true;

            switchActivity(ReadmeBot.shardManager.getShards());
        }

    }

}