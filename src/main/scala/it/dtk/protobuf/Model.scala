// Generated by ScalaBuff, the Scala Protocol Buffers compiler. DO NOT EDIT!
// source: model.proto

package it.dtk.protobuf

final case class Article (
	uri: String = "",
	title: String = "",
	description: String = "",
	categories: scala.collection.immutable.Seq[String] = Vector.empty[String],
	keywords: scala.collection.immutable.Seq[String] = Vector.empty[String],
	imageUrl: String = "",
	publisher: String = "",
	date: Long = 0L,
	lang: String = "",
	cleanedText: String = "",
	annotations: scala.collection.immutable.Seq[Annotation] = Vector.empty[Annotation],
	focusLocation: Option[Location] = None
) extends com.google.protobuf.GeneratedMessageLite
	with com.google.protobuf.MessageLite.Builder
	with net.sandrogrzicic.scalabuff.Message[Article]
	with net.sandrogrzicic.scalabuff.Parser[Article] {

	def setCategories(_i: Int, _v: String) = copy(categories = categories.updated(_i, _v))
	def addCategories(_f: String) = copy(categories = categories :+ _f)
	def addAllCategories(_f: String*) = copy(categories = categories ++ _f)
	def addAllCategories(_f: TraversableOnce[String]) = copy(categories = categories ++ _f)
	def setKeywords(_i: Int, _v: String) = copy(keywords = keywords.updated(_i, _v))
	def addKeywords(_f: String) = copy(keywords = keywords :+ _f)
	def addAllKeywords(_f: String*) = copy(keywords = keywords ++ _f)
	def addAllKeywords(_f: TraversableOnce[String]) = copy(keywords = keywords ++ _f)
	def setAnnotations(_i: Int, _v: Annotation) = copy(annotations = annotations.updated(_i, _v))
	def addAnnotations(_f: Annotation) = copy(annotations = annotations :+ _f)
	def addAllAnnotations(_f: Annotation*) = copy(annotations = annotations ++ _f)
	def addAllAnnotations(_f: TraversableOnce[Annotation]) = copy(annotations = annotations ++ _f)
	def setFocusLocation(_f: Location) = copy(focusLocation = Some(_f))

	def clearCategories = copy(categories = Vector.empty[String])
	def clearKeywords = copy(keywords = Vector.empty[String])
	def clearAnnotations = copy(annotations = Vector.empty[Annotation])
	def clearFocusLocation = copy(focusLocation = None)

	def writeTo(output: com.google.protobuf.CodedOutputStream) {
		output.writeString(1, uri)
		output.writeString(2, title)
		output.writeString(3, description)
		var index_categories = 0
		while (index_categories < categories.length) {
			output.writeString(4, categories(index_categories))
			index_categories += 1
		}
		var index_keywords = 0
		while (index_keywords < keywords.length) {
			output.writeString(5, keywords(index_keywords))
			index_keywords += 1
		}
		output.writeString(6, imageUrl)
		output.writeString(7, publisher)
		output.writeInt64(8, date)
		output.writeString(9, lang)
		output.writeString(10, cleanedText)
		var index_annotations = 0
		while (index_annotations < annotations.length) {
			output.writeMessage(11, annotations(index_annotations))
			index_annotations += 1
		}
		if (focusLocation.isDefined) output.writeMessage(12, focusLocation.get)
	}

	def getSerializedSize = {
		import com.google.protobuf.CodedOutputStream._
		var __size = 0
		__size += computeStringSize(1, uri)
		__size += computeStringSize(2, title)
		__size += computeStringSize(3, description)
		var index_categories = 0
		while (index_categories < categories.length) {
			__size += computeStringSize(4, categories(index_categories))
			index_categories += 1
		}
		var index_keywords = 0
		while (index_keywords < keywords.length) {
			__size += computeStringSize(5, keywords(index_keywords))
			index_keywords += 1
		}
		__size += computeStringSize(6, imageUrl)
		__size += computeStringSize(7, publisher)
		__size += computeInt64Size(8, date)
		__size += computeStringSize(9, lang)
		__size += computeStringSize(10, cleanedText)
		var index_annotations = 0
		while (index_annotations < annotations.length) {
			__size += computeMessageSize(11, annotations(index_annotations))
			index_annotations += 1
		}
		if (focusLocation.isDefined) __size += computeMessageSize(12, focusLocation.get)

		__size
	}

	def mergeFrom(in: com.google.protobuf.CodedInputStream, extensionRegistry: com.google.protobuf.ExtensionRegistryLite): Article = {
		import com.google.protobuf.ExtensionRegistryLite.{getEmptyRegistry => _emptyRegistry}
		var __uri: String = ""
		var __title: String = ""
		var __description: String = ""
		val __categories: scala.collection.mutable.Buffer[String] = categories.toBuffer
		val __keywords: scala.collection.mutable.Buffer[String] = keywords.toBuffer
		var __imageUrl: String = ""
		var __publisher: String = ""
		var __date: Long = 0L
		var __lang: String = ""
		var __cleanedText: String = ""
		val __annotations: scala.collection.mutable.Buffer[Annotation] = annotations.toBuffer
		var __focusLocation: Option[Location] = focusLocation

		def __newMerged = Article(
			__uri,
			__title,
			__description,
			Vector(__categories: _*),
			Vector(__keywords: _*),
			__imageUrl,
			__publisher,
			__date,
			__lang,
			__cleanedText,
			Vector(__annotations: _*),
			__focusLocation
		)
		while (true) in.readTag match {
			case 0 => return __newMerged
			case 10 => __uri = in.readString()
			case 18 => __title = in.readString()
			case 26 => __description = in.readString()
			case 34 => __categories += in.readString()
			case 42 => __keywords += in.readString()
			case 50 => __imageUrl = in.readString()
			case 58 => __publisher = in.readString()
			case 64 => __date = in.readInt64()
			case 74 => __lang = in.readString()
			case 82 => __cleanedText = in.readString()
			case 90 => __annotations += readMessage[Annotation](in, Annotation.defaultInstance, _emptyRegistry)
			case 98 => __focusLocation = Some(readMessage[Location](in, __focusLocation.orElse({
				__focusLocation = Location.defaultInstance
				__focusLocation
			}).get, _emptyRegistry))
			case default => if (!in.skipField(default)) return __newMerged
		}
		null
	}

	def mergeFrom(m: Article) = {
		Article(
			m.uri,
			m.title,
			m.description,
			categories ++ m.categories,
			keywords ++ m.keywords,
			m.imageUrl,
			m.publisher,
			m.date,
			m.lang,
			m.cleanedText,
			annotations ++ m.annotations,
			m.focusLocation.orElse(focusLocation)
		)
	}

	def getDefaultInstanceForType = Article.defaultInstance
	def clear = getDefaultInstanceForType
	def isInitialized = true
	def build = this
	def buildPartial = this
	def parsePartialFrom(cis: com.google.protobuf.CodedInputStream, er: com.google.protobuf.ExtensionRegistryLite) = mergeFrom(cis, er)
	override def getParserForType = this
	def newBuilderForType = getDefaultInstanceForType
	def toBuilder = this
	def toJson(indent: Int = 0): String = "ScalaBuff JSON generation not enabled. Use --generate_json_method to enable."
}

