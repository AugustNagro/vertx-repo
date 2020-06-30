drop table if exists windsurf_type_cd;

create table windsurf_type_cd (
    cd varchar(50) primary key,
    descr varchar(200)
);

insert into windsurf_type_cd values
('FREESTYLE', 'Freestyle; jumps and tricks'),
('SLALOM', 'Slalom; downwind speed racing'),
('COURSE', 'Course; upwind course racing'),
('WAVE', 'Wave; carving big surf'),
('FREERIDE', 'Freeride; mixed-performance for recreation');