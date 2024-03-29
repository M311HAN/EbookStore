CREATE DATABASE ebookstore;
USE ebookstore;
CREATE TABLE books (
    id INT PRIMARY KEY,
    title VARCHAR(100),
    author VARCHAR(100),
    qty INT
);

INSERT INTO books (id, title, author, qty) VALUES
(3001, 'A Tale of Two Cities', 'Charles Dickens', 30),
(3002, 'Harry Potter and the Philosopher''s Stone', 'J.K. Rowling', 40),
(3003, 'The Lion, the Witch and the Wardrobe', 'C.S. Lewis', 25),
(3004, 'The Lord of the Rings', 'J.R.R Tolkien', 37),
(3005, 'Alice in Wonderland', 'Lewis Carroll', 12);

