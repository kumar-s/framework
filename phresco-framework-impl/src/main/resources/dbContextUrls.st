<div class="middle_div">
	<span>DB Context URLs</span>
	<div class="clear"></div>
</div>
<script type="text/javascript">
	var dbJsonFlag = true;
</script>
<div id="dbContextDivParent">
	$myObject:{ obj |
		$obj.dbContextUrls: { dbContextUrlsObj |
			<script type="text/javascript">
				dbJsonFlag = false;
			</script>
		<div id="dbContextDiv" class="dbContextDivClass" style="margin-bottom:5px;">
			<table class="table table-striped table_border table-bordered context_table border_div
" cellpadding="0" cellspacing="0" border="0">
				<thead>
				    <tr>
				    	<th colspan="4">
						<img width="15" height="15" title="Add DB Context" style="cursor:pointer;" border="0" alt="" src="themes/default/images/Phresco/small_plus_icon.png" name="dbContextAdd"
							 class="dbContextAdd">
				        <div class="delete_icon removeDBContext">
				        	<img class="removeContextImg" src="themes/default/images/Phresco/delete_row.png">
				        </div>
				        </th>
				    </tr>
				</thead>
				<tbody>
				    <tr>
				        <td>
							<label>Name<sup>*</sup></label>
							<input type="text" name="dbName" value="$dbContextUrlsObj.name$">
						</td>
				        <td>
				        	<label>Query Type<sup>*</sup></label>
							<select name="queryType" id="queryType$dbContextUrlsObj.name$">
								<option value="Select Statement">Select Statement</option>
								<option value="Update Statement">Update Statement</option>
							</select>
				        </td>
				        <td>
							<label>Query<sup>*</sup></label>
							<textarea name="query" id="query">$dbContextUrlsObj.query$</textarea>
				        </td>
				    </tr>
				</tbody>
			</table>
		</div>	
		<script type="text/javascript">
				document.getElementById("queryType$dbContextUrlsObj.name$").value = '$dbContextUrlsObj.queryType$';
			</script>
		}$	
	}$	
</div>	
<script type="text/javascript">
	if(dbJsonFlag) {
		var newTextBoxDiv = jQuery(document.createElement('div')).attr('id', 'dbContextDiv').attr('class','dbContextDivClass');
		newTextBoxDiv.html("<table class='table table-striped table_border table-bordered context_table border_div
' cellpadding='0' cellspacing='0' border='0'>
			<thead><tr><th colspan='4'><img width='15' height='10' style='cursor:pointer;' title='Add DB Context' border='0' alt='' src='themes/default/images/Phresco/small_plus_icon.png' name='dbContextAdd' class='dbContextAdd'><div class='delete_icon removeDBContext'><img class='removeContextImg' src='themes/default/images/Phresco/delete_row.png'></div></th></tr>
			</thead><tbody><tr>
			<td><label>Name<sup>*</sup></label><input type='text' name='dbName' value=''></td><td><label>Query Type<sup>*</sup></label><select name='queryType'><option value='Select Statement'>Select Statement</option><option value='Update Statement'>Update Statement</option></select></td><td><label>Query<sup>*</sup></label><textarea name='query' id='query'></textarea></td></tr></tbody></table>");
		newTextBoxDiv.appendTo("#dbContextDivParent");	
	}
</script>	