/*
 *  Copyright Lufthansa Systems.
 */

package com.lhsystems.rd.mm.resources;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class QRNotification {
    String format;
    String qrData;
    String mediaType;
    String info;
}
