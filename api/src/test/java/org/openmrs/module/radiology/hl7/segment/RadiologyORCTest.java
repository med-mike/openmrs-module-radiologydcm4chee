/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, 
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can 
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under 
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * 
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS 
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.hl7.segment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.util.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.Order;
import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.test.Verifies;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.message.ORM_O01;
import ca.uhn.hl7v2.model.v231.segment.ORC;
import ca.uhn.hl7v2.parser.EncodingCharacters;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * Tests {@link RadiologyORC}
 */
public class RadiologyORCTest {
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	private static final EncodingCharacters encodingCharacters = new EncodingCharacters('|', '^', '~', '\\', '&');
	
	/**
	 * @see {@link RadiologyORC#populateCommonOrder(ORC, RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should return populated common order segment given all params", method = "populateCommonOrder(ORC, RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)")
	public void populateCommonOrder_shouldReturnPopulatedCommonOrderSegmentGivenAllParams() throws Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		radiologyOrder.setOrderId(1);
		
		Field orderNumber = Order.class.getDeclaredField("orderNumber");
		orderNumber.setAccessible(true);
		orderNumber.set(radiologyOrder, "ORD-" + radiologyOrder.getOrderId());
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(2015, Calendar.FEBRUARY, 04);
		calendar.set(Calendar.HOUR_OF_DAY, 14);
		calendar.set(Calendar.MINUTE, 35);
		calendar.set(Calendar.SECOND, 00);
		radiologyOrder.setScheduledDate(calendar.getTime());
		radiologyOrder.setUrgency(Order.Urgency.ON_SCHEDULED_DATE);
		
		ORM_O01 message = new ORM_O01();
		RadiologyORC.populateCommonOrder(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC(),
		    radiologyOrder, CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
		
		ORC commonOrderSegment = message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC();
		assertThat(commonOrderSegment.getOrderControl().getValue(), is("NW"));
		assertThat(commonOrderSegment.getPlacerOrderNumber().getEntityIdentifier().getValue(), is("ORD-1"));
		assertThat(commonOrderSegment.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().getValue(),
		    is("20150204143500"));
		assertThat(commonOrderSegment.getQuantityTiming().getPriority().getValue(), is("S"));
		
		assertThat(PipeParser.encode(commonOrderSegment, encodingCharacters), is("ORC|NW|ORD-1|||||^^^20150204143500^^S"));
	}
	
	/**
	 * @see {@link RadiologyORC#populateCommonOrder(ORC, RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as common order segment", method = "populateCommonOrder(ORC, RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)")
	public void populateCommonOrder_shouldThrowIllegalArgumentExceptionGivenNullAsCommonOrderSegment() throws HL7Exception {
		
		RadiologyOrder radiologyOrder = new RadiologyOrder();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("commonOrderSegment cannot be null."));
		RadiologyORC.populateCommonOrder(null, radiologyOrder, CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
	}
	
	/**
	 * @see {@link RadiologyORC#populateCommonOrder(ORC, RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)}
	 */
	@Test
	@Verifies(value = "should throw illegal argument exception given null as radiology order", method = "populateCommonOrder(ORC, RadiologyOrder, CommonOrderOrderControl, CommonOrderPriority)")
	public void populateCommonOrder_shouldThrowIllegalArgumentExceptionGivenNullAsRadiologyOrder() throws HL7Exception {
		
		ORM_O01 message = new ORM_O01();
		
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(is("radiologyOrder cannot be null."));
		RadiologyORC.populateCommonOrder(message.getORCOBRRQDRQ1ODSODTRXONTEDG1RXRRXCNTEOBXNTECTIBLG().getORC(), null,
		    CommonOrderOrderControl.NEW_ORDER, CommonOrderPriority.STAT);
	}
}
