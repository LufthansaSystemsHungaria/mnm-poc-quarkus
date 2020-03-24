/*
 *  Copyright Lufthansa Systems.
 */

package com.lhsystems.rd.mm.resources;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BarcodeNotification {
    String format;
    String barcodeData;
    String mediaType;
    String info;
}
