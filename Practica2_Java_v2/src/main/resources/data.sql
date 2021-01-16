-- Saving books
INSERT INTO books (title, summary, author, publisher, publication_year) VALUES ("Book 1 title", "Book 1 summary","Book 1 author","Book 1 publisher",1999);
INSERT INTO books (title, summary, author, publisher, publication_year) VALUES ("Book 2 title", "Book 2 summary","Book 2 author","Book 2 publisher",2012);

-- saving users
INSERT INTO users (email, nick, password) VALUES('user1@email.es', 'user1', 'pass');
INSERT INTO users (email, nick, password) VALUES('user2@email.es', 'user2', 'pass');

-- saving roles
INSERT INTO roles (name) VALUES('ADMIN');
INSERT INTO roles (name) VALUES('USER');

-- saving
INSERT INTO user_roles (user_id,role_id) VALUES(1,1);
INSERT INTO user_roles (user_id,role_id) VALUES(2,2);


-- saving comments
INSERT INTO comments (comment, score,  book_id, user_id) VALUES ("Book 2 comment 1", 3, 2, 1);
INSERT INTO comments (comment, score, book_id, user_id) VALUES ("Book 2 comment 2", 4, 2, 1);