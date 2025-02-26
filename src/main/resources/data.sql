INSERT INTO users (id, username, password, email, role) values ('1', 'bob', '{noop}password1', 'bob@gmail.com', 'customer');
INSERT INTO users (id, username, password, email, role) values ('2', 'john', '{noop}password2', 'john@gmail.com', 'customer');

INSERT INTO customer (id, name, user_id) values ('1234', 'Bob', '1');
INSERT INTO customer (id, name, user_id) values ('9876', 'John', '2');

INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('121212121', '1234', 20000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('444444444', '1234', 10000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('343434343', '1234', 50000.0, 'SAVING', 'EUR');

INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('717171717', '9876', 15000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('828282828', '9876', 40000.0, 'SAVING', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('555555555', '9876', 5000.0, 'SAVING', 'EUR');
