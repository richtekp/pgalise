/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.model;

import java.beans.*;

/**
 *
 * @author richter
 */
public class ControlCenterStartParameterBeanInfo extends SimpleBeanInfo {

    // Bean descriptor//GEN-FIRST:BeanDescriptor
    /*lazy BeanDescriptor*/
    private static BeanDescriptor getBdescriptor(){
        BeanDescriptor beanDescriptor = new BeanDescriptor  ( de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class , null ); // NOI18N//GEN-HEADEREND:BeanDescriptor
    // Here you can add code for customizing the BeanDescriptor.

        return beanDescriptor;     }//GEN-LAST:BeanDescriptor


    // Property identifiers//GEN-FIRST:Properties
    private static final int PROPERTY_aggregatedWeatherDataEnabled = 0;
    private static final int PROPERTY_attractionCollection = 1;
    private static final int PROPERTY_busRoutes = 2;
    private static final int PROPERTY_city = 3;
    private static final int PROPERTY_clockGeneratorInterval = 4;
    private static final int PROPERTY_controlCenterAddress = 5;
    private static final int PROPERTY_endTimestamp = 6;
    private static final int PROPERTY_importedInstanceFileContent = 7;
    private static final int PROPERTY_interval = 8;
    private static final int PROPERTY_ipEnergyController = 9;
    private static final int PROPERTY_ipSimulationController = 10;
    private static final int PROPERTY_ipStaticSensorController = 11;
    private static final int PROPERTY_ipTrafficController = 12;
    private static final int PROPERTY_ipWeatherController = 13;
    private static final int PROPERTY_name = 14;
    private static final int PROPERTY_operationCenterAddress = 15;
    private static final int PROPERTY_oSMAndBusstopFileData = 16;
    private static final int PROPERTY_randomDynamicSensorBundle = 17;
    private static final int PROPERTY_sensorHelperList = 18;
    private static final int PROPERTY_sensorUpdateSteps = 19;
    private static final int PROPERTY_simulationEventLists = 20;
    private static final int PROPERTY_specificUpdateSteps = 21;
    private static final int PROPERTY_startParameterOriginEnum = 22;
    private static final int PROPERTY_startTimestamp = 23;
    private static final int PROPERTY_trafficFuzzyData = 24;
    private static final int PROPERTY_trafficServerIPs = 25;
    private static final int PROPERTY_weatherEventList = 26;
    private static final int PROPERTY_withSensorInterferes = 27;

