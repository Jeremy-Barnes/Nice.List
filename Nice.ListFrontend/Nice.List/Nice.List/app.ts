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
    public status: KnockoutObservable<AppStatus> = ko.observable(AppStatus.Landing);
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
            this.status(AppStatus.SignUp);
        } else {
            let selectorValidator = siteCookie.split(":");
            this.getUser(selectorValidator[0], selectorValidator[1]);
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
            self.user(ko.toJS(o));
        }).fail(function (request: JQueryXHR) {
            alert(request);
        });
    }

    private findCookie(): string {
        let usefulCookies = ("; " + document.cookie).split("; nicelist="); //everyone else's garbage ; mine
        if (usefulCookies.length == 2) {
            return usefulCookies[1];
        } else {
            return null;
        }
    }
    
}

enum AppStatus {
    SignUp, Landing, Account, ViewUser
}