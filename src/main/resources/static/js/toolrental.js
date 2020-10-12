var TOOLDATA;
/*
 * on ready method to do initialization the tool data and selector
 */
$(document).ready(function() {
    tr_ajax("api/toollist", "GET", "", function(data) {
        //console.log("Data = " + JSON.stringify(data));
        //console.log("Initializing global and tool selection");
        tr_initializeToolDataAndSelector(data);
    });
});

/*
 * Initialization method for both the tool data global and the tool selector field. Both these actions
 * are performed here since the list needs to be traversed in both cases. Builds the drop down elements
 * for the selector and defines the associative array (object) of the data, for easy lookup/reference.
 * toollist - json data representing the list of tools retrieved from tool service
 */
function tr_initializeToolDataAndSelector(toollist) {
    var sel = $("#toolSelector");
    TOOLDATA = {}; // build associative array(object) for all the tool data. Tool code being the key

    for (var i=0; i<toollist.length; i++) {
        var toolCode = toollist[i].code;
        var toolType = toollist[i].type;
        var toolBrand = toollist[i].brand;
        sel.append(`<option value="${toolCode}">${toolType} (${toolBrand})</option>`);

        TOOLDATA[toolCode] = toollist[i];
    }
}

/*
 * Function called when the tool selector is updated (tool is selected/deselected).
 * When tool is picked, details from data set are pulled and displayed to the user.
 */
function tr_selectTool() {
    var sel = $("#toolSelector").val();

    if (sel === "") {
        // close tool detail - show welcome message
       tr_hideToolDetail();
    } else {
        // tool selected, show the tool details from global data
        var details = TOOLDATA[sel];
        tr_toggleToolDetail(details);
    }
}

/*
 * Function to build and toggle the display of tool details for the rental input form
 * details - json object of the tool details (from TOOLDATA)
 */
function tr_toggleToolDetail(details) {
    // build new tool detail div
    var elmStr = tr_buildToolDetails(details);

    // insert as first child to tool-wrapper and animate the display
    $(".tool-wrapper").prepend($(elmStr));
    var newdetail = $(".tool-detail-new");
    newdetail.animate({height: '100%'}, 200, function() {
        // after animation - get existing detail (if there is one and remove it)
        $(".tool-detail").remove();
        // switch tool-detail-new to just tool-detail
        newdetail.addClass("tool-detail").removeClass("tool-detail-new");
    });
}

/*
 * Simple function to hide the tool details if the tool selector is cleared out.
 */
function tr_hideToolDetail() {
    $(".tool-detail").animate({height: 0}, 200);
}

/*
 * Simple function to build the tool details div for a given tool. Returns string of the div html.
 * details - json object of the tool details (from TOOLDATA)
 */
function tr_buildToolDetails(details) {
    // build the rental terms freebie list (weekends/holidays free?)
    var freebies = "";
    if (!details.costs.weekday) freebies += "&bull; Free weekdays!<br/>";
    if (!details.costs.weekend) freebies += "&bull; Free weekends<br/>";
    if (!details.costs.holiday) freebies += "&bull; Free holidays<br/>";

    var detailElm = `<div class="tool-detail-new">
            <img src="image/tool/${details.code}.png" style="width: 100%; height: 160px;" />
            <div style="padding: 5px;">
                ${details.brand} ${details.tooltype}
                <br/>$${details.costs.charge} daily charge
                <p style="margin: 8px 0;">${freebies}</p>
                <span class="item-note">*Item condition may vary</span>
            </div>
        </div>`;

    return detailElm;
}

/*
 * Method to submit the rental input form / checkout. Process form data, validate
 * and marshall to json object for checkout service end point.
 */
function tr_submitCheckout() {
    if (!tr_validateCheckout()) return;

    var order = {};
    order.toolCode = $("#toolSelector").val();
    order.rentalDays = $("#rentalDays").val();
    order.discount = $("#discount").val();
    order.checkoutDate = $("#checkoutDate").val();
    //console.log("Order = " + JSON.stringify(order));

    tr_ajax("api/checkout", "POST", order, function(data) {
        //console.log("Data = " + JSON.stringify(data));

        // if validation errors - display
        if (data.status === 'OK') {
            // show the agreement screen / div
            showRentalAgreement(data.result);

        } else if (data.status === 'VALIDATION') {
            tr_displayFieldError(data.field, data.message);

        } else { // General error

        }
    });
}

/*
 * Method to display an error message for a given input (form) field
 * field - string of the field name to insert the message after
 * message - string message to be displayed as the error text
 * noarrow - optional boolean to suppress the arrow display
 */
function tr_displayFieldError(field, message, noarrow) {
    if (!noarrow) message = "&nearhk; " + message;
    //if (!noarrow) message = "&#11180; " + message;
    $("#" + field).after(`<span class="field-error">${message}</span>`);
    return false;
}

