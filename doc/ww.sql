CREATE TABLE `TypeClass` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Idx` int(11) NOT NULL default 0,
  `Name` varchar(64) NOT NULL,
  `Ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  KEY (`Ts`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `ItemType` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `Type` int(11) NOT NULL,
  `Idx` int(11) NOT NULL default 0,
  `ClassId` int(11) NOT NULL default 0,
  `Name` varchar(64) NOT NULL,
  `Unit` varchar(20) NOT NULL,
  `Price` float(10,2) NOT NULL default 0,
  `Ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `ItemDetail` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ItemId` int(11) NOT NULL default 0,
  `TypeId` int(11) NOT NULL default 0, 
  `Price` float(10,2) NOT NULL default 0,
  `Quantity` float(10,2) NOT NULL default 0,
  `Amount` float(10,2) NOT NULL default 0,
  `Remark` varchar(1024) NOT NULL default '',
  `Ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  KEY (`ItemId`, `TypeId`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `Item` (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `ItemDate` varchar(10) NOT NULL default '',
  `PrepareAmount` float(10,2) NOT NULL  default 0,
  `TotalIncoming` float(10,2) NOT NULL  default 0,
  `TotalOutgoing` float(10,2) NOT NULL  default 0,
  `RemainAmount` float(10,2) NOT NULL  default 0,
  `IncomingRemark` varchar(1024) NOT NULL default '',
  `OutgoingRemark` varchar(1024) NOT NULL default '',
  `CreateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `Ts` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`Id`),
  UNIQUE (`ItemDate`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;