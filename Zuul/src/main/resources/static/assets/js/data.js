var files = new Object();
var c = 0;
//var field_data = [];
type = ['', 'info', 'success', 'warning', 'danger'];
$(document).ready(function () {

});


data = {
    //选择文件
    pickF: function (path) {
        console.log(files);
        if (files[path] != 1)
            $("#picked_files").append("<tr><td>" + (c++) + "</td><td>" + path + "</td><td> <i class='ti-na icon-danger' onclick=data.removeF('" + path + "',this)></i></td></tr>");
        files[path] = 1;
    },


    //从table中删除文件
    removeF: function (path, _this) {
        files[path] = 0;
        console.log(files);
        $(_this).parent().parent().remove();
    },

    //提交任务
    process: function () {

        var field_ids = "";
        var files = "";
        var task_name = $("#task_name").val();
        var task_desc = $("#task_desc").val();

        //$("#files_select").css("display", "none");
        $("#field_table tr").each(function (i) {
            $(this).children('td').each(function (j) {  // 遍历 tr 的各个 td
                //alert("第"+(i+1)+"行，第"+(j+1)+"个td的值："+$(this).text()+"。");
                if (j == 0) field_ids += $(this).text() + ",";
            });

        });

        $("#file_table tr").each(function (i) {
            $(this).children('td').each(function (j) {  // 遍历 tr 的各个 td
                //alert("第"+(i+1)+"行，第"+(j+1)+"个td的值："+$(this).text()+"。");
                if (j == 1) {
                    if ($(this).text().indexOf(",") != -1) {
                        alert("文件名不能包含','");
                        return;
                    }
                    files += $(this).text() + ",";
                }
            });

        });
        if (field_ids == "") {
            alert("选择的属性不能为空");
            return;
        }
        if (files == "") {
            alert("选择的文件不能为空");
            return;
        }

        $.post("/fileservice/task/submit", {
            "field_ids": field_ids,
            "files": files,
            "task_name": task_name,
            "task_desc": task_desc
        }, function (result) {
            if (result.success == true) {
                window.location.href="/";
            }
        });
/*
        alert(field_ids);
        alert(files);
        alert(task_name);
        alert(task_desc);*/

    }
};


/*
和属性相关的方法
*/
field = {
    update: function () {

        var param = {
            "fid": $("#fid_modal").val(),
            "fieldName": $("#fieldName_modal").val(),
            "regex": $("#regex_modal").val(),
            "description": $("#desc_modal").val(),
            "useCount": $("#count_modal").val(),
        };
        $.ajax({
            url: "/fieldservice/save",
            type: "POST",
            data: JSON.stringify(param),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (result) {
                if (result.success == true) {
                    common.showNotification('top', 'center', null, "修改成功");
                    setTimeout(function () {
                        location.reload();
                    }, 700)

                } else {
                    common.showNotification('top', 'center', 4, "修改失败");
                }
            },
            error: function (result) {
                common.showNotification('top', 'center', 4, "修改失败,服务器错误");
            }
        })


        $("#myModal").modal('toggle');

    },

    removeFromTable: function (_this) {

        $(_this).parent().parent().remove();


    },
    addToTable: function () {
        var id = $("#field_select_fid").val();
        var name = $("#field_select_name").find("option:selected").text();
        var desc = $("#field_select_desc").val();
        var regex = $("#field_select_regex").val();
        var count = $("#field_select_count").val();
        var str = "<tr><td>" + id + "</td><td>" + name + "</td><td>" + regex + "</td><td>" + desc + "</td><td>" + count + "</td>" +
            "<td><!--<input type=\"button\" class=\"btn btn-primary\" data-toggle=\"modal\" data-target=\"#myModal\" value=\"编辑\"/>--><input type=\"button\" class=\"btn btn-danger\"\n" +
            " value=\"移除\"\n" +
            " onclick=\"field.removeFieldFromTable(this)\"/></td>\n" +
            " </tr>";

        $("#field_tbody").append(str);

    },
    select_onchange: function (fid) {

        $.post("fieldservice/find/" + fid, function (result) {
            if (result.success == true) {
                $("#field_select_regex").val(result.data.regex);
                $("#field_select_desc").val(result.data.description);
                $("#field_select_fid").val(result.data.fid);
                $("#field_select_count").val(result.data.useCount);
                //$("#field_select_name").val(result.data.fieldName);
            }
        });

    },
    removeFieldFromTable: function (_this) {
        $(_this).parent().parent().remove();

    },
    delete: function (id, _this) {
        $.post("/fieldservice/delete/" + id, function (result) {

            if (result.success == true) {
                common.showNotification('top', 'center', null, "删除成功");
                $(_this).parent().parent().remove();
            } else {
                common.showNotification('top', 'center', 4, "删除失败")
            }
        });
    },
    updateModal: function (fid, fieldName, regex, description, count) {
        $("#fid_modal").val(fid);
        $("#fieldName_modal").val(fieldName);
        $("#regex_modal").val(regex);
        $("#desc_modal").val(description);
        $("#count_modal").val(count);
        $("#myModal").modal('toggle');


    },
    /*显示添加属性的modal*/
    showAddModal: function () {
        $("#fid_modal").val("");
        $("#fieldName_modal").val("");
        $("#regex_modal").val("");
        $("#desc_modal").val("");
        $("#count_modal").val("");
        $("#myModal").modal('toggle');

    }


};


