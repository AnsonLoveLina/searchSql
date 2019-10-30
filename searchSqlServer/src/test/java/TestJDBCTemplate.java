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

    private static final String my_index_relation = "my_index_relation";

    private static final String my_index = "my_index1";

    String insertSql1 = "insert into " + my_index + " values(true,'1990-01-01 12:11:11',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    String insertSql2 = "insert into " + my_index + "(fieldDate,fieldKeyword,fieldText) values('1990-01-01 12:11:11','是的','中华人民共和国')";

    String insertSql3 = "insert into " + my_index + "(fieldDate,fieldText) values('1990-01-01','中华人民共和国')";

    String insertSql4 = "insert into " + my_index + "(fieldDate,fieldText) values('1990-01-01 12','中华人民共和国')";

    String insertSql5 = "insert into " + my_index + "(fieldBoolean,fieldDate,fieldDouble,fieldGeoPoin,fieldInteger,fieldKeyword,fieldLong,fieldText) values(true,'1990-01-01',11.1,'41.12,-71.34',1,'是的',123213,'中华人民共和国')";

    String updateSql1 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国'";

    String updateSql2 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国' where fieldKeyword='是的'";

    String updateSql3 = "update " + my_index + " set fieldDate='1990-01-01 12',fieldText='中华人民共和国' where q=query('中华')";

    String updateSql4 = "update " + my_index + " set fieldBoolean=false,fieldDate='1990-01-01 12',fieldDouble=1.11,fieldGeoPoin='41.12,-71.34',fieldInteger=2,fieldKeyword='是的',fieldLong=2320909,fieldText='中华人民共和国'";


    @Autowired
    @Qualifier("esJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private String sql = "select * from t_dsmanager_dataejj limit 0,10";

    @Test
    public void test() throws SQLException {
        List<MyIndexDynamic> result1 = jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<MyIndexDynamic>(MyIndexDynamic.class));
        System.out.println("result1 = " + result1);
        List<Map<String, Object>> result2 = jdbcTemplate.queryForList(sql);
        System.out.println("result = " + result2);
        jdbcTemplate.update(insertSql5);
    }
}
