##################################################
# user-service-3.1.2-SNAPSHOT | 13-Dec-2021
##################################################
### Code changes:
Bug fix for merchant login using email


##################################################
# user-service-3.1.1-SNAPSHOT | 10-Dec-2021
##################################################
### Code changes:
New API generateTempToken() to generate temporary token that expired in 5 minutes. 
This token allow merchant to check unprocess order immediately using url with token, without enter username/password


##################################################
# user-service-3.1.0-SNAPSHOT
##################################################
### Code changes:
New function for store-user. this user that manage branch (store). one customer (client) can have multiple branch (store)

1) new table : store_user

	CREATE TABLE store_user 
	(
	id VARCHAR(100) PRIMARY KEY,
	storeId VARCHAR(50),
	username VARCHAR(256),
	PASSWORD VARCHAR(256),
	NAME VARCHAR(200),
	phoneNumber VARCHAR(30),
	phoneNumberVerified TINYINT(1),
	email VARCHAR(300),
	emailVerified TINYINT(1),
	deactivated TINYINT(1),
	LOCKED TINYINT(1),
	created TIMESTAMP,
	updated TIMESTAMP,
	roledId VARCHAR(50)
	);

2) new table : store_user_session
	CREATE TABLE store_user_session 
	(
	id VARCHAR(100) PRIMARY KEY,
	remoteAddress varchar(50),
	status varchar(50),
	username varchar(50),
	expiry timestamp,
	accessToken  varchar(300),
	refreshToken  varchar(300),
	created timestamp,
	updated timestamp,
	ownerId varchar(50)
	);
	

##################################################
# user-service-3.0.29-SNAPSHOT | 11-Oct-2021
##################################################
### Code Changes:
bug fix


##################################################
# user-service-3.0.26-SNAPSHOT | 5-Oct-2021
##################################################
### Code Changes:
add new parameter in response for get session details :
-ownerId
-sessionType


