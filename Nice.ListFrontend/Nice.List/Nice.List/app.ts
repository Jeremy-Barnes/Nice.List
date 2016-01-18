/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    var page = new App();
    ko.applyBindings(page);
});

class App {
    public user: KnockoutObservable<UserModel>;
    public status: KnockoutObservable<AppStatus> = ko.observable(AppStatus.Home);
    public passwordConfirm: KnockoutObservable<string> = ko.observable("");
    public passwordsMatch: KnockoutComputed<boolean>;


    constructor() {
        this.user = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(() => { return this.user().password() == this.passwordConfirm(); }, this);
        this.initUser();
    }//ctor

    private initUser(): void {
        let siteCookie: string = this.findCookie();
        if (siteCookie == null) { //no user or too many users
            //TODO kill these cookies
            this.status(AppStatus.Landing);
        } else {
            let selectorValidator = siteCookie.split(":");
            this.getUser(selectorValidator[0], selectorValidator[1]);
        }
    }

    private findCookie(): string {
        let usefulCookies = ("; " + document.cookie).split("; nicelist="); //everyone else's garbage ; mine
        if (usefulCookies.length == 2) {
            return usefulCookies[1];
        } else {
            return null;
        }
    }

    public getUser(selector: string, validator: string) {
        var valid: Token = { selector: selector, validator: validator}
        var parameters = JSON.stringify(valid);
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "getUserFromToken",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (o: User) {
            self.user(ko.mapping.fromJS(o));
            self.status(AppStatus.Home);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public submitAccountChanges() {
        var parameters = JSON.stringify(ko.toJS(this.user()));

        var methodName = "";
        if (this.status() == AppStatus.Landing) {
            methodName = "createUser";
        } else {
            methodName = "changeUserInformation";
        }

        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + methodName,
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (updatedUser: User) {
            var us = ko.mapping.fromJS(updatedUser); 
            self.user(us);
            self.status(AppStatus.Account);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public logIn() {
        var x = this.status;
        var parameters = JSON.stringify(ko.mapping.toJS(this.user));
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "getUserFromLogin",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (o: User) {
            self.user(ko.mapping.fromJS(o));
            self.status(AppStatus.Home);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }
    
    public signUp() {
        var parameters = JSON.stringify(ko.mapping.toJS(this.user));
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "createUser",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: parameters,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (o: User) {
            self.user(ko.mapping.fromJS(o));
            self.status(AppStatus.Home);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public switchState() {
        if (this.status() == AppStatus.Home) {
            this.status(AppStatus.Landing);
        } else {
            this.status(AppStatus.Account);
        }
    }

}

enum AppStatus {
    Home, Account, ViewUser, Landing
}