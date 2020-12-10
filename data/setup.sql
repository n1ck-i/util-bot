CREATE TABLE `ubot`.`Tutor` (
	`id` VARCHAR(40) NOT NULL,
	`Name` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`));

CREATE TABLE `ubot`.`Professor` (
	`id` VARCHAR(40) NOT NULL,
	`Name` VARCHAR(50) NOT NULL,
	`Subject` VARCHAR(20) NOT NULL,
	`ChanelId` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id`));

CREATE TABLE `ubot`.`Meeting` (
	`id` VARCHAR(40) NOT NULL,
	`refTutorId` VARCHAR(40) NULL,
	`refProfId` VARCHAR(40) NULL,
	`GroupNumber` TINYINT(1) NOT NULL,
	`Link` VARCHAR(255) NOT NULL,
	`Weekday` TINYINT(1) NOT NULL,
	`StartTime` TIME NOT NULL,
	PRIMARY KEY (`id`));

CREATE TABLE `ubot`.`User` (
	`id` VARCHAR(40) NOT NULL,
	`DiscordId` VARCHAR(80) NOT NULL,
	PRIMARY KEY (`id`));

CREATE TABLE `ubot`.`UserToMeeting` (
	`id` VARCHAR(40) NOT NULL,
	`refUserId` VARCHAR(40) NOT NULL,
	`refMeetingId` VARCHAR(40) NOT NULL,
	PRIMARY KEY (`id`));
