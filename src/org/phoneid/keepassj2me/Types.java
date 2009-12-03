/*
KeePass for J2ME

Copyright 2007 Naomaru Itoi <nao@phoneid.org>

This file was derived from 

Java clone of KeePass - A KeePass file viewer for Java
Copyright 2006 Bill Zwicky <billzwicky@users.sourceforge.net>

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package org.phoneid.keepassj2me;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Tools for slicing and dicing Java and KeePass data types.
 * 
 * @author Bill Zwicky <wrzwicky@pobox.com>
 */
public class Types {
	
	/*
	public static long readUInt(byte buf[], int offset) {
        int firstByte = 0;
        int secondByte = 0;
        int thirdByte = 0;
        int fourthByte = 0;

        firstByte = (0x000000FF & ((int)buf[offset]));
        secondByte = (0x000000FF & ((int)buf[offset+1]));
        thirdByte = (0x000000FF & ((int)buf[offset+2]));
        fourthByte = (0x000000FF & ((int)buf[offset+3]));

        return ((long) (firstByte << 24
	                 | secondByte << 16
                     | thirdByte << 8
                     | fourthByte))
                     & 0xFFFFFFFFL;

	}
	
	public static byte[] writeUInt(long val) {
		byte[] buf = new byte[4];
		
		buf[0] = (byte) ((val & 0xFF000000L) >> 24);
		buf[1] = (byte) ((val & 0x00FF0000L) >> 16);
		buf[2] = (byte) ((val & 0x0000FF00L) >> 8);
		buf[3] = (byte) (val & 0x000000FFL);
		
		return buf;
	}
	*/
	
  /**
   * Read a 32-bit value.
   * 
   * @param buf
   * @param offset
   * @return
   */
  public static int readInt( byte buf[], int offset ) {
    return (buf[offset + 0] & 0xFF) + ((buf[offset + 1] & 0xFF) << 8) + ((buf[offset + 2] & 0xFF) << 16)
           + ((buf[offset + 3] & 0xFF) << 24);
  }



  /**
   * Write a 32-bit value.
   * 
   * @param val
   * @param buf
   * @param offset
   */
  public static void writeInt( int val, byte[] buf, int offset ) {
    buf[offset + 0] = (byte)(val & 0xFF);
    buf[offset + 1] = (byte)((val >>> 8) & 0xFF);
    buf[offset + 2] = (byte)((val >>> 16) & 0xFF);
    buf[offset + 3] = (byte)((val >>> 24) & 0xFF);
  }
  
  public static byte[] writeInt(int val) {
	  byte[] buf = new byte[4];
	  writeInt(val, buf, 0);

	  return buf;
  }

  /**
   * Read an unsigned 16-bit value.
   * 
   * @param buf
   * @param offset
   * @return
   */
  public static int readShort( byte[] buf, int offset ) {
    return (buf[offset + 0] & 0xFF) + ((buf[offset + 1] & 0xFF) << 8);
  }
  
  /** Write an unsigned 16-bit value
   * 
   * @param val
   * @param buf
   * @param offset
   */
  public static void writeShort(int val, byte[] buf, int offset) {
	  buf[offset + 0] = (byte)(val & 0x00FF);
	  buf[offset + 1] = (byte)((val & 0xFF00) >> 8);
  }

  public static byte[] writeShort(int val) {
	  byte[] buf = new byte[2];
	  
	  writeShort(val, buf, 0);
	  
	  return buf;
  }
                     
  /** Read an unsigned byte */
  public static int readUByte( byte[] buf, int offset ) {
    return ((int)buf[offset] & 0xFF);
  }

  /** Write an unsigned byte
   * 
   * @param val
   * @param buf
   * @param offset
   */
  public static void writeUByte(int val, byte[] buf, int offset) {
	  buf[offset] = (byte)(val & 0xFF);
  }
  
  public static byte writeUByte(int val) {
	  byte[] buf = new byte[1];
	  
	  writeUByte(val, buf, 0);
	  
	  return buf[0];
  }

