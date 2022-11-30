var tableObject;
var searchResultsData = [];
var timeout;
var searchFromSystem = false;
var highlightedKeyboardRowIndex, dTable;

(function(mchClinicConstants){
    mchClinicConstants["MCH Clinic user for ANC"] = "ANC";
    mchClinicConstants["a46c6cd7-9ce3-4ade-99c8-0e395cab6c57"] = "ANC";
	
    mchClinicConstants["MCH Clinic user for PNC"] = "PNC";
    mchClinicConstants["20e3222e-9a05-44bc-a95d-7333d85ade82"] = "PNC";
	
    mchClinicConstants["MCH Clinic user for CWC"] = "CWC";
    mchClinicConstants["1c54b4e5-ac8a-4ac3-bff2-a87192c9557f"] = "CWC";
	
    mchClinicConstants["MCH Clinic user for FP"] = "FP";
    mchClinicConstants["8fa718ef-550a-4d71-8ff9-a61174e9e65f"] = "FP";
	
    mchClinicConstants["CWC Immunization Queue"] = "Immunization";
})(window.mchClinicConstants = window.mchClinicConstants || {})

function startRefresh(){
	getPatientsFromQueue();
}

var toggleQueueSystemTables = function () {
	if (jq('#search-in-db').is(':checked')) {
		jq('.page-label').html(patientInSystemLabel);
		jq('.in-system').show();
		jq('.queue').hide();
		jq('#queueRoles').hide(100);
		
		searchFromSystem = true;
	} else {
		jq('.page-label').html(opdQueueLabel);
		jq('.in-system').hide();
		jq('.queue').show();
		jq('#queueRoles').show(100);
		
		searchFromSystem = false;
	}
}

var updateSearchResults = function(results){
	searchResultsData = results || [];
	var dataRows = [];
	_.each(searchResultsData, function(result){
		var patient_name = result.patientName.replace("null","");
		console.log("The results 1 are >>"+result);
		if (result.referralConcept && result.referralConcept.conceptId === 1000104){
			patient_name += " <span class='recent-lozenge'>From Lab</span>";
		}
		
		dataRows.push([result.patientIdentifier, patient_name, result.age,  result.sex, result.visitStatus, result.status]);
	});
	console.log("tttt", dataRows);

	dTable.api().clear();
	
	if(dataRows.length > 0) {
		dTable.fnAddData(dataRows);
	}

	refreshInQueueTable();
}
var updateMCHSearchResults = function(results){
	searchResultsData = results || [];
	var dataRows = [];
	_.each(searchResultsData, function(result){
		var patient_name = result.patientName.replace("null","");
		if (result.referralConcept && result.referralConcept.conceptId === 1000104){
			patient_name += " <span class='recent-lozenge'>From Lab</span>";
		}

		dataRows.push([result.patientIdentifier, patient_name, result.age, result.clinic, result.sex, result.visitStatus, result.status]);
	});

	dTable.api().clear();

	if(dataRows.length > 0) {
		dTable.fnAddData(dataRows);
	}

	refreshInQueueTable();
}

var selectRow = function(selectedRowIndex) {
	handlePatientRowSelection.handle(searchResultsData[selectedRowIndex]);
}

var refreshInQueueTable = function(){
	var rowCount = searchResultsData.length;
	if(rowCount === 0){
		tableObject.find('td.dataTables_empty').html("No patient in queue");
	}
	dTable.fnPageChange(0);
}

var getPatientsFromQueue = function(){
	tableObject.find('td.dataTables_empty').html('<span><img class="search-spinner" src="'+ui.resourceLink('ehrconfigs', 'images/spinner.gif')+'" /></span>');
	jq.getJSON(ui.fragmentActionLink("patientqueueapp", "patientQueue", "getPatientsInQueue"),
		{
		  'opdId': jq('#queue-choice').val()
		})
	.success(function(results) {
		updateSearchResults(results.data);
		if(results.user==="triageUser"){
			//jq(".user-processing").text("Nurse Processing");
		}else{
			//jq(".user-processing").text("Doctor Processing");
		}

	})
	.fail(function(xhr, status, err) {
		updateSearchResults([]);
	});
};

