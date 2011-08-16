/**
 * @constructor
 */
var UserService = {}

/**#@+
 * @memberOf UserService
 */

UserService.store = function(user, onOk, onFailure) {
    new Ajax.Request(createUrl("json/userService/store"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON({
            uid: user.uid,
            user: user
        }),
        on200: function(transport) {
            onOk(transport.responseJSON.uid)
        },
        on400: function(transport) {
            onFailure(transport.responseJSON)
        }
    });
}

UserService.deleteUser = function(uid, onOk, onFailure) {
    new Ajax.Request(createUrl("json/userService/delete"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON({
            uid: uid
        }),
        on200: function() {
            onOk()
        },
        on400: function(transport) {
            onFailure(transport.responseJSON)
        }
    });
}

UserService.addGroup = function(uid, group, onOk, onFailure) {
    new Ajax.Request(createUrl("json/userService/addGroup"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON({
            uid: uid,
            group: group
        }),
        on200: function() {
            onOk()
        },
        on400: function(transport) {
            onFailure(transport.responseJSON)
        }
    });
}

UserService.removeGroup = function(uid, group, onOk, onFailure) {
    new Ajax.Request(createUrl("json/userService/removeGroup"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON({
            uid: uid,
            group: group
        }),
        on200: function() {
            onOk()
        },
        on400: function(transport) {
            onFailure(transport.responseJSON)
        }
    });
}

UserService.setPassword = function(uid, password, onOk, onFailure) {
    new Ajax.Request(createUrl("json/userService/setPassword"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON({
            uid: uid,
            password: password
        }),
        on200: function() {
            onOk()
        },
        on400: function(transport) {
            onFailure(transport.responseJSON)
        }
    });
}

UserService.generatePassword = function(uid, onOk, onFailure) {
    new Ajax.Request(createUrl("json/userService/generatePassword"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON({
            uid: uid
        }),
        on200: function(transport) {
            onOk(transport.responseJSON.password)
        },
        on400: function(transport) {
            onFailure(transport.responseJSON)
        }
    });
}

/**#@-*/
