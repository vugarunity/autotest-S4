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

public class EmployeeTest extends AbstractTest {

    @Test
    void getEmployees_whenValid_shouldReturn() throws SQLException {
        //given
        String sql = "SELECT * FROM employee";
        Statement stmt  = getConnection().createStatement();
        int countTableSize = 0;
        //when
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            countTableSize++;
        }
        String hql = "FROM EmployeeEntity";
        //when
        final Query query = getSession().createQuery(hql);
        List<EmployeeEntity> employees = query.list();
        //then
        Assertions.assertEquals(2, employees.size());
    }

    @ParameterizedTest
    @CsvSource({"1, Кирил, Денисов", "2, Владимир, Махлин"})
    void getClientById_whenValid_shouldReturn(int id, String name, String lastName) throws SQLException {
        //given
        String sql = "SELECT * FROM employee WHERE employee_id = ?";
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

    @ParameterizedTest
    @CsvSource({"1, Менеджер", "2, Главный Менеджер"})
    void getEmployeeById_whenValid_shouldReturn(int id, String portion) throws SQLException {
        //given
        //when
        final Query query = getSession().createQuery("from " + "EmployeeEntity" + " where employeeId=" + id);
        EmployeeEntity employeeEntity = (EmployeeEntity) query.uniqueResult();
        //then
        Assertions.assertEquals(portion, employeeEntity.getPortion());
    }
}
