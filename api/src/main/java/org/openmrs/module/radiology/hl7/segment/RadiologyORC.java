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

import org.openmrs.module.radiology.RadiologyOrder;
import org.openmrs.module.radiology.hl7.CommonOrderOrderControl;
import org.openmrs.module.radiology.hl7.CommonOrderPriority;
import org.openmrs.module.radiology.utils.DateTimeUtils;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.segment.ORC;

public class RadiologyORC {
	
	/**
	 * Fill HL7 (version 2.3.1) Common Order Segment (ORC) with data from given OpenMRS Study and
	 * Order
	 * 
	 * @param commonOrderSegment Common Order Segment to populate
	 * @param radiologyOrder to map to commonOrderSegment segment
	 * @param commonOrderOrderControl Order Control element of Common Order (ORC)
	 * @param commonOrderPriority Priority component of Common Order (ORC) segment attribute
	 *            Quantity/Timing
	 * @return populated commonOrderSegment segment
	 * @throws DataTypeException
	 * @should return populated common order segment given all params
	 * @should throw illegal argument exception given null as common order segment
	 * @should throw illegal argument exception given null as radiology order
	 */
	public static ORC populateCommonOrder(ORC commonOrderSegment, RadiologyOrder radiologyOrder,
	        CommonOrderOrderControl commonOrderOrderControl, CommonOrderPriority commonOrderPriority)
	        throws DataTypeException {
		
		if (commonOrderSegment == null) {
			throw new IllegalArgumentException("commonOrderSegment cannot be null.");
		}
		
		if (radiologyOrder == null) {
			throw new IllegalArgumentException("radiologyOrder cannot be null.");
		}
		
		commonOrderSegment.getOrderControl().setValue(commonOrderOrderControl.getValue());
		commonOrderSegment.getPlacerOrderNumber().getEntityIdentifier().setValue(
		    radiologyOrder.getOrderNumber() == null ? "" : String.valueOf(radiologyOrder.getOrderNumber()));
		commonOrderSegment.getQuantityTiming().getStartDateTime().getTimeOfAnEvent().setValue(
		    DateTimeUtils.getPlainDateTimeFrom(radiologyOrder.getEffectiveStartDate()));
		commonOrderSegment.getQuantityTiming().getPriority().setValue(commonOrderPriority.getValue());
		
		return commonOrderSegment;
	}
}
