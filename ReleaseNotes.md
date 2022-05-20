##################################################
# user-service-3.2.18-SNAPSHOT | 20-May-2022
##################################################
Bug fix for newuser voucher for gooogle/fb/apple loginoauth


##################################################
# user-service-3.2.17-SNAPSHOT | 19-May-2022
##################################################
Bug fix for apple login for customer
Bug fix for newuser voucher for gooogle/fb/apple loginoauth


##################################################
# user-service-3.2.16-SNAPSHOT | 18-May-2022
##################################################
Set sender email address & sender name when sending email to email-service
Bug fix for PUT customer

##################################################
# user-service-3.2.15-SNAPSHOT | 13-May-2022
##################################################
Remove storeId from customer relationship for marketplace feature
Activate customer account if login via Google/FB/Apple
Claim newuser voucher for newly activated account
Add domain to customer profile
Add channel to customer profile
Change facebook appId & secret
Bug fix for customer register, check duplicate phone number

##DB Changes:
ALTER TABLE customer ADD domain VARCHAR(100);
ALTER TABLE customer ADD channel VARCHAR(100);

##Config changes:
orderService.claimnewuservoucher.URL=https://api.symplified.it/order-service/v1/voucher/claim/newuser/<customerId>


##################################################
# user-service-3.2.14-SNAPSHOT | 22-April-2022
##################################################
Bug fix for customer PUT
Bug fix for customer GET for FB, apple & google


##################################################
# user-service-3.2.13-SNAPSHOT | 20-April-2022
##################################################
Bug fix for customer oauth login
Bug for for customer sign up
Add cookie for refreshToken & loginoauth

##DB Changes:
ALTER TABLE customer_session ADD domain VARCHAR(100);


##################################################
# user-service-3.2.12-SNAPSHOT | 15-April-2022
##################################################
New API for customer change password


##################################################
# user-service-3.2.11-SNAPSHOT | 08-April-2022
##################################################
New field for customer address

##DB Changes:
ALTER TABLE customer_address ADD isDefault TINYINT(1) NOT NULL DEFAULT 0;


##################################################
# user-service-3.2.10-SNAPSHOT | 07-April-2022
##################################################
Bug fix for customer reset password


##################################################
# user-service-3.2.9-SNAPSHOT | 06-April-2022
##################################################
Bug fix for customer address API


##################################################
# user-service-3.2.8-SNAPSHOT | 05-April-2022
##################################################
Remove httpOnly from cookies parameter in authenticateCustomer


##################################################
# user-service-3.2.7-SNAPSHOT | 04-April-2022
##################################################
Receive domain in customer login used to set cookies


##################################################
# user-service-3.2.6-SNAPSHOT | 01-April-2022
##################################################
receive full domain in apple callback


##################################################
# user-service-3.2.5-SNAPSHOT | 28-March-2022
##################################################
Add AddResponseHeaderFilter class to add customer http header in response
Add http response header in customer login

##################################################
# user-service-3.2.4-SNAPSHOT | 25-March-2022
##################################################
Loginoauth for customer 


##################################################
# user-service-3.2.3-SNAPSHOT | 24-March-2022
##################################################
Validate apple token 


##################################################
# user-service-3.2.2-SNAPSHOT | 24-March-2022
##################################################
New parameter in customer reset password : domain & reseturl 


##################################################
# user-service-3.2.1-SNAPSHOT | 22-March-2022
##################################################
Bug fix for register customer


##################################################
# user-service-3.2.0-SNAPSHOT | 09-March-2022
##################################################
Add token verification for login using facebook & google


##################################################
# user-service-3.1.7-SNAPSHOT | 24-Feb-2022
##################################################
Bug fix for email template for password reset


##################################################
# user-service-3.1.6-SNAPSHOT | 11-Feb-2022
##################################################
Link client with regionCountry


##################################################
# user-service-3.1.5-SNAPSHOT | 19-Jan-2022
##################################################
Add new field for merchant : country to disallow merchant create store for multiple country

### DB changes:
ALTER TABLE client ADD countryId VARCHAR(3);


##################################################
# user-service-3.1.4-SNAPSHOT | 03-Jan-2022
##################################################
### Code changes:
New function changePasswordClientById()


##################################################
# user-service-3.1.3-SNAPSHOT | 29-Dec-2021
##################################################
### Code changes:
Remove config : symplified.mechant-portal.url=https://symplified.biz/merchant

Add new config : 
	symplified.merchant.reset.password.url=https://symplified.biz/merchant/forgot-password
	symplified.merchant.email.verification.url=https://symplified.biz/merchant/email-verified
	
	
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


