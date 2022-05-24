package de.sage.ready.sql;

public class TableManager {

    public static void createTables(){
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS link(id VARCHAR PRIMARY KEY, guildID INTEGER, url VARCHAR UNIQUE, author INTEGER, views INTEGER DEFAULT 0, channelID INTEGER, messageID INTEGER, webhookURL VARCHAR DEFAULT '')");
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS stats(messageID INTEGER PRIMARY KEY, guildID INTEGER, channelID INTEGER, linkID VARCHAR, FOREIGN KEY(linkID) REFERENCES link(id))");
    }

    //Method to clear all tables
    public static void clearTables(){
        LiteSQL.onUpdate("DELETE FROM link");
        LiteSQL.onUpdate("DELETE FROM stats");
    }

    //Method to drop all tables
    public static void dropTables(){
        LiteSQL.onUpdate("DROP TABLE link");
        LiteSQL.onUpdate("DROP TABLE stats");
    }

}
