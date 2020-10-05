package ru.job4j.html;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private Connection conn;
    private static final Logger LOG = LoggerFactory.getLogger(PsqlStore.class);

    public PsqlStore() {
        init();
    }

    public void init() {
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            conn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement st = this.conn.prepareStatement("insert into post(name, text, link, created) values(?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, post.getPostName());
            st.setString(2, post.getPostText());
            st.setString(3, post.getPostLink());
            st.setTimestamp(4, Timestamp.valueOf(post.getPostDate()));
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                System.out.println(String.format("%s %s %s %s", rs.getInt("id"), rs.getString("name"), rs.getString("link"),
                        rs.getString("created")));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement st = this.conn.prepareStatement("select * from post")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Post post = new Post();
                post.setPostName(rs.getString("name"));
                post.setPostText(rs.getString("text"));
                post.setPostLink(rs.getString("link"));
                post.setPostDate(rs.getString("created"));
                posts.add(post);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return posts;
    }

    public Post findById(String id) {
        Post post = new Post();
        try (PreparedStatement st = this.conn.prepareStatement("select * from post where id=?")) {
            st.setInt(1, Integer.parseInt(id));
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                post.setPostName(rs.getString("name"));
                post.setPostText(rs.getString("text"));
                post.setPostLink(rs.getString("link"));
                post.setPostDate(rs.getString("created"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (conn != null) {
            conn.close();
        }
    }
}