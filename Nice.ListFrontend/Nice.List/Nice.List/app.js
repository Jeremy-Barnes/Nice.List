/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />
var page;
$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    page = new App();
    ko.applyBindings(page);
});
var App = (function () {
    function App() {
        var _this = this;
        /******** App status tracking *******/
        this.status = ko.observable(AppStatus.Landing);
        this.friendAddStatus = ko.observable(FriendAddStatus.Waiting);
        this.loadedHTML = new Array();
        /******** Active user data ******/
        this.user = ko.observable(new UserModel());
        this.topFriends = ko.observableArray(null);
        /******** Form Field Bind Vars *******/
        this.friendEmailAddress = ko.observable("");
        this.editWishListItem = ko.observable(new WishListItemModel());
        this.passwordConfirm = ko.observable("");
        this.wishUser = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(function () {
            return _this.user().password() == _this.passwordConfirm();
        }, this);
        ko.computed(function () {
            var user = _this.user();
            if (user != null && user.friends() != null) {
                _this.topFriends.removeAll();
                var max = _this.user().friends().length < 5 ? _this.user().friends().length : 5;
                for (var i = 0; i < max; i++) {
                    _this.topFriends.push(user.friends()[i]);
                }
            }
        }, this);
        ko.computed(function () {
            if (_this.status() != AppStatus.Landing && !_this.loadedHTML[_this.status().toString()]) {
                _this.loadedHTML[_this.status().toString()] = true;
                _this.getTemplateHTML(AppStatus[_this.status()]);
            }
        }, this);
        this.initUser();
        jQuery('#add-friend').on('hidden.bs.modal', function (e) { _this.friendAddStatus(FriendAddStatus.Waiting); _this.friendEmailAddress(""); }); //I'm not happy about this, either
    } //ctor
    App.prototype.initUser = function () {
        var siteCookie = this.findCookie();
        if (siteCookie == null && ServiceMethods.selectorValidator == null) {
            this.status(AppStatus.Landing);
        }
        else {
            var selectorValidator = siteCookie ? siteCookie.split(":") : ServiceMethods.selectorValidator;
            this.getUser(selectorValidator[0], selectorValidator[1]);
        }
    };
    App.prototype.findCookie = function () {
        var usefulCookies = ("; " + document.cookie).split("; nicelist="); //everyone else's garbage ; mine
        if (usefulCookies.length == 2) {
            return usefulCookies[1].split(";")[0]; //mine; other garbage
        }
        else {
            return null;
        }
    };
    App.prototype.getUser = function (selector, validator) {
        var self = this;
        ServiceMethods.getUserFromToken(selector, validator).then(function (o) {
            self.user(ko.mapping.fromJS(o));
            self.wishUser(self.user());
            self.status(AppStatus.Home);
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.createUser = function () {
        var self = this;
        ServiceMethods.createUser(this.user()).then(function (updatedUser) {
            var us = ko.mapping.fromJS(updatedUser);
            self.user(us);
            self.passwordConfirm("");
            $("#sign-up").modal('hide');
            self.status(AppStatus.Account);
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.submitAccountChanges = function () {
        var self = this;
        ServiceMethods.changeUserInformation(this.user()).then(function (updatedUser) {
            var us = ko.mapping.fromJS(updatedUser);
            self.user(us);
            self.status(AppStatus.Home);
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.logIn = function () {
        var self = this;
        ServiceMethods.logIn(this.user()).then(function (o) {
            self.user(ko.mapping.fromJS(o));
            self.wishUser(self.user());
            self.passwordConfirm("");
            $("#log-in").modal('hide');
            if (o.firstName.length && o.lastName.length) {
                self.status(AppStatus.Home);
            }
            else {
                self.status(AppStatus.Account);
            }
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.addFriend = function () {
        var self = this;
        ServiceMethods.addFriend(this.user(), this.friendEmailAddress()).then(function () {
            self.friendAddStatus(FriendAddStatus.Success);
        }).fail(function (request) {
            self.friendAddStatus(FriendAddStatus.Failure);
        });
    };
    App.prototype.acceptFriendRequest = function (newFriend) {
        this.respondToFriendRequest(true, newFriend);
    };
    App.prototype.rejectFriendRequest = function (snubbed) {
        this.respondToFriendRequest(false, snubbed);
    };
    App.prototype.respondToFriendRequest = function (acceptedRequest, requester) {
        var self = this;
        ServiceMethods.respondToFriendRequest(acceptedRequest, requester, this.user()).then(function () {
            var deletedIndex = self.user().requestsToReview.remove(requester);
            if (acceptedRequest) {
                self.user().friends.push(requester);
            }
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.addWishListItem = function () {
        this.editWishListItem().price(0);
        var self = this;
        ServiceMethods.addWishListItem(this.editWishListItem()).then(function (item) {
            self.user().wishList.push(ko.mapping.fromJS(item));
            self.editWishListItem(new WishListItemModel());
        }).fail(function (request) {
            self.editWishListItem(new WishListItemModel());
            alert(request);
        });
    };
    App.prototype.editItem = function (item) {
        this.editWishListItem(item);
        $("#edit-item").modal('show');
    };
    App.prototype.updateWishListItem = function () {
        if (!this.editWishListItem().wishListItemID()) {
            this.addWishListItem();
            return;
        }
        if (this.editWishListItem().price(0) == null)
            this.editWishListItem().price(0);
        var self = this;
        ServiceMethods.updateWishListItem(this.editWishListItem()).then(function (item) {
            if (!(self.editWishListItem().wishListItemID() > 0)) {
                self.user().wishList.push(ko.mapping.fromJS(item));
            }
            self.editWishListItem(new WishListItemModel());
        }).fail(function (request) {
            self.editWishListItem(new WishListItemModel());
            alert(request);
        });
    };
    App.prototype.markWishListItemAsBought = function (item) {
        item.isBought(true);
        item.purchaserUserID(this.user().userID());
        this.editWishListItem(item);
        this.updateWishListItem();
    };
    App.prototype.buyWishListItem = function (item) {
        this.markWishListItemAsBought(item);
        window.open(item.URL());
    };
    App.prototype.selectFriend = function (friend) {
        var self = this;
        ServiceMethods.getUserWishList(this.user(), friend).then(function (item) {
            if (!(self.editWishListItem().wishListItemID() > 0)) {
                self.wishUser().wishList(ko.mapping.fromJS(item)());
            }
            self.editWishListItem(new WishListItemModel());
        }).fail(function (request) {
            alert(request);
        });
        this.wishUser(friend);
        this.status(AppStatus.Home);
    };
    App.prototype.getTemplateHTML = function (page) {
        var opts = {
            url: location.origin + "/Views/" + page + ".htm",
            type: "GET",
            dataType: "html",
            contentType: "text/html"
        };
        var self = this;
        return jQuery.ajax(opts).done(function (html) {
            jQuery("#bindDIV").append(html);
            ko.applyBindings(self, jQuery("#" + jQuery(html)[0].id)[0]);
        });
    };
    return App;
})();
var AppStatus;
(function (AppStatus) {
    AppStatus[AppStatus["Home"] = 0] = "Home";
    AppStatus[AppStatus["Account"] = 1] = "Account";
    AppStatus[AppStatus["Landing"] = 2] = "Landing";
    AppStatus[AppStatus["Friends"] = 3] = "Friends";
    AppStatus[AppStatus["FriendRequests"] = 4] = "FriendRequests";
})(AppStatus || (AppStatus = {}));
var FriendAddStatus;
(function (FriendAddStatus) {
    FriendAddStatus[FriendAddStatus["Success"] = 0] = "Success";
    FriendAddStatus[FriendAddStatus["Failure"] = 1] = "Failure";
    FriendAddStatus[FriendAddStatus["Waiting"] = 2] = "Waiting";
})(FriendAddStatus || (FriendAddStatus = {}));
//# sourceMappingURL=app.js.map