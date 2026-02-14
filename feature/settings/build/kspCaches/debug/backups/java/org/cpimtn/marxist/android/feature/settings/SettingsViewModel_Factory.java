package org.cpimtn.marxist.android.feature.settings;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.cpimtn.marxist.android.domain.usecase.GetSettingsFlowUseCase;
import org.cpimtn.marxist.android.domain.usecase.SetFontSizeUseCase;
import org.cpimtn.marxist.android.domain.usecase.SetLanguageUseCase;
import org.cpimtn.marxist.android.domain.usecase.SetPushNotificationsUseCase;
import org.cpimtn.marxist.android.domain.usecase.SetThemeUseCase;

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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<GetSettingsFlowUseCase> getSettingsFlowUseCaseProvider;

  private final Provider<SetThemeUseCase> setThemeUseCaseProvider;

  private final Provider<SetFontSizeUseCase> setFontSizeUseCaseProvider;

  private final Provider<SetLanguageUseCase> setLanguageUseCaseProvider;

  private final Provider<SetPushNotificationsUseCase> setPushNotificationsUseCaseProvider;

  private SettingsViewModel_Factory(Provider<GetSettingsFlowUseCase> getSettingsFlowUseCaseProvider,
      Provider<SetThemeUseCase> setThemeUseCaseProvider,
      Provider<SetFontSizeUseCase> setFontSizeUseCaseProvider,
      Provider<SetLanguageUseCase> setLanguageUseCaseProvider,
      Provider<SetPushNotificationsUseCase> setPushNotificationsUseCaseProvider) {
    this.getSettingsFlowUseCaseProvider = getSettingsFlowUseCaseProvider;
    this.setThemeUseCaseProvider = setThemeUseCaseProvider;
    this.setFontSizeUseCaseProvider = setFontSizeUseCaseProvider;
    this.setLanguageUseCaseProvider = setLanguageUseCaseProvider;
    this.setPushNotificationsUseCaseProvider = setPushNotificationsUseCaseProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(getSettingsFlowUseCaseProvider.get(), setThemeUseCaseProvider.get(), setFontSizeUseCaseProvider.get(), setLanguageUseCaseProvider.get(), setPushNotificationsUseCaseProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<GetSettingsFlowUseCase> getSettingsFlowUseCaseProvider,
      Provider<SetThemeUseCase> setThemeUseCaseProvider,
      Provider<SetFontSizeUseCase> setFontSizeUseCaseProvider,
      Provider<SetLanguageUseCase> setLanguageUseCaseProvider,
      Provider<SetPushNotificationsUseCase> setPushNotificationsUseCaseProvider) {
    return new SettingsViewModel_Factory(getSettingsFlowUseCaseProvider, setThemeUseCaseProvider, setFontSizeUseCaseProvider, setLanguageUseCaseProvider, setPushNotificationsUseCaseProvider);
  }

  public static SettingsViewModel newInstance(GetSettingsFlowUseCase getSettingsFlowUseCase,
      SetThemeUseCase setThemeUseCase, SetFontSizeUseCase setFontSizeUseCase,
      SetLanguageUseCase setLanguageUseCase,
      SetPushNotificationsUseCase setPushNotificationsUseCase) {
    return new SettingsViewModel(getSettingsFlowUseCase, setThemeUseCase, setFontSizeUseCase, setLanguageUseCase, setPushNotificationsUseCase);
  }
}