task = {
    listTask: function () {
        $('#task_table').DataTable({
            "ajax": {
                "url": "/fileservice/task/listAll"
            },
            "columns": [
                {"data": "taskId"},
                {"data": "taskName"},
                {"data": "startTime"},
                {"data": "endTime"},
                {"data": "status"},
            ],
            columnDefs: [{
                "targets": 3,
                "data": null,
                "defaultContent": "---"
            }, {
                "targets": 2,
                render: function (data, type, row, meta) {
                    var unixTimestamp = new Date(row.startTime);
                    commonTime = unixTimestamp.toLocaleString();
                    return commonTime;
                },
            }, {
                "targets": 3,
                render: function (data, type, row, meta) {
                    if (row.endTime == null) return "--";
                    var unixTimestamp = new Date(row.endTime);
                    commonTime = unixTimestamp.toLocaleString();
                    return commonTime;
                },
            }, {
                "targets": 5,
                render: function (data, type, row, meta) {
                    console.log(row)
                    var html = '<input type="button" class="btn btn-danger" value="删除" onclick="task.deleteTask(' + row.taskId + ')"/>';
                    if (row.status == "已完成") {
                        html += '<input type="button" class="btn btn-danger" value="详细" onclick="task.resultDetail(' + row.taskId + ')"/>';
                    }
                    return html;
                },
            }],
            "bLengthChange": false,
            "searching": false,
            "bSort": false,
        });
    },
    resultDetail: function (taskId) {

        //window.location.href = "./result.html?taskId=" + taskId;
        window.open("./result.html?taskId=" + taskId);

    },
    deleteTask: function (taskId) {
        $.post("/fileservice/task/del/" + taskId, function (result) {
            if (result.success == true) {
                common.showNotification('top', 'center', null, "删除成功");
                setTimeout(function () {
                    location.reload();
                }, 700);
            } else {
                common.showNotification('top', 'center', 4, "删除失败")
            }
        });
    }
}

