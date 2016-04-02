<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="bootswatch"/>


    <asset:stylesheet src="../bower_components/jquery-week-calendar/jquery.weekcalendar.css"/>
    <asset:stylesheet src="../bower_components/jquery-week-calendar/full_demo/reset.css"/>
    <asset:stylesheet src="../bower_components/jquery-week-calendar/full_demo/demo.css"/>
    <asset:stylesheet src="../bower_components/jquery-week-calendar/skins/gcalendar.css"/>

    <!--
	<link rel='stylesheet' type='text/css' href='${resource(dir: 'bower_components/bootstrap-wysiwyg', file: 'index.css')}' />
	<link rel='stylesheet' type='text/css' href='${resource(dir: 'bootstrap-colorpalette/css', file: 'bootstrap-colorpalette.css')}' />
    -->
    <style>
    .wc-business-hours {
        font-size: 100%;
    }
    input[name=title] {
        width: 700px;
        color: #000000;
    }
    </style>
	<script src="${resource(dir: 'js/jquery-week-calendar-master', file: 'jquery.weekcalendar.js')}"></script>
	<script src="${resource(dir: 'bower_components/bootstrap-wysiwyg', file: 'bootstrap-wysiwyg.js')}"></script>
	<script src="${resource(dir: 'bower_components/bootstrap-wysiwyg/external', file: 'jquery.hotkeys.js')}"></script>
	<script src="${resource(dir: 'bootstrap-colorpalette/js', file: 'bootstrap-colorpalette.js')}"></script>
 
    <script type="text/javascript">
/*!
 * Cross-Browser Split 1.1.1
 * Copyright 2007-2012 Steven Levithan <stevenlevithan.com>
 * Available under the MIT License
 * ECMAScript compliant, uniform cross-browser split method
 */

/**
 * Splits a string into an array of strings using a regex or string separator. Matches of the
 * separator are not included in the result array. However, if `separator` is a regex that contains
 * capturing groups, backreferences are spliced into the result each time `separator` is matched.
 * Fixes browser bugs compared to the native `String.prototype.split` and can be used reliably
 * cross-browser.
 * @param {String} str String to split.
 * @param {RegExp|String} separator Regex or string to use for separating the string.
 * @param {Number} [limit] Maximum number of items to include in the result array.
 * @returns {Array} Array of substrings.
 * @example
 *
 * // Basic use
 * split('a b c d', ' ');
 * // -> ['a', 'b', 'c', 'd']
 *
 * // With limit
 * split('a b c d', ' ', 2);
 * // -> ['a', 'b']
 *
 * // Backreferences in result array
 * split('..word1 word2..', /([a-z]+)(\d+)/i);
 * // -> ['..', 'word', '1', ' ', 'word', '2', '..']
 */
var split;

