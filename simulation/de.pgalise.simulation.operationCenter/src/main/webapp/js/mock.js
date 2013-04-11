
/*
 * =====================================================
 * !!!!!!!!!!!!!!!!!!!! TEST !!!!!!!!!!!!!!!!!!!!!!!!!!!
 * =====================================================
 * these are mock/test functions
 */

var testOnConnectMessage = function() {
	ctrl.mainCtrl.applyOnConnectMessage({
		sensorTypeMap : {
			4 : 'GPS_CAR'
		},
		sensorUnitMap : {
			12 : 'KWh',
			0 : 'Anne'
		}
	});
};

var testSimulationInitMessage = function() {
	if (testCounter < 0) {
		ctrl.mainCtrl.applySimulationInitMessage({
			startTimestamp : new Date().getTime() - 3600000 * 24,
			endTimestamp : new Date().getTime(),
			interval : 500,
			clockGeneratorInterval : 500
		});
	}
};

var testSimulationStartMessage = function() {
	if (testCounter < 0) {
		ctrl.mainCtrl.applySimulationStartMessage({
			cityName : 'Oldenburg',
			population : 162154,
			cityBoundary : {
				northEast : {
					latitude : {
						degree : 53.210967
					},
					longitude : {
						degree : 8.10379
					}
				},
				southWest : {
					latitude : {
						degree : 53.079178
					},
					longitude : {
						degree : 8.352356
					}
				}
			}
		});
	}
};

