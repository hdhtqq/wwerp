/*
MySQL Data Transfer
Source Host: localhost
Source Database: ww
Target Host: localhost
Target Database: ww
Date: 2014-10-17 19:46:53
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for itemtype
-- ----------------------------
CREATE TABLE `itemtype` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Type` int(11) NOT NULL,
  `Name` varchar(64) NOT NULL,
  `Unit` varchar(20) NOT NULL,
  `Price` int(11) NOT NULL,
  `Ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `itemtype` VALUES ('1', '1', '大人', '人', '3800', '2014-10-29 19:45:05');
INSERT INTO `itemtype` VALUES ('2', '1', '小孩', '人', '0', '2014-10-17 19:45:25');
INSERT INTO `itemtype` VALUES ('3', '1', '饮料', '瓶', '100', '2014-10-17 19:45:41');
INSERT INTO `itemtype` VALUES ('4', '2', '青菜', '斤', '200', '2014-10-17 19:45:55');
INSERT INTO `itemtype` VALUES ('5', '2', '冻货', '斤', '1200', '2014-10-17 19:46:24');
