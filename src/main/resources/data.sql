insert into application_user (version, id, username,name,hashed_password, email, isoauth_user, secret) values (1, '1','user','John Normal','$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe','john@mail.com', false, '7X5NEYVFI7JNREFDBBBA6ISGUYCQ4W7T');
insert into user_roles (user_id, roles) values ('1', 'USER');
insert into application_user (version, id, username,name,hashed_password, email, isoauth_user, secret) values (1, '2','admin','Emma Powerful','$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.','emma@mail.com', false, '7X5NEYVFI7JNREFDBBBA6ISGUYCQ4W7T');
insert into user_roles (user_id, roles) values ('2', 'USER');
insert into user_roles (user_id, roles) values ('2', 'ADMIN');
