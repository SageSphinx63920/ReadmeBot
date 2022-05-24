package de.sage.ready.sql;

import java.io.File;
import java.sql.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 *
 *  @author SageSphinx63920
 *
 *  Copyright (c) 2019 - 2022 by SageSphinx63920 to present. All rights reserved
 *
 */
public class LiteSQL {

    private static Connection connection;

    private static Statement statement;

    public static void connect() {
        connection = null;
        try {
            File file = new File("database.db");
            if (!file.exists())
                file.createNewFile();

            String url = "jdbc:sqlite:" + file.getPath();
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();

        } catch (SQLException | java.io.IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isConnected() {
        return (connection != null);
    }

    public static void disconnect() {
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void onUpdate(final String query) {
        if(isConnected()) {
            new FutureTask(new Runnable() {
                PreparedStatement preparedStatement;

                public void run() {
                    try {
                        this.preparedStatement = connection.prepareStatement(query);
                        this.preparedStatement.executeUpdate();
                        this.preparedStatement.close();
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                    }
                }
            }, 1).run();
        }
    }

    public static ResultSet onQuery(final String query) {
        if (isConnected()) {
            try {
                FutureTask<ResultSet> task = new FutureTask<>(new Callable<ResultSet>() {
                    PreparedStatement ps;

                    public ResultSet call() throws Exception {
                        this.ps = connection.prepareStatement(query);
                        return this.ps.executeQuery();
                    }
                });
                task.run();
                return task.get();
            } catch (InterruptedException| ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            connect();
        }
        return null;
    }



}