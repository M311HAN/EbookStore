import java.sql.*;
import java.util.Scanner;

/**
 * This class represents an eBook store management system, providing a command-line
 * interface to interact with a database to perform operations such as adding,
 * updating, deleting, and searching for books.
 */
public class EbookStore {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/ebookstore?useSSL=false";
    private static final String USERNAME = "YourChosenUsername";
    private static final String PASSWORD = "YourChosenPassword";
    
    /**
     * The main method that drives the program, establishing a connection to the database
     * and allowing the user to choose various operations via a menu.
     * @param args Command-line arguments passed to the program (not used).
     */
    /**
     * Establishes a connection to the database and handles the main program flow.
     * Uses a loop to provide a menu for user operations and switches between different
     * operations based on user input.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;

        try {
        	// Connect to the database using the specified URL, username, and password.
            // The useSSL=false parameter is used to avoid SSL issues.
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            System.out.println("Connection to the bookstore database successful!\n");

            int option;
            do {
                printBooks(connection); // Display current books
                printMenu();
                System.out.print("Choose an option: ");
                option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 1:
                        enterBook(scanner, connection);
                        break;
                    case 2:
                        updateBook(scanner, connection);
                        break;
                    case 3:
                        deleteBook(scanner, connection);
                        break;
                    case 4:
                        searchBooks(scanner, connection);
                        break;
                    case 0: // Exit the program
                        System.out.println("Exiting the program.");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } while (option != 0);

        } catch (SQLException e) {
            System.out.println("Connection to the database failed!");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        scanner.close();
    }
    
    /**
     * Prints the menu for the bookstore management system.
     */
    private static void printMenu() {
        System.out.println("\n--- Bookstore Management System ---");
        System.out.println("1. Enter book");
        System.out.println("2. Update book");
        System.out.println("3. Delete book");
        System.out.println("4. Search books");
        System.out.println("0. Exit");
    }

