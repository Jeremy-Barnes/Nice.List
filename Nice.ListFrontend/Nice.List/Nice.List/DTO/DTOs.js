var User = (function () {
    function User() {
    }
    return User;
})();
var UserModel = (function () {
    function UserModel() {
        this.userID = ko.observable(0);
        this.firstName = ko.observable("");
        this.lastName = ko.observable("");
        this.emailAddress = ko.observable("");
        this.password = ko.observable("");
        this.city = ko.observable("");
        this.state = ko.observable("");
        this.country = ko.observable("");
        this.postcode = ko.observable("");
        this.pictureURL = ko.observable("");
        this.friends = ko.observableArray(null);
        this.pendingRequests = ko.observableArray(null);
        this.requestsToReview = ko.observableArray(null);
        this.wishList = ko.observableArray(null);
    }
    return UserModel;
})();
var Token = (function () {
    function Token() {
    }
    return Token;
})();
var UserFriendAddContainer = (function () {
    function UserFriendAddContainer() {
    }
    return UserFriendAddContainer;
})();
var Friendship = (function () {
    function Friendship() {
    }
    return Friendship;
})();
var WishListItem = (function () {
    function WishListItem() {
        this.itemName = "";
    }
    return WishListItem;
})();
var WishListItemModel = (function () {
    function WishListItemModel() {
        this.wishListItemID = ko.observable(0);
        this.requesterUserID = ko.observable(0);
        this.URL = ko.observable(null);
        this.imageURL = ko.observable(null);
        this.itemName = ko.observable(null);
        this.comment = ko.observable(null);
        this.isBought = ko.observable(false);
        this.purchaserUserID = ko.observable(null);
        this.dateAdded = ko.observable(null);
        this.price = ko.observable(null);
        this.want = ko.observable(0);
    }
    return WishListItemModel;
})();
//# sourceMappingURL=DTOs.js.map