object Article {
	@scala.beans.BeanProperty val defaultInstance = new Article()

	def parseFrom(data: Array[Byte]): Article = defaultInstance.mergeFrom(data)
	def parseFrom(data: Array[Byte], offset: Int, length: Int): Article = defaultInstance.mergeFrom(data, offset, length)
	def parseFrom(byteString: com.google.protobuf.ByteString): Article = defaultInstance.mergeFrom(byteString)
	def parseFrom(stream: java.io.InputStream): Article = defaultInstance.mergeFrom(stream)
	def parseDelimitedFrom(stream: java.io.InputStream): Option[Article] = defaultInstance.mergeDelimitedFromStream(stream)

	val URI_FIELD_NUMBER = 1
	val TITLE_FIELD_NUMBER = 2
	val DESCRIPTION_FIELD_NUMBER = 3
	val CATEGORIES_FIELD_NUMBER = 4
	val KEYWORDS_FIELD_NUMBER = 5
	val IMAGEURL_FIELD_NUMBER = 6
	val PUBLISHER_FIELD_NUMBER = 7
	val DATE_FIELD_NUMBER = 8
	val LANG_FIELD_NUMBER = 9
	val CLEANEDTEXT_FIELD_NUMBER = 10
	val ANNOTATIONS_FIELD_NUMBER = 11
	val FOCUSLOCATION_FIELD_NUMBER = 12

