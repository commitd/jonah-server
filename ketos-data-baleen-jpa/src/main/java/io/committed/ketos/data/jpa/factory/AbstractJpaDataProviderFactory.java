package io.committed.ketos.data.jpa.factory;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.util.StringUtils;

import io.committed.invest.server.data.providers.AbstractDataProviderFactory;
import io.committed.invest.server.data.providers.DataProvider;
import io.committed.invest.server.data.providers.DatabaseConstants;
import io.committed.ketos.data.jpa.dao.JpaDocument;

public abstract class AbstractJpaDataProviderFactory<P extends DataProvider>
    extends AbstractDataProviderFactory<P> {

  private final EntityManagerFactoryBuilder emfBuilder;

  protected AbstractJpaDataProviderFactory(final EntityManagerFactoryBuilder emfBuilder,
      final String id, final Class<P> clazz) {
    super(id, clazz, DatabaseConstants.SQL);
    this.emfBuilder = emfBuilder;
  }

  protected JpaRepositoryFactory buildRepositoryFactory(
      final Map<String, Object> settings) {

    final String driverClassName = (String) settings.get("driverClassName");
    final String url = (String) settings.get("url");
    final String username = (String) settings.get("username");
    final String password = (String) settings.get("password");

    final DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create()
        .driverClassName(driverClassName)
        .url(url);

    if (!StringUtils.isEmpty(username)) {
      dataSourceBuilder.username(username);
    }
    if (!StringUtils.isEmpty(password)) {
      dataSourceBuilder.password(password);
    }

    final DataSource dataSource = dataSourceBuilder.build();


    final LocalContainerEntityManagerFactoryBean emf = emfBuilder.dataSource(dataSource)
        .packages(JpaDocument.class).persistenceUnit(getId()).build();

    final EntityManager entityManager = emf.getObject().createEntityManager();
    final JpaRepositoryFactory factory = new JpaRepositoryFactory(entityManager);
    factory.setBeanClassLoader(emf.getBeanClassLoader());
    return factory;
  }



}