  /**
   * Return len of null-terminated string (i.e. distance to null)
   * within a byte buffer.
   * 
   * @param buf
   * @param offset
   * @return
   */
  public static int strlen( byte[] buf, int offset ) {
    int len = 0;
    while( buf[offset + len] != 0 )
      len++;
    return len;
  }



  /**
   * Copy a sequence of bytes into a new array.
   * 
   * @param b - source array
   * @param offset - first byte
   * @param len - number of bytes
   * @return new byte[len]
   */
  public static byte[] extract( byte[] b, int offset, int len ) {
    byte[] b2 = new byte[len];
    System.arraycopy( b, offset, b2, 0, len );
    return b2;
  }



  /**
   * Unpack date from 5 byte format.
   * The five bytes at 'offset' are unpacked to a java.util.Date instance.
   */
  public static Date readTime( byte[] buf, int offset ) {
    int dw1 = readUByte( buf, offset );
    int dw2 = readUByte( buf, offset + 1 );
    int dw3 = readUByte( buf, offset + 2 );
    int dw4 = readUByte( buf, offset + 3 );
    int dw5 = readUByte( buf, offset + 4 );
  
    // Unpack 5 byte structure to date and time
    int year   =  (dw1 << 6) | (dw2 >> 2);
    int month  = ((dw2 & 0x00000003) << 2) | (dw3 >> 6);
    
    int day    =  (dw3 >> 1) & 0x0000001F;
    int hour   = ((dw3 & 0x00000001) << 4) | (dw4 >> 4);
    int minute = ((dw4 & 0x0000000F) << 2) | (dw5 >> 6);
    int second =   dw5 & 0x0000003F;
  
    Calendar time = Calendar.getInstance();
    // File format is a 1 based month, java Calendar uses a zero based month
    time.set( year, month-1, day, hour, minute, second );
  
    return time.getTime();

    //return null;
  }
  
  public static byte[] writeTime(Date date, Calendar cal) {
	  if ( date == null ) {
		  return null;
	  }
	  
	  byte[] buf = new byte[5];
	  if ( cal == null ) {
	  	cal = Calendar.getInstance();
	  }
	  cal.setTime(date);
	  
	  int year = cal.get(Calendar.YEAR);
      // File format is a 1 based month, java Calendar uses a zero based month
	  int month = cal.get(Calendar.MONTH)+1;
	  int day = cal.get(Calendar.DAY_OF_MONTH);
	  int hour = cal.get(Calendar.HOUR_OF_DAY);
	  int minute = cal.get(Calendar.MINUTE);
	  int second = cal.get(Calendar.SECOND);
	  
	  buf[0] = writeUByte(((year >> 6) & 0x0000003F));
	  buf[1] = writeUByte(((year & 0x0000003F) << 2) | ((month >> 2) & 0x00000003) );
      buf[2] = (byte)(((month & 0x00000003) << 6) | ((day & 0x0000001F) << 1) | ((hour >> 4) & 0x00000001));
      buf[3] = (byte)(((hour & 0x0000000F) << 4) | ((minute >> 2) & 0x0000000F));
      buf[4] = (byte)(((minute & 0x00000003) << 6) | (second & 0x0000003F));
      
      return buf;
  }

  public static int writeCString(String str, OutputStream os) throws IOException {
	  if ( str == null ) {
		  os.write(writeInt(0));
		  return 0;
	  }
	  byte[] initial = str.getBytes("UTF-8");
	  
	  int length = initial.length+1;
	  os.write(writeInt(length));
	  os.write(initial);
	  os.write(0x00);
	  
	  return length;
  }
    
  public static UUID bytestoUUID(byte[] buf) {

	  long msb = 0;
	  for (int i = 0; i < 8; i++) {
		  msb = (msb << 8) | (buf[i] & 0xff);
	  }

	  long lsb = 0;
	  for (int i = 8; i < 16; i++) {
		  lsb = (lsb << 8) | (buf[i] & 0xff);
	  }

	  return new UUID(msb, lsb);

  }

}
