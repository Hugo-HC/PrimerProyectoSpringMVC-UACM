package mx.edu.uacm;
//package mx.edu.uacm.springboot;

import java.util.Properties;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jca.support.LocalConnectionFactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration//con esta anotacion denotamos que es una clase de configuracion para configurar la conexion a la base de datos
@EnableTransactionManagement//Habilitacion del manejo de las transacciones
public class DatabaseConfig {
	//Elemento autoinyectado que nos permite leer la configuracion de las distintas propiedades
	
	@Autowired
	private Environment env;
	
	//Nos va a permitir que cuando se cargue la configuracion se inyecte el bean del datasource
	@Autowired
	private DataSource dataSource;
	
	//Objeto que nos permitira generar el entityManager donde lo vayamos a necesitar
	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory;

	//tenemos que crear una serie de beans
	
	//1.- Codigo donde cargamos las propiedades
	//Configuramos el bean del origen de los datos
	
	@Bean
	public DataSource dataSource() {
		
		//Crear el datasource a traves de drivermanager
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		
		//propiedades del origen de los datos
		dataSource.setDriverClassName(env.getProperty("db.driver"));
		dataSource.setUrl(env.getProperty("db.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(getProperty("db.password"));
		
		return dataSource;
	}
	
	//Nos permite definir el EntityManagerFactory de manera local
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		
		LocalContainerEntityManagerFactoryBean
		 entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		
		//asignar una serie de propiedades
		
		entityManagerFactory.setDataSource(dataSource);
		
		entityManagerFactory.setPackagesToScan(env.getProperty("entityManager.packagesToScan"));
		
		//como implemantacion de JPA utilizaremos hibernate
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
		
		//agregar las propiedades que hemos visto antes, dialecto, show sql, generacion ddl
		
		Properties additionalProperties = new Properties();
		additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
		
		additionalProperties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		
		additionalProperties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		
		entityManagerFactory.setJpaProperties(additionalProperties);
		
		return entityManagerFactory;
	}
	
	//Definir el gestor de  transacciones
	
	public JpaTransactionManager transactionManger() {
		JpaTransactionManager transactionManger = new JpaTransactionManager();
		transactionManger.setEntityManagerFactory(entityManagerFactory.getObject());
		return transactionManger;
	}
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTransaction() {		
		
		return new PersistenceExceptionTranslationPostProcessor();
	}
}
