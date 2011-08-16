function log(text) {
    ((window.console && console.log) ||
     (window.opera && opera.postError) ||
     window.alert).call(this, text);
}

function createUrl(url) {
    return baseurl + url
}

function clearFieldErrors(fieldErrorsDiv) {
    fieldErrorsDiv.hide()
}

function createDefaultFailureHandler(messageBoxId) {
    return function() {
        MessageBox.showError(messageBoxId, "Error performing request. Please reload the page and try again.")
    }
}

function updateFieldErrors(response, fieldErrorsDiv) {
    //    console.log("response: " + Object.inspect($H(response.fieldErrors)));
    var fieldErrors = $H(response.fieldErrors)
//    console.log("fieldErrors: " + Object.inspect(fieldErrors));
    if (fieldErrors.size() == 0) {
        return
    }
    var ulId = fieldErrorsDiv.id + "_ul"
    var ul = new Element("ul", { 'id': ulId })

    fieldErrors.each(function(field) {
        //console.log("field: " + field.key)
        field.value.each(function(message) {
            //console.log("message: " + message)
            var span = new Element("span", { 'class': 'errorMessage' }).update(message);
            ul.appendChild(span.wrap("li"))
        })
    })

    var oldUl = $(ulId)

    if (oldUl) {
        oldUl.remove()
    }
    fieldErrorsDiv.appendChild(ul)
    fieldErrorsDiv.show()
}