	def newBuilder = defaultInstance.newBuilderForType
	def newBuilder(prototype: Article) = defaultInstance.mergeFrom(prototype)

}
final case class Annotation (
	surfaceForm: String = "",
	dbpediaUrl: String = "",
	wikipediaUrl: String = "",
	types: scala.collection.immutable.Seq[AnnotationType] = Vector.empty[AnnotationType],
	offset: Long = 0L,
	support: Long = 0L,
	pin: Option[Pin] = None,
	section: Annotation.DocumentSection.EnumVal = Annotation.DocumentSection._UNINITIALIZED
) extends com.google.protobuf.GeneratedMessageLite
	with com.google.protobuf.MessageLite.Builder
	with net.sandrogrzicic.scalabuff.Message[Annotation]
	with net.sandrogrzicic.scalabuff.Parser[Annotation] {

	def setTypes(_i: Int, _v: AnnotationType) = copy(types = types.updated(_i, _v))
	def addTypes(_f: AnnotationType) = copy(types = types :+ _f)
	def addAllTypes(_f: AnnotationType*) = copy(types = types ++ _f)
	def addAllTypes(_f: TraversableOnce[AnnotationType]) = copy(types = types ++ _f)
	def setPin(_f: Pin) = copy(pin = Some(_f))

	def clearTypes = copy(types = Vector.empty[AnnotationType])
	def clearPin = copy(pin = None)

	def writeTo(output: com.google.protobuf.CodedOutputStream) {
		output.writeString(1, surfaceForm)
		output.writeString(2, dbpediaUrl)
		output.writeString(3, wikipediaUrl)
		var index_types = 0
		while (index_types < types.length) {
			output.writeMessage(4, types(index_types))
			index_types += 1
		}
		output.writeInt64(5, offset)
		output.writeInt64(6, support)
		if (pin.isDefined) output.writeMessage(7, pin.get)
		output.writeEnum(8, section)
	}

	def getSerializedSize = {
		import com.google.protobuf.CodedOutputStream._
		var __size = 0
		__size += computeStringSize(1, surfaceForm)
		__size += computeStringSize(2, dbpediaUrl)
		__size += computeStringSize(3, wikipediaUrl)
		var index_types = 0
		while (index_types < types.length) {
			__size += computeMessageSize(4, types(index_types))
			index_types += 1
		}
		__size += computeInt64Size(5, offset)
		__size += computeInt64Size(6, support)
		if (pin.isDefined) __size += computeMessageSize(7, pin.get)
		__size += computeEnumSize(8, section)

		__size
	}

	def mergeFrom(in: com.google.protobuf.CodedInputStream, extensionRegistry: com.google.protobuf.ExtensionRegistryLite): Annotation = {
		import com.google.protobuf.ExtensionRegistryLite.{getEmptyRegistry => _emptyRegistry}
		var __surfaceForm: String = ""
		var __dbpediaUrl: String = ""
		var __wikipediaUrl: String = ""
		val __types: scala.collection.mutable.Buffer[AnnotationType] = types.toBuffer
		var __offset: Long = 0L
		var __support: Long = 0L
		var __pin: Option[Pin] = pin
		var __section: Annotation.DocumentSection.EnumVal = Annotation.DocumentSection._UNINITIALIZED

		def __newMerged = Annotation(
			__surfaceForm,
			__dbpediaUrl,
			__wikipediaUrl,
			Vector(__types: _*),
			__offset,
			__support,
			__pin,
			__section
		)
		while (true) in.readTag match {
			case 0 => return __newMerged
			case 10 => __surfaceForm = in.readString()
			case 18 => __dbpediaUrl = in.readString()
			case 26 => __wikipediaUrl = in.readString()
			case 34 => __types += readMessage[AnnotationType](in, AnnotationType.defaultInstance, _emptyRegistry)
			case 40 => __offset = in.readInt64()
			case 48 => __support = in.readInt64()
			case 58 => __pin = Some(readMessage[Pin](in, __pin.orElse({
				__pin = Pin.defaultInstance
				__pin
			}).get, _emptyRegistry))
			case 64 => __section = Annotation.DocumentSection.valueOf(in.readEnum())
			case default => if (!in.skipField(default)) return __newMerged
		}
		null
	}

	def mergeFrom(m: Annotation) = {
		Annotation(
			m.surfaceForm,
			m.dbpediaUrl,
			m.wikipediaUrl,
			types ++ m.types,
			m.offset,
			m.support,
			m.pin.orElse(pin),
			m.section
		)
	}

	def getDefaultInstanceForType = Annotation.defaultInstance
	def clear = getDefaultInstanceForType
	def isInitialized = true
	def build = this
	def buildPartial = this
	def parsePartialFrom(cis: com.google.protobuf.CodedInputStream, er: com.google.protobuf.ExtensionRegistryLite) = mergeFrom(cis, er)
	override def getParserForType = this
	def newBuilderForType = getDefaultInstanceForType
	def toBuilder = this
	def toJson(indent: Int = 0): String = "ScalaBuff JSON generation not enabled. Use --generate_json_method to enable."
}

