// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: CollectionTypeEvent.proto

public final class CollectionTypeEventOuterClass {
  private CollectionTypeEventOuterClass() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface CollectionTypeEventOrBuilder extends
      // @@protoc_insertion_point(interface_extends:CollectionTypeEvent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated int32 integerList = 1;</code>
     * @return A list containing the integerList.
     */
    java.util.List<java.lang.Integer> getIntegerListList();
    /**
     * <code>repeated int32 integerList = 1;</code>
     * @return The count of integerList.
     */
    int getIntegerListCount();
    /**
     * <code>repeated int32 integerList = 1;</code>
     * @param index The index of the element to return.
     * @return The integerList at the given index.
     */
    int getIntegerList(int index);

    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    int getMapStringIntCount();
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    boolean containsMapStringInt(
        java.lang.String key);
    /**
     * Use {@link #getMapStringIntMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, java.lang.Integer>
    getMapStringInt();
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    java.util.Map<java.lang.String, java.lang.Integer>
    getMapStringIntMap();
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    int getMapStringIntOrDefault(
        java.lang.String key,
        int defaultValue);
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    int getMapStringIntOrThrow(
        java.lang.String key);
  }
  /**
   * Protobuf type {@code CollectionTypeEvent}
   */
  public static final class CollectionTypeEvent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:CollectionTypeEvent)
      CollectionTypeEventOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use CollectionTypeEvent.newBuilder() to construct.
    private CollectionTypeEvent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private CollectionTypeEvent() {
      integerList_ = emptyIntList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new CollectionTypeEvent();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CollectionTypeEventOuterClass.internal_static_CollectionTypeEvent_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    @java.lang.Override
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 2:
          return internalGetMapStringInt();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CollectionTypeEventOuterClass.internal_static_CollectionTypeEvent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              CollectionTypeEventOuterClass.CollectionTypeEvent.class, CollectionTypeEventOuterClass.CollectionTypeEvent.Builder.class);
    }

    public static final int INTEGERLIST_FIELD_NUMBER = 1;
    @SuppressWarnings("serial")
    private com.google.protobuf.Internal.IntList integerList_;
    /**
     * <code>repeated int32 integerList = 1;</code>
     * @return A list containing the integerList.
     */
    @java.lang.Override
    public java.util.List<java.lang.Integer>
        getIntegerListList() {
      return integerList_;
    }
    /**
     * <code>repeated int32 integerList = 1;</code>
     * @return The count of integerList.
     */
    public int getIntegerListCount() {
      return integerList_.size();
    }
    /**
     * <code>repeated int32 integerList = 1;</code>
     * @param index The index of the element to return.
     * @return The integerList at the given index.
     */
    public int getIntegerList(int index) {
      return integerList_.getInt(index);
    }
    private int integerListMemoizedSerializedSize = -1;

    public static final int MAPSTRINGINT_FIELD_NUMBER = 2;
    private static final class MapStringIntDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, java.lang.Integer> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, java.lang.Integer>newDefaultInstance(
                  CollectionTypeEventOuterClass.internal_static_CollectionTypeEvent_MapStringIntEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.INT32,
                  0);
    }
    @SuppressWarnings("serial")
    private com.google.protobuf.MapField<
        java.lang.String, java.lang.Integer> mapStringInt_;
    private com.google.protobuf.MapField<java.lang.String, java.lang.Integer>
    internalGetMapStringInt() {
      if (mapStringInt_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            MapStringIntDefaultEntryHolder.defaultEntry);
      }
      return mapStringInt_;
    }
    public int getMapStringIntCount() {
      return internalGetMapStringInt().getMap().size();
    }
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    @java.lang.Override
    public boolean containsMapStringInt(
        java.lang.String key) {
      if (key == null) { throw new NullPointerException("map key"); }
      return internalGetMapStringInt().getMap().containsKey(key);
    }
    /**
     * Use {@link #getMapStringIntMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, java.lang.Integer> getMapStringInt() {
      return getMapStringIntMap();
    }
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    @java.lang.Override
    public java.util.Map<java.lang.String, java.lang.Integer> getMapStringIntMap() {
      return internalGetMapStringInt().getMap();
    }
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    @java.lang.Override
    public int getMapStringIntOrDefault(
        java.lang.String key,
        int defaultValue) {
      if (key == null) { throw new NullPointerException("map key"); }
      java.util.Map<java.lang.String, java.lang.Integer> map =
          internalGetMapStringInt().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
     */
    @java.lang.Override
    public int getMapStringIntOrThrow(
        java.lang.String key) {
      if (key == null) { throw new NullPointerException("map key"); }
      java.util.Map<java.lang.String, java.lang.Integer> map =
          internalGetMapStringInt().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (getIntegerListList().size() > 0) {
        output.writeUInt32NoTag(10);
        output.writeUInt32NoTag(integerListMemoizedSerializedSize);
      }
      for (int i = 0; i < integerList_.size(); i++) {
        output.writeInt32NoTag(integerList_.getInt(i));
      }
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetMapStringInt(),
          MapStringIntDefaultEntryHolder.defaultEntry,
          2);
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      {
        int dataSize = 0;
        for (int i = 0; i < integerList_.size(); i++) {
          dataSize += com.google.protobuf.CodedOutputStream
            .computeInt32SizeNoTag(integerList_.getInt(i));
        }
        size += dataSize;
        if (!getIntegerListList().isEmpty()) {
          size += 1;
          size += com.google.protobuf.CodedOutputStream
              .computeInt32SizeNoTag(dataSize);
        }
        integerListMemoizedSerializedSize = dataSize;
      }
      for (java.util.Map.Entry<java.lang.String, java.lang.Integer> entry
           : internalGetMapStringInt().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, java.lang.Integer>
        mapStringInt__ = MapStringIntDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(2, mapStringInt__);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof CollectionTypeEventOuterClass.CollectionTypeEvent)) {
        return super.equals(obj);
      }
      CollectionTypeEventOuterClass.CollectionTypeEvent other = (CollectionTypeEventOuterClass.CollectionTypeEvent) obj;

      if (!getIntegerListList()
          .equals(other.getIntegerListList())) return false;
      if (!internalGetMapStringInt().equals(
          other.internalGetMapStringInt())) return false;
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (getIntegerListCount() > 0) {
        hash = (37 * hash) + INTEGERLIST_FIELD_NUMBER;
        hash = (53 * hash) + getIntegerListList().hashCode();
      }
      if (!internalGetMapStringInt().getMap().isEmpty()) {
        hash = (37 * hash) + MAPSTRINGINT_FIELD_NUMBER;
        hash = (53 * hash) + internalGetMapStringInt().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }

    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static CollectionTypeEventOuterClass.CollectionTypeEvent parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(CollectionTypeEventOuterClass.CollectionTypeEvent prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code CollectionTypeEvent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:CollectionTypeEvent)
        CollectionTypeEventOuterClass.CollectionTypeEventOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return CollectionTypeEventOuterClass.internal_static_CollectionTypeEvent_descriptor;
      }

      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapField internalGetMapField(
          int number) {
        switch (number) {
          case 2:
            return internalGetMapStringInt();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapField internalGetMutableMapField(
          int number) {
        switch (number) {
          case 2:
            return internalGetMutableMapStringInt();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return CollectionTypeEventOuterClass.internal_static_CollectionTypeEvent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                CollectionTypeEventOuterClass.CollectionTypeEvent.class, CollectionTypeEventOuterClass.CollectionTypeEvent.Builder.class);
      }

      // Construct using CollectionTypeEventOuterClass.CollectionTypeEvent.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        integerList_ = emptyIntList();
        internalGetMutableMapStringInt().clear();
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return CollectionTypeEventOuterClass.internal_static_CollectionTypeEvent_descriptor;
      }

      @java.lang.Override
      public CollectionTypeEventOuterClass.CollectionTypeEvent getDefaultInstanceForType() {
        return CollectionTypeEventOuterClass.CollectionTypeEvent.getDefaultInstance();
      }

      @java.lang.Override
      public CollectionTypeEventOuterClass.CollectionTypeEvent build() {
        CollectionTypeEventOuterClass.CollectionTypeEvent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public CollectionTypeEventOuterClass.CollectionTypeEvent buildPartial() {
        CollectionTypeEventOuterClass.CollectionTypeEvent result = new CollectionTypeEventOuterClass.CollectionTypeEvent(this);
        buildPartialRepeatedFields(result);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartialRepeatedFields(CollectionTypeEventOuterClass.CollectionTypeEvent result) {
        if (((bitField0_ & 0x00000001) != 0)) {
          integerList_.makeImmutable();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.integerList_ = integerList_;
      }

      private void buildPartial0(CollectionTypeEventOuterClass.CollectionTypeEvent result) {
        int from_bitField0_ = bitField0_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.mapStringInt_ = internalGetMapStringInt();
          result.mapStringInt_.makeImmutable();
        }
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof CollectionTypeEventOuterClass.CollectionTypeEvent) {
          return mergeFrom((CollectionTypeEventOuterClass.CollectionTypeEvent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(CollectionTypeEventOuterClass.CollectionTypeEvent other) {
        if (other == CollectionTypeEventOuterClass.CollectionTypeEvent.getDefaultInstance()) return this;
        if (!other.integerList_.isEmpty()) {
          if (integerList_.isEmpty()) {
            integerList_ = other.integerList_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureIntegerListIsMutable();
            integerList_.addAll(other.integerList_);
          }
          onChanged();
        }
        internalGetMutableMapStringInt().mergeFrom(
            other.internalGetMapStringInt());
        bitField0_ |= 0x00000002;
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                int v = input.readInt32();
                ensureIntegerListIsMutable();
                integerList_.addInt(v);
                break;
              } // case 8
              case 10: {
                int length = input.readRawVarint32();
                int limit = input.pushLimit(length);
                ensureIntegerListIsMutable();
                while (input.getBytesUntilLimit() > 0) {
                  integerList_.addInt(input.readInt32());
                }
                input.popLimit(limit);
                break;
              } // case 10
              case 18: {
                com.google.protobuf.MapEntry<java.lang.String, java.lang.Integer>
                mapStringInt__ = input.readMessage(
                    MapStringIntDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                internalGetMutableMapStringInt().getMutableMap().put(
                    mapStringInt__.getKey(), mapStringInt__.getValue());
                bitField0_ |= 0x00000002;
                break;
              } // case 18
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.Internal.IntList integerList_ = emptyIntList();
      private void ensureIntegerListIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          integerList_ = mutableCopy(integerList_);
          bitField0_ |= 0x00000001;
        }
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @return A list containing the integerList.
       */
      public java.util.List<java.lang.Integer>
          getIntegerListList() {
        return ((bitField0_ & 0x00000001) != 0) ?
                 java.util.Collections.unmodifiableList(integerList_) : integerList_;
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @return The count of integerList.
       */
      public int getIntegerListCount() {
        return integerList_.size();
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @param index The index of the element to return.
       * @return The integerList at the given index.
       */
      public int getIntegerList(int index) {
        return integerList_.getInt(index);
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @param index The index to set the value at.
       * @param value The integerList to set.
       * @return This builder for chaining.
       */
      public Builder setIntegerList(
          int index, int value) {

        ensureIntegerListIsMutable();
        integerList_.setInt(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @param value The integerList to add.
       * @return This builder for chaining.
       */
      public Builder addIntegerList(int value) {

        ensureIntegerListIsMutable();
        integerList_.addInt(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @param values The integerList to add.
       * @return This builder for chaining.
       */
      public Builder addAllIntegerList(
          java.lang.Iterable<? extends java.lang.Integer> values) {
        ensureIntegerListIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, integerList_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated int32 integerList = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearIntegerList() {
        integerList_ = emptyIntList();
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }

      private com.google.protobuf.MapField<
          java.lang.String, java.lang.Integer> mapStringInt_;
      private com.google.protobuf.MapField<java.lang.String, java.lang.Integer>
          internalGetMapStringInt() {
        if (mapStringInt_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              MapStringIntDefaultEntryHolder.defaultEntry);
        }
        return mapStringInt_;
      }
      private com.google.protobuf.MapField<java.lang.String, java.lang.Integer>
          internalGetMutableMapStringInt() {
        if (mapStringInt_ == null) {
          mapStringInt_ = com.google.protobuf.MapField.newMapField(
              MapStringIntDefaultEntryHolder.defaultEntry);
        }
        if (!mapStringInt_.isMutable()) {
          mapStringInt_ = mapStringInt_.copy();
        }
        bitField0_ |= 0x00000002;
        onChanged();
        return mapStringInt_;
      }
      public int getMapStringIntCount() {
        return internalGetMapStringInt().getMap().size();
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      @java.lang.Override
      public boolean containsMapStringInt(
          java.lang.String key) {
        if (key == null) { throw new NullPointerException("map key"); }
        return internalGetMapStringInt().getMap().containsKey(key);
      }
      /**
       * Use {@link #getMapStringIntMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, java.lang.Integer> getMapStringInt() {
        return getMapStringIntMap();
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      @java.lang.Override
      public java.util.Map<java.lang.String, java.lang.Integer> getMapStringIntMap() {
        return internalGetMapStringInt().getMap();
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      @java.lang.Override
      public int getMapStringIntOrDefault(
          java.lang.String key,
          int defaultValue) {
        if (key == null) { throw new NullPointerException("map key"); }
        java.util.Map<java.lang.String, java.lang.Integer> map =
            internalGetMapStringInt().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      @java.lang.Override
      public int getMapStringIntOrThrow(
          java.lang.String key) {
        if (key == null) { throw new NullPointerException("map key"); }
        java.util.Map<java.lang.String, java.lang.Integer> map =
            internalGetMapStringInt().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }
      public Builder clearMapStringInt() {
        bitField0_ = (bitField0_ & ~0x00000002);
        internalGetMutableMapStringInt().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      public Builder removeMapStringInt(
          java.lang.String key) {
        if (key == null) { throw new NullPointerException("map key"); }
        internalGetMutableMapStringInt().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, java.lang.Integer>
          getMutableMapStringInt() {
        bitField0_ |= 0x00000002;
        return internalGetMutableMapStringInt().getMutableMap();
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      public Builder putMapStringInt(
          java.lang.String key,
          int value) {
        if (key == null) { throw new NullPointerException("map key"); }

        internalGetMutableMapStringInt().getMutableMap()
            .put(key, value);
        bitField0_ |= 0x00000002;
        return this;
      }
      /**
       * <code>map&lt;string, int32&gt; mapStringInt = 2;</code>
       */
      public Builder putAllMapStringInt(
          java.util.Map<java.lang.String, java.lang.Integer> values) {
        internalGetMutableMapStringInt().getMutableMap()
            .putAll(values);
        bitField0_ |= 0x00000002;
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:CollectionTypeEvent)
    }

    // @@protoc_insertion_point(class_scope:CollectionTypeEvent)
    private static final CollectionTypeEventOuterClass.CollectionTypeEvent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new CollectionTypeEventOuterClass.CollectionTypeEvent();
    }

    public static CollectionTypeEventOuterClass.CollectionTypeEvent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<CollectionTypeEvent>
        PARSER = new com.google.protobuf.AbstractParser<CollectionTypeEvent>() {
      @java.lang.Override
      public CollectionTypeEvent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<CollectionTypeEvent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<CollectionTypeEvent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public CollectionTypeEventOuterClass.CollectionTypeEvent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_CollectionTypeEvent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_CollectionTypeEvent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_CollectionTypeEvent_MapStringIntEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_CollectionTypeEvent_MapStringIntEntry_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\031CollectionTypeEvent.proto\"\235\001\n\023Collecti" +
      "onTypeEvent\022\023\n\013integerList\030\001 \003(\005\022<\n\014mapS" +
      "tringInt\030\002 \003(\0132&.CollectionTypeEvent.Map" +
      "StringIntEntry\0323\n\021MapStringIntEntry\022\013\n\003k" +
      "ey\030\001 \001(\t\022\r\n\005value\030\002 \001(\005:\0028\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_CollectionTypeEvent_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_CollectionTypeEvent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_CollectionTypeEvent_descriptor,
        new java.lang.String[] { "IntegerList", "MapStringInt", });
    internal_static_CollectionTypeEvent_MapStringIntEntry_descriptor =
      internal_static_CollectionTypeEvent_descriptor.getNestedTypes().get(0);
    internal_static_CollectionTypeEvent_MapStringIntEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_CollectionTypeEvent_MapStringIntEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}