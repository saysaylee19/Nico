/**************************************************************************************************
  Filename:       Sensor.java
  Revised:        $Date: 2013-08-30 11:44:31 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27454 $

  Copyright (c) 2013 - 2014 Texas Instruments Incorporated

  All rights reserved not granted herein.
  Limited License. 

  Texas Instruments Incorporated grants a world-wide, royalty-free,
  non-exclusive license under copyrights and patents it now or hereafter
  owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
  this software subject to the terms herein.  With respect to the foregoing patent
  license, such license is granted  solely to the extent that any such patent is necessary
  to Utilize the software alone.  The patent license shall not apply to any combinations which
  include this software, other than combinations with devices manufactured by or for TI (�TI Devices�). 
  No hardware patent is licensed hereunder.

  Redistributions must preserve existing copyright notices and reproduce this license (including the
  above copyright notice and the disclaimer and (if applicable) source code license limitations below)
  in the documentation and/or other materials provided with the distribution

  Redistribution and use in binary form, without modification, are permitted provided that the following
  conditions are met:

    * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
      software provided in binary form.
    * any redistribution and use are licensed by TI for use only with TI Devices.
    * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.

  If software source code is provided to you, modification and redistribution of the source code are permitted
  provided that the following conditions are met:

    * any redistribution and use of the source code, including any resulting derivative works, are licensed by
      TI for use only with TI Devices.
    * any redistribution and use of any object code compiled from the source code and any resulting derivative
      works, are licensed by TI for use only with TI Devices.

  Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
  promote products derived from this software without specific prior written permission.

  DISCLAIMER.

  THIS SOFTWARE IS PROVIDED BY TI AND TI�S LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL TI AND TI�S LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
  POSSIBILITY OF SUCH DAMAGE.


 **************************************************************************************************/
package com.nicotrax.nicotrax.ble;

//import static android.bluetooth.BluetoothGattCharacteristic.FORMAT_UINT8;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.List;
import java.util.UUID;

import static com.nicotrax.nicotrax.ble.SensorTagGatt.*;
import static java.lang.Math.pow;

import java.util.List;
import java.util.UUID;
import com.nicotrax.nicotrax.ble.Point3D;
import android.bluetooth.BluetoothGattCharacteristic;

// import android.util.Log;
import static java.lang.Math.pow;
// import android.util.Log;


/**
 * This enum encapsulates the differences amongst the sensors. The differences include UUID values and how to interpret the
 * characteristic-containing-measurement.
 */
public enum Sensor {
  IR_TEMPERATURE(UUID_IRT_SERV, UUID_IRT_DATA, UUID_IRT_CONF) {
    @Override
    public Point3D convert(final byte [] value) {

      /*
       * The IR Temperature sensor produces two measurements; Object ( AKA target or IR) Temperature, and Ambient ( AKA die ) temperature.
       * Both need some conversion, and Object temperature is dependent on Ambient temperature.
       * They are stored as [ObjLSB, ObjMSB, AmbLSB, AmbMSB] (4 bytes) Which means we need to shift the bytes around to get the correct values.
       */

      double ambient = extractAmbientTemperature(value);
      double target = extractTargetTemperature(value, ambient);
      return new Point3D(ambient, target, 0);
    }

    private double extractAmbientTemperature(byte [] v) {
      int offset = 2;
      return shortUnsignedAtOffset(v, offset) / 128.0;
    }

    private double extractTargetTemperature(byte [] v, double ambient) {
      Integer twoByteValue = shortSignedAtOffset(v, 0);

      double Vobj2 = twoByteValue.doubleValue();
      Vobj2 *= 0.00000015625;

      double Tdie = ambient + 273.15;

      double S0 = 5.593E-14; // Calibration factor
      double a1 = 1.75E-3;
      double a2 = -1.678E-5;
      double b0 = -2.94E-5;
      double b1 = -5.7E-7;
      double b2 = 4.63E-9;
      double c2 = 13.4;
      double Tref = 298.15;
      double S = S0 * (1 + a1 * (Tdie - Tref) + a2 * pow((Tdie - Tref), 2));
      double Vos = b0 + b1 * (Tdie - Tref) + b2 * pow((Tdie - Tref), 2);
      double fObj = (Vobj2 - Vos) + c2 * pow((Vobj2 - Vos), 2);
      double tObj = pow(pow(Tdie, 4) + (fObj / S), .25);

      return tObj - 273.15;
    }
  };

  /**
   * Gyroscope, Magnetometer, Barometer, IR temperature all store 16 bit two's complement values as LSB MSB, which cannot be directly parsed
   * as getIntValue(FORMAT_SINT16, offset) because the bytes are stored as little-endian.
   * 
   * This function extracts these 16 bit two's complement values.
   * */
  private static Integer shortSignedAtOffset(byte[] c, int offset) {
    Integer lowerByte = (int) c[offset] & 0xFF; 
    Integer upperByte = (int) c[offset+1]; // // Interpret MSB as signed
    return (upperByte << 8) + lowerByte;
  }

  private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
    Integer lowerByte = (int) c[offset] & 0xFF; 
    Integer upperByte = (int) c[offset+1] & 0xFF; // // Interpret MSB as signed
    return (upperByte << 8) + lowerByte;
  }

  public void onCharacteristicChanged(BluetoothGattCharacteristic c) {
    throw new UnsupportedOperationException("Error: the individual enum classes are supposed to override this method.");
  }

  public Point3D convert(byte[] value) {
    throw new UnsupportedOperationException("Error: the individual enum classes are supposed to override this method.");
  }

	private final UUID service, data, config;
	private byte enableCode; // See getEnableSensorCode for explanation.
	public static final byte DISABLE_SENSOR_CODE = 0;
	public static final byte ENABLE_SENSOR_CODE = 1;
	public static final byte CALIBRATE_SENSOR_CODE = 2;

	/**
	 * Constructor called by the Gyroscope and Accelerometer because it more than a boolean enable
	 * code.
	 */
  private Sensor(UUID service, UUID data, UUID config, byte enableCode) {
    this.service = service;
    this.data = data;
    this.config = config;
    this.enableCode = enableCode;
  }

  /**
   * Constructor called by all the sensors except Gyroscope
   * */
  private Sensor(UUID service, UUID data, UUID config) {
    this.service = service;
    this.data = data;
    this.config = config;
    this.enableCode = ENABLE_SENSOR_CODE; // This is the sensor enable code for all sensors except the gyroscope
  }

  /**
   * @return the code which, when written to the configuration characteristic, turns on the sensor.
   * */
  public byte getEnableSensorCode() {
    return enableCode;
  }

  public UUID getService() {
    return service;
  }

  public UUID getData() {
    return data;
  }

  public UUID getConfig() {
    return config;
  }

  public static Sensor getFromDataUuid(UUID uuid) {
    for (Sensor s : Sensor.values()) {
      if (s.getData().equals(uuid)) {
        return s;
      }
    }
    throw new RuntimeException("unable to find UUID.");
  }
  
  public static final Sensor[] SENSOR_LIST = {IR_TEMPERATURE};
}
