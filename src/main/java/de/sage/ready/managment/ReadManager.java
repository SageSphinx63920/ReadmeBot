package de.sage.ready.managment;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.receive.ReadonlyMessage;
import de.sage.ready.ReadmeBot;
import de.sage.ready.commans.TestCommand;
import de.sage.ready.sql.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static de.sage.ready.sql.LiteSQL.onQuery;

public class ReadManager {

    private static final String baseURL = "https://bots.sagesphinx63920.dev/readme/api/v1/read/news?id=";
    private static final Random sharedRandom = new Random();
    public static final String fileName = "test.png";
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public ReadManager(){

    }

    public static @NotNull String generateURL(){

        while (true){
            String uuid = UUID.randomUUID().toString();
            ResultSet check = LiteSQL.onQuery("SELECT * FROM link WHERE url = '" + uuid + "'");
            try {
                if(!check.next()){
                    return baseURL + uuid;
                }
            } catch (SQLException e) {e.printStackTrace();}
        }

    }

    public void registerWebRequest(String id){

        //System.out.println("REGISTERING WEB REQUEST");

        editMessage(baseURL + id);
       /* ReadmeBot.shardManager.getTextChannelById(900173747074498610L).sendMessageEmbeds(new EmbedBuilder()
                .setTitle("New Web Request", "http://134.255.235.171:9841/read/news?id=" + id)
                .setColor(Color.decode("#2b77b1"))
                .setAuthor("*Soon the creator")
                .setTimestamp(new Date().toInstant())
                .setDescription("Someone read your news message in *PLACEHOLDER* \n \n" +
                        "*Soon the creator*" + "\n \n" +
                        "*Maybe who*" + "\n \n" +
                        "**Time:** <t:" + System.currentTimeMillis() / 1000 + ":F> \n \n" +
                        "Soon more information about the news message")
                .setFooter("Readme version " + ReadmeBot.version)
                .build()).queue();*/

    }

    private static void editMessage(String url) {
        ResultSet getData = LiteSQL.onQuery("SELECT * FROM link WHERE url = '" + url + "'");

        try {
            if (getData.next()) {
                String uuid = getData.getString("id");
                int views = getData.getInt("views");
                String webhookURL = getData.getString("webhookURL");
                long messageID = getData.getLong("messageID");
                long guildID = getData.getLong("guildID");

                Guild guild = ReadmeBot.shardManager.getGuildById(guildID);

                //No URL = bot message
                if (url.equalsIgnoreCase("")) {

                    //TODO : Impl Bot messages

                } else {
                    executor.schedule(() -> {
                        WebhookClient client = WebhookClient.withUrl(webhookURL);

                        ReadonlyMessage message = client.get(messageID).join();
                        String newURL = ReadManager.generateURL();

                        client.edit(messageID, message.getContent().substring(0, (message.getContent().length() - (88 + baseURL.length() + 36))) + " \n \n " + newURL + " \n||*Please note: This link isn't any kind of logger! Its for statistic proposes.*||").thenRun(new Runnable() {
                            @Override
                            public void run() {
                                LiteSQL.onUpdate("UPDATE link SET views = " + (views + 1) + " WHERE id = '" + uuid + "'");
                                LiteSQL.onUpdate("UPDATE link SET url = '" + newURL + "' WHERE id = '" + uuid + "'");
                                updateStatsMes(guild, uuid, views + 1);
                                System.out.println("Updated message");
                            }
                        });
                    }, 15, TimeUnit.SECONDS);

                }

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static void updateStatsMes(Guild guild, String uuid, int views) {
        ResultSet getData = LiteSQL.onQuery("SELECT * FROM stats WHERE linkID = '" + uuid + "'");

        try {
            if (getData.next()) {
                long messageID = getData.getLong("messageID");
                long channelID = getData.getLong("channelID");

                MessageChannel channel = (MessageChannel) guild.getGuildChannelById(channelID);
                Message message = channel.getIterableHistory().stream().filter(mes -> mes.getIdLong() == messageID).toList().get(0);

                EmbedBuilder builder = new EmbedBuilder(message.getEmbeds().get(0));
                String description = builder.getDescriptionBuilder().toString();

                channel.editMessageEmbedsById(messageID, builder
                                .setTimestamp(new Date().toInstant())
                                .setDescription(replaceNewStats(description, views, System.currentTimeMillis() / 1000))
                        .build()).queue();

                }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Fuck this shit
    //Nvm

    private static String replaceNewStats(String description, int views, long time){

        StringBuilder builder = new StringBuilder();

        for(String line : description.lines().toList()){
            if(line.contains("Last Viewed:")){
                builder.append("**Last Viewed:** <t:" + time + ":F>");
            } else if (line.contains("Views:")){
                builder.append("**Views:** " + views);
            }else
                builder.append(line);

            builder.append("\n");
            }

        return builder.toString().trim();
    }

    private void getGuildByWebRequest(String id){


    }

}