object Annotation {
	@scala.beans.BeanProperty val defaultInstance = new Annotation()

	def parseFrom(data: Array[Byte]): Annotation = defaultInstance.mergeFrom(data)
	def parseFrom(data: Array[Byte], offset: Int, length: Int): Annotation = defaultInstance.mergeFrom(data, offset, length)
	def parseFrom(byteString: com.google.protobuf.ByteString): Annotation = defaultInstance.mergeFrom(byteString)
	def parseFrom(stream: java.io.InputStream): Annotation = defaultInstance.mergeFrom(stream)
	def parseDelimitedFrom(stream: java.io.InputStream): Option[Annotation] = defaultInstance.mergeDelimitedFromStream(stream)

	val SURFACEFORM_FIELD_NUMBER = 1
	val DBPEDIAURL_FIELD_NUMBER = 2
	val WIKIPEDIAURL_FIELD_NUMBER = 3
	val TYPES_FIELD_NUMBER = 4
	val OFFSET_FIELD_NUMBER = 5
	val SUPPORT_FIELD_NUMBER = 6
	val PIN_FIELD_NUMBER = 7
	val SECTION_FIELD_NUMBER = 8

	def newBuilder = defaultInstance.newBuilderForType
	def newBuilder(prototype: Annotation) = defaultInstance.mergeFrom(prototype)

	object DocumentSection extends net.sandrogrzicic.scalabuff.Enum {
		sealed trait EnumVal extends Value
		val _UNINITIALIZED = new EnumVal { val name = "UNINITIALIZED ENUM VALUE"; val id = -1 }

		val Title = new EnumVal { val name = "Title"; val id = 0 }
		val Summary = new EnumVal { val name = "Summary"; val id = 1 }
		val Corpus = new EnumVal { val name = "Corpus"; val id = 2 }
		val KeyWords = new EnumVal { val name = "KeyWords"; val id = 3 }
		val NotSet = new EnumVal { val name = "NotSet"; val id = 4 }