// Avoid running twice; that would break the `nativeSplit` reference
split = split || function (undef) {

    var nativeSplit = String.prototype.split,
        compliantExecNpcg = /()??/.exec("")[1] === undef, // NPCG: nonparticipating capturing group
        self;

    self = function (str, separator, limit) {
        // If `separator` is not a regex, use `nativeSplit`
        if (Object.prototype.toString.call(separator) !== "[object RegExp]") {
            return nativeSplit.call(str, separator, limit);
        }
        var output = [],
            flags = (separator.ignoreCase ? "i" : "") +
                    (separator.multiline  ? "m" : "") +
                    (separator.extended   ? "x" : "") + // Proposed for ES6
                    (separator.sticky     ? "y" : ""), // Firefox 3+
            lastLastIndex = 0,
            // Make `global` and avoid `lastIndex` issues by working with a copy
            separator = new RegExp(separator.source, flags + "g"),
            separator2, match, lastIndex, lastLength;
        str += ""; // Type-convert
        if (!compliantExecNpcg) {
            // Doesn't need flags gy, but they don't hurt
            separator2 = new RegExp("^" + separator.source + "$(?!\\s)", flags);
        }
        /* Values for `limit`, per the spec:
         * If undefined: 4294967295 // Math.pow(2, 32) - 1
         * If 0, Infinity, or NaN: 0
         * If positive number: limit = Math.floor(limit); if (limit > 4294967295) limit -= 4294967296;
         * If negative number: 4294967296 - Math.floor(Math.abs(limit))
         * If other: Type-convert, then use the above rules
         */
        limit = limit === undef ?
            -1 >>> 0 : // Math.pow(2, 32) - 1
            limit >>> 0; // ToUint32(limit)
        while (match = separator.exec(str)) {
            // `separator.lastIndex` is not reliable cross-browser
            lastIndex = match.index + match[0].length;
            if (lastIndex > lastLastIndex) {
                output.push(str.slice(lastLastIndex, match.index));
                // Fix browsers whose `exec` methods don't consistently return `undefined` for
                // nonparticipating capturing groups
                if (!compliantExecNpcg && match.length > 1) {
                    match[0].replace(separator2, function () {
                        for (var i = 1; i < arguments.length - 2; i++) {
                            if (arguments[i] === undef) {
                                match[i] = undef;
                            }
                        }
                    });
                }
                if (match.length > 1 && match.index < str.length) {
                    Array.prototype.push.apply(output, match.slice(1));
                }
                lastLength = match[0].length;
                lastLastIndex = lastIndex;
                if (output.length >= limit) {
                    break;
                }
            }
            if (separator.lastIndex === match.index) {
                separator.lastIndex++; // Avoid an infinite loop
            }
        }
        if (lastLastIndex === str.length) {
            if (lastLength || !separator.test("")) {
                output.push("");
            }
        } else {
            output.push(str.slice(lastLastIndex));
        }
        return output.length > limit ? output.slice(0, limit) : output;
    };

    // For convenience
    String.prototype.split = function (separator, limit) {
        return self(this, separator, limit);
    };

    return self;

}();


    function getInternetExplorerVersion() {
        // Returns the version of Windows Internet Explorer or a -1
        // (indicating the use of another browser).
        var rv = -1; // Return value assumes failure.
        if (navigator.appName == 'Microsoft Internet Explorer') {
            var ua = navigator.userAgent;
            var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
            if (re.exec(ua) != null)
                rv = parseFloat( RegExp.$1 );
        }
        return rv;
    }

    $(document).ready(function() {
        // close sidebar
        $('#sidebar').addClass('menu-min');
        // wysiwyg
        $('#editor').wysiwyg();
        // colorpalette
        $('#fcolorpalette').colorPalette().on('selectColor', function(e) {
            $('#fc').data("edit", "foreColor " + e.color).trigger("click");
        });
        $('#bcolorpalette').colorPalette().on('selectColor', function(e) {
            $('#bc').data("edit", "backColor " + e.color).trigger("click");
        });
        // calendar
        var $calendar = $('#calendar');
    
        $calendar.weekCalendar({ 
            date: ${dateLong},
            timeFormat: 'H:i', 
            dateFormat: 'Y/m/d', 
            use24Hour: true, 
            firstDayOfWeek: 0,
            daysToShow: 7,
            timeSeparator: ' 到 ',
            businessHours: {start: 8, end: 20, limitDisplay: true},
            timeslotsPerHour: 4, 
            buttonText: {today : "本周", lastWeek : "前一周", nextWeek : "后一周"}, 
            height: function($calendar) {
                return $(window).height() - 180; // - $('.navbar').outerHeight(true);
            },
            hourLine: true,
            longDays: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'], 
            displayOddEven:true,
            allowCalEventOverlap : true,
            overlapEventsSeparate: true,
            title: function(daysToShow) {
                return daysToShow == 1 ? '%date%' : '%start% - %end%';
            },
            users: ${rooms},
            showAsSeparateUser: true,            
            eventRender : function(calEvent, $event) {
                if (calEvent.end.getTime() < new Date().getTime()) {
                    $event.css("backgroundColor", "#aaa");
                    $event.find(".wc-time").css({
                       "backgroundColor" : "#999",
                       "border" : "1px solid #888"
                    });
                }
            },
            draggable : function(calEvent, $event) {
                return calEvent.readOnly != true;
            },
            resizable : function(calEvent, $event) {
                return calEvent.readOnly != true;
            },
            eventNew : function(calEvent, $event) {
                // if click old time, do nothing
                if (calEvent.start < new Date()) {
                    $('#calendar').weekCalendar("removeUnsavedEvents");
                    return;
                }
                var $dialogContent = $("#event_edit_container");
                resetForm($dialogContent);
                var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
                var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
                var titleField = $dialogContent.find("input[name='title']");
                var bodyField = $dialogContent.find("#editor");
                $dialogContent.dialog({
                    width: 900,
                    modal: true,
                    title: "新增会议",
                    close: function() {
                        $dialogContent.dialog("destroy");
                        $dialogContent.hide();
                        $('#calendar').weekCalendar("removeUnsavedEvents");
                    },
                    buttons: {
                        "储存" : function() {
                            var start = new Date(startField.val());
                            var end = new Date(endField.val());
                            var title = titleField.val();
                            var body = bodyField.html();
                            $.ajax({
                                url: 'create_event',
                                type: 'post',
                                data: {start: start.getTime(), end: end.getTime(), title: title, body: body, userId: calEvent.userId}, 
                                dataType: 'json'
                            }).done(function(json) {
                                calEvent.id = json.id;
                                calEvent.start = start;
                                calEvent.end = end;
                                calEvent.title = title;
                                calEvent.body = body;
                                $calendar.weekCalendar("removeUnsavedEvents");
                                $calendar.weekCalendar("updateEvent", calEvent);
                                $dialogContent.dialog("close");
                            }).fail(function(json) {
                                alert('AJAX FAIL!');    
                            });  
                        },
                        "关闭" : function() {
                            $dialogContent.dialog("close");
                        }
                    }
                }).show();
                $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
                setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));
            },
            eventDrop : function(calEvent, $event) {
                $.ajax({
                    url: 'update_event',
                    type: 'get',
                    data: {start: calEvent.start.getTime(), end: calEvent.end.getTime(), userId: calEvent.userId, id: calEvent.id}, 
                    dataType: 'json'
                }).done(function(json) {
                    // DO NOTHING
                }).fail(function(json) {
                    alert('AJAX FAIL!');    
                });   
            },
            eventResize : function(calEvent, $event) {
                $.ajax({
                    url: 'update_event',
                    type: 'get',
                    data: {start: calEvent.start.getTime(), end: calEvent.end.getTime(), userId: calEvent.userId, id: calEvent.id}, 
                    dataType: 'json'
                }).done(function(json) {
                    // DO NOTHING
                }).fail(function(json) {
                    alert('AJAX FAIL!');    
                });     
            },
            eventClick : function(calEvent, $event) {
                if (calEvent.readOnly) {
                    return;
                }
                var $dialogContent = $("#event_edit_container");
                resetForm($dialogContent);
                var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
                var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
                var titleField = $dialogContent.find("input[name='title']").val(calEvent.title);
                var bodyField = $dialogContent.find("#editor").html(calEvent.body);
                $dialogContent.find("#link").html('<a class="btn btn-primary" href="show/' + calEvent.id + '">详细会议连结</a>');
                $dialogContent.dialog({
                    width: 900,
                    modal: true,
                    title: "编辑 - " + calEvent.title,
                    close: function() {
                        $dialogContent.dialog("destroy");
                        $dialogContent.hide();
                        $('#calendar').weekCalendar("removeUnsavedEvents");
                    },
                    buttons: {
                        "储存" : function() {
                            var start = new Date(startField.val());
                            var end = new Date(endField.val());
                            var title = titleField.val();
                            var body = bodyField.html();
                            $.ajax({
                                url: 'update_event',
                                type: 'post',
                                data: {start: start.getTime(), end: end.getTime(), title: title, body: body, userId: calEvent.userId, id: calEvent.id}, 
                                dataType: 'json'
                            }).done(function(json) {
                                calEvent.start = start;
                                calEvent.end = end;
                                calEvent.title = title;
                                calEvent.body = body;
                                $calendar.weekCalendar("updateEvent", calEvent);
                                $dialogContent.dialog("close");
                            }).fail(function(json) {
                                alert('AJAX FAIL!');    
                            });                          
                        },
                        "删除" : function() {
                            var cnt = calEvent.title.split(/[\u4e00-\u9a05]/).length - 1;
                            var inp = prompt("会议主题, 包含几个中文字 ?\n-------------------------\n" + calEvent.title + "\n-------------------------\n答对才可以删除", "");
                            if (inp == null) {
                                // DO NOTHING
                            } else if (parseInt(cnt, 10) == parseInt(inp, 10)) {
                                $.ajax({
                                    url: 'delete_event',
                                    type: 'get',
                                    data: {id: calEvent.id}, 
                                    dataType: 'json'
                                }).done(function(json) {
                                    $calendar.weekCalendar("removeEvent", calEvent.id);
                                    $dialogContent.dialog("close");
                                }).fail(function(json) {
                                    alert('AJAX FAIL!');    
                                });  
                            } else {
                                alert('答错了!');
                            }
                        },
                        "关闭" : function() {
                            $dialogContent.dialog("close");
                        }
                    }
                }).show();

                var startField = $dialogContent.find("select[name='start']").val(calEvent.start);
                var endField = $dialogContent.find("select[name='end']").val(calEvent.end);
                $dialogContent.find(".date_holder").text($calendar.weekCalendar("formatDate", calEvent.start));
                setupStartAndEndTimeFields(startField, endField, calEvent, $calendar.weekCalendar("getTimeslotTimes", calEvent.start));
                $(window).resize().resize(); //fixes a bug in modal overlay size ??
            },
            eventMouseover : function(calEvent, $event) {

            },
            eventMouseout : function(calEvent, $event) {

            },
            noEvents : function() {
    
            },
            calendarBeforeLoad: function(calendar) {
            },
            calendarAfterLoad: function(calendar) {
            },         
            data : function(start, end, callback) {
                $.getJSON("get_events", {
                    start: start.getTime(),
                    end: end.getTime()
                },  function(result) {
                    callback(result);
                });
            }
        });

        function resetForm($dialogContent) {
            $dialogContent.find("input").val("");
            $dialogContent.find("#editor").html("");
            $dialogContent.find("#link").html("");
        }

       /*
        * Sets up the start and end time fields in the calendar event
        * form for editing based on the calendar event being edited
        */
        function setupStartAndEndTimeFields($startTimeField, $endTimeField, calEvent, timeslotTimes) {
            $startTimeField.empty();
            $endTimeField.empty();
            for (var i = 0; i < timeslotTimes.length; i++) {
                var startTime = timeslotTimes[i].start;
                var endTime = timeslotTimes[i].end;
                var startSelected = "";
                if (startTime.getTime() === calEvent.start.getTime()) {
                    startSelected = 'selected="selected"';
                }
                var endSelected = "";
                if (endTime.getTime() === calEvent.end.getTime()) {
                    endSelected = 'selected="selected"';
                }
                $startTimeField.append('<option value="' + startTime + '" ' + startSelected + '>' + timeslotTimes[i].startFormatted + '</option>');
                $endTimeField.append('<option value="' + endTime + '" ' + endSelected + '>' + timeslotTimes[i].endFormatted + '</option>');
                $timestampsOfOptions.start[timeslotTimes[i].startFormatted] = startTime.getTime();
                $timestampsOfOptions.end[timeslotTimes[i].endFormatted] = endTime.getTime();
            }
            $endTimeOptions = $endTimeField.find("option");
            $startTimeField.trigger("change");
        }

        var $endTimeField = $("select[name='end']");
        var $endTimeOptions = $endTimeField.find("option");
        var $timestampsOfOptions = {start:[],end:[]};
        
        //reduces the end time options to be only after the start time options.
        $("select[name='start']").change(function() {
            var startTime = $timestampsOfOptions.start[$(this).find(":selected").text()];
            var currentEndTime = $endTimeField.find("option:selected").val();
            // filter options
            $endTimeField.html(
                $endTimeOptions.filter(function() {
                    return startTime < $timestampsOfOptions.end[$(this).text()];
                })
            );

            var endTimeSelected = false;
            $endTimeField.find("option").each(function() {
                if ($(this).val() === currentEndTime) {
                    $(this).prop("selected", "selected");
                    endTimeSelected = true;
                    return false;
                }
            });

            if (!endTimeSelected) {
                //automatically select an end date 2 slots away.
                $endTimeField.find("option:eq(1)").prop("selected", "selected");
            }
        });

    });
    
    </script>
