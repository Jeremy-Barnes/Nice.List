var ServiceMethods = (function () {
    function ServiceMethods() {
    }
    ServiceMethods.doAjax = function (functionName, functionService, parameters) {
        var param = JSON.stringify(parameters);
        var settings = {
            url: ServiceMethods.baseURL + functionService + "/" + functionName,
            type: "POST",
            contentType: "application/json",
            headers: {
                SelectorValidator: ServiceMethods.selectorValidator ? ServiceMethods.selectorValidator[0] + ':' + ServiceMethods.selectorValidator[1] : null,
            },
            success: function (json, status, args) {
                if (args.getResponseHeader("SelectorValidator")) {
                    ServiceMethods.selectorValidator = args.getResponseHeader("SelectorValidator").split(":");
                }
            },
            data: param,
            crossDomain: true,
        };
        return jQuery.ajax(settings);
    };
    ServiceMethods.createUser = function (user) {
        return ServiceMethods.doAjax("createUser", "users", ko.toJS(user));
    };
    ServiceMethods.getUserFromToken = function (selector, validator) {
        return ServiceMethods.doAjax("getUserFromToken", "users", { selector: selector, validator: validator });
    };
    ServiceMethods.changeUserInformation = function (user) {
        var dat = new FormData();
        dat.append("user", JSON.stringify(ko.toJS(user)));
        dat.append("file", jQuery("#file")[0].files[0]);
        var settings = {
            url: ServiceMethods.baseURL + "users/changeUserInformation",
            type: "POST",
            contentType: false,
            processData: false,
            dataType: "json",
            data: dat,
            crossDomain: true,
            headers: {
                SelectorValidator: ServiceMethods.selectorValidator ? ServiceMethods.selectorValidator[0] + ':' + ServiceMethods.selectorValidator[1] : null,
            },
            success: function (json, status, args) {
                if (args.getResponseHeader("SelectorValidator")) {
                    ServiceMethods.selectorValidator = args.getResponseHeader("SelectorValidator").split(":");
                }
            },
        };
        return jQuery.ajax(settings);
    };
    ServiceMethods.logIn = function (user) {
        return ServiceMethods.doAjax("getUserFromLogin", "users", ko.mapping.toJS(user));
    };
    ServiceMethods.addFriend = function (user, friendEmailAddress) {
        var parameters = {};
        parameters["user"] = ko.mapping.toJS(user);
        parameters["requestedEmailAddress"] = friendEmailAddress;
        return ServiceMethods.doAjax("createFriendship", "friends", parameters);
    };
    ServiceMethods.respondToFriendRequest = function (acceptedRequest, requester, user) {
        var req = {
            friendshipID: -1,
            accepted: acceptedRequest,
            requestedUserID: user.userID(),
            requesterUserID: requester.userID()
        };
        return ServiceMethods.doAjax("respondToFriendRequest", "friends", req);
    };
    ServiceMethods.addWishListItem = function (item) {
        item.price(0);
        return ServiceMethods.doAjax("addListItem", "wishlist", ko.mapping.toJS(item));
    };
    ServiceMethods.updateWishListItem = function (editWishListItem) {
        return ServiceMethods.doAjax("editListItem", "wishlist", ko.mapping.toJS(editWishListItem));
    };
    ServiceMethods.getUserWishList = function (requester, friend) {
        var req = {
            accepted: true,
            friendshipID: -1,
            requesterUserID: requester.userID(),
            requestedUserID: friend.userID(),
        };
        return ServiceMethods.doAjax("getUserWishList", "wishlist", req);
    };
    ServiceMethods.baseURL = "http://52.32.150.194:8080/api/nice/"; //http://localhost:8080/api/nice/
    return ServiceMethods;
})();
//# sourceMappingURL=ServiceMethods.js.map