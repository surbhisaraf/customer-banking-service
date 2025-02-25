INSERT INTO users (id, username, password, email, role) values ('1', 'surbhisaraf', '{noop}password', 'surbhisaraf@gmail.com', 'customer');
INSERT INTO users (id, username, password, email, role) values ('2', 'john', '{noop}password', 'john@gmail.com', 'customer');

INSERT INTO customer (id, name, user_id) values ('1234', 'Surbhi Saraf', '1');
INSERT INTO customer (id, name, user_id) values ('4321', 'John', '2');

INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('121212121', '1234', 0.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('343434343', '1234', 500.0, 'SAVING', 'EUR');

INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('717171717', '4321', 20000.0, 'REGULAR', 'EUR');
INSERT INTO account (account_no, customer_id, balance, account_type, currency) values ('828282828', '4321', 500000.0, 'SAVING', 'EUR');
