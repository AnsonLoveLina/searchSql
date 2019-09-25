import com.google.common.collect.Maps;
import com.ngw.App;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 * Created by zy-xx on 2019/9/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {App.class})// 指定启动类
public class TestJDBCTemplate {

    @Autowired
    @Qualifier("esJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private String sql = "select * from my_index_relation limit 0,10";

    @Test
    public void test() throws SQLException {
        List<MyIndexDynamic> result1 = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<MyIndexDynamic>(MyIndexDynamic.class));
        System.out.println("result1 = " + result1);
        List<Map<String, Object>> result2 = jdbcTemplate.queryForList(sql);
        System.out.println("result = " + result2);
    }
}
