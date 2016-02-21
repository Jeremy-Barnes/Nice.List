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
    pendingRequests: User[];
    requestsToReview: User[];
    wishList: WishListItem[];
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

    friends: KnockoutObservableArray<UserModel> = ko.observableArray<UserModel>(null);
    pendingRequests: KnockoutObservableArray<UserModel> = ko.observableArray<UserModel>(null);
    requestsToReview: KnockoutObservableArray<UserModel> = ko.observableArray<UserModel>(null);
    wishList: KnockoutObservableArray<WishListItemModel> = ko.observableArray<WishListItemModel>(null);
}

class Token {
    selector: string;
    validator: string;
}

class UserFriendAddContainer {
    user: User;
    requestedEmailAddress: string;
}

class Friendship {
    friendshipID: number;
    requesterUserID: number;
    requestedUserID: number;
    accepted: boolean;
}

class WishListItem {
    wishListItemID: number;
    requesterUserID: number;
    URL: string;
    imageURL: string;
    itemName: string = "";
    comment: string;
    isBought: boolean;
    purchaserUserID: number;
    dateAdded: Date;
    price: number;
    want: number;
}

class WishListItemModel {
    wishListItemID: KnockoutObservable<number> = ko.observable(0);
    requesterUserID: KnockoutObservable<number> = ko.observable(0);
    URL: KnockoutObservable<string> = ko.observable(null);
    imageURL: KnockoutObservable<string> = ko.observable(null);
    itemName: KnockoutObservable<string> = ko.observable(null);
    comment: KnockoutObservable<string> = ko.observable(null);
    isBought: KnockoutObservable<boolean> = ko.observable(false);
    purchaserUserID: KnockoutObservable<number> = ko.observable(null);
    dateAdded: KnockoutObservable<Date> = ko.observable(null);
    price: KnockoutObservable<number> = ko.observable(null);
    want: KnockoutObservable<number> = ko.observable(0);
}