result = {
    listResult: function () {
        var taskId = common.getQueryString("taskId");
        var fieldNames;

        $.post("/fileservice/task/listResultFieldNames/" + taskId, function (data) {

            if (data.success = true) {
                fieldNames = data.data;
                var thead = "<tr>";
                for (var i = 0; i < fieldNames.length; i++) {
                    thead += "<th>" + fieldNames[i] + "</th>";
                }
                $("#result_thead").html(thead + "<th>来源文件</th><th>操作</th></tr>");
            }
        });

        $.post("/fileservice/task/listResult/" + taskId, function (data) {
            if (data.success == true) {
                var html = "";
                for (var i = 0; i < data.data.length; i++) {
                    html += "<tr>";
                    for (var j = 0; j < fieldNames.length; j++) {
                        var t = fieldNames[j];
                        var td = "--";
                        if (data.data[i][t] != undefined)
                            td = data.data[i][t].fieldResult.result;

                        if (data.data[i][t] != undefined && data.data[i][t].count > 1)
                            html += "<td title='来源' data-container='body' data-toggle='popover' data-content='" + data.data[i][t].fieldResult.prefix + "<font color=red>" + td + "</font>" + data.data[i][t].fieldResult.suffix + "'>" + td + "<span title='多个结果' class='badge' onclick='result.showMultiResult(" + data.data[i][t].fieldResult.frid + ")'>" + data.data[i][t].count + "</span></td>"
                        else if (data.data[i][t] != undefined)
                            html += "<td title='来源' data-container='body' data-toggle='popover' data-content='" + data.data[i][t].fieldResult.prefix + "<font color=red>" + td + "</font>" + data.data[i][t].fieldResult.suffix + "'>" + td + "</td>";
                        else html += "<td>" + td + "</td>";
                        //"+"<td>"+data.data[i][t].fieldResult.file+"</td>
                    }
                    var keys = Object.keys(data.data[i]);
                    var key = keys[0];
                    html += "<td>" + data.data[i][key].fieldResult.file + "</td><td><input type='button' class='btn btn-danger' onclick='result.delResultByFile(" + data.data[i][key].fieldResult.taskId + "," + data.data[i][key].fieldResult.frid + ")' value='删除' /></td></tr>"
                }
                $("#result_tbody").html(html);
                $('#task_table').DataTable({
                    "bLengthChange": false,
                    "searching": false,
                    "bSort": false,
                });
                $("[data-toggle='popover']").popover({
                    html: true,
                    placement: "top",
                    trigger: "hover"
                });
            }

        });
    },
    delResultByFile: function (taskId, frid) {
        $.post("fileservice/task/delResultByFile", {"taskId": taskId, "frid": frid}, function (result) {
            if (result.success == true) {
                common.showNotification('top', 'center', null, "删除成功");
                location.reload();

            } else {
                common.showNotification('top', 'center', 4, "删除失败")
            }
        });
    },
    showMultiResult: function (frid) {
        // alert(frid);
        $.post("/fileservice/task/listMultiResult/" + frid, function (result) {
            if (result.success == true) {
                var html;
                for (var i = 0; i < result.data.length; i++) {
                    html += "<tr><td>" + result.data[i].prefix + "<strong><font color='red'>" + result.data[i].result + "</font></strong>" + result.data[i].suffix + "</td><td><input type='button' class='btn btn-default' value='确认' onclick='result.checkResult(" + result.data[i].frid + ")'/></td></tr>"

                }

                $("#multi_tbody").html(html);

            }
        });
        $("#myModal").modal('toggle');
    },
    checkResult: function (frid) {
        $.post("/fileservice/task/checkResult/" + frid, function (result) {
            if (result.success == true) {
                common.showNotification('top', 'center', null, "删除成功");
                location.reload();

            } else {
                common.showNotification('top', 'center', 4, "删除失败")
            }
        });
    },
    download: function () {
        var taskId = common.getQueryString("taskId");
        console.log(taskId);
        console.log($('#result_tbody'));

        var params = new Array();
        $('#result_tbody tr').each(function (i) {
            var arr = new Array();
            $(this).children('td').each(function (j) {  // 遍历 tr 的各个 td
                //alert("第" + (i + 1) + "行，第" + (j + 1) + "个td的值：" + $(this).text() + "。");
                arr[j]=$(this).text();
            });
            params[i]=arr;

        });
        console.log(JSON.stringify({"data":params}));
        $.ajax({
            type: "POST",
            url: "fileservice/task/downloadResult/"+taskId,
            data:JSON.stringify({"data":params}),
            dataType: "json",
            contentType:"application/json;charset=utf-8",
            });
       // window.location.href="fileservice/task/downloadResult/"+taskId

    }

}


user = {
    register:function () {
        var username = $("#username").val()
        var password = $("#password").val()
        var vcode = $("#vcode").val();
        console.log(username + "  " + password + "  " + vcode)
        if (username == null || password == null || vcode == null) {
            alert("请输入完整信息");
            return;
        }
        $.post("http://localhost/doRegister", {
            "username": username,
            "password": password,
            "vcode": vcode
        }, function (result) {
            if (result.success == true) window.location.href = "/";
            else {
                if(result.success == false) alert(result.msg);
                //alert("注册失败！");
            }
        });
    },
    login: function () {
        var username = $("#username").val()
        var password = $("#password").val()
        var vcode = $("#vcode").val();
        console.log(username + "  " + password + "  " + vcode)
        if (username == null || password == null || vcode == null) {
            alert("请输入完整信息");
            return;
        }
        $.post("http://localhost/doLogin", {
            "username": username,
            "password": password,
            "vcode": vcode
        }, function (result) {
            if (result.success == true) window.location.href = "/";
            else {
                alert("登录失败！");
            }
        });
    }
}


common = {
    showNotification: function (from, align, color, msg) {
        if (isNaN(color))
            color = Math.floor((Math.random() * 4) + 1);

        $.notify({
            icon: "ti-gift",
            message: msg,

        }, {
            type: type[color],
            timer: 1000,
            placement: {
                from: from,
                align: align
            }
        });
    },
    getQueryString: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); // 匹配目标参数
        var result = window.location.search.substr(1).match(reg); // 对querystring匹配目标参数
        if (result != null) {
            return decodeURIComponent(result[2]);
        } else {
            return null;
        }
    },

}