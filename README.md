
# Util Bot

## Feature


### Request meeting command

Command: !meeting [type] [identifier]

type: ti luds alpro itsec

identifier:
- null -> lecture
- list -> lists all meetings for the current day
- tutor name -> sends specific link
- exercise number -> sends specific link
- starttime -> sends all links wich start at the specified time

	
###	Lecture reminder

Automatically post lecture link 30 mins before 
in the related channel


### Personal reminder

Command: !remind [type] [identifier]

type: ti luds alpro itsec

identifier:
- null -> lecture
- tutor name -> sends specific link
- exercise number -> sends specific link
- starttime -> sends list of available meetings at the time for user to select

When the user is registered a msg will be sent to the channel 
confirming registration and specifying details how to remove reminder (in private messages).

Personal reminders will be send as private messages.


### Remove personal reminder

Command: !remove  [type] [identifier]

type: ti luds alpro itsec

identifier:
- null -> lecture
- tutor name -> sends specific link
- exercise number -> sends specific link
- starttime -> sends list of available meetings at the time for user to select


## Database Structure

Tutor
Name | Type		| Description
-----|------	|--------
ID   | UUID 	| 
Name | String	| Name of tutor

Professor
Name	  | Type		| Description
-----	  |------		|--------
ID   	  | UUID 		| 
Name 	  | String		| Name of professor
Subject   | String		| Subject for professor
ChannelId | String		| discord channel related to the subject

Meetings
Name		| Type		| Description
-----		|------		|--------
ID   		| UUID 		| 
refTutorId  | UUID      | reference id for the tutor
refProfId   | UUID      | reference id for the professor
GroupNumber | tinyint   | group numbering like TI Group 5
Link		| String	| one link for zoom,bbb etc.
WeekDay		| tinytint	| day of the week starting with monday = 0
StartTime	| time		| starttime 

User
Name		| Type		| Description
-----		|------		|--------
ID   		| UUID 		| 
DiscordID   | String    | discord user id

UserToMeeting
Name		 | Type		| Description
-----		 |------	|--------
ID   		 | UUID 	| 
refUserID    | String   | reference id for user
refMeetingID | String   | reference id for meeting
