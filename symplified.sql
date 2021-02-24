/*
SQLyog Community v13.1.7 (64 bit)
MySQL - 8.0.22 : Database - symplified
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`symplified` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `symplified`;

/*Table structure for table `administrator` */

DROP TABLE IF EXISTS `administrator`;

CREATE TABLE `administrator` (
  `id` varchar(100) NOT NULL,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `name` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `phoneNumber` varchar(30) DEFAULT NULL,
  `phoneNumberVerified` tinyint(1) DEFAULT '0',
  `email` varchar(300) DEFAULT NULL,
  `emailverified` tinyint(1) DEFAULT '0',
  `deactivated` tinyint(1) DEFAULT '0',
  `locked` tinyint(1) DEFAULT '0',
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `roleId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`),
  KEY `role` (`roleId`),
  CONSTRAINT `admin_role_fk1` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Admins are responsible for managing overall configurations, analytics and sites.';

/*Data for the table `administrator` */

/*Table structure for table `administrator_session` */

DROP TABLE IF EXISTS `administrator_session`;

CREATE TABLE `administrator_session` (
  `id` varchar(50) NOT NULL,
  `remoteAddress` varchar(50) DEFAULT NULL COMMENT 'Ip address of user is stored while creating session.',
  `status` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `expiry` timestamp NULL DEFAULT NULL,
  `accessToken` varchar(300) DEFAULT NULL,
  `refreshToken` varchar(300) DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ownerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_idx` (`ownerId`),
  CONSTRAINT `administrator_session_ibfk_1` FOREIGN KEY (`ownerId`) REFERENCES `administrator` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `administrator_session` */

/*Table structure for table `authority` */

DROP TABLE IF EXISTS `authority`;

CREATE TABLE `authority` (
  `id` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'id is the name of endpoint sepcified by deveoper in code example ''post-users'' or ''update-user-by-id''',
  `serviceId` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `name` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`,`serviceId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Authority is basicallly an endpoint of api.';

/*Data for the table `authority` */

insert  into `authority`(`id`,`serviceId`,`name`,`description`) values 
('administrators-authenticate','users-service','authenticateAdministrator','{POST /administrators/authenticate}'),
('administrators-delete-by-id','users-service','deleteAdministratorById','{DELETE /administrators/{id}}'),
('administrators-get','users-service','getAdministrators','{GET /administrators/}'),
('administrators-get-by-id','users-service','getAdministratorById','{GET /administrators/{id}}'),
('administrators-post','users-service','postAdministrator','{POST /administrators/register}'),
('administrators-put-by-id','users-service','putAdministratorById','{PUT /administrators/{id}}'),
('authorities-delete-by-id','users-service','deleteAuthorityById','{DELETE /authorities/{id}}'),
('authorities-get','users-service','getAuthorities','{GET /authorities/}'),
('authorities-get-by-id','users-service','getAuthorityById','{GET /authorities/{id}}'),
('authorities-post','users-service','postAuthority','{POST /authorities}'),
('authorities-put-by-id','users-service','putAuthorityById','{PUT /authorities/{id}}'),
('clients-authenticate','users-service','authenticateClient','{POST /clients/authenticate}'),
('clients-delete-by-id','users-service','deleteClientById','{DELETE /clients/{id}}'),
('clients-get','users-service','getClients','{GET /clients/}'),
('clients-get-by-id','users-service','getClientById','{GET /clients/{id}}'),
('clients-post','users-service','postClient','{POST /clients/register}'),
('clients-put-by-id','users-service','putClientById','{PUT /clients/{id}}'),
('clients-session-details','users-service','getSessionDetails','{POST /clients/client/details}'),
('clients-session-refresh','users-service','refreshSession','{POST /clients/session/refresh}'),
('customers-authenticate','users-service','authenticateCustomer','{POST /customers/authenticate}'),
('customers-delete-by-id','users-service','deleteCustomerById','{DELETE /customers/{id}}'),
('customers-get','users-service','getCustomers','{GET /customers/}'),
('customers-get-by-id','users-service','getCustomerById','{GET /customers/{id}}'),
('customers-post','users-service','postCustomer','{POST /customers/register}'),
('customers-put-by-id','users-service','putCustomerById','{PUT /customers/{id}}'),
('roles-delete-authorities-by-id','users-service','deleteRoleAuthority','{DELETE /roles/{roleId}/authorities/{authorityId}{serviceId}}'),
('roles-delete-by-id','users-service','deleteRoleById','{DELETE /roles/{id}}'),
('roles-get','users-service','getRoles','{GET /roles/}'),
('roles-get-authorities-by-roleId','users-service','getRoleAuthoritiesByRoleId','{GET /roles/{roleId}/authorities}'),
('roles-get-by-id','users-service','getRoleById','{GET /roles/{id}}'),
('roles-post','users-service','postRole','{POST /roles}'),
('roles-post-authorities-by-roleId','users-service','postRoleAuthority','{POST /roles/{roleId}/authorities}'),
('roles-put-by-id','users-service','putRoleById','{PUT /roles/{id}}'),
('session-details-administrator','users-service','getSessionDetailsAdministrator','{POST /sessions/details/administrator}'),
('session-details-client','users-service','getSessionDetailsClient','{POST /sessions/details/client}'),
('session-details-customer','users-service','getSessionDetailsCustomer','{POST /sessions/details/customer}');

/*Table structure for table `cart` */

DROP TABLE IF EXISTS `cart`;

CREATE TABLE `cart` (
  `id` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `state` varchar(100) DEFAULT NULL,
  `customerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'One buyer can only have one editable cart at one time.',
  PRIMARY KEY (`id`),
  KEY `buyerId` (`customerId`),
  CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`customerId`) REFERENCES `customer` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cart` */

/*Table structure for table `cart_product` */

DROP TABLE IF EXISTS `cart_product`;

CREATE TABLE `cart_product` (
  `id` varchar(50) NOT NULL,
  `quantity` int DEFAULT NULL COMMENT 'Once the cart is order the quantity is subtracted from option.',
  `cartId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `prodcutId` varchar(50) NOT NULL COMMENT 'A cart can have same product listed multiple times with different options. ',
  PRIMARY KEY (`id`),
  KEY `order_product_product_fk_idx` (`prodcutId`),
  KEY `order_product_cart_f` (`cartId`),
  CONSTRAINT `order_product_cart_f` FOREIGN KEY (`cartId`) REFERENCES `cart` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_product_product_fk0` FOREIGN KEY (`prodcutId`) REFERENCES `product` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `cart_product` */

/*Table structure for table `cart_product_option` */

DROP TABLE IF EXISTS `cart_product_option`;

CREATE TABLE `cart_product_option` (
  `id` varchar(45) NOT NULL,
  `cartProductId` varchar(50) NOT NULL,
  `productIOptionId` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cart_product_option_product_option_fk_idx` (`productIOptionId`),
  KEY `cart_product_option_cart_product_fk` (`cartProductId`),
  CONSTRAINT `cart_product_option_cart_product_fk` FOREIGN KEY (`cartProductId`) REFERENCES `cart_product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cart_product_option_product_option_fk` FOREIGN KEY (`productIOptionId`) REFERENCES `product_option` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='This table is to maintain options selected for a single product. A product in cart can have multiple variants with different options. Like a person can add a red also a blue shirt in the same cart. ';

