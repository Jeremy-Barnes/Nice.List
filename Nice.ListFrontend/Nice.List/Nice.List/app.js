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
        this.status = ko.observable(AppStatus.Home);
        this.passwordConfirm = ko.observable("");
        this.friendEmailAddress = ko.observable("");
        this.friendAddStatus = ko.observable(FriendAddStatus.Waiting);
        this.user = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(function () {
            var passwordCo = _this.passwordConfirm();
            return _this.user().password() == _this.passwordConfirm();
        }, this);
        this.initUser();
        jQuery('#add-friend').on('hidden.bs.modal', function (e) { _this.friendAddStatus(FriendAddStatus.Waiting); _this.friendEmailAddress(""); }); //I'm not happy about this, either
    } //ctor
    App.prototype.initUser = function () {
        var siteCookie = this.findCookie();
        if (siteCookie == null) {
            //TODO kill these cookies
            this.status(AppStatus.Landing);
        }
        else {
            var selectorValidator = siteCookie.split(":");
            this.getUser(selectorValidator[0], selectorValidator[1]);
        }
    };
    App.prototype.findCookie = function () {
        var usefulCookies = ("; " + document.cookie).split("; nicelist="); //everyone else's garbage ; mine
        if (usefulCookies.length == 2) {
            return usefulCookies[1];
        }
        else {
            return null;
        }
    };
    App.prototype.getUser = function (selector, validator) {
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
        jQuery.ajax(settings).then(function (o) {
            self.user(ko.mapping.fromJS(o));
            self.status(AppStatus.Home);
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.createUser = function () {
        var methodName = "";
        var param = JSON.stringify(ko.toJS(this.user()));
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "createUser",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: param,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (updatedUser) {
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
        var methodName = "";
        var dat = new FormData();
        dat.append("user", JSON.stringify(ko.toJS(this.user())));
        dat.append("file", jQuery("#file")[0].files[0]);
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "changeUserInformation",
            type: "POST",
            contentType: false,
            processData: false,
            dataType: "json",
            data: dat,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (updatedUser) {
            var us = ko.mapping.fromJS(updatedUser);
            self.user(us);
            self.status(AppStatus.Home);
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.logIn = function () {
        var parameters = JSON.stringify(ko.mapping.toJS(this.user));
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + "getUserFromLogin",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (o) {
            self.user(ko.mapping.fromJS(o));
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
        var parameters = {};
        parameters["user"] = ko.mapping.toJS(this.user);
        parameters["requestedEmailAddress"] = this.friendEmailAddress();
        var settings = {
            url: "http://localhost:8080/api/nice/friends/" + "createFriendship",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(parameters),
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function () {
            self.friendAddStatus(FriendAddStatus.Success);
        }).fail(function (request) {
            self.friendAddStatus(FriendAddStatus.Failure);
        });
    };
    App.prototype.switchState = function () {
        if (this.status() != AppStatus.Landing) {
            this.status(AppStatus.Landing);
        }
        else {
            this.status(AppStatus.Home);
        }
    };
    return App;
})();
var AppStatus;
(function (AppStatus) {
    AppStatus[AppStatus["Home"] = 0] = "Home";
    AppStatus[AppStatus["Account"] = 1] = "Account";
    AppStatus[AppStatus["ViewUser"] = 2] = "ViewUser";
    AppStatus[AppStatus["Landing"] = 3] = "Landing";
    AppStatus[AppStatus["Friends"] = 4] = "Friends";
    AppStatus[AppStatus["FriendRequests"] = 5] = "FriendRequests";
})(AppStatus || (AppStatus = {}));
var FriendAddStatus;
(function (FriendAddStatus) {
    FriendAddStatus[FriendAddStatus["Success"] = 0] = "Success";
    FriendAddStatus[FriendAddStatus["Failure"] = 1] = "Failure";
    FriendAddStatus[FriendAddStatus["Waiting"] = 2] = "Waiting";
})(FriendAddStatus || (FriendAddStatus = {}));
//# sourceMappingURL=app.js.map