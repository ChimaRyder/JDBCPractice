import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateData {
    public static void main(String[] args) {
        try(Connection c = MySQLConnection.getConnection();
            PreparedStatement statement = c.prepareStatement(
                    "UPDATE users SET name=? WHERE id=?"
            )
        ) {
            String new_name = "Frenz Nicole Repunte";
            int id = 1;
            statement.setString(1, new_name);
            statement.setInt(2, id);
            int rowsUpdated = statement.executeUpdate();
            System.out.println("Updated " + rowsUpdated + " rows!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
