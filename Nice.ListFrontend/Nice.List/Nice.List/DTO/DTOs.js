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
//# sourceMappingURL=DTOs.js.map