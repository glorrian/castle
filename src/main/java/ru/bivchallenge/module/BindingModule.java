package ru.bivchallenge.module;

import dagger.Binds;
import dagger.Module;
import ru.bivchallenge.dto.BenefeciarSet;
import ru.bivchallenge.dto.Company;
import ru.bivchallenge.dto.LegalEntity;
import ru.bivchallenge.dto.NaturalEntity;
import ru.bivchallenge.persistence.*;

import javax.inject.Singleton;

@Module
public abstract class BindingModule {
    @Binds
    @Singleton
    abstract DataProvider<LegalEntity> bindLegalEntityLocalProvider(LegalEntityLocalProvider legalEntityLocalProvider);

    @Binds
    @Singleton
    abstract DataProvider<NaturalEntity> bindNaturalEntityLocalProvider(NaturalEntityLocalProvider naturalEntityLocalProvider);

    @Binds
    @Singleton
    abstract DataProvider<Company> bindCompanyLocalProvider(CompanyLocalProvider companyLocalProvider);

    @Binds
    @Singleton
    abstract DataDispatcher<BenefeciarSet> bindBenefeciarLocalDataDispatcher(BenefeciarLocalDataDispatcher benefeciarLocalDataDispatcher);
}
