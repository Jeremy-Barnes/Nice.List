CREATE TABLE users(
	userID SERIAL NOT NULL PRIMARY KEY,
	firstName VARCHAR(24),
	lastName VARCHAR(24),
	emailAddress VARCHAR(100) NOT NULL,
	password VARCHAR NOT NULL,
	salt VARCHAR NOT NULL,
	city VARCHAR(50),
	state VARCHAR(50),
	country VARCHAR(50),
	postcode VARCHAR(20),
	tokenSelector VARCHAR,
	tokenValidator VARCHAR,
	pictureURL VARCHAR,
	CONSTRAINT uk_email UNIQUE (emailAddress)
);

CREATE TABLE wishlistItems(
	wishlistItemID SERIAL NOT NULL PRIMARY KEY,
	requesterUserID INT NOT NULL REFERENCES users(userid),
	URL VARCHAR,
	imageURL VARCHAR,
	itemName VARCHAR(200),
	comment VARCHAR(500),
	isBought BOOLEAN NOT NULL,
	purchaserUserID INT REFERENCES users(userID),
	dateAdded DATE NOT NULL,
	price NUMERIC(6,2),
	want INT
);

CREATE TABLE friendships(
	friendshipID SERIAL NOT NULL PRIMARY KEY,
	requesterUserID INT NOT NULL REFERENCES users(userID),
  requestedUserID INT NOT NULL REFERENCES users(userID),
	accepted BOOLEAN NOT NULL,
	CONSTRAINT friendshipLink UNIQUE (requesterUserID, requestedUserID)
);

CREATE TABLE friendGroups (
	friendGroupID SERIAL NOT NULL PRIMARY KEY,
	ownerID INT NOT NULL REFERENCES users(userID),
	name VARCHAR(50) NOT NULL, 
	CONSTRAINT friendGroupingNamePerOwner UNIQUE (ownerID, name)
);

CREATE TABLE friendGroupingEntry (
	friendGroupingEntryID SERIAL NOT NULL PRIMARY KEY,
	friendGroupID INT NOT NULL REFERENCES friendGroups(friendGroupID),
	friendID INT NOT NULL REFERENCES users(userID)
);
 
 CREATE TABLE toDoItem (
	toDoEntryID SERIAL NOT NULL PRIMARY KEY,
	friendID INT NOT NULL REFERENCES users(userID),
	ownerID INT NOT NULL REFERENCES users(userID),
	done BOOLEAN NOT NULL
);