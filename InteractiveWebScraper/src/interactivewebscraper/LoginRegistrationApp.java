import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class LoginRegistrationApp extends Application {

    private Connection connectDatabase() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/taskscheduler", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login and Registration");

        // Login Page UI
        Label loginLabel = new Label("Login");
        loginLabel.setFont(new Font("Arial", 24));
        loginLabel.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");

        Label loginMessage = new Label();
        loginMessage.setTextFill(Color.WHITE);

        Label noAccountLabel = new Label("Don't have an account?");
        noAccountLabel.setTextFill(Color.LIGHTGRAY);

        Button registerLink = new Button("Register Here");
        registerLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #0095FF; -fx-underline: true;");

        VBox loginLayout = new VBox(15, loginLabel, usernameField, passwordField, loginButton, loginMessage, noAccountLabel, registerLink);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(30));
        loginLayout.setStyle("-fx-background-color: #2E2E2E;"); // Dark background color

        Scene loginScene = new Scene(loginLayout, 400, 400);

        // Button Actions
        registerLink.setOnAction(e -> primaryStage.setScene(createRegisterScene(primaryStage)));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                loginMessage.setText("Please enter both username and password.");
            } else {
                Connection connection = connectDatabase();
                if (connection != null) {
                    try {
                        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, username);
                        statement.setString(2, password);
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            loginMessage.setTextFill(Color.GREEN);
                            loginMessage.setText("Login successful! Welcome, " + username + ".");
                            primaryStage.setTitle("DTS Use Case in WebScraper");
                            showUrlInputPage(primaryStage);
                        } else {
                            loginMessage.setTextFill(Color.RED);
                            loginMessage.setText("Invalid username or password.");
                        }
                    } catch (SQLException ex) {
                        loginMessage.setTextFill(Color.RED);
                        loginMessage.setText("Database error: " + ex.getMessage());
                    }
                }
            }
        });

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private Scene createRegisterScene(Stage primaryStage) {
        // Registration Page UI
        Label registerLabel = new Label("Register");
        registerLabel.setFont(new Font("Arial", 24));
        registerLabel.setTextFill(Color.WHITE);

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        fullNameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        phoneField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Username");
        newUsernameField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Password");
        newPasswordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setStyle("-fx-background-color: #ffffff; -fx-border-color: #b0b0b0; -fx-padding: 5;");

        Button registerButton = new Button("Register");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");

        Button backToLoginButton = new Button("Back to Login");
        backToLoginButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #0095FF; -fx-underline: true;");

        Label registerMessage = new Label();
        registerMessage.setTextFill(Color.WHITE);

        VBox registerLayout = new VBox(15, registerLabel, fullNameField, phoneField, newUsernameField, newPasswordField, confirmPasswordField, registerButton, registerMessage, backToLoginButton);
        registerLayout.setAlignment(Pos.CENTER);
        registerLayout.setPadding(new Insets(30));
        registerLayout.setStyle("-fx-background-color: #2E2E2E;"); // Dark background color

        Scene registerScene = new Scene(registerLayout, 400, 500);

        // Button Actions
        backToLoginButton.setOnAction(e -> primaryStage.setScene(createLoginScene(primaryStage)));

        registerButton.setOnAction(e -> {
            String fullName = fullNameField.getText();
            String phone = phoneField.getText();
            String username = newUsernameField.getText();
            String password = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();

            if (fullName.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                registerMessage.setTextFill(Color.RED);
                registerMessage.setText("All fields are required.");
            } else if (!password.equals(confirmPassword)) {
                registerMessage.setTextFill(Color.RED);
                registerMessage.setText("Passwords do not match.");
            } else {
                Connection connection = connectDatabase();
                if (connection != null) {
                    try {
                        String sql = "INSERT INTO users (full_name, phone, username, password) VALUES (?, ?, ?, ?);";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, fullName);
                        statement.setString(2, phone);
                        statement.setString(3, username);
                        statement.setString(4, password);
                        statement.executeUpdate();
                        registerMessage.setTextFill(Color.GREEN);
                        registerMessage.setText("Registration successful!");
                        fullNameField.clear();
                        phoneField.clear();
                        newUsernameField.clear();
                        newPasswordField.clear();
                        confirmPasswordField.clear();
                        primaryStage.setScene(createLoginScene(primaryStage));
                    } catch (SQLException ex) {
                        registerMessage.setTextFill(Color.RED);
                        registerMessage.setText("Database error: " + ex.getMessage());
                    }
                }
            }
        });

        return registerScene;
    }
    
    
    
    
    
    
    
