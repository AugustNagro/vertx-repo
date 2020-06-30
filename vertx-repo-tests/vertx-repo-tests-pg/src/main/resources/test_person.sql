drop table if exists test_person cascade;

create table test_person (
    id bigserial primary key,
    first_name varchar(50),
    last_name varchar(50) not null,
    is_admin boolean default true,
    created timestamptz default current_timestamp
);

insert into test_person (first_name, last_name, is_admin, created) values
('George', 'Washington', true, default),
('Alexander', 'Hamilton', true, default),
('John', 'Adams', true, default),
('Benjamin', 'Franklin', true, default),
('John', 'Jay', true, default),
('Thomas', 'Jefferson', true, default),
('James', 'Madison', true, default),
(null, 'Nagro', false, timestamp '1997-08-12');