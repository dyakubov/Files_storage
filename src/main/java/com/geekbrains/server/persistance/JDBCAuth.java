//package com.geekbrains.server.persistance;
//
//import com.geekbrains.common.messages.server.ServerAnswerType;
//import com.geekbrains.common.messages.server.ServerMessage;
//import com.geekbrains.server.auth.AuthInterface;
//
//import java.sql.*;
//
//public class JDBCAuth implements AuthInterface {
//
//    private final Connection conn;
//
//    public JDBCAuth() {
//
//
//    }
//
//    private void createTableIfNotExists(Connection conn) throws SQLException {
//        try (Statement stmt = conn.createStatement()) {
//            stmt.execute("create table if not exists users (\n" +
//                    "\tid int auto_increment primary key,\n" +
//                    "    login varchar(25),\n" +
//                    "    password varchar(25),\n" +
//                    "    unique index uq_login(login)\n" +
//                    ");");
//        }
//    }
//
//    @Override
//    public ServerMessage checkAuth(String login, String pass) {
//        return null;
//    }
//
//    @Override
//    public ServerMessage regUser(String login, String pass) throws SQLException {
//        try (PreparedStatement stmt = conn.prepareStatement(
//                "insert into users(login, password) values (?, ?);")) {
//            stmt.setString(1, login);
//            stmt.setString(2, pass);
//            stmt.execute();
//
//        } catch (SQLException e){
//            if (e.getErrorCode() == 1062){
//                return new ServerMessage(ServerAnswerType.USER_ALREADY_EXIST);
//            } else e.printStackTrace();
//        }
//        return new ServerMessage(ServerAnswerType.REG_OK);
//    }
//
//    @Override
//    public boolean userExist(String login) throws SQLException {
//        try(PreparedStatement stmt = conn.prepareStatement(
//                "select id, login, password from users where login = ?"a)){
//            stmt.setString(1, login);
//            ResultSet rs = stmt.executeQuery();
//            if (rs.next()){
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//}
