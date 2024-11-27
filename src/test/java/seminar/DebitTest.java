package seminar;

import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import seminar.AbstractTest;
import seminar.DebitEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DebitTest extends AbstractTest {

    @Test
    void getDebitswhenValidshouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM debit";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        final Query query = getSession().createSQLQuery(sql).addEntity(DebitEntity.class);
        //then
        Assertions.assertEquals(0, countTableSize);
        Assertions.assertEquals(0, query.list().size());
    }

    @Test
    void shouldThrowExceptionWhenQueryContainsInvalidColumn() throws SQLException {
        // given
        String sql = "SELECT * FROM debit WHERE invalid_column = 1";
        Statement stmt = getConnection().createStatement();

        // when & then
        Assertions.assertThrows(SQLException.class, () -> {
            stmt.executeQuery(sql);
        });
    }

    @Test
    void shouldRollbackTransactionWhenErrorOccurs() throws SQLException {
        // given
        getConnection().setAutoCommit(false);
        Statement stmt = getConnection().createStatement();

        // when
        try {
            stmt.executeUpdate("INSERT INTO debit (debit_amount) VALUES (1000)");
            // Вызываем ошибку, например, вставка пустого значения
            stmt.executeUpdate("INSERT INTO debit (debit_amount) VALUES (NULL)");
            getConnection().commit();
        } catch (SQLException e) {
            getConnection().rollback();
        }

        // then
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM debit");
        rs.next();
        int count = rs.getInt(1);
        Assertions.assertEquals(0, count, "Запись не должна быть добавлена из-за ошибки.");
    }
}
