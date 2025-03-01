create table roles (role varchar(255) not null, primary key (role));

create table users (role smallint not null check (role between 0 and 2),
                    id bigint generated by default as identity,
                    password varchar(255) not null,
                    username varchar(255) not null unique,
                    primary key (id));

create table tickets (is_complete boolean not null,
                      version integer not null,
                      created_at timestamp(6),
                      description varchar(255),
                      technician_name varchar(255),
                      ticket_id varchar(255) not null,
                      primary key (ticket_id));

-- creating the default roles for the app
insert into roles (role) values ('ADMIN');
insert into roles (role) values ('MANAGER');
insert into roles (role) values ('TECHNICIAN');

-- creating one admin so that the rest of the users can be registered by the admin. For all users Plaintext Password is 123 :)
insert into users (password,role,username)
values ('$2a$10$.T6xpFR3L7mWdFbEdnYlBudraGWNxc9wi6r4sJejKpymQl5ifIAN.',0,'admin');

insert into users (password,role,username)
values ('$2a$10$.T6xpFR3L7mWdFbEdnYlBudraGWNxc9wi6r4sJejKpymQl5ifIAN.',1,'Wikicoding');

insert into users (password,role,username)
values ('$2a$10$.T6xpFR3L7mWdFbEdnYlBudraGWNxc9wi6r4sJejKpymQl5ifIAN.',2,'PO1');

-- adding some tickets
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 1', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620cdd');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 2', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620cde');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 3', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620cdf');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 4', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce0');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 5', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce1');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 6', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce2');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 7', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce3');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 8', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce4');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 9', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce5');
insert into tickets (description, technician_name, is_complete, version, ticket_id) values ('Team ticket 10', 'PO1', false, 1, 'e4eaaaf2-d142-11e1-b3e4-080027620ce6');