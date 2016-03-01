var ServiceMethods = (function () {
    function ServiceMethods() {
    }
    ServiceMethods.prototype.getUserFromToken = function (selector, validator) {
        var valid = { selector: selector, validator: validator };
        var parameters = JSON.stringify(valid);
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "getUserFromToken",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        return jQuery.ajax(settings).fail(function (request) {
            alert(request);
        });
    };
    ServiceMethods.prototype.updateWishListItem = function (editWishListItem) {
        var parameters = JSON.stringify(ko.mapping.toJS(editWishListItem));
        var settings = {
            url: "http://localhost:8080/api/nice/wishlist/" + "editListItem",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: parameters,
            crossDomain: true
        };
        return jQuery.ajax(settings).fail(function (request) {
            alert(request);
        });
    };
    ServiceMethods.prototype.getUserWishList = function (requester, friend) {
        var req = {
            accepted: true,
            friendshipID: -1,
            requesterUserID: requester.userID(),
            requestedUserID: friend.userID(),
        };
        var parameters = JSON.stringify(req);
        var settings = {
            url: "http://localhost:8080/api/nice/wishlist/" + "getUserWishList",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).fail(function (request) {
            alert(request);
        });
    };
    return ServiceMethods;
})();
//# sourceMappingURL=ServiceMethod.js.map