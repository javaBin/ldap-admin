/**
 * @constructor
 */
var MessageBox = {}

/**#@+
 * @memberOf MessageBox
 */

MessageBox.showForbidden = function(boxId) {
    MessageBox.showError(boxId, "Permission denied")
}

MessageBox.showError = function(boxId, message) {
    MessageBox.updateBox(boxId, message, "dialog-error", "Error");
}

MessageBox.showInformation = function(boxId, message) {
    MessageBox.updateBox(boxId, message, "dialog-information", "Information");
}

MessageBox.showWarning = function(boxId, message) {
    MessageBox.updateBox(boxId, message, "dialog-warning", "Warning");
}

/**#@+
 * @private
 */

MessageBox.updateBox = function(boxId, message, image, alt) {
    $("message-box-" + boxId).show()
    $("message-box-text-" + boxId).update(message)
    $("message-box-icon-" + boxId).setAttribute("src", createUrl("tango/32x32/status/" + image + ".png"))
    $("message-box-icon-" + boxId).setAttribute("alt", alt)

    var container = $("message-box-container-" + boxId)

    if (container) {
        container.show()
    }
}

/**#@-*/

/**#@-*/
