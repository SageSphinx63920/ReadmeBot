package de.sage.ready.managment;

import de.sage.ready.commans.TestCommand;

import java.util.Random;
import java.util.UUID;

public class ReadManager {

    private static final String baseURL = "http://134.255.235.171:9841/read/news?id=";
    private static final Random sharedRandom = new Random();

    public ReadManager(){

    }

    public static /*Later non static*/  String generateURL(){
        String id = UUID.randomUUID().toString();

        return baseURL + id;
    }

    public void registerWebRequest(String id){

        System.out.println("RESGISTERING WEB REQUEST");

        TestCommand.editMessage();

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

    private void getGuildByWebRequest(String id){


    }

}