    /**
     * Fetches and prints a list of all books from the database.
     * @param connection The established SQL connection.
     */
    private static void printBooks(Connection connection) {
        String sql = "SELECT id, title, author, qty FROM books ORDER BY id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nCurrent Books:");
            System.out.println(String.format("%-5s %-50s %-30s %-4s", "ID", "Title", "Author", "Qty"));
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int qty = rs.getInt("qty");
                System.out.println(String.format("%-5d %-50s %-30s %-4d", id, title, author, qty));
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while fetching books.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the user input for entering a new book into the database.
     * @param scanner The scanner instance for user input.
     * @param connection The established SQL connection.
     */
    /**
     * Inserts a new book into the database after ensuring the ID is unique, improving data integrity.
     * Consumes the newline character left over from nextInt() to prevent input skipping.
     */
    private static void enterBook(Scanner scanner, Connection connection) {
        System.out.println("\nEnter details of the book:");

        int id;
        while (true) {
            System.out.print("ID: ");
            id = scanner.nextInt();
            scanner.nextLine(); // consume the leftover newline

          // Check if the ID already exists to enforce the uniqueness constraint on book IDs.
            if (checkIfIdExists(id, connection)) {
                System.out.println("ID already in use. Try another.");
            } else {
                break; // Exit loop if ID is unique
            }
        }

        System.out.print("Title: ");
        String title = scanner.nextLine();

        System.out.print("Author: ");
        String author = scanner.nextLine();

        System.out.print("Quantity: ");
        int qty = scanner.nextInt();
        
        String sql = "INSERT INTO books (id, title, author, qty) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, author);
            preparedStatement.setInt(4, qty);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " book(s) entered.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the book ID already exists in the database.
     * @param id The book ID to check.
     * @param connection The established SQL connection.
     * @return boolean True if the ID exists, false otherwise.
     */
    // Method to check if a book ID already exists in the database to ensure each book has a unique identifier, this helps with data integrity.
    private static boolean checkIfIdExists(int id, Connection connection) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
            	// true if count > 0, meaning ID exists
                return resultSet.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Updates details for an existing book in the database.
     * Users can search for the book by ID or name and choose to update the title, author, quantity, or ID.
     *
     * @param scanner    Scanner to read user input.
     * @param connection Connection object to the database.
     */
    private static void updateBook(Scanner scanner, Connection connection) {
    	// Get user choice for search method
    	String methodChoice;
        while (true) {
            System.out.println("\nHow would you like to find the book you want to update?");
            System.out.println("1. By ID");
            System.out.println("2. By Title"); 
            System.out.print("Enter your choice (1 or 2): ");
            methodChoice = scanner.nextLine().trim();
          // Validate user choice
            if (methodChoice.equals("1") || methodChoice.equals("2")) {
                break;
            }
            System.out.println("Invalid choice. Please enter '1' or '2'.");
        }

        String sqlQuery = "";
        int bookId = 0;
        String bookTitle = "";
        
       // Prepare the SQL query based on the search method
        if (methodChoice.equals("1")) {
            System.out.print("Enter the Book ID: ");
            try {
            	// Read the book ID from the user
                bookId = Integer.parseInt(scanner.nextLine());
                sqlQuery = "SELECT * FROM books WHERE id = ?";            
            } catch (NumberFormatException e) {
            	// SQL query to select a book by ID
                System.out.println("Invalid book ID format.");
                return;
            }
        } else {
        	System.out.print("Enter the Book Title: ");
            // Read the book name from the user
            bookTitle = scanner.nextLine();
            // SQL query to select a book by name, ignoring case and special characters
            sqlQuery = "SELECT * FROM books WHERE LOWER(REPLACE(REPLACE(title, '''', ''), ',', '')) LIKE ?";

        }
        // Execute the prepared statement and process the ResultSet
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            if (methodChoice.equals("1")) {
                preparedStatement.setInt(1, bookId);
            } else {
                preparedStatement.setString(1, "%" + bookTitle.toLowerCase().replace("'", "").replace(",", "") + "%");
                // Set the book name in the SQL query, formatted for the LIKE clause
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Book not found.");
                return;
            } else {
            	// Book found, retrieve details from ResultSet
                bookId = resultSet.getInt("id"); 
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                int quantity = resultSet.getInt("qty");
                
                // Display current book details before updating
                System.out.println("Current Book Details:");
                System.out.println("ID: " + bookId);
                System.out.println("Title: " + title);
                System.out.println("Author: " + author);
                System.out.println("Quantity: " + quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        String updateChoice;
        while (true) {
            // Get user choice for which field to update
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. ID");
            System.out.println("2. Title");
            System.out.println("3. Author");
            System.out.println("4. Quantity");
            System.out.print("Choose an option (1-4): ");
            updateChoice = scanner.nextLine().trim();

            if (updateChoice.equals("1") || updateChoice.equals("2") ||
                updateChoice.equals("3") || updateChoice.equals("4")) {
                break;
            }
            System.out.println("Invalid option. Please choose a number between 1 and 4.");
        }

        String updateField = "";
        switch (updateChoice) {
            case "1":
                updateField = "id";
                break;
            case "2":
                updateField = "title";
                break;
            case "3":
                updateField = "author";
                break;
            case "4":
                updateField = "qty";
                break;
        }

        System.out.print("Enter new " + updateField + ": ");
        String newValue = scanner.nextLine();

        String updateSql = "UPDATE books SET " + updateField + " = ? WHERE id = ?";
        try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
            if (updateField.equals("qty") || updateField.equals("id")) {
                updateStmt.setInt(1, Integer.parseInt(newValue));
            } else {
                updateStmt.setString(1, newValue);
            }
            updateStmt.setInt(2, bookId);

            int rowsAffected = updateStmt.executeUpdate();
            System.out.println(rowsAffected + " book(s) updated.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Deletes a book from the database.
     * Users can search for the book by ID or name and then confirm before deletion.
     *
     * @param scanner    Scanner to read user input.
     * @param connection Connection object to the database.
     */
    private static void deleteBook(Scanner scanner, Connection connection) {
    	// Get user choice for search method
    	String methodChoice;
        while (true) {
            System.out.println("\nHow would you like to find the book you want to delete?");
            System.out.println("1. By ID");
            System.out.println("2. By Title");
            System.out.print("Enter your choice (1 or 2): ");
            methodChoice = scanner.nextLine().trim();

            if ("1".equals(methodChoice) || "2".equals(methodChoice)) {
                break;
            }
            System.out.println("Invalid choice. Please enter '1' or '2'.");
        }

        String sqlQuery;
        int bookId = 0;
        String bookTitle = ""; 

        if ("1".equals(methodChoice)) {
            System.out.print("Enter the Book ID: ");
            bookId = scanner.nextInt();
           // consume the newline
            scanner.nextLine(); 
            sqlQuery = "SELECT * FROM books WHERE id = ?";
        } else {
        	System.out.print("Enter the Book Title: ");
            bookTitle = scanner.nextLine();
            sqlQuery = "SELECT * FROM books WHERE LOWER(REPLACE(REPLACE(title, '''', ''), ',', '')) LIKE ?";
        }
        // Execute the prepared statement and process the ResultSet
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
        	// Validate user choice
        	if ("1".equals(methodChoice)) {
                preparedStatement.setInt(1, bookId);
            } else {
            	preparedStatement.setString(1, "%" + bookTitle.toLowerCase().replace("'", "").replace(",", "") + "%"); 
            }
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("Book not found.");
                return;
            }
            // Book found, retrieve details from ResultSet
            bookId = resultSet.getInt("id");
            String title = resultSet.getString("title");
            String author = resultSet.getString("author");
            int quantity = resultSet.getInt("qty");

            // Display book details to confirm deletion
            System.out.println("Book Details:");
            System.out.println("ID: " + bookId);
            System.out.println("Title: " + title);
            System.out.println("Author: " + author);
            System.out.println("Quantity: " + quantity);

            // Confirm deletion with the user
            String confirmation;
            while (true) {
                System.out.print("Are you sure you want to delete this book (y/n)? ");
                confirmation = scanner.nextLine().trim().toLowerCase();
                
                // Perform the deletion if confirmed
                if ("y".equals(confirmation) || "n".equals(confirmation)) {
                    break;
                }
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }

            if ("y".equals(confirmation)) {
                // SQL query to delete a book by ID
                sqlQuery = "DELETE FROM books WHERE id = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(sqlQuery)) {
                    deleteStmt.setInt(1, bookId);
                    int rowsAffected = deleteStmt.executeUpdate();
                    System.out.println(rowsAffected + " book(s) deleted.");
                }
            } else {
            	// Deletion cancelled by the user
                System.out.println("Book deletion cancelled.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the user input for searching books in the database based on ID, title, or author.
     * @param scanner The scanner instance for user input.
     * @param connection The established SQL connection.
     */
    /**
     * Searches books based on ID, title, or author using a dynamic SQL query.
     * The use of LIKE and wildcards enables partial matching, giving users flexibility in searching.
     */
    private static void searchBooks(Scanner scanner, Connection connection) {
        System.out.println("\nSearch by:");
        System.out.println("1. ID");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.print("Enter search type: ");
        int searchType = scanner.nextInt();
        scanner.nextLine(); // consume the leftover newline

        String sql = "";
        String userInput = "";
        // Choose the SQL query based on the type of search. Using LIKE allows for a more flexible search by partial matching.
        switch (searchType) {
            case 1:
                System.out.print("Enter ID number: ");
                int id = scanner.nextInt();
                sql = "SELECT * FROM books WHERE id = ?";
                userInput = String.valueOf(id);
                break;
            case 2:
                System.out.print("Enter title: ");
                userInput = scanner.nextLine();
                // When searching, use LIKE to allow partial matches, enabling more flexible searches.
                sql = "SELECT * FROM books WHERE LOWER(REPLACE(REPLACE(title, '''', ''), ',', '')) LIKE ?";

                // Wildcards used for partial match
                userInput = "%" + userInput + "%";
                break;
            case 3:
                System.out.print("Enter author: ");
                userInput = scanner.nextLine();
                sql = "SELECT * FROM books WHERE author LIKE ?";
                userInput = "%" + userInput + "%";
                break;
            default:
                System.out.println("Invalid search type.");
                return;
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, 
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setString(1, userInput);
            ResultSet resultSet = preparedStatement.executeQuery();
         // Move to the last row of resultSet to determine the number of rows and thus the existence of any search results.
            if (resultSet.last()) { 
            	// Get the number of rows/books found
                int rowCount = resultSet.getRow(); 
                if (rowCount > 0) {
                    System.out.println("\nYay! We have " + rowCount + " book(s) from this Search!\n");
                   // Reset the cursor to the beginning to iterate over the results
                    resultSet.beforeFirst(); 

                    while (resultSet.next()) {
                        int bookId = resultSet.getInt("id");
                        String title = resultSet.getString("title");
                        String author = resultSet.getString("author");
                        int qty = resultSet.getInt("qty");
                        System.out.println("ID: " + bookId);
                        System.out.println("Title: " + title);
                        System.out.println("Author: " + author);
                        System.out.println("Quantity: " + qty + " available in stock!\n");
                    }
                } else {
                    System.out.println("Sorry, this book is not available.");
                }
            } else {
                System.out.println("Sorry, this book is not available.");
            }
         // Close the prepared statement
            preparedStatement.close(); 
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}