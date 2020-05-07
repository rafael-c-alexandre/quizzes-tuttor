update quizzes set associated_tournament_id = NULL where associated_tournament_id = 2;
delete from tournaments_questions where tournament_id = 2;
delete from tournaments_signed_users where tournament_id = 2;
delete from tournaments_topics where tournament_id = 2;
delete from users_created_tournaments where created_tournaments_id = 2;
delete from users_signed_tournaments where signed_tournaments_id = 2;
delete from tournaments where title ='Tournament Title2';