var testDynamicSensors = function() {
	if (testCounter == 1) {
		var startTime = new Date().getTime();
		ctrl.mainCtrl.applyNewVehiclesMessage([{
			"sensors" : [{
				"sensorType" : 4,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 0.0
						},
						"longitude" : {
							"degree" : 0.0
						}
					},
					"sensorID" : 101,
					"sensorType" : "GPS_CAR",
					"updateSteps" : 1
				}
			}],
			"vehicleID" : "00000000-0000-0000-0000-000000000000",
			"vehicleTypeID" : 0,
			"vehicleModelID" : 0
		}, {
			"sensors" : [{
				"sensorType" : 19,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 0.0
						},
						"longitude" : {
							"degree" : 0.0
						}
					},
					"sensorID" : 102,
					"sensorType" : "GPS_TRUCK",
					"updateSteps" : 1
				}
			}],
			"vehicleID" : "00000000-0000-0000-0000-000000000001",
			"vehicleTypeID" : 3,
			"vehicleModelID" : 0
		}, {
			"sensors" : [{
				"sensorType" : 20,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 0.0
						},
						"longitude" : {
							"degree" : 0.0
						}
					},
					"sensorID" : 103,
					"sensorType" : "GPS_MOTORCYCLE",
					"updateSteps" : 1
				}
			}],
			"vehicleID" : "00000000-0000-0000-0000-000000000002",
			"vehicleTypeID" : 1,
			"vehicleModelID" : 0
		}, {
			"sensors" : [{
				"sensorType" : 3,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 0.0
						},
						"longitude" : {
							"degree" : 0.0
						}
					},
					"sensorID" : 104,
					"sensorType" : "GPS_BUS",
					"updateSteps" : 1
				}
			},{
				"sensorType" : 7,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 53.0927932811735 + Math.random() / 10
						},
						"longitude" : {
							"degree" : 8.14169802992515 + Math.random() / 7
						}
					},
					"sensorID" : 634543,
					"sensorType" : "INFRARED",
					"updateSteps" : 1
				}
			}],
			"vehicleID" : "00000000-0000-0000-0000-000000000003",
			"vehicleTypeID" : 4,
			"vehicleModelID" : 0
		}, {
			"sensors" : [{
				"sensorType" : 2,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 0.0
						},
						"longitude" : {
							"degree" : 0.0
						}
					},
					"sensorID" : 105,
					"sensorType" : "GPS_BIKE",
					"updateSteps" : 1
				}
			}],
			"vehicleID" : "00000000-0000-0000-0000-000000000004",
			"vehicleTypeID" : 2,
			"vehicleModelID" : 0
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);
	} else if(testCounter > 1) {
		var startTime = new Date().getTime();
		ctrl.mainCtrl.applySensorDataMessage([{
			"lat" : 53.0927932811735 + Math.random() / 10,
			"lng" : 8.14169802992515 + Math.random() / 7,
			"type" : 4,
			"id" : 101,
			"speedInKmh" : Math.random(),
			"avgSpeedInKmh" : Math.random(),
			"distanceInMperStep" : Math.random(),
			"totalDistanceInM" : Math.random(),
			"directionInGrad" : Math.random(),
			"travelTimeInMs" : Math.random()
		}, {
			"lat" : 53.0927932811735 + Math.random() / 10,
			"lng" : 8.14169802992515 + Math.random() / 7,
			"type" : 19,
			"id" : 102,
			"speedInKmh" : Math.random(),
			"avgSpeedInKmh" : Math.random(),
			"distanceInMperStep" : Math.random(),
			"totalDistanceInM" : Math.random(),
			"directionInGrad" : Math.random(),
			"travelTimeInMs" : Math.random()
		}, {
			"lat" : 53.0927932811735 + Math.random() / 10,
			"lng" : 8.14169802992515 + Math.random() / 7,
			"type" : 20,
			"id" : 103,
			"speedInKmh" : Math.random(),
			"avgSpeedInKmh" : Math.random(),
			"distanceInMperStep" : Math.random(),
			"totalDistanceInM" : Math.random(),
			"directionInGrad" : Math.random(),
			"travelTimeInMs" : Math.random()
		}, {
			"lat" : 53.0927932811735 + Math.random() / 10,
			"lng" : 8.14169802992515 + Math.random() / 7,
			"type" : 3,
			"id" : 104,
			"speedInKmh" : Math.random(),
			"avgSpeedInKmh" : Math.random(),
			"distanceInMperStep" : Math.random(),
			"totalDistanceInM" : Math.random(),
			"directionInGrad" : Math.random(),
			"travelTimeInMs" : Math.random()
		}, {
			"lat" : 53.0927932811735 + Math.random() / 10,
			"lng" : 8.14169802992515 + Math.random() / 7,
			"type" : 2,
			"id" : 105,
			"speedInKmh" : Math.random(),
			"avgSpeedInKmh" : Math.random(),
			"distanceInMperStep" : Math.random(),
			"totalDistanceInM" : Math.random(),
			"directionInGrad" : Math.random(),
			"travelTimeInMs" : Math.random()
		}, {
			"value" : Math.random(),
			"type" : 7,
			"id" : 634543,
			"speedInKmh" : Math.random(),
			"avgSpeedInKmh" : Math.random(),
			"distanceInMperStep" : Math.random(),
			"totalDistanceInM" : Math.random(),
			"directionInGrad" : Math.random(),
			"travelTimeInMs" : Math.random()
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);
	}
};

var map = {};
var testStaticSensors = function() {
	if (testCounter == 1) {
		var startTime = new Date().getTime();

		map[0] = 6;
		map[1] = 18;
		map[2] = 9;
		map[3] = 12;
		map[4] = 16;
		map[5] = 0;
		map[6] = 1;
		map[7] = 5;
		map[8] = 8;
		map[9] = 10;
		map[10] = 11;
		map[11] = 13;
		map[12] = 15;
		ctrl.mainCtrl.applyNewSensorsMessage([{
			"sensorType" : 6,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"sensorID" : 0,
				"sensorType" : "INDUCTIONLOOP",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 18,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"sensorID" : 1,
				"sensorType" : "TOPORADAR",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 9,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"sensorID" : 2,
				"sensorType" : "PHOTOVOLTAIK",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 12,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"sensorID" : 3,
				"sensorType" : "SMARTMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 16,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"sensorID" : 4,
				"sensorType" : "WINDPOWERSENSOR",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 0,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 5,
				"sensorType" : "ANEMOMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 1,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 6,
				"sensorType" : "BAROMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 5,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 7,
				"sensorType" : "HYGROMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 8,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 8,
				"sensorType" : "LUXMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 10,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 9,
				"sensorType" : "PYRANOMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 11,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 10,
				"sensorType" : "RAIN",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 13,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 11,
				"sensorType" : "THERMOMETER",
				"updateSteps" : 1
			}
		}, {
			"sensorType" : 15,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.0927932811735 + Math.random() / 10
					},
					"longitude" : {
						"degree" : 8.14169802992515 + Math.random() / 7
					}
				},
				"weatherStationID" : 1000,
				"sensorID" : 12,
				"sensorType" : "WINDFLAG",
				"updateSteps" : 1
			}
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);
	} else if(testCounter > 1) {
		var startTime = new Date().getTime();
		var data = [];
		for ( var i = 0; i <= 12; i++) {
			data.push({
				"value" : Math.random(),
				"type" : map[i],
				"id" : i
			});
		}
		ctrl.mainCtrl.applySensorDataMessage(data);
	}
};

