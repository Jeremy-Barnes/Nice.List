/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />
$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    var page = new App();
    ko.applyBindings(page);
});
var App = (function () {
    function App() {
        var _this = this;
        this.status = ko.observable(AppStatus.Home);
        this.passwordConfirm = ko.observable("");
        this.user = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(function () { return _this.user().password() == _this.passwordConfirm(); }, this);
        this.initUser();
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
    App.prototype.doThing = function () {
        var settings = {
            url: "http://localhost:8080/api/nice/users/getUser",
            type: "POST",
            dataType: "json",
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings);
    };
    App.prototype.submitAccountChanges = function () {
        var parameters = JSON.stringify(ko.toJS(this.user()));
        var methodName = "";
        if (this.status() == AppStatus.Landing) {
            methodName = "createUser";
        }
        else {
            methodName = "changeUserInformation";
        }
        var settings = {
            url: "http://localhost:8080/api/nice/users/" + methodName,
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (updatedUser) {
            var us = ko.mapping.fromJS(updatedUser);
            self.user(us);
            self.status(AppStatus.Account);
        }).fail(function (request) {
            alert(request);
        });
    };
    App.prototype.logIn = function () {
        var parameters = JSON.stringify({ emailAddress: this.user().emailAddress(), password: this.user().password() });
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
            self.status(AppStatus.Home);
        }).fail(function (request) {
            alert(request);
        });
    };
    return App;
})();
var AppStatus;
(function (AppStatus) {
    AppStatus[AppStatus["Home"] = 0] = "Home";
    AppStatus[AppStatus["Account"] = 1] = "Account";
    AppStatus[AppStatus["ViewUser"] = 2] = "ViewUser";
    AppStatus[AppStatus["Landing"] = 3] = "Landing";
})(AppStatus || (AppStatus = {}));
//# sourceMappingURL=app.js.map