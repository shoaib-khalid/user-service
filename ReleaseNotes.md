##################################################
# user-service-3.5.1-SNAPSHOT | 27-Jan-2023
##################################################
Bug fix authenticate store user to update FCM token


##################################################
# user-service-3.5.0-SNAPSHOT | 03-Jan-2023
##################################################
New user roles : STORE_WAITER
Use store user to register waiter


New API : store_user/endShift
	-insert into store_shift_summary for total sales by payment channel for that staff for current shift
	-push logout notification to mobile-app for auto-logout for that staff
	
##Config changes:
spring.jpa.hibernate.use-new-id-generator-mappings=false
	
##DB Changes:
INSERT INTO role VALUES ('STORE_WAITER',1,'Store Waiter','Store Waiter to take order from customer','STORE_OWNER');
ALTER TABLE store ADD storePrefix VARCHAR(10) comment 'prefix to append in staff username & invoice no';
ALTER TABLE `order` ADD isShiftEnd TINYINT(1) DEFAULT 0;
ALTER TABLE `store_user` ADD fcmToken VARCHAR(200);

CREATE TABLE store_shift_summary (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	userId VARCHAR(100),
	firstOrderId VARCHAR(50),
	lastOrderId VARCHAR(50),
	firstOrderTime DATETIME,
	lastOrderTime DATETIME,
	created DATETIME,
	updated DATETIME
);


CREATE TABLE store_shift_summary_details (
	summaryId BIGINT,
	saleAmount DECIMAL(10,2),
	paymentChannel VARCHAR(20)
);


INSERT INTO role_authority VALUES ('STORE_OWNER','store-users-get','user-service');
INSERT INTO role_authority VALUES ('STORE_OWNER','store-users-post','user-service');
INSERT INTO role_authority VALUES ('STORE_OWNER','store-users-delete','user-service');
INSERT INTO role_authority VALUES ('STORE_OWNER','store-users-put','user-service');


##################################################
# user-service-3.4.9-SNAPSHOT | 21-Nov-2022
##################################################
Allow customer to login using email or password


##################################################
# user-service-3.4.8-SNAPSHOT | 15-Nov-2022
##################################################
Add guest session expiry datetime in response of generateSession

##DB Changes:
ALTER TABLE guest_session ADD expiryTime DATETIME;

##Config Changes:
guest.session.expiry=1800 (1800 second)

##################################################
# user-service-3.4.7-SNAPSHOT | 3-Nov-2022
##################################################
New API to validate password :
PUT /{id}/validatepassword


##################################################
# user-service-3.4.6-SNAPSHOT | 1-Nov-2022
##################################################
New parameter in pingresponse : deviceModel
New API : deactivateCustomerById()

##DB Changes
ALTER TABLE client ADD deviceModel VARCHAR(255);
ALTER TABLE customer ADD originalUsername VARCHAR(255);
ALTER TABLE customer ADD originalEmail VARCHAR(255);


##################################################
# user-service-3.4.5-SNAPSHOT | 20-Oct-2022
##################################################
New API : 
PUT /guest/updateSessionEmail

##DB Changes
ALTER TABLE guest_session ADD email VARCHAR(255);


##################################################
# user-service-3.4.4-SNAPSHOT | 18-Oct-2022
##################################################
New API : to receive error from mobile app to store in db
POST /clients/logerror

##DB Changes
CREATE TABLE application_error (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
clientId VARCHAR(50),
severity VARCHAR(50),
errorMsg TEXT,
created DATETIME,
updated DATETIME
);


##################################################
# user-service-3.4.3-SNAPSHOT | 24-Aug-2022
##################################################
New feature : send ping to mobileapp in scheduler
New api to receive ping response from mobileapp : PUT /clients/{id}/pingresponse/{transactionId}

##DB Changes
ALTER TABLE `client` ADD mobilePingTxnId VARCHAR(50);
ALTER TABLE `client` ADD mobilePingLastResponse DATETIME;

##Config changes:
#enable scheduler to push FCM for heartbeat
mobileapp.heartbeat.scheduler.enabled=true
#sleep between batch during push FCM to mobile app
mobileapp.heartbeat.scheduler.sleep=1


##################################################
# user-service-3.4.2-SNAPSHOT | 13-July-2022
##################################################
Bug fix for update customer address


##################################################
# user-service-3.4.1-SNAPSHOT | 8-July-2022
##################################################
Bug fix for guest session


##################################################
# user-service-3.4.0-SNAPSHOT | 27-June-2022
##################################################
New API to generate guest session

##DB Changes:
CREATE TABLE guest_session (
id VARCHAR(100) PRIMARY KEY,
ip VARCHAR(20),
os VARCHAR(20),
device VARCHAR(20),
created DATETIME,
updated DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


##################################################
# user-service-3.3.2-SNAPSHOT | 13-June-2022
##################################################
Fix cookies expiry date


##################################################
# user-service-3.3.1-SNAPSHOT | 2-June-2022
##################################################
New field for customer search history

ALTER TABLE customer_search_history ADD domain VARCHAR(100);
ALTER TABLE customer_search_history ADD image VARCHAR(100);


##################################################
# user-service-3.3.0-SNAPSHOT | 31-May-2022
##################################################
Add new controller : customer search - to store history of search text entered in front-end
Add error_code table to be used by all backend module to standardize message displayed to customer
Bug fix for message display to user when user not registered during login & reset password

##DB Changes:
CREATE TABLE customer_search_history (
id VARCHAR(100) PRIMARY KEY,
customerId VARCHAR(100),
searchText VARCHAR(200),
storeId VARCHAR(100),
created DATETIME
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `error_code` (
  `modules` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `errorCategory` varchar(100) NOT NULL,
  `errorCode` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `errorMessage` varchar(200) DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  `errorDescription` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`errorCode`,`errorCategory`,`modules`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

INSERT INTO role_authority VALUES ('STORE_OWNER','customer-search-get','user-service');
INSERT INTO role_authority VALUES ('STORE_OWNER','customer-search-delete-by-id','user-service');
INSERT INTO role_authority VALUES ('STORE_OWNER','customer-search-post','user-service');

INSERT INTO role_authority VALUES ('CUSTOMER','customer-search-get','user-service');
INSERT INTO role_authority VALUES ('CUSTOMER','customer-search-delete-by-id','user-service');
INSERT INTO role_authority VALUES ('CUSTOMER','customer-search-post','user-service');

INSERT INTO role_authority VALUES ('STORE_OWNER','error-code-get','user-service');


##################################################
# user-service-3.2.21-SNAPSHOT | 25-May-2022
##################################################
Add new field for customer :latitude & longitude

##DB Changes:
ALTER TABLE customer ADD latitude VARCHAR(20);
ALTER TABLE customer ADD longitude VARCHAR(20);


##################################################
# user-service-3.2.20-SNAPSHOT | 23-May-2022
##################################################
Set different error message for user that register using third party account : fb/apple/google


##################################################
# user-service-3.2.19-SNAPSHOT | 23-May-2022
##################################################
Disable email verification for new customer created account
Send email notification for new customer created account


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


