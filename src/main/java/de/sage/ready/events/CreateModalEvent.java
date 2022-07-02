package de.sage.ready.events;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.AllowedMentions;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import de.sage.ready.ReadmeBot;
import de.sage.ready.commans.CreateCommand;
import de.sage.ready.managment.ReadManager;
import de.sage.ready.sql.LiteSQL;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.awt.*;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class CreateModalEvent extends ListenerAdapter {

    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    public void onModalInteraction(ModalInteractionEvent event){
      if(event.getModalId().startsWith("news-message-create")){

          Member m = event.getMember();
          Guild g = event.getGuild();

          long logID = Long.parseLong(event.getModalId().split(",")[1].split(":")[1]);
          long sendID = Long.parseLong(event.getModalId().split(",")[0].split(":")[1]);
          CreateCommand.Type type = CreateCommand.Type.getType(event.getModalId().split(",")[2].split(":")[1]);

          String messageContent = event.getValue("message-body").getAsString();
          MessageChannel logChannel = (MessageChannel) event.getGuild().getGuildChannelById(logID);
          MessageChannel sendChannel = (MessageChannel) event.getGuild().getGuildChannelById(sendID);

          AtomicReference messageURL = new AtomicReference("");

          UUID uuid = UUID.randomUUID();

          //TODO : Try to fix the pings

          if(type.equals(CreateCommand.Type.WEBHOOK)){

              ResultSet getWebhook = LiteSQL.onQuery("SELECT webhookURL FROM link WHERE channelID = " + sendChannel.getIdLong());
              try {
                  AtomicReference<String> webhookURL = new AtomicReference<>("");

                  if(getWebhook.next()){
                      webhookURL.set(getWebhook.getString("webhookURL"));
                  }else {

                      Icon icon = null;

                      try {
                          icon = Icon.from(event.getJDA().getSelfUser().getAvatar().download().get());
                      } catch (IOException | ExecutionException | InterruptedException e) {
                          event.reply("Unexpected error occurred while trying to get the avatar of the bot.").queue();
                          throw new RuntimeException(e);
                      }

                      if(sendChannel.getType().equals(ChannelType.TEXT)){

                          ((TextChannel)sendChannel).createWebhook("Readme Bot")
                                  .setAvatar(icon)
                                  .queue(url -> webhookURL.set(url.getUrl()), failure -> {
                                      event.reply("Unexpected error occurred while trying to create a webhook.").queue();
                                      return;
                                  });

                      }else {
                          ((NewsChannel)sendChannel).createWebhook("Readme Bot")
                                  .setAvatar(icon)
                                  .queue(url -> webhookURL.set(url.getUrl()), failure -> {
                                      event.reply("Unexpected error occurred while trying to create a webhook.").queue();
                                      return;
                                  }); }
                  }


                  executor.schedule(() -> {

                      System.out.println("Sending message to webhook");

                      String url = ReadManager.generateURL();

                      WebhookClient client = WebhookClient.withUrl(webhookURL.get());

                      WebhookMessageBuilder builder = new WebhookMessageBuilder();
                      builder.setUsername(m.getEffectiveName()); // use the users nickname
                      builder.setAvatarUrl(m.getEffectiveAvatarUrl()); // use the user's guild avatar
                      builder.setAllowedMentions(AllowedMentions.all()); // We want to use all mentions
                      builder.setContent(messageContent + " \n \n" + url + " \n||*Please note: This link isn't any kind of logger! Its for statistic proposes.*||"); // use the content

                      client.send(builder.build()).thenAccept(message -> {
                          LiteSQL.onUpdate("INSERT INTO link (id, guildID, url, author, channelID, messageID, webhookURL) VALUES ('" + uuid + "'," + g.getIdLong() + ",'" + url + "'," + m.getIdLong() + ", " + message.getChannelId() + "," + message.getId() + ",'" + webhookURL + "')");
                          messageURL.set("https://discord.com/channels/" + g.getIdLong() +  "/"+ sendChannel.getIdLong() + "/" + message.getId());
                      });

                      System.out.println("Message sent to webhook");
                  }, 2500, TimeUnit.MILLISECONDS);





              } catch (SQLException e) {
                    event.reply("Unexpected error occurred while trying to create a webhook or inserting data into our database.").queue();
                  throw new RuntimeException(e);
              }

          }

          executor.schedule(() -> {
              logChannel.sendMessageEmbeds(new EmbedBuilder()
                      .setTitle("News Message Stats", "https://bots.sagesphinx63920.dev/readme")
                      .setAuthor(event.getMember().getEffectiveName(), null, event.getMember().getEffectiveAvatarUrl())
                      .setColor(Color.decode("#2c6d90"))
                      .setThumbnail(event.getGuild().getIconUrl())
                      .setTimestamp(new Date().toInstant())
                      .setFooter(ReadmeBot.footer, event.getJDA().getSelfUser().getEffectiveAvatarUrl())
                      .setDescription("This message was send by " + event.getMember().getAsMention() + " in " + sendChannel.getAsMention() + " as a **" + type.name() + "** message! \n \n" +
                              "**Views:** " + 0 + " \n" +
                              "**Last Viewed:** - \n \n" +
                              "(Jump to message)[" + messageURL + "]" + " *Note: This also counts as view* \n \n" +
                              "*Disclaimer: Dont use this feature to stalk or bother any other user! Only use it for statistical purposes. Any violation of these rules will result in a ban of the bot and its functions! *")

                      .build()).queue(success -> {
                  LiteSQL.onUpdate("INSERT INTO stats(messageID, guildID, channelID, linkID) VALUES (" + success.getIdLong() + "," + g.getIdLong() + "," + logChannel.getIdLong() + ",'" + uuid + "')");

              });
          }, 2750, TimeUnit.MILLISECONDS);



          //<t:" + System.currentTimeMillis() / 1000 + ":F>
                event.reply("Your message has been send into " + sendChannel.getAsMention() + "!").queue();

      }

    }

}
