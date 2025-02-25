INSERT INTO users (id, username, password, email, role) values ('1', 'bob', '{noop}password', 'bob@gmail.com', 'customer');
INSERT INTO users (id, username, password, email, role) values ('2', 'john', '{noop}password', 'john@gmail.com', 'customer');

INSERT INTO customer (id, name, user_id) values ('1234', 'Bob', '1');
INSERT INTO customer (id, name, user_id) values ('4321', 'John', '2');

INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('121212121', '1234', 10000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('444444444', '1234', 3000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('343434343', '1234', 5000.0, 'SAVING', 'EUR');

INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('717171717', '4321', 20000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('828282828', '4321', 500000.0, 'SAVING', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('555555555', '4321', 1000.0, 'SAVING', 'EUR');
