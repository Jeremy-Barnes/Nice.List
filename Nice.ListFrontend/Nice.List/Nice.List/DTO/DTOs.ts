class User {
    userID: number;
    userName: string;
    firstName: string;
    lastName: string;
    emailAddress: string;
    password: string;
    city: string;
    state: string;
    country: string;
    postcode: string;
}

class UserModel {
    userID: KnockoutObservable<number> = ko.observable(0);
    userName: KnockoutObservable<string> = ko.observable("");
    firstName: KnockoutObservable<string> = ko.observable("");
    lastName: KnockoutObservable<string> = ko.observable("");
    emailAddress: KnockoutObservable<string> = ko.observable("");
    password: KnockoutObservable<string> = ko.observable("");
    city: KnockoutObservable<string> = ko.observable("");
    state: KnockoutObservable<string> = ko.observable("");
    country: KnockoutObservable<string> = ko.observable("");
    postcode: KnockoutObservable<string> = ko.observable("");
}


