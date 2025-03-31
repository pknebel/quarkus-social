create table users (
    id bigserial not null primary key,
    name varchar(100) not null,
    age integer not null
);

create table posts (
    id bigserial not null primary key,
    user_id bigint not null references users(id),
    post_body text not null
    date timestamp not null default now()
);