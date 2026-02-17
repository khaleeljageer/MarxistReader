package org.cpimtn.marxist.android.feature.saved;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import org.cpimtn.marxist.android.domain.usecase.GetCategoriesFlowUseCase;
import org.cpimtn.marxist.android.domain.usecase.GetSavedPostsFlowUseCase;
import org.cpimtn.marxist.android.domain.usecase.GetTagsFlowUseCase;
import org.cpimtn.marxist.android.domain.usecase.UnsavePostUseCase;

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
public final class SavedViewModel_Factory implements Factory<SavedViewModel> {
  private final Provider<GetSavedPostsFlowUseCase> getSavedPostsFlowUseCaseProvider;

  private final Provider<GetCategoriesFlowUseCase> getCategoriesFlowUseCaseProvider;

  private final Provider<GetTagsFlowUseCase> getTagsFlowUseCaseProvider;

  private final Provider<UnsavePostUseCase> unsavePostUseCaseProvider;

  private SavedViewModel_Factory(
      Provider<GetSavedPostsFlowUseCase> getSavedPostsFlowUseCaseProvider,
      Provider<GetCategoriesFlowUseCase> getCategoriesFlowUseCaseProvider,
      Provider<GetTagsFlowUseCase> getTagsFlowUseCaseProvider,
      Provider<UnsavePostUseCase> unsavePostUseCaseProvider) {
    this.getSavedPostsFlowUseCaseProvider = getSavedPostsFlowUseCaseProvider;
    this.getCategoriesFlowUseCaseProvider = getCategoriesFlowUseCaseProvider;
    this.getTagsFlowUseCaseProvider = getTagsFlowUseCaseProvider;
    this.unsavePostUseCaseProvider = unsavePostUseCaseProvider;
  }

  @Override
  public SavedViewModel get() {
    return newInstance(getSavedPostsFlowUseCaseProvider.get(), getCategoriesFlowUseCaseProvider.get(), getTagsFlowUseCaseProvider.get(), unsavePostUseCaseProvider.get());
  }

  public static SavedViewModel_Factory create(
      Provider<GetSavedPostsFlowUseCase> getSavedPostsFlowUseCaseProvider,
      Provider<GetCategoriesFlowUseCase> getCategoriesFlowUseCaseProvider,
      Provider<GetTagsFlowUseCase> getTagsFlowUseCaseProvider,
      Provider<UnsavePostUseCase> unsavePostUseCaseProvider) {
    return new SavedViewModel_Factory(getSavedPostsFlowUseCaseProvider, getCategoriesFlowUseCaseProvider, getTagsFlowUseCaseProvider, unsavePostUseCaseProvider);
  }

  public static SavedViewModel newInstance(GetSavedPostsFlowUseCase getSavedPostsFlowUseCase,
      GetCategoriesFlowUseCase getCategoriesFlowUseCase, GetTagsFlowUseCase getTagsFlowUseCase,
      UnsavePostUseCase unsavePostUseCase) {
    return new SavedViewModel(getSavedPostsFlowUseCase, getCategoriesFlowUseCase, getTagsFlowUseCase, unsavePostUseCase);
  }
}
