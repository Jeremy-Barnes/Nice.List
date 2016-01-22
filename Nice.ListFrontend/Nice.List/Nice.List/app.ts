﻿/// <reference path="Scripts/typings/jquery/jquery.d.ts" />
/// <reference path="Scripts/typings/jqueryui/jqueryui.d.ts" />
/// <reference path="Scripts/typings/knockout/knockout.d.ts" />
/// <reference path="Scripts/typings/knockout.mapping/knockout.mapping.d.ts" />


var page: App;

$(document).ready(function () {
    $.ajaxSetup({ cache: false });
    page = new App();
    ko.applyBindings(page);
});


class App {

    public user: KnockoutObservable<UserModel>;
    public status: KnockoutObservable<AppStatus> = ko.observable(AppStatus.Home);
    public passwordConfirm: KnockoutObservable<string> = ko.observable("");
    public passwordsMatch: KnockoutComputed<boolean>;

    public friendEmailAddress: KnockoutObservable<string> = ko.observable("");
    public friendAddStatus : KnockoutObservable<FriendAddStatus> = ko.observable(FriendAddStatus.Waiting);

    constructor() {

        this.user = ko.observable(new UserModel());
        this.passwordsMatch = ko.pureComputed(() => {
            var passwordCo = this.passwordConfirm();
            return this.user().password() == this.passwordConfirm();
        }, this);
        this.initUser();

        jQuery('#add-friend').on('hidden.bs.modal', (e) => { this.friendAddStatus(FriendAddStatus.Waiting); this.friendEmailAddress(""); }); //I'm not happy about this, either

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

        var valid: Token = { selector: selector, validator: validator }
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

    public createUser() {
        var methodName = "";
        var param = JSON.stringify(ko.toJS(this.user()));
 
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "createUser",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: param,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (updatedUser: User) {
            var us = ko.mapping.fromJS(updatedUser);
            self.user(us);
            self.passwordConfirm("");
            (<any>$("#sign-up")).modal('hide'); 
            self.status(AppStatus.Account);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }


    public submitAccountChanges() {
        var methodName = "";
        var dat = new FormData();
            dat.append("user", JSON.stringify(ko.toJS(this.user())));
            dat.append("file", (<any>jQuery("#file")[0]).files[0]);
        
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/users/" + "changeUserInformation",
            type: "POST",
            contentType: false,
            processData: false,
           dataType: "json", 
            data: dat,
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function (updatedUser: User) {
            var us = ko.mapping.fromJS(updatedUser); 
            self.user(us);
            self.status(AppStatus.Home);
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public logIn() {

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
            (<any>$("#log-in")).modal('hide'); 
            if (o.firstName.length && o.lastName.length) {
                self.status(AppStatus.Home);
            } else {
                self.status(AppStatus.Account);
            }
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    public addFriend() {

        var parameters = {};
        parameters["user"] = ko.mapping.toJS(this.user);
        parameters["requestedEmailAddress"] = this.friendEmailAddress();
        var settings: JQueryAjaxSettings = {
            url: "http://localhost:8080/api/nice/friends/" + "createFriendship",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(parameters),
            crossDomain: true
        };
        var self = this;
        jQuery.ajax(settings).then(function () {
            self.friendAddStatus(FriendAddStatus.Success);
        }).fail(function (request: JQueryXHR) {
            self.friendAddStatus(FriendAddStatus.Failure);
        });
    }

    public switchState() {
        if (this.status() != AppStatus.Landing) {
            this.status(AppStatus.Landing);
        } else {
            this.status(AppStatus.Friends);
        }
    }
}

enum AppStatus {
    Home, Account, ViewUser, Landing, Friends
}

enum FriendAddStatus {
    Success, Failure, Waiting
}