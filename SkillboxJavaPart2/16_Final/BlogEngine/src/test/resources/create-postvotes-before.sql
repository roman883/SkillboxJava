delete from post_votes;

insert into post_votes(id, time, value, post_id, user_id) values
(100, '2020-04-15 22:48:25.0', 1, 10, 21),
(101, '2020-04-15 22:48:25.0', 1, 13, 21),
(102, '2020-04-14 22:48:25.0', 1, 14, 21),
(103, '2020-04-13 22:48:25.0', -1, 15, 21),
(105, '2020-04-13 22:48:25.0', 1, 11, 21),
(104, '2020-04-12 22:48:25.0', -1, 16, 21),
(106, '2020-04-15 22:49:25.0', 1, 10, 22),
(107, '2020-04-15 22:47:25.0', 1, 13, 22),
(108, '2020-04-12 22:46:25.0', -1, 16, 22);