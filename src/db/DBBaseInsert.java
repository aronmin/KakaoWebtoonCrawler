package db;

import db.base.DB;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DBInsert extends DB {
    public void contentInsert(String title, String img, String description, String director, String actor, String url, String tableName) throws SQLException {
        String sql = "INSERT INTO " + tableName + "(title, img, description, director, actor, url)" +
                "VALUES (?,?,?,?,?,?)";

        super.setStatement(super.getConnection().prepareStatement(sql));
        PreparedStatement state = super.getStatement();

        state.setString(1, title);
        state.setString(2, img);
        state.setString(3, description);
        state.setString(4, director);
        state.setString(5, actor);
        state.setString(6, url);

        state.executeUpdate();
    }

    public void genreInsert(int id, int genre_id, String tableName) throws SQLException {
        String sql = "INSERT INTO " + tableName + "(id, genre_id)" +
                "VALUES (?,?)";

        super.setStatement(super.getConnection().prepareStatement(sql));
        PreparedStatement state = super.getStatement();

        state.setInt(1, id);
        state.setInt(2, genre_id);

        state.executeUpdate();
    }

    private HashMap<String, Integer> allGenreID() throws SQLException {
        String sql = "SELECT * FROM genre";
        HashMap<String, Integer> result = new HashMap<>();

        super.setStatement(super.getConnection().prepareStatement(sql));
        PreparedStatement state = super.getStatement();
        ResultSet rs = state.executeQuery();

        while(rs.next()){
            result.put(rs.getString(2), rs.getInt(1));
        }

        return result;
    }

    public Integer searchGenreID(String genre) throws SQLException {
        HashMap<String, Integer> genreID = allGenreID();

        return genreID.get(genre);
    }

    public int searchMovieID(String tableName, String content_name) throws SQLException {
        String sql = "SELECT id FROM " + tableName + " where title=" + "\"" + content_name + "\"";

        super.setStatement(super.getConnection().prepareStatement(sql));
        PreparedStatement state = super.getStatement();
        ResultSet rs = state.executeQuery();

        int movie_id = 0;

        while(rs.next()){
            movie_id = rs.getInt(1);
        }

        return movie_id;
    }
}
