class User {
    userID: number;
    firstName: string;
    lastName: string;
    emailAddress: string;
    password: string;
    city: string;
    state: string;
    country: string;
    postcode: string;
    pictureURL: string;

    friends: User[];
    //friendsOf: User[];
    pendingRequests: User[];
    requestsToReview: User[];
}

class UserModel {
    userID: KnockoutObservable<number> = ko.observable(0);
    firstName: KnockoutObservable<string> = ko.observable("");
    lastName: KnockoutObservable<string> = ko.observable("");
    emailAddress: KnockoutObservable<string> = ko.observable("");
    password: KnockoutObservable<string> = ko.observable("");
    city: KnockoutObservable<string> = ko.observable("");
    state: KnockoutObservable<string> = ko.observable("");
    country: KnockoutObservable<string> = ko.observable("");
    postcode: KnockoutObservable<string> = ko.observable("");
    pictureURL: KnockoutObservable<string> = ko.observable("");
}

class Token {
    selector: string;
    validator: string;
}

class UserFriendAddContainer {
    user: User;
    requestedEmailAddress: string;
}