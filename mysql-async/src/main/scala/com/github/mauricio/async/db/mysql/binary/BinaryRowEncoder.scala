/*
 * Copyright 2013 Maurício Linhares
 *
 * Maurício Linhares licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.github.mauricio.async.db.mysql.binary

import java.nio.charset.Charset
import com.github.mauricio.async.db.mysql.binary.encoder._
import org.jboss.netty.buffer.ChannelBuffer
import com.github.mauricio.async.db.util.{BitMap, ChannelUtils}
import org.joda.time._

class BinaryRowEncoder( charset : Charset ) {

  private final val stringEncoder = new StringEncoder(charset)

  def encode( values : Seq[Any] ) : ChannelBuffer = {

    val buffer = ChannelUtils.packetBuffer()
    val bitMap = BitMap.forSize(values.length)

    var index = 0

    while ( index < values.length ) {
      val value = values(index)
      if ( value == null ) {
        bitMap.set(index)
      } else {
        val encoder = encoderFor(value)
        encoder.encode(value, buffer)
      }
      index += 1
    }

    buffer
  }

  private def encoderFor( v : Any ) : BinaryEncoder = {

    v match {
      case value : String => this.stringEncoder
      case integer : BigInt => this.stringEncoder
      case integerJava : java.math.BigInteger => this.stringEncoder
      case decimal : BigDecimal => this.stringEncoder
      case decimalJava : java.math.BigDecimal => this.stringEncoder
      case value : Byte => ByteEncoder
      case v : java.lang.Byte => ByteEncoder
      case value : Short => ShortEncoder
      case v : java.lang.Short => ShortEncoder
      case value : Int => IntegerEncoder
      case v : java.lang.Integer => IntegerEncoder
      case value : Long => LongEncoder
      case v : java.lang.Long => LongEncoder
      case value : Float => FloatEncoder
      case v : java.lang.Float => FloatEncoder
      case value : Double => DoubleEncoder
      case v : java.lang.Double => DoubleEncoder
      case v : ReadableDateTime => DateTimeEncoder
      case v : ReadableInstant => ReadableInstantEncoder
      case v : LocalDateTime => LocalDateTimeEncoder
      case v : java.util.Date => JavaDateEncoder
      case v : java.sql.Timestamp => TimestampEncoder
      case v : java.util.Calendar => CalendarEncoder
      case d : LocalDate => DateEncoder
      case d : java.sql.Date => SQLDateEncoder
    }

  }

}