		val Title_VALUE = 0
		val Summary_VALUE = 1
		val Corpus_VALUE = 2
		val KeyWords_VALUE = 3
		val NotSet_VALUE = 4

		def valueOf(id: Int) = id match {
			case 0 => Title
			case 1 => Summary
			case 2 => Corpus
			case 3 => KeyWords
			case 4 => NotSet
			case _default => throw new net.sandrogrzicic.scalabuff.UnknownEnumException(_default)
		}
		val internalGetValueMap = new com.google.protobuf.Internal.EnumLiteMap[EnumVal] {
			def findValueByNumber(id: Int): EnumVal = valueOf(id)
		}
	}

}
final case class Location (
	id: Long = 0L,
	cityName: String = "",
	provinceName: String = "",
	regionName: String = "",
	pin: Pin = Pin.defaultInstance
) extends com.google.protobuf.GeneratedMessageLite
	with com.google.protobuf.MessageLite.Builder
	with net.sandrogrzicic.scalabuff.Message[Location]
	with net.sandrogrzicic.scalabuff.Parser[Location] {



	def writeTo(output: com.google.protobuf.CodedOutputStream) {
		output.writeInt64(1, id)
		output.writeString(2, cityName)
		output.writeString(3, provinceName)
		output.writeString(4, regionName)
		output.writeMessage(5, pin)
	}

	def getSerializedSize = {
		import com.google.protobuf.CodedOutputStream._
		var __size = 0
		__size += computeInt64Size(1, id)
		__size += computeStringSize(2, cityName)
		__size += computeStringSize(3, provinceName)
		__size += computeStringSize(4, regionName)
		__size += computeMessageSize(5, pin)

		__size
	}

	def mergeFrom(in: com.google.protobuf.CodedInputStream, extensionRegistry: com.google.protobuf.ExtensionRegistryLite): Location = {
		import com.google.protobuf.ExtensionRegistryLite.{getEmptyRegistry => _emptyRegistry}
		var __id: Long = 0L
		var __cityName: String = ""
		var __provinceName: String = ""
		var __regionName: String = ""
		var __pin: Pin = Pin.defaultInstance

		def __newMerged = Location(
			__id,
			__cityName,
			__provinceName,
			__regionName,
			__pin
		)
		while (true) in.readTag match {
			case 0 => return __newMerged
			case 8 => __id = in.readInt64()
			case 18 => __cityName = in.readString()
			case 26 => __provinceName = in.readString()
			case 34 => __regionName = in.readString()
			case 42 => __pin = readMessage[Pin](in, __pin, _emptyRegistry)
			case default => if (!in.skipField(default)) return __newMerged
		}
		null
	}

	def mergeFrom(m: Location) = {
		Location(
			m.id,
			m.cityName,
			m.provinceName,
			m.regionName,
			m.pin
		)
	}

	def getDefaultInstanceForType = Location.defaultInstance
	def clear = getDefaultInstanceForType
	def isInitialized = true
	def build = this
	def buildPartial = this
	def parsePartialFrom(cis: com.google.protobuf.CodedInputStream, er: com.google.protobuf.ExtensionRegistryLite) = mergeFrom(cis, er)
	override def getParserForType = this
	def newBuilderForType = getDefaultInstanceForType
	def toBuilder = this
	def toJson(indent: Int = 0): String = "ScalaBuff JSON generation not enabled. Use --generate_json_method to enable."
}

object Location {
	@scala.beans.BeanProperty val defaultInstance = new Location()

