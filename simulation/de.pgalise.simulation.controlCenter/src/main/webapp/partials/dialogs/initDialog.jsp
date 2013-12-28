<%-- any content can be specified here e.g.: --%>
<%@page import="de.pgalise.simulation.controlCenter.InitDialogCtrlInitialTypeEnum"%>
<%@ page pageEncoding="UTF-8" %>

<ui:composition xmlns="http://www.w3.org/1999/xhtml"
								xmlns:ui="http://java.sun.com/jsf/facelets" 
								xmlns:h="http://xmlns.jcp.org/jsf/html"
								xmlns:ng="http://angularjs.org"
								xmlns:f="http://xmlns.jcp.org/jsf/core"
								xmlns:p="http://primefaces.org/ui" 
    xmlns:cc="http://java.sun.com/jsf/composite">
	<p:dialog id="initialDialog">
		<p:selectOneRadio id="customRadio" value="#{initDialogCtrl.initialTypeEnum}" layout="custom">  
        <f:selectItem itemLabel="<%= InitDialogCtrlInitialTypeEnum.CREATE.getStringValue() %>" itemValue="#{InitDialogCtrlInitialTypeEnum.CREATE}" />  
        <f:selectItem itemLabel="<%= InitDialogCtrlInitialTypeEnum.RECENT.getStringValue() %>" itemValue="#{InitDialogCtrlInitialTypeEnum.CREATE}" />  
        <f:selectItem itemLabel="<%= InitDialogCtrlInitialTypeEnum.IMPORT.getStringValue() %>" itemValue="#{InitDialogCtrlInitialTypeEnum.CREATE}" />  
    </p:selectOneRadio>  
		<div>
			<div class="block">
				<table>
					<tr class="head">
						<td colspan="2">
								<h:selectOneRadio value="#{mainCtrl.chosenInitialType}" label="create"/> <h3>Create new szenario</h3>
						</td>
					</tr>
					<tr>
						<td>City name</td>
						<td>
							<p:autoComplete value="#{startParameter.city.name}" completeMethod="#{initDialogCtrl.retrieveOSMAutocomplete}" styleClass="normalWidth"  forceSelection="true"/>
						</td>
					</tr>
					<tr>
						<td>Bus system</td>
						<td>
							<h:selectOneMenu class="normalWidth" value="#{startParameter.getoSMAndBusstopFileData().busStopFileName}" >
								<f:selectItem itemLabel="#{startParameter.getoSMAndBusstopFileData().busStopFileName}" itemValue="#{startParameter.getoSMAndBusstopFileData().busStopFileName}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<td>Street map</td>
						<td>
							<h:selectOneMenu class="normalWidth" value="#{startParameter.getoSMAndBusstopFileData().osmFileName}" disabled="#{initDialogCtrl.chosenInitialType eq 'create'}">
								<f:selectItem itemLabel="#{startParameter.getoSMAndBusstopFileData().osmFileName}" itemValue="#{startParameter.getoSMAndBusstopFileData().osmFileName}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				</table>
			</div>
			<div class="block">
				<table>
					<tr class="head">
						<td><h3>
								<p:radioButton id="opt1" for="customRadio" itemIndex="2"/>
								<h:selectOneRadio value="#{mainCtrl.chosenInitialType}" /> Load recently started scenario</h3>
						</td>
					</tr>
					<tr>
						<td>
							<b>Recent scenarios:</b>
							<div>
								<h:selectManyListbox >
									<f:selectItems value="#{initDialogCtrl.recentScenarios}"/>
								</h:selectManyListbox>
							</div>
						</td>
					</tr>
					<tr>
						<td>
							<div><b>Current:</b></div>
							<div>File name: #{initDialogCtrl.chosenRecentScenario.key}</div>
							<div>Date: #{initDialogCtrl.chosenRecentScenario.value.startTimestamp}</div>
						</td>
					</tr>
				</table>
			</div>
			<div class="block">
				<table>
					<tr class="head">
						<td><h3>
							<p:radioButton id="opt1" for="customRadio" itemIndex="2"/>  Import existing scenario</h3></td>
					</tr>
					<tr>
						<td>
							<b>Load XML:</b>
							<div>
								<input class="largeWidth" type="file" id="fileUpload" ng-disabled="chosenInitialType != 'import'" accept="text/xml" />â
							</div>
						</td>
					</tr>
				</table>
			</div>
		</div>
		<input type="button" class="fullWidth" value="Confirm" ng-click="confirmInitialDialog()" ng-disabled="chosenInitialType == 'confirmed'" />
	</p:dialog>
</ui:composition>