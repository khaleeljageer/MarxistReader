package org.cpimtn.marxist.android.feature.more;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class MoreViewModel_Factory implements Factory<MoreViewModel> {
  @Override
  public MoreViewModel get() {
    return newInstance();
  }

  public static MoreViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static MoreViewModel newInstance() {
    return new MoreViewModel();
  }

  private static final class InstanceHolder {
    static final MoreViewModel_Factory INSTANCE = new MoreViewModel_Factory();
  }
}
