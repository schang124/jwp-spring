package next.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import next.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class QuestionDao {
	@Autowired
	private static QuestionDao questionDao;

	@Resource
	private JdbcTemplate jdbcTemplate;

    public Question insert(Question question) {
        String sql = "INSERT INTO QUESTIONS (writer, title, contents, createdDate) VALUES (?, ?, ?, ?)";
		KeyHolder keyholder = new GeneratedKeyHolder();
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(sql);
				ps.setString(1, question.getWriter());
				ps.setString(2, question.getTitle());
				ps.setString(3, question.getContents());
				ps.setTimestamp(4, new Timestamp(question.getTimeFromCreateDate()));
				return ps;
			}
		}, keyholder);
        
        return findById((Long)keyholder.getKey());
    }
	
	public List<Question> findAll() {
		String sql = "SELECT questionId, writer, title, createdDate, countOfAnswer FROM QUESTIONS "
				+ "order by questionId desc";
		
		RowMapper<Question> rm = new RowMapper<Question>() {

			@Override
			public Question mapRow(ResultSet resultSet, int i) throws SQLException {
				return new Question(resultSet.getLong("questionId"),
						resultSet.getString("writer"), resultSet.getString("title"), null,
						resultSet.getTimestamp("createdDate"),
						resultSet.getInt("countOfAnswer"));
			}
		};
		
		return jdbcTemplate.query(sql, rm);
	}

	public Question findById(long questionId) {
		String sql = "SELECT questionId, writer, title, contents, createdDate, countOfAnswer FROM QUESTIONS "
				+ "WHERE questionId = ?";
		
		RowMapper<Question> rm = new RowMapper<Question>() {
			@Override
			public Question mapRow(ResultSet resultSet, int i) throws SQLException {
				return new Question(resultSet.getLong("questionId"),
						resultSet.getString("writer"), resultSet.getString("title"),
						resultSet.getString("contents"),
						resultSet.getTimestamp("createdDate"),
						resultSet.getInt("countOfAnswer"));
			}
		};
		
		return jdbcTemplate.queryForObject(sql, rm, questionId);
	}

	public void update(Question question) {
		String sql = "UPDATE QUESTIONS set title = ?, contents = ? WHERE questionId = ?";
        jdbcTemplate.update(sql, 
        		question.getTitle(),
                question.getContents(),
                question.getQuestionId());
	}

	public void delete(long questionId) {
		String sql = "DELETE FROM QUESTIONS WHERE questionId = ?";
		jdbcTemplate.update(sql, questionId);
	}

	public void updateCountOfAnswer(long questionId) {
		String sql = "UPDATE QUESTIONS set countOfAnswer = countOfAnswer + 1 WHERE questionId = ?";
		jdbcTemplate.update(sql, questionId);
	}
}
