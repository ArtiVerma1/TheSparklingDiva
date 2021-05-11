package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new com.kumaoni.blessings.DataBinderMapperImpl());
    addMapper("com.kumaoni.blessings");
  }
}
