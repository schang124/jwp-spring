package next.dao;

import java.sql.*;
import java.util.List;

import next.model.Answer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class AnswerDao {

    @Autowired
	private static AnswerDao answerDao;

    @Resource
	private JdbcTemplate jdbcTemplate;

    public Answer insert(Answer answer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO ANSWERS (writer, contents, createdDate, questionId) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                ps.setString(1, answer.getWriter());
                ps.setString(2, answer.getContents());
                ps.setTimestamp(3, new Timestamp(answer.getTimeFromCreateDate()));
                ps.setLong(4, answer.getQuestionId());
                return ps;
            }
        }, keyHolder);
        return findById((Long)keyHolder.getKey());
    }

    public Answer findById(long answerId) {
        String sql = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE answerId = ?";

        RowMapper<Answer> rm = new RowMapper<Answer>() {

            @Override
            public Answer mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Answer(resultSet.getLong("answerId"), resultSet.getString("writer"), resultSet.getString("contents"),
                        resultSet.getTimestamp("createdDate"), resultSet.getLong("questionId"));
            }
        };

        return jdbcTemplate.queryForObject(sql, rm, answerId);
    }

    public List<Answer> findAllByQuestionId(long questionId) {
        String sql = "SELECT answerId, writer, contents, createdDate FROM ANSWERS WHERE questionId = ? "
                + "order by answerId desc";

        RowMapper<Answer> rm = new RowMapper<Answer>() {
            @Override
            public Answer mapRow(ResultSet resultSet, int i) throws SQLException {
                return new Answer(resultSet.getLong("answerId"), resultSet.getString("writer"), resultSet.getString("contents"),
                        resultSet.getTimestamp("createdDate"), questionId);
            }

        };

        return jdbcTemplate.query(sql, rm, questionId);
    }

	public void delete(Long answerId) {
        String sql = "DELETE FROM ANSWERS WHERE answerId = ?";
        jdbcTemplate.update(sql, answerId);
	}
}
