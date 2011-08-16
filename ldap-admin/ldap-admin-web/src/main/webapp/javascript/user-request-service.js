/**
 * @constructor
 */
var UserRequestService = {}

/**#@+
 * @memberOf UserRequestService
 */

/**
 * @param user The user object to validate
 * @param onValid Callback handler that will be called if the object was valid
 * @param onInvalid Callback handler that will be called if the object was invalid
 */
UserRequestService.validate = function(user, onValid, onInvalid) {
    new Ajax.Request(createUrl("json/userRequestService/validate"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON(user),
        on200: function(transport) {
            onValid(transport.responseJSON.uid)
        },
        on400: function(transport) {
            onInvalid(transport.responseJSON)
        }
    });
}

UserRequestService.store = function(user, onOk, onFail) {
    new Ajax.Request(createUrl("json/userRequestService/store"), {
        method: 'post',
        contentType: 'application/json',
        postBody: Object.toJSON(user),
        on200: function(transport) {
            onOk(transport.responseJSON)
        },
        onFailure: function(transport) {
            onFail(transport.responseJSON)
        }
    });
}

UserRequestService.accept = function(requestId, user, onOk, onFail) {
    var data = Object.toJSON({
        requestId: requestId,
        uid: user.uid,
        firstName: user.firstName,
        lastName: user.lastName,
        mail: user.mail,
        mobilePhoneNumber: user.mobilePhoneNumber
    })
    new Ajax.Request(createUrl("json/userRequestService/accept"), {
        method: 'post',
        contentType: 'application/json',
        postBody: data,
        on200: function(transport) {
            onOk(transport.responseJSON)
        },
        onFailure: function(transport) {
            onFail(transport.responseJSON)
        }
    });
}

UserRequestService.reject = function(requestId, onOk, onFail) {
    var data = Object.toJSON({
        requestId: requestId
    })
    new Ajax.Request(createUrl("json/userRequestService/reject"), {
        method: 'post',
        contentType: 'application/json',
        postBody: data,
        on200: function(transport) {
            onOk(transport.responseJSON)
        },
        on400: function(transport) {
            onFail(transport.responseJSON)
        }
    });
}

/**#@-*/
