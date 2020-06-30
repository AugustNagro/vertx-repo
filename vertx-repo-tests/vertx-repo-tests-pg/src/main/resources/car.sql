drop table if exists car;

create table car (
    id bigserial primary key,
    model varchar(50) not null,
    top_speed int
);

insert into car (model, top_speed) values
('McLaren Senna', 208),
('Ferrari F8 Tributo', 212),
('Aston Martin Superleggera', 211);
