package seminar;

import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class ClientTest extends AbstractTest {

    @Test
    void getClients_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM client";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        String hql = "FROM ClientEntity";
        //when
        final Query<ClientEntity> query = getSession().createQuery(hql, ClientEntity.class);
        List<ClientEntity> clients = query.list();
        //then
        Assertions.assertEquals(3, clients.size());
    }

    @ParameterizedTest
    @CsvSource({"1, Иван, Иванов", "2, Петр, Петров", "3, Сидр, Сидоров"})
    void getClientById_whenValid_shouldReturn(int id, String name, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM client WHERE client_id = ?";
        PreparedStatement stmt  = getConnection().prepareStatement(sql);
        stmt.setInt(1, id);

        // when
        ResultSet rs = stmt.executeQuery();
        String dbName = "";
        String dbLastName = "";
        while (rs.next()) {
            dbName = rs.getString("first_name");
            dbLastName = rs.getString("last_name");
        }

        Assertions.assertNotNull(dbName);
        Assertions.assertNotNull(dbLastName);

        // then
        Assertions.assertEquals(name, dbName);
        Assertions.assertEquals(lastName, dbLastName);
    }

}


