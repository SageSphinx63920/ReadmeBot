package de.sage.ready.sql;

public class TableManager {

    public static void createTables(){
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS link(id VARCHAR PRIMARY KEY, guildID INTEGER, url VARCHAR UNIQUE, author INTEGER, views INTEGER DEFAULT 0, channelID INTEGER, messageID INTEGER, webhookURL VARCHAR DEFAULT '')");
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS stats(messageID INTEGER PRIMARY KEY, guildID INTEGER, channelID INTEGER, linkID VARCHAR, FOREIGN KEY(linkID) REFERENCES link(id))");
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS blocks(id VARCHAR PRIMARY KEY, reason VARCHAR, type INTEGER, mod INTEGER, time INTEGER)");
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS messages(messageId VARCHAR PRIMARY KEY, guildID INTEGER, channelID INTEGER, author INTEGER, time INTEGER, deleted INTEGER DEFAULT 0)");
       LiteSQL.onUpdate("CREATE TABLE IF NOT EXISTS joins(userId VARCHAR PRIMARY KEY, guildID INTEGER, invitor INTEGER, left BOOLEAN DEFAULT 0, time INTEGER)");
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

    //Use this to call the methods above
    public static void main(String... args){

    }

}