    // Property array 
    /*lazy PropertyDescriptor*/
    private static PropertyDescriptor[] getPdescriptor(){
        PropertyDescriptor[] properties = new PropertyDescriptor[28];
    
        try {
            properties[PROPERTY_aggregatedWeatherDataEnabled] = new PropertyDescriptor ( "aggregatedWeatherDataEnabled", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "isAggregatedWeatherDataEnabled", "setAggregatedWeatherDataEnabled" ); // NOI18N
            properties[PROPERTY_attractionCollection] = new PropertyDescriptor ( "attractionCollection", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getAttractionCollection", "setAttractionCollection" ); // NOI18N
            properties[PROPERTY_busRoutes] = new PropertyDescriptor ( "busRoutes", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getBusRoutes", "setBusRoutes" ); // NOI18N
            properties[PROPERTY_city] = new PropertyDescriptor ( "city", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getCity", "setCity" ); // NOI18N
            properties[PROPERTY_clockGeneratorInterval] = new PropertyDescriptor ( "clockGeneratorInterval", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getClockGeneratorInterval", "setClockGeneratorInterval" ); // NOI18N
            properties[PROPERTY_controlCenterAddress] = new PropertyDescriptor ( "controlCenterAddress", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getControlCenterAddress", "setControlCenterAddress" ); // NOI18N
            properties[PROPERTY_endTimestamp] = new PropertyDescriptor ( "endTimestamp", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getEndTimestamp", "setEndTimestamp" ); // NOI18N
            properties[PROPERTY_importedInstanceFileContent] = new PropertyDescriptor ( "importedInstanceFileContent", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getImportedInstanceFileContent", "setImportedInstanceFileContent" ); // NOI18N
            properties[PROPERTY_interval] = new PropertyDescriptor ( "interval", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getInterval", "setInterval" ); // NOI18N
            properties[PROPERTY_ipEnergyController] = new PropertyDescriptor ( "ipEnergyController", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getIpEnergyController", "setIpEnergyController" ); // NOI18N
            properties[PROPERTY_ipSimulationController] = new PropertyDescriptor ( "ipSimulationController", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getIpSimulationController", "setIpSimulationController" ); // NOI18N
            properties[PROPERTY_ipStaticSensorController] = new PropertyDescriptor ( "ipStaticSensorController", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getIpStaticSensorController", "setIpStaticSensorController" ); // NOI18N
            properties[PROPERTY_ipTrafficController] = new PropertyDescriptor ( "ipTrafficController", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getIpTrafficController", "setIpTrafficController" ); // NOI18N
            properties[PROPERTY_ipWeatherController] = new PropertyDescriptor ( "ipWeatherController", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getIpWeatherController", "setIpWeatherController" ); // NOI18N
            properties[PROPERTY_name] = new PropertyDescriptor ( "name", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getName", "setName" ); // NOI18N
            properties[PROPERTY_operationCenterAddress] = new PropertyDescriptor ( "operationCenterAddress", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getOperationCenterAddress", "setOperationCenterAddress" ); // NOI18N
            properties[PROPERTY_oSMAndBusstopFileData] = new PropertyDescriptor ( "oSMAndBusstopFileData", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getoSMAndBusstopFileData", "setoSMAndBusstopFileData" ); // NOI18N
            properties[PROPERTY_randomDynamicSensorBundle] = new PropertyDescriptor ( "randomDynamicSensorBundle", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getRandomDynamicSensorBundle", "setRandomDynamicSensorBundle" ); // NOI18N
            properties[PROPERTY_sensorHelperList] = new PropertyDescriptor ( "sensorHelperList", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getSensorHelperList", "setSensorHelperList" ); // NOI18N
            properties[PROPERTY_sensorUpdateSteps] = new PropertyDescriptor ( "sensorUpdateSteps", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getSensorUpdateSteps", "setSensorUpdateSteps" ); // NOI18N
            properties[PROPERTY_simulationEventLists] = new PropertyDescriptor ( "simulationEventLists", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getSimulationEventLists", "setSimulationEventLists" ); // NOI18N
            properties[PROPERTY_specificUpdateSteps] = new PropertyDescriptor ( "specificUpdateSteps", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getSpecificUpdateSteps", "setSpecificUpdateSteps" ); // NOI18N
            properties[PROPERTY_startParameterOriginEnum] = new PropertyDescriptor ( "startParameterOriginEnum", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getStartParameterOriginEnum", "setStartParameterOriginEnum" ); // NOI18N
            properties[PROPERTY_startTimestamp] = new PropertyDescriptor ( "startTimestamp", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getStartTimestamp", "setStartTimestamp" ); // NOI18N
            properties[PROPERTY_trafficFuzzyData] = new PropertyDescriptor ( "trafficFuzzyData", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getTrafficFuzzyData", "setTrafficFuzzyData" ); // NOI18N
            properties[PROPERTY_trafficServerIPs] = new PropertyDescriptor ( "trafficServerIPs", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getTrafficServerIPs", "setTrafficServerIPs" ); // NOI18N
            properties[PROPERTY_weatherEventList] = new PropertyDescriptor ( "weatherEventList", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "getWeatherEventList", "setWeatherEventList" ); // NOI18N
            properties[PROPERTY_withSensorInterferes] = new PropertyDescriptor ( "withSensorInterferes", de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "isWithSensorInterferes", "setWithSensorInterferes" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Properties
    // Here you can add code for customizing the properties array.

        return properties;     }//GEN-LAST:Properties

    // EventSet identifiers//GEN-FIRST:Events
    private static final int EVENT_propertyChangeListener = 0;

    // EventSet array
    /*lazy EventSetDescriptor*/
    private static EventSetDescriptor[] getEdescriptor(){
        EventSetDescriptor[] eventSets = new EventSetDescriptor[1];
    
        try {
            eventSets[EVENT_propertyChangeListener] = new EventSetDescriptor ( de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class, "propertyChangeListener", java.beans.PropertyChangeListener.class, new String[] {"propertyChange"}, "addPropertyChangeListener", "removePropertyChangeListener" ); // NOI18N
        }
        catch(IntrospectionException e) {
            e.printStackTrace();
        }//GEN-HEADEREND:Events
    // Here you can add code for customizing the event sets array.

        return eventSets;     }//GEN-LAST:Events

    // Method identifiers//GEN-FIRST:Methods
    private static final int METHOD_overwriteWithImportedInstanceProperties0 = 0;

    // Method array 
    /*lazy MethodDescriptor*/
    private static MethodDescriptor[] getMdescriptor(){
        MethodDescriptor[] methods = new MethodDescriptor[1];
    
        try {
            methods[METHOD_overwriteWithImportedInstanceProperties0] = new MethodDescriptor(de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter.class.getMethod("overwriteWithImportedInstanceProperties", new Class[] {})); // NOI18N
            methods[METHOD_overwriteWithImportedInstanceProperties0].setDisplayName ( "" );
        }
        catch( Exception e) {}//GEN-HEADEREND:Methods
    // Here you can add code for customizing the methods array.

        return methods;     }//GEN-LAST:Methods

    private static java.awt.Image iconColor16 = null;//GEN-BEGIN:IconsDef
    private static java.awt.Image iconColor32 = null;
    private static java.awt.Image iconMono16 = null;
    private static java.awt.Image iconMono32 = null;//GEN-END:IconsDef
    private static String iconNameC16 = null;//GEN-BEGIN:Icons
    private static String iconNameC32 = null;
    private static String iconNameM16 = null;
    private static String iconNameM32 = null;//GEN-END:Icons

    private static final int defaultPropertyIndex = -1;//GEN-BEGIN:Idx
    private static final int defaultEventIndex = -1;//GEN-END:Idx


//GEN-FIRST:Superclass
    // Here you can add code for customizing the Superclass BeanInfo.

//GEN-LAST:Superclass
	/**
	 * Gets the bean's <code>BeanDescriptor</code>s.
	 *
	 * @return BeanDescriptor describing the editable properties of this bean. May
	 * return null if the information should be obtained by automatic analysis.
	 */
	@Override
	public BeanDescriptor getBeanDescriptor() {
		return getBdescriptor();
	}

	/**
	 * Gets the bean's <code>PropertyDescriptor</code>s.
	 *
	 * @return An array of PropertyDescriptors describing the editable properties
	 * supported by this bean. May return null if the information should be
	 * obtained by automatic analysis.
	 * <p>
	 * If a property is indexed, then its entry in the result array will belong to
	 * the IndexedPropertyDescriptor subclass of PropertyDescriptor. A client of
	 * getPropertyDescriptors can use "instanceof" to check if a given
	 * PropertyDescriptor is an IndexedPropertyDescriptor.
	 */
	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return getPdescriptor();
	}

	/**
	 * Gets the bean's <code>EventSetDescriptor</code>s.
	 *
	 * @return An array of EventSetDescriptors describing the kinds of events
	 * fired by this bean. May return null if the information should be obtained
	 * by automatic analysis.
	 */
	@Override
	public EventSetDescriptor[] getEventSetDescriptors() {
		return getEdescriptor();
	}

	/**
	 * Gets the bean's <code>MethodDescriptor</code>s.
	 *
	 * @return An array of MethodDescriptors describing the methods implemented by
	 * this bean. May return null if the information should be obtained by
	 * automatic analysis.
	 */
	@Override
	public MethodDescriptor[] getMethodDescriptors() {
		return getMdescriptor();
	}

	/**
	 * A bean may have a "default" property that is the property that will mostly
	 * commonly be initially chosen for update by human's who are customizing the
	 * bean.
	 *
	 * @return Index of default property in the PropertyDescriptor array returned
	 * by getPropertyDescriptors.
	 * <P>
	 * Returns -1 if there is no default property.
	 */
	@Override
	public int getDefaultPropertyIndex() {
		return defaultPropertyIndex;
	}

	/**
	 * A bean may have a "default" event that is the event that will mostly
	 * commonly be used by human's when using the bean.
	 *
	 * @return Index of default event in the EventSetDescriptor array returned by
	 * getEventSetDescriptors.
	 * <P>
	 * Returns -1 if there is no default event.
	 */
	@Override
	public int getDefaultEventIndex() {
		return defaultEventIndex;
	}

	/**
	 * This method returns an image object that can be used to represent the bean
	 * in toolboxes, toolbars, etc. Icon images will typically be GIFs, but may in
	 * future include other formats.
	 * <p>
	 * Beans aren't required to provide icons and may return null from this
	 * method.
	 * <p>
	 * There are four possible flavors of icons (16x16 color, 32x32 color, 16x16
	 * mono, 32x32 mono). If a bean choses to only support a single icon we
	 * recommend supporting 16x16 color.
	 * <p>
	 * We recommend that icons have a "transparent" background so they can be
	 * rendered onto an existing background.
	 *
	 * @param iconKind The kind of icon requested. This should be one of the
	 * constant values ICON_COLOR_16x16, ICON_COLOR_32x32, ICON_MONO_16x16, or
	 * ICON_MONO_32x32.
	 * @return An image object representing the requested icon. May return null if
	 * no suitable icon is available.
	 */
	@Override
	public java.awt.Image getIcon(int iconKind) {
		switch (iconKind) {
			case ICON_COLOR_16x16:
				if (iconNameC16 == null) {
					return null;
				} else {
					if (iconColor16 == null) {
						iconColor16 = loadImage(iconNameC16);
					}
					return iconColor16;
				}
			case ICON_COLOR_32x32:
				if (iconNameC32 == null) {
					return null;
				} else {
					if (iconColor32 == null) {
						iconColor32 = loadImage(iconNameC32);
					}
					return iconColor32;
				}
			case ICON_MONO_16x16:
				if (iconNameM16 == null) {
					return null;
				} else {
					if (iconMono16 == null) {
						iconMono16 = loadImage(iconNameM16);
					}
					return iconMono16;
				}
			case ICON_MONO_32x32:
				if (iconNameM32 == null) {
					return null;
				} else {
					if (iconMono32 == null) {
						iconMono32 = loadImage(iconNameM32);
					}
					return iconMono32;
				}
			default:
				return null;
		}
	}
	
}