	def parseFrom(data: Array[Byte]): Location = defaultInstance.mergeFrom(data)
	def parseFrom(data: Array[Byte], offset: Int, length: Int): Location = defaultInstance.mergeFrom(data, offset, length)
	def parseFrom(byteString: com.google.protobuf.ByteString): Location = defaultInstance.mergeFrom(byteString)
	def parseFrom(stream: java.io.InputStream): Location = defaultInstance.mergeFrom(stream)
	def parseDelimitedFrom(stream: java.io.InputStream): Option[Location] = defaultInstance.mergeDelimitedFromStream(stream)

	val ID_FIELD_NUMBER = 1
	val CITYNAME_FIELD_NUMBER = 2
	val PROVINCENAME_FIELD_NUMBER = 3
	val REGIONNAME_FIELD_NUMBER = 4
	val PIN_FIELD_NUMBER = 5

	def newBuilder = defaultInstance.newBuilderForType
	def newBuilder(prototype: Location) = defaultInstance.mergeFrom(prototype)

}
final case class Pin (
	lat: Double = 0.0,
	lon: Double = 0.0
) extends com.google.protobuf.GeneratedMessageLite
	with com.google.protobuf.MessageLite.Builder
	with net.sandrogrzicic.scalabuff.Message[Pin]
	with net.sandrogrzicic.scalabuff.Parser[Pin] {



	def writeTo(output: com.google.protobuf.CodedOutputStream) {
		output.writeDouble(1, lat)
		output.writeDouble(2, lon)
	}

	def getSerializedSize = {
		import com.google.protobuf.CodedOutputStream._
		var __size = 0
		__size += computeDoubleSize(1, lat)
		__size += computeDoubleSize(2, lon)

		__size
	}

	def mergeFrom(in: com.google.protobuf.CodedInputStream, extensionRegistry: com.google.protobuf.ExtensionRegistryLite): Pin = {
		import com.google.protobuf.ExtensionRegistryLite.{getEmptyRegistry => _emptyRegistry}
		var __lat: Double = 0.0
		var __lon: Double = 0.0

		def __newMerged = Pin(
			__lat,
			__lon
		)
		while (true) in.readTag match {
			case 0 => return __newMerged
			case 9 => __lat = in.readDouble()
			case 17 => __lon = in.readDouble()
			case default => if (!in.skipField(default)) return __newMerged
		}
		null
	}

	def mergeFrom(m: Pin) = {
		Pin(
			m.lat,
			m.lon
		)
	}

	def getDefaultInstanceForType = Pin.defaultInstance
	def clear = getDefaultInstanceForType
	def isInitialized = true
	def build = this
	def buildPartial = this
	def parsePartialFrom(cis: com.google.protobuf.CodedInputStream, er: com.google.protobuf.ExtensionRegistryLite) = mergeFrom(cis, er)
	override def getParserForType = this
	def newBuilderForType = getDefaultInstanceForType
	def toBuilder = this
	def toJson(indent: Int = 0): String = "ScalaBuff JSON generation not enabled. Use --generate_json_method to enable."
}

object Pin {
	@scala.beans.BeanProperty val defaultInstance = new Pin()

	def parseFrom(data: Array[Byte]): Pin = defaultInstance.mergeFrom(data)
	def parseFrom(data: Array[Byte], offset: Int, length: Int): Pin = defaultInstance.mergeFrom(data, offset, length)
	def parseFrom(byteString: com.google.protobuf.ByteString): Pin = defaultInstance.mergeFrom(byteString)
	def parseFrom(stream: java.io.InputStream): Pin = defaultInstance.mergeFrom(stream)
	def parseDelimitedFrom(stream: java.io.InputStream): Option[Pin] = defaultInstance.mergeDelimitedFromStream(stream)

	val LAT_FIELD_NUMBER = 1
	val LON_FIELD_NUMBER = 2