</head>
<content tag="title">会议安排</content>
<content tag="subtitle">resource booking</content>
<body> 
	<div id='calendar'></div>
	<div id="event_edit_container">
		<form>
            <div class="row-fluid">
                <div class="span2 text-right">会议日期:</div>
                <div class="span6"><span class="date_holder"></span></div>
                <div class="span4" id="link"></div>
            </div>
            <div class="row-fluid">
                <div class="span2 text-right">开始时间: </div>
                <div class="span10"><select name="start"><option value="">Select Start Time</option></select></div>
            </div>
            <div class="row-fluid">
                <div class="span2 text-right">结束时间: </div>
                <div class="span10"><select name="end"><option value="">Select End Time</option></select></div>
            </div>
            <div class="row-fluid">
                <div class="span2 text-right">会议主题: </div>
                <div class="span10"><input type="text" name="title" /></div>
            </div>
            <div class="row-fluid">
                <div class="span2 text-right">会议内容: </div>
                <div class="span10">
                    <div class="btn-toolbar" data-role="editor-toolbar" data-target="#editor">
                        <div class="btn-group">
                            <a class="btn" data-edit="foreColor" id="fc" style="display: none;">foreColor</a>
                            <a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" title="字体颜色"><i class="icon-pencil"></i>&nbsp;<b class="caret"></b></a> 
                            <ul class="dropdown-menu">
                                <li><div id="fcolorpalette"></div></li>
                            </ul>
                        </div>
                        <div class="btn-group">
                            <a class="btn" data-edit="backColor" id="bc" style="display: none;">backColor</a>
                            <a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" title="字体背景颜色"><i class="icon-tint"></i>&nbsp;<b class="caret"></b></a> 
                            <ul class="dropdown-menu">
                                <li><div id="bcolorpalette"></div></li>
                            </ul>
                        </div>
                        <div class="btn-group">
                            <a class="btn btn-primary" data-edit="bold" title="加粗 (Ctrl+B)"><i class="icon-bold"></i></a>
                            <a class="btn btn-primary" data-edit="italic" title="倾斜 (Ctrl+I)"><i class="icon-italic"></i></a>
                            <a class="btn btn-primary" data-edit="strikethrough" title="删除线"><i class="icon-strikethrough"></i></a>
                            <a class="btn btn-primary" data-edit="underline" title="下划线 (Ctrl+U)"><i class="icon-underline"></i></a>
                        </div>
                        <div class="btn-group">
                            <a class="btn btn-primary" data-edit="justifyleft" title="左对齐 (Ctrl+L)"><i class="icon-align-left"></i></a>
                            <a class="btn btn-primary" data-edit="justifycenter" title="居中 (Ctrl+E)"><i class="icon-align-center"></i></a>
                            <a class="btn btn-primary" data-edit="justifyright" title="右对齐 (Ctrl+R)"><i class="icon-align-right"></i></a>
                        </div>
                        <div class="btn-group">
                            <a class="btn btn-primary" data-edit="undo" title="撤销 (Ctrl+Z)"><i class="icon-undo"></i></a>
                        </div>
                    </div>
                    <div id="editor">
                    </div>
                </div>
            </div>
		</form>
	</div>


</body>
</html>