var MIN_ID = 1000, MAX_ID = 5000;
var loadTest = function() {
	if (testCounter == 1) {
		var startTime = new Date().getTime();
		var data = [];
		for ( var i = MIN_ID; i < MAX_ID; i++) {
			data.push({
				"sensorType" : 4,
				"sensorHelper" : {
					"position" : {
						"latitude" : {
							"degree" : 0.0
						},
						"longitude" : {
							"degree" : 0.0
						}
					},
					"sensorID" : i,
					"sensorType" : "GPS_CAR",
					"updateSteps" : 1
				}
			});
		}
		ctrl.mainCtrl.applyNewSensorsMessage(data);
		ctrl.mainCtrl.applyNewSensorsMessage([{
			"sensorType" : 19,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 0.0
					},
					"longitude" : {
						"degree" : 0.0
					}
				},
				"sensorID" : 999,
				"sensorType" : "GPS_CAR",
				"updateSteps" : 1
			}
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);
	} else if(testCounter > 1) {
		var startTime = new Date().getTime();
		var data = [];
		for ( var i = MIN_ID; i < MAX_ID; i++) {
			data.push({
				"lat" : 53.0927932811735 + Math.random() / 10,
				"lng" : 8.14169802992515 + Math.random() / 7,
				"type" : 4,
				"id" : i
			});
		}

		ctrl.mainCtrl.applySensorDataMessage(data);
		ctrl.mainCtrl.applySensorDataMessage([{
			"lat" : 53.0927932811735 + Math.random() / 10,
			"lng" : 8.14169802992515 + Math.random() / 7,
			"type" : 19,
			"id" : 999
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);

		if (testCounter > 5) {
			var rand1 = parseInt(MIN_ID + (Math.random() * (MAX_ID - MIN_ID)));
			var rand2 = parseInt(MIN_ID + (Math.random() * (MAX_ID - MIN_ID)));
			var rand3 = parseInt(MIN_ID + (Math.random() * (MAX_ID - MIN_ID)));
			var rand4 = parseInt(MIN_ID + (Math.random() * (MAX_ID - MIN_ID)));
			var rand5 = parseInt(MIN_ID + (Math.random() * (MAX_ID - MIN_ID)));
			ctrl.mainCtrl.applyGPSSensorTimeoutMessage({
				id : rand1
			});
			ctrl.mainCtrl.applyGPSSensorTimeoutMessage({
				id : rand2
			});
			ctrl.mainCtrl.applyGPSSensorTimeoutMessage({
				id : rand3
			});
			ctrl.mainCtrl.applyGPSSensorTimeoutMessage({
				id : rand4
			});
			ctrl.mainCtrl.applyGPSSensorTimeoutMessage({
				id : rand5
			});
		}
	}
};

var trafficLightTest = function() {
	if (testCounter == 1) {
		var startTime = new Date().getTime();
		ctrl.mainCtrl.applyNewSensorsMessage([{
			"sensorType" : 21,
			"sensorHelper" : {
				"position" : {
					"latitude" : {
						"degree" : 53.09
					},
					"longitude" : {
						"degree" : 8.14
					}
				},
				"sensorID" : 12344,
				"sensorType" : "TRAFFICLIGHT",
				"updateSteps" : 1,
				"trafficLightIds" : [12345,12346]
			}
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);
	} else if(testCounter > 1) {
		var startTime = new Date().getTime();
		ctrl.mainCtrl.applySensorDataMessage([{
			"value" : Math.random() * 4,
			"angle1" : 0 + new Date().getTime(),
			"angle2" : 180 + new Date().getTime(),
			"type" : 14,
			"id" : 12345
		}, {
			"value" : parseInt(Math.random() * 4),
			"angle1" : 90 + new Date().getTime(),
			"angle2" : 275 + new Date().getTime(),
			"type" : 14,
			"id" : 12346
		}]);

		var endTime = new Date().getTime();
		//console.log(endTime - startTime);
	}
};

var serverHeatMapTest = function() {
	var startTime = new Date().getTime();
	ctrl.mainCtrl.applyTestServerHeatmapData({
		max : 1,
		data : [{
			lonlat : {
				latitude : {
					degree : 53
				},
				longitude : {
					degree : 8
				}
			},
			count : 1
		}, {
			lonlat : {
				latitude : {
					degree : 53.5
				},
				longitude : {
					degree : 8.5
				}
			},
			count : 1
		}]
	});
	var endTime = new Date().getTime();
	//console.log(endTime - startTime);

};

var testSomeMessage = function() {
	if(testCounter == 1) {
		ctrl.mainCtrl.receive('{"messageType":24,"messageID":0,"content":[{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":74034913,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1017749431,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"52b15d6a-48d4-4689-a097-83102c9ac00b","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":611520830,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1725317743,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"23200be0-27e7-4a03-9a48-cb62e49c2b49","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":2133084165,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":63354030,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"97ca841a-4753-4e56-bbb6-c266ec53eea6","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1685711309,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":844075368,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"b42357e3-cf25-41bd-bef3-ecbb264de24c","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":697863583,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1110699137,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"5ed75c9c-a795-4903-98a4-923218a48c9c","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1263310767,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":503465835,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"ef9e0e1e-ed40-4af0-93cd-353ea4af3744","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1797264482,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1924761104,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"cccfd6b6-b51a-4385-9969-e7e9b9be74ef","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1769445906,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1559836496,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"c15f7243-fe2d-4878-be4b-d653ba73a582","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":2026194058,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1066911724,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"3d686d43-f8f9-4684-b13f-e3e1986b5112","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1772880168,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1254678238,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"03dc6677-1dd4-4da1-a3a3-4b15f0d6cc8c","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":265661548,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1142999343,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"fe5d18ca-7ebc-4228-8f36-c21dfabd9961","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":42027703,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1184146311,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"b5cc7773-e170-4eed-995b-c1a2a84cd44f","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":808714700,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1487030040,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"c1e4e7ca-390e-49b5-be41-35a871d1a1ff","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1090938818,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":284558738,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"4858c10f-6c59-485c-ae7b-bcdd07a2db8f","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1590618401,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":2055058191,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"becad5c3-a32f-43e6-aa52-76062a5e3e1c","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":674516583,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1960881092,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"a9cfa498-1342-4075-979d-42b9d75d5cfa","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":714580607,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":363685068,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"1af7b4db-d9d9-4fb5-8eae-19222146d610","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1250996699,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1834799162,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"8c24aca3-decb-4b2c-8788-039628dbbde3","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1021161183,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":700222959,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"1365ce69-fa55-44f1-8914-90c88fac2d17","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":557381344,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1929667736,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"ae048dbf-15ac-4dae-a356-b4ba8e19748a","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":352807232,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1869831791,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"86f55a32-bb50-4a2e-b852-365b0e8bb569","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":800287773,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":817766521,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"54792e9b-247f-430c-aa6f-57877a205fc8","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":692368601,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":820000733,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"14c7615b-9fb9-488e-bdcc-0c5dcefd8fd7","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":491102827,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":2021174326,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"cc971209-27ed-42e1-a7d1-c9b5c0e0a89b","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":66496623,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":818554334,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"9647a315-d902-411b-80dc-df1440b1b413","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":49639098,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1204265717,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"00929f36-b6a3-4d41-bc5b-8d336279414a","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":540994005,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":815958926,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"a8da8a16-e9da-427f-94ed-a16173f154ed","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":775934788,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":956444407,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"df0879a3-c6db-43da-8925-2fc79b2e3f3c","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":731444744,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":358807206,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"b0a57b68-44ca-45ad-b2f9-19c3409490cd","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":431362719,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1850715658,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"05af7e0d-620b-444e-a829-e71885f75ce3","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":695887726,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":942986114,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"3a4d6152-b43f-40e8-b081-d314e2d40757","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1007467184,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":516471844,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"a5528412-d174-4aa2-adde-32dd2243184a","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":316086245,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":741642570,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"1f234379-245e-428c-9961-218f817de9a7","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1581918715,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1990874327,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"abe6feae-5d2e-4600-887d-36888349d569","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":841643763,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1726099391,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"547b725c-3ab0-4191-b3c4-77ebd0e87280","vehicleTypeID":4,"vehicleModelID":5},{"sensors":[{"sensorType":3,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":1679557713,"sensorType":"GPS_BUS","updateSteps":1,"sensorInterfererType":[],"nodeId":""}},{"sensorType":7,"sensorHelper":{"position":{"latitude":{"degree":0.0},"longitude":{"degree":0.0}},"sensorID":107222666,"sensorType":"INFRARED","updateSteps":1,"sensorInterfererType":[],"nodeId":""}}],"vehicleID":"c4c604a4-5107-4582-b376-404c2770d43f","vehicleTypeID":4,"vehicleModelID":5}]}');
	}
};

// mock
var testCounter = -2;
var testMethod = function() {
	if(!mock) return;
	
	testCounter++;
	testOnConnectMessage();

	testSimulationInitMessage();

	testSimulationStartMessage();

	trafficLightTest();

	// serverHeatMapTest();

	testDynamicSensors();

	testStaticSensors();

	// loadTest();
	//testSomeMessage();
};

if (demo) {
	window.setInterval(function() {
		testMethod();
	}, 1000);
};