	def newBuilder = defaultInstance.newBuilderForType
	def newBuilder(prototype: Pin) = defaultInstance.mergeFrom(prototype)

}
final case class AnnotationType (
	src: String = "",
	value: String = ""
) extends com.google.protobuf.GeneratedMessageLite
	with com.google.protobuf.MessageLite.Builder
	with net.sandrogrzicic.scalabuff.Message[AnnotationType]
	with net.sandrogrzicic.scalabuff.Parser[AnnotationType] {



	def writeTo(output: com.google.protobuf.CodedOutputStream) {
		output.writeString(1, src)
		output.writeString(2, value)
	}

	def getSerializedSize = {
		import com.google.protobuf.CodedOutputStream._
		var __size = 0
		__size += computeStringSize(1, src)
		__size += computeStringSize(2, value)

		__size
	}

	def mergeFrom(in: com.google.protobuf.CodedInputStream, extensionRegistry: com.google.protobuf.ExtensionRegistryLite): AnnotationType = {
		import com.google.protobuf.ExtensionRegistryLite.{getEmptyRegistry => _emptyRegistry}
		var __src: String = ""
		var __value: String = ""

		def __newMerged = AnnotationType(
			__src,
			__value
		)
		while (true) in.readTag match {
			case 0 => return __newMerged
			case 10 => __src = in.readString()
			case 18 => __value = in.readString()
			case default => if (!in.skipField(default)) return __newMerged
		}
		null
	}

	def mergeFrom(m: AnnotationType) = {
		AnnotationType(
			m.src,
			m.value
		)
	}

	def getDefaultInstanceForType = AnnotationType.defaultInstance
	def clear = getDefaultInstanceForType
	def isInitialized = true
	def build = this
	def buildPartial = this
	def parsePartialFrom(cis: com.google.protobuf.CodedInputStream, er: com.google.protobuf.ExtensionRegistryLite) = mergeFrom(cis, er)
	override def getParserForType = this
	def newBuilderForType = getDefaultInstanceForType
	def toBuilder = this
	def toJson(indent: Int = 0): String = "ScalaBuff JSON generation not enabled. Use --generate_json_method to enable."
}

object AnnotationType {
	@scala.beans.BeanProperty val defaultInstance = new AnnotationType()

	def parseFrom(data: Array[Byte]): AnnotationType = defaultInstance.mergeFrom(data)
	def parseFrom(data: Array[Byte], offset: Int, length: Int): AnnotationType = defaultInstance.mergeFrom(data, offset, length)
	def parseFrom(byteString: com.google.protobuf.ByteString): AnnotationType = defaultInstance.mergeFrom(byteString)
	def parseFrom(stream: java.io.InputStream): AnnotationType = defaultInstance.mergeFrom(stream)
	def parseDelimitedFrom(stream: java.io.InputStream): Option[AnnotationType] = defaultInstance.mergeDelimitedFromStream(stream)

	val SRC_FIELD_NUMBER = 1
	val VALUE_FIELD_NUMBER = 2

	def newBuilder = defaultInstance.newBuilderForType
	def newBuilder(prototype: AnnotationType) = defaultInstance.mergeFrom(prototype)

}

object Model {
	def registerAllExtensions(registry: com.google.protobuf.ExtensionRegistryLite) {
	}

	private val fromBinaryHintMap = collection.immutable.HashMap[String, Array[Byte] ⇒ com.google.protobuf.GeneratedMessageLite](
		 "Article" -> (bytes ⇒ Article.parseFrom(bytes)),
		 "Annotation" -> (bytes ⇒ Annotation.parseFrom(bytes)),
		 "Location" -> (bytes ⇒ Location.parseFrom(bytes)),
		 "Pin" -> (bytes ⇒ Pin.parseFrom(bytes)),
		 "AnnotationType" -> (bytes ⇒ AnnotationType.parseFrom(bytes))
	)

	def deserializePayload(payload: Array[Byte], payloadType: String): com.google.protobuf.GeneratedMessageLite = {
		fromBinaryHintMap.get(payloadType) match {
			case Some(f) ⇒ f(payload)
			case None    ⇒ throw new IllegalArgumentException(s"unimplemented deserialization of message payload of type [${payloadType}]")
		}
	}
}