/*Data for the table `cart_product_option` */

/*Table structure for table `category` */

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `parentCategoryId` varchar(50) DEFAULT NULL COMMENT 'If category is not derived from other category than parentCategoryId would be null. But if it derived from other category like in Monitor->LCD than the parentCategoryId will be set accordingly',
  PRIMARY KEY (`id`),
  KEY `parentCategoryId` (`parentCategoryId`),
  CONSTRAINT `category_ibfk_1` FOREIGN KEY (`parentCategoryId`) REFERENCES `category` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `category` */

/*Table structure for table `client` */

DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `id` varchar(100) NOT NULL,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `name` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `phoneNumber` varchar(30) DEFAULT NULL,
  `phoneNumberVerified` tinyint(1) DEFAULT '0',
  `email` varchar(300) DEFAULT NULL,
  `emailverified` tinyint(1) DEFAULT '0',
  `deactivated` tinyint(1) DEFAULT '0',
  `locked` tinyint(1) DEFAULT '0',
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `roleId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`),
  KEY `role` (`roleId`),
  CONSTRAINT `client_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='User is the person who can register on the platform then can add/manage products, stores and its agents on sore managers.';

/*Data for the table `client` */

insert  into `client`(`id`,`username`,`password`,`name`,`phoneNumber`,`phoneNumberVerified`,`email`,`emailverified`,`deactivated`,`locked`,`created`,`updated`,`roleId`) values 
('0c225e0f-0588-4740-9aad-bf7db071ddb4','sarosh','$2a$10$Vbphfsq9Fz9hpucyo6aNQum6Se/txQ2AbjKwIviypRH0wjLWaygce','Sarosh',NULL,0,'saroshtariq77@gmail.com',0,0,0,'2021-02-19 09:02:53','2021-02-19 09:02:53','ADMINISTRATOR'),
('14e1f7b4-be83-46a2-a388-4c83f027c816','mohsin','$2a$10$pbw4Q.wnF4onHZuH2MxvyeTEDQzPPCr9m8ZPxWc0pe..d3GgO40He','Mohsin',NULL,0,'mohsin.ali1@kalsym.com',0,0,0,'2021-02-19 06:12:59','2021-02-19 06:12:59','STORE_OWNER'),
('238091a2-7aab-4219-b7e2-ff05cbc02488','test','$2a$10$NxBG7CmPdAv/12Ld.QglwuLGraosK.Gy7.mCA8eWb7R1JqIONDMNG','test',NULL,0,'test@gmail.com',0,0,0,'2021-02-19 06:20:49','2021-02-19 06:20:49','STORE_OWNER'),
('3ebd1691-3645-4118-a003-e1688ce06de1','we','$2a$10$oqOSPoPp7dtgJ5UNO5FUEu8/.wVwpw8HUxxZB14Fv2FqgUkgza.zm','ewwed',NULL,0,'wed',0,0,0,'2021-02-19 10:49:44','2021-02-19 10:49:44','STORE_OWNER'),
('7019bdcc-25a9-4b0f-b619-555307c536fc','asd','$2a$10$k6t.nbx0aLQKGIZQuEX9jO27RmU3T1mVBq05rKH6GPc2P5KO5MoBy','ads',NULL,0,'sad@gmail.com',0,NULL,0,'2021-02-22 09:39:55','2021-02-22 09:39:55','STORE_OWNER'),
('d2aa4feb-736f-4258-a1b4-8cb1fef15826','mohsin-ks','$2a$10$LW.wnW22vZFyiMBTQ9X0zOfhUigiagWO/EA2a6iilypGZaXrim1hy','Mohsin',NULL,0,'mohsin.ali@kalsym.com',0,0,0,'2021-02-19 05:49:39','2021-02-19 05:49:39','STORE_OWNER'),
('f905d7da-52df-45f1-83fa-ab1af0c16a28','sad','$2a$10$1NCgBLiTMPva9KaLrF3u/eEKIFxmx8d3h4YJPuOPvRaZAb9tw87RW','sad',NULL,0,'mohsin.]',0,0,0,'2021-02-19 10:52:51','2021-02-19 10:52:51','STORE_OWNER');

/*Table structure for table `client_session` */

DROP TABLE IF EXISTS `client_session`;

CREATE TABLE `client_session` (
  `id` varchar(50) NOT NULL,
  `remoteAddress` varchar(50) DEFAULT NULL COMMENT 'Ip address of user is stored while creating session.',
  `status` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `accessToken` varchar(300) DEFAULT NULL,
  `refreshToken` varchar(300) DEFAULT NULL,
  `expiry` timestamp NULL DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ownerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_idx` (`ownerId`),
  CONSTRAINT `client_session_ibfk_1` FOREIGN KEY (`ownerId`) REFERENCES `client` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `client_session` */