/*
 * Function to validate the order input form and display any issues from the front-end
 * perspective. Invalid entries will prompt error messages and keep order from being
 * submitted / processed.
 */
function tr_validateCheckout() {
    $(".field-error").remove();
    var valid = true;
    // tool selection - must exist
    if ($("#toolSelector").val() === "") {
        valid = tr_displayFieldError("toolSelector", "Please select a tool");
    }

    var startdate = $("#checkoutDate").val();
    // simple format check of start date field - let backend handle full date validation
    if (!startdate || !/^(0?[1-9]|1[0-2])\/(0?[1-9]|1\d|2\d|3[01])\/\d{2}$/.test(startdate)) {
        valid = tr_displayFieldError("checkoutDate", "Please enter a valid checkout date");
    }

    var days = $("#rentalDays").val();
    if (!days || !/^\d+$/.test(days) || parseInt(rentalDays) < 1) {
        valid = tr_displayFieldError("rentalDays", "Please enter a valid number of rental days");
    }

    var discount = $("#discount").val();
    if (!discount || !/^\d+$/.test(discount) || parseInt(discount) > 100) {
        valid = tr_displayFieldError("discount", "Please enter a valid discount percent");
    }

    return valid;
}

/*
 * Common method to populate and show the rental agreement screen
 * data - agreement data to be displayed to user
 */
function showRentalAgreement(data) {
    // hide rental form
    $("#rentalScreen").hide();

    // populate and show the agreement screen
    var toolCode = data.toolCode;
    $("#agreementImage").prop("src", "image/tool/" + toolCode + ".png");

    $("#agrToolCode").html(toolCode);
    $("#agrToolType").html(data.toolType);
    $("#agrToolBrand").html(data.toolBrand);

    $("#agrRentalDays").html(data.rentalDays);
    $("#agrCheckoutDate").html(displayPaddedDate(data.checkoutDate));
    $("#agrDueDate").html(displayPaddedDate(data.returnDate));

    $("#agrDailyCharge").html(displayCurrencyAmount(data.rentalCharge));
    $("#agrChargeDays").html(data.chargeDays);
    $("#agrSubTotal").html(displayCurrencyAmount(data.preDiscountAmount));
    $("#agrDiscountPercent").html(displayPercentAmount(data.discount));
    $("#agrDiscountAmount").html("-" + displayCurrencyAmount(data.discountAmount));
    $("#agrFinalCharge").html(displayCurrencyAmount(data.finalAmount));

    $("#agreementScreen").show();
}

/*
 * Simple method to reset inputs and switch the screens to display the rental form
 */
function resetRentalForm() {
    // hide agreement show rental form
    $("#agreementScreen").hide();
    $("#rentalScreen").show();
    tr_hideToolDetail();

    // reset fields
    $("#toolSelector").val("");
    $("#checkoutDate").val("");
    $("#rentalDays").val("");
    $("#discount").val("");
}


/*
 * Common method to call service end points of the tool rental application
 * url - the service endpoint url
 * method - GET, POST, PUT, DELETE service method
 * fDone - function representing the callback of a successful call
 * fError - function representing the callback of an erroneous call // fError(jqXHR, status, errorThrown)
 */
function tr_ajax(url, method, content, fDone, fError) {
    // setup ajax object to pass to jQuery ajax method
    var ajaxObj = {
        url : url,
        type : method,
        dataType : "json",
        contentType : "application/json;charset=UTF-8",
        data : JSON.stringify(content)
    };

  	// make service call
  	jQuery.ajax(ajaxObj).done(fDone).fail(function(jqXHR, status, errorThrown) {
        if (typeof fError === "function") { // make sure exists and is a function
            fError(jqXHR, status, errorThrown);
        } else {
            // TODO: need to improve error handling and messaging - overlay, more robust messaging
            //defaultError();
            console.log("status=" + status + ", " + errorThrown);
            console.log("jqXHR=" + JSON.stringify(jqXHR));
            alert("An unexpected error occurred. Please try again.");
        }
  	});
}

/*
 * Additional for fun, show user profile overlay
 */
function viewProfileOverlay() {
    $("#overlaycontainer").show();
    $("#overlay-profile").show();
}

function closeOverlay() {
    $("#overlaycontainer").hide();
    $(".overlay").hide();
}


/*
 * Simple format / display methods - can move to common js file
 */
function displayCurrencyAmount(value) {
    // TODO: add delimiter for large numbers
    return "$" + value.toFixed(2);
}
function displayPercentAmount(value) {
    return value + "%";
}
function displayPaddedDate(value) {
    var parts = value.split("/");
    return ('0' + parts[0]).slice(-2) + '/'
         + ('0' + parts[1]).slice(-2) + '/'
         + parts[2];
}