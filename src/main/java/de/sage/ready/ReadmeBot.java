package de.sage.ready;

import de.sage.ready.commans.TestCommand;
import de.sage.ready.events.readyListener;
import de.sage.ready.managment.ReadManager;
import de.sage.ready.sql.LiteSQL;
import de.sage.ready.sql.TableManager;
import de.sage.ready.web.WebServer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ReadmeBot {


    //Global Variables
    public static ShardManager shardManager;
    public static DefaultShardManagerBuilder builder;

    public static final int shardCount = 1;
    public static final boolean testing = true;
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);

    public static final String prefix = "dev.";
    public static final String version = "pre-alpha 0.0.1";

    public static final ReadManager manager = new ReadManager();

    public static void main(String[] args) throws IOException {
        //SQL Connection
        LiteSQL.connect();
        TableManager.createTables();

        //Web server
        WebServer webServer = new WebServer();
        webServer.startServer();


        //Discord Bot Stuff
        builder = DefaultShardManagerBuilder.createDefault(Tokens.botToken);


        builder.setMemberCachePolicy(MemberCachePolicy.ALL);

        builder.enableIntents(Arrays.asList(GatewayIntent.values()));
        builder.enableCache(Arrays.asList(CacheFlag.values()));

        //Set Bot Start Status
        builder.setActivity(Activity.watching("your messages"));
        builder.setStatus(OnlineStatus.IDLE);

        //Intern JDA Settings
        builder.setAutoReconnect(true);

        // Event Listener
        builder.addEventListeners(new readyListener());
        builder.addEventListeners(new TestCommand());

        //Start JDA with Shards
        try {
            shardManager = builder.setShardsTotal(shardCount).build();
            System.out.println("+++++\nReadme is online \n+++++\n\n");
        } catch (Exception e) {
            System.out.println("-----\nERROR starting readme) \n-----\n\n Error code: \n" + e.getMessage());
        }

    }

    public static void registerSlashCommands(@NotNull JDA shard, boolean testing) {

        CommandListUpdateAction clua = shard.updateCommands();

        //Message Menu
        clua.addCommands(Commands.message("Convert to readme"));

        //User Menu

        //Slash Commands
        clua.addCommands(Commands.slash("test", "Command for intern testing"));
        clua.addCommands(Commands.slash("create", "Creates a new readme message").addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel the message should be send in", true).setChannelTypes(ChannelType.NEWS, ChannelType.TEXT), new OptionData(OptionType.CHANNEL, "log-channel", "The channel the statistics should be send in", true), new OptionData(OptionType.STRING, "mode", "The message mode", true).addChoices(new Command.Choice("webhook","webhook"), new Command.Choice("bot", "bot"))));

        clua.queue();
        System.out.println("Commands loaded!");

    }


}
