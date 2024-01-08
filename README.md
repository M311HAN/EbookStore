# EbookStore

## Introduction
This eBookstore application is a simple yet functional Java console application that allows users to interact with a MySQL database to perform basic operations such as adding, deleting, and updating book records. This project is designed to showcase the integration of Java with a MySQL database and demonstrates CRUD (Create, Read, Update, Delete) operations.

## Table of Contents

- [Installation](#installation)
- [Technologies Used](#TechnologiesUsed)
- [Requirements](#Requirements)
- [setup](#Setup)
- [Running the Application](RunningtheApplication)
- [Credits](#credits)

## Installation

To clone and navigate into the eBookstore project, execute the following commands in your terminal:

```bash
# Open a terminal

# Clone the repository by typing:
git clone https://github.com/M311HAN/EbookStore.git

# Navigate into the cloned directory with:
cd EbookStore

# If you have a SQL script to set up the database, run it using the MySQL command-line tool
mysql -u yourusername -p < setup.sql

# Replace 'yourusername' with your actual MySQL username and 
# you will be prompted to enter your MySQL password.

# Open the source code in your text editor or IDE
# Configure your database credentials in EbookStore.java

# Compile the Java program (ensure you have the JDK installed)
javac EbookStore.java

# Run the compiled Java program
java EbookStore

# Follow the application prompts to manage your eBookstore

```

## Technologies Used
- Java
- MySQL

## Requirements
- Java JDK 8 or above
- MySQL Server 5.7 or above

## Setup
To run this project locally, follow these steps:

### Database Setup
1. Install MySQL Server on your local machine.
2. Open your MySQL client (such as MySQL Workbench or phpMyAdmin).
3. Execute the `setup.sql` script from the `/database` directory to create the database and tables required for this project.

### IDE Setup
1. Clone the repository to your local machine explained above in "Installation".
2. Import the project into your preferred IDE (Eclipse, IntelliJ IDEA, etc.) as a Java project.
3. Ensure that the Java JDK is set up correctly in the project settings.

### Application Configuration
1. Navigate to `EbookStore.java`.
2. Locate the following lines:

    ```java
    private static final String USERNAME = "YourChosenUsername";
    private static final String PASSWORD = "YourChosenPassword";
    ```

3. Replace `YourChosenUsername` and `YourChosenPassword` with your MySQL database username and password.

### Running the Application
1. Compile and run `EbookStore.java`.
2. Use the console to interact with the application.

## credits
This project was created by Melihhan (https://github.com/M311HAN). For more details, questions, or feedback, reach out through GitHub.
