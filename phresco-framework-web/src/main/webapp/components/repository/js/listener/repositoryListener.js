define([], function() {

	Clazz.createPackage("com.components.repository.js.listener");

	Clazz.com.components.repository.js.listener.RepositoryListener = Clazz.extend(Clazz.WidgetWithTemplate, {
		initialize : function(config) {
			var self = this;
		},
		
		repository : function(header, callback) {
			var self = this;
			try {
				commonVariables.api.ajaxRequest(header,
					function(response) {
						if (commonVariables.callLadda) {
							Ladda.stopAll();
						}
						if (response !== null && response.status !== "error" && response.status !== "failure") {
							callback(response);
						} else {
							commonVariables.api.showError(response.responseCode ,"error", true);
						}
					},
					function(textStatus) {
						commonVariables.api.showError("serviceerror" ,"error", true);
					}
				);
			} catch(exception) {
				
			}

		},
		
		getActionHeader : function(requestBody, action) {
			var self=this, header, data = {}, userId, projectId;
			var customerId = self.getCustomer();
			projectId = commonVariables.api.localVal.getSession('projectId');
			customerId = (customerId === "") ? "photon" : customerId;
			data = JSON.parse(commonVariables.api.localVal.getSession('userInfo'));
			if (data !== undefined && data !== null && data !== "") {
				userId = data.id; 
			}
			header = {
				contentType: "application/json",				
				dataType: "json",
				webserviceurl: ''
			};
			if (action === "browseBuildRepo") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/browseBuildRepo?userId='+userId+'&customerId='+customerId+'&projectId='+projectId;
			}
			if (action === "browseSourceRepo") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/browseSourceRepo?userId='+userId+'&customerId='+customerId+'&projectId='+projectId+'&userName=admin&password=manage';
			}
			if (action === "artifactInfo") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/artifactInfo?userId='+userId+'&customerId='+customerId+'&appDirName='+requestBody.appDirName+'&version='+requestBody.version+'&nature='+requestBody.nature;
				if (!self.isBlank(requestBody.moduleName)) {
					header.webserviceurl = header.webserviceurl + "&moduleName="+requestBody.moduleName
				}
			}
			if (action === "downloadBuild") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/download?userId='+userId+'&customerId='+customerId+'&appDirName='+requestBody.appDirName+'&version='+requestBody.version+'&nature='+requestBody.nature;
				if (!self.isBlank(requestBody.moduleName)) {
					header.webserviceurl = header.webserviceurl + "&moduleName="+requestBody.moduleName
				}
			}
			if (action === "createBranch") {
				header.requestMethod = "POST";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/createBranch?url='+requestBody.url+'&version='+requestBody.version+'&username='+requestBody.username+'&password='+requestBody.password + 
										'&comment='+requestBody.comment+'&currentbranchname='+requestBody.currentbranchname+'&branchname='+requestBody.branchname+'&downloadoption='+requestBody.downloadoption;
			}
			if (action === "createTag") {
				header.requestMethod = "POST";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/createTag?url='+requestBody.url+'&username='+requestBody.username+'&password='+requestBody.password+'&version='+requestBody.version +
										'&comment='+requestBody.comment+'&currentbranchname='+requestBody.currentbranchname+'&tagname='+requestBody.tagname+'&downloadoption='+requestBody.downloadoption;
			}
			if (action === "getVersion") {
				header.requestMethod = "GET";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/version?url='+requestBody.url+'&currentbranchname='+requestBody.currentbranchname;
			}
			if (action === "release") {
				header.requestMethod = "POST";
				header.webserviceurl = commonVariables.webserviceurl + 'repository/release?appDirName='+requestBody.appDirName+'&username='+requestBody.username+'&password='+requestBody.password +
										'&message='+requestBody.comment+'&developmentVersion='+requestBody.developmentVersion+'&releaseVersion='+requestBody.releaseVersion+'&tag='+requestBody.tag+'&environmentName='+requestBody.environmentName +
										'&branchName='+requestBody.branchName+'&deploy='+requestBody.deploy;
			}
			
			return header;
		},
		
		getxmlDoc : function(xmlStr, callback) {
			var parseXml;
			if (window.DOMParser) {
				parseXml = function(xmlStr) {
					return ( new window.DOMParser() ).parseFromString(xmlStr, "text/xml");
				};
			}
			callback(parseXml(xmlStr));
		},
		
		constructTree : function(xmlStr) {
			var self = this;
			self.getxmlDoc(xmlStr, function(xmlDoc) {
				var rootItem = $(xmlDoc).contents().children().children();
				self.getList(rootItem, function(returnValue) {
					setTimeout(function() {
						$('.tree').append(returnValue);
						$('.bbtreeview').jstree({"ui" : {
							"select_limit" : 2,
							"select_multiple_modifier" : "alt",
							"selected_parent_close" : "select_parent"
						}, "plugins" : [ "themes", "html_data", "ui" ]}).bind("loaded.jstree");
						
						$('.tree li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');
						$('.tree li.parent_li > span').unbind('click');
						$('.tree li.parent_li > span').bind('click', function (e) {
							var children = $(this).parent('li.parent_li').find(' > ul > li');
							if (children.is(":visible")) {
								children.hide('fast');
								$(this).attr('title', 'Expand this branch').find(' > i').addClass('icon-plus-sign').removeClass('icon-minus-sign');
							} else {
								children.show('fast');
								var iElement = $(this).attr('title', 'Collapse this branch').find(' > i');
								if (iElement.hasClass('icon-plus-sign')) {
									iElement.addClass('icon-minus-sign').removeClass('icon-plus-sign');
								}
							}
							e.stopPropagation();
						});
						
						$('.badge-success').each(function() {
							var len = $(this).parent().find("li:visible").length;
							if (len > 0) {
								$(this).find("i").removeClass("icon-plus-sign").addClass("icon-minus-sign");
							}
						});
						
						$(".badge-warning").unbind("click");
						$(".badge-warning").bind("click", function() {
							var currentTab = commonVariables.navListener.currentTab;
							var appDirName = $(this).find('a').attr('appdirname');
							if (currentTab === "buildRepo") {
								var nature = $(this).find('a').attr('nature');
								var moduleName = $(this).find('a').attr('modulename');
								var version = $(this).parent().parent().prev().find('a').text();
								if (appDirName !== undefined && nature !== undefined) {
									self.getArtifactInformation(appDirName, nature, version, moduleName);
								}
							}
							if (currentTab === "sourceRepo") {
								var nature = $(this).find('a').attr("nature");
								if (nature === "branches") {
									$('input[name=selectedAppDirName]').val(appDirName);
									var baseRepoUrl = $(this).find('a').attr("url");
									$("input[name=baseRepoUrl]").val(baseRepoUrl);
									var selectedBranch = $(this).find('a').text();
									$('#fileListHead').text(selectedBranch);
									$("input[name=selectedBranchName]").val(selectedBranch);
									var requestBody = {};
									requestBody.url = baseRepoUrl;
									requestBody.currentbranchname = selectedBranch;
									self.repository(self.getActionHeader(requestBody, "getVersion"), function(response) {
										$("#branchFromVersion").val(response.data);
										$("#tagFromVersion").val(response.data);
										$("#releaseVersion").val(response.data);
										$(".file_view").show();
										setTimeout(function() {
											$(this).css("background", "#e3e3e3 !important");
										}, 1000);
									});
								} else {
									$(".file_view").hide();
								}
							}
						});
					}, 500);
				});
			});
		},
		
		getArtifactInformation : function(appDirName, nature, version, moduleName) {
			var self = this;
			var requestBody = {};
			requestBody.appDirName = appDirName;
			requestBody.nature = nature;
			requestBody.version = version;
			requestBody.moduleName = moduleName;
			self.repository(self.getActionHeader(requestBody, "artifactInfo"), function(response) {
				var responseData = response.data;
				if (responseData !== undefined && responseData !== null) {
					$('#infoMessagedisp').hide();
					$('.file_view').show();
					var artifactInfo = responseData.artifactInfo;
					$('#repoPath').text(artifactInfo.repositoryPath);
					$('#uploader').text(artifactInfo.uploader);
					$('#size').text((artifactInfo.size/1024).toFixed(2) + " KB");
					$('#uploadedDate').text(new Date(artifactInfo.uploaded));
					$('#modifiedDate').text(new Date(artifactInfo.lastChanged));
					var mavenInfo = responseData.mavenInfo;
					$('#groupId').text(mavenInfo.groupId);
					$('#artifactId').text(mavenInfo.artifactId);
					$('#artifactVersion').text(mavenInfo.version);
					$('#artifactType').text(mavenInfo.extension);
					var xmlContent = "<dependency>" + 
										"<groupId>" + mavenInfo.groupId + "</groupId>" +
					  					"<artifactId>"+ mavenInfo.artifactId +"</artifactId>" +
				  						"<version>" + mavenInfo.version + "</version>";
					if (!self.isBlank(mavenInfo.extension)) {
						xmlContent = xmlContent + "<type>"+ mavenInfo.extension +"</type"
					}
					xmlContent = xmlContent + "</dependency>";
					$('#artifactXml').val(xmlContent);
				}
			});
			
			$("input[name=downloadArtifact]").attr("appdirname", appDirName).attr("nature", nature).attr("version", version).attr("moduleName", moduleName);
		},
		
		getList : function (ItemList, callback) {
			var self = this;
			var strUl = "", strRoot ="", strItems ="", strCollection = "";
			$(ItemList).each(function(index, value) {
				if ($(value).children().length > 0) {
					strRoot = $(value).attr('name');
					self.getList($(value).children(),function(callback) {
						strCollection = callback;
					});
					strItems += '<li role=treeItem class=parent_li>' + '<span class="badge badge-success" title="Collapse this branch">'+
								'<i class="icon-plus-sign"></i>' + '<a version="'+ strRoot +'">' + strRoot + '</a></span>' + strCollection +'</li>';
				} else {
					var moduleName = $(value).attr('moduleName');
					var appDirName = $(value).attr('appDirName');
					var nature = $(value).attr('nature');
					var url = $(value).attr('url');
					strItems += '<li role=treeItem class="parent_li hideContent">' + '<span class="badge badge-warning" title="Expand this branch">'+ '<i></i><a ';
					if (moduleName !== undefined) {
						strItems += 'moduleName="'+moduleName+'"';
					}
					if (appDirName !== undefined) {
						strItems += 'appDirName="'+appDirName+'"';
					}
					if (nature !== undefined) {
						strItems += 'nature="'+nature+'"';
					}
					if (url !== undefined) {
						strItems += 'url="'+url+'"';
					}
					
					strItems += '>' + $(value).attr('name') + '</a></span></li>';
				}
			});
			
			strUl = '<ul role=group >' + strItems + '</ul>';
			callback(strUl); 
		},
		
		createBranch : function() {
			var self = this;
			if (!self.validateCreateBranch()) {
				commonVariables.hideloading = true;
				self.showBtnLoading("#createBranch");
				var requestBody = {};
				requestBody.url = $("input[name=baseRepoUrl]").val();
				requestBody.version = $("#createBranchVersion").val();
				requestBody.username = $("#branchUsername").val();
				requestBody.password = $("#branchPassword").val();
				requestBody.comment = $("#branchComment").val();
				requestBody.currentbranchname = $("#branchFromName").val();
				requestBody.branchname = $("#newBranchName").val();
				requestBody.downloadoption = $("#downloadBrToWorkspace").is(":checked");
				self.repository(self.getActionHeader(requestBody, "createBranch"), function(response) {
					commonVariables.hideloading = false;
					commonVariables.navListener.onMytabEvent(commonVariables.sourceRepo);
				});
			}
		},
		
		validateCreateBranch : function() {
			var self = this;
			var branchName = $("#newBranchName").val();
			var version = $("#createBranchVersion").val();
			var username = $("#branchUsername").val();
			var password = $("#branchPassword").val();
			if (self.isBlank(branchName)) {
				commonVariables.navListener.validateTextBox( $("#newBranchName"), 'Enter branch name');
				return true;
			}
			if (self.isBlank(version)) {
				commonVariables.navListener.validateTextBox( $("#createBranchVersion"), 'Enter version');
				return true;
			}
			if (self.isBlank(username)) {
				commonVariables.navListener.validateTextBox( $("#branchUsername"), 'Enter Username');
				return true;
			}
			if (self.isBlank(password)) {
				commonVariables.navListener.validateTextBox( $("#branchPassword"), 'Enter Password');
				return true;
			}
			return false;
		},
		
		createTag : function() {
			var self = this;
			if (!self.validateCreateTag()) {
				commonVariables.hideloading = true;
				self.showBtnLoading("#createTag");
				var requestBody = {};
				requestBody.url = $("input[name=baseRepoUrl]").val();
				requestBody.username = $("#tagUsername").val();
				requestBody.password = $("#tagPassword").val();
				requestBody.comment = $("#tagComment").val();
				requestBody.currentbranchname = $("#tagFromName").val();
				requestBody.version = $("#tagFromVersion").val();
				requestBody.tagname = $("#tagName").val();
				requestBody.downloadoption = $("#downloadTagToWorkspace").is(":checked");
				self.repository(self.getActionHeader(requestBody, "createTag"), function(response) {
					commonVariables.hideloading = false;
					commonVariables.navListener.onMytabEvent(commonVariables.sourceRepo);
				});
			}
		},
		
		validateCreateTag : function() {
			var self = this;
			var tagName = $("#tagName").val();
			var username = $("#tagUsername").val();
			var password = $("#tagPassword").val();
			if (self.isBlank(tagName)) {
				commonVariables.navListener.validateTextBox( $("#tagName"), 'Enter Tag name');
				return true;
			}
			if (self.isBlank(username)) {
				commonVariables.navListener.validateTextBox( $("#tagUsername"), 'Enter Username');
				return true;
			}
			if (self.isBlank(password)) {
				commonVariables.navListener.validateTextBox( $("#tagPassword"), 'Enter Password');
				return true;
			}
			return false;
		},
		
		release : function() {
			var self = this;
			if (!self.validateReleaseData()) {
				commonVariables.hideloading = true;
				self.showBtnLoading("#release");
				var requestBody = {};
				requestBody.releaseVersion = $("#releaseVersion").val();
				requestBody.developmentVersion = $("#devVersion").val();
				requestBody.tag = $("#releaseTagName").val();
				requestBody.username = $("#releaseUsername").val();
				requestBody.password = $("#releasePassword").val();
				requestBody.comment = $("#releaseComment").val();
				requestBody.environmentName = $("#releaseEnv").val();
				requestBody.deploy = $("#releaseDeploy").is(":checked");
				requestBody.branchName = $("input[name=selectedBranchName]").val();
				requestBody.appDirName = $('input[name=selectedAppDirName]').val();
				self.repository(self.getActionHeader(requestBody, "release"), function(response) {
					commonVariables.hideloading = false;
					commonVariables.navListener.onMytabEvent(commonVariables.sourceRepo);
				});
			}
		},
		
		validateReleaseData : function() {
			var self = this;
			var releaseVersion = $("#releaseVersion").val();
			var tagName = $("#releaseTagName").val();
			var username = $("#releaseUsername").val();
			var password = $("#releasePassword").val();
			var devVersion = $("#devVersion").val();
			if (self.isBlank(releaseVersion)) {
				commonVariables.navListener.validateTextBox( $("#releaseVersion"), 'Enter Release Version');
				return true;
			}
			if (self.isBlank(tagName)) {
				commonVariables.navListener.validateTextBox( $("#releaseTagName"), 'Enter Tag name');
				return true;
			}
			if (self.isBlank(username)) {
				commonVariables.navListener.validateTextBox( $("#releaseUsername"), 'Enter Username');
				return true;
			}
			if (self.isBlank(password)) {
				commonVariables.navListener.validateTextBox( $("#releasePassword"), 'Enter Password');
				return true;
			}
			if (self.isBlank(devVersion)) {
				commonVariables.navListener.validateTextBox( $("#devVersion"), 'Enter Development Version');
				return true;
			}
			return false;
		},
	});

	return Clazz.com.components.repository.js.listener.RepositoryListener;
});