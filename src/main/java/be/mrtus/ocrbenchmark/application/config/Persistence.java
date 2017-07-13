package be.mrtus.ocrbenchmark.application.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

@Configuration
public class Persistence {

	@Autowired
	private Environment environment;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName(this.environment.getProperty("database.datasource.driverClassName"));
		dataSource.setUrl(this.environment.getProperty("database.datasource.url"));
		dataSource.setUsername(this.environment.getProperty("database.datasource.username"));
		dataSource.setPassword(this.environment.getProperty("database.datasource.password"));

		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

		factory.setDataSource(this.dataSource());
		factory.setJpaVendorAdapter(this.jpaVendorAdapter());
		factory.setPackagesToScan("be.mrtus.ts3history");
		factory.setJpaDialect(this.jpaDialect());

		Properties properties = new Properties();

		properties.put(
				"eclipselink.weaving",
				"false"
		);

		properties.put(
				"eclipselink.logging.level",
				this.environment.getProperty("eclipselink.logging.level", "SEVERE")
		);

		properties.put(
				"eclipselink.logging.parameters",
				this.environment.getProperty("eclipselink.logging.parameters", "false")
		);

		factory.setJpaProperties(properties);

		return factory;
	}

	@Bean
	public JpaDialect jpaDialect() {
		return new EclipseLinkJpaDialect();
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {
		EclipseLinkJpaVendorAdapter adapter = new EclipseLinkJpaVendorAdapter();

		adapter.setDatabase(Database.MYSQL);
		adapter.setGenerateDdl(true);

		return adapter;
	}
}
