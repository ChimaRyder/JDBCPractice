import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteData {
    public static void main(String[] args) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement(
                     "DELETE FROM users WHERE id>=? AND id<=?"
             )
        ) {
            int starting_id = 2;
            int ending_id = 5;
            statement.setInt(1, starting_id);
            statement.setInt(2, ending_id);
            int rowsDeleted = statement.executeUpdate();
            System.out.println("Deleted " + rowsDeleted + " rows!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
