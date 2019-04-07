
function getPatientTableBody(patients){
    tb = ""
    for(let i = 0; i< patients.length; i++){
        let pat = patients[i]
        tb += '<tr>'
        tb += '<td>' + pat.idPaciente + '</td>'
        tb += '<td>' + pat.score + '</td>'
        tb += '<td class="align-middle" style="text-align:center">'
        tb += '<button type="button" class="btn-sm btn btn-outline-light mybtn" add_info_row="add_info_row_'+pat.idPaciente+'" patient-id="'+pat.idPaciente+'"'
        tb += 'onclick="showAddInfoRow(this)"'
        tb += '>'
        tb += '<i class="far fa-plus-square " style="color: green"></i>'
        tb += '</button>'
        tb += '</td>'
        tb += '<td>'
        tb += '<div class="alert alert-primary" style="display: none" id="status_msg'+pat.idPaciente+'" role="alert"></div>'
        tb += '</td>'
        tb += '</tr>'
        tb += '<tr id="add_info_row_'+pat.idPaciente+'" style="display:none">'
        tb += '<td colspan="4"></td>'
        tb += '</tr>'
    }
    return tb
}

function getZombiePatients(){
    jQuery.ajax({
        type: "GET",
        dataType: "json",
        url: "/api/zombiepatients",
        success: function(data){
            //console.log(data)
            $("#tbody_zombiepatients").html(getPatientTableBody(data))
        }
      });
}


function getPatientPruebasTable(pruebas){
    tb = '<td colspan="4">'
    tb += '<table class="table">'
    tb += '<thead><tr>'
    tb += '<th scope="col" style="width: 45%">Test Name</th>'
    tb += '<th scope="col" style="width: 45%">Date</th>'
    tb += '<th scope="col" style="width: 5%">Value</th>'
    tb += '<th scope="col" style="width: 5%">Unit</th>'
    tb += '</tr></thead>'
    tb += '<tbody>'
    for(let i = 0;i<pruebas.length;i++){
        tb += '<tr>'
        tb += '<td>'+pruebas[i].name+'</td>'
        tb += '<td>'+pruebas[i].dateTime+'</td>'
        tb += '<td>'+pruebas[i].value+'</td>'
        tb += '<td>'+pruebas[i].unit+'</td>'
        tb += '</tr>'
    }
    tb += '</tbody>'
    tb += '</table>'
    tb += '</td>'
    return tb
}

function getPatientPruebas(pat_id,row_id){
    jQuery.ajax({
        type: "GET",
        dataType: "json",
        url: "/api/patientpruebas/"+pat_id,
        success: function(data){
            //console.log(data)
            $("#"+row_id)[0].innerHTML = getPatientPruebasTable(data)
        }
      });
}

function showAddInfoRow(btn){
        //console.log(btn)
        row_info = btn.getAttribute("add_info_row")
        pat_id = btn.getAttribute("patient-id")
        //console.log(row_info)
        if(btn.getAttribute("is-open") == "true"){
            btn.setAttribute("is-open","false")
            $("#"+row_info).removeClass("table-active").hide()
            btn.innerHTML = '<i class="far fa-plus-square" style="color: green"></i>'
        }
        else{
            $("#"+row_info).addClass("table-active").show()
            btn.setAttribute("is-open","true")
            btn.innerHTML = '<i class="far fa-minus-square" style="color: red"></i>'
            getPatientPruebas(pat_id,row_info)
        }
}


getZombiePatients()