private void showUrlInputPage(Stage primaryStage) {
        // URL Input UI
        Label urlLabel = new Label("Enter URL to Scrape");
        urlLabel.setFont(new Font("Arial", 24));
        urlLabel.setTextFill(Color.GOLD);

        TextField urlField = new TextField();
        urlField.setPromptText("Enter a valid URL here...");

        Button scrapeButton = new Button("Scrape");
        scrapeButton.setStyle("-fx-background-color: goldenrod; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: transparent; -fx-text-fill: GOLD; -fx-underline: true;");

        Label statusLabel = new Label();
        statusLabel.setTextFill(Color.WHITE);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setVisible(false);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefHeight(200);
        outputArea.setWrapText(true);

        VBox urlInputLayout = new VBox(10, urlLabel, urlField, scrapeButton, progressIndicator, statusLabel, outputArea, cancelButton);
        urlInputLayout.setAlignment(Pos.CENTER);
        urlInputLayout.setPadding(new Insets(20));
        urlInputLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #002147, #0e1a36);");

        Scene urlInputScene = new Scene(urlInputLayout, 500, 500);

        // Button Actions
        scrapeButton.setOnAction(e -> {
            String url = urlField.getText().trim();
            if (!url.isEmpty()) {
                statusLabel.setText("Scraping: " + url);
                progressIndicator.setVisible(true);

                // Perform scraping asynchronously
                CompletableFuture.runAsync(() -> {
                    try {
                        // Start MasterNode in a new thread
                            new Thread(() -> {
                                MasterNode.main(new String[0]); // Start the MasterNode
                            }).start();

                        // Simulating communication with MasterNode and WebScraperServer
                        
                   
                     // Step 2: Fetch size after MasterNode processing
                    WebPageSizeFetcher sizeFetcher = new WebPageSizeFetcher();

                        sizeFetcher.fetchAndSendPageSize(url);
                    // Step 1: Call MasterNode (which refers to WebScraperServer)
                    String output = callMasterNodeForScraping(url);
                    

                        Platform.runLater(() -> {
                            outputArea.setText(output);  // Display the scraped content/output
                            statusLabel.setText("Scraping complete.");
                            progressIndicator.setVisible(false);
                        });
                    } catch (Exception ex) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Error: " + ex.getMessage());
                            progressIndicator.setVisible(false);
                        });
                    }
                });
            } else {
                statusLabel.setText("Please enter a URL.");
            }
        });

        cancelButton.setOnAction(e -> primaryStage.setScene(createLoginScene(primaryStage))); // Going back to login
        primaryStage.setScene(urlInputScene);
    }

   private String callMasterNodeForScraping(String url) throws Exception {
    // Implement the logic to send URL and size to the MasterNode
    // Here's a basic structure to simulate that communication
    try (Socket socket = new Socket("localhost", 8080); // Assuming MasterNode is running on localhost
         PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
        
        // Send URL and size to MasterNode
        writer.println(url);
       
        
        // Read the response from the MasterNode (scraped content)
        return reader.readLine();
    } catch (IOException e) {
        throw new Exception("Failed to communicate with MasterNode: " + e.getMessage());
    }
}
    
    
    
    
    private Scene createLoginScene(Stage primaryStage) {
        // Similar to the login scene creation logic
        return new Scene(new VBox(), 400, 400); // Placeholder, implement as needed
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}