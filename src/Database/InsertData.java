import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertData {
    public static void main(String[] args) {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement statement = c.prepareStatement(
                     "INSERT INTO users (name, email) VALUES (?, ?)"
             )
        ) {
            String name = "Nino Rey Cabiltes";
            String email = "ninoreycablites@gmail.com";
            statement.setString(1, name);
            statement.setString(2, email);
            int rowsInserted = statement.executeUpdate();
            System.out.println("Inserted " + rowsInserted + " rows!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
