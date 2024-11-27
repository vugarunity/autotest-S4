package seminar;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreditTest extends AbstractTest {

    @Test
    @Order(1)
    void getCredits_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM credit";
        Statement stmt = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        String hql = "FROM CreditEntity";
        Query<CreditEntity> query = getSession().createQuery(hql, CreditEntity.class);
        //then
        Assertions.assertEquals(1, countTableSize);
        Assertions.assertEquals(1, query.list().size());
    }

    @Test
    @Order(2)
    void shouldUpdateCreditWhenValidDataIsProvided() throws SQLException {
        // given
        String sql = "UPDATE credit SET balance = 1000 WHERE credit_id = 1";
        Statement stmt = getConnection().createStatement();

        // when
        int rowsAffected = stmt.executeUpdate(sql);

        // then
        Assertions.assertEquals(1, rowsAffected);

        ResultSet rs = stmt.executeQuery("SELECT balance FROM credit WHERE credit_id = 1");
        rs.next();
        int updatedDebitAmount = rs.getInt("balance");
        Assertions.assertEquals(1000, updatedDebitAmount);
    }

    @Test
    @Order(3)
    void shouldReturnCreditsForSpecificCondition() throws SQLException {
        // given
        String sql = "SELECT * FROM credit WHERE balance > 100";
        Statement stmt = getConnection().createStatement();
        int countTableSize = 0;

        // when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }

        final Query query = getSession().createSQLQuery(sql).addEntity(CreditEntity.class);

        // then
        Assertions.assertTrue(countTableSize > 0);
        Assertions.assertTrue(query.list().size() > 0);
    }

    @Test
    @Order(4)
    void addCredit_whenValid_shouldSave() {
        //given
        CreditEntity entity = new CreditEntity();
        entity.setCreditId((short) 2);
        entity.setBalance("1000");
        entity.setCloseDate("2033-02-01 00:00:00");
        entity.setOpenDate("2033-02-01 00:00:00");
        entity.setNumber("100");
        entity.setSumm("1000000");
        entity.setStatus("Open");
        entity.setClient((short) 1);
        entity.setEmployee((short) 1);

        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            //when
            session.persist(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;  // Можно обработать ошибку или бросить исключение
        }

        String hql = "FROM CreditEntity WHERE creditId = :id";
        Query<CreditEntity> query = getSession().createQuery(hql, CreditEntity.class);
        query.setParameter("id", (short) 2);
        CreditEntity creditEntity = query.uniqueResult();

        //then
        Assertions.assertNotNull(creditEntity);
        Assertions.assertEquals("1000", creditEntity.getBalance());
    }

    @Test
    @Order(5)
    void deleteCredit_whenValid_shouldDelete() {
        //given
        String hql = "FROM CreditEntity WHERE creditId = :id";
        Query<CreditEntity> query = getSession().createQuery(hql, CreditEntity.class);
        query.setParameter("id", (short) 2);
        Optional<CreditEntity> creditEntityOptional = Optional.ofNullable(query.uniqueResult());

        // Проверяем, что объект существует перед удалением
        creditEntityOptional.ifPresentOrElse(creditEntity -> {
            //when
            Session session = getSession();
            Transaction transaction = session.beginTransaction();
            try {
                session.delete(creditEntity);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw e;
            }
            //then
            // Проверяем, что объект был удалён
            Query<CreditEntity> queryAfterDelete = getSession().createQuery(hql, CreditEntity.class);
            queryAfterDelete.setParameter("id", (short) 2);
            Optional<CreditEntity> creditEntityAfterDelete = Optional.ofNullable(queryAfterDelete.uniqueResult());
            Assertions.assertFalse(creditEntityAfterDelete.isPresent());
        }, () -> {
            Assertions.fail("CreditEntity with ID 2 not found");
        });
    }

    @Test
    @Order(6)
    void shouldReturnCorrectCreditData() throws SQLException {
        //        // given
        String sql = "SELECT * FROM credit WHERE credit_id = 1";
        Statement stmt = getConnection().createStatement();

        // when
        ResultSet rs = stmt.executeQuery(sql);
        rs.next();
        int expectedDebitAmount = 1000;  // ожидаемая сумма дебета
        int actualDebitAmount = rs.getInt("balance");

        // then
        Assertions.assertEquals(expectedDebitAmount, actualDebitAmount);
    }
}