insert  into `client_session`(`id`,`remoteAddress`,`status`,`username`,`accessToken`,`refreshToken`,`expiry`,`created`,`updated`,`ownerId`) values 
('051dc76a-8d5f-49a7-ac0f-73b267aa225c','127.0.0.1','ACTIVE','sarosh','W0JAMjdjZmY2NzE=','W0JANzYwMDFlZjU=','2021-02-23 12:18:13','2021-02-23 11:18:13','2021-02-23 11:18:13','0c225e0f-0588-4740-9aad-bf7db071ddb4'),
('1a1a5e74-2740-45ed-bab4-e7291324afb2','182.180.53.204','ACTIVE','test','W0JANzAzM2JmOTY=','W0JANzllZjI3Yzc=','2021-02-23 15:56:52','2021-02-23 11:56:52','2021-02-23 11:56:52','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('4251ea19-1838-4ab4-b747-d56ba16fbce7','182.180.53.204','ACTIVE','test','W0JANTBmYjMzYjk=','W0JANmUzNzBkNTM=','2021-02-23 15:37:06','2021-02-23 11:37:06','2021-02-23 11:37:06','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('56fe1597-9e8c-4f8c-8f02-61c3470848d1','182.180.53.204','ACTIVE','test','W0JANmUyZmE5YjU=','W0JAMTEzYzY2NQ==','2021-02-23 16:22:03','2021-02-23 12:22:03','2021-02-23 12:22:03','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('57d00fb4-bbf9-4825-82c5-1fe82d9fcb9e','182.180.53.204','ACTIVE','test','W0JAMTQyOTNiYWM=','W0JAN2FhM2ZmNDQ=','2021-02-23 15:56:24','2021-02-23 11:56:24','2021-02-23 11:56:24','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('ea2047a4-8f4d-4081-b48d-62df4bd93d3b','182.180.53.204','ACTIVE','test','W0JANzc1N2UzN2U=','W0JANmFjODQ3MTU=','2021-02-23 16:31:52','2021-02-23 12:31:52','2021-02-23 12:31:52','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('ec445bf9-bad5-494f-81c9-52ee6c141fe7','182.180.53.204','ACTIVE','test','W0JAMTllNjVhOWE=','W0JANjZmYjg4NzE=','2021-02-23 16:38:50','2021-02-23 12:38:50','2021-02-23 12:38:50','238091a2-7aab-4219-b7e2-ff05cbc02488');

/*Table structure for table `coupon` */

DROP TABLE IF EXISTS `coupon`;

CREATE TABLE `coupon` (
  `id` varchar(50) NOT NULL,
  `code` varchar(50) DEFAULT NULL,
  `startDate` timestamp NULL DEFAULT NULL,
  `endDate` timestamp NULL DEFAULT NULL,
  `referal` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `coupon` */

/*Table structure for table `customer` */

DROP TABLE IF EXISTS `customer`;

CREATE TABLE `customer` (
  `id` varchar(100) NOT NULL,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `name` varchar(200) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `phoneNumber` varchar(30) DEFAULT NULL,
  `phoneNumberVerified` tinyint(1) DEFAULT '0',
  `email` varchar(300) DEFAULT NULL,
  `emailverified` tinyint(1) DEFAULT '0',
  `deactivated` tinyint(1) DEFAULT '0',
  `locked` tinyint(1) DEFAULT '0',
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `roleId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_username` (`username`),
  KEY `role` (`roleId`),
  CONSTRAINT `cust_role_fk1` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Customer is a person who can make purchases from the platfrom with or without creating id.';

/*Data for the table `customer` */

/*Table structure for table `customer_session` */

DROP TABLE IF EXISTS `customer_session`;

CREATE TABLE `customer_session` (
  `id` varchar(50) NOT NULL,
  `remoteAddress` varchar(50) DEFAULT NULL COMMENT 'Ip address of user is stored while creating session.',
  `status` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `accessToken` varchar(300) DEFAULT NULL,
  `refreshToken` varchar(300) DEFAULT NULL,
  `expiry` timestamp NULL DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ownerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_idx` (`ownerId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `customer_session` */

insert  into `customer_session`(`id`,`remoteAddress`,`status`,`username`,`accessToken`,`refreshToken`,`expiry`,`created`,`updated`,`ownerId`) values 
('07befc0e-002f-4f2b-8647-962a66a034e0','182.180.53.204','ACTIVE','test','W0JANWY5MThmMWY=','W0JANTNjYzFmY2U=','2021-02-22 16:20:42','2021-02-22 12:20:42','2021-02-22 12:20:42','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('2573af6a-444a-4b93-968e-1b9b4730a579','182.180.53.204','ACTIVE','test','W0JANjFhNDIwMWY=','W0JAN2JlOWZmMTU=','2021-02-22 16:21:02','2021-02-22 12:21:02','2021-02-22 12:21:02','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('3a57d55f-e023-469b-846e-90f14752bcd1','182.180.53.204','ACTIVE','test','W0JAMWI5Y2EzMw==','W0JAM2JlMGMyNA==','2021-02-22 16:21:38','2021-02-22 12:21:38','2021-02-22 12:21:38','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('64f5b251-5ccf-406f-b214-44f488f32550','182.180.53.204','ACTIVE','test','W0JAOWIyNWNmMg==','W0JANjk5ZmJjNWM=','2021-02-22 16:20:26','2021-02-22 12:20:26','2021-02-22 12:20:26','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('838414e5-3e4f-4aa8-9529-b7a69188e14d','182.180.53.204','ACTIVE','test','W0JAMjkwMzMxNzQ=','W0JAZGEyMTY3Yw==','2021-02-22 16:22:18','2021-02-22 12:22:18','2021-02-22 12:22:18','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('87c96806-84ec-4a53-8dea-3fcc2bd99107','111.119.177.17','ACTIVE','test',NULL,NULL,'2021-02-19 15:29:22','2021-02-19 11:29:22','2021-02-19 11:29:22','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('8b8fcf31-16a7-4c77-8b75-df6ea8c2670f','182.180.53.204','ACTIVE','test','W0JAMWY5MjUyMzQ=','W0JANDk3YjVmODE=','2021-02-22 16:17:57','2021-02-22 12:17:57','2021-02-22 12:17:57','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('8dd691d0-d630-48cc-aa61-237aa093e013','182.180.53.204','ACTIVE','sarosh','W0JAM2Q4YWU2YjY=','W0JANmQ1ZTVhNTc=','2021-02-23 10:01:43','2021-02-23 06:01:43','2021-02-23 06:01:43','0c225e0f-0588-4740-9aad-bf7db071ddb4'),
('b44cd0c4-c642-4ac6-aed8-762eb44a4ef6','182.180.53.204','ACTIVE','sarosh',NULL,NULL,'2021-02-19 15:33:22','2021-02-19 11:33:22','2021-02-19 11:33:22','0c225e0f-0588-4740-9aad-bf7db071ddb4'),
('b53e7e5b-85cf-48df-8d79-2d7da5940360','182.180.53.204','ACTIVE','test','W0JAMjMxMzJlOWM=','W0JAMWUzMzA0ZQ==','2021-02-22 16:21:20','2021-02-22 12:21:20','2021-02-22 12:21:20','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('c28122e4-b605-4035-b35b-51daca9a50cd','182.180.53.204','ACTIVE','test','W0JANWQ3ZjhlNTA=','W0JANmQxMDRjZjY=','2021-02-22 16:29:28','2021-02-22 12:29:28','2021-02-22 12:29:28','238091a2-7aab-4219-b7e2-ff05cbc02488'),
('cc9b0801-7669-4280-a8a9-0d7ed3b52133','115.186.136.106','ACTIVE','sarosh','W0JAN2U2M2M3YTg=','W0JAMjdkMDgzNjI=','2021-02-22 14:27:21','2021-02-22 10:27:21','2021-02-22 10:27:21','0c225e0f-0588-4740-9aad-bf7db071ddb4');

/*Table structure for table `discount` */

DROP TABLE IF EXISTS `discount`;

CREATE TABLE `discount` (
  `id` varchar(50) DEFAULT NULL,
  `amount` decimal(15,9) DEFAULT NULL,
  `unit` enum('PERCENTAGE') CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `startDate` timestamp NULL DEFAULT NULL,
  `endDate` timestamp NULL DEFAULT NULL,
  `storeId` varchar(50) DEFAULT NULL,
  `productId` varchar(50) DEFAULT NULL,
  KEY `storeId` (`storeId`),
  KEY `productId` (`productId`),
  CONSTRAINT `discount_ibfk_1` FOREIGN KEY (`storeId`) REFERENCES `store` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `discount_ibfk_2` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `discount` */

/*Table structure for table `order_payment_detail` */

DROP TABLE IF EXISTS `order_payment_detail`;

CREATE TABLE `order_payment_detail` (
  `accountName` varchar(100) DEFAULT NULL,
  `gatewayId` varchar(50) DEFAULT NULL,
  `couponId` varchar(50) DEFAULT NULL,
  `time` timestamp NULL DEFAULT NULL,
  `orderId` varchar(50) NOT NULL,
  PRIMARY KEY (`orderId`),
  KEY `couponId` (`couponId`),
  CONSTRAINT `order_payment_detail_ibfk_1` FOREIGN KEY (`couponId`) REFERENCES `coupon` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `order_payment_detail_ibfk_2` FOREIGN KEY (`orderId`) REFERENCES `cart` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `order_payment_detail` */

/*Table structure for table `order_shipping_detail` */

DROP TABLE IF EXISTS `order_shipping_detail`;

CREATE TABLE `order_shipping_detail` (
  `receiverName` varchar(100) DEFAULT NULL,
  `phoneNumber` varchar(20) DEFAULT NULL,
  `address` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `zipcode` varchar(20) DEFAULT NULL,
  `orderId` varchar(50) NOT NULL,
  KEY `order_shipping_detail_order_fk_idx` (`orderId`),
  CONSTRAINT `order_shipping_detail_ibfk_1` FOREIGN KEY (`orderId`) REFERENCES `cart` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `order_shipping_detail` */

/*Table structure for table `product` */

DROP TABLE IF EXISTS `product`;

CREATE TABLE `product` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `stock` int DEFAULT NULL,
  `storeId` varchar(50) DEFAULT NULL COMMENT 'A product can exist independently without a store.',
  `categoryId` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `category_fk_idx` (`categoryId`),
  KEY `store_fk_idx` (`storeId`),
  CONSTRAINT `product_category_fk` FOREIGN KEY (`categoryId`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `product_store_fk` FOREIGN KEY (`storeId`) REFERENCES `store` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `product` */

/*Table structure for table `product_asset` */

DROP TABLE IF EXISTS `product_asset`;

CREATE TABLE `product_asset` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL COMMENT 'Name of asset',
  `location` varchar(300) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL COMMENT 'location can be url or file path of specified asset.',
  `productId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `productId` (`productId`),
  CONSTRAINT `product_asset_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='This table contains assets belonging to each product in product table. A product can have multiple assets.';

/*Data for the table `product_asset` */

/*Table structure for table `product_feature` */

DROP TABLE IF EXISTS `product_feature`;

CREATE TABLE `product_feature` (
  `id` varchar(45) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `imageUrl` varchar(500) DEFAULT NULL,
  `productId` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `productId` (`productId`),
  CONSTRAINT `product_feature_ibfk_1` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='Feature in every product come with the product and user does not have option to select or unselect them.';

/*Data for the table `product_feature` */

/*Table structure for table `product_option` */

DROP TABLE IF EXISTS `product_option`;

CREATE TABLE `product_option` (
  `id` varchar(45) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `imageUrl` varchar(500) DEFAULT NULL,
  `stock` int DEFAULT NULL COMMENT 'In case an option is added to a product than the stock depends on stock of option. ',
  `type` varchar(45) NOT NULL COMMENT 'Type of product option shows if option is like a radio or check. Radio type options can only be selected from multiple. A check type option user can select one,all or none. For example water cooling option in a PC will be check type but ram option will be radio type.',
  `optionCategory` varchar(100) NOT NULL COMMENT 'An option can be categorized into color, size or anything that contains multiple options.',
  `productId` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `product_option_product_fk` (`productId`),
  CONSTRAINT `product_option_product_fk` FOREIGN KEY (`productId`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `product_option` */

/*Table structure for table `product_review` */

DROP TABLE IF EXISTS `product_review`;

CREATE TABLE `product_review` (
  `productId` varchar(50) NOT NULL,
  `customerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `rating` int DEFAULT NULL,
  `review` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`productId`,`customerId`),
  KEY `product_review_buyerfk_idx` (`customerId`),
  CONSTRAINT `product_review_buyerfk` FOREIGN KEY (`customerId`) REFERENCES `customer` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `product_review_productfk` FOREIGN KEY (`productId`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `product_review` */

/*Table structure for table `refreshTokencustomer_session` */

DROP TABLE IF EXISTS `refreshTokencustomer_session`;

CREATE TABLE `refreshTokencustomer_session` (
  `id` varchar(50) NOT NULL,
  `remoteAddress` varchar(50) DEFAULT NULL COMMENT 'Ip address of user is stored while creating session.',
  `status` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `username` varchar(50) DEFAULT NULL,
  `expiry` timestamp NULL DEFAULT NULL,
  `accessToken` varchar(300) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `refreshToken` varchar(300) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ownerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_idx` (`ownerId`),
  CONSTRAINT `refreshTokencustomer_session_ibfk_1` FOREIGN KEY (`ownerId`) REFERENCES `customer` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `refreshTokencustomer_session` */

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL COMMENT 'id is name of role. A role can have multiple authorities.',
  `allowedSimoultaneousSessions` int DEFAULT '1' COMMENT 'User cannot have more than allowed session at one time.',
  `name` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `description` varchar(300) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `parentRoleId` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parentRoleId` (`parentRoleId`),
  CONSTRAINT `role_ibfk_1` FOREIGN KEY (`parentRoleId`) REFERENCES `role` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `role` */

insert  into `role`(`id`,`allowedSimoultaneousSessions`,`name`,`description`,`parentRoleId`) values 
('ADMINISTRATOR',1,'Administrator',NULL,NULL),
('CUSTOMER',1,'Customer',NULL,'ADMINISTRATOR'),
('STORE_MANAGER',1,'Store Manager',NULL,'STORE_OWNER'),
('STORE_OWNER',1,'Store Owner',NULL,'ADMINISTRATOR');

/*Table structure for table `role_authority` */

DROP TABLE IF EXISTS `role_authority`;

CREATE TABLE `role_authority` (
  `roleId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `authorityId` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `serviceId` varchar(100) NOT NULL,
  PRIMARY KEY (`roleId`,`authorityId`,`serviceId`),
  KEY `authority` (`authorityId`),
  KEY `role_authority_ibfk_2` (`authorityId`,`serviceId`),
  CONSTRAINT `role_authority_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `role_authority_ibfk_2` FOREIGN KEY (`authorityId`, `serviceId`) REFERENCES `authority` (`id`, `serviceId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='A role has multiple authorities.';

/*Data for the table `role_authority` */

insert  into `role_authority`(`roleId`,`authorityId`,`serviceId`) values 
('ADMINISTRATOR','roles-get','users-service');

/*Table structure for table `session` */

DROP TABLE IF EXISTS `session`;

CREATE TABLE `session` (
  `id` varchar(50) NOT NULL,
  `remoteAddress` varchar(50) DEFAULT NULL COMMENT 'Ip address of user is stored while creating session.',
  `status` varchar(45) DEFAULT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `ownerId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `status_UNIQUE` (`status`),
  KEY `fk_user_idx` (`ownerId`),
  CONSTRAINT `fk_user` FOREIGN KEY (`ownerId`) REFERENCES `client` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `session_ibfk_1` FOREIGN KEY (`ownerId`) REFERENCES `customer` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `session_ibfk_2` FOREIGN KEY (`ownerId`) REFERENCES `administrator` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `session` */

/*Table structure for table `store` */

DROP TABLE IF EXISTS `store`;

CREATE TABLE `store` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `address` varchar(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL,
  `userId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `sellerId` (`userId`),
  CONSTRAINT `store_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `client` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Data for the table `store` */

/*Table structure for table `user_channel` */

DROP TABLE IF EXISTS `user_channel`;

CREATE TABLE `user_channel` (
  `id` varchar(50) NOT NULL,
  `created` timestamp NULL DEFAULT NULL,
  `updated` timestamp NULL DEFAULT NULL,
  `refId` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL COMMENT 'ref id is users mobile number or page id.',
  `userId` varchar(50) DEFAULT NULL,
  `channelName` varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci DEFAULT NULL COMMENT 'This will be linked to mongo db.',
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  CONSTRAINT `user_channel_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `client` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='User channels are channels which a user has available, a user can have muliple channels.';

/*Data for the table `user_channel` */

/* Procedure structure for procedure `get_users_without_password` */

/*!50003 DROP PROCEDURE IF EXISTS  `get_users_without_password` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `get_users_without_password`(IN username varchar(256))
BEGIN
		SELECT `users`.`id`, `users`.`username`, `users`.`firstname`,`users`.`lastname`,`users`.`email`,`users`.`locked`,
		`users`.`expiry`,`users`.`created`,`users`.`updated`  FROM `users` where `users`.`username`=`username` or `username` is null;
		SELECT `users`.`id`, `users`.`username`, `users`.`firstname`,`users`.`lastname`,`users`.`email`,`users`.`locked`,
		`users`.`expiry`,`users`.`created`,`users`.`updated`  FROM `users` WHERE `users`.`username`=`username` OR `username` IS NULL;
	END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
