ALTER TABLE user
ADD COLUMN nickname varchar(255) null,
ADD COLUMN profile_image_url varchar(255) null,
ADD COLUMN point int null,
ADD COLUMN student_id_card_image_url varchar(255) null,
ADD COLUMN student_id_card_verification_status varchar(255) null;

UPDATE user
SET nickname = 'DEFAULT',
    profile_image_url = 'DEFAULT',
    point = 0,
    student_id_card_image_url = null,
    student_id_card_verification_status = 'NOT_VERIFIED_YET';