var startTimer = function () {
	if (jq("#queue-choice").val() !== 0 && !searchFromSystem){
		startRefresh();
		if (timeout) {
			clearTimeout(timeout);    		
		}
		timeout = setInterval(startRefresh, 30000);
	}
}

var bindPatientQueueSearchEvent = function () {
    jq("#patient-search").on("keyup", function(){
        if (!searchFromSystem) {
            var searchPhrase = jq(this).val();
            dTable.api().search(searchPhrase).draw();
        }
    });

    jq("#patient-search-clear-button").on("click", function(){
        jq("#patient-search").val('');
        if (!searchFromSystem) {
            dTable.api().search('').draw();
        }
    });
}

var isTableEmpty = function(){
	if(searchResultsData.length > 0){
		return false
	}
	return !dTable || dTable.fnGetNodes().length === 0;
};

jq.fn.dataTable.ext.search.push(
    function( settings, data, dataIndex ) {
		var inputFields = jq('#queueRoles input:checked');
        var clinicType = data[3]; // use data for the age column
		
		if (jq('#search-in-db:checked').length > 0){
			return true;
		}		
		else if (inputFields.length > 0){
			var acceptedClinicTypes = ['N/A'];
			
			
			jq.each(inputFields, function(index, field){
				acceptedClinicTypes.push(mchClinicConstants[jq(field).val()]);				
			});
			
			if (acceptedClinicTypes.indexOf(clinicType) !== -1){
				return true;
			}
			else{
				return false;
			}
		}
		else if (jq('#queueRoles').length > 0){
			if (clinicType === 'N/A'){
				return true;				
			}
			else{
				return false;
			}
		}
		else{			
			return true;
		} 
    }
);
	
jq(function(){
    tableObject = jq("#patient-queue");

    startTimer();

    jq('#queue-choice').change(function() {
    	if (!searchFromSystem) {
    		getPatientsFromQueue();
    		if (timeout) {
    			clearTimeout(timeout);    		
    		}
    		timeout = setInterval(startRefresh, 30000);
    	}
    });
	
    dTable = tableObject.dataTable({
        bFilter: true,
        bJQueryUI: true,
        bLengthChange: false,
        iDisplayLength: 15,
        sPaginationType: "full_numbers",
        bSort: false,
        sDom: 't<"fg-toolbar ui-toolbar ui-corner-bl ui-corner-br ui-helper-clearfix datatables-info-and-pg"ip>',
        oLanguage: {
            "sInfo": "Patients in queue",
            "sInfoEmpty": " ",
            "sZeroRecords": "No patients in queue",
            "oPaginate": {
                "sFirst": "First",
                "sPrevious": "Previous",
                "sNext": "Next",
                "sLast": "Last"
            }
        },

        fnDrawCallback : function(oSettings){
            if(isTableEmpty()){
                //this should ensure that nothing happens when the use clicks the
                //row that contain the text that says 'No data available in table'
                return;
            }

            if(highlightedKeyboardRowIndex !== undefined && !isHighlightedRowOnVisiblePage()){
                unHighlightRow(dTable.fnGetNodes(highlightedKeyboardRowIndex));
            }

            //fnDrawCallback is called on each page redraw so we need to remove any previous handlers
            //otherwise there will multiple hence the logic getting executed multiples times as the
            //user the goes back and forth between pages
            tableObject.find('tbody tr').unbind('click');
            tableObject.find('tbody tr').unbind('hover');

            tableObject.find('tbody tr').click(
                function(){
                    highlightedMouseRowIndex = dTable.fnGetPosition(this);
                    selectRow(highlightedMouseRowIndex);
                }
            );
        },
		
		fnRowCallback : function (nRow, aData, index){
			if (searchResultsData[index].referralConcept && searchResultsData[index].referralConcept.conceptId === 1283){
				nRow.className += " from-lab";
				return nRow;
			}
		}
    });

    bindPatientQueueSearchEvent();
});