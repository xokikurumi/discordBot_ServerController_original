package org.example.db;

import java.sql.*;

public class Database {
    public static final String CONNECT_STRING = "jdbc:mysql://localhost:3306/discord?characterEncoding=utf8&useSSL=false&serverTimezone=GMT%2B9&rewriteBatchedStatements=true&allowPublicKeyRetrieval=true";
    public static final String USERID = "root";
    public static final String PASSWORD = "root";
    public static Connection con = null;
    public static PreparedStatement ps = null;
    public static ResultSet rs = null;


    /***********************************************
     * SELECT系
     ***********************************************/
    public static boolean executeCheckId(String sql){

        boolean result = false;

        try {
            con = DriverManager.getConnection(CONNECT_STRING, USERID, PASSWORD);

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            String id = "";

            while(rs.next()) {
                id = rs.getString("id");
            }
//            System.out.println(id);
//            System.out.println(sql);
            result = !id.isEmpty();

            rs.close();
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        return result;
    }

    public static String[] executeCheckIdStr(String sql){

        String[] result = {"","","",""};

        try {
            con = DriverManager.getConnection(CONNECT_STRING, USERID, PASSWORD);

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            String id = "";

            while(rs.next()) {
                result[0] = rs.getString("id");
                result[1] = rs.getString("latitude");
                result[2] = rs.getString("longitude");
                result[3] = rs.getString("quake_time");

            }

//            result = !id.isEmpty();

            rs.close();
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        return result;
    }


    public static void save(String query){
        try {
            con = DriverManager.getConnection(CONNECT_STRING, USERID, PASSWORD);

            PreparedStatement ps = con.prepareStatement(query);

            ps.executeUpdate();
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    public static String[] executeTsunamiCheckIdTStr(String sql){

        String[] result = {"", ""};

        try {
            con = DriverManager.getConnection(CONNECT_STRING, USERID, PASSWORD);

            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();
            String id = "";

            while(rs.next()) {
                result[0] = rs.getString("id");
                result[1] = rs.getString("tsunami_time");

            }

//            result = !id.isEmpty();

            rs.close();
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }

        return result;
